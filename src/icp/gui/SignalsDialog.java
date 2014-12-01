package icp.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class SignalsDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	private final short DWIDTH = 700, DHEIGHT = 500;
	private final short WIDTH_P = DWIDTH - 25 , HEIGHT_P = 130;
	private JFrame mainWindow;
	private MainWindowProvider mainWindowProvider;
	
	private JScrollPane scrollPane;
	private JPanel mainPanel;
	private JPanel imagePanel;
	private JButton okBT;


	/**
	 * Vytváøí objekt tøídy.
	 * 
	 * @param mainWindow - okno ve kterém se má dialog otevírat.
	 * @param mainWindowProvider - provider poskytující komunikaci mainWindow a ostatních oken.
	 */
	public SignalsDialog(final JFrame mainWindow, final MainWindowProvider mainWindowProvider)
	{
		super(mainWindow);
		this.setTitle("Signals");
		this.mainWindow = mainWindow;
		this.mainWindowProvider = mainWindowProvider;
				
		this.addWindowListener(new WindowAdapter() {
                        @Override
			public void windowClosing(WindowEvent e)
			{				
				mainWindow.setEnabled(true);		
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
	
	private JScrollPane createCenterPanel()
	{
		scrollPane = new JScrollPane();
		mainPanel = new JPanel(new BorderLayout()); 
		imagePanel = new JPanel(new GridLayout(2,0, 0,0)); 
		addBaseLabel();
		mainPanel.add(imagePanel, BorderLayout.NORTH);
		scrollPane.setViewportView(mainPanel);

		
		return scrollPane;
	}
	
	public void addBaseLabel()
	{ 
		imagePanel.removeAll();
		JLabel baseLabel = new JLabel("No selected epochs!");
		imagePanel.add(baseLabel);
	}
	
	
	
	private JPanel createSouthPanel()
	{
		JPanel southPanel = new JPanel();
		okBT = new JButton("Ok");
		okBT.addActionListener(new FunctionOkBT());
		southPanel.add(okBT);
		
		return southPanel;
	}
	
	
	void createSignalsPanels()
	{
		int countPanels = 0;//mainWindowProvider.transform.getSignals().size();
		imagePanel.removeAll();
		int count = 0;//mainWindowProvider.transform.getCastiSig().size();
		for(int i = 0;i < count;i++)
		{
			JPanel panelOriginal = new JPanel();
			panelOriginal.setLayout(new BoxLayout(panelOriginal, BoxLayout.Y_AXIS));
						
			JPanel panelSignal = new JPanel();
			panelSignal.setLayout(new BoxLayout(panelSignal, BoxLayout.Y_AXIS));
			panelSignal.setAlignmentX(JComponent.LEFT_ALIGNMENT);
			JLabel labelSignal = new JLabel("uroven"+ (i));
			labelSignal.setAlignmentX(JComponent.LEFT_ALIGNMENT);
			panelSignal.setMinimumSize(new Dimension(WIDTH_P, HEIGHT_P));
			panelSignal.setMaximumSize(new Dimension(WIDTH_P, HEIGHT_P));
			panelSignal.setPreferredSize(new Dimension(WIDTH_P, HEIGHT_P));
			SignalImage sigIm1 = new SignalImage(panelSignal); 
			panelSignal.add(sigIm1);
			panelOriginal.add(labelSignal);
			panelOriginal.add(panelSignal);
			
			//sigIm1.setValues(mainWindowProvider.transform.getCastiSig().get(i));
			imagePanel.add(panelOriginal);
		}
		
		/*for(int i = 0; i < countPanels;i++)
		{
			JPanel panelOriginal = new JPanel();
			panelOriginal.setLayout(new BoxLayout(panelOriginal, BoxLayout.Y_AXIS));
						
			JPanel panelSignal = new JPanel();
			panelSignal.setLayout(new BoxLayout(panelSignal, BoxLayout.Y_AXIS));
			panelSignal.setAlignmentX(JComponent.LEFT_ALIGNMENT);
			JLabel labelSignal = new JLabel("Original Signal - Epoch "+ (i+1) +" ("+
					mainWindowProvider.transform.getChannelName()+"):");
			labelSignal.setAlignmentX(JComponent.LEFT_ALIGNMENT);
			panelSignal.setMinimumSize(new Dimension(WIDTH_P, HEIGHT_P));
			panelSignal.setMaximumSize(new Dimension(WIDTH_P, HEIGHT_P));
			panelSignal.setPreferredSize(new Dimension(WIDTH_P, HEIGHT_P));
			SignalImage sigIm1 = new SignalImage(panelSignal); 
			panelSignal.add(sigIm1);
			panelOriginal.add(labelSignal);
			panelOriginal.add(panelSignal);
			
			
			JPanel panelTransform = new JPanel();
			panelTransform.setLayout(new BoxLayout(panelTransform, BoxLayout.Y_AXIS));
			
			JPanel panelSignalTR = new JPanel();
			panelSignalTR.setLayout(new BoxLayout(panelSignalTR, BoxLayout.Y_AXIS));
			panelSignalTR.setAlignmentX(JComponent.LEFT_ALIGNMENT);
			JLabel labelSignalTR = new JLabel("Wavelet Coeficients - Epoch "+ (i+1) +" ("+
					mainWindowProvider.transform.getTypeWaveletString()+"):");
			labelSignalTR.setAlignmentX(JComponent.LEFT_ALIGNMENT);
			panelSignalTR.setMinimumSize(new Dimension(WIDTH_P, HEIGHT_P));
			panelSignalTR.setMaximumSize(new Dimension(WIDTH_P, HEIGHT_P));
			panelSignalTR.setPreferredSize(new Dimension(WIDTH_P, HEIGHT_P));
			SignalImage sigIm2 = new SignalImage(panelSignalTR); 
			panelSignalTR.add(sigIm2);
			panelTransform.add(labelSignalTR);
			panelTransform.add(panelSignalTR);
			
			sigIm1.setValues(mainWindowProvider.transform.getSignals().get(i));
			sigIm2.setValues(mainWindowProvider.transform.getTransformedSignal().get(i));
			
			imagePanel.add(panelOriginal);
			imagePanel.add(panelTransform);
		}*/
		
		imagePanel.repaint();
		imagePanel.validate();
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
	
	
	private class FunctionOkBT implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	      	
        	mainWindow.setEnabled(true);        	
        	SignalsDialog.this.setVisible(false);  
        }
    }
}
