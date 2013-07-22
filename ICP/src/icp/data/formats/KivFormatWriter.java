package icp.data.formats;

import icp.Const;
import icp.data.*;

import java.io.*;
import java.util.*;

import javax.xml.stream.*;


/**
 * Modul pro zápis BrainStudio formátu.
 * @author ZSWI
 */
public class KivFormatWriter implements DataFormatWriter {
    /**
     * Defaultní je binární zápis dat.
     */
    private static String asciiOrBinary = "-binary";
    /**
     * Implicitní jméno souboru uchovávajícího data jednoho signálu. Pøi
     * vytváøení tohoto souboru se za tento String pøipojuje dvojèíslí s
     * poøadovım èíslem signálu. (Napø. Signal5 je pátı signál)
     */
    private final String NAME_OF_CHANNELS_FILE = "Signal";
    /**
     * Koncovka souboru signálu, ve kterém jsou hodnoty uloeny jako Ascii znaky
     */
    private final String ASCII_FORMAT_EXTENSION = "txt";
    
    private static final int MICROSECOND = 1000000;    
    
    
    public void write(Header header, Buffer buffer, File outputFile) throws IOException {
        // podle mnoství dat v prvním èasovém okamiku zjišuje poèet signálù
        float[] minima = new float[buffer.getNumberOfSignals()]; // fakt jsem to takhle

        float[] maxima = new float[buffer.getNumberOfSignals()]; // našel (minima a
        // maxima), e je to i
        // anglicky

        int[] counts = new int[buffer.getNumberOfSignals()];

        /*
         * __________________________________________________ Vytvoøení .xml
         * souboru nesoucího popisné informace
         * __________________________________________________
         */

        // rozhoduje, jestli budou data zapsána binárnì nebo v ascii
        if (asciiOrBinary.equals(Const.KIV_BINARY)) {
            writeBinary(header, buffer, outputFile.getAbsolutePath(), minima, maxima, counts);
        } else if (asciiOrBinary.equals(Const.KIV_ASCII)) {
            writeAscii(header, buffer, outputFile.getAbsolutePath(), minima, maxima, counts);
        } else {
//            System.out.println("Nastala chyba, ke kterï¿½ nemï¿½lo nikdy dojï¿½t! (v asciiOrBinary se vyskytl " + "neoï¿½ekï¿½vanï¿½ ï¿½etï¿½zec: " + asciiOrBinary);   // FIXME
            throw new IOException("Nastala chyba, ke kterï¿½ nemï¿½lo nikdy dojï¿½t! (v asciiOrBinary se vyskytl " + "neoï¿½ekï¿½vanï¿½ ï¿½etï¿½zec: " + asciiOrBinary + this.getClass()); // FIXME
        }

        final String KODOVANI = "utf-8";
        final String VERZE = "1.0";

        try {
            XMLOutputFactory oFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter stWriter = oFactory.createXMLStreamWriter(
                    new FileOutputStream(outputFile), KODOVANI);

            stWriter.writeStartDocument(KODOVANI, VERZE);

            stWriter.writeCharacters("\r\n");
            stWriter.writeStartElement("eeg");
            stWriter.writeCharacters("\r\n  ");
            stWriter.writeStartElement("header");
            stWriter.writeCharacters("\r\n    ");
            stWriter.writeStartElement("subject");
            stWriter.writeCharacters("\r\n      ");
            stWriter.writeStartElement("name");
            stWriter.writeCharacters(header.getSubjectName());
            stWriter.writeEndElement();//name

            stWriter.writeCharacters("\r\n    ");
            stWriter.writeEndElement();//subject

            stWriter.writeCharacters("\r\n    ");
            stWriter.writeStartElement("doctor");
            stWriter.writeCharacters("\r\n      ");
            stWriter.writeStartElement("name");
            stWriter.writeCharacters(header.getDocName());
            stWriter.writeEndElement();//name

            stWriter.writeCharacters("\r\n    ");
            stWriter.writeEndElement();//doctor

            stWriter.writeCharacters("\r\n    ");
            stWriter.writeStartElement("date");
            stWriter.writeCharacters(dateInvestigation(header));
            stWriter.writeEndElement();//date

            stWriter.writeCharacters("\r\n    ");
            stWriter.writeStartElement("time");
            stWriter.writeCharacters(timeInvestigation(header));
            stWriter.writeEndElement();//time

            stWriter.writeCharacters("\r\n    ");
            stWriter.writeStartElement("length");
            stWriter.writeCharacters(header.getLength());
            stWriter.writeEndElement();//length

            stWriter.writeCharacters("\r\n  ");
            stWriter.writeEndElement();//header


            stWriter.writeCharacters("\r\n  ");
            stWriter.writeStartElement("signals");

            for (int i = 0; i < header.getNumberOfChannels(); i++) {
                stWriter.writeCharacters("\r\n    ");
                stWriter.writeEmptyElement("signal");

                if (asciiOrBinary.equals(Const.KIV_ASCII)) {
                    stWriter.writeAttribute("filename", NAME_OF_CHANNELS_FILE + (i + 1) + "" +
                                            "." + ASCII_FORMAT_EXTENSION);
                } else {
                    stWriter.writeAttribute("filename", NAME_OF_CHANNELS_FILE + (i + 1));
                }

                stWriter.writeAttribute("frequency", "" + header.getChannels().get(i).getFrequency());
                stWriter.writeAttribute("period", "" + (header.getChannels().get(i).getPeriod() / MICROSECOND));
                stWriter.writeAttribute("original", "" + header.getChannels().get(i).getOriginal());
                stWriter.writeAttribute("min", "" + minima[i]);
                stWriter.writeAttribute("max", "" + maxima[i]);
                stWriter.writeAttribute("units", "" + header.getChannels().get(i).getUnit());
                stWriter.writeAttribute("name", "" + header.getChannels().get(i).getName());
                stWriter.writeAttribute("count", "" + counts[i]);
            }

            stWriter.writeCharacters("\r\n  ");
            stWriter.writeEndElement();//signals

            stWriter.writeCharacters("\r\n");
            stWriter.writeEndElement();//eeg

            stWriter.close();
        //asciiOrBinary = "-binary"; // nastaví jako defaultní opìt binární zápis dat
        } catch (Exception e) {
            //asciiOrBinary = "-binary"; // nastaví jako defaultní opìt binární zápis dat
            throw new IOException("Chyba pï¿½i zï¿½pisu hlaviï¿½ky do souboru!, " + e + ", " + this.getClass(), e);
        }

    }
    /**
     * Vytvoøí pro kadı signál jeden soubor (Signal(1) a Signal(n)) a zapíše
     * do nìj hodnoty, které tomuto signálu pøísluší. Data jsou zapisována jako
     * znaky ascii.
     * 
     * @param outputFile
     * 				název zapisovaného souboru, pøípadnì i z celou cestou 
     * @param data
     *            Objekt spoleèné vnitøní datové reprezentace
     * @param minima
     *            Nejmenší namìøená hodnota z kadého signálu
     * @param maxima
     *            Nejvìtší namìøená hodnota z kadého signálu
     * @param count
     * 			  Poèet namìøenıch hodnot kadého signálu
     * 
     * @throws IOException 
     */
    private void writeAscii(Header head, Buffer buffer, String outputFile,
            float[] minima, float[] maxima, int[] counts) throws IOException {
//		 pole hodnot všech signálù, pro doplòování hodnot
        int[] numberOfDataRecord = new int[head.getNumberOfChannels()];
        double maxFrequency = 0.0;

        for (int i = 0; i < head.getNumberOfChannels(); i++) {
            if (maxFrequency <= head.getChannels().get(i).getFrequency()) {
                maxFrequency = head.getChannels().get(i).getFrequency();
            }
        }

        for (int i = 0; i < numberOfDataRecord.length; i++) {
            numberOfDataRecord[i] = (int) (maxFrequency /
                    head.getChannels().get(i).getFrequency());
        }

        int slashPosition = outputFile.lastIndexOf(File.separator);
        String outputPath = null;
        if (slashPosition != -1) {
            outputPath = outputFile.substring(0, slashPosition + 1);
        }

        int numberOfChannels = minima.length;
        // pole bufferedWriterù, kadı zapisuje do jednoho souboru (soubor
        // jednoho signálu)
        BufferedWriter[] bufferedWriters = new BufferedWriter[numberOfChannels];
        Arrays.fill(minima, 0);
        Arrays.fill(maxima, 0);
        Arrays.fill(counts, 0);
        try {
            for (int index = 0; index < bufferedWriters.length; index++) {
                if (outputPath == null) {// vytváøí soubory jednotlivıch signálù

                    bufferedWriters[index] = new BufferedWriter(new FileWriter(
                                                                NAME_OF_CHANNELS_FILE + (index + 1) + "." + ASCII_FORMAT_EXTENSION));
                } else {
                    bufferedWriters[index] = new BufferedWriter(new FileWriter(
                                                                outputPath + File.separator + NAME_OF_CHANNELS_FILE + (index + 1) + "." + ASCII_FORMAT_EXTENSION));
                }
            }

            int line = 0;
            float[] values;

            while ((values = buffer.getNextFrame()) != null) {
                for (int i = 0; i < values.length; i++) {
                    if (line % numberOfDataRecord[i] == 0) {
                        counts[i]++;
                        //bufferedWriters[index].write(moment[index]); ukládat do xml upravit
                        if (values[i] < minima[i]) {
                            minima[i] = values[i]; // ukládá nové minimum
                        // daného signálu

                        } else if (values[i] > maxima[i]) {
                            maxima[i] = values[i]; // ukládá nové maximum
                        // daného signálu

                        }
                        bufferedWriters[i].write(String.valueOf(values[i]));
                        bufferedWriters[i].newLine();
                    }
                }
                line++;
            }
        } catch (InvalidFrameIndexException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
//            System.out.println("Chyba pï¿½i vytvï¿½ï¿½enï¿½ nebo zï¿½pisu do souboru!");
            throw new IOException("Chyba pï¿½i vytvï¿½ï¿½enï¿½ nebo zï¿½pisu do souboru!, " + e + ", " + this.getClass(), e);
        } // k uzavøení otevøenıch souborù by mìlo dojít vdy
        finally {
            for (int index = 0; index < bufferedWriters.length; index++) {
                try {
                    if (bufferedWriters[index] != null) {
                        bufferedWriters[index].close();
                    }
                } catch (IOException e) {
//                    System.out.println("Nepodaï¿½ilo se uzavï¿½ï¿½t vï¿½echny soubory, data mohou bï¿½t neï¿½plnï¿½!");
                    throw new IOException("Nepodaï¿½ilo se uzavï¿½ï¿½t vï¿½echny soubory, data mohou bï¿½t neï¿½plnï¿½!, " + e + ", " + this.getClass(), e);
                }
            }
        }
    }

    /**
     * Vytvoøí pro kadı signál jeden soubor (Signal(1) a Signal(n)) a zapíše
     * do nìj hodnoty, které tomuto signálu pøísluší. Data jsou zapisována jako
     * 4 bytová reálná èísla.
     * 
     * @param outputFile
     * 				název zapisovaného souboru, pøípadnì i z celou cestou 
     * @param data
     *            Objekt spoleèné vnitøní datové reprezentace
     * @param minima
     *            Nejmenší namìøená hodnota z kadého signálu
     * @param maxima
     *            Nejvìtší namìøená hodnota z kadého signálu
     * @param count
     * 			  Poèet namìøenıch hodnot kadého signálu
     * @throws IOException 
     * 
     * @throws IOException 
     */
    private void writeBinary(Header head, Buffer buffer, String outputFile, float[] minima, float[] maxima, int[] counts) throws IOException {
//		 pole hodnot všech signálù, pro doplòování hodnot

        int[] numberOfDataRecord = new int[head.getNumberOfChannels()];
        double maxFrequency = 0.0;

        for (int i = 0; i < head.getNumberOfChannels(); i++) {
            if (maxFrequency <= head.getChannels().get(i).getFrequency()) {
                maxFrequency = head.getChannels().get(i).getFrequency();
            }
        }

        for (int i = 0; i < numberOfDataRecord.length; i++) {
            numberOfDataRecord[i] = (int) (maxFrequency /
                    head.getChannels().get(i).getFrequency());
        }

        int slashPosition = outputFile.lastIndexOf(File.separator);
        String outputPath = null;
        if (slashPosition != -1) {
            outputPath = outputFile.substring(0, slashPosition + 1);
        }

        int numberOfChannels = minima.length;
        // pole bufferedWriterù, kadı zapisuje do jednoho souboru (soubor
        // jednoho signálu)
        DataOutputStream[] dataOut = new DataOutputStream[numberOfChannels];
        Arrays.fill(minima, 0);
        Arrays.fill(maxima, 0);
        Arrays.fill(counts, 0);

        try {
            for (int index = 0; index < dataOut.length; index++) {
                if (outputPath == null) {// vytváøí soubory jednotlivıch signálù

                    dataOut[index] = new DataOutputStream(new FileOutputStream(
                                                          NAME_OF_CHANNELS_FILE + (index + 1)));
                } else {
                    dataOut[index] = new DataOutputStream(new FileOutputStream(
                                                          outputPath + File.separator + NAME_OF_CHANNELS_FILE + (index + 1)));
                }
            }

            int line = 0;
            float[] values;

            while ((values = buffer.getNextFrame()) != null) {
                for (int i = 0; i < values.length; i++) {
                    if (line % numberOfDataRecord[i] == 0) {
                        counts[i]++;
                        //bufferedWriters[index].write(moment[index]); ukládat do xml upravit
                        if (values[i] < minima[i]) {
                            minima[i] = values[i]; // ukládá nové minimum
                        // daného signálu

                        } else if (values[i] > maxima[i]) {
                            maxima[i] = values[i]; // ukládá nové maximum
                        // daného signálu

                        }
                        dataOut[i].writeFloat(values[i]);
                    }
                }
                line++;
            }
        } catch (Exception e) {
//            System.out.println("Chyba pï¿½i vytvï¿½ï¿½enï¿½ nebo zï¿½pisu do souboru!");
            throw new IOException("Chyba pï¿½i vytvï¿½ï¿½enï¿½ nebo zï¿½pisu do souboru!, " + e + ", " + this.getClass(), e);
        } finally {
            for (int index = 0; index < dataOut.length; index++) {
                try {
                    if (dataOut[index] != null) {
                        dataOut[index].close();
                    }
                } catch (IOException e) {
//                    System.out.println("Nepodaï¿½ilo se uzavï¿½ï¿½t vï¿½echny soubory, data mohou bï¿½t neï¿½plnï¿½!");
                    throw new IOException("Nepodaï¿½ilo se uzavï¿½ï¿½t vï¿½echny soubory, data mohou bï¿½t neï¿½plnï¿½!, " + e + ", " + this.getClass(), e);
                }
            }
        }
    }
    
        /**
     * Metoda ma na starosti prevedeni z GregorianCalendar na datum mï¿½ï¿½enï¿½.
     * @param Header header
     * @return dateInvestigation
     */
    private String dateInvestigation(Header header) {
        String dateInvestigation = "";
        GregorianCalendar calendar = header.getDateOfAcquisition();

        dateInvestigation += calendar.get(Calendar.DAY_OF_MONTH) + "." +
                (calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.YEAR);

        return dateInvestigation;
    }

    /**
     * Metoda ma na starosti prevedeni z GregorianCalendar na ï¿½as mï¿½ï¿½enï¿½.
     * @param Header header
     * @return timeInvestigation
     */
    private String timeInvestigation(Header header) {
        String timeInvestigation = "";
        GregorianCalendar calendar = header.getDateOfAcquisition();

        timeInvestigation += calendar.get(Calendar.HOUR_OF_DAY) + ":" +
                calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);

        return timeInvestigation;
    }


}
