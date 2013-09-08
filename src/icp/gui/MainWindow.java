package icp.gui;

import icp.Const;
import icp.algorithm.mp.*;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;


/**
 * Hlavní okno.
 * Obsahuje hlavní tlaèítka a udržuje rozmístìní hlavních komponent.
 * @author Petr Soukal
 */
public class MainWindow extends JFrame {
    private static final int SPLIT_DIVIDER_SIZE = 7;
    private static final long serialVersionUID = 1L;
    private MainWindowProvider mainWindowProvider;
    private MainWindow mainWindow = this;
    private JMenuBar menu;
    private JToolBar toolBar;
    private GuiController guiController = null;
    protected JSplitPane splitVerticalLeft;
    protected JSplitPane splitVerticalRight;
    protected JSplitPane split;
    private JMenu fileMenu;
    private JMenu toolsMenu;
    private JMenu helpMenu;
    protected JMenuItem openMenuItem;
    protected JMenuItem autoLoadClassifierMenuItem;
    protected JMenuItem loadClassifierMenuItem;
    protected JMenuItem trainClassifierMenuItem;
    protected JMenuItem averagingItem;
    protected JMenuItem resultItem;
    private JMenuItem aboutMenuItem;
    private JMenuItem onlineDetectionMenuItem;
    protected JButton openButton;
    protected JButton infoButton;
    protected JButton waveletDialogBT;
    protected JButton matchingDialogJB;
    protected JButton detectionWT_BT;
    protected JButton detectionMP_BT;
    protected JButton detectionMPandWT_BT;
    
    protected JButton detectionUniversal;
    protected WaveletTransformDialog waveletDialog;
    protected AveragingDialog averagingDialog;
    protected ResultDialog resultDialog;
    private ProgressDialog progressDialog;
    private OnlineDialog onlineDialog; 

    /**
     * Vytváøí instanci tøídy.
     * @param guiController - rozhraní pro komunikaci hlavního okna s aplikaèní vrstvou a
     * ostatními komponentami
     * @param mainWindowProvider 
     */
    public MainWindow(final GuiController guiController, final MainWindowProvider mainWindowProvider) {
        super(Const.APP_NAME);
        this.mainWindowProvider = mainWindowProvider;
        this.guiController = guiController;
        this.setIconImage(guiController.loadIcon("icon.gif").getImage());
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setJMenuBar(getMenu());
        this.setLayout(new BorderLayout());
        this.add(getInterior());
        this.setLocationByPlatform(true);
        this.setVisible(true);
        this.pack();
        this.setSize(Const.MAIN_WINDOW_WIDTH, Const.MAIN_WINDOW_HEIGHT);
        this.onlineDialog = new OnlineDialog(mainWindow, mainWindowProvider);
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                guiController.exitAplication();
            }
        });

        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
//                mainWindowProvider.setSplitPaneHistory();
                //mainWindowProvider.toggleWindows();
            }
        });
    }

    public void createAveragingDialogs() {
    	averagingDialog = new AveragingDialog(mainWindow, mainWindowProvider);
    }
    
    public void createWaveletDialog() {
    	waveletDialog = new WaveletTransformDialog(mainWindow, mainWindowProvider);
    }
    
    public void createResultDialog() {
    	resultDialog = new ResultDialog(mainWindow, mainWindowProvider);
    }

    /**
     * Vytváøí menu aplikace.
     */
    private JMenuBar getMenu() {
        menu = new JMenuBar();


        fileMenu = new JMenu("File");
        helpMenu = new JMenu("Help");
        toolsMenu = new JMenu("Tools");

        openMenuItem = new JMenuItem("Open...");
        autoLoadClassifierMenuItem = new JMenuItem("Classifier AutoLoad");
        loadClassifierMenuItem     = new JMenuItem("Load classifier");
        trainClassifierMenuItem    = new JMenuItem("Train classifier");
        trainClassifierMenuItem.setEnabled(false);
        

        averagingItem = new JMenuItem("Averaging");
        resultItem = new JMenuItem("Results");
        onlineDetectionMenuItem = new JMenuItem("On-line Detection");
        aboutMenuItem = new JMenuItem("About");
        onlineDetectionMenuItem.setEnabled(false);

        openMenuItem.addActionListener(new OpenFileListener());
        autoLoadClassifierMenuItem.addActionListener(new LoadClassifierListener(true));
        loadClassifierMenuItem.addActionListener(new LoadClassifierListener(false));
        
        onlineDetectionMenuItem.addActionListener(new OnlineDetectionListener());
        averagingItem.addActionListener(new AveragingListener());
        resultItem.addActionListener(new ResultListener());

        aboutMenuItem.addActionListener(new AboutListener());

        JMenuItem exitMenuItem = new JMenuItem("Exit");

        exitMenuItem.addActionListener(new ExitListener());

        averagingItem.setEnabled(false);
        resultItem.setEnabled(false);

        fileMenu.add(openMenuItem);
        fileMenu.add(new JSeparator());
        fileMenu.add(autoLoadClassifierMenuItem);
        fileMenu.add(loadClassifierMenuItem);
        fileMenu.add(trainClassifierMenuItem);
        fileMenu.add(new JSeparator());
        fileMenu.add(exitMenuItem);
        
        toolsMenu.add(averagingItem);
        toolsMenu.add(resultItem);
        toolsMenu.add(onlineDetectionMenuItem);

        helpMenu.add(aboutMenuItem);

        menu.add(fileMenu);
        menu.add(toolsMenu);
        menu.add(helpMenu);

        return menu;
    }

    /**
     * Vytváøí prostor pro vnitøek aplikace a toolBar.
     */
    private JPanel getInterior() {
        JPanel interior = new JPanel(new BorderLayout());

        interior.add(getToolBar(), BorderLayout.NORTH);
        interior.add(guiController.getSignalsWindow(), BorderLayout.CENTER);

        return interior;
    }

    /**
     * Vytvoøí a vrátí toolbar.
     * @return Toolbar.
     */
    private JToolBar getToolBar() {
        if (toolBar == null) {
            toolBar = new JToolBar();

            openButton = new JButton(guiController.loadIcon("open24.gif"));
            infoButton = new JButton(guiController.loadIcon("information24.gif"));
            waveletDialogBT = new JButton("WT");
            matchingDialogJB = new JButton("MP");
            detectionWT_BT = new JButton("Detection WT");
            detectionMP_BT = new JButton("Detection MP");
            detectionMPandWT_BT = new JButton("Detection MP+WT");
            detectionUniversal = new JButton("Classifier-based Detection");
            waveletDialogBT.setEnabled(false);
            matchingDialogJB.setEnabled(false);
            detectionWT_BT.setEnabled(false);
            detectionMP_BT.setEnabled(false);
            detectionMPandWT_BT.setEnabled(false);
            detectionUniversal.setEnabled(false);
            
            Font buttonsFont = new Font(Font.SANS_SERIF, Font.BOLD, 16);


            openButton.addActionListener(new OpenFileListener());
            infoButton.addActionListener(new AboutListener());
            waveletDialogBT.addActionListener(new WaveletTRListener());
            detectionWT_BT.addActionListener(new DetectionWTListener());
            detectionMP_BT.addActionListener(new DetectionMPListener());
            detectionMPandWT_BT.addActionListener(new DetectionMPandWTListener());
            detectionUniversal.addActionListener(new ClassifierDetectionListener());
            
            matchingDialogJB.addActionListener(new MatchingDialogListener());

            toolBar.add(openButton);
            toolBar.addSeparator();
            toolBar.add(waveletDialogBT);
            toolBar.add(matchingDialogJB);
            toolBar.add(detectionWT_BT);
            toolBar.add(detectionMP_BT);
            toolBar.add(detectionMPandWT_BT);
            toolBar.add(detectionUniversal);
            toolBar.addSeparator();
            toolBar.add(infoButton);
            toolBar.setFloatable(false);
        }

        return toolBar;
    }
    
    public void setEnabledDetection(boolean enabled)
    {
    	detectionWT_BT.setEnabled(enabled);
    	detectionMP_BT.setEnabled(enabled);
    	detectionMPandWT_BT.setEnabled(enabled);
        detectionUniversal.setEnabled(enabled);
    }
    
    public void setEnabledClassifierDetection(boolean enabled)
    {
    	onlineDetectionMenuItem.setEnabled(enabled);
    	//detectionUniversal.setEnabled(enabled);
    }
    
    public void sendProgressUnits(double units) {	
		progressDialog.setProgressUnits(units);
    }
    
    public void wtDetection()
    {
    	progressDialog = new ProgressDialog(MainWindow.this, "WT-detection complete...", mainWindowProvider);
    	mainWindowProvider.waveletTransformDetection();
    }

    /**
     * Obsluhuje tlaèítko openFile
     * Otevírá openFile pomocí presentationProveidera.
     */
    private class OpenFileListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            guiController.openFile();
        }
    }

    /**
     * Obsluhuje tlaèítko exit
     * Otevírá exit pomocí presentationProveidera.
     */
    private class ExitListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            guiController.exitAplication();
        }
    }
    
    private class DetectionWTListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
        	wtDetection();
        }
    }
    
    private class DetectionMPListener implements ActionListener {

        public void actionPerformed(ActionEvent e) 
        {
        	progressDialog = new ProgressDialog(MainWindow.this, "MP-detection complete...", mainWindowProvider);
        	UserBase ub;
    		try
			{
				ub = mainWindowProvider.getUserBase();
				UserAtomDefinition selected = ub.getAtom(0);
	    		double[] function = selected.getValues(selected.getOriginalLength());	
	    		mainWindowProvider.matchingPursuitDetection(function, 15, 200, 5, DetectionAlgorithm.CORELATION);
			}
    		catch (IOException e1)
			{
    			e1.printStackTrace();
    			JOptionPane.showMessageDialog(null,
    	    		    "Convolution atom hasn't been found.",
    	    		    "Loading exception",
    	    		    JOptionPane.ERROR_MESSAGE);
    			e1.printStackTrace();
			}
        }
    }

    private class WaveletTRListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            waveletDialog.setActualLocationAndVisibility();
        }
    }
    
    private class DetectionMPandWTListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
        	progressDialog = new ProgressDialog(MainWindow.this, "MP-preprocessing complete...", mainWindowProvider);
        	mainWindowProvider.mpPreprocessing();
        }
    }
    
    
    private class ClassifierDetectionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
        	progressDialog = new ProgressDialog(MainWindow.this, "Processing complete...", mainWindowProvider);
        	mainWindowProvider.app.classifierDetection();
        }
    }
    
    private class MatchingDialogListener implements ActionListener
    {
		@Override
		public void actionPerformed(ActionEvent event)
		{
			progressDialog = new ProgressDialog(MainWindow.this, "MP-detection complete...", mainWindowProvider);
			matchingPursuitDetection();
		}
    }
    
    private void matchingPursuitDetection()
    {
    	UserBase ub;
		try
		{
			ub = mainWindowProvider.getUserBase();
		
	    	ConvolutionSettingsPanel csp = new ConvolutionSettingsPanel(ub);
			
			Object[] options = {"Apply", "Storno"};
			int n = JOptionPane.showOptionDialog(this, csp, "Correlation settings", JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
			
			if (n == 0)
			{
				UserAtomDefinition selected = ub.getAtom(csp.getSelectedAtomIndex());
				
				double[] function = selected.getValues(selected.getOriginalLength() + csp.getStretch());
				
				for (int i = 0; i < function.length; i++)
				{
					function[i] = function[i] * csp.getScale();
				}
				
				mainWindowProvider.matchingPursuitDetection(function, csp.getMinPosition(), csp.getMaxPosition(), csp.getNumberOfIterations(), csp.getMethod());
			}
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(this,
	    		    "Convolution atom hasn't been found.",
	    		    "Loading exception",
	    		    JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
    }
    
    private class AveragingListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            averagingDialog.setActualLocationAndVisibility();
        }
    }
    
    private class ResultListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            resultDialog.setActualLocationAndVisibility();
        }
    }

    private class AboutListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            mainWindowProvider.about();
        }
    }
    
    private class OnlineDetectionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			onlineDialog.setVisible(true);
			
		}
    	
    }
    
    private class LoadClassifierListener implements ActionListener {
    	private boolean auto;
    	
    	public LoadClassifierListener(boolean auto) {
    		this.auto = auto;
    	}

		@Override
		public void actionPerformed(ActionEvent e) {
			mainWindowProvider.loadClassifier(auto);
			
		}
    	
    }


}
