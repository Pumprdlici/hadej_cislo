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
		 * Creates the JPanel of the Generovani tab.
		 * @return JPanel with all needed components.
		 */
		public JPanel createGeneratePanel(){
			genPN = new JPanel(new BorderLayout());
			GridBagLayout gbl = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
			JPanel paramPN = new JPanel(gbl);
			
			lowerTF = new JTextField(12);
			lowerTF.setText("0.1");
			lowerTF.setToolTipText("Interval: 0 - horní hranice");
			upperTF = new JTextField(12);
			upperTF.setText("8");
			upperTF.setToolTipText("Interval: 0 - vzorkovací frekvence/2");
			sampleRateTF = new JTextField(12);
			sampleRateTF.setText("1024");
			sampleRateTF.setToolTipText("Interval: > 0");
			
			c.insets = new Insets(5,12,5,12);
			c.fill = GridBagConstraints.HORIZONTAL;
			paramPN.add(new JLabel("Dolní hranice: *"),c);
			c.gridx = 1;
			paramPN.add(lowerTF,c);
			c.gridx = 0;
			c.gridy = 1;
			paramPN.add(new JLabel("Horní hranice: *"),c);
			c.gridx = 1;
			paramPN.add(upperTF,c);
			c.gridx = 0;
			c.gridy = 2;
			paramPN.add(new JLabel("Vzorkovací frekvence: *"),c);
			c.gridx = 1;
			paramPN.add(sampleRateTF,c);
			c.gridx = 0;
			c.gridy = 3;
			genPN.add(paramPN, BorderLayout.CENTER);
			
			impulsTA = new JTextArea(12,20);
			impulsTA.setText("Impulzní odezva\r\n");
			impulsTA.setEditable(false);
			impulsTA.setFont(new Font("Calibri",12,12));
			JScrollPane jsc = new JScrollPane(impulsTA);
			genPN.add(jsc, BorderLayout.EAST);
			
			JButton genBT = new JButton("Generuj");
			genBT.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					double lower, upper;
					int sampleRate;
					try {
						lower = Double.parseDouble(lowerTF.getText());
					} catch (NumberFormatException e){
						JOptionPane.showMessageDialog(ButterWorthFilterDialog.this,"Dolní hranice musí být reálná èíslo.","Špatnì zadaná hodnota", JOptionPane.ERROR_MESSAGE);
						return;
					}
					try {
						upper = Double.parseDouble(upperTF.getText());
					} catch (NumberFormatException e){
						JOptionPane.showMessageDialog(ButterWorthFilterDialog.this,"Horní hranice musí být reálné èíslo vìtší než 0\r\n"
							+ "a menší než polovina vzorkovací frekvence!","Špatnì zadaná hodnota", JOptionPane.ERROR_MESSAGE);
						return;
					}
					try {
						sampleRate = Integer.parseInt(sampleRateTF.getText());
					} catch (NumberFormatException e){
						JOptionPane.showMessageDialog(ButterWorthFilterDialog.this,"Vzorkovací frekvence musí být celoèíselná hodnota.","Špatnì zadaná hodnota", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					if(checkValues(lower, upper, sampleRate)) {
						impulsTA.setText(null);
						String temp = Arrays.toString(ButterWorthFilter.calculateImpulseResponse(lower, upper, sampleRate));
						String[] pole = temp.substring(1, temp.length() - 1).split(",");
						impulsTA.append("Impulzní odezva\r\npro hodnoty: " + lower + " " + upper + " " + sampleRate);
						for(String cislo : pole)
							impulsTA.append("\r\n"+cislo);
					}
					else {
						JOptionPane.showMessageDialog(ButterWorthFilterDialog.this,"Nìkterá z hodnot je zadána mimo povolený interval!\r\n"
								+ "Informace o intervalech naleznete v tooltipech parametrù."
								,"Špatnì zadaná hodnota", JOptionPane.ERROR_MESSAGE);
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
						JOptionPane.showMessageDialog(ButterWorthFilterDialog.this,"Dolní hranice musí být reálná èíslo.","Špatnì zadaná hodnota", JOptionPane.ERROR_MESSAGE);
						return;
					}
					try {
						upper = Double.parseDouble(upperTF.getText());
					} catch (NumberFormatException e){
						JOptionPane.showMessageDialog(ButterWorthFilterDialog.this,"Horní hranice musí reálné èíslo.","Špatnì zadaná hodnota", JOptionPane.ERROR_MESSAGE);
						return;
					}
					try {
						sampleRate = Integer.parseInt(sampleRateTF.getText());
					} catch (NumberFormatException e){
						JOptionPane.showMessageDialog(ButterWorthFilterDialog.this,"Vzorkovací frekvence musí být reálné èíslo\r\n"
								+ "vìtší než 0 a menší než polovina vzorkovací frevence!","Špatnì zadaná hodnota", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					if(checkValues(lower, upper, sampleRate))
						mainFrame.dataFilter = new ButterWorthFilter(lower, upper, sampleRate);
					else {
						JOptionPane.showMessageDialog(ButterWorthFilterDialog.this,"Nìkterá z hodnot je zadána mimo povolený interval!\r\n"
								+ "Informace o intervalech naleznete v tooltipech parametrù."
								,"Špatnì zadaná hodnota", JOptionPane.ERROR_MESSAGE);
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
		 * Creates JPanel of the tab Napoveda.
		 * @return JPanel - the panel with Napoveda
		 */
		public JPanel createHelpPanel(){
			JPanel helpPN = new JPanel();
			JTextArea help = new JTextArea(20,50);
			JScrollPane jsc = new JScrollPane(help);
			help.setLineWrap(true);
			help.setWrapStyleWord(true);
			help.setEditable(false);
			help.setFont(new Font("Calibri",12,12));
			help.setText("Butter Worth Filter");
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
			tabbedPN.addTab("Generování", genPN);
			tabbedPN.addTab("Nápovìda", helpPN);
			
			return tabbedPN;
		}
		
		private boolean checkValues(double lower, double upper, int sampleRate) {
			if(lower <= 0 || lower >= upper) return false;
			if(upper <= 0 || upper*2 >= sampleRate) return false;
			if(sampleRate <= 0) return false;
			return true;
		}
	}

