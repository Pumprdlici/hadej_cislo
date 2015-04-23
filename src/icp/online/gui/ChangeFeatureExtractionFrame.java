package icp.online.gui;

import icp.Const;
import icp.application.classification.FilterAndSubsamplingFeatureExtraction;
import icp.application.classification.HHTFeatureExtraction;
import icp.application.classification.IFeatureExtraction;
import icp.application.classification.MatchingPursuitFeatureExtraction;
import icp.application.classification.WaveletTransformFeatureExtraction;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;

@SuppressWarnings("serial")
public class ChangeFeatureExtractionFrame extends JFrame {

	private MainFrame mainFrame;

	private JSpinner epochSpinner;

	private final SpinnerNumberModel epochSnm = new SpinnerNumberModel(512, 1,
			Const.POSTSTIMULUS_VALUES, 1);

	private JSpinner subsampleSpinner;

	private final SpinnerNumberModel subsampleSnm = new SpinnerNumberModel(1,
			1, Const.POSTSTIMULUS_VALUES, 1);

	private JSpinner skipSpinner;

	private final SpinnerNumberModel skipSnm = new SpinnerNumberModel(0, 0,
			Const.POSTSTIMULUS_VALUES, 1);

	private JComboBox<String> waveletNameComboBox;

	private String[] waveletNames = { "Coiflet 6", "Coiflet 12", "Coiflet 18",
			"Coiflet 24", "Coiflet 30", "Daubechies 4", "Daubechies 6",
			"Daubechies 8", "Daubechies 10", "Daubechies 12", "Daubechies 14",
			"Daubechies 16", "Daubechies 18", "Daubechies 20", "Haar",
			"Symmlet 4", "Symmlet 6", "Symmlet 8" };

	private JSpinner hhtMinSample;

	private JSpinner hhtMaxSample;

	private JSpinner hhtSampleWindowSize;

	private JSpinner hhtSampleWindowShift;

	private JSpinner hhtAmplitudeThreshold;

	private JSpinner hhtMinFreq;

	private JSpinner hhtMaxFreq;

	private JComboBox<String> hhtTypeOfFeatures;

	private String[] hhtFeatureTypes = { "Frequencies", "Amplitudes" };

	private JRadioButton fasBttn;

	private JRadioButton wtBttn;

	private JRadioButton mpBttn;

	private JRadioButton hhtBttn;

	public ChangeFeatureExtractionFrame(MainFrame mainFrame) {
		super("Choose Feature Extractor and Its Parameters");
		this.mainFrame = mainFrame;
		this.getContentPane().add(createClassifierFrame());
		this.setVisible(false);
		this.pack();

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(900, 600);
		this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height
				/ 2 - this.getSize().height / 2);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setEscListener(this);
	}

	private void setEscListener(final ChangeFeatureExtractionFrame frame) {
		ActionListener escListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		};

		this.getRootPane().registerKeyboardAction(escListener,
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
	}

	private JPanel createClassifierFrame() {
		GridLayout mainLayout = new GridLayout(1, 2);
		JPanel contentJP = new JPanel(mainLayout);
		contentJP.add(createRadioBttns());
		contentJP.add(createParameters());

		return contentJP;
	}

	private JPanel createRadioBttns() {
		fasBttn = new JRadioButton("Filter and Subsample");
		fasBttn.setSelected(false);
		fasBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO disable other input fields
				subsampleSpinner.setEnabled(true);
				waveletNameComboBox.setEnabled(false);
				hhtMinSample.setEnabled(false);
				hhtMaxSample.setEnabled(false);
				hhtSampleWindowSize.setEnabled(false);
				hhtSampleWindowShift.setEnabled(false);
				hhtAmplitudeThreshold.setEnabled(false);
				hhtMinFreq.setEnabled(false);
				hhtMaxFreq.setEnabled(false);
				hhtTypeOfFeatures.setEnabled(false);
			}
		});

		wtBttn = new JRadioButton("Wavelet Transform");
		wtBttn.setSelected(false);
		wtBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO disable other input fields
				subsampleSpinner.setEnabled(false);
				waveletNameComboBox.setEnabled(true);
				hhtMinSample.setEnabled(false);
				hhtMaxSample.setEnabled(false);
				hhtSampleWindowSize.setEnabled(false);
				hhtSampleWindowShift.setEnabled(false);
				hhtAmplitudeThreshold.setEnabled(false);
				hhtMinFreq.setEnabled(false);
				hhtMaxFreq.setEnabled(false);
				hhtTypeOfFeatures.setEnabled(false);
			}
		});

		mpBttn = new JRadioButton("Matching Pursuit");
		mpBttn.setSelected(false);
		mpBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO disable other input fields
				subsampleSpinner.setEnabled(true);
				waveletNameComboBox.setEnabled(false);
				hhtMinSample.setEnabled(false);
				hhtMaxSample.setEnabled(false);
				hhtSampleWindowSize.setEnabled(false);
				hhtSampleWindowShift.setEnabled(false);
				hhtAmplitudeThreshold.setEnabled(false);
				hhtMinFreq.setEnabled(false);
				hhtMaxFreq.setEnabled(false);
				hhtTypeOfFeatures.setEnabled(false);
			}
		});

		hhtBttn = new JRadioButton("Hilbert-Huang Transform");
		hhtBttn.setSelected(false);
		hhtBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO disable other input fields
				subsampleSpinner.setEnabled(true);
				waveletNameComboBox.setEnabled(false);
				hhtMinSample.setEnabled(true);
				hhtMaxSample.setEnabled(true);
				hhtSampleWindowSize.setEnabled(true);
				hhtSampleWindowShift.setEnabled(true);
				hhtAmplitudeThreshold.setEnabled(true);
				hhtMinFreq.setEnabled(true);
				hhtMaxFreq.setEnabled(true);
				hhtTypeOfFeatures.setEnabled(true);
			}
		});

		ButtonGroup group = new ButtonGroup();
		group.add(fasBttn);
		group.add(wtBttn);
		group.add(mpBttn);
		group.add(hhtBttn);

		JPanel pane = new JPanel();
		pane.setBorder(BorderFactory.createTitledBorder("Feature Extraction"));
		pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
		pane.add(fasBttn);
		pane.add(wtBttn);
		pane.add(mpBttn);
		pane.add(hhtBttn);

		return pane;
	}

	private JPanel createParameters() {
		JPanel allPane = createAllPane();

		// Filter and Subsample
		JPanel fasPane = createFasPane();

		// Wavelet Transform
		JPanel wtPane = createWtPane();

		// Matching Pursuit
		JPanel mpPane = createMpPane();

		// Hilbert-Huang Transform
		JPanel hhtPane = createHhtPane();

		// Buttons
		JPanel bttnPane = createBttnPane();

		JPanel pane = new JPanel();
		pane.setBorder(BorderFactory.createTitledBorder("Parameters"));
		pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
		pane.add(allPane);
		pane.add(fasPane);
		pane.add(wtPane);
		pane.add(mpPane);
		pane.add(hhtPane);
		pane.add(bttnPane);

		return pane;
	}

	private JPanel createAllPane() {
		JPanel allPane = new JPanel();
		allPane.setBorder(BorderFactory
				.createTitledBorder("Parameters applicable to all Feature Extraction methods"));
		allPane.setLayout(new GridLayout(0, 2));

		JLabel allEpochLabel = new JLabel("Epoch Size");
		allPane.add(allEpochLabel);

		epochSpinner = new JSpinner(epochSnm);
		epochSpinner.setEnabled(true);
		allPane.add(epochSpinner);

		JLabel allSubsampleLable = new JLabel("Subsampling Factor");
		allPane.add(allSubsampleLable);

		subsampleSpinner = new JSpinner(subsampleSnm);
		subsampleSpinner.setEnabled(true);
		allPane.add(subsampleSpinner);

		JLabel allSkipLabel = new JLabel("Skip Samples");
		allPane.add(allSkipLabel);

		skipSpinner = new JSpinner(skipSnm);
		skipSpinner.setEnabled(true);
		allPane.add(skipSpinner);

		return allPane;
	}

	private JPanel createFasPane() {
		JPanel fasPane = new JPanel();
		fasPane.setBorder(BorderFactory
				.createTitledBorder("Filter and Subsample"));
		fasPane.setLayout(new GridLayout(0, 2));

		JLabel fasLabel = new JLabel("No more parameters to set");
		fasPane.add(fasLabel);

		return fasPane;
	}

	private JPanel createWtPane() {
		JPanel wtPane = new JPanel();
		wtPane.setBorder(BorderFactory.createTitledBorder("Wavelet Transform"));
		wtPane.setLayout(new GridLayout(0, 2));

		JLabel waveletNameLabel = new JLabel("Wavelet Name");
		wtPane.add(waveletNameLabel);

		waveletNameComboBox = new JComboBox<String>(waveletNames);
		waveletNameComboBox.setSelectedIndex(8);
		waveletNameComboBox.setEnabled(false);
		wtPane.add(waveletNameComboBox);

		return wtPane;
	}

	private JPanel createMpPane() {

		JPanel mpPane = new JPanel();
		mpPane.setBorder(BorderFactory.createTitledBorder("Matching Pursuit"));
		mpPane.setLayout(new GridLayout(0, 2));

		return mpPane;
	}

	private JPanel createHhtPane() {
		JPanel hhtPane = new JPanel();
		hhtPane.setBorder(BorderFactory
				.createTitledBorder("Hilbert-Huang Transform"));
		hhtPane.setLayout(new GridLayout(0, 2));

		JLabel minSampleLabel = new JLabel("Min Sample");
		hhtPane.add(minSampleLabel);

		SpinnerNumberModel minSampleSnm = new SpinnerNumberModel(0, 0,
				Const.POSTSTIMULUS_VALUES, 1);
		hhtMinSample = new JSpinner(minSampleSnm);
		hhtMinSample.setEnabled(false);
		hhtPane.add(hhtMinSample);

		JLabel maxSampleLabel = new JLabel("Max Sample");
		hhtPane.add(maxSampleLabel);

		SpinnerNumberModel maxSampleSnm = new SpinnerNumberModel(0, 0,
				Const.POSTSTIMULUS_VALUES, 1);
		hhtMaxSample = new JSpinner(maxSampleSnm);
		hhtMaxSample.setEnabled(false);
		hhtPane.add(hhtMaxSample);

		JLabel sampleWindowsSizeLabel = new JLabel("Sample Window Size");
		hhtPane.add(sampleWindowsSizeLabel);

		SpinnerNumberModel sampleWindowSizeSnn = new SpinnerNumberModel(1, 1,
				Const.POSTSTIMULUS_VALUES, 1);
		hhtSampleWindowSize = new JSpinner(sampleWindowSizeSnn);
		hhtSampleWindowSize.setEnabled(false);
		hhtPane.add(hhtSampleWindowSize);

		JLabel sampleWindowsShiftLabel = new JLabel("Sample Window Shift");
		hhtPane.add(sampleWindowsShiftLabel);

		SpinnerNumberModel sampleWindowShiftSnn = new SpinnerNumberModel(1, 1,
				Const.POSTSTIMULUS_VALUES - 1, 1);
		hhtSampleWindowShift = new JSpinner(sampleWindowShiftSnn);
		hhtSampleWindowShift.setEnabled(false);
		hhtPane.add(hhtSampleWindowShift);

		JLabel amplitudeThresholdLabel = new JLabel("Amplitude Threshold");
		hhtPane.add(amplitudeThresholdLabel);

		SpinnerNumberModel amplitudeThresholdSnn = new SpinnerNumberModel(0, 0,
				Const.POSTSTIMULUS_VALUES, 1);
		hhtAmplitudeThreshold = new JSpinner(amplitudeThresholdSnn);
		hhtAmplitudeThreshold.setEnabled(false);
		hhtPane.add(hhtAmplitudeThreshold);

		JLabel minFreqLabel = new JLabel("Min Frequency");
		hhtPane.add(minFreqLabel);

		SpinnerNumberModel minFreqSnn = new SpinnerNumberModel(1, 1,
				Const.POSTSTIMULUS_VALUES, 1);
		hhtMinFreq = new JSpinner(minFreqSnn);
		hhtMinFreq.setEnabled(false);
		hhtPane.add(hhtMinFreq);

		JLabel maxFreqLabel = new JLabel("Max Frequency");
		hhtPane.add(maxFreqLabel);

		SpinnerNumberModel maxFreqSnn = new SpinnerNumberModel(1, 1,
				Const.POSTSTIMULUS_VALUES, 1);
		hhtMaxFreq = new JSpinner(maxFreqSnn);
		hhtMaxFreq.setEnabled(false);
		hhtPane.add(hhtMaxFreq);

		JLabel typeOfFeatures = new JLabel("Type of Features");
		hhtPane.add(typeOfFeatures);

		hhtTypeOfFeatures = new JComboBox<String>(hhtFeatureTypes);
		hhtTypeOfFeatures.setSelectedIndex(0);
		hhtTypeOfFeatures.setEnabled(false);
		hhtPane.add(hhtTypeOfFeatures);

		return hhtPane;
	}

	private JPanel createBttnPane() {
		JPanel bttnPane = new JPanel();
		JButton okBttn = new JButton("Next");
		final ChangeFeatureExtractionFrame c = this;
		okBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// TODO set parameters to extractors

				if (fasBttn.isSelected()) {

					mainFrame.setTrained(false);

					IFeatureExtraction fe = new FilterAndSubsamplingFeatureExtraction();

					c.dispose();

					ChangeClassifierFrame cc = new ChangeClassifierFrame(
							mainFrame, fe);
					cc.setVisible(true);

				} else if (wtBttn.isSelected()) {

					mainFrame.setTrained(false);

					IFeatureExtraction fe = new WaveletTransformFeatureExtraction();

					c.dispose();

					ChangeClassifierFrame cc = new ChangeClassifierFrame(
							mainFrame, fe);
					cc.setVisible(true);

				} else if (mpBttn.isSelected()) {

					mainFrame.setTrained(false);

					IFeatureExtraction fe = new MatchingPursuitFeatureExtraction();

					c.dispose();

					ChangeClassifierFrame cc = new ChangeClassifierFrame(
							mainFrame, fe);
					cc.setVisible(true);

				} else if (hhtBttn.isSelected()) {
					if (hhtConditions() == true) {
						mainFrame.setTrained(false);

						IFeatureExtraction fe = new HHTFeatureExtraction();
						((HHTFeatureExtraction) fe)
								.setMinSample((int) hhtMinSample.getValue());
						((HHTFeatureExtraction) fe)
								.setMaxSample((int) hhtMaxSample.getValue());
						((HHTFeatureExtraction) fe)
								.setSampleWindowSize((int) hhtSampleWindowSize
										.getValue());
						((HHTFeatureExtraction) fe)
								.setSampleWindowShift((int) hhtSampleWindowShift
										.getValue());
						((HHTFeatureExtraction) fe)
								.setAmplitudeThreshold((int) hhtAmplitudeThreshold
										.getValue());
						((HHTFeatureExtraction) fe).setMinFreq((int) hhtMinFreq
								.getValue());
						((HHTFeatureExtraction) fe).setMaxFreq((int) hhtMaxFreq
								.getValue());
						((HHTFeatureExtraction) fe)
								.setTypeOfFeatures(hhtTypeOfFeatures
										.getSelectedIndex() + 1);

						c.dispose();

						ChangeClassifierFrame cc = new ChangeClassifierFrame(
								mainFrame, fe);

						cc.setVisible(true);
					}
				} else {
					JOptionPane
							.showMessageDialog(null,
									"Choose one Feature Extraction method and fill in its parameters");
				}
			}
		});

		bttnPane.setLayout(new BoxLayout(bttnPane, BoxLayout.LINE_AXIS));
		bttnPane.add(Box.createHorizontalGlue());
		bttnPane.add(okBttn);

		return bttnPane;
	}

	private boolean hhtConditions() {
		if (((int) hhtMinSample.getValue()) >= ((int) hhtMaxSample.getValue())) {
			JOptionPane.showMessageDialog(null,
					"Min Sample must be < Max Sample");
			return false;
		}
		if (((int) hhtSampleWindowShift.getValue()) > ((int) hhtSampleWindowSize
				.getValue())) {
			JOptionPane.showMessageDialog(null,
					"Sample Window Shift must be <= Sample Window Size");
			return false;
		}
		if (((int) hhtMinFreq.getValue()) > ((int) hhtMaxFreq.getValue())) {
			JOptionPane.showMessageDialog(null,
					"Min Frequency must be <= Max Frequency");
			return false;
		}
		if ((((int) hhtMaxSample.getValue()) - ((int) hhtMinSample.getValue())) > ((int) epochSpinner
				.getValue())) {
			JOptionPane.showMessageDialog(null,
					"(Max Sample - Min Sample) must be <= Epoch Size");
			return false;
		}
		if (((int) hhtSampleWindowSize.getValue()) > (((int) hhtMaxSample
				.getValue()) - ((int) hhtMinSample.getValue()))) {
			JOptionPane.showMessageDialog(null,
					"Sample Window Size must be <= (Max Sample - Min Sample)");
			return false;
		}
		if (((int) hhtMinSample.getValue()) < ((int) skipSpinner.getValue())) {
			JOptionPane.showMessageDialog(null,
					"Min Sample must be >= Skip Samples");
			return false;
		}

		return true;
	}
}
