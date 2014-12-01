package icp.gui.result;

import icp.algorithm.mp.Const;
import icp.algorithm.mp.DetectionAlgorithm;
import icp.algorithm.mp.UsersAtom;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextPane;


@SuppressWarnings("serial")
public class MPDetectionDetail extends JPanel
{
	private Dimension signalSize = new Dimension(600, 200);
	
	private SignalViewer2 original;
	
	private SignalViewer2 reconstruction;
	
	private SignalViewer2 convolution;
	
	private JTextPane convolutionInfoJTP;
	
	public MPDetectionDetail(DetectionAlgorithm ca, double[] originalData)
	{
		setLayout(new BorderLayout());
	
		JPanel signalBackgroundJP = new JPanel(new GridLayout(3, 1));
		
		JPanel originalJP = new JPanel(new GridLayout(1, 1));
		originalJP.setBorder(BorderFactory.createTitledBorder("Original signal"));
		original = new SignalViewer2(-1, originalJP, Const.BEGIN_POSITION);
		original.setValues(Arrays.copyOfRange(originalData, 256, 768));
		originalJP.add(original);
		
		
		JPanel reconstructionJP = new JPanel(new GridLayout(1, 1));
		reconstructionJP.setPreferredSize(signalSize);
		reconstructionJP.setBorder(BorderFactory.createTitledBorder("MP Reconstruction"));
		reconstruction = new SignalViewer2(-1, reconstructionJP, Const.BEGIN_POSITION);
		reconstruction.setValues(ca.getEpoch());
		reconstructionJP.add(reconstruction);
		JPanel convolutionJP = new JPanel(new GridLayout(1, 1));
		convolutionJP.setPreferredSize(signalSize);
		convolutionJP.setBorder(BorderFactory.createTitledBorder("Convolution result"));
		convolution = new SignalViewer2(-1, convolutionJP, Const.BEGIN_POSITION);
		convolution.setValues(ca.getMaxFunction());
		convolutionJP.add(convolution);
		
		reconstruction.getComunicationProvider().addObserver(convolution.getComunicationProvider());
		reconstruction.getComunicationProvider().addObserver(original.getComunicationProvider());
		convolution.getComunicationProvider().addObserver(reconstruction.getComunicationProvider());
		convolution.getComunicationProvider().addObserver(original.getComunicationProvider());
		original.getComunicationProvider().addObserver(convolution.getComunicationProvider());
		original.getComunicationProvider().addObserver(reconstruction.getComunicationProvider());
		
		UsersAtom ua = new UsersAtom();
		ua.setName("Best");
		ua.setPosition(ca.getMaxPosition());
		ua.setStretch(ca.getFunction().length);
		
		original.setAtom(ua);
		reconstruction.setAtom(ua);
		
		signalBackgroundJP.add(originalJP);
		signalBackgroundJP.add(reconstructionJP);
		signalBackgroundJP.add(convolutionJP);
		
		convolutionInfoJTP = new JTextPane();
		convolutionInfoJTP.setEditable(false);
		
		convolutionInfoJTP.setText("Max value: " + ca.getMaxEvaluation() + ", max position: " + (ca.getMaxPosition() + Const.BEGIN_POSITION));
		
		JPanel convolutionInfoJP = new JPanel(new FlowLayout(FlowLayout.LEFT));
		convolutionInfoJP.setBorder(BorderFactory.createTitledBorder("Convolution info"));
		convolutionInfoJP.add(convolutionInfoJTP);
		
		add(convolutionInfoJP, BorderLayout.NORTH);
		add(signalBackgroundJP, BorderLayout.CENTER);
		
	}
}
