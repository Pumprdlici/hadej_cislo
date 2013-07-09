package icp.aplication;

import icp.algorithm.cwt.CWT;
import icp.algorithm.dwt.DWT;
import icp.data.*;
import icp.data.formats.CorruptedFileException;
import icp.gui.*;

import java.io.*;
import java.util.*;


/**
 * Tøída starající se o vytáøení, zavírání a ukládání projektù a pøístupu k nim.
 * @author Jiøí Kuèera
 */
public class SessionManager {

    private GuiController guiController;
    private Transformation transform;
    private SignalsSegmentation signalsSegmentation;
    private Buffer buffer;
    private Header header;
    private ArrayList<Epoch> epochs;
    private List<Integer> selectedChannels;
    private int leftEpochBorder;
    private int rightEpochBorder;

    /**
     * Konstruktor vytvoøí instanci tøídy.
     */
    public SessionManager() {
    	buffer = null;
        header = null;
        signalsSegmentation = new SignalsSegmentation(this);
    }

    /**
     * Metoda spustí grafické uživatelské rozhraní.
     */
    public void startGui() {
        final SessionManager app = this;

        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                guiController = new GuiController(app);
            }
        });
    }

    /**
     * Naète datový soubor zadaný objektem <code>file</code> a vytvoøí nad ním nový projekt, který nastaví jako aktuální.
     * @param file Objekt reprezentující soubor pro naètení.
     * @throws IOException
     * @throws CorruptedFileException
     */
    public void loadFile(File file) throws IOException, CorruptedFileException {
        
    	BufferCreator loader = new BufferCreator(file);
        buffer = loader.getBuffer();
        header = loader.getHeader();
        epochs = loader.getEpochs();
        
        
        selectedChannels = new ArrayList<Integer>();
        
        for(int i = 0; i < header.getNumberOfChannels(); i++)
        	selectedChannels.add(i);

        signalsSegmentation.setSegmentArrays();        
        signalsSegmentation.setEpochs(epochs);
        
        createTransformation();
    }

    public Header getHeader() {
        return header;
    }
    
    public Buffer getBuffer() {
        return buffer;
    }
    
    public ArrayList<Epoch> getEpochs() {
        return epochs;
    }
    
    public List<Integer> getSelectedChannels() {
        return selectedChannels;
    }
    
    public void setLeftEpochBorder(int value)
    {
    	leftEpochBorder = value;
    }
    
    public void setRightEpochBorder(int value)
    {
    	rightEpochBorder = value;
    }
    
    public int getLeftEpochBorder()
    {
    	return leftEpochBorder;
    }
    
    public int getRightEpochBorder()
    {
    	return rightEpochBorder;
    }
    
    private void createTransformation()
    {
    	transform = null;
    	System.gc();
    	transform = new Transformation(this);
    	transform.setBuffer(buffer);
        transform.setHeader(header);
    }
    
    public void dwt(DWT dwt,  int[] channelsIndexes, boolean averaging)
    {        
    	createTransformation();
    	transform.setChannelsIndexes(channelsIndexes);
		transform.setAveraging(averaging);
		
		if(transform.setDWT(dwt))
			transform.start();
		else
			guiController.sendMessage(GuiController.MSG_DWT_DISABLED);
			
    }
    
    public void cwt(CWT cwt,  int[] channelsIndexes, boolean averaging)
    {    	
    	createTransformation();
    	transform.setChannelsIndexes(channelsIndexes);
		transform.setAveraging(averaging);
		transform.setCWT(cwt);
		transform.start();
    }
    
    public void stopWT()
    {    	
		transform.stopWT();
		guiController.sendMessage(GuiController.MSG_WAVELET_STORNO);
    }
    
    /**

     */
    public void detectErp(int start, int end, int indexScaleWavelet)
    {
    	transform.detectErp(start, end, indexScaleWavelet);
    } 
    
    public void sendProgressUnits(double units) {
        guiController.sendProgressUnits(units);
    }
    
    public void sendWtMessage() {
        guiController.sendMessage(GuiController.MSG_WAVELET_TRANSFORM);
    }
    
    public void sendWtErrorMessage() {
        guiController.sendMessage(GuiController.MSG_WAVELET_ERROR);
    }

    /**
     * @return the signalsSegmentation
     */
    public SignalsSegmentation getSignalsSegmentation() {
        return signalsSegmentation;
    }
    
    /**
     * @return the signalsSegmentation
     */
    public Transformation getTransform() {
        return transform;
    }

    
    public void deleteUnusedTemporaryFiles() {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        File[] files = tmpDir.listFiles();

    }

}
