/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icp.gui;

import icp.algorithm.cwt.CWT;
import icp.algorithm.dwt.DWT;
import icp.algorithm.mp.UserBase;
import icp.application.*;
import icp.data.InvalidFrameIndexException;

import java.io.IOException;
import java.util.*;

import javax.swing.*;



/**
 * Prezentaèní logika hlavního okna.
 * @author Jiøí Kuèera.
 */
public class MainWindowProvider implements Observer {

    protected MainWindow mainWindow;
    private GuiController guiController;
    protected SessionManager app;
    protected Transformation transform;
    private ImageIcon warningIcon;
    protected int lastLeftSplitPosition;
    protected int lastRightSplitPosition;
    protected int lastSplitPosition;

    public MainWindowProvider(SessionManager app, GuiController guiController) {
        this.app = app;
        this.guiController = guiController;
        mainWindow = new MainWindow(guiController, this);
        lastLeftSplitPosition = mainWindow.getHeight() / 2;
        lastRightSplitPosition = mainWindow.getHeight() / 2;
        lastSplitPosition = mainWindow.getWidth() / 2;

    }

    public void update(Observable o, Object arg) {
        int msg;

        if (arg instanceof java.lang.Integer) {
            msg = ((Integer) arg).intValue();
        } else {
            return;
        }

        switch (msg) {
            case GuiController.MSG_PROJECT_CLOSED:
                mainWindow.averagingItem.setEnabled(false);
                mainWindow.waveletDialogBT.setEnabled(false);
                mainWindow.matchingDialogJB.setEnabled(false);
                mainWindow.resultItem.setEnabled(false);
                mainWindow.setEnabledDetection(false);
                break;
                
            case GuiController.MSG_CURRENT_PROJECT_CHANGED:
                mainWindow.setEnabled(true);
                mainWindow.resultItem.setEnabled(false);
                mainWindow.waveletDialogBT.setEnabled(false);
                mainWindow.matchingDialogJB.setEnabled(false);
                mainWindow.setEnabledDetection(false);
                mainWindow.createAveragingDialogs();
                
                if(!app.getSignalsSegmentation().getEpochs().isEmpty())
	                mainWindow.averagingItem.setEnabled(true);
	                
                break;

            case GuiController.MSG_SIGNAL_PLAYBACK_RESUME:
            case GuiController.MSG_SIGNAL_PLAYBACK_START:
                mainWindow.openButton.setEnabled(false);
                mainWindow.openMenuItem.setEnabled(false);
                mainWindow.averagingItem.setEnabled(false);
                mainWindow.waveletDialogBT.setEnabled(false);
                mainWindow.matchingDialogJB.setEnabled(false);
                mainWindow.setEnabledDetection(false);
                break;

            case GuiController.MSG_SIGNAL_PLAYBACK_PAUSE:
            case GuiController.MSG_SIGNAL_PLAYBACK_STOP:
                mainWindow.openButton.setEnabled(true);
                mainWindow.openMenuItem.setEnabled(true);
                
                if(!app.getSignalsSegmentation().getEpochs().isEmpty())
                {
                	mainWindow.waveletDialogBT.setEnabled(true);
                	mainWindow.matchingDialogJB.setEnabled(true);
                }
                break;
                
            case GuiController.MSG_WAVELET_TRANSFORM:
                transform = this.app.getTransform();
                mainWindow.waveletDialog.setEnabledToolsBT(true);
                break;
                
            case GuiController.MSG_WAVELET_STORNO:
                mainWindow.waveletDialog.setEnabledToolsBT(false);
                break;
                
            case GuiController.MSG_SHOW_IMPORT_DIALOG:
                mainWindow.setEnabled(false);
                break;
                
            case GuiController.MSG_MODAL_DIALOG_CLOSED:
                mainWindow.setEnabled(true);
                mainWindow.requestFocus();
                break;
                
            case GuiController.MSG_NEW_INDEXES_FOR_AVERAGING_AVAILABLE:
            	
            	if(app.getSignalsSegmentation().getEpochs().isEmpty())
            		mainWindow.averagingItem.setEnabled(false);
            	else
            		mainWindow.averagingItem.setEnabled(true);
            	
                break;
            
            case GuiController.MSG_DWT_DISABLED:
            	this.mainWindow.waveletDialog.setInvisibleProgressDialog();
            	JOptionPane.showMessageDialog(mainWindow, "Epoch is shorter than wavelet.", 
        				"Error DWT!", JOptionPane.ERROR_MESSAGE, 
        				null);
                break;
                
            case GuiController.MSG_WAVELET_ERROR:
            	JOptionPane.showMessageDialog(mainWindow, "Error by wavelet transformation.", 
        				"Error!", JOptionPane.ERROR_MESSAGE, 
        				null);
                break;
                
            case GuiController.MSG_AVERAGING:
            	mainWindow.createWaveletDialog();
            	mainWindow.createResultDialog();
            	mainWindow.setEnabledDetection(true);
            	mainWindow.resultItem.setEnabled(false);
            	mainWindow.waveletDialogBT.setEnabled(true);
            	mainWindow.matchingDialogJB.setEnabled(true);
            	break;
               
            case GuiController.MSG_DETECTION:
            	mainWindow.createResultDialog();
            	mainWindow.resultItem.setEnabled(true);
            	mainWindow.resultDialog.setActualLocationAndVisibility();
                break;
                
            case GuiController.MSG_MP_PREPROCESSING:
            	mainWindow.wtDetection();
                break;
            case GuiController.MSG_DETECTION_STOP:
            	mainWindow.resultItem.setEnabled(false);
            	break;
        }


    }
    
    void setSplitPaneHistory() {
        lastLeftSplitPosition = mainWindow.splitVerticalLeft.getDividerLocation();
        lastRightSplitPosition = mainWindow.splitVerticalRight.getDividerLocation();
        lastSplitPosition = mainWindow.split.getDividerLocation();
    }

    void importData() {
        guiController.sendMessage(GuiController.MSG_SHOW_IMPORT_DIALOG);
        
//        new DialogWindow(this).setVisible(true);
    }

    protected void about() {
        JOptionPane.showMessageDialog(mainWindow,
        		"Petr Soukal <psoukal@students.zcu.cz>\n" +
        		"Tomáš Øondík <trondik@students.zcu.cz>\n",
        		"About",
                JOptionPane.DEFAULT_OPTION);
    }

    

    
    public ImageIcon getWarningIcon() {
        if (warningIcon == null) {
        	warningIcon = guiController.loadIcon("warning.gif");
        }
        return warningIcon;
    }
    
    /**
     * Posílá data o dwt.
     * @throws InvalidFrameIndexException 
     */
    public void sendDWTData(DWT dwt) 
    {
    	app.dwt(dwt);
    }
    
    /**
     * Posílá data o cwt.
     * @throws InvalidFrameIndexException 
     */
    public void sendCWTData(CWT cwt)
    {
    	app.cwt(cwt);
    }    
    
    /**

     */
    public void detectErp(int start, int end, int indexScaleWavelet)
    {
    	app.detectErp(start, end, indexScaleWavelet);
    } 
    
    public void sendDetectionProgressUnits(double units) {
    	mainWindow.sendProgressUnits(units);
    }
    
    public void sendWaveletProgressUnits(double units) {
        mainWindow.waveletDialog.sendProgressUnits(units);
    }
    
    public void averaging(int epochCountForAveraging, int channelIndex, boolean useBaselineCorection, int shiftValue) {
        app.averaging(epochCountForAveraging, channelIndex, useBaselineCorection, shiftValue);
    }
    
    public UserBase getUserBase() throws IOException
    {
    	return app.loadUserBase();
    }
    
    public void waveletTransformDetection()
    {
    	app.waveletTransformationDetection();
    }
    
    public void matchingPursuitDetection(double[] function, int minPosition, int maxPosition, int numberOfItertions, int method)
    {
    	app.matchingPursuitDetection(function, minPosition, maxPosition, numberOfItertions, method);
    }
    
    public void mpPreprocessing()
    {
    	app.mpPreprocessing();
    }
}
