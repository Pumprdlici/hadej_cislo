package icp.aplication;

import icp.algorithm.cwt.CWT;
import icp.algorithm.dwt.DWT;
import icp.algorithm.mp.*;
import icp.data.*;
import icp.data.formats.CorruptedFileException;
import icp.gui.GuiController;

import java.io.*;
import java.util.*;



/**
 * Tøída starající se o vytáøení, zavírání a ukládání projektù a pøístupu k nim.
 * @author Jiøí Kuèera
 */
public class SessionManager {
	
	public static final int NO_DETECTION = -1;
	
	public static final int WAVELET_DETECTION = 0;
	
	public static final int MP_DETECTION = 1;
	
	public static final int WAVELET_TRANSFORM = 2;
	
	public static final int MP_PREPROCESSING = 3;
	
    private GuiController guiController;
    private Transformation transform;
    private SignalsSegmentation signalsSegmentation;
    private Buffer buffer;
    private Header header;
    private ArrayList<Epoch> epochs;
    private List<Integer> selectedChannels;
    private int leftEpochBorder;
    private int rightEpochBorder;
    private Averaging averaging;
    private MatchingPursuitDetectionAlgorithm mpda;
    private WaveletTransformDetectionAlgorithm wtda;
    private int lastUsedDetection;
    private int lastUsedProcess;
    private MatchingPreprocessing mpp;
    /**
     * Konstruktor vytvoøí instanci tøídy.
     */
    public SessionManager() {
    	buffer = null;
        header = null;
        signalsSegmentation = new SignalsSegmentation(this);
        mpda = null;
        lastUsedDetection = NO_DETECTION;
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
        
        createAveraging();
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
    
    private void createAveraging()
    {
    	averaging = new Averaging(this, signalsSegmentation);
    	averaging.setBuffer(buffer);
        averaging.setHeader(header);
    }
    
    private void createTransformation()
    {
    	transform = null;
    	System.gc();
    	transform = new Transformation(this);
    	transform.setBuffer(buffer);
        transform.setHeader(header);
    }
    
    public void averaging(int epochCountForAveraging, int channelIndex, boolean useBaselineCorection, int shiftValue)
    {
    	averaging.averagingElements(epochCountForAveraging, channelIndex, useBaselineCorection, shiftValue);
    	
    	guiController.sendMessage(GuiController.MSG_AVERAGING);    	
    }
    
    public void dwt(DWT dwt)
    {        
    	createTransformation();
    	lastUsedProcess = WAVELET_TRANSFORM;
    	
		if(transform.setDWT(dwt))
			transform.start();
		else
			guiController.sendMessage(GuiController.MSG_DWT_DISABLED);
			
    }
    
    public void cwt(CWT cwt)
    {    	
    	createTransformation();
    	lastUsedProcess = WAVELET_TRANSFORM;

		transform.setCWT(cwt);
		transform.start();
    }
    
    public void stopProcess()
    {    	    	
    	if(lastUsedProcess == WAVELET_TRANSFORM)
    	{
			transform.stopWT();
			guiController.sendMessage(GuiController.MSG_WAVELET_STORNO);
			return;
    	}
    	else if(lastUsedProcess == WAVELET_DETECTION)
    	{
    		wtda.stopWT();
    	}
    	else if (lastUsedProcess == MP_PREPROCESSING)
    	{
    		mpp.stopMP();    		
    	}
    	else if (lastUsedProcess == MP_DETECTION)
    	{
    		mpda.stopMP();
    	}
    	
    	guiController.sendMessage(GuiController.MSG_DETECTION_STOP);
    	
    	lastUsedProcess = NO_DETECTION;
    	lastUsedDetection = NO_DETECTION;
    }
    
    /**

     */
    public void detectErp(int start, int end, int indexScaleWavelet)
    {
    	transform.detectErp(start, end, indexScaleWavelet);
    } 
    
    public void sendProgressUnits(double units) {
    	if(lastUsedProcess == WAVELET_TRANSFORM)
    		guiController.sendWaveletProgressUnits(units);
    	else
    		guiController.sendDetectionProgressUnits(units);
    }
    
    public void sendWtMessage() {
        guiController.sendMessage(GuiController.MSG_WAVELET_TRANSFORM);
    }
    
    public void sendWtErrorMessage() {
        guiController.sendMessage(GuiController.MSG_WAVELET_ERROR);
    }
    
    public void sendDetectionMessage() {
        guiController.sendMessage(GuiController.MSG_DETECTION);
    }

    /**
     * @return the signalsSegmentation
     */
    public SignalsSegmentation getSignalsSegmentation() {
        return signalsSegmentation;
    }
    
    /**
     */
    public Transformation getTransform() {
        return transform;
    }
    
    /**
     */
    public Averaging getAveraging() {
        return averaging;
    }
    
    public void deleteUnusedTemporaryFiles() {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        File[] files = tmpDir.listFiles();
    }
    
    public UserBase loadUserBase() throws IOException
    {
    	UserBase ub = new UserBase();
    	
    	ub.addAtom(
    			new UserAtomDefinition(
    			RawDataLoader.loadAtom(new File("Functions//P3.txt")),
    			"P3",
    			Const.BEGIN_POSITION,
    			Const.END_POSITION,
    			1,
    			0,
    			0));
    	
    	return ub;
    }
	
    public void matchingPursuitDetection(double[] function, int minPosition, int maxPosition, int numberOfIterations, int method)
    {
    	lastUsedDetection = MP_DETECTION;
    	lastUsedProcess = MP_DETECTION;
    	mpda = new MatchingPursuitDetectionAlgorithm(this, function, minPosition, maxPosition, numberOfIterations, averaging.getElements(), method);
    	mpda.start();
    }
    
    public void waveletTransformationDetection()
    {
    	lastUsedDetection = WAVELET_DETECTION;
    	lastUsedProcess = WAVELET_DETECTION;
    	wtda = new WaveletTransformDetectionAlgorithm(this, averaging.getElements());
    	wtda.start();
    }
    
    public WaveletTransformDetectionAlgorithm getWTDetectionAlgorithm()
    {
    	return wtda;
    }

    public MatchingPursuitDetectionAlgorithm getMatchingPursuitDetectionAlgorithm()
    {
    	return mpda;
    }
    
    public int getLastUsedDetection()
    {
    	return lastUsedDetection;
    }
    
    public void mpPreprocessing()
    {
    	lastUsedProcess = MP_PREPROCESSING;
    	List<Element> elements = averaging.getElements();
    	mpp = new MatchingPreprocessing(this);
    	mpp.init(elements);
    	mpp.start();
    }
    
    public void sendMPPreprocessingMessage()
    {
    	guiController.sendMessage(GuiController.MSG_MP_PREPROCESSING);
    }
}
