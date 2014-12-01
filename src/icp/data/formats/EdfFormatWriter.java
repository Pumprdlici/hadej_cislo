package icp.data.formats;

import icp.Const;
import icp.data.Buffer;
import icp.data.Channel;
import icp.data.Header;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;


/**
 * Modul pro zápis EDF souboru.<br/>
 * Nepodporuje rozdílné vzorkovací frekvence.
 * @author Jièí Kuèera
 */
public class EdfFormatWriter implements DataFormatWriter {

    /**
     * Defaultni hodnota, ktera se ma podle kalwiho pouzit pri zapisu do souboru. (kori)
     */
    private int durationOfADataRecord = 10;
    /**
     * Neinicializovane pole bytu o velikosti 80. Slouzi jako buffer pri cteni a zapisu.
     */
    private byte[] buffer = new byte[80];
    /**
     * Promena den mereni, pres kterou se bude zapisovat do souboru.
     */
    private String dayOfAcquisition;
    /**
     * Promena cas mereni, pres kterou se bude zapisovat do souboru.
     */
    private String timeOfAcquisition;
    
    
    public void write(Header header, Buffer fileBuffer, File outputFile) throws IOException {
//        System.out.println("---------------- edf write ---------------");

        FileOutputStream stream;
        int maxNrOfSamplesInDR = 0;
        int[] nrOfSamplesInDRs;
        int numberOfDataRecords;
        List<Channel> channels = header.getChannels();


        //Vypocet hodnot.
        try {
            maxNrOfSamplesInDR = (int) Math.round(durationOfADataRecord * 1000000. / header.getSamplingInterval());
        } catch (ArithmeticException e) {
//            System.out.println("Do headeru nebyla ulozena hodnota samplingInterval");
            throw new IOException(e);
        }

        numberOfDataRecords = (int) Math.ceil(header.getNumberOfSamples() / (float) maxNrOfSamplesInDR);

        nrOfSamplesInDRs = new int[header.getNumberOfChannels()];

//        System.out.println("maxNrOfSamplesInDR: " + maxNrOfSamplesInDR);
//        System.out.println("numberOfDataRecords: " + numberOfDataRecords);
//        System.out.println("header.getSamplingInterval(): " + header.getSamplingInterval());
//        System.out.println("header.getNumberOfSamples(): " + header.getNumberOfSamples());

        // Otevreni souboru.
        outputFile.createNewFile();

        if (!outputFile.isFile()) {
            throw new IOException(outputFile.getAbsolutePath() + "isn't file.");
        }
        if (!outputFile.canWrite()) {
            throw new IOException("Not allowed to write to the file " + outputFile.getAbsolutePath());
        }

        if (outputFile.exists()) {
            outputFile.delete();
        }

        stream = new FileOutputStream(outputFile);

        /**
         * Ulozeni zacatku hlavicky (obecne info).
         */
        try {
            // version of this data format (0)
            fillByteArray(buffer, Const.EDF_VERSION);
            stream.write(buffer, 0, 8);

            // local patient identification
            //fillByteArray(buffer, header.getEdfLocalPatientIdentification());
            fillByteArray(buffer, header.getPersonalNumber() + " " + header.getSubjectName());
            stream.write(buffer, 0, 80);

            // local recording identification
            fillByteArray(buffer, header.getDocName());
            stream.write(buffer, 0, 80);

            calendarToDayAndTime(header.getDateOfAcquisition());

            // startdate of recording (dd.mm.yy)
            fillByteArray(buffer, dayOfAcquisition);
            stream.write(buffer, 0, 8);

            // starttime of recording (hh.mm.ss)
            fillByteArray(buffer, timeOfAcquisition);
            stream.write(buffer, 0, 8);

            // number of bytes in header record
            fillByteArray(buffer, (header.getNumberOfChannels() + 1) * 256);
            stream.write(buffer, 0, 8);

            // reserved
            fillByteArray(buffer, "");
            stream.write(buffer, 0, 44);

            // number of data records
            fillByteArray(buffer, numberOfDataRecords);
            stream.write(buffer, 0, 8);

            // duration of a data record, in seconds
            fillByteArray(buffer, durationOfADataRecord);
            stream.write(buffer, 0, 8);

            // number of signals (ns) in data record
            fillByteArray(buffer, header.getNumberOfChannels());
            stream.write(buffer, 0, 4);


            /**
             * Ulozeni informaci o kanalech.
             */
//            System.out.println("No sinals: " + header.getNumberOfChannels());

            // label
            for (int i = 0; i < header.getNumberOfChannels(); i++) {
                fillByteArray(buffer, channels.get(i).getName());
                stream.write(buffer, 0, 16);
            }

            // transducer type
            for (int i = 0; i < header.getNumberOfChannels(); i++) {
                fillByteArray(buffer, channels.get(i).getEdfTransducerType());
                stream.write(buffer, 0, 80);
            }

            // physical dimension
            for (int i = 0; i < header.getNumberOfChannels(); i++) {
                fillByteArray(buffer, channels.get(i).getUnit());
                stream.write(buffer, 0, 8);
            }

            // physical minimum
            for (int i = 0; i < header.getNumberOfChannels(); i++) {
//                fillByteArray(buffer, channels.get(i).getEdfPhysicalMinimum());// FIXME
                stream.write(buffer, 0, 8);
            }

            // physical maximum
            for (int i = 0; i < header.getNumberOfChannels(); i++) {
//                fillByteArray(buffer, channels.get(i).getEdfPhysicalMaximum());// FIXME
                stream.write(buffer, 0, 8);
            }

            // digital minimum
            for (int i = 0; i < header.getNumberOfChannels(); i++) {
//                fillByteArray(buffer, channels.get(i).getEdfDigitalMinimum());// FIXME
                stream.write(buffer, 0, 8);
            }

            // digital maximum
            for (int i = 0; i < header.getNumberOfChannels(); i++) {
//                fillByteArray(buffer, channels.get(i).getEdfDigitalMaximum());// FIXME
                stream.write(buffer, 0, 8);
            }

            // prefiltering
            for (int i = 0; i < header.getNumberOfChannels(); i++) {
                fillByteArray(buffer, channels.get(i).getEdfPrefiltering());
                stream.write(buffer, 0, 80);
            }

            // nr of samples in each data record
            for (int i = 0; i < header.getNumberOfChannels(); i++) {
                nrOfSamplesInDRs[i] = (int) channels.get(i).getFrequency() * durationOfADataRecord;
                fillByteArray(buffer, nrOfSamplesInDRs[i]);
                stream.write(buffer, 0, 8);
//                System.out.println("nrOfSamplesInDRs[" + i + "]: " + nrOfSamplesInDRs[i]);
            }

            // reserved
            for (int i = 0; i < header.getNumberOfChannels(); i++) {
                fillByteArray(buffer, "");
                stream.write(buffer, 0, 32);
            }

            /**
             * Zpracovani vsech dat data records.
             */
            int fcounter = 0;
            a:
            for (int dataRecord = 0; dataRecord < numberOfDataRecords; dataRecord++) {

                /**
                 * Ulozeni data recordu.
                 */
                for (int signal = 0; signal < header.getNumberOfChannels(); signal++) {
                    int fillAmount = maxNrOfSamplesInDR / nrOfSamplesInDRs[signal];
                    for (int frame = 0; frame < nrOfSamplesInDRs[signal]; frame += fillAmount) {
                        /*
                         * XXX Zkontrolovat nahrazenï¿½!!! (ï¿½onï¿½a)
                         * co:
                         * int tmp = dataRecord * maxNrOfSamplesInDR + frame < data.size() ? data.get(dataRecord * maxNrOfSamplesInDR + frame)[signal] : 0;
                         * ï¿½ï¿½m:                                                //data.size() nenï¿½ k dispozici   // je k dispozici fileBuffer.getFrame();
                         * int tmp = (int) (dataRecord * maxNrOfSamplesInDR + frame < header.getNumberOfSamples() ? fileBuffer.getFrame(dataRecord * maxNrOfSamplesInDR + frame)[signal] : 0);
                         */															//header.numberOfSamples zï¿½skï¿½vï¿½me ve vï¿½ech formï¿½tech
                        int tmp = 0;
                        try {
                            tmp = (int) (dataRecord * maxNrOfSamplesInDR + frame < header.getNumberOfSamples() ? fileBuffer.getFrame(dataRecord * maxNrOfSamplesInDR + frame)[signal] : 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                            break a;
                        }

                        buffer[0] = (byte) (tmp & 0x00ff);
                        buffer[1] = (byte) ((tmp & 0xff00) >> 8);
                        stream.write(buffer, 0, 2);
                    }
                    fcounter++;
                }

            }
//            System.out.println("Ulozeno snimku: " + fcounter);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new IOException(e);
        }

        stream.close();
//        System.out.println("File " + outputFile.getAbsolutePath() + " was created.");

    }
    
    
    public void write(Header header, List<short[]> data, String outputFile) throws IOException {
//        System.out.println("---------------- edf write ---------------");

        FileOutputStream stream;
        File file = new File(outputFile);
        int maxNrOfSamplesInDR = 0;
        int[] nrOfSamplesInDRs;
        int numberOfDataRecords;
        List<Channel> channels = header.getChannels();

        /**
         * Vypocet hodnot.
         */
        try {
            maxNrOfSamplesInDR = (int) Math.round(durationOfADataRecord * 1000000. / header.getSamplingInterval());
        } catch (ArithmeticException e) {
//            System.out.println("Do headeru nebyla ulozena hodnota samplingInterval");
            System.exit(1);
        }

        numberOfDataRecords = (int) Math.ceil(header.getNumberOfSamples() / (float) maxNrOfSamplesInDR);

        nrOfSamplesInDRs = new int[header.getNumberOfChannels()];

//        System.out.println("maxNrOfSamplesInDR: " + maxNrOfSamplesInDR);
//        System.out.println("numberOfDataRecords: " + numberOfDataRecords);
//        System.out.println("header.getSamplingInterval(): " + header.getSamplingInterval());
//        System.out.println("header.getNumberOfSamples(): " + header.getNumberOfSamples());

        /**
         * Otevreni souboru.
         */
        file.createNewFile();

        if (!file.isFile()) {
            throw new IOException(outputFile + " isn't file.");
        }
        if (!file.canWrite()) {
            throw new IOException("Not allowed to write to the file " + outputFile);
        }

        if (file.exists()) {
            file.delete();
        }

        stream = new FileOutputStream(file);

        /**
         * Ulozeni zacatku hlavicky (obecne info).
         */
        try {
            // version of this data format (0)
            fillByteArray(buffer, Const.EDF_VERSION);
            stream.write(buffer, 0, 8);

            // local patient identification
            //fillByteArray(buffer, header.getEdfLocalPatientIdentification());
            fillByteArray(buffer, header.getPersonalNumber() + " " + header.getSubjectName());
            stream.write(buffer, 0, 80);

            // local recording identification
            fillByteArray(buffer, header.getDocName());
            stream.write(buffer, 0, 80);

            calendarToDayAndTime(header.getDateOfAcquisition());

            // startdate of recording (dd.mm.yy)
            fillByteArray(buffer, dayOfAcquisition);
            stream.write(buffer, 0, 8);

            // starttime of recording (hh.mm.ss)
            fillByteArray(buffer, timeOfAcquisition);
            stream.write(buffer, 0, 8);

            // number of bytes in header record
            fillByteArray(buffer, (header.getNumberOfChannels() + 1) * 256);
            stream.write(buffer, 0, 8);

            // reserved
            fillByteArray(buffer, "");
            stream.write(buffer, 0, 44);

            // number of data records
            fillByteArray(buffer, numberOfDataRecords);
            stream.write(buffer, 0, 8);

            // duration of a data record, in seconds
            fillByteArray(buffer, durationOfADataRecord);
            stream.write(buffer, 0, 8);

            // number of signals (ns) in data record
            fillByteArray(buffer, header.getNumberOfChannels());
            stream.write(buffer, 0, 4);


            /**
             * Ulozeni informaci o kanalech.
             */
//            System.out.println("No sinals: " + header.getNumberOfChannels());

            // label
            for (int i = 0; i < header.getNumberOfChannels(); i++) {
                fillByteArray(buffer, channels.get(i).getName());
                stream.write(buffer, 0, 16);
            }

            // transducer type
            for (int i = 0; i < header.getNumberOfChannels(); i++) {
                fillByteArray(buffer, channels.get(i).getEdfTransducerType());
                stream.write(buffer, 0, 80);
            }

            // physical dimension
            for (int i = 0; i < header.getNumberOfChannels(); i++) {
                fillByteArray(buffer, channels.get(i).getUnit());
                stream.write(buffer, 0, 8);
            }

            // physical minimum
            for (int i = 0; i < header.getNumberOfChannels(); i++) {
//                fillByteArray(buffer, channels.get(i).getEdfPhysicalMinimum());   // FIXME
                stream.write(buffer, 0, 8);
            }

            // physical maximum
            for (int i = 0; i < header.getNumberOfChannels(); i++) {
//                fillByteArray(buffer, channels.get(i).getEdfPhysicalMaximum());   // FIXME
                stream.write(buffer, 0, 8);
            }

            // digital minimum
            for (int i = 0; i < header.getNumberOfChannels(); i++) {
//                fillByteArray(buffer, channels.get(i).getEdfDigitalMinimum());    // FIXME
                stream.write(buffer, 0, 8);
            }

            // digital maximum
            for (int i = 0; i < header.getNumberOfChannels(); i++) {
//                fillByteArray(buffer, channels.get(i).getEdfDigitalMaximum());    // FIXME
                stream.write(buffer, 0, 8);
            }

            // prefiltering
            for (int i = 0; i < header.getNumberOfChannels(); i++) {
                fillByteArray(buffer, channels.get(i).getEdfPrefiltering());
                stream.write(buffer, 0, 80);
            }

            // nr of samples in each data record
            for (int i = 0; i < header.getNumberOfChannels(); i++) {
                nrOfSamplesInDRs[i] = (int) channels.get(i).getFrequency() * durationOfADataRecord;
                fillByteArray(buffer, nrOfSamplesInDRs[i]);
                stream.write(buffer, 0, 8);
//                System.out.println("nrOfSamplesInDRs[" + i + "]: " + nrOfSamplesInDRs[i]);
            }

            // reserved
            for (int i = 0; i < header.getNumberOfChannels(); i++) {
                fillByteArray(buffer, "");
                stream.write(buffer, 0, 32);
            }

            /**
             * Zpracovani vsech dat data records.
             */
            int fcounter = 0;
            for (int dataRecord = 0; dataRecord < numberOfDataRecords; dataRecord++) {

                /**
                 * Ulozeni data recordu.
                 */
                for (int signal = 0; signal < header.getNumberOfChannels(); signal++) {
                    int fillAmount = maxNrOfSamplesInDR / nrOfSamplesInDRs[signal];
                    for (int frameIndex = 0; frameIndex < nrOfSamplesInDRs[signal]; frameIndex += fillAmount) {
                        int tmp = dataRecord * maxNrOfSamplesInDR + frameIndex < data.size() ? data.get(dataRecord * maxNrOfSamplesInDR + frameIndex)[signal] : 0;
                        buffer[0] = (byte) (tmp & 0x00ff);
                        buffer[1] = (byte) ((tmp & 0xff00) >> 8);
                        stream.write(buffer, 0, 2);
                    }
                    fcounter++;
                }

            }
//            System.out.println("Ulozeno snimku: " + fcounter);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new IOException(e);
        }

        stream.close();
//        System.out.println("File " + outputFile + " was created.");
    }
    
    
    /**
     * <p>
     * Pï¿½evede floatNumber na String, zkopï¿½ruje jej do array a zbytek vyplnï¿½ mezerami
     * </p>
     * @param array array to fill
     * @param intNumber number to fill array in
     */
    private void fillByteArray(byte[] array, float floatNumber) {
        String string = String.valueOf(floatNumber);
        for (int i = 0; i < array.length; i++) {
            if (i < string.length()) {
                array[i] = (byte) string.charAt(i);
            } else {
                array[i] = 32;
            }
        }
    }

    /**
     * <p>
     * Zkopï¿½ruje string do array a zbytek vyplnï¿½ mezerami
     * </p>
     * @param array array to fill
     * @param string String to fill array in
     */
    private void fillByteArray(byte[] array, String string) {
        for (int i = 0; i < array.length; i++) {
            if (i < string.length()) {
                array[i] = (byte) string.charAt(i);
            } else {
                array[i] = 32;
            }
        }
    }

    /**
     * <p>
     * Pï¿½evede intNumber na String, zkopï¿½ruje jej do array a zbytek vyplnï¿½ mezerami
     * </p>
     * @param array array to fill
     * @param intNumber number to fill array in
     */
    private void fillByteArray(byte[] array, int intNumber) {
        String string = String.valueOf(intNumber);
        for (int i = 0; i < array.length; i++) {
            if (i < string.length()) {
                array[i] = (byte) string.charAt(i);
            } else {
                array[i] = 32;
            }
        }
    }

    /**
     * Metoda ziska z GregorianCalendar cas a datum ve stringu, ktery se bude dat zapsat do souboru.
     * @param gragorianCalendar
     */
    private void calendarToDayAndTime(GregorianCalendar gregorianCalendar) {
        dayOfAcquisition = String.valueOf(gregorianCalendar.get(Calendar.DAY_OF_MONTH)) + "." + String.valueOf(gregorianCalendar.get(Calendar.MONTH) + 1) + "." + String.valueOf(gregorianCalendar.get(Calendar.YEAR)).substring(2);
        timeOfAcquisition = String.valueOf(gregorianCalendar.get(Calendar.HOUR_OF_DAY)) + "." + String.valueOf(gregorianCalendar.get(Calendar.MINUTE)) + "." + String.valueOf(gregorianCalendar.get(Calendar.SECOND));

//        System.out.println("Time of Acquisition: " + timeOfAcquisition + ", " + this.getClass());
//        System.out.println("Day of Acquisition: " + dayOfAcquisition + ", " + this.getClass());
    }
    
}
