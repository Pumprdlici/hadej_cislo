package icp.gui.signals;

import icp.Const;
import icp.aplication.*;
import icp.data.Header;
import icp.gui.GuiController;

import java.awt.Color;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

/**
 * Rozhraní mezi aplikaèní a prezentaèní vrstvou.
 * Slouží pro komunikaci mezi oknem se zobrazenými signály, aplikaèní vrstvou 
 * a mezi ostatními potøebnými komponentami
 * 
 * @author Petr Soukal
 */
public class SignalsWindowProvider implements Observer {
	private final int EPOCH_MIN_LENGTH = 800;
    private SessionManager appCore;
    private DrawingComponent drawingComponent;
    private SignalsSegmentation signalsSegmentation;
    protected SignalsWindow signalsWindow;
    private GuiController guiController;
    private ImageIcon playIcon;
    private ImageIcon pauseIcon;
    private ImageIcon stopIcon;
    private ImageIcon selectionEpochIcon;
    private ImageIcon unselectionEpochIcon;
    private ImageIcon playbackIcon;
    private int selectedFunction;
    private int selectionStart; // bude tato promenna (+ set/gettery) jeste potreba?
    private int selectionEnd;   // bude tato promenna (+ set/gettery) jeste potreba?
    private boolean areaSelection = false;
    private boolean changeEpochInterval = false;
    private boolean showPopupMenu = false;
    private Color colorSelection;
//    private Header header;
//    private Buffer buffer;
    private int numberOfDrawableChannels; // Poèet vybraných signálù (tedy tìch, které mohou, ale nemusí být zobrazeny)
    private int numberOfVisibleChannels;  // Poèet zobrazených signálù
    private int firstVisibleChannel;      // Index prvního vykreslovaného signálu
//    private List<Integer> visibleSignalsIndexes;
    private float paintVolume;


    /**
     * Vytváøí instance tøídy.
     * @param appCore - jádro aplikace udržující vztah mezi aplikaèní a prezentaèní vrstvou.
     * @param guiController 
     */
    public SignalsWindowProvider(SessionManager appCore, GuiController guiController) {
        this.appCore = appCore;
        this.guiController = guiController;
        drawingComponent = new DrawingComponent(this);
        signalsWindow = new SignalsWindow(this);
        signalsSegmentation = this.appCore.getSignalsSegmentation();
        setFirstVisibleChannel(0);
    }

    /**
     * Pøijímá zprávy posíláné pomocí guiControlleru.(Komunikace mezi providery)
     */
    public void update(Observable o, Object arg) {
        int msg;

        if (arg instanceof java.lang.Integer) {
            msg = ((Integer) arg).intValue();
        } else {
            return;
        }

        switch (msg) {
            case GuiController.MSG_PROJECT_CLOSED:
                setDrawingComponent();
                signalsWindow.verticalScrollBar.setEnabled(false);
                signalsWindow.horizontalScrollBar.setEnabled(false);
                signalsWindow.drawableSignalsCheckBoxes = null;
                signalsWindow.checkBoxesPanel.removeAll();
                setAllWindowControlsEnabled(false);
                showPopupMenu = false;
                signalsWindow.increaseNumberOfChannelsButton.setEnabled(false);
                signalsWindow.decreaseNumberOfChannelsButton.setEnabled(false);
                signalsWindow.playBT.setEnabled(false);
                signalsWindow.stopBT.setEnabled(false);
                break;                
            case GuiController.MSG_CURRENT_PROJECT_CHANGED:
            	setDrawingComponent();
            	setNumberOfVisibleChannels(appCore.getSelectedChannels().size());
            	setNumberOfVisibleChannels(1);
            	setSignalSegmentation();
                setAllWindowControlsEnabled(true);
                setFirstVisibleChannel(0);
                showPopupMenu = true;
                //recountChannels();
                averageSelectedEpochs();
//                signalsWindow.invertedSignalsButton.setSelected(appCore.getCurrentProject().isInvertedSignalsView());
               
                break;

            case GuiController.MSG_SIGNAL_PLAYBACK_START:
                setAllWindowControlsEnabled(false);
                showPopupMenu = false;
                signalsWindow.setPlayButtonIcon(getPauseIcon());
                selectedFunction = Const.SELECT_NOTHING;
                getDrawingComponent().startDrawing();
                break;

            case GuiController.MSG_SIGNAL_PLAYBACK_RESUME:
                signalsWindow.setPlayButtonIcon(getPauseIcon());
                showPopupMenu = false;
                setAllWindowControlsEnabled(false);
                selectedFunction = Const.SELECT_NOTHING;
                getDrawingComponent().togglePause();
                break;

            case GuiController.MSG_SIGNAL_PLAYBACK_PAUSE:
                signalsWindow.setPlayButtonIcon(getPlayIcon());
                setAllWindowControlsEnabled(true);
                showPopupMenu = true;
                selectedFunction = signalsWindow.getSelectedFunctionIndex();
                getDrawingComponent().togglePause();
                break;

            case GuiController.MSG_SIGNAL_PLAYBACK_STOP:
                getDrawingComponent().stopDrawing();
                showPopupMenu = true;
                signalsWindow.setPlayButtonIcon(getPlayIcon());
                signalsWindow.setHorizontalScrollbarValue(0);
                setAllWindowControlsEnabled(true);
                selectedFunction = signalsWindow.getSelectedFunctionIndex();
                break;
                
            case GuiController.MSG_CHANNEL_SELECTED:
                recountChannels();
                break;
                
            case GuiController.MSG_NEW_BUFFER:
            	
            	drawingComponent.setDrawedEpochs(signalsSegmentation.getEpochsDraw());
            	break;
                
            case GuiController.MSG_INVERTED_SIGNALS_VIEW_CHANGED:
//                signalsWindow.invertedSignalsButton.setSelected(appCore.getCurrentProject().isInvertedSignalsView());
                

        }

    }

    /**
     * Nastavuje jednotlivì pøední a zadní hodnotu intervalu oblasti epoch.
     */
    void saveEpochInterval() {            	
        
        if (appCore.getBuffer() == null) {
            return;
        }
            if (drawingComponent != null) {
                int startValue = ((Integer) signalsWindow.startEpoch.getValue()).intValue();
                int endValue = ((Integer) signalsWindow.endEpoch.getValue()).intValue();
                
                if((startValue+endValue) < EPOCH_MIN_LENGTH)
                {
                	JOptionPane.showMessageDialog(null, "Epoch is short (length >= "+EPOCH_MIN_LENGTH+").", 
							"Epoch length error!", JOptionPane.ERROR_MESSAGE, 
							null);
                	return;
                }

                if (!setLeftEpochBorder(startValue)) {
                    signalsWindow.startEpoch.setValue(new Integer(appCore.getLeftEpochBorder()));
                    changeEpochInterval = true;
                }
                else {
                    appCore.setLeftEpochBorder((int) drawingComponent.timeToAbsoluteFrame(startValue));
                }
                                
                
                if (!setRightEpochBorder(endValue)) {
                    signalsWindow.endEpoch.setValue(new Integer(appCore.getRightEpochBorder()));
                    changeEpochInterval = true;
                }
                else {
                	appCore.setRightEpochBorder((int) drawingComponent.timeToAbsoluteFrame(endValue));
                }
                
                if(changeEpochInterval)
                {
                	guiController.sendMessage(GuiController.MSG_NEW_INDEXES_FOR_AVERAGING_AVAILABLE);
                	changeEpochInterval = false;
                }
                
            }
    }
    
    /**
     * Zakazuje/povoluje tlaèítka v oknì tøídy SignalsWindow.
     * 
     * @param enabled - hodnota zakázání/povolení.
     */
    private void setAllWindowControlsEnabled(boolean enabled) {
    			signalsWindow.saveEpochIntervalButton.setEnabled(enabled);
                signalsWindow.selectEpochTB.setEnabled(enabled);
                signalsWindow.unselectEpochTB.setEnabled(enabled);
                signalsWindow.selectPlaybackTB.setEnabled(enabled);
//                signalsWindow.invertedSignalsButton.setEnabled(enabled);
                signalsWindow.decreaseVerticalZoomButton.setEnabled(enabled);
                signalsWindow.increaseVerticalZoomButton.setEnabled(enabled);
                signalsWindow.decreaseHorizontalZoomButton.setEnabled(enabled);
                signalsWindow.increaseHorizontalZoomButton.setEnabled(enabled);
    }
    
    /**
     * Zvìtší poèet zobrazených signálù o jedna.<br/>
     * Hodnota mimo povolený interval bude automaticky opravena na maximum nebo minimum intervalu.
     */
    protected synchronized void increaseNumberOfVisibleChannels() {
        setNumberOfVisibleChannels(numberOfVisibleChannels + 1);
    }
    
    /**
     * Zmenší poèet zobrazených signálù o jedna.
     * Hodnota mimo povolený interval bude automaticky opravena na maximum nebo minimum intervalu.
     */
    protected synchronized void decreaseNumberOfVisibleChannels() {
        setNumberOfVisibleChannels(numberOfVisibleChannels - 1);
    }
    
    protected void decreaseVerticalZoom() {
        float zoom = getDrawingComponent().getVerticalZoom();
        float step = zoom / 5f;
        
        zoom += step;
        getDrawingComponent().setVerticalZoom(zoom);
    }

    protected void increaseVerticalZoom() {
        float zoom = getDrawingComponent().getVerticalZoom();
        float step = zoom / 5f;
        zoom -= step;
        if (zoom <= 1) {
            return;
        }
        getDrawingComponent().setVerticalZoom(zoom);
    }

    protected void increaseHorizontalZoom() {
        long time = getDrawingComponent().frameToTime(getDrawingComponent().getDrawedFrames());
        
        if (time >= 2000) {
            time -= 1000;
        }
//        time = time - (time % 1000);
        
        getDrawingComponent().setHorizontalZoom((int) getDrawingComponent().timeToAbsoluteFrame(time));
    }

    protected void decreaseHorizontalZoom() {
        long time = getDrawingComponent().frameToTime(getDrawingComponent().getDrawedFrames());
        time += 1000;
        getDrawingComponent().setHorizontalZoom((int) getDrawingComponent().timeToAbsoluteFrame(time));
    }

    /**
     * Nastavuje parametry obejtku tøídy SignalSegmentation podle
     * parametrù aktuálního projektu.
     */
    private synchronized void setSignalSegmentation()
    {
    	signalsSegmentation.setSegmentArrays();
    	
    	Integer startValue = (Integer) signalsWindow.startEpoch.getValue();
        Integer endValue = (Integer) signalsWindow.endEpoch.getValue();
        //inicializace dat pøi naèítání projektu
        signalsSegmentation.setEpochs(appCore.getEpochs());        
        getDrawingComponent().setDrawedEpochs(signalsSegmentation.getEpochsDraw());
        
    }
    /**
     * Nastavuje vykreslovací komponentu, na kterou se budou vykreslovat vybrané signály.
     * TODO - tuhle prasarnu predelat - predelano, ale mozna by se dalo jeste trochu
     */
    protected synchronized void setDrawingComponent() {
        if (appCore.getBuffer() == null) {
            drawingComponent.setDataSource(null, null);
            signalsWindow.horizontalScrollBar.setEnabled(false);
        } else {
            drawingComponent.setDataSource(appCore.getBuffer(), appCore.getHeader());
            drawingComponent.setDrawableChannels(appCore.getSelectedChannels());
            resetHorizontalScrollbarMaximum();
            signalsWindow.horizontalScrollBar.setMinimum(0);
            signalsWindow.horizontalScrollBar.setEnabled(true);
            signalsWindow.verticalScrollBar.setEnabled(true);

            signalsWindow.selectEpochTB.setSelected(true);
            setSelectedFunction(signalsWindow.getSelectedFunctionIndex());
            signalsWindow.playBT.setEnabled(true);
            signalsWindow.stopBT.setEnabled(true);
        }        

    }

    protected void togglePause() {
        if (getDrawingComponent().isPaused()) {
            if (getDrawingComponent().isRunning()) {
                guiController.sendMessage(GuiController.MSG_SIGNAL_PLAYBACK_RESUME);
            } else {
                guiController.sendMessage(GuiController.MSG_SIGNAL_PLAYBACK_START);
            }
        } else {
            guiController.sendMessage(GuiController.MSG_SIGNAL_PLAYBACK_PAUSE);
        }
    }

    protected ImageIcon getIcon(String name) {
        if (name == null) {
            return null;
        }
        ImageIcon icon = guiController.loadIcon(name);
        return icon;
    }
    
    protected void stopPlayback() {
        guiController.sendMessage(GuiController.MSG_SIGNAL_PLAYBACK_STOP);
    }

    public JPanel getWindow() {
        return signalsWindow;
    }

    /**
     * @return ikonu pro pøehrávání. 
     */
    protected ImageIcon getPlayIcon() {
        if (playIcon == null) {
            playIcon = guiController.loadIcon("play24.gif");
        }
        return playIcon;
    }
    /**
     * @return ikonu pro pauzu. 
     */

    protected ImageIcon getPauseIcon() {
        if (pauseIcon == null) {
            pauseIcon = guiController.loadIcon("pause24.gif");
        }
        return pauseIcon;
    }
    
    /**
     * @return ikonu pro zastavení pøehrávání. 
     */
    protected ImageIcon getStopIcon() {
        if (stopIcon == null) {
            stopIcon = guiController.loadIcon("stop24.gif");
        }
        return stopIcon;
    }
    
    /**
     * @return ikonu oznaèování epoch. 
     */
    protected ImageIcon getSelectionEpochIcon() {
        if (selectionEpochIcon == null) {
        	selectionEpochIcon = guiController.loadIcon("selEpochIcon.gif");
        }
        return selectionEpochIcon;
    }
    

    /**
     * @return ikonu odznaèování epoch. 
     */
    protected ImageIcon getUnselectionEpochIcon() {

        if (unselectionEpochIcon == null) {
        	unselectionEpochIcon = guiController.loadIcon("unselEpochIcon.gif");
        }
        return unselectionEpochIcon;
    }
    
       
    /**
     * @return ikonu nastavení ukazatele pøehrávání. 
     */
    protected ImageIcon getPlaybackIcon() {
        if (playbackIcon == null) {
        	playbackIcon = guiController.loadIcon("playbackicon.png");
        }
        return playbackIcon;
    }

    protected void setVerticalZoom(int vZoom) {
        if (getDrawingComponent() != null) {
//            System.out.println("vzoom: " + vZoom);
            getDrawingComponent().setVerticalZoom(vZoom);
        }
    }

    protected void setHorizontalZoom(int hZoom) {
        if (getDrawingComponent() != null) {
            getDrawingComponent().setHorizontalZoom(hZoom);
            resetHorizontalScrollbarMaximum();
//            drawingComponent.refresh();
        }
    }

    protected void resetHorizontalScrollbarMaximum() {
        signalsWindow.horizontalScrollBar.setMaximum((int) appCore.getHeader().getNumberOfSamples() - drawingComponent.getDrawedFrames() + 20);
    }

    protected void setSelectedFunction(int value) {
        this.selectedFunction = value;
    }
    
    /**
	 * Nastavuje parametry popup-menu a jeho zobrazení.
	 * 
	 * @param visualComponent - komponenta, ke které se menu váže.
	 * @param xAxis - x-ová souøadnice zobrazení menu.
	 * @param yAxis - y-ová souøadnice zobrazení menu.
	 * @param frame - místo v souboru, pøepoèítané ze souøadnic kliku.
	 */
    protected void setPopupmenu(JComponent visualComponent, int xAxis, int yAxis, long frame)
    {
    	if(showPopupMenu)
    	{
	    	boolean enabledSelEpoch = signalsSegmentation.getEnabledSelEpoch(frame);
	    	boolean enabledUnselEpoch = signalsSegmentation.getEnabledUnselEpoch(frame);
	    	boolean enabledUnselArtefact = signalsSegmentation.getEnabledUnselArtefact(frame);
	    	boolean enabledUnselAllEpochs = signalsSegmentation.getEnabledUnselAllEpochs();
	    	boolean enabledUnselAllArtefacts = signalsSegmentation.getEnabledUnselAllArtefacts();
	    	boolean enabledUnselAll;
	    	
	    	if (enabledUnselAllEpochs || enabledUnselAllArtefacts) {
	    		enabledUnselAll = true;
                } else {
	    		enabledUnselAll = false;
                }
	    	
	    	signalsWindow.optionMenu.setEnabledItems(enabledSelEpoch, enabledUnselEpoch, 
	    			enabledUnselArtefact, enabledUnselAllEpochs, enabledUnselAllArtefacts, enabledUnselAll);
	    	
	    	signalsWindow.optionMenu.setVisibleMenu(visualComponent, xAxis, yAxis, frame);
    	}
    }
    
    /**
     * Posílá informace pro oznaèení epochy objektu tøídy SignalSegmentation.
     * 
     * @param frame - místo oznaèení epochy.
     */
    protected void selectEpoch(long frame)
    {
    	
    	signalsSegmentation.selectEpoch((int)frame);
    	getDrawingComponent().setDrawedEpochs(signalsSegmentation.getEpochsDraw());
    	averageSelectedEpochs();
    }
    
    /**
     * Posílá informace pro odznaèení epochy objektu tøídy SignalSegmentation.
     * 
     * @param frame - místo odznaèení epochy.
     */
    protected void unselectEpoch(long frame)

    {
    	
    	signalsSegmentation.unselectEpoch((int)frame);
    	getDrawingComponent().setDrawedEpochs(signalsSegmentation.getEpochsDraw());
    	averageSelectedEpochs();
    }
    
 

    /**
     * Posílá informace pro odznaèení všech epoch objektu tøídy SignalSegmentation.
     */
    protected void unselectAllEpochs()
    {
    	
    	signalsSegmentation.unselectionAllEpochs();
    	getDrawingComponent().setDrawedEpochs(signalsSegmentation.getEpochsDraw());
    	averageSelectedEpochs();
    }
    
    /**
     * Posílá informace pro odznaèení všeho objektu tøídy SignalSegmentation.
     */
    protected void unselectAllEpochsAndArtefacts()
    {
    	signalsSegmentation.unselectionAllArtefacts();
    	signalsSegmentation.unselectionAllEpochs();
    	getDrawingComponent().setDrawedArtefacts(signalsSegmentation.getArtefactsDraw());
    	getDrawingComponent().setDrawedEpochs(signalsSegmentation.getEpochsDraw());
    	averageSelectedEpochs();
    }

    /**
     * Provede operaci podle aktuálnì vybrané funkce pøi stisku tlaèítka myši.
     * 
     * @param position - pozice kursoru pøi stisku tlaèítka myši.
     */
    protected void setPressedPosition(long position) {
        int xAxis = (int) position;

        switch (selectedFunction) {
            case Const.SELECT_PLAYBACK:
                drawingComponent.setPlaybackIndicatorPosition(position);
                break;
                
            case Const.SELECT_EPOCH:// TODO - pridat znacku	 

                if (signalsSegmentation.selectEpoch(xAxis)) {
                    getDrawingComponent().setDrawedEpochs(signalsSegmentation.getEpochsDraw());
                    averageSelectedEpochs();
                }
                break;
                
            case Const.UNSELECT_EPOCH:
                if (signalsSegmentation.unselectEpoch(xAxis)) {
                    getDrawingComponent().setDrawedEpochs(signalsSegmentation.getEpochsDraw());
                    averageSelectedEpochs();
                }
                break;
        }
    }

    /**
     * Nastavuje levou oblast epochy.
     * 
     * @param start - poèáteèní hodnotu intervalu epochy. 
     * @return true - pokud lze tuto hodnotu uložit.<br>
     * false - pokud nelze tuto hodnotu uložit.
     */
    protected boolean setLeftEpochBorder(int start) {
    	int leftBorder = (int)drawingComponent.timeToAbsoluteFrame(start);
    	
        if (signalsSegmentation.setLeftEpochBorder(leftBorder)) {
        	getDrawingComponent().setDrawedEpochs(signalsSegmentation.getEpochsDraw());
        	guiController.sendMessage(GuiController.MSG_NEW_INDEXES_FOR_AVERAGING_AVAILABLE);
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Nastavuje pravou oblast epochy.
     * 
     * @param end - koneènou hodnotu intervalu epochy. 
     * @return true - pokud lze tuto hodnotu uložit.<br>
     * false - pokud nelze tuto hodnotu uložit.
     */
    protected boolean setRightEpochBorder(int end) {
    	int rightBorder = (int)drawingComponent.timeToAbsoluteFrame(end);
    	
        if (signalsSegmentation.setRightEpochBorder(rightBorder)) {
        	getDrawingComponent().setDrawedEpochs(signalsSegmentation.getEpochsDraw());
        	guiController.sendMessage(GuiController.MSG_NEW_INDEXES_FOR_AVERAGING_AVAILABLE);
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return ArrayList indexù epoch k prùmìrování
     */
    protected ArrayList<Integer> getIndicesEpochsForAveraging() {

        return signalsSegmentation.getIndicesEpochsForAveraging();
    }
    

    /**
     * @return (int) hodnotu aktuální funkce.
     */
    protected int getSelectedFunction() {
        return selectedFunction;
    }


    /**
     * @return (boolean) hodnotu zda se oznaèuje souvislá oblast.
     * Napø. pøi oznaèování artefaktù.
     */
    protected boolean isAreaSelection() {
        return areaSelection;
    }

    /**
     * Nastavuje poèáteèní hodnotu vykreslované oblasti.
     * @param xAxis 
     */
    protected void setStartSelection(int xAxis) {
        selectionStart = xAxis;
    }


    /**
     * @return poèáteèní hodnotu vykreslované oblasti.
     */
    protected int getStartSelection() {
        return selectionStart;
    }

    /**
     * Nastavuje koneènou hodnotu vykreslované oblasti.
     * @param xAxis 
     */
    protected void setEndSelection(int xAxis) {
        selectionEnd = xAxis - selectionStart;
    }

    /**
     * @return koneènou hodnotu vykreslované oblasti.
     */
    protected int getEndSelection() {
    	return selectionEnd;
    }

    /**
     * @return nastavenou barvu vykreslované oblasti.
     */
    protected Color getColorSelection() {
        return colorSelection;
    }
    
    /**
     * Pøepoète hodnoty vykreslitelných a zobrazených signálù a nastaví související parametry GUI.
     */
    private synchronized void recountChannels() {
        try {
            numberOfDrawableChannels = appCore.getSelectedChannels().size();
        } catch (NullPointerException e) {
            return;
        }
        
        if (numberOfVisibleChannels > numberOfDrawableChannels) {
            numberOfVisibleChannels = numberOfDrawableChannels;
        } else if (numberOfVisibleChannels < 1) {
            numberOfVisibleChannels = 1;
        }

        int maximalFirstVisibleChannel = numberOfDrawableChannels - numberOfVisibleChannels;
        
        if (firstVisibleChannel + numberOfVisibleChannels > numberOfDrawableChannels) {
            firstVisibleChannel = maximalFirstVisibleChannel;
        } else if (firstVisibleChannel < 0) {
            firstVisibleChannel = 0;
        }

        drawingComponent.setDrawableChannels(appCore.getSelectedChannels());
        drawingComponent.loadNumbersOfSignals();
        signalsWindow.verticalScrollBar.setEnabled(numberOfVisibleChannels < numberOfDrawableChannels);
        signalsWindow.verticalScrollBar.setMaximum(maximalFirstVisibleChannel);
        signalsWindow.verticalScrollBar.setValue(firstVisibleChannel);
        setVisibleSignals();

        signalsWindow.setNumberOfSelectedSignalsButtonsEnabled(numberOfVisibleChannels > 1, numberOfVisibleChannels < numberOfDrawableChannels);

    }

    /**
     * Nastavuje indexy epoch k prùmìrování a posílá o tom zprávu.
     */
    protected void averageSelectedEpochs() {

        //appCore.getCurrentProject().setAveragedEpochsIndexes(getIndicesEpochsForAveraging());
        guiController.sendMessage(GuiController.MSG_NEW_INDEXES_FOR_AVERAGING_AVAILABLE);
    }
    

    /**
     * Nastavuje vykreslované signály.
     */
    protected void setVisibleSignals() {
        
        Header header = appCore.getHeader();
        ChannelCheckBoxListener channelCheckBoxListener = new ChannelCheckBoxListener();
        
        List<Integer> selectedChannels = appCore.getSelectedChannels();
        
        signalsWindow.drawableSignalsCheckBoxes = new JCheckBox[selectedChannels.size()];

//        paintVolume = signalsWindow.checkBoxesPanel.getHeight() / (float) getNumberOfVisibleChannels(); // FIXME ()
        for (int i = 0; i < signalsWindow.drawableSignalsCheckBoxes.length; i++) {
            JCheckBox checkBox = new JCheckBox(header.getChannels().get(selectedChannels.get(i)).getName());
            JLabel label = new JLabel("", JLabel.RIGHT);
            
            checkBox.setToolTipText(header.getChannels().get(selectedChannels.get(i)).getName());
            checkBox.setForeground(Const.DC_SIGNALS_COLORS[i % Const.DC_SIGNALS_COLORS.length]);
            checkBox.setOpaque(false);
            

            checkBox.addActionListener(channelCheckBoxListener);
            
            label.setBackground(Color.WHITE);
            label.setOpaque(true);
            label.setBorder(BorderFactory.createLineBorder(Const.DC_SIGNALS_COLORS[i % Const.DC_SIGNALS_COLORS.length], SignalsWindow.LABEL_LINE));
            label.setForeground(Const.DC_SIGNALS_COLORS[i % Const.DC_SIGNALS_COLORS.length]);

            signalsWindow.drawableSignalsCheckBoxes[i] = checkBox;
        }

        signalsWindow.repaintVisibleSignals();

        signalsWindow.repaint();
        signalsWindow.validate();
    }

    
    /**
     * Nastaví poèet zobrazených signálù na zadanou hodnotu.<br/>
     * Bude-li použita hodnota vìtší, než je poèet vykreslitelných signálù,
     * poèet zobrazených signálù se nastaví na poèet vykreslitelných signálù.
     * @param count Poèet zobrazených signálù.
     */
    private synchronized void setNumberOfVisibleChannels(int count) {
        numberOfVisibleChannels = count;
        recountChannels();
    }
    
    /**
     * Nastaví index prvního zobrazeného kanálu.<br/>
     * Hodnota mimo rozsah se pøepoète na nejbližší povolenou hodnotu.
     * @param index Index prvního zobrazeného kanálu.
     */
    protected synchronized void setFirstVisibleChannel(int index) {
        firstVisibleChannel = index;
        recountChannels();
    }
    
    /**
     * Vrací poèet vybraných signálù, tzn. poèet signálù, které mohou být vykresleny.
     * @return Poèet signálù, které mohou být vykresleny.
     */
    protected synchronized int getNumberOfDrawableChannels() {
        return numberOfDrawableChannels;
    }

    /**
     * Vrací poèet vykreslovaných signálù.<br/>
     * Je vžy menší nebo roven poètu signálù, které mohou být vykresleny.
     * @return Poèet vykreslovaných signálù.
     */
    protected synchronized int getNumberOfVisibleChannels() {
        return numberOfVisibleChannels;
    }

    /**
     * Vrací index prvního vykreslovaného signálu.
     * @return Index prvního vykreslovaného signálu.
     */
    protected synchronized int getFirstVisibleChannel() {
        return firstVisibleChannel;
    }

    protected DrawingComponent getDrawingComponent() {
        return drawingComponent;
    }
    
    protected float getPaintVolume() {
        return paintVolume;
    }
    
    protected JPopupMenu getOptionMenu() {
        return signalsWindow.optionMenu;
    }
    
    
    /**
     * Obsluhuje funkci CheckBoxù jednotlivých kanálù, 
     * které se mají využívat pøi prùmìrování.
     */
    private class ChannelCheckBoxListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            
            ArrayList<Integer> averagedChannels = new ArrayList<Integer>();

            for (int i = 0; i < signalsWindow.drawableSignalsCheckBoxes.length; i++) {

                if (signalsWindow.drawableSignalsCheckBoxes[i].isSelected()) {
                    averagedChannels.add(appCore.getSelectedChannels().get(i));
                }
            }
            
            //project.setAveragedSignalsIndexes(averagedChannels);            
            averageSelectedEpochs();
        }
    }
    
//    protected void toggleInvertedView() {
//        appCore.getCurrentProject().setInvertedSignalsView(signalsWindow.invertedSignalsButton.isSelected());
//        guiController.sendMessage(GuiController.MSG_INVERTED_SIGNALS_VIEW_CHANGED);
//    }
}
