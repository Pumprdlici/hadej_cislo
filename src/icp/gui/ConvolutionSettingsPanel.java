package icp.gui;

import icp.algorithm.mp.*;
import icp.gui.result.SignalViewer2;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;


@SuppressWarnings("serial")
public class ConvolutionSettingsPanel extends JPanel
{
	private UserBase userBase;
	
	private SignalViewer2 function;
	
	private JComboBox functionsJCB;
	
	private JSpinner scaleJS;
	
	private JSpinner minPosJS;
	
	private JSpinner maxPosJS;
	
	private JSpinner stretchJS;
	
	private JSpinner numberOfIterationsJS;
	
	private JRadioButton convolutionJRB;
	
	private JRadioButton minDistanceJRB;
	
	public ConvolutionSettingsPanel(UserBase userBase)
	{
		super();
		this.userBase = userBase;
		createInside();
	}
	
	private void settingsChanged()
	{
		UserAtomDefinition uad = userBase.getAtom(functionsJCB.getSelectedIndex());
		double[] preview = uad.getValues(uad.getOriginalLength() + getStretch());
		double scale = getScale();
		
		for (int i = 0; i < preview.length; i++)
		{
			preview[i] = preview[i] * scale;
		}
		
		function.setValues(preview);
		minPosJS.setValue(uad.getMinPosition());
		maxPosJS.setValue(uad.getMaxPosition());
		
		repaint();
	}
	
	private void createInside()
	{
		setLayout(new BorderLayout());
		JPanel functionJP = new JPanel(new FlowLayout(FlowLayout.LEFT));
		functionJP.setBorder(BorderFactory.createTitledBorder("Selected function"));
		
		DefaultComboBoxModel cbm = new DefaultComboBoxModel();
		for (UserAtomDefinition uad: userBase)
		{
			cbm.addElement(uad.getName());
		}
		
		functionsJCB = new JComboBox(cbm);
		functionsJCB.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				UserAtomDefinition uad = userBase.getAtom(functionsJCB.getSelectedIndex());
				function.setValues(uad.getValues(uad.getOriginalLength()));
			}
		});
		functionsJCB.setPreferredSize(new Dimension(200, 20));
		JLabel functionsJL = new JLabel("Function: ");
		functionsJL.setLabelFor(functionsJCB);
		
		functionJP.add(functionsJL);
		functionJP.add(functionsJCB);
		
		scaleJS = new JSpinner(new SpinnerNumberModel(1D, 0.1D, 10D, 0.1D));
		scaleJS.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent arg0)
			{
				settingsChanged();
			}
		});
		minPosJS = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
		maxPosJS = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
		stretchJS = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
		stretchJS.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent arg0)
			{
				settingsChanged();
			}
		});
		
		numberOfIterationsJS = new JSpinner(new SpinnerNumberModel(5, 1, 20, 1));
		
		JPanel methodJP = new JPanel(new GridLayout(1, 2));
		convolutionJRB = new JRadioButton("Correlation");
		minDistanceJRB = new JRadioButton("Min Distance");
		ButtonGroup bg = new ButtonGroup();
		bg.add(convolutionJRB);
		bg.add(minDistanceJRB);
		convolutionJRB.setSelected(true);
		methodJP.add(convolutionJRB);
		methodJP.add(minDistanceJRB);
		
		JLabel scaleJL= new JLabel("Scale: ");
		scaleJL.setLabelFor(scaleJS);
		JLabel minPosJL= new JLabel("Min position: ");
		minPosJL.setLabelFor(minPosJS);
		JLabel maxPosJL = new JLabel("Max position: ");
		maxPosJL.setLabelFor(maxPosJS);
		JLabel stretchJL = new JLabel("Stretch: ");
		stretchJL.setLabelFor(stretchJS);
		JLabel thresholdJL = new JLabel("Method: ");
		thresholdJL.setLabelFor(methodJP);
		JLabel numberOfIterationsJL = new JLabel("Number of MP iterations: ");
		numberOfIterationsJL.setLabelFor(numberOfIterationsJS);
		
		
		JPanel spinnerBackgroundJP = new JPanel(new GridLayout(6, 2));
		spinnerBackgroundJP.setBorder(BorderFactory.createTitledBorder("Selected function settings"));
		
		spinnerBackgroundJP.add(scaleJL);
		spinnerBackgroundJP.add(scaleJS);
		spinnerBackgroundJP.add(minPosJL);
		spinnerBackgroundJP.add(minPosJS);
		spinnerBackgroundJP.add(maxPosJL);
		spinnerBackgroundJP.add(maxPosJS);
		spinnerBackgroundJP.add(stretchJL);
		spinnerBackgroundJP.add(stretchJS);
		spinnerBackgroundJP.add(thresholdJL);
		spinnerBackgroundJP.add(methodJP);
		spinnerBackgroundJP.add(numberOfIterationsJL);
		spinnerBackgroundJP.add(numberOfIterationsJS);
		
		JPanel functionBackgroundJP = new JPanel(new GridLayout(1, 1));
		
		function = new SignalViewer2(-1, functionBackgroundJP, 0);
		functionBackgroundJP.add(function);
		
		JPanel settingsBackgroundJP = new JPanel(new GridLayout(1, 2));
		settingsBackgroundJP.add(spinnerBackgroundJP);
		settingsBackgroundJP.add(functionBackgroundJP);
		
		
		add(functionJP, BorderLayout.NORTH);
		add(settingsBackgroundJP, BorderLayout.CENTER);
		
		settingsChanged();
	}
	
	public int getSelectedAtomIndex()
	{
		return functionsJCB.getSelectedIndex();
	}
	
	public double getScale()
	{
		return ((Double) scaleJS.getValue()).doubleValue();
	}
	
	public int getMinPosition()
	{
		return ((Integer) minPosJS.getValue()).intValue() - Const.BEGIN_POSITION;
	}
	
	public int getMaxPosition()
	{
		return ((Integer) maxPosJS.getValue()).intValue() - Const.END_POSITION;
	}
	
	public int getStretch()
	{
		return ((Integer) stretchJS.getValue()).intValue();
	}
	
	public int getNumberOfIterations()
	{
		return ((Integer) numberOfIterationsJS.getValue()).intValue();
	}
	
	public int getMethod()
	{
		if (convolutionJRB.isSelected())
			return DetectionAlgorithm.CORELATION;
		else
			return DetectionAlgorithm.MIN_DISTANCE;
	}
}
