package icp.gui;

import icp.algorithm.cwt.CWT;
import icp.algorithm.cwt.wavelets.*;
import icp.algorithm.dwt.DWT;
import icp.algorithm.dwt.wavelets.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;


/**
 * 
 * @author Petr - Soukal
 *
 */
public class WaveletTransformDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	private final short DWIDTH = 400, DHEIGHT = 370, BORD = 2, JL_WIDTH = 50, JL_HEIGHT = 20,
						JC_WIDTH = 150, JC_HEIGHT = 20, CHP_WIDTH = 200, CHP_HEIGHT = 100,
						COMPLEX_MORLET_INDEX = 1;
	private JFrame mainWindow;
	private MainWindowProvider mainWindowProvider;
	private JCheckBox[] allChannels;
	
	private WaveletDWT[] waveletsDWT = {new Haar(), new Daubechies4(), new Daubechies6(), new Daubechies8(),
			new Daubechies10(), new Daubechies12(), new Daubechies14(), new Daubechies16(), new Daubechies18(),
			new Daubechies20(), new Coiflet6(), new Coiflet12(), new Coiflet18(), new Coiflet24(), new Coiflet30(),
			new Symmlet4(), new Symmlet6(), new Symmlet8()};
	
	private WaveletCWT[] waveletsCWT = {new Gaussian(), new MexicanHat(), new Morlet()};
	private WaveletCWT[] waveletsComplexCWT = {new ComplexGaussian(), new ComplexMorlet(1, 1)};
	
	private JComboBox waveletsJCB;
	private ButtonGroup bGroup;
	private JRadioButton dwtJRB, cwtJRB, complexCwtJRB;
	private JTextField minScaleCWT_JTF, maxScaleCWT_JTF, stepCWT_JTF,
			constantFb_JTF, constantFc_JTF;
	private JCheckBox useAveragingCHB;
	private JCheckBox selectAllChannelsCHB;
	private JPanel channelsPanel;
	private JButton wTransformBT;
	private JButton scaleAndERPDetectBT;
	private ProgressDialog progressDialog;
	private ScaleDialog scaleDialog;
	


	/**
	 * Vytváøí objekt tøídy.
	 * 
	 * @param mainWindow - okno ve kterém se má dialog otevírat.
	 * @param mainWindowProvider - provider poskytující komunikaci mainWindow a ostatních oken.
	 */
	public WaveletTransformDialog(final JFrame mainWindow, final MainWindowProvider mainWindowProvider)
	{
		super(mainWindow);
		this.setTitle("Wavelet Transformation");
		this.mainWindow = mainWindow;
		this.mainWindowProvider = mainWindowProvider;
				
		this.addWindowListener(new WindowAdapter() {
                        @Override
			public void windowClosing(WindowEvent e)
			{				
				mainWindow.setEnabled(true);		
			}
		});
		
		scaleDialog = new ScaleDialog(this, this.mainWindowProvider);
		bGroup = new ButtonGroup();
		this.add(createInterior());
		this.setSize(new Dimension(DWIDTH, DHEIGHT));
		this.setResizable(false);
		//setChannelsName();
	}
	
	/**
	 * Vytváøí hlavní panel.
	 * 
	 * @return mainPanel.
	 */
	private JPanel createInterior()
	{
		JPanel mainPanel = new JPanel(new BorderLayout());
		
		JPanel wtPanel = new JPanel(new BorderLayout());
		wtPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Wavelet transformation"),
				BorderFactory.createEmptyBorder(BORD, BORD, BORD, BORD)));
				
		JPanel radioPanel = new JPanel(new GridLayout());
		
		dwtJRB = new JRadioButton("DWT");
		dwtJRB.addActionListener(new FunctionDWT_JRB());
		bGroup.add(dwtJRB);
		radioPanel.add(dwtJRB);
		cwtJRB = new JRadioButton("CWT");
		cwtJRB.addActionListener(new FunctionCWT_JRB());
		bGroup.add(cwtJRB);
		radioPanel.add(cwtJRB);
		complexCwtJRB = new JRadioButton("ComplexCWT");
		complexCwtJRB.addActionListener(new FunctionComplexCWT_JRB());
		bGroup.add(complexCwtJRB);
		radioPanel.add(complexCwtJRB);
		
		wtPanel.add(radioPanel, BorderLayout.NORTH);
		wtPanel.add(centerPanel(), BorderLayout.SOUTH);
		
		mainPanel.add(wtPanel, BorderLayout.NORTH);
		mainPanel.add(southPanel(), BorderLayout.SOUTH);
		
		setDWTSetting();
		return mainPanel;
	}
	
	private JPanel centerPanel()
	{		
		JPanel centerPanel = new JPanel(new BorderLayout());
		JPanel panel = new JPanel(new GridLayout());
		
		panel.add(waveletsPanel());
		panel.add(cwtScalePanel());

		JPanel buttonPanel = new JPanel();
		wTransformBT = new JButton("Run Wavelet Transformation");
		wTransformBT.addActionListener(new FunctionRunWT_BT());
		buttonPanel.add(wTransformBT);
		//wTransformBT.setEnabled(false);
		
		centerPanel.add(panel, BorderLayout.NORTH);
		//centerPanel.add(signalPanel(), BorderLayout.CENTER);
		centerPanel.add(buttonPanel, BorderLayout.SOUTH);
		return centerPanel;
	}
	
	private JPanel waveletsPanel()
	{
		JPanel waveletsPanel = new JPanel(new BorderLayout());
		waveletsPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Wavelets"),
				BorderFactory.createEmptyBorder(BORD, BORD, BORD, BORD)));
		
		JPanel wavePanel = new JPanel();
		waveletsJCB = new JComboBox();
		waveletsJCB.setPreferredSize(new Dimension(JC_WIDTH, JC_HEIGHT));
		waveletsJCB.setMaximumSize(new Dimension(JC_WIDTH, JC_HEIGHT));
		waveletsJCB.setMinimumSize(new Dimension(JC_WIDTH, JC_HEIGHT));
		waveletsJCB.addActionListener(new FunctionWaveletsJCB());
		
		JPanel constPanel = new JPanel();
		JPanel constantPanel = new JPanel(new GridLayout(2,2, 10, 15));
		JLabel fbJL = new JLabel("Fb: ");
		fbJL.setPreferredSize(new Dimension(JL_WIDTH, JL_HEIGHT));
		fbJL.setPreferredSize(new Dimension(JL_WIDTH, JL_HEIGHT));
		fbJL.setPreferredSize(new Dimension(JL_WIDTH, JL_HEIGHT));
		constantFb_JTF = new JTextField();
		JLabel fcJL = new JLabel("Fc: ");
		constantFc_JTF = new JTextField();
		constantPanel.add(fbJL);
		constantPanel.add(constantFb_JTF);
		constantPanel.add(fcJL);
		constantPanel.add(constantFc_JTF);
		
		wavePanel.add(waveletsJCB);
		constPanel.add(constantPanel);
		waveletsPanel.add(wavePanel, BorderLayout.NORTH);
		waveletsPanel.add(constPanel, BorderLayout.SOUTH);
		
		return waveletsPanel;
	}
	
	private JPanel cwtScalePanel()
	{		
		JPanel cwtScalePanel = new JPanel();
		cwtScalePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Scale settings"),
				BorderFactory.createEmptyBorder(BORD, BORD, BORD, BORD)));
		
		JPanel panel = new JPanel(new GridLayout(3, 2, 20, 15));
		
		JLabel minScaleJL = new JLabel("Min (>0): ");
		minScaleCWT_JTF = new JTextField();
		JLabel maxScaleJL = new JLabel("Max (>Min): ");
		maxScaleCWT_JTF = new JTextField();
		JLabel stepJL = new JLabel("Step (>0): ");
		stepCWT_JTF = new JTextField();
		
		panel.add(minScaleJL);
		panel.add(minScaleCWT_JTF);
		panel.add(maxScaleJL);
		panel.add(maxScaleCWT_JTF);
		panel.add(stepJL);
		panel.add(stepCWT_JTF);
				
		cwtScalePanel.add(panel);
		return cwtScalePanel;
	}	
	
	/*private JPanel signalPanel()
	{		
		JPanel signalPanel = new JPanel(new BorderLayout());
		signalPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Channels for transformation"),
				BorderFactory.createEmptyBorder(BORD, BORD, BORD, BORD)));
		
		channelsPanel = new JPanel();
		channelsPanel.setLayout(new BoxLayout(channelsPanel, BoxLayout.PAGE_AXIS));
		channelsPanel.setBackground(Color.WHITE);
		
		JPanel panel = new JPanel();
		JScrollPane scrollPane = new JScrollPane(channelsPanel);
		scrollPane.getVerticalScrollBar().setVisibleAmount(5);
		scrollPane.setBackground(Color.WHITE);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setPreferredSize(new Dimension(CHP_WIDTH, CHP_HEIGHT));
		scrollPane.setMaximumSize(new Dimension(CHP_WIDTH, CHP_HEIGHT));
		scrollPane.setMinimumSize(new Dimension(CHP_WIDTH, CHP_HEIGHT));
		panel.add(scrollPane);

		JPanel checkBoxesPanel = new JPanel(new GridLayout(1,0,0,0));
		useAveragingCHB = new JCheckBox("Use averaging");
		selectAllChannelsCHB = new JCheckBox("Select all channels");
		selectAllChannelsCHB.addActionListener(new FunctionSelectAllChannelsCHB());
		checkBoxesPanel.add(useAveragingCHB);
		checkBoxesPanel.add(selectAllChannelsCHB);
					
		signalPanel.add(checkBoxesPanel, BorderLayout.NORTH);
		signalPanel.add(panel, BorderLayout.SOUTH);
		return signalPanel;
	}	*/
	
	
	/**
	 * Vytváøí panel s tlaèítky.
	 * 
	 * @return southPanel.
	 */
	private JPanel southPanel()
	{
		JPanel southPanel = new JPanel(new BorderLayout());		
		
		JPanel toolsPanel = new JPanel();
		toolsPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Scalogram and ERP-detection"),
				BorderFactory.createEmptyBorder(BORD, BORD, BORD, BORD)));
		scaleAndERPDetectBT = new JButton("Show Scalograms And ERP-Detection");
		scaleAndERPDetectBT.addActionListener(new FunctionScalogramBT());
		setEnabledToolsBT(false);
		toolsPanel.add(scaleAndERPDetectBT);
		
		JPanel okPanel = new JPanel();
		JButton okBT = new JButton("Ok");
		okBT.addActionListener(new FunctionOkBT());
		okPanel.add(okBT);

		southPanel.add(toolsPanel, BorderLayout.NORTH);
		southPanel.add(okPanel, BorderLayout.SOUTH);
		
		return southPanel;
	}
	
	
	
	/**
	 * Nastavuje viditelnost dialogu a jeho umístìní na monitoru.
	 */
	public void setActualLocationAndVisibility()
	{
		mainWindow.setEnabled(false);
		this.setLocationRelativeTo(mainWindow);
		this.setVisible(true);
	}
	
	/**
	 * Nastavuje jména všech kanálù v souboru a vkládá je do seznamu pro kontrolované kanály.
	 */
	/*private void setChannelsName()
	{
		List<Channel> channels = mainWindowProvider.app.getHeader().getChannels();
		
		allChannels = new JCheckBox[channels.size()];
		channelsPanel.removeAll();
		
		for(int i = 0; i < allChannels.length; i++)
		{
			allChannels[i] = new JCheckBox(channels.get(i).getName());
			allChannels[i].setSelected(false);
			allChannels[i].setBackground(Color.WHITE);
			allChannels[i].addActionListener(new FunctionChannelsJRB());
			channelsPanel.add(allChannels[i]);
		}	
		
		repaint();
		validate();
	}*/
	
	public void setDWTSetting()
	{
		dwtJRB.setSelected(true);
		minScaleCWT_JTF.setEnabled(false);	
		maxScaleCWT_JTF.setEnabled(false);	
		stepCWT_JTF.setEnabled(false);	
		
		constantFb_JTF.setEnabled(false);
		constantFc_JTF.setEnabled(false);
		waveletsJCB.removeAllItems();

		for(int i = 0; i < waveletsDWT.length;i++)
			waveletsJCB.addItem(waveletsDWT[i].getName());
		
		waveletsJCB.setSelectedIndex(0);
	}
	
	public void setCWTSetting()
	{
		cwtJRB.setSelected(true);
		minScaleCWT_JTF.setEnabled(true);	
		maxScaleCWT_JTF.setEnabled(true);	
		stepCWT_JTF.setEnabled(true);	
		
		constantFb_JTF.setEnabled(false);
		constantFc_JTF.setEnabled(false);

		waveletsJCB.removeAllItems();

		for(int i = 0; i < waveletsCWT.length;i++)
			waveletsJCB.addItem(waveletsCWT[i].getName());
		
		waveletsJCB.setSelectedIndex(0);
	}
	
	public void setComplexCWTSetting()
	{
		complexCwtJRB.setSelected(true);
		minScaleCWT_JTF.setEnabled(true);	
		maxScaleCWT_JTF.setEnabled(true);	
		stepCWT_JTF.setEnabled(true);	
		
		constantFb_JTF.setEnabled(false);
		constantFc_JTF.setEnabled(false);

		waveletsJCB.removeAllItems();

		for(int i = 0; i < waveletsComplexCWT.length;i++)
			waveletsJCB.addItem(waveletsComplexCWT[i].getName());
		
		waveletsJCB.setSelectedIndex(0);
	}
	
	public void setEnabledToolsBT(boolean enabled)
	{
		if(enabled)
			scaleDialog.setScaleDialogData();
		
		scaleAndERPDetectBT.setEnabled(enabled);
	}
	
	public void sendProgressUnits(double units) {	
		progressDialog.setProgressUnits(units);
    }
	
	public void setInvisibleProgressDialog() {	
		progressDialog.setVisible(false);
		this.setEnabled(true);
    }
	
	private class FunctionDWT_JRB implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	setDWTSetting();
        }
    }
	
	private class FunctionCWT_JRB implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	setCWTSetting();
        }
    }
	
	private class FunctionComplexCWT_JRB implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	setComplexCWTSetting();
        }
    }
	
	private class FunctionWaveletsJCB implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	if(complexCwtJRB.isSelected() && waveletsJCB.getSelectedIndex() == 1)
        	{
        		constantFb_JTF.setEnabled(true);
        		constantFc_JTF.setEnabled(true);
        	}
        	else
        	{
        		constantFb_JTF.setEnabled(false);
        		constantFc_JTF.setEnabled(false);
        	}
        }
    }
	
	/**
	 * Obsluhuje tlaèítko pro vykonání automatického oznaèování artefaktù
	 * vybraným kritériem. 
	 */
	private class FunctionRunWT_BT implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	/*int channelsForTransform = 0;
        	
        	for(int i = 0; i < allChannels.length; i++)
        	{
        		if(allChannels[i].isSelected())
        			channelsForTransform++;
        	}
        	
        	int[]channelsIndexes  = new int[channelsForTransform];
        	
        	int index = 0;
        	
        	for(int i = 0; i < channelsIndexes.length; i++)
        	{
        		for(; index < allChannels.length; index++)
            	{
            		if(allChannels[index].isSelected())
            		{
            			channelsIndexes[i] = index;
            			index++;
            			break;
            		}
            	}
        	}*/
        	
        	//System.out.println(channelsIndexes.length);
        	
        	//boolean averaging = useAveragingCHB.isSelected();
        	
        	if(dwtJRB.isSelected())
        	{
        		WaveletDWT wDwt = waveletsDWT[waveletsJCB.getSelectedIndex()];
        		DWT dwt = new DWT(wDwt);        
        		
        		progressDialog = new ProgressDialog(WaveletTransformDialog.this, "DWT complete...", mainWindowProvider);
				mainWindowProvider.sendDWTData(dwt);				
        	}
        	else
        	{
        		double minScale = 0, maxScale = 0, step = 0;
				double fb = 0, fc = 0;
				boolean error = false;
				WaveletCWT wCwt = waveletsCWT[waveletsJCB.getSelectedIndex()];
				
        		try
				{
					minScale = Double.parseDouble(minScaleCWT_JTF.getText());
					maxScale = Double.parseDouble(maxScaleCWT_JTF.getText());
					step = Double.parseDouble(stepCWT_JTF.getText());
					
					if(minScale <= 0 || step <= 0 || minScale > maxScale)
					{
						error = true;
						JOptionPane.showMessageDialog(mainWindow, "Incorrect assignment of values scale.", 
								"Scales error!", JOptionPane.ERROR_MESSAGE, 
								null);
					}
					
				}
				catch (Exception ex)
				{
					error = true;
					JOptionPane.showMessageDialog(mainWindow, "Incorrect assignment of values scale.", 
							"Scales error!", JOptionPane.ERROR_MESSAGE, 
							null);
				}	
				
				if(complexCwtJRB.isSelected())
				{
					if(waveletsJCB.getSelectedIndex() == COMPLEX_MORLET_INDEX)
					{
						try
						{
							fb = Double.parseDouble(constantFb_JTF.getText());
							fc = Double.parseDouble(constantFc_JTF.getText());
							
							if(fc <= 0 || fb <= 0)
							{
								error = true;
								JOptionPane.showMessageDialog(mainWindow, "Constants FB and FC must be higher than zero.", 
										"Constant error!", JOptionPane.ERROR_MESSAGE, 
										null);
							}
							else
								waveletsComplexCWT[waveletsJCB.getSelectedIndex()] = new ComplexMorlet(fb, fc);
						}
						catch (Exception ex)
						{
							error = true;
							JOptionPane.showMessageDialog(mainWindow, "Incorrect assignment of values constants FB, FC.", 
									"Constant error!", JOptionPane.ERROR_MESSAGE, 
									null);
						}
					}
					
					wCwt = waveletsComplexCWT[waveletsJCB.getSelectedIndex()];
				}
				
				
				if(!error)
				{
	        		CWT cwt = new CWT(minScale, maxScale, step, wCwt);

	        		progressDialog = new ProgressDialog(WaveletTransformDialog.this, "CWT complete...", mainWindowProvider);
					mainWindowProvider.sendCWTData(cwt);
				}
        	}   
        }
    }
	
	/**
	 * Obsluhuje tlaèítko pro stornování akce a zavøení dialogu.
	 */
	private class FunctionScalogramBT implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	scaleDialog.setActualLocationAndVisibility();
        }
    }
	
	/**
	 * Obsluhuje tlaèítko pro stornování akce a zavøení dialogu.
	 *//*
	private class FunctionChannelsJRB implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	boolean foundSelectedChannel = false;
        	
        	for(int i = 0; i < allChannels.length; i++)
        	{
        		if(allChannels[i].isSelected())
        		{
        			foundSelectedChannel = true;
        			break;
        		}
        	}       	
        	
        	wTransformBT.setEnabled(foundSelectedChannel);

        }
    }*/
	
	/**
	 * Obsluhuje tlaèítko pro stornování akce a zavøení dialogu.
	 *//*
	private class FunctionSelectAllChannelsCHB implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	boolean select = selectAllChannelsCHB.isSelected(); 
        	        	
        	for(int i = 0; i < allChannels.length; i++)
        	{
        		allChannels[i].setSelected(select);
        	}
        	
        	wTransformBT.setEnabled(select);

        }
    }*/
	
	/**
	 * Obsluhuje tlaèítko pro stornování akce a zavøení dialogu.
	 */
	private class FunctionOkBT implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	mainWindow.setEnabled(true);
        	WaveletTransformDialog.this.setVisible(false);
        }
    }
}
