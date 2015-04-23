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

	private JSpinner subsampleSpinner;

	private JSpinner skipSpinner;

	private JSpinner waveletNameSpinner;

	private final SpinnerNumberModel epochSnm = new SpinnerNumberModel(512, 1,

	Const.POSTSTIMULUS_VALUES, 1);

	private final SpinnerNumberModel subsampleSnm = new SpinnerNumberModel(1,

	1, Const.POSTSTIMULUS_VALUES, 1);

	private final SpinnerNumberModel skipSnm = new SpinnerNumberModel(0, 0,

	Const.POSTSTIMULUS_VALUES, 1);

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
		// TODO this.setSize(Const.MAIN_WINDOW_WIDTH, Const.MAIN_WINDOW_HEIGHT);
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
			}
		});

		wtBttn = new JRadioButton("Wavelet Transform");
		wtBttn.setSelected(false);
		wtBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO disable other input fields
			}
		});

		mpBttn = new JRadioButton("Matching Pursuit");
		mpBttn.setSelected(false);
		mpBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO disable other input fields
			}
		});

		hhtBttn = new JRadioButton("Hilbert-Huang Transform");
		hhtBttn.setSelected(false);
		hhtBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO disable other input fields
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

		SpinnerNumberModel waveletSnm = new SpinnerNumberModel(0, 0, 17, 1);
		waveletNameSpinner = new JSpinner(waveletSnm);
		waveletNameSpinner.setEnabled(false);
		wtPane.add(waveletNameSpinner);

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

					mainFrame.setTrained(false);

					IFeatureExtraction fe = new HHTFeatureExtraction();

					c.dispose();

					ChangeClassifierFrame cc = new ChangeClassifierFrame(
							mainFrame, fe);
					cc.setVisible(true);

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
}
