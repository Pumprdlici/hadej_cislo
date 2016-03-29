package icp.online.gui;
import icp.algorithm.math.CorrelationArtifactDet;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Class providing the GUI dialog for the creation of the correlation artifact detection.
 * @author Michal Veverka
 * @version 1.00
 */
public class CorrDetDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	/**
	 * Instance of MainFrame, owner of the dialog.
	 */
	private MainFrame mainFrame;
	/**
	 * The pattern used for the correlation.
	 */
	private double[] pattern;
	
	/*
	 * These atributes are here only because of 
	 * referencing from inner classes.
	 * They have no other use here.
	 */
	private JPanel filePN;
	private JPanel gaussPN;
	private JPanel helpPN;
	private JTextArea fileTA;
	private JTextArea gaussTA;
	private JTextField delimiterTF;
	private JTextField fileTF;
	private JTextField nPointsTF;
	private JTextField aTF;
	private JTextField muTF;
	private JTextField sigmaTF;
	private JTextField thresholdTF1;
	private JTextField thresholdTF2;
	private JButton okBT;
	
	/**
	 * Creates the dialog and sets its owner.
	 * @param frame Owner of the JDialog.
	 */
	public CorrDetDialog(MainFrame frame){
		super(frame);
		this.mainFrame = frame;
		this.setModal(true);
		this.setTitle("Correlation method");	
		this.getContentPane().add(createMainPanel());
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
	}
	
	/**
	 * Reads the correlation pattern from a .txt file. The individual values must be separeted by the delimiter 
	 * or each values must be on a new line. In that case the separater can everything but emtpy.
	 * 
	 * @param file .txt file with the correlation pattern.
	 * @param delimiter Delimter that separates the values in the file.
	 * @param log JTextArea into which the information from file processing are being written.
	 * @return Array of double values representing the correlation pattern.
	 */
	public static double[] readPatternFromFile(File file, String delimiter, JTextArea log){
		ArrayList<Double> patternList = new ArrayList<Double>();
		BufferedReader bfr;
		log.setText("");
		try {
			bfr = new BufferedReader(new FileReader(file));
			log.append("File not found. Reading values:\r\n");
			String line;
			try {
				while((line = bfr.readLine()) != null){
					String[] hodnoty = line.trim().split(delimiter);
					for(int i = 0; i<hodnoty.length; i++){
						try {
							double a = Double.parseDouble(hodnoty[i]);
							log.append(a+"\r\n");
							patternList.add(a);
						} catch(NumberFormatException e){
							log.append("Nonnumerical value found: " + hodnoty[i]+"\r\n" +
									"Unable to read the pattern.\r\n");
							bfr.close();
							return null;
						}
					}
				}
				bfr.close();
			} catch (IOException e) {
				log.append("Unexpected end of file. Unable to read the pattern.\r\n");
				return null;
			}
		} catch (FileNotFoundException e1) {
			log.append("File not found. Unable to read the pattern.\r\n");
		}
		double[] pattern = new double[patternList.size()];
		for(int i = 0; i < pattern.length; i++){
			pattern[i] = patternList.get(i);
		}
		return pattern;
	}
	
	/**
	 * Creates the JPanel, that allows the user to read the correlation patter from file.
	 * @return JPanel with GUI.
	 */
	public JPanel createFilePanel(){
		JPanel filePN = new JPanel(new BorderLayout());
		fileTA = new JTextArea(12,20);
		fileTA.setText("Waiting for file...");
		fileTA.setEditable(false);
		fileTA.setFont(new Font("Calibri",12,12));
		JScrollPane jsp = new JScrollPane(fileTA);
		filePN.add(jsp, BorderLayout.EAST);
		JPanel chooseFilePN = new JPanel();
		JButton chooseFileBT = new JButton("Choose a file");
		chooseFileBT.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(CorrDetDialog.this);
				if(returnVal == JFileChooser.APPROVE_OPTION){
					File file = fc.getSelectedFile();
					fileTF.setText(file.getAbsolutePath());
					String delimiter = (delimiterTF.getText().equals("")) ? ";" : delimiterTF.getText();
					CorrDetDialog.this.pattern = readPatternFromFile(file, delimiter, fileTA);
					System.out.println(Arrays.toString(pattern));
				}
			}
		});
		fileTF = new JTextField(30);
		chooseFilePN.add(fileTF);
		chooseFilePN.add(chooseFileBT);
		filePN.add(chooseFilePN, BorderLayout.NORTH);
		
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JPanel paramPN = new JPanel(gbl);
		
		this.delimiterTF = new JTextField(12);
		delimiterTF.setText(";");
		delimiterTF.setToolTipText("Insert the delimiter of the values in the file.");
		thresholdTF1 = new JTextField(12);
		thresholdTF1.setText("0.86");
		thresholdTF1.setToolTipText("Insert maximal value of correlation coefficient.");
		
		c.insets = new Insets(12,12,12,12);
		c.fill = GridBagConstraints.HORIZONTAL;
		paramPN.add(new JLabel("Delimiter:"),c);
		paramPN.add(delimiterTF,c);
		c.gridx = 0;
		c.gridy = 1;
		paramPN.add(new JLabel("Max. corr coeffient:"),c);
		c.gridx = 1;
		paramPN.add(thresholdTF1,c);
		
		filePN.add(paramPN, BorderLayout.CENTER);
		JPanel bottomPN = new JPanel();
		JButton okBT = new JButton("OK");
		okBT.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				double threshold = -2;
				try {
					threshold = Double.parseDouble(thresholdTF1.getText());
				} catch (NumberFormatException e){
					JOptionPane.showMessageDialog(CorrDetDialog.this,"The maximal corr. coefficient value must be numerical.");
					return;
				}
				if(Math.abs(threshold)>1.0){
					JOptionPane.showMessageDialog(CorrDetDialog.this,"The maximal corr. coefficient \r\n" +
							"must be numerical and \r\n" + "in the range of <-1,1>.", "Wrong threshold of corr. coeff.", JOptionPane.ERROR_MESSAGE);
				} else if(CorrDetDialog.this.pattern == null){
					JOptionPane.showMessageDialog(CorrDetDialog.this,"The pattern for the correlation was not read. Please try again and \r\n" +
							"check the file for nonnumerical values or wrong decimal points. \r\n",
							"Wrong correlation pattern", JOptionPane.ERROR_MESSAGE);
				} else {
					MainFrame.artifactDetection = new CorrelationArtifactDet(CorrDetDialog.this.pattern, threshold);
					CorrDetDialog.this.dispose();
				}
			}
		});
		bottomPN.add(okBT);
		filePN.add(bottomPN, BorderLayout.SOUTH);
		return filePN;
	}
	
	/**
	 * Creates the JPanel, that allows the user to generate the pattern as gaussian curve.
	 * @return JPanel with the GUI.
	 */
	public JPanel createGaussPanel(){
		JPanel gaussPN = new JPanel(new BorderLayout());
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JPanel paramPN = new JPanel(gbl);
		
		nPointsTF = new JTextField(12);
		nPointsTF.setText("250");
		nPointsTF.setToolTipText("Insert the number of points of the pattern.\r\n One point represents 1 ms of the signal.");
		aTF = new JTextField(12);
		aTF.setText("1");
		aTF.setToolTipText("Type the amplitude of the curve.");
		muTF = new JTextField(12);
		muTF.setText("0");
		muTF.setToolTipText("Type the shift of the amplitude on the x-axis.");
		sigmaTF = new JTextField(12);
		sigmaTF.setText("1");
		sigmaTF.setToolTipText("Type the width of the hill.");
		thresholdTF2 = new JTextField(12);
		thresholdTF2.setText("0.86");
		thresholdTF2.setToolTipText("Type the threshold for the correlation coefficient.");
		
		c.insets = new Insets(5,12,5,12);
		c.fill = GridBagConstraints.HORIZONTAL;
		paramPN.add(new JLabel("No. of points:"),c);
		c.gridx = 1;
		paramPN.add(nPointsTF,c);
		c.gridx = 0;
		c.gridy = 1;
		paramPN.add(new JLabel("Value a:"),c);
		c.gridx = 1;
		paramPN.add(aTF,c);
		c.gridx = 0;
		c.gridy = 2;
		paramPN.add(new JLabel("Value mu:"),c);
		c.gridx = 1;
		paramPN.add(muTF,c);
		c.gridx = 0;
		c.gridy = 3;
		paramPN.add(new JLabel("Value sigma:"),c);
		c.gridx = 1;
		paramPN.add(sigmaTF,c);
		c.gridx = 0;
		c.gridy = 4;
		paramPN.add(new JLabel("Max. corr. coef:"),c);
		c.gridx = 1;
		paramPN.add(thresholdTF2,c);
		gaussPN.add(paramPN, BorderLayout.CENTER);
		
		gaussTA = new JTextArea(12,20);
		gaussTA.setText("Generated curve:\r\n");
		gaussTA.setEditable(false);
		gaussTA.setFont(new Font("Calibri",12,12));
		JScrollPane jsc = new JScrollPane(gaussTA);
		gaussPN.add(jsc, BorderLayout.EAST);
		
		JButton genBT = new JButton("Generate");
		genBT.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				double a, sigma, mu = 1.0;
				int nPoints = 200;
				try {
					nPoints = Integer.parseInt(nPointsTF.getText());
				} catch (NumberFormatException e){
					JOptionPane.showMessageDialog(CorrDetDialog.this,"The no. of points must be a numerical value.","Nonnumerical value", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					a = Double.parseDouble(aTF.getText());
				} catch (NumberFormatException e){
					JOptionPane.showMessageDialog(CorrDetDialog.this,"The value of 'a' must be a numerical value.","Nonnumerical value", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					sigma = Double.parseDouble(sigmaTF.getText());
				} catch (NumberFormatException e){
					JOptionPane.showMessageDialog(CorrDetDialog.this,"The value of 'mu' must be a numerical value.","Nonnumerical value", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					mu = Double.parseDouble(muTF.getText());
				} catch (NumberFormatException e){
					JOptionPane.showMessageDialog(CorrDetDialog.this,"The value of 'sigma' must be a numerical value.","Nonnumerical value", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(nPoints<0){
					JOptionPane.showMessageDialog(CorrDetDialog.this,"The no. of poitns must be higher than one.","Nonnumerical value", JOptionPane.ERROR_MESSAGE);
				} else {
					CorrDetDialog.this.pattern = CorrelationArtifactDet.generateGaussianPattern(nPoints, a, mu, sigma);
					okBT.setEnabled(true);
					gaussTA.setText("Generated curve:\r\n");
					for(int i = 0; i<pattern.length; i++){
						gaussTA.append(i + ": " + pattern[i]+"\r\n");
					}
				}
			}
		});
		
		JPanel bottomPN = new JPanel();
		okBT = new JButton("OK");
		okBT.setEnabled(false);
		okBT.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				double threshold = -2;
				try {
					threshold = Double.parseDouble(thresholdTF2.getText());
				} catch (NumberFormatException e){
					JOptionPane.showMessageDialog(CorrDetDialog.this,"The value of the corr. coeff. must be numerical.","Nonnumerical value", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(Math.abs(threshold)>1.0){
					JOptionPane.showMessageDialog(CorrDetDialog.this,"The value of the corr. coeff. must be\r\nnumerical " +
							"and within the range \r\n" + "of <-1,1>.", "Nonnumerical value", JOptionPane.ERROR_MESSAGE);
				} else {
					mainFrame.artifactDetection = new CorrelationArtifactDet(CorrDetDialog.this.pattern, threshold);
					CorrDetDialog.this.dispose();
				}
			}
				
		});
		bottomPN.add(genBT);
		bottomPN.add(okBT);
		gaussPN.add(bottomPN, BorderLayout.SOUTH);
		return gaussPN;
	}
	
	public JPanel createHelpPanel(){
		JPanel helpPN = new JPanel();
		JTextArea help = new JTextArea(20,50);
		JScrollPane jsc = new JScrollPane(help);
		help.setLineWrap(true);
		help.setWrapStyleWord(true);
		help.setEditable(false);
		help.setFont(new Font("Calibri",12,12));
		help.setText("The correlation method is used for the detecion and removal of artifact in EEG signal." +
				" The method searches for a similarity between two signals. " +
				"That is accomplished via the Pearson's correlation coefficient\r\n r: " +
				"r = (n*sum(xi*yi)-sum(xi)*sum(yi)) / (sqrt(n*sum(xi*xi)-sum(xi)*sum(xi)) * sqrt(n*sum(yi*yi)-sum(yi)*sum(yi)))\r\n" +
				"This coefficient has values of <-1,1>. It reaches the value -1 in case of anti-correlation of the two signals. " +
				"Opposite to that, the coefficient reaches the value of 1 when the values of both signals are increasing at the same rate. " +
				"Two arrays of double values are correlated. If the result of the correlation is higher than the set threshold an artifact is detected " +
				"and the epoch is removed from the signal.\r\n\r\n" +
				"The correlation pattern can be chosen in tow ways: \r\n-from a .txt file (the values must be separated bt the set delimiter" +
				"\r\n-generated as Gaussian curve using the equation y = a*e^(-((x-mu)^2)/(2*sigma^2)");
		helpPN.add(jsc);
		return helpPN;
	}
	
	/**
	 * Creates the main panel of the GUI with two tabs: file and gauss.
	 * @return JTabbedPane with the GUI.
	 */
	public JTabbedPane createMainPanel(){
		JTabbedPane tabbedPN = new JTabbedPane();
		this.filePN = createFilePanel();
		this.gaussPN = createGaussPanel();
		this.helpPN = createHelpPanel();
		tabbedPN.addTab("File", filePN);
		tabbedPN.addTab("Gaussian curve", gaussPN);
		tabbedPN.addTab("Help", helpPN);
		
		return tabbedPN;
	}
	
}
