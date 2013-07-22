package icp.data.formats;

import icp.data.*;

import java.io.*;
import java.util.*;


/**
 * Modul pro naèítání dat z EDF souboru.
 * @author Jiøí Kuèera
 */
public class EdfFormatLoader implements DataFormatLoader {

    /**
     * Neinicializovane pole bytu o velikosti 80. Slouzi jako buffer pri cteni a zapisu.
     */
    private byte[] buffer = new byte[80];
    /**
     * Pocet prectenych bytu (pro potreby ladeni).
     */
    private int bytesRead = 0;
    /**
     * Promena den mereni, pres kterou se bude zapisovat do souboru.
     */
    private String dayOfAcquisition;
    /**
     * Promena cas mereni, pres kterou se bude zapisovat do souboru.
     */
    private String timeOfAcquisition;
    //private float frame[] = null;
    private Header header;
    private float[] physicalMinimums;
    private float[] physicalMaximums;
    private int[] digitalMinimums;
    private int[] digitalMaximums;
    private int curBytesRead = 0;
    /**
     * Input stream for binary reading.
     */
    private NioInputStream nioStream;
    private int headerSize = 0;
    /**
     * Number of signals (channals).
     */
    private int numberOfSignals = 0;
    /**
     * Number of data records.
     */
    private int numberOfDataRecords = 0;
    private int durationOfDRecord = 0;
    /**
     * Total number of samples.
     */
    private long numberOfSamples = 0;
    /**
     * Number of samples in each data record.
     */
    private int globalNrOfSamplesInDR = 0;
    private int[] nrOfSamplesInDRs;
    private Channel[] channels;
    private int loadedSamples = 0;
    /**
     * Nacteni jednotlivych data rocords.
     */
    private int correctReadings = 0;
    private int incorrectReadings = 0;

    public EdfFormatLoader() {
        header = new Header();
    }

    /* (non-Javadoc)
     * @see cz.zcu.kiv.jerpstudio.data.Format#load(cz.zcu.kiv.jerpstudio.data.Header, cz.zcu.kiv.jerpstudio.data.Loader)
     */
    public Header load(BufferCreator loader) throws IOException, CorruptedFileException {

        File file = loader.getInputFile();

        if (!file.isFile()) {
            throw new IOException(loader.getInputFile().getAbsolutePath() + " isn't file.");
        }

        if (!file.canRead()) {
            throw new IOException(loader.getInputFile().getAbsolutePath() + " can't be read.");
        }

        nioStream = new NioInputStream(file);

        try {
            loadGlobalHeader();

            loadChannelsHeader();
        } catch (CorruptedFileException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new CorruptedFileException("Probably not an EDF file");
        }

        headerChecksum();

        loadDataRecords(loader);

        dataChecksum();

        return header;

    }

    private void loadGlobalHeader() throws EOFException, CorruptedFileException {
        // version of this data format (0)
        if ((curBytesRead = nioStream.read(buffer, 0, 8)) == 8) {
            this.bytesRead += curBytesRead;
        } else {
            throw new CorruptedFileException("Can't read EDF file version (EOF).");
        }


        // local patient identification
        if ((curBytesRead = nioStream.read(buffer, 0, 80)) == 80) {
            this.bytesRead += curBytesRead;
        } else {
            throw new CorruptedFileException("Can't read local patient identification (EOF).");
        }
        header.setPersonalNumber(new String(buffer, 0, 80).trim());


        // local recording identification
        if ((curBytesRead = nioStream.read(buffer, 0, 80)) == 80) {
            this.bytesRead += curBytesRead;
        } else {
            throw new CorruptedFileException("Can't read local recording identification (EOF).");
        }
        header.setDocName(new String(buffer, 0, 80).trim());


        // startdate of recording (dd.mm.yy)
        if ((curBytesRead = nioStream.read(buffer, 0, 8)) == 8) {
            this.bytesRead += curBytesRead;
        } else {
            throw new CorruptedFileException("Can't read startdate of recording (EOF).");
        }
        dayOfAcquisition = new String(buffer, 0, 8).trim();


        // starttime of recording (hh.mm.ss)
        if ((curBytesRead = nioStream.read(buffer, 0, 8)) == 8) {
            this.bytesRead += curBytesRead;
        } else {
            throw new CorruptedFileException("Can't read starttime of recording (EOF).");
        }
        timeOfAcquisition = new String(buffer, 0, 8).trim();
        
        header.setDateOfAcquisition(transformTimeAndDayToCalendar(dayOfAcquisition, timeOfAcquisition));


        // number of bytes in header record
        if ((curBytesRead = nioStream.read(buffer, 0, 8)) == 8) {
            this.bytesRead += curBytesRead;
        } else {
            throw new CorruptedFileException("Can't read number of bytes in header record (EOF).");
        }
        headerSize = Integer.valueOf(new String(buffer, 0, 8).trim()).intValue();


        // reserved
        if ((curBytesRead = nioStream.read(buffer, 0, 44)) == 44) {
            this.bytesRead += curBytesRead;
        } else {
            throw new CorruptedFileException("Can't read reserved space (EOF).");
        }


        // number of data records
        if ((curBytesRead = nioStream.read(buffer, 0, 8)) == 8) {
            this.bytesRead += curBytesRead;
        } else {
            throw new CorruptedFileException("Can't read number of data records (EOF).");
        }
        numberOfDataRecords = Integer.valueOf(new String(buffer, 0, 8).trim()).intValue();


        // duration of a data record, in seconds
        if ((curBytesRead = nioStream.read(buffer, 0, 8)) == 8) {
            this.bytesRead += curBytesRead;
        } else {
            throw new CorruptedFileException("Can't read duration of data record (EOF).");
        }
        durationOfDRecord = (Integer.valueOf(new String(buffer, 0, 8).trim()).intValue());


        // number of signals (ns) in data record
        if ((curBytesRead = nioStream.read(buffer, 0, 4)) == 4) {
            this.bytesRead += curBytesRead;
        } else {
            throw new CorruptedFileException("Can't read number of signals in data record (EOF).");
        }
        numberOfSignals = (Integer.valueOf(new String(buffer, 0, 4).trim()).intValue());

    }

    private void loadChannelsHeader() throws CorruptedFileException {
        nrOfSamplesInDRs = new int[numberOfSignals];

        channels = new Channel[numberOfSignals];
        physicalMinimums = new float[numberOfSignals];
        physicalMaximums = new float[numberOfSignals];

        digitalMinimums = new int[numberOfSignals];
        digitalMaximums = new int[numberOfSignals];

        for (int i = 0; i < numberOfSignals; i++) {
            channels[i] = new Channel();
        }

        /**
         * Nacteni informaci o kanalech
         */
        // label
        for (int i = 0; i < numberOfSignals; i++) {
            try {
                channels[i].setName(getStringFromStream(16, nioStream));
            } catch (EOFException e) {
                throw new CorruptedFileException("Can't read label (EOF). Channel: " + i, e);
            }
        }

        // transducer type
        for (int i = 0; i < numberOfSignals; i++) {
            try {
                channels[i].setEdfTransducerType((getStringFromStream(80, nioStream)));
            } catch (EOFException e) {
                throw new CorruptedFileException("Can't read transducer type (EOF). Channel: " + i, e);
            }
        }

        // physical dimension
        for (int i = 0; i < numberOfSignals; i++) {
            try {
                channels[i].setUnit(getStringFromStream(8, nioStream));
            } catch (EOFException e) {
                throw new CorruptedFileException("Can't read physical dimension (EOF). Channel: " + i, e);
            }
        }

        // physical minimum
        for (int i = 0; i < numberOfSignals; i++) {
            try {
                physicalMinimums[i] = getFloatFromStream(8, nioStream);
            } catch (EOFException e) {
                throw new CorruptedFileException("Can't read physical minimum (EOF). Channel: " + i, e);
            } catch (NumberFormatException e) {
                throw new CorruptedFileException("Can't read physical minimum (" + e.getMessage() + " not a number). Channel: " + i, e);
            }
        }

        // physical maximum
        for (int i = 0; i < numberOfSignals; i++) {
            try {
                physicalMaximums[i] = getFloatFromStream(8, nioStream);
            } catch (EOFException e) {
                throw new CorruptedFileException("Can't read physical maximum (EOF). Channel: " + i);
            } catch (NumberFormatException e) {
                throw new CorruptedFileException("Can't read physical maximum (" + e.getMessage() + " not a number). Channel: " + i, e);
            }
        }

        // digital minimum
        for (int i = 0; i < numberOfSignals; i++) {
            try {
                digitalMinimums[i] = getIntFromStream(8, nioStream);
            } catch (EOFException e) {
                throw new CorruptedFileException("Can't read digital minimum (EOF). Channel: " + i, e);
            } catch (NumberFormatException e) {
                throw new CorruptedFileException("Can't read digital minimum (" + e.getMessage() + " not a number). Channel: " + i, e);
            }
        }

        // digital maximum
        for (int i = 0; i < numberOfSignals; i++) {
            try {
                digitalMaximums[i] = getIntFromStream(8, nioStream);
            } catch (EOFException e) {
                throw new CorruptedFileException("Can't read digital maximum (EOF). Channel: " + i, e);
            } catch (NumberFormatException e) {
                throw new CorruptedFileException("Can't read digital maximum (" + e.getMessage() + " not a number). Channel: " + i, e);
            }
        }

        // prefiltering
        for (int i = 0; i < numberOfSignals; i++) {
            try {
                channels[i].setEdfPrefiltering(getStringFromStream(80, nioStream));
            } catch (EOFException e) {
                throw new CorruptedFileException("Can't read prefiltering (EOF). Channel: " + i, e);
            }
        }

        // nr of samples in each data record
        for (int i = 0; i < numberOfSignals; i++) {
            int nrOfSamplesInDR;
            try {
                nrOfSamplesInDR = getIntFromStream(8, nioStream);
            } catch (EOFException e) {
                throw new CorruptedFileException("Can't read number of samples in each data record (EOF). Channel: " + i, e);
            } catch (NumberFormatException e) {
                throw new CorruptedFileException("Can't read number of samples in each data record (" + e.getMessage() + " not a number). Channel: " + i, e);
            }

            // TODO - opravit na NSN, popr. zkontrolovat, jestli to takhle bude chodit pro nesoudelny kombinace
            if (nrOfSamplesInDR > globalNrOfSamplesInDR) {
                globalNrOfSamplesInDR = nrOfSamplesInDR;
            }

            nrOfSamplesInDRs[i] = nrOfSamplesInDR;
            float frequency = (float) nrOfSamplesInDR / durationOfDRecord;
            channels[i].setFrequency(frequency);
        }

        numberOfSamples = globalNrOfSamplesInDR * numberOfDataRecords;

        header.setNumberOfSamples(numberOfSamples);
        try {
            header.setSamplingInterval(durationOfDRecord * 1000000 / globalNrOfSamplesInDR);
        } catch (ArithmeticException e) {
            throw new CorruptedFileException("Can't calculate sampling interval (division by zero?).");
        }

        // reserved
        for (int i = 0; i < numberOfSignals; i++) {
            try {
                getStringFromStream(32, nioStream);
            } catch (EOFException e) {
                throw new CorruptedFileException("Can't read reserved space (EOF). Channel: " + i, e);
            }
        }

        for (int i = 0; i < numberOfSignals; i++) {
            header.addChannel(channels[i]);
        }

    }

    private void loadDataRecords(BufferCreator loader) throws IOException {
        loadedSamples = 0;

        /**
         * Nacteni jednotlivych data rocords.
         */
        correctReadings = 0;
        incorrectReadings = 0;

        for (int dataRecordsCounter = 0; dataRecordsCounter < numberOfDataRecords; dataRecordsCounter++) {

            short[][] signals = new short[numberOfSignals][globalNrOfSamplesInDR];

            /**
             * Ulozeni data recordu do pole.
             */
            for (int signal = 0; signal < numberOfSignals; signal++) {
                int fillAmount = globalNrOfSamplesInDR / nrOfSamplesInDRs[signal];
                short value = 0;
                int filledFrames = 0;
                for (int frameIndex = 0; frameIndex < nrOfSamplesInDRs[signal]; frameIndex++) {
                    curBytesRead = nioStream.read(buffer, 0, 2);
                    if (curBytesRead == 2) {
                        this.bytesRead += curBytesRead;
                        correctReadings++;
                    } else {
                        incorrectReadings++;
                    }

                    short tmp = (short) (buffer[1] << 8 | (buffer[0] & 0x00ff));

                    float x1 = physicalMinimums[signal];
                    float x2 = physicalMaximums[signal];
                    int y1 = digitalMinimums[signal];
                    int y2 = digitalMaximums[signal];

                    value = (short) ((tmp - y1) * (x2 - x1) / (y2 - y1) + x1);

                    // vyplneni framu, ktere vznikly prevzorkovanim
                    for (int i = 0; i < fillAmount; i++) {
                        signals[signal][frameIndex + i] = value;
                        filledFrames++;
                    }
                }

                /* doplneni zbyvajicich framu data recordu:
                 * napr. pri globalNrOfSamplesInDR = 10 a nrOfSamplesInDRs = 3
                 * se ve vyslednem poli predchozim kroku vyplni 3x 3 framy
                 * a 10. frame by zustal nevyplneny
                 */
                for (; filledFrames < globalNrOfSamplesInDR; filledFrames++) {
                    signals[signal][filledFrames] = value;
                }

            }

            /**
             * Ulozeni jednotlivych snimku do ArrayListu.
             */
            for (int frameIndex = 0; frameIndex < globalNrOfSamplesInDR; frameIndex++) {
                float[] sample = new float[numberOfSignals];
                for (int signal = 0; signal < numberOfSignals; signal++) {
                    sample[signal] = signals[signal][frameIndex];
                }
                loader.saveFrame(sample);
                loadedSamples++;
            }
        }

    }

    private void headerChecksum() throws CorruptedFileException {
        if (this.bytesRead != headerSize) {
//            System.out.println("Header checksum: FAILED");
            throw new CorruptedFileException("Invalid header size (expected/loaded): " + headerSize + "/" + this.bytesRead);
        } else {
//            System.out.println("Header checksum: OK");
        }

    }

    private void dataChecksum() throws CorruptedFileException {
        if (loadedSamples == numberOfSamples) {
//            System.out.println("Data checksum: OK");
        } else {
//            System.out.println("Data checksum: FAILED");
//            System.out.println("loadedSamples: " + loadedSamples);
//            System.out.println("numberOfSamples: " + numberOfSamples);
            throw new CorruptedFileException("Loaded samples not equal to expected (expected/loaded): " + numberOfSamples + "/" + loadedSamples);
        }

//        System.out.println("Correct readings: " + correctReadings);
//        System.out.println("Incorrect readings: " + incorrectReadings);

        if (incorrectReadings > 0) {
            throw new CorruptedFileException("Unexpected EOF (missing bytes: " + incorrectReadings * 2 + ")");
        }

        if (nioStream.getRemaining() > 0) {
            throw new CorruptedFileException("Non-loaded remaining bytes: " + nioStream.getRemaining());
        }


        //stream.close();

//        System.out.println("remaining: " + nioStream.getRemaining());

    }

    /**
     * <p>Metoda naï¿½te zadanï¿½ poï¿½et znakï¿½ ze vstupnï¿½ho streamu a uloï¿½ï¿½ je do ï¿½etï¿½zce.</p>
     * @param length poï¿½et znakï¿½ k naï¿½tenï¿½.
     * @param stream vstupnï¿½ stream.
     * @return ï¿½etï¿½zec s uloï¿½enï¿½m vstupem.
     * @throws EOFException
     */
    private String getStringFromStream(int length, NioInputStream stream) throws EOFException {
        int tmp;
        if ((tmp = stream.read(buffer, 0, length)) == length) {
            this.bytesRead += tmp;
            return (new String(buffer, 0, length)).trim();
        } else {
            throw new EOFException("Pocet nactenych znaku se lisi od delky");
        }
    }

    /**
     * <p>Metoda naï¿½te zadanï¿½ poï¿½et znakï¿½ ze vstupnï¿½ho streamu a pï¿½evede je na celï¿½ ï¿½ï¿½slo.</p>
     * @param length poï¿½et znakï¿½ k naï¿½tenï¿½.
     * @param stream vstupnï¿½ stream.
     * @return Naï¿½tenï¿½ ï¿½ï¿½slo.
     * @throws EOFException
     * @throws java.lang.NumberFormatException
     */
    private int getIntFromStream(int length, NioInputStream stream) throws EOFException, NumberFormatException {
        int tmp;
        String numstr = "";

        if ((tmp = stream.read(buffer, 0, length)) == length) {
            this.bytesRead += tmp;
            numstr = new String(buffer, 0, length);
            try {
                return Integer.valueOf(numstr.trim()).intValue();
            } catch (NumberFormatException e) {
                throw new NumberFormatException(numstr);
            }
        } else {
            throw new EOFException("Pocet nactenych znaku se lisi od delky");
        }
    }

    /**
     * <p>Metoda naï¿½te zadanï¿½ poï¿½et znakï¿½ ze vstupnï¿½ho streamu a pï¿½evede je na ï¿½ï¿½slo s plovoucï¿½ desetinnou ï¿½ï¿½rkou.</p>
     * @param length poï¿½et znakï¿½ k naï¿½tenï¿½.
     * @param stream vstupnï¿½ stream.
     * @return Naï¿½tenï¿½ ï¿½ï¿½slo.
     * @throws EOFException
     * @throws java.lang.NumberFormatException
     */
    private float getFloatFromStream(int length, NioInputStream stream) throws EOFException, NumberFormatException {
        int tmp;
        String numstr = "";

        if ((tmp = stream.read(buffer, 0, length)) == length) {
            this.bytesRead += tmp;
            numstr = new String(buffer, 0, length);
            try {
                return Float.valueOf(numstr.trim()).floatValue();
            } catch (NumberFormatException e) {
                throw new NumberFormatException(numstr);
            }
        } else {
            throw new EOFException("Pocet nactenych znaku se lisi od delky");
        }
    }

    /**
     * Metoda ma na starosti prevedeni stringu casu a stringu dne mereni na GregorianCalendar.
     * @param String date
     * @param String time
     * @return gregorianCalendar
     */
    private GregorianCalendar transformTimeAndDayToCalendar(String date, String time) throws CorruptedFileException {

        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        String year;
        String[] datesArray = date.split("[. ]");
        String[] timesArray = time.split("[.]");
        
        if (datesArray.length != 3) {
            throw new CorruptedFileException("Incorrect day of acquisition: '" + date + "'.");
        }
        
        if (timesArray.length != 3) {
            throw new CorruptedFileException("Incorrect time of acquisition: '" + time + "'.");
        }
        
        if (Integer.valueOf(datesArray[2]) <= 70 && Integer.valueOf(datesArray[2]) >= 0) {
            year = new String("20" + datesArray[2]);
        } else {
            year = new String("19" + datesArray[2]);
        }

        gregorianCalendar.set(Integer.valueOf(year), Integer.valueOf(datesArray[1]),
                              Integer.valueOf(datesArray[0]), Integer.valueOf(timesArray[0]), Integer.valueOf(timesArray[1]),
                              Integer.valueOf(timesArray[2]));
        return gregorianCalendar;
    }


    public ArrayList<Epoch> getEpochs() {
        return new ArrayList<Epoch>();
    }
}
