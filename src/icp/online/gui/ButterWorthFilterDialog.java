package icp.online.gui;
import icp.algorithm.math.ButterWorthFilter;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

	/**
	 * Class providing the GUI dialog for the creation of the butter worth filter.
	 * @author Anezka Jachymova
	 * @version 1.01
	 */
	public class ButterWorthFilterDialog extends JDialog {

		private static final long serialVersionUID = 1L;
		/**
		 * Instance of MainFrame, owner of the dialog.
		 */
		private MainFrame mainFrame;
		
		/*
		 * These attributes are here only because of 
		 * referencing from inner classes.
		 * They have no other use here.
		 */
		private JPanel genPN;
		private JPanel helpPN;
		private JTextArea impulsTA;
		private JTextField upperTF;
		private JTextField lowerTF;
		private JTextField sampleRateTF;
		private JButton okBT;
		
		/**
		 * Creates the dialog and sets its owner.
		 * @param frame Owner of the JDialog.
		 */
		public ButterWorthFilterDialog(MainFrame frame){
			super(frame);
			this.mainFrame = frame;
			this.setModal(true);
			this.setTitle("Butter Worth Filter");	
			this.getContentPane().add(createMainPanel());
			this.pack();
			this.setLocationRelativeTo(null);
			this.setVisible(true);
			
		}
		
		
		
		/**
		 * Creates the JPanel of the Generate tab.
		 * @return JPanel with all needed components.
		 */
		public JPanel createGeneratePanel(){
			genPN = new JPanel(new BorderLayout());
			GridBagLayout gbl = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
			JPanel paramPN = new JPanel(gbl);
			
			lowerTF = new JTextField(12);
			lowerTF.setText("0.1");
			lowerTF.setToolTipText("Interval: 0 - Lower frequency");
			upperTF = new JTextField(12);
			upperTF.setText("8");
			upperTF.setToolTipText("Interval: 0 - Sample rate/2");
			sampleRateTF = new JTextField(12);
			sampleRateTF.setText("1024");
			sampleRateTF.setToolTipText("Interval: > 0");
			
			c.insets = new Insets(5,12,5,12);
			c.fill = GridBagConstraints.HORIZONTAL;
			paramPN.add(new JLabel("Lower frequency: *"),c);
			c.gridx = 1;
			paramPN.add(lowerTF,c);
			c.gridx = 0;
			c.gridy = 1;
			paramPN.add(new JLabel("Higher frequency: *"),c);
			c.gridx = 1;
			paramPN.add(upperTF,c);
			c.gridx = 0;
			c.gridy = 2;
			paramPN.add(new JLabel("Sample rate: *"),c);
			c.gridx = 1;
			paramPN.add(sampleRateTF,c);
			c.gridx = 0;
			c.gridy = 3;
			genPN.add(paramPN, BorderLayout.CENTER);
			
			impulsTA = new JTextArea(12,20);
			impulsTA.setText("Impulse response\r\n");
			impulsTA.setEditable(false);
			impulsTA.setFont(new Font("Calibri",12,12));
			JScrollPane jsc = new JScrollPane(impulsTA);
			genPN.add(jsc, BorderLayout.EAST);
			
			JButton genBT = new JButton("Generate");
			genBT.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					double lower, upper;
					int sampleRate;
					try {
						lower = Double.parseDouble(lowerTF.getText());
					} catch (NumberFormatException e){
						JOptionPane.showMessageDialog(ButterWorthFilterDialog.this,"Lower threshold must be a real number greater than 0.","Wrong value", JOptionPane.ERROR_MESSAGE);
						return;
					}
					try {
						upper = Double.parseDouble(upperTF.getText());
					} catch (NumberFormatException e){
						JOptionPane.showMessageDialog(ButterWorthFilterDialog.this,"Higher threshold must be a real number greater than 0\r\n"
							+ "and lesser than half of sample rate!","Wrong value", JOptionPane.ERROR_MESSAGE);
						return;
					}
					try {
						sampleRate = Integer.parseInt(sampleRateTF.getText());
					} catch (NumberFormatException e){
						JOptionPane.showMessageDialog(ButterWorthFilterDialog.this,"Sample rate must be an integer number.","Wrong value", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					if(checkValues(lower, upper, sampleRate)) {
						impulsTA.setText(null);
						String temp = Arrays.toString(ButterWorthFilter.calculateImpulseResponse(lower, upper, sampleRate));
						String[] pole = temp.substring(1, temp.length() - 1).split(",");
						impulsTA.append("Impulse response\r\nfor values: " + lower + " " + upper + " " + sampleRate);
						for(String cislo : pole)
							impulsTA.append("\r\n"+cislo);
					}
					else {
						JOptionPane.showMessageDialog(ButterWorthFilterDialog.this,"Some of the values are outside the interval!\r\n"
								+ "You can find info about intervals in the tooltips of parametres."
								,"Wrong values", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
			});
			
			JPanel bottomPN = new JPanel();
			okBT = new JButton("OK");
			okBT.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					double lower, upper;
					int sampleRate;
					try {
						lower = Double.parseDouble(lowerTF.getText());
					} catch (NumberFormatException e){
						JOptionPane.showMessageDialog(ButterWorthFilterDialog.this,"Lower threshold must be a real number greater than 0.","Wrong value", JOptionPane.ERROR_MESSAGE);
						return;
					}
					try {
						upper = Double.parseDouble(upperTF.getText());
					} catch (NumberFormatException e){
						JOptionPane.showMessageDialog(ButterWorthFilterDialog.this,"Lower threshold must be a real number greater than 0.","Wrong value", JOptionPane.ERROR_MESSAGE);
						return;
					}
					try {
						sampleRate = Integer.parseInt(sampleRateTF.getText());
					} catch (NumberFormatException e){
						JOptionPane.showMessageDialog(ButterWorthFilterDialog.this,"Sample rate must be a real number,\r\n"
								+ "greater than 0 and lesser than half of sample rate","Wrong value", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					if(checkValues(lower, upper, sampleRate))
						mainFrame.dataFilter = new ButterWorthFilter(lower, upper, sampleRate);
					else {
						JOptionPane.showMessageDialog(ButterWorthFilterDialog.this,"Some of the values are outside the interval!\r\n"
								+ "You can find info about intervals in the tooltips of parametres."
								,"Wrong value", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					ButterWorthFilterDialog.this.dispose();
				}
					
			});
			bottomPN.add(genBT);
			bottomPN.add(okBT);
			genPN.add(bottomPN, BorderLayout.SOUTH);
			return genPN;
		}
		
		/**
		 * Creates JPanel of the tab Help.
		 * @return JPanel - the panel with Help
		 */
		public JPanel createHelpPanel(){
			JPanel helpPN = new JPanel();
			JTextArea help = new JTextArea(20,50);
			JScrollPane jsc = new JScrollPane(help);
			help.setLineWrap(true);
			help.setWrapStyleWord(true);
			help.setEditable(false);
			help.setFont(new Font("Calibri",12,12));
			help.setText("Infinite Impulse Response ButterWorth Filter is used to remove unwanted components from EEG signal."
					+ " This filter is defined by its Impuse Response. This value determines its filtering atributes."
					+ " Response is Infinite because it doesn't become exactly zero past a certain point, but continues indefinitely."
					+ " IIR offers more effitient way of filtering than FIR filter."
					+ " However it can't be modified to involve ripple or to use higher quantity of IIR samples.\r\n"
					+ "This filter can be modified with 3 parameters. All 3 of them (signed with *) are required.\r\n"
					+ "Lower frequency[Hz] - The lower threshold of chosen band. Required parameter.\r\n"
					+ "Higher frequency[Hz] - The higher threshold of chosen band. Required parameter.\r\n"
					+ "Sample rate[Hz] - Determines how many samples are taken each second. Required parameter.\r\n");
			helpPN.add(jsc);
			return helpPN;
		}
		
		/**
		 * Creates the main panel of the dialog window with two tabs: Generovani and Napoveda.
		 * @return JTabbedPane - the main pane of the dialog window
		 */
		public JTabbedPane createMainPanel(){
			JTabbedPane tabbedPN = new JTabbedPane();
			this.genPN = createGeneratePanel();
			this.helpPN = createHelpPanel();
			tabbedPN.addTab("Generate", genPN);
			tabbedPN.addTab("Help", helpPN);
			
			return tabbedPN;
		}
		
		private boolean checkValues(double lower, double upper, int sampleRate) {
			if(lower <= 0 || lower >= upper) return false;
			if(upper <= 0 || upper*2 >= sampleRate) return false;
			if(sampleRate <= 0) return false;
			return true;
		}
	}

