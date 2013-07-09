package icp.data.formats;

import icp.data.*;

import java.io.*;
import java.util.*;
import javax.xml.stream.*;


import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 * Modul pro naèítání BrainStudio formátu.
 * @author ZSWI
 */
public class KivFormatLoader implements DataFormatLoader {

    /**
     * Hlavièkovı soubor
     */
    private Header header;
    /**
     * Koncovka souboru signálu, ve kterém jsou hodnoty uloeny jako Ascii znaky
     */
    private final String ASCII_FORMAT_EXTENSION = "txt";
    /**
     * Promìnná den mìøení, pøes kterou se bude zapisovat do souboru.
     */
    private String dayOfAcquisition = null;
    /**
     * Promena cas mereni, pres kterou se bude zapisovat do souboru.
     */
    private String timeOfAcquisition = null;
    private static final int MICROSECOND = 1000000;

    public KivFormatLoader() {
        header = new Header();
    }

    /**
     * Nahrává data do hlavièky z popisného .xml souboru, 
     * jeho jméno obsahuje objekt loader.
     * Zjišuje, jestli jsou namìøené hodnoty uloeny v binárním nebo ascii
     * formátu a podle toho volá metody pro naètení.
     * 
     */
    public Header load(BufferCreator loader) throws IOException, CorruptedFileException {
        List<String> channelFileNames = new ArrayList<String>();

        try {
            XMLInputFactory iFactory = XMLInputFactory.newInstance();
            XMLStreamReader stReader = iFactory.createXMLStreamReader(new FileReader(loader.getInputFile()));
            channelFileNames = new ArrayList<String>();
            float globalPeriod = Float.MAX_VALUE;
            long globalCount = 0;
            final char MARK_SUBJECT = 's';
            final char EMPTY_MARK = ' ';
            char mark = EMPTY_MARK;

            while (stReader.hasNext()) {
                stReader.next();

                if (stReader.isStartElement()) {

                    if (stReader.getLocalName().equals("signal")) {
                        Channel point = new Channel();
                        channelFileNames.add(stReader.getAttributeValue(null, "filename").trim());

                        float frequency = Float.parseFloat(stReader.getAttributeValue(null, "frequency").trim());
                        point.setFrequency(frequency);

                        float period = Float.parseFloat(stReader.getAttributeValue(null, "period").trim());
                        point.setPeriod(period * MICROSECOND);

                        long count = Long.parseLong(stReader.getAttributeValue(null, "count").trim());

                        if (globalPeriod >= period) {
                            globalPeriod = period;
                        }

                        if (globalCount <= count) {
                            globalCount = count;
                        }

//					ještì tam je min a max ale nevím pøesnì co to je za hodnoty
                        point.setOriginal(stReader.getAttributeValue(null, "original").trim());
                        point.setUnit(stReader.getAttributeValue(null, "units").trim());
                        point.setName(stReader.getAttributeValue(null, "name").trim());
//					ještì count ale nevím jestli bude potøeba

                        header.addChannel(point);
                    }

                    if (stReader.getLocalName().equals("subject")) {
                        mark = MARK_SUBJECT;
                    }

                    if (stReader.getLocalName().equals("name")) {
                        if (mark == MARK_SUBJECT) {
                            String subjectName = stReader.getElementText().trim();

                            if (subjectName.length() == 0) {
                                header.setSubjectName(null);
                            } else {
                                header.setSubjectName(subjectName);
                            }
                        } else {
                            header.setDocName(stReader.getElementText().trim());
                        }

                        mark = EMPTY_MARK;
                    }

                    if (stReader.getLocalName().equals("date")) {
                        dayOfAcquisition = stReader.getElementText().trim();
                    }

                    if (stReader.getLocalName().equals("time")) {
                        timeOfAcquisition = stReader.getElementText().trim();
                    }

                    if (stReader.getLocalName().equals("length")) {
                        header.setLength(stReader.getElementText().trim());
                    }
                }
            }

            header.setSamplingInterval((int) (globalPeriod * MICROSECOND));
            header.setNumberOfSamples(globalCount);

            if (timeOfAcquisition != null && dayOfAcquisition != null) {
                header.setDateOfAcquisition(transformTimeAndDayToCalendar(dayOfAcquisition, timeOfAcquisition));
            }

            /*
             * ____________________________________________
             */
            String[] parsedNameOfChannel = channelFileNames.get(0).split("[.]");

            if ((parsedNameOfChannel.length == 2) && (parsedNameOfChannel[parsedNameOfChannel.length - 1].equals(ASCII_FORMAT_EXTENSION))) {
                loadAscii(loader, channelFileNames);
            } // kdy jsou data v souborech jednotlivıch signálù uloena jako 4 bytová reálná èísla
            else if (parsedNameOfChannel.length == 1) {
                loadBinary(loader, channelFileNames);
            } // jinak
            else {
//                System.out.println("Neznï¿½mï¿½ formï¿½t datovï¿½ch souborï¿½ signï¿½lï¿½!");
                throw new CorruptedFileException("Neznï¿½mï¿½ formï¿½t datovï¿½ch souborï¿½ signï¿½lï¿½!" + this.getClass());
            }
        } catch (XMLStreamException e) {
            throw new CorruptedFileException("XML parsing error.");
        } catch (IOException e) {
            throw e;
        } catch (CorruptedFileException e) {
            throw e;
        } catch (Exception e) {
            throw new CorruptedFileException("Probably not a KIV EEG file.");
        }

        return header;
    }

    /**
     * Ukládá data ze souborù (kde jsou data uloena jako 4 bytová reálná èísla)
     * všech signálù do doèasného souboru.
     * 
     * @param ChannelFileNames
     *            Jména souborù s hodnotami z jednotlivıch kanálù
     * @throws IOException 
     */
    private void loadBinary(BufferCreator loader, List<String> ChannelFileNames) throws IOException {
//		 pole hodnot vï¿½ech signï¿½lï¿½, pro doplï¿½ovï¿½nï¿½ hodnot
        int[] numberOfDataRecord = new int[header.getNumberOfChannels()];
        double maxFrequency = 0.0;
        String inputFile = loader.getInputFile().getAbsolutePath();

        for (int i = 0; i < header.getNumberOfChannels(); i++) {
            if (maxFrequency <= header.getChannels().get(i).getFrequency()) {
                maxFrequency = header.getChannels().get(i).getFrequency();
            }
        }

        for (int i = 0; i < numberOfDataRecord.length; i++) {
            numberOfDataRecord[i] = (int) (maxFrequency /
                    header.getChannels().get(i).getFrequency());
        }

        int slashPosition = inputFile.lastIndexOf(File.separator);
        String inputPath = null;
        if (slashPosition != -1) {
            inputPath = inputFile.substring(0, slashPosition + 1);
        }

        // pole vstupních proudù od jednotlivıch souborù
        NioInputStream[] dataIn = new NioInputStream[header.getNumberOfChannels()];
        // inicializace pole vstupních proudù
        for (int index = 0; index < dataIn.length; index++) {
            if (inputPath == null) {
                dataIn[index] = new NioInputStream(ChannelFileNames.get(index), ByteOrder.LITTLE_ENDIAN); // getName();

            } else {
                dataIn[index] = new NioInputStream(inputPath + ChannelFileNames.get(index), ByteOrder.LITTLE_ENDIAN);
            }
        }

        float[] previousValues = new float[dataIn.length];
        // prochází do nejvìtšího poètu snímkù
        for (int i = 0; i < header.getNumberOfSamples(); i++) {
            // pole hodnot všech signálù v jednom èasovém okamiku
            float[] values = new float[dataIn.length];
            for (int index = 0; index < dataIn.length; index++) {
                if (i % numberOfDataRecord[index] == 0) {
                    values[index] = dataIn[index].readFloat();
                } else {
                    values[index] = previousValues[index];
                }

            }

            previousValues = values;
//				 vkládá pole hodnot jednoho èasového okamiku do
            // spoleèného datového formátu
            try {
                loader.saveFrame(values);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        for (int i = 0; i < dataIn.length; i++) {
//                dataIn[i].close();
        }
    }

    /**
     * Ukládá data ze souborù (kde jsou data uloena jako ascii znaky) všech
     * signálù do doèasného souboru.
     * 
     * @param ChannelFileNames
     *            Jména souborù s hodnotami z jednotlivıch kanálù
     * @throws Exception 
     */
    private void loadAscii(BufferCreator loader, List<String> ChannelFileNames) throws IOException {
//		 pole hodnot všech signálù, pro doplòování hodnot
        int[] numberOfDataRecord = new int[header.getNumberOfChannels()];
        double maxFrequency = 0.0;
        String inputFile = loader.getInputFile().getAbsolutePath();

        for (int i = 0; i < header.getNumberOfChannels(); i++) {
            if (maxFrequency <= header.getChannels().get(i).getFrequency()) {
                maxFrequency = header.getChannels().get(i).getFrequency();
            }
        }

        for (int i = 0; i < numberOfDataRecord.length; i++) {
            numberOfDataRecord[i] = (int) (maxFrequency /
                    header.getChannels().get(i).getFrequency());
        }

        int slashPosition = inputFile.lastIndexOf(File.separator);
        String inputPath = null;
        if (slashPosition != -1) {
            inputPath = inputFile.substring(0, slashPosition + 1);
        }

        // pole èteèek
        BufferedReader[] bufferedReaders = new BufferedReader[header.getNumberOfChannels()];

        try {
            for (int index = 0; index < bufferedReaders.length; index++) {
                if (inputPath == null) {
                    bufferedReaders[index] = new BufferedReader(new FileReader(
                                                                ChannelFileNames.get(index)));
                } else {
                    bufferedReaders[index] = new BufferedReader(new FileReader(
                                                                inputPath + ChannelFileNames.get(index)));
                }
            }

            String value;
            Float floatValue;
            float[] previousValues = new float[bufferedReaders.length];

//			 prochází do nejvìtšího poètu snímkù
            for (int i = 0; i < header.getNumberOfSamples(); i++) {
                // pole hodnot vï¿½ech signï¿½lï¿½ v jednom ï¿½asovï¿½m okamï¿½iku
                float[] values = new float[bufferedReaders.length];
                for (int index = 0; index < bufferedReaders.length; index++) {
                    if (i % numberOfDataRecord[index] == 0) {
                        value = bufferedReaders[index].readLine();
                        floatValue = Float.parseFloat(value);
                        values[index] = floatValue.floatValue()*10;//doèas!!!
                    } else {
                        values[index] = previousValues[index]*10;//doèas!!!
                    }

                }

                previousValues = values;
//				 vkládá pole hodnot jednoho èasového okamiku do
                // spoleèného datového formátu

                try {
                    loader.saveFrame(values);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            for (int i = 0; i < bufferedReaders.length; i++) {
                bufferedReaders[i].close();
            }
        } catch (IOException e) {
            throw new IOException("Chyba uvnitï¿½ KIVFormï¿½tu v metodï¿½ loadAscii(...), " + e + ", " + this.getClass(), e); // FIXME

        }
    }

    /**
     * Metoda ma na starosti prevedeni stringu casu a stringu dne mereni na GregorianCalendar.
     * @param String date
     * @param String time
     * @return gregorianCalendar
     */
    private GregorianCalendar transformTimeAndDayToCalendar(String date, String time) {

        String[] datesArray = date.split("[:. ]");
        String[] timesArray = time.split("[:.]");

        GregorianCalendar gregorianCalendar =
                new GregorianCalendar(Integer.valueOf(datesArray[2]), Integer.valueOf(datesArray[1]) - 1,
                                      Integer.valueOf(datesArray[0]), Integer.valueOf(timesArray[0]), Integer.valueOf(timesArray[1]),
                                      Integer.valueOf(timesArray[2]));

        return gregorianCalendar;
    }


    public ArrayList<Epoch> getEpochs() {
        return new ArrayList<Epoch>();
    }
}
