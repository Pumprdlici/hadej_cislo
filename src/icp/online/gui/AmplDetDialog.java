package icp.online.gui;

import icp.algorithm.math.AmplitudeArtifactDet;
import icp.algorithm.math.GradientArtifactDet;

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
 * Class providing the GUI dialog for the creation of the amplitude artifact detection.
 * @author Anezka Jachymova
 * @version 1.01
 */
public class AmplDetDialog extends JDialog{
	
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
	
	/**
	 * Creates the dialog and sets its owner.
	 * @param frame Owner of the JDialog.
	 */
	public AmplDetDialog(MainFrame frame){
		super(frame);
		this.mainFrame = frame;
		this.setModal(true);
		this.setTitle("Amplitude Artifact Detection Dialog");	
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
		borderTF.setText("75");
		borderTF.setToolTipText("Interval: > 0");
		
		c.insets = new Insets(5,12,5,12);
		c.fill = GridBagConstraints.HORIZONTAL;
		mainPN.add(new JLabel("Hranice: *"),c);
		c.gridx = 1;
		mainPN.add(borderTF,c);
		c.gridx = 0;
		c.gridy = 1;
		final JRadioButton amplBT = new JRadioButton("Amplitude");
		amplBT.setSelected(true);
		final JRadioButton gradBT = new JRadioButton("Gradient");
		amplBT.setToolTipText("Amplitude");
		gradBT.setToolTipText("Gradient");
		ButtonGroup lowHighBG = new ButtonGroup();
		lowHighBG.add(amplBT);
		lowHighBG.add(gradBT);
		mainPN.add(amplBT,c);
		c.gridx = 0;
		c.gridy = 2;
		mainPN.add(gradBT, c);
		
		JButton okBT = new JButton("OK");
		okBT.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				double border;
				try {
					border = Double.parseDouble(borderTF.getText());
					if (border <= 0) {
						throw new NumberFormatException();
					}
				} catch (NumberFormatException e){
					JOptionPane.showMessageDialog(AmplDetDialog.this,"Hranice musí být reálná èíslo vìtší než 0!","Špatnì zadaná hodnota", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				if(amplBT.isSelected())
					mainFrame.artifactDetection = new AmplitudeArtifactDet(border);
				else if(gradBT.isSelected())
					mainFrame.artifactDetection = new GradientArtifactDet(border);

				AmplDetDialog.this.dispose();
			}
		});
		c.gridx = 0;
		c.gridy = 3;
		
		mainPN.add(okBT, c);
		return mainPN;
	}
}