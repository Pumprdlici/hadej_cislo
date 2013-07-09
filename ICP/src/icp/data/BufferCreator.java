package icp.data;

import icp.Const;
import icp.data.formats.*;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import java.util.ArrayList;


/**
 * Tøída pro vytváøení <code>Buffer</code>u a naèítání dat z formátových datových modulù.
 * @author Jiøí Kuèera
 */
public class BufferCreator {

    private DataFormatLoader formatFilter;	/* pozn. - vytvaret ref. promenny jen na interface,
                                                            instanci potom udelat od konkretni tridy, ktera dany interface implementuje (kalwi) */
    private Buffer buffer;
    private Header header = null;
    private ArrayList<Epoch> epochs;
    private String tmpPath;
    private File tmpFile;
    private File inputFile;
    private NioOutputStream ost;

    /**
     * Konstruktor pro vytvoøení prázdného doèasného souboru.<br/>
     * Header by nebylo nutno pøedávat, ale tímto je uživatel donucen vyplnit metadata v Headeru.
     * @param header
     * @throws java.io.IOException
     * @throws CorruptedFileException 
     */
    public BufferCreator(Header header) throws IOException, CorruptedFileException {
        if (header == null) {
            throw new CorruptedFileException("Header not set or null.");
        }
        this.header = header;
        this.buffer = null;
        this.epochs = new ArrayList<Epoch>();
        
        tmpPath = generateTmpFileName();
        tmpFile = new File(tmpPath);
        ost = new NioOutputStream(tmpFile);
    }
    
    /**
     * Konstruktor pro naètení dat z datového souboru.
     * @param inputFile Vstupní datový soubor.
     * @throws java.io.IOException
     * @throws cz.zcu.kiv.jerpstudio.data.formats.CorruptedFileException
     */
    public BufferCreator(File inputFile) throws IOException, CorruptedFileException {
        buffer = null;
        header = null;
        formatFilter = null;
        epochs = null;
        tmpPath = null;
        tmpFile = null;
        
        this.inputFile = inputFile;
        tmpPath = generateTmpFileName();
        loadFromFile();
    }
    
    /**
     * Construktor s parametrem jmena souboru, jako string
     * @param inputFileName jmeno vstupniho souboru
     * @throws java.io.IOException
     * @throws cz.zcu.kiv.jerpstudio.data.formats.CorruptedFileException
     */
    public BufferCreator(String inputFileName) throws IOException, CorruptedFileException {
        this(new File(inputFileName));
    }
            

    /**
     * Vrací vytvoøenou instanci tøídy <code>Buffer</code>.
     * @return Instance tøídy <code>Buffer</code> vytvoøená <code>BufferCreator</code>em.
     * @throws NullPointerException
     * @throws IOException 
     */
    public Buffer getBuffer() throws NullPointerException, IOException {
        if (header == null) {
            throw new NullPointerException("Buffer not created (Header null).");
        }
        if (buffer == null) {
            ost.close();
            buffer = new Buffer(tmpFile, header.getNumberOfSamples(), header.getNumberOfChannels());
        }
        return buffer;
    }

    /**
     * Vrací vytvoøenou instanci tøídy <code>Header</code>.
     * @return Instance tøídy <code>Header</code> vytvoøená <code>BufferCreator</code>em.
     * @throws NullPointerException 
     */
    public Header getHeader() throws NullPointerException {
        if (header == null) {
            throw new NullPointerException("Header not created.");
        }
        return header;
    }

    /**
     * Uloï¿½ï¿½ jeden snï¿½mek do doï¿½asnï¿½ho souboru.
     * @param frame
     * @throws IOException
     */
    public void saveFrame(float[] frame) throws IOException {
        try {
            for (int i = 0; i < frame.length; i++) {
                //tmpDataOut.writeFloat(frame[i]);
                ost.writeFloat(frame[i]);
            }

        } catch (IOException e) {
            throw new IOException("Error writing frame to temporary file.");
        }
    }

    protected void saveFloat(float value) throws IOException {
        try {
            ost.writeFloat(value);
        } catch (IOException e) {
            throw new IOException("Error writing float to temporary file.");
        }
    }
    
    public File getInputFile() {
        return inputFile;
    }

    /**
     * Metoda pro naï¿½tenï¿½ datovï¿½ho souboru do doï¿½asnï¿½ho souboru
     * @throws Exception
     */
    private void loadFromFile() throws IOException, CorruptedFileException {
        if (inputFile == null) {
            throw new IOException("Null inputFile.");
        }

        // otevï¿½enï¿½ datovï¿½ho a doï¿½asnï¿½ho souboru
        tmpFile = new File(tmpPath);

        ost = new NioOutputStream(tmpFile);

        // instance Format dle typu souboru
            String path = inputFile.getAbsolutePath();
            
            if (path.endsWith(Const.EDF_FILE_EXTENSION) || path.endsWith(Const.EDF_FILE_EXTENSION2)) {
                formatFilter = new EdfFormatLoader();
            } else if (path.endsWith(Const.KIV_FILE_EXTENSION)) {
                formatFilter = new KivFormatLoader();
            } else if (path.endsWith(Const.GENERATOR_EXTENSION)) {
                formatFilter = new GeneratorLoader();
            } else if (path.endsWith(Const.VHDR_EXTENSION)) {
                formatFilter = new VdefLoader();
            } else {
                /**
                 * Neznamy format je nastaven na true v pripade, ze koncovka
                 * souboru nesouhlasi s zadnym z predeslych znamych formatu.
                 */
                throw new CorruptedFileException("Unknown format: " + path); // TODO -  udelat autodetekci formatu
            }
            
            header = formatFilter.load(this);
            epochs = formatFilter.getEpochs();
            
//        ost.close();  // vystupni stream do docasneho souboru se uzavira az pri volani getBuffer();

        
    }

    /**
     * Metoda vygeneruje název pro doèasný soubor.
     * @return Název doèasného souboru vèetnì cesty.
     */
    protected static String generateTmpFileName() {
        Date d = new Date();
        String fileName = new String(System.getProperty("java.io.tmpdir") + "eeg" + d.getTime() + ".tmp");
        return fileName;
    }

    /**
     * Vrací seznam obsahující epochy (resp. markery) naètené z datového souboru.
     * @return Seznam s epochami.
     */
    public ArrayList<Epoch> getEpochs() {
        return epochs;
    }
    
    @Override
    protected void finalize() throws Throwable {
        ost.close();
    }

}
