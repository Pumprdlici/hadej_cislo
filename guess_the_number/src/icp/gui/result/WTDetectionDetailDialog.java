package icp.gui.result;

import icp.Const;
import icp.algorithm.cwt.CWT;
import icp.gui.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class WTDetectionDetailDialog extends JDialog implements DialogInterface
{
	private static final long serialVersionUID = 1L;
	private final short DWIDTH = 940, DHEIGHT = 450, BORD = 2, BORD_2 = -2;
	private final short WIDTH_E = DWIDTH - 205 , HEIGHT_E = 120;
	private final short WIDTH_S = DWIDTH - 205 , HEIGHT_S = 180;
	private final short ROWS_COLUMNS = 4, COUNT_FRACT = 6;
	private final short WIDTH_L = 130 , HEIGHT_L = 160;
	private final short WIDTH_I = 100 , HEIGHT_I = 20;
	private final double MILISECOND_IN_SECOND = 1000.0;
	private Component dialog;
	private JLabel valueOfScalogramLabel;
	private JLabel infoLabel;
	private MainWindowProvider mainWindowProvider;
	private double[][] waveletCoeficients;
	private double[] highestCoeficients;
	private double[] signalData;
	private JPanel imagePanelS;
	private CWT cwt;
	private int elementIndex;
	private String rowOrColumn;
	private int rowOrColumnIndex;
	private int startIntervalDetection;
	private int endIntervalDetection;
	private double highestCoefInInterval;
	private int indexHighestCoefInInterval;
	


	/**
	 * Vytváøí objekt tøídy.
	 * 
	 * @param mainWindow - okno ve kterém se má dialog otevírat.
	 * @param mainWindowProvider - provider poskytující komunikaci mainWindow a ostatních oken.
	 */
	public WTDetectionDetailDialog(Component dialog, JFrame mainWindow, MainWindowProvider mainWindowProvider)
	{
		super(mainWindow);
		this.setTitle("Detection result detail");
		this.dialog = dialog;
		this.mainWindowProvider = mainWindowProvider;
				
		this.addWindowListener(new WindowAdapter() {
                        @Override
			public void windowClosing(WindowEvent e)
			{				
				WTDetectionDetailDialog.this.dialog.setEnabled(true);		
			}
		});
		
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
		
		mainPanel.add(centerPanel(), BorderLayout.CENTER);
		mainPanel.add(southPanel(), BorderLayout.SOUTH);
		
		return mainPanel;
	}
	
	/**
	 *
	 */
	private JPanel centerPanel()
	{
		JPanel centerPanel = new JPanel(new BorderLayout());
		imagePanelS = new JPanel();
		imagePanelS.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Element and Scalogram"),
				BorderFactory.createEmptyBorder(BORD, BORD, BORD, BORD)));
		imagePanelS.setLayout(new BoxLayout(imagePanelS, BoxLayout.Y_AXIS));
		
		centerPanel.add(infoPanel(), BorderLayout.WEST);
		centerPanel.add(imagePanelS, BorderLayout.CENTER);	
		
		
		return centerPanel;
	}
	
	
	private JPanel infoPanel()
	{
		JPanel infoPanel = new JPanel();
		infoPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Detection info"),
				BorderFactory.createEmptyBorder(BORD, BORD, BORD, BORD)));
		
		JPanel labelsPanel = new JPanel(new BorderLayout());
		
		infoLabel = new JLabel();
		infoLabel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Detection detail"),
				BorderFactory.createEmptyBorder(BORD, BORD, BORD, BORD)));
		valueOfScalogramLabel = new JLabel();
		valueOfScalogramLabel.setPreferredSize(new Dimension(WIDTH_L, HEIGHT_L));
		valueOfScalogramLabel.setMaximumSize(new Dimension(WIDTH_L, HEIGHT_L));
		valueOfScalogramLabel.setMinimumSize(new Dimension(WIDTH_L, HEIGHT_L));
		valueOfScalogramLabel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Scalogram coeficient"),
				BorderFactory.createEmptyBorder(BORD, BORD, BORD, BORD)));
		clearValueOfScalogram();
		setInfo();
		labelsPanel.add(infoLabel, BorderLayout.NORTH);
		labelsPanel.add(valueOfScalogramLabel, BorderLayout.SOUTH);
		
		infoPanel.add(labelsPanel);		
		
		return infoPanel;
	}
	
	
	/**
	 * Vytváøí panel s tlaèítky.
	 * 
	 * @return southPanel.
	 */
	private JPanel southPanel()
	{
		JPanel okPanel = new JPanel();
		JButton okBT = new JButton("Ok");
		okBT.addActionListener(new FunctionOkBT());
		okPanel.add(okBT);

		
		return okPanel;
	}
	
	void createSignalAndScalogramPanel()
	{
		imagePanelS.removeAll();		
		
		JPanel panelOriginal = new JPanel();
		panelOriginal.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Element: "+(elementIndex+1)+
						" - ("+rowOrColumn+""+rowOrColumnIndex+")"),
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
				
		sigIm.setValues(signalData);
		
		panelTransform.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Scalogram ("+cwt.getWavelet().getName()+"):"),
				BorderFactory.createEmptyBorder(BORD_2, BORD_2, BORD_2, BORD_2)));
			
		scaleIm.setValues(waveletCoeficients, highestCoeficients, cwt);
				
		imagePanelS.add(panelOriginal);
		imagePanelS.add(panelTransform);	
		
		imagePanelS.repaint();
		imagePanelS.validate();
	}
	
	public void setSignalData(double[] signalData)
	{
		this.signalData = signalData;
	}
	
	public void setWaveletCoeficients(double[] waveletCoeficients)
	{
		this.waveletCoeficients = new double[1][waveletCoeficients.length];
		this.waveletCoeficients[0] = waveletCoeficients; 
	}
	
	public void setHighestCoeficients(double highestCoeficient)
	{
		this.highestCoeficients = new double[1];
		this.highestCoeficients[0] = highestCoeficient;
	}
	
	public void setCWT(CWT cwt)
	{
		this.cwt = cwt;
	}
	
	public void setElementIndex(int index)
	{
		elementIndex = index;
	}
	
	public void setRowOrColumn(int indexRowOrColumn)
	{
		int result = indexRowOrColumn - ROWS_COLUMNS;
		
		if(result < 0)
		{
			rowOrColumn = "Row";
			rowOrColumnIndex = indexRowOrColumn+1;
		}
		else
		{
			rowOrColumn = "Column";
			rowOrColumnIndex = result+1;
		}
	}
	
	public void setStartIntervalDetection(int start)
	{
		this.startIntervalDetection = start;
	}
	
	public void setEndIntervalDetection(int end)
	{
		this.endIntervalDetection = end;
	}
	
	public void setHighestCoefInInterval(double highestCoef)
	{
		String[] valueString = (""+highestCoef).split("[.]");
		
		if(valueString[1].length() > COUNT_FRACT)
			valueString[1] = valueString[1].substring(0, COUNT_FRACT);
		
		highestCoef = Double.parseDouble(valueString[0]+"."+valueString[1]);
		
		this.highestCoefInInterval = highestCoef;
	}
	
	public void setIndexHighestCoefInInterval(int indexHighestCoef)
	{
		this.indexHighestCoefInInterval = indexHighestCoef;
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
	
	public void setInfo()
	{		
		infoLabel.setText("<html>" +
				"Start-interval:<br> "+startIntervalDetection+" [ms]<br><br>" +
				"End-interval:<br> "+endIntervalDetection+" [ms]<br><br>" +
				"Highest coef. in interval:<br> "+highestCoefInInterval+" <br><br>" +
				"Position highest coef.:<br> "+indexHighestCoefInInterval+" [ms]</html>");
	}
	
	/**
	 * Nastavuje viditelnost dialogu a jeho umístìní na monitoru.
	 */
	public void setActualLocationAndVisibility()
	{
		dialog.setEnabled(false);
		this.setLocationRelativeTo(dialog);
		this.setVisible(true);
		createSignalAndScalogramPanel();
		setInfo();
	}

	
	/**
	 * Obsluhuje tlaèítko pro stornování akce a zavøení dialogu.
	 */
	private class FunctionOkBT implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	dialog.setEnabled(true);
        	WTDetectionDetailDialog.this.setVisible(false);
        }
    }
}
