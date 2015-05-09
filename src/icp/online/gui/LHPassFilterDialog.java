package icp.online.gui;

import icp.algorithm.math.HighPassFilter;
import icp.algorithm.math.LowPassFilter;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 * Class providing the GUI dialog for the creation of the low/high pass filter.
 * @author Anezka Jachymova
 * @version 1.01
 */
public class LHPassFilterDialog extends JDialog{
	
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
	private JPanel mainPN;
	private JTextField borderTF;
	private JTextField sampleRateTF;
	
	/**
	 * Creates the dialog and sets its owner.
	 * @param frame Owner of the JDialog.
	 */
	public LHPassFilterDialog(MainFrame frame){
		super(frame);
		this.mainFrame = frame;
		this.setModal(true);
		this.setTitle("Low/high Pass filter");	
		this.getContentPane().add(createMainPanel());
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
	}
	
	/**
	 * Creates the main panel of the JDialog with all needed components
	 * @return the main panel of the dialog window
	 */
	public JPanel createMainPanel(){
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		mainPN = new JPanel(gbl);
		
		borderTF = new JTextField(12);
		borderTF.setText("8");
		borderTF.setToolTipText("Interval: 0 - vzorkovací frekvence/2");
		sampleRateTF = new JTextField(12);
		sampleRateTF.setText("1024");
		sampleRateTF.setToolTipText("Interval: > 0");
		
		c.insets = new Insets(5,12,5,12);
		c.fill = GridBagConstraints.HORIZONTAL;
		mainPN.add(new JLabel("Hranice: *"),c);
		c.gridx = 1;
		mainPN.add(borderTF,c);
		c.gridx = 0;
		c.gridy = 1;
		mainPN.add(new JLabel("Vzorkovací frekvence: *"),c);
		c.gridx = 1;
		mainPN.add(sampleRateTF,c);
		c.gridx = 0;
		c.gridy = 2;
		final JRadioButton lowBT = new JRadioButton("Low");
		lowBT.setSelected(true);
		final JRadioButton highBT = new JRadioButton("High");
		lowBT.setToolTipText("Low");
		highBT.setToolTipText("High");
		ButtonGroup lowHighBG = new ButtonGroup();
		lowHighBG.add(lowBT);
		lowHighBG.add(highBT);
		mainPN.add(lowBT,c);
		c.gridx = 0;
		c.gridy = 3;
		mainPN.add(highBT, c);
		
		JButton okBT = new JButton("OK");
		okBT.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				double border;
				double sampleRate;
				try {
					border = Double.parseDouble(borderTF.getText());
					if (border <= 0) {
						throw new NumberFormatException();
					}
				} catch (NumberFormatException e){
					JOptionPane.showMessageDialog(LHPassFilterDialog.this,"Hranice musí být reálné èíslo vìtší než 0\r\n"
							+ "a menší než polovina vzorkovací frekvence!","Špatnì zadaná hodnota", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					sampleRate = Double.parseDouble(sampleRateTF.getText());
					if (sampleRate <= 0 || sampleRate <= border*2) {
						throw new NumberFormatException();
					}
				} catch (NumberFormatException e){
					JOptionPane.showMessageDialog(LHPassFilterDialog.this,"Vzorkovací frekvence musí být reálné èíslo vìtší než 0!","Špatnì zadaná hodnota", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				if(lowBT.isSelected())
					mainFrame.dataFilter = new LowPassFilter(border, sampleRate);
				else if(highBT.isSelected())
					mainFrame.dataFilter = new HighPassFilter(border, sampleRate);

				LHPassFilterDialog.this.dispose();
			}
		});
		c.gridx = 0;
		c.gridy = 4;
		
		mainPN.add(okBT, c);
		return mainPN;
	}
}
