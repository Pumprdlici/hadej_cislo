package icp.gui;

import icp.Const;
import icp.algorithm.cwt.CWT;
import icp.algorithm.dwt.DWT;
import icp.algorithm.math.Mathematic;
import icp.aplication.Element;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;


public class ScaleDialog extends JDialog implements DialogInterface
{
	private static final long serialVersionUID = 1L;
	private final short DWIDTH = 940, DHEIGHT = 640, BORD = 2, BORD_2 = -2;
	private final short WIDTH_E = DWIDTH - 205 , HEIGHT_E = 120;
	private final short WIDTH_S = DWIDTH - 205 , HEIGHT_S = 330;
	private final short WIDTH_L = 130 , HEIGHT_L = 165, HEIGHT_LI = 270;;
	private final short WIDTH_I = 100 , HEIGHT_I = 20;
	private final double MILISECOND_IN_SECOND = 1000.0;
	private Component mainWindow;
	private MainWindowProvider mainWindowProvider;

	private JPanel imagePanelS;
	private JPanel imagePanelE;
	private JButton okBT;
	private double samplFrequency;
	private double epochLength;
	private double[] waveletLengths;
	private int actualWT;
	private ArrayList<double[][]> elementsData;
	private String[] elementsNames;
	private String[] allEpochNames;
	private JList elementsListS;
	private JList epochsListS;
	private JSpinner startSpinner;
	private JSpinner endSpinner;
	private JComboBox showElementsJCB;
	private JComboBox waveletLengthsJCB;
	private int selectedStart, selectedEnd, selectedElementIndex;
	private String selectedElementName;
	private String selectedWaveletLength;
	private String selectedWaveletName;
	private String detectedChars;
	private JLabel infoLabel;
	private JLabel valueOfScalogramLabel;
	private JPanel[][] epochPanels;
	//private JProgressBar scaleProgress;
	//private JProgressBar erpProgress;
	private SignalImage[][] epochImages;
	private boolean erpDetection;
	private JScrollPane erpScrollPane;
	
	private ArrayList<double[][]> dwtData;
	private ArrayList<ArrayList<double[]>> highestCoeficientsDWT;
	private DWT dwt;
	private ArrayList<ArrayList<double[][]>> cwtData;
	private ArrayList<ArrayList<double[]>> highestCoeficientsCWT;
	private CWT cwt;

	/**
	 * Vytváøí objekt tøídy.
	 * 
	 * @param mainWindow - okno ve kterém se má dialog otevírat.
	 * @param mainWindowProvider - provider poskytující komunikaci mainWindow a ostatních oken.
	 */
	public ScaleDialog(Component mainWindow, final MainWindowProvider mainWindowProvider)
	{
		super(mainWindowProvider.mainWindow);
		this.setTitle("Signal and Scalogram");
		this.mainWindow = mainWindow;
		this.mainWindowProvider = mainWindowProvider;
				
		this.addWindowListener(new WindowAdapter() {
                        @Override
			public void windowClosing(WindowEvent e)
			{				
				ScaleDialog.this.mainWindow.setEnabled(true);		
			}
		});
		
		this.add(createInterior());
		this.setSize(new Dimension(DWIDTH, DHEIGHT));
		this.setResizable(false);
	}
	
	private JPanel createInterior()
	{
		JPanel panel = new JPanel(new BorderLayout()); 

		panel.add(createCenterPanel(), BorderLayout.CENTER);
		panel.add(createSouthPanel(), BorderLayout.SOUTH);
		
		return panel;
	}
	
	private JPanel createCenterPanel()
	{
		JPanel mainPanel = new JPanel(new BorderLayout());
		JTabbedPane tabbedPane = new JTabbedPane();
		
		JPanel scalogramPanel = new JPanel(new BorderLayout());
		imagePanelS = new JPanel();
		imagePanelS.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Original epochs and Scalogram"),
				BorderFactory.createEmptyBorder(BORD, BORD, BORD, BORD)));
		imagePanelS.setLayout(new BoxLayout(imagePanelS, BoxLayout.Y_AXIS));
		scalogramPanel.add(scalogramTools(), BorderLayout.WEST);
		scalogramPanel.add(imagePanelS, BorderLayout.CENTER);
		
		
		JPanel erpPanel = new JPanel(new BorderLayout());
		imagePanelE = new JPanel();
		imagePanelE.setLayout(new BoxLayout(imagePanelE, BoxLayout.Y_AXIS));
		erpPanel.add(erpDetectionTools(), BorderLayout.WEST);
		erpScrollPane = new JScrollPane();
		erpScrollPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("All epochs"),
				BorderFactory.createEmptyBorder(BORD, BORD, BORD, BORD)));
		erpScrollPane.setViewportView(imagePanelE);
		erpPanel.add(erpScrollPane, BorderLayout.CENTER);
		
		tabbedPane.addTab("Scalograms", scalogramPanel);		
		tabbedPane.addTab("ERP Detection", erpPanel);		
		
		mainPanel.add(tabbedPane, BorderLayout.CENTER);
		
		return mainPanel;
	}	
	
	private JPanel scalogramTools()
	{
		JPanel scalogramToolsPanel = new JPanel(new BorderLayout());
		JPanel panel = new JPanel(new BorderLayout());
		
		JScrollPane channelsPane = new JScrollPane();
		channelsPane.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		channelsPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Channels"),
				BorderFactory.createEmptyBorder(BORD, BORD, BORD, BORD)));
		channelsPane.setPreferredSize(new Dimension(WIDTH_L, HEIGHT_L));
		channelsPane.setMaximumSize(new Dimension(WIDTH_L, HEIGHT_L));
		channelsPane.setMinimumSize(new Dimension(WIDTH_L, HEIGHT_L));
		elementsListS = new JList();
		elementsListS.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		elementsListS.setLayoutOrientation(JList.VERTICAL);
		channelsPane.setViewportView(elementsListS);
		channelsPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		JScrollPane epochsPane = new JScrollPane();
		epochsPane.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		epochsPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Epochs"),
				BorderFactory.createEmptyBorder(BORD, BORD, BORD, BORD)));
		epochsPane.setPreferredSize(new Dimension(WIDTH_L, HEIGHT_L));
		epochsPane.setMaximumSize(new Dimension(WIDTH_L, HEIGHT_L));
		epochsPane.setMinimumSize(new Dimension(WIDTH_L, HEIGHT_L));
		epochsListS = new JList();
		epochsListS.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		epochsListS.setLayoutOrientation(JList.VERTICAL);
		epochsPane.setViewportView(epochsListS);
		epochsPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		
		valueOfScalogramLabel = new JLabel();
		valueOfScalogramLabel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Scalogram coeficient"),
				BorderFactory.createEmptyBorder(BORD, BORD, BORD, BORD)));
		clearValueOfScalogram();
		
		JButton showScalogramBT = new JButton("Show Scalogram");
		showScalogramBT.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		showScalogramBT.addActionListener(new FunctionShowScalogramBT());
		
		northPanel.add(valueOfScalogramLabel, BorderLayout.NORTH);
		northPanel.add(showScalogramBT, BorderLayout.SOUTH);
		
		panel.add(channelsPane, BorderLayout.NORTH);
		panel.add(epochsPane, BorderLayout.CENTER);
		//panel.add(progressPanelS);
		panel.add(northPanel, BorderLayout.SOUTH);		
		
		scalogramToolsPanel.add(panel, BorderLayout.NORTH);
		
		return scalogramToolsPanel;
	}
	
	private JPanel erpDetectionTools()
	{
		JPanel erpDetectionTools = new JPanel(new BorderLayout());
		JPanel panel = new JPanel(new BorderLayout());
		
		JPanel northPanel = new JPanel(new BorderLayout());
		JPanel channelsShowJP = new JPanel();
		channelsShowJP.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		channelsShowJP.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Show channel"),
				BorderFactory.createEmptyBorder(BORD, BORD, BORD, BORD)));
		showElementsJCB = new JComboBox();
		showElementsJCB.addActionListener(new FunctionShowChannelsJCB());
		showElementsJCB.setPreferredSize(new Dimension(WIDTH_I, HEIGHT_I));
		showElementsJCB.setMaximumSize(new Dimension(WIDTH_I, HEIGHT_I));
		showElementsJCB.setMinimumSize(new Dimension(WIDTH_I, HEIGHT_I));
		channelsShowJP.add(showElementsJCB);
		northPanel.add(channelsShowJP, BorderLayout.NORTH);
		northPanel.add(new JSeparator(), BorderLayout.SOUTH);
		
		JPanel panelERP = new JPanel();
		panelERP.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		panelERP.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("ERP location [ms]"),
				BorderFactory.createEmptyBorder(BORD, BORD, BORD, BORD)));
		
		JPanel panelERPLocation = new JPanel(new GridLayout(0,1,0,10));
		JPanel startPanel = new JPanel(new BorderLayout());
		JLabel startLabel = new JLabel("Start interval:");
		startSpinner = new JSpinner();
		startSpinner.setPreferredSize(new Dimension(WIDTH_I, HEIGHT_I));
		startSpinner.setMaximumSize(new Dimension(WIDTH_I, HEIGHT_I));
		startSpinner.setMinimumSize(new Dimension(WIDTH_I, HEIGHT_I));
		startPanel.add(startLabel, BorderLayout.NORTH);
		startPanel.add(startSpinner, BorderLayout.SOUTH);
		
		JPanel endPanel = new JPanel(new BorderLayout());
		JLabel endLabel = new JLabel("End interval:");
		endSpinner = new JSpinner();
		endSpinner.setPreferredSize(new Dimension(WIDTH_I, HEIGHT_I));
		endSpinner.setMaximumSize(new Dimension(WIDTH_I, HEIGHT_I));
		endSpinner.setMinimumSize(new Dimension(WIDTH_I, HEIGHT_I));
		endPanel.add(endLabel, BorderLayout.NORTH);
		endPanel.add(endSpinner, BorderLayout.SOUTH);
		
		panelERPLocation.add(startPanel);
		panelERPLocation.add(endPanel);
		panelERP.add(panelERPLocation);		
		
		JPanel waveletAndPolarityPanel = new JPanel(new BorderLayout());
		
		JPanel waveletLengthsPanel = new JPanel(new BorderLayout());
		waveletLengthsPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		waveletLengthsPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Wavelet lengths [ms]"),
				BorderFactory.createEmptyBorder(BORD, BORD, BORD, BORD)));	
		
		waveletLengthsJCB = new JComboBox();		
		waveletLengthsJCB.addActionListener(new FunctionWaveletLengthsJCB());
		waveletLengthsJCB.setPreferredSize(new Dimension(WIDTH_I, HEIGHT_I));
		waveletLengthsJCB.setMaximumSize(new Dimension(WIDTH_I, HEIGHT_I));
		waveletLengthsJCB.setMinimumSize(new Dimension(WIDTH_I, HEIGHT_I));
		waveletLengthsPanel.add(waveletLengthsJCB, BorderLayout.NORTH);
		
		
		waveletAndPolarityPanel.add(waveletLengthsPanel, BorderLayout.NORTH);
		
		JScrollPane infoPane = new JScrollPane();
		infoPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Information"),
				BorderFactory.createEmptyBorder(BORD, BORD, BORD, BORD)));
		infoLabel = new JLabel();
		infoPane.setPreferredSize(new Dimension(WIDTH_L, HEIGHT_LI));
		infoPane.setMaximumSize(new Dimension(WIDTH_L, HEIGHT_LI));
		infoPane.setMinimumSize(new Dimension(WIDTH_L, HEIGHT_LI));
		infoPane.setViewportView(infoLabel);
		
		JPanel progressPanelS = new JPanel();
		progressPanelS.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		
		JButton detectErpBT = new JButton("Detect ERP");
		detectErpBT.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		detectErpBT.addActionListener(new FunctionDetectErpBT());
		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(waveletAndPolarityPanel, BorderLayout.NORTH);
		southPanel.add(infoPane, BorderLayout.CENTER);
		southPanel.add(detectErpBT, BorderLayout.SOUTH);
		
		panel.add(northPanel, BorderLayout.NORTH);
		panel.add(panelERP, BorderLayout.CENTER);		
		panel.add(southPanel, BorderLayout.SOUTH);		
		
		erpDetectionTools.add(panel, BorderLayout.NORTH);
		
		return erpDetectionTools;
	}
	
	private JPanel createSouthPanel()
	{
		JPanel southPanel = new JPanel();
		okBT = new JButton("Ok");
		okBT.addActionListener(new FunctionOkBT());
		southPanel.add(okBT);
		
		return southPanel;
	}
	
	
	void createSignalAndScalogramPanel(int elementIndex, int epochIndex)
	{
		imagePanelS.removeAll();		
		
		JPanel panelOriginal = new JPanel();
		panelOriginal.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Element: "+elementsNames[elementIndex]+" - ("+allEpochNames[epochIndex]+")"),
				BorderFactory.createEmptyBorder(BORD_2, BORD_2, BORD_2, BORD_2)));
		panelOriginal.setLayout(new BoxLayout(panelOriginal, BoxLayout.Y_AXIS));
		panelOriginal.setAlignmentX(JComponent.CENTER_ALIGNMENT);
					
		JPanel panelSignal = new JPanel();
		panelSignal.setLayout(new BoxLayout(panelSignal, BoxLayout.Y_AXIS));
		panelSignal.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		panelSignal.setMinimumSize(new Dimension(WIDTH_E, HEIGHT_E));
		panelSignal.setMaximumSize(new Dimension(WIDTH_E, HEIGHT_E));
		panelSignal.setPreferredSize(new Dimension(WIDTH_E, HEIGHT_E));
		SignalImage sigIm = new SignalImage(panelSignal); 
		panelSignal.add(sigIm);
		panelOriginal.add(panelSignal);
		
		
		JPanel panelTransform = new JPanel();
		panelTransform.setLayout(new BoxLayout(panelTransform, BoxLayout.Y_AXIS));
		panelTransform.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		
		JPanel panelSignalTR = new JPanel();
		panelSignalTR.setLayout(new BoxLayout(panelSignalTR, BoxLayout.Y_AXIS));
		panelSignalTR.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		panelSignalTR.setMinimumSize(new Dimension(WIDTH_S, HEIGHT_S));
		panelSignalTR.setMaximumSize(new Dimension(WIDTH_S, HEIGHT_S));
		panelSignalTR.setPreferredSize(new Dimension(WIDTH_S, HEIGHT_S));
		ScaleImage scaleIm = new ScaleImage(panelSignalTR, this); 
		panelSignalTR.add(scaleIm);
		panelTransform.add(panelSignalTR);
		
		
		sigIm.setValues(elementsData.get(elementIndex)[epochIndex]);
		
		if(actualWT == Const.DWT)
		{
			panelTransform.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder("Scalogram ("+dwt.getWavelet().getName()+"):"),
					BorderFactory.createEmptyBorder(BORD_2, BORD_2, BORD_2, BORD_2)));
			
			scaleIm.setValues(dwtData.get(elementIndex)[epochIndex], highestCoeficientsDWT.get(elementIndex).get(epochIndex));
		}
		else
		{
			panelTransform.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder("Scalogram ("+cwt.getWavelet().getName()+"):"),
					BorderFactory.createEmptyBorder(BORD_2, BORD_2, BORD_2, BORD_2)));
			
			scaleIm.setValues(cwtData.get(elementIndex).get(epochIndex), highestCoeficientsCWT.get(elementIndex).get(epochIndex), cwt);
		}
		
		imagePanelS.add(panelOriginal);
		imagePanelS.add(panelTransform);	
		
		imagePanelS.repaint();
		imagePanelS.validate();
	}
	
	void createERPPanels()
	{
		epochPanels = new JPanel[elementsData.size()][elementsData.get(0).length];
		epochImages = new SignalImage[elementsData.size()][elementsData.get(0).length];
		
		imagePanelE.removeAll();		
		
		for(int i = 0; i < epochPanels.length; i++)
		{
			for(int j = 0; j < epochPanels[i].length; j++)
			{
				JPanel panelOriginal = new JPanel();
				panelOriginal.setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder("Element: "+elementsNames[i]+" - ("+allEpochNames[j]+")"),
						BorderFactory.createEmptyBorder(BORD_2, BORD_2, BORD_2, BORD_2)));
				panelOriginal.setLayout(new BoxLayout(panelOriginal, BoxLayout.Y_AXIS));
				panelOriginal.setAlignmentX(JComponent.CENTER_ALIGNMENT);
							
				JPanel panelSignal = new JPanel();
				panelSignal.setLayout(new BoxLayout(panelSignal, BoxLayout.Y_AXIS));
				panelSignal.setAlignmentX(JComponent.LEFT_ALIGNMENT);
				panelSignal.setMinimumSize(new Dimension(WIDTH_E, HEIGHT_E));
				panelSignal.setMaximumSize(new Dimension(WIDTH_E, HEIGHT_E));
				panelSignal.setPreferredSize(new Dimension(WIDTH_E, HEIGHT_E));
				SignalImage sigIm = new SignalImage(panelSignal); 
				epochImages[i][j] = sigIm;
				panelSignal.add(sigIm);
				panelOriginal.add(panelSignal);				
				
				sigIm.setValues(elementsData.get(i)[j]);
				epochPanels[i][j] = panelOriginal;
				
				imagePanelE.add(panelOriginal);	
			}
		}

		imagePanelE.validate();
		imagePanelE.repaint();
	}
	
	private void showEpochsByElement()
	{
		int startElement = 0;
		int endElement = epochPanels.length;
		
		if(selectedElementIndex > 0)
		{
			startElement = selectedElementIndex - 1;
			endElement = selectedElementIndex;
		}
				
		erpScrollPane.setViewportView(null);
		imagePanelE.removeAll();
		
		for(int i = startElement; i < endElement; i++)
		{
			for(int j = 0; j < epochPanels[i].length; j++)
			{				
				imagePanelE.add(epochPanels[i][j]);	
			}
		}
		
		erpScrollPane.setViewportView(imagePanelE);

		imagePanelE.validate();
		imagePanelE.repaint();
	}
	
	private void setErpDetection()
	{
		boolean[][] erpDetection = this.mainWindowProvider.transform.getDetectionERP();
		int row = 0;
		int column = 0;
		boolean rowFind;
		detectedChars = "";
		
		for(int i = 0; i < epochPanels.length; i++)
		{
			row = 0;
			column = 0;
			rowFind = false;
			
			for(int j = 0; j < epochPanels[i].length; j++)
			{
				if(erpDetection[i][j] && !rowFind)
				{
					row = j;
					rowFind = true;
				}
				else if(erpDetection[i][j] && rowFind)
				{
					column = j - 4;
				}
				
				epochImages[i][j].setDetectionERP(erpDetection[i][j]);
				epochPanels[i][j].setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder("Element: "+elementsNames[i]+" - ("+allEpochNames[j]+")"+
								" - [Erp detected: "+erpDetection[i][j]+"]"),
						BorderFactory.createEmptyBorder(BORD_2, BORD_2, BORD_2, BORD_2)));
				
				imagePanelE.repaint();
			}
			
			detectedChars += Element.calculatorChars[row][column];
		}

		setInfoPanel();
	}
	
	private void setLocationIntervalForDWT()
	{
		int step = (int)Math.pow(Mathematic.CONST_2, waveletLengthsJCB.getSelectedIndex()+1);
		startSpinner.setModel(new PositionSpinnerModel(0, step,(int) epochLength - step));
		endSpinner.setModel(new PositionSpinnerModel(step, step,(int) epochLength));
	}
	
	private void setComponents()
	{
		elementsListS.removeAll();
		epochsListS.removeAll();
		waveletLengthsJCB.removeAllItems();
		
		elementsListS.setListData(elementsNames);
		elementsListS.setSelectedIndex(0);
		epochsListS.setListData(allEpochNames);
		epochsListS.setSelectedIndex(0);
		samplFrequency = this.mainWindowProvider.app.getHeader().getSamplingInterval();
		double milisecondFrequency = samplFrequency/MILISECOND_IN_SECOND;
		int levelOfDecomposition;
		int waveletLength;
		
		if(actualWT == Const.DWT)
		{
			selectedWaveletName = dwt.getWavelet().getName();
			epochLength = dwtData.get(0)[0].length*milisecondFrequency;
			levelOfDecomposition = dwt.getLevelsOfDecompositon();
			waveletLength = dwt.getWavelet().getIScaleArray().length;
			waveletLengths = new double[levelOfDecomposition];
			
			for(int i = 0; i < waveletLengths.length; i++)
			{
				waveletLengths[i] = waveletLength*milisecondFrequency*Math.pow(Mathematic.CONST_2, i);
				waveletLengthsJCB.addItem(waveletLengths[i]);
			}
			
			setLocationIntervalForDWT();  
		}
		else
		{
			selectedWaveletName = cwt.getWavelet().getName();
			epochLength = cwtData.get(0).get(0)[0].length*milisecondFrequency;
			levelOfDecomposition = (int)((cwt.getMaxScale()-cwt.getMinScale())/cwt.getStepScale())+1;
			waveletLength = cwt.getWavelet().getMainLength();
			waveletLengths = new double[levelOfDecomposition];
			double scale = cwt.getMinScale();
			
			for(int i = 0; i < waveletLengths.length; i++)
			{
				waveletLengths[i] = scale*waveletLength*milisecondFrequency;
				waveletLengthsJCB.addItem(waveletLengths[i]);
				scale += cwt.getStepScale();
			}
			
			startSpinner.setModel(new PositionSpinnerModel(0,1,(int) epochLength - 1));
			endSpinner.setModel(new PositionSpinnerModel(1,1,(int) epochLength));
		}
		
		createERPPanels();
	}

	public void setValueOfScalogram(String value, String scale, String position)
	{		
		valueOfScalogramLabel.setText("<html>" +
				"Coeficient:<br> "+value+" <br><br>" +
				"Scale:<br> "+scale+" <br><br>" +
				"Position [ms]:<br> "+position+" </html>");
	}
	
	public void clearValueOfScalogram()
	{
		valueOfScalogramLabel.setText("<html>" +
				"Coeficient:<br> none <br><br>" +
				"Scale:<br> none <br><br>" +
				"Position [ms]:<br> none</html>");
	}
	
	private void setInfoPanel()
	{
		
		infoLabel.setText("<html>" +
				"Detected chars:<br>"+ detectedChars +"<br><br>"+
				"Samp. frequency:<br>"+ samplFrequency +" [1/s]<br><br>"+
				"Epoch length:<br>"+ epochLength +" [ms]<br><br>"+
				"Start-interval:<br>"+ selectedStart +" [ms]<br><br>"+
				"End-interval:<br>"+ selectedEnd +" [ms]<br><br>"+
				"Channel:<br>"+ selectedElementName +"<br><br>"+
				"Wavelet-name:<br>"+ selectedWaveletName +"<br><br>"+
				"Wavelet-length:<br>"+ selectedWaveletLength +" [ms]<br><br>"+
				"</html>");
		
		infoLabel.repaint();
	}
	
	public void setScaleDialogData()
	{
		actualWT = this.mainWindowProvider.transform.getActualTransform();
		elementsData = mainWindowProvider.transform.getElementsData(); 
		elementsNames = new String[elementsData.size()];
		
		for(int i = 0;i < elementsNames.length;i++)
			elementsNames[i] = "Element "+(i+1);
		
		allEpochNames = new String[Const.ROWS_COLS_COUNT_OF_ELEMENT];
		
		for(int i = 0; i < allEpochNames.length/2 ;i++)
			allEpochNames[i] = "Row "+(i+1);
		
		int index = 1;
		
		for(int i = allEpochNames.length/2; i < allEpochNames.length ;i++)
			allEpochNames[i] = "Column "+(index++);
		
		dwtData = mainWindowProvider.transform.getTransformedEpochsDWT();
		highestCoeficientsDWT = mainWindowProvider.transform.getHighestCoeficientsDWT();
		dwt = mainWindowProvider.transform.getDWT();
		cwtData = mainWindowProvider.transform.getTransformedEpochsCWT();
		highestCoeficientsCWT = mainWindowProvider.transform.getHighestCoeficientsCWT();
		cwt = mainWindowProvider.transform.getCWT();
		selectedWaveletLength = "0";
		selectedStart = 0;
		selectedEnd = 0;
		detectedChars = "nothing";
		erpDetection = false;
				
		
		setComponents();
		
		showElementsJCB.removeAllItems();
		
		if(elementsNames.length > 1)
			showElementsJCB.addItem("All elements");
			
		for(int i = 0; i < elementsNames.length ;i++)
		{
			showElementsJCB.addItem(elementsNames[i]);
		}
		
		showElementsJCB.setSelectedIndex(0);		
		selectedElementIndex = showElementsJCB.getSelectedIndex();
		selectedElementName = (String) showElementsJCB.getSelectedItem();
		
		setInfoPanel();
	}
	
	/**
	 * Nastavuje viditelnost dialogu a jeho umístìní na monitoru.
	 */
	public void setActualLocationAndVisibility()
	{
		this.setLocationRelativeTo(mainWindow);
		mainWindow.setEnabled(false);
		this.setVisible(true);
	}
	
	private class FunctionShowScalogramBT implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	int elementIndex = elementsListS.getSelectedIndex();
        	int epochIndex = epochsListS.getSelectedIndex();
        	
        	if(elementIndex < 0 || epochIndex < 0)
        	{
        		JOptionPane.showMessageDialog(ScaleDialog.this, "Must be setected element and epoch.", 
						"Selection error!", JOptionPane.ERROR_MESSAGE, 
						null);
        	}
        	else
        		createSignalAndScalogramPanel(elementIndex, epochIndex);
        }
    }
	
	private class FunctionDetectErpBT implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	int start = (Integer)startSpinner.getValue();
        	int end = (Integer)endSpinner.getValue();
        	
        	if(start < end)
        	{
	        	int indexScaleWavelet = waveletLengthsJCB.getSelectedIndex();
	        	selectedWaveletLength = waveletLengthsJCB.getSelectedItem().toString();
	        	selectedElementIndex = showElementsJCB.getSelectedIndex();
	    		selectedElementName = (String) showElementsJCB.getSelectedItem();
	    		selectedStart = start;
	    		selectedEnd = end;
	
	    		mainWindowProvider.detectErp(start, end, indexScaleWavelet);
	        	erpDetection = true;
	        	setErpDetection();   
        	}
        	else
        	{
        		JOptionPane.showMessageDialog(ScaleDialog.this, "End-value must be higher than start-value.", 
						"Interval error!", JOptionPane.ERROR_MESSAGE, 
						null);
        	}
        }
    }
	
	private class FunctionShowChannelsJCB implements ActionListener {
        public void actionPerformed(ActionEvent e) {    
        	selectedElementIndex = showElementsJCB.getSelectedIndex();
    		selectedElementName = (String) showElementsJCB.getSelectedItem();
        	showEpochsByElement();
        	setInfoPanel();
        }
    }
	
	private class FunctionWaveletLengthsJCB implements ActionListener {
        public void actionPerformed(ActionEvent e) {        	
        	if(actualWT == Const.DWT)
        		setLocationIntervalForDWT();
        }
    }
	
	private class FunctionOkBT implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	mainWindow.setEnabled(true);
        	ScaleDialog.this.setVisible(false);  
        }
    }
}
