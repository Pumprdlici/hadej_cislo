package icp.data.formats;

import icp.data.*;

import java.io.*;
import java.util.*;

/**
 * Tøída VdefLoader obsahuje všechny metody pro parsování vstupních souborù
 * a naèítání hodnot z EEG souboru.
 * @author Tùma Lukáš (ok.lukas@seznam.cz), Jiøí Kuèera
 */
public class VdefLoader implements DataFormatLoader {

    private ArrayList<ChannelDataSet> signals;
    private ArrayList<Epoch> markers;
    private HashMap<String, String> headerKeyNames;
    private HashMap<String, String> epochKeyNames;
    private File headerFile;
    private String markerFile;
    private String eegFile;
    private byte[] data;
    private int channelLines;
    private Header header;
    private BufferCreator loader;
    private long numberOfSamples;
    private ArrayList<String> epochDescription;

    /**
     * Konstruktor tøídy VdefLoader
     */
    public VdefLoader() {
        header = new Header();
        this.signals = new ArrayList<ChannelDataSet>();
        this.markers = new ArrayList<Epoch>();
        this.headerKeyNames = new HashMap<String, String>();
        this.epochKeyNames = new HashMap<String, String>();
//        this.headerFile = headerFile;
        channelLines = 0;
        numberOfSamples = 0;
        epochDescription = new ArrayList<String>();
    }

    /**
     * Nastavuje defaultní hodnoty pro klíèová slova, která nebyla v header
     * souboru zadána.
     */
    private void loadDefaultValues() {
        if (!this.headerKeyNames.containsKey("DataFormat")) {
            this.headerKeyNames.put("DataFormat", "ASCII");
        }

        if (!this.headerKeyNames.containsKey("DataOrientation")) {
            this.headerKeyNames.put("DataOrientation", "MULTIPLEXED");
        }

        if (this.headerKeyNames.get("DataFormat").equals("BINARY")) {
            if (!this.headerKeyNames.containsKey("BinaryFormat")) {
                this.headerKeyNames.put("BinaryFormat", "INT_16");
            }

            if (!this.headerKeyNames.containsKey("UseBigEndianOrder")) {
                this.headerKeyNames.put("UseBigEndianOrder", "NO");
            }
        }
    }

    /**
     * Metoda pro parsování header souboru.
     */
    private void parseHeader() throws IOException {
        try {
//            File fileHeader = new File(this.headerFile);
            FileReader fr = new FileReader(headerFile);
            BufferedReader in = new BufferedReader(fr);
            String line = "";

            in.readLine();

            while ((line = in.readLine()) != null) {
                if (line.length() == 0) {
                    continue;
                } else if (line.charAt(0) == ';') {
                    continue;
                } else if (line.trim().equals("[Comment]")) {
                    break;
                } else if (line.indexOf("[") != -1 && line.indexOf("]") != -1) {
                    continue;
                } else if (line.startsWith("Ch") && (int) line.charAt(2) >= (int) '0' && (int) line.charAt(2) <= (int) '9') {
                    this.signals.add(createChannelDataSet(line));
                    channelLines++;
                } else {
                    String[] pole = line.split("=");
                    this.headerKeyNames.put(pole[0], pole[1]);
                }
            }
            fr.close();
            if (this.headerKeyNames.containsKey("MarkerFile")) {
                this.markerFile = this.headerKeyNames.get("MarkerFile");
                if (this.markerFile.indexOf("$b") >= 0) {
                    this.markerFile = this.markerFile.replace("$b", this.headerFile.getAbsolutePath().substring(0, this.headerFile.getAbsolutePath().indexOf(".")));
                }
            }
            this.eegFile = this.headerKeyNames.get("DataFile");
            if (this.eegFile.indexOf("$b") >= 0) {
                this.eegFile = this.eegFile.replace("$b", this.headerFile.getAbsolutePath().substring(0, this.headerFile.getAbsolutePath().indexOf(".")));
            }
        } catch (IOException e) {
            throw new IOException("Error parsing header file: " + headerFile.getAbsolutePath());
        }
    }

    /**
     * Vytvoøí objekt tøídy ChannelDataSet a uloží ho do seznamu.
     * @param line Øetìzec, ze kterého se získají informace o kanálu.
     */
    private ChannelDataSet createChannelDataSet(String line) {
        if (line.indexOf("\u00C2") != -1) {
            line = line.split("\u00C2")[0] + line.split("\u00C2")[1];
        }
        ChannelDataSet ch = new ChannelDataSet();
        String[] pole = line.split(",");
        ch.name = pole[0].split("=")[1];
        ch.reference = pole[1];
        ch.resolution = Float.valueOf(pole[2]).floatValue();
        ch.unit = pole[3];
        return ch;
    }

    /**
     * Metoda pro parsování marker souboru.
     */
    private void parseMarker() throws IOException {
        try {
            File fileMarker = new File(this.markerFile);
            if (!fileMarker.exists()) {
                this.markerFile = this.headerFile.getAbsolutePath().substring(0, this.headerFile.getAbsolutePath().lastIndexOf("\\") + 1) + this.markerFile;
                fileMarker = new File(this.markerFile);
            }
            FileReader fr = new FileReader(fileMarker);
            BufferedReader in = new BufferedReader(fr);
            String line = "";

            in.readLine();

            while ((line = in.readLine()) != null) {
                if (line.length() == 0) {
                    continue;
                } else if (line.charAt(0) == ';') {
                    continue;
                } else if (line.trim().equals("[Comment]")) {
                    break;
                } else if (line.indexOf("[") != -1 && line.indexOf("]") != -1) {
                    continue;
                } else if (line.startsWith("Mk") && (int) line.charAt(2) >= (int) '0' &&
                        (int) line.charAt(2) <= (int) '9') {
                    createMarker(line);
                } else {
                    String[] pole = line.split("=");
                    this.epochKeyNames.put(pole[0], pole[1]);
                }
            }
            
            header.setEpochs(markers);
            header.setEpochTypes(epochDescription);
            fr.close();
        } catch (IOException e) {
            throw new IOException("Error parsing marker file: " + markerFile);
        }
    }

    /**
     * Vytvoøí objekt tøídy Marker a uloží ho do seznamu.
     * @param line Øetìzec, ze kterého se získají informace o markeru.
     */
    private void createMarker(String line) {
        String[] pole = line.split(",");
        Epoch m = new Epoch(signals.size());
//        m.setName(pole[0].split("=")[0]);
        m.setType(pole[0].split("=")[1]);
        m.setDescription(pole[1]);
        m.setPosition(Integer.parseInt(pole[2]));
        m.setLength(Byte.parseByte(pole[3]));
        m.setChannelNumber(Byte.parseByte(pole[4]));
        this.markers.add(m);
        
        boolean found = false;
        
        for(int i = 0;i < epochDescription.size();i++)
        {
        	if(epochDescription.get(i).equals(m.getDescription()))
        	{
        		found = true;
        		break;
        	}
        }
        
        if(!found)
        	epochDescription.add(m.getDescription());
    }

    /**
     * Metoda naète binární EEG soubor do pole bajtù.
     */
    private void loadBinaryEEG() {
        File fileEEG = new File(this.eegFile);
        if (!fileEEG.exists()) {
            this.eegFile = this.headerFile.getAbsolutePath().substring(0, this.headerFile.getAbsolutePath().lastIndexOf("\\") + 1) + this.eegFile;
            fileEEG = new File(this.eegFile);
        }
        long length = fileEEG.length();
        byte[] bytess = new byte[(int) length];
//        InputStream input;
        NioInputStream input;

        try {
            input = new NioInputStream(this.eegFile);
            if (length > Integer.MAX_VALUE) {
                this.data = new byte[0];
                return;
            }
            int offset = 0;
            int numRead = 0;
            while (offset < bytess.length && (numRead = input.read(bytess, offset, bytess.length - offset)) >= 0) {
                offset += numRead;
            }
            if (offset < bytess.length) {
                throw new IOException("Could not completely read file " + fileEEG.getName());
            }
//            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.data = bytess;
    }

    /**
     * Metoda urèuje kam se budou jednotlivé hodnoty ukládat a volá metody pro
     * pøevod bajtù na hodnoty.
     */
    private void loadBinaryValues() {
        int numberOfBytes;
        if (this.headerKeyNames.get("BinaryFormat").equals("IEEE_FLOAT_32")) {
            numberOfBytes = 4;
        } else {
            numberOfBytes = 2;
        }

        int numberOfValues = this.data.length / (Integer.parseInt(this.headerKeyNames.get(
                "NumberOfChannels")) * numberOfBytes);
        for (int i = 0; i < this.signals.size(); i++) {
            this.signals.get(i).values = new float[numberOfValues];
        }

        if (this.headerKeyNames.get("DataOrientation").equals("MULTIPLEXED")) {
            int row = 0;
            float value = 0;
            for (int i = 0; i < this.data.length; i += numberOfBytes) {
                if (numberOfBytes == 2) {
                    byte b1 = this.data[i];
                    byte b2 = this.data[i + 1];
                    if (this.headerKeyNames.get("BinaryFormat").equals("INT_16")) {
                        if (this.headerKeyNames.get("UseBigEndianOrder").equals("YES")) {
                            value = valueINT16(b1, b2);
                        } else {
                            value = valueINT16(b2, b1);
                        }
                    } else {
                        if (this.headerKeyNames.get("UseBigEndianOrder").equals("YES")) {
                            value = valueUINT16(b1, b2);
                        } else {
                            value = valueUINT16(b2, b1);
                        }
                    }
                } else {
                    byte b1 = this.data[i];
                    byte b2 = this.data[i + 1];
                    byte b3 = this.data[i + 2];
                    byte b4 = this.data[i + 3];
                    value = valueFLOAT32(b4, b3, b2, b1);
                }
                value *= signals.get(row).resolution;

                this.signals.get(row).addValue(value);
                if (row == Integer.parseInt(this.headerKeyNames.get("NumberOfChannels")) - 1) {
                    row = 0;
                } else {
                    row++;
                }
            }
        } else {
            int index = 0;
            float value = 0;
            for (int x = 0; x < this.signals.size(); x++) {
                for (int i = 0; i < numberOfValues; i++) {
                    if (numberOfBytes == 2) {
                        byte b1 = this.data[index++];
                        byte b2 = this.data[index++];
                        if (this.headerKeyNames.get("BinaryFormat").equals("INT_16")) {
                            if (this.headerKeyNames.get("UseBigEndianOrder").equals("YES")) {
                                value = valueINT16(b1, b2);
                            } else {
                                value = valueINT16(b2, b1);
                            }
                        } else {
                            if (this.headerKeyNames.get("UseBigEndianOrder").equals("YES")) {
                                value = valueUINT16(b1, b2);
                            } else {
                                value = valueUINT16(b2, b1);
                            }
                        }
                    } else {
                        byte b1 = this.data[index++];
                        byte b2 = this.data[index++];
                        byte b3 = this.data[index++];
                        byte b4 = this.data[index++];
                        value = valueFLOAT32(b4, b3, b2, b1);
//                        if (this.headerKeyNames.get("UseBigEndianOrder").equals("YES")) {
//                            value = valueFLOAT32(b1, b2, b3, b4);
//                        } else {
//                            value = valueFLOAT32(b2, b1, b4, b3);
//                        }
                    }

                    value *= signals.get(x).resolution;
                    this.signals.get(x).addValue(value);
                }
            }
        }
    }

    /**
     * Získá UINT_16 hodnotu z dvou bajtù.
     * @param b1 První bajt hodnoty.
     * @param b2 Druhý bajt hodnoty.
     * @return Vrací hodnotu typu float.
     */
    private float valueUINT16(byte b1, byte b2) {
        return (b1 < 0) ? ((b1 + 256) << 8) | b2 & 0x00ff : b1 << 8 | b2 & 0x00ff;
    }

    /**
     * Získá INT_16 hodnotu z dvou bajtù.
     * @param b1 První bajt hodnoty.
     * @param b2 Druhý bajt hodnoty.
     * @return Vrací hodnotu typu float.
     */
    private float valueINT16(byte b1, byte b2) {
        return b1 << 8 | b2 & 0x00ff;
    }

    /**
     * Získá FLOAT_32 hodnotu ze ètyø bajtù.
     * @param b1 První bajt hodnoty.
     * @param b2 Druhý bajt hodnoty.
     * @param b3 Tøetí bajt hodnoty.
     * @param b4 Ètvrtý bajt hodnoty.
     * @return Vrací hodnotu typu float.
     */
    private float valueFLOAT32(byte b1, byte b2, byte b3, byte b4) {
        return Float.intBitsToFloat(b1 << 24 | (b2 & 0xff) << 16 | (b3 & 0xff) << 8 | (b4 & 0xff));
    }

    /**
     * Vytvoøí pole float hodnot pøedstavující jednotlivý frame a odešle ho
     * metodì saveFrame. 
     */
    private void sendFrames() throws IOException {
        for (int x = 0; x < this.signals.get(0).values.length; x++) {
            float[] array = new float[this.signals.size()];
            for (int y = 0; y < array.length; y++) {
                array[y] = this.signals.get(y).values[x];
            }
            loader.saveFrame(array);
            numberOfSamples++;
        }
        header.setNumberOfSamples(numberOfSamples);
    }

    /**
     * Metoda naète ASCII EEG soubor.
     */
    private void loadAsciiEEG() {
        // NOT IMPLEMENTED YET
    }

    /**
     * Tøída reprezentuje jednotlivý kanál. Obsahuje informace o kanálu
     * a všechny jeho namìøené hodnoty.
     * @author Tùma Lukáš  (ok.lukas@seznamm.cz)
     */
    public class ChannelDataSet {

        private String name;
        private String reference;
        private float resolution;
        private String unit;
        private float[] values;
        private int size;

        /**
         * Konstruktor tøídy ChannelDataSet.
         */
        private ChannelDataSet() {
            this.name = "";
            this.reference = "";
            this.resolution = 0;
            this.unit = "";
            this.size = 0;
        }

        /**
         * Pøidá hodnotu do pole na daný index.
         * @param value Pøidávaná hodnota
         */
        private void addValue(float value) {
            this.values[size] = value;
            this.size++;
        }
    }

    @Override
    public Header load(BufferCreator loader) throws IOException, CorruptedFileException {
        this.loader = loader;
        headerFile = loader.getInputFile();

        parseHeader();
        loadDefaultValues();
        if (this.markerFile != null) {
            parseMarker();
        }
        if (this.headerKeyNames.get("DataFormat").equals("BINARY")) {
//            System.out.println("loading binary EEG");
            loadBinaryEEG();

//            System.out.println("decoding binary Values");
            loadBinaryValues();
        } else {
            loadAsciiEEG();
        }
//        System.out.println("Sending frames");
        sendFrames();

        String dataType = headerKeyNames.get("DataType");

        if (dataType == null || dataType.equals("TIMEDOMAIN")) {
            header.setSamplingInterval(Float.parseFloat(headerKeyNames.get("SamplingInterval")));
        } else if (headerKeyNames.get("DataFormat").equals("FREQUENCYDOMAIN")) {
            header.setSamplingInterval(1f / Float.parseFloat(headerKeyNames.get("SamplingInterval")));
        }

        setChannelsInfo();

        return header;
    }

    private void setChannelsInfo() throws CorruptedFileException {

        int numberOfChannels = Integer.parseInt(headerKeyNames.get("NumberOfChannels"));

        if (numberOfChannels < 0) {
            throw new CorruptedFileException("Number of channels not specified.");
        }

        if (numberOfChannels != channelLines) {
            throw new CorruptedFileException("Number of channels not equal to channel line entries");
        }

        List<Channel> channels = new ArrayList<Channel>();

        for (int i = 0; i < numberOfChannels; i++) {
            Channel channel = new Channel();

            channel.setName(signals.get(i).name);
            channel.setUnit(signals.get(i).unit);

            String dataType = headerKeyNames.get("DataType");

            if (dataType == null || dataType.equals("TIMEDOMAIN")) {
                channel.setPeriod(Float.parseFloat(headerKeyNames.get("SamplingInterval")));
            } else if (headerKeyNames.get("DataFormat").equals("FREQUENCYDOMAIN")) {
                channel.setFrequency(1f / Float.parseFloat(headerKeyNames.get("SamplingInterval")));
            }

            channels.add(channel);
        }

        header.setChannels(channels);

    }

    @Override
    public ArrayList<Epoch> getEpochs() {
        return markers;
    }
}
