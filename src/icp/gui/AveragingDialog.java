package icp.gui;

import icp.Const;
import icp.application.SessionManager;
import icp.data.Channel;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

import javax.swing.*;

public class AveragingDialog extends JDialog
{
	private final short DWIDTH = 320, DHEIGHT = 470, SP_WIDTH = 200, SP_HEIGHT = 100, BORD = 2;
	private JFrame mainWindow;
	private MainWindowProvider mainWindowProvider;
	private ButtonGroup buttonGroup;
	private JRadioButton[] allChannels;
	private JPanel channelsPanel;
	private JButton averagingBT;
	private JSpinner spinnerBaseline;
	private JSpinner spinnerCountEpochForAveraging;
	private JCheckBox useBaselineCHB;
	private SessionManager app;
	
	public AveragingDialog(final JFrame mainWindow, final MainWindowProvider mainWindowProvider)
	{
		super(mainWindow);
		this.setTitle("Averaging by epoch");
		this.mainWindow = mainWindow;
		this.mainWindowProvider = mainWindowProvider; 
		this.app = mainWindowProvider.app;
		
		this.addWindowListener(new WindowAdapter() {
            @Override
			public void windowClosing(WindowEvent e)
			{				
				mainWindow.setEnabled(true);		
			}
		});
		
		this.add(interior());
		this.setSize(new Dimension(DWIDTH, DHEIGHT));
		this.setResizable(false);
		setChannelsName();
		
	}
	
	public JPanel interior()
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Epoch averaging"),
				BorderFactory.createEmptyBorder(BORD, BORD, BORD, BORD)));		
		
		panel.add(northPanel(), BorderLayout.NORTH);
		panel.add(buttonPanel(), BorderLayout.SOUTH);
		return panel;
	}
	
	private JPanel northPanel()
	{
		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(baselinePanel(), BorderLayout.NORTH);
		southPanel.add(countEpochPanel(), BorderLayout.CENTER);
		southPanel.add(signalPanel(), BorderLayout.SOUTH);
		
		return southPanel;
	}
	
	private JPanel baselinePanel()
	{		
		JPanel baselinePanel = new JPanel(new GridLayout(1,0));
		baselinePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Baseline tools"),
				BorderFactory.createEmptyBorder(BORD, BORD, BORD, BORD)));
		useBaselineCHB = new JCheckBox("Baseline correction");
		useBaselineCHB.addActionListener(new FunctionBaselineCorrectionCHB());
		
		JPanel spinnerPanel = new JPanel();
		spinnerPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Added value for shift"),
				BorderFactory.createEmptyBorder(BORD, BORD, BORD, BORD)));
		spinnerBaseline = new JSpinner(new SpinnerNumberModel(0, -1000, 1000, 1));
		spinnerBaseline.setEnabled(false);
		spinnerPanel.add(spinnerBaseline);
		
		baselinePanel.add(useBaselineCHB);
		baselinePanel.add(spinnerPanel);	
		
		return baselinePanel;
	}
	
	private JPanel countEpochPanel()
	{		
		JPanel countEpochPanel = new JPanel();
		countEpochPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Epoch count for averaging"),
				BorderFactory.createEmptyBorder(BORD, BORD, BORD, BORD)));
		JLabel countLabel = new JLabel("Epoch count: ");
		int max = app.getHeader().getEpochs().size()-1;
		spinnerCountEpochForAveraging = new JSpinner(new SpinnerNumberModel(1,1,max,1));
				
		countEpochPanel.add(countLabel);
		countEpochPanel.add(spinnerCountEpochForAveraging);	
		
		return countEpochPanel;
	}
	
	private JPanel signalPanel()
	{
		channelsPanel = new JPanel();
		channelsPanel.setLayout(new BoxLayout(channelsPanel, BoxLayout.PAGE_AXIS));
		channelsPanel.setBackground(Color.WHITE);
		
		JPanel signalPanel = new JPanel();
		signalPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Channels for averaging"),
				BorderFactory.createEmptyBorder(BORD, BORD, BORD, BORD)));
		JScrollPane scrollPane = new JScrollPane(channelsPanel);
		scrollPane.setBackground(Color.WHITE);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setPreferredSize(new Dimension(SP_WIDTH, SP_HEIGHT));
		scrollPane.setMaximumSize(new Dimension(SP_WIDTH, SP_HEIGHT));
		scrollPane.setMinimumSize(new Dimension(SP_WIDTH, SP_HEIGHT));
		signalPanel.add(scrollPane);
		
		return signalPanel;
	}
	
	private JPanel buttonPanel()
	{
		JPanel buttonPanel = new JPanel(new BorderLayout());
		
		JPanel panelAveragingButton = new JPanel();
		panelAveragingButton.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Run averaging"),
				BorderFactory.createEmptyBorder(BORD, BORD, BORD, BORD)));
		averagingBT = new JButton("Averaging");
		averagingBT.addActionListener(new FunctionAveragingBT());
		panelAveragingButton.add(averagingBT);
		
		JPanel panelOkButton = new JPanel();
		JButton okButton = new JButton("Ok");
		okButton.addActionListener(new FunctionOkBT());
		panelOkButton.add(okButton);		
		
		buttonPanel.add(panelAveragingButton, BorderLayout.NORTH);
		buttonPanel.add(panelOkButton, BorderLayout.SOUTH);	
		
		return buttonPanel;
	}
	
	/**
	 * Nastavuje jména všech kanálù v souboru a vkládá je do seznamu pro kontrolované kanály.
	 */
	public void setChannelsName()
	{
		List<Channel> channels = mainWindowProvider.app.getHeader().getChannels();
		
		buttonGroup = new ButtonGroup();
		allChannels = new JRadioButton[channels.size()];
		channelsPanel.removeAll();
		
		for(int i = 0; i < allChannels.length; i++)
		{
			allChannels[i] = new JRadioButton(channels.get(i).getName());
			allChannels[i].setSelected(false);
			allChannels[i].setBackground(Color.WHITE);
			channelsPanel.add(allChannels[i]);
			buttonGroup.add(allChannels[i]);
		}	
		
		allChannels[0].setSelected(true);
		
		repaint();
		validate();
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
	 */
	private class FunctionAveragingBT implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	int epochCountForAveraging = (Integer) spinnerCountEpochForAveraging.getValue();
        	int integerPart = (app.getEpochs().size()-1)/epochCountForAveraging;
        	int rest = (app.getEpochs().size()-1)%epochCountForAveraging;
        	
        	
        	if(rest == 0 && integerPart%Const.ROWS_COLS_COUNT_OF_ELEMENT == 0)
        	{
	        	int channelIndex = 0;
	        	boolean useBaselineCorection = useBaselineCHB.isSelected();
	        	int shiftValue =  (Integer) spinnerBaseline.getValue();
	        	
	        	
	        	for(int i = 0;i < allChannels.length ;i++)
	        	{
	        		if(allChannels[i].isSelected())
	        		{
	        			channelIndex = i;
	        			break;
	        		}
	        	}       	
	        	
	        	mainWindowProvider.averaging(epochCountForAveraging, channelIndex, useBaselineCorection, shiftValue);
        	}
        	else
        	{
        		JOptionPane.showMessageDialog(mainWindow, "Wrong count of epoch.", 
						"Epochs count error!", JOptionPane.ERROR_MESSAGE, 
						null);
        	}
        	
        }
    }
	
	/**
	 */
	private class FunctionBaselineCorrectionCHB implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	
        	spinnerBaseline.setEnabled(useBaselineCHB.isSelected());
        }
    }
	
	/**
	 * Obsluhuje tlaèítko pro stornování akce a zavøení dialogu.
	 */
	private class FunctionOkBT implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	mainWindow.setEnabled(true);
        	AveragingDialog.this.setVisible(false);
        }
    }
}
