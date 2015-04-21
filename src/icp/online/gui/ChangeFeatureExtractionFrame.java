package icp.online.gui;

import icp.Const;

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
	private JSpinner fasEpochSpinner;
	private JSpinner fasSubsampleSpinner;
	private JSpinner fasSkipSpinner;
	private JSpinner wtEpochSpinner;
	private JSpinner wtSubsampleSpinner;
	private JSpinner wtSkipSpinner;
	private JSpinner waveletNameSpinner;
	private JSpinner mpEpochSpinner;
	private JSpinner mpSubsampleSpinner;
	private JSpinner mpSkipSpinner;
	private JSpinner hhtEpochSpinner;
	private JSpinner hhtSubsampleSpinner;
	private JSpinner hhtSkipSpinner;
	private final SpinnerNumberModel epochSnm = new SpinnerNumberModel(1, 1,
			Const.POSTSTIMULUS_VALUES, 1);
	private final SpinnerNumberModel subsampleSnm = new SpinnerNumberModel(0,
			0, 512, 1);
	private final SpinnerNumberModel skipSnm = new SpinnerNumberModel(0, 0,
			512, 1);
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

	private void setEscListener(ChangeFeatureExtractionFrame frame) {
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
				fasEpochSpinner.setEnabled(true);
				fasSubsampleSpinner.setEnabled(true);
				fasSkipSpinner.setEnabled(true);
				wtEpochSpinner.setEnabled(false);
				wtSubsampleSpinner.setEnabled(false);
				wtSkipSpinner.setEnabled(false);
				waveletNameSpinner.setEnabled(false);
				mpEpochSpinner.setEnabled(false);
				mpSubsampleSpinner.setEnabled(false);
				mpSkipSpinner.setEnabled(false);
				hhtEpochSpinner.setEnabled(false);
				hhtSubsampleSpinner.setEnabled(false);
				hhtSkipSpinner.setEnabled(false);
			}
		});

		wtBttn = new JRadioButton("Wavelet Transform");
		wtBttn.setSelected(false);
		wtBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				fasEpochSpinner.setEnabled(false);
				fasSubsampleSpinner.setEnabled(false);
				fasSkipSpinner.setEnabled(false);
				wtEpochSpinner.setEnabled(true);
				wtSubsampleSpinner.setEnabled(true);
				wtSkipSpinner.setEnabled(true);
				waveletNameSpinner.setEnabled(true);
				mpEpochSpinner.setEnabled(false);
				mpSubsampleSpinner.setEnabled(false);
				mpSkipSpinner.setEnabled(false);
				hhtEpochSpinner.setEnabled(false);
				hhtSubsampleSpinner.setEnabled(false);
				hhtSkipSpinner.setEnabled(false);
			}
		});

		mpBttn = new JRadioButton("Matching Pursuit");
		mpBttn.setSelected(false);
		mpBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				fasEpochSpinner.setEnabled(false);
				fasSubsampleSpinner.setEnabled(false);
				fasSkipSpinner.setEnabled(false);
				wtEpochSpinner.setEnabled(false);
				wtSubsampleSpinner.setEnabled(false);
				wtSkipSpinner.setEnabled(false);
				waveletNameSpinner.setEnabled(false);
				mpEpochSpinner.setEnabled(true);
				mpSubsampleSpinner.setEnabled(true);
				mpSkipSpinner.setEnabled(true);
				hhtEpochSpinner.setEnabled(false);
				hhtSubsampleSpinner.setEnabled(false);
				hhtSkipSpinner.setEnabled(false);
			}
		});

		hhtBttn = new JRadioButton("Hilbert-Huang Transform");
		hhtBttn.setSelected(false);
		hhtBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				fasEpochSpinner.setEnabled(false);
				fasSubsampleSpinner.setEnabled(false);
				fasSkipSpinner.setEnabled(false);
				wtEpochSpinner.setEnabled(false);
				wtSubsampleSpinner.setEnabled(false);
				wtSkipSpinner.setEnabled(false);
				waveletNameSpinner.setEnabled(false);
				mpEpochSpinner.setEnabled(false);
				mpSubsampleSpinner.setEnabled(false);
				mpSkipSpinner.setEnabled(false);
				hhtEpochSpinner.setEnabled(true);
				hhtSubsampleSpinner.setEnabled(true);
				hhtSkipSpinner.setEnabled(true);
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

		// Filter and Subsample
		JPanel fasPane = createFasPane();

		// Wavelet Transform
		JPanel wtPane = createWtPane();

		// Matching Pursuit
		JPanel mpPane = createMpPane();

		// Hilbert-Huang Transform
		JPanel hhtPane = createHhtPane();

		JPanel bttnPane = createBttnPane();

		JPanel pane = new JPanel();
		pane.setBorder(BorderFactory.createTitledBorder("Parameters"));
		pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
		pane.add(fasPane);
		pane.add(wtPane);
		pane.add(mpPane);
		pane.add(hhtPane);
		pane.add(bttnPane);

		return pane;
	}

	private JPanel createFasPane() {
		JPanel fasPane = new JPanel();
		fasPane.setBorder(BorderFactory
				.createTitledBorder("Filter and Subsample"));
		fasPane.setLayout(new GridLayout(0, 2));

		JLabel fasEpochLabel = new JLabel("Epoch Size");
		fasPane.add(fasEpochLabel);
		fasEpochSpinner = new JSpinner(epochSnm);
		fasEpochSpinner.setEnabled(false);
		fasPane.add(fasEpochSpinner);

		JLabel fasSubsampleLable = new JLabel("Subsampling Factor");
		fasPane.add(fasSubsampleLable);
		fasSubsampleSpinner = new JSpinner(subsampleSnm);
		fasSubsampleSpinner.setEnabled(false);
		fasPane.add(fasSubsampleSpinner);

		JLabel fasSkipLabel = new JLabel("Skip Samples");
		fasPane.add(fasSkipLabel);
		fasSkipSpinner = new JSpinner(skipSnm);
		fasSkipSpinner.setEnabled(false);
		fasPane.add(fasSkipSpinner);
		return fasPane;
	}

	private JPanel createWtPane() {
		JPanel wtPane = new JPanel();
		wtPane.setBorder(BorderFactory.createTitledBorder("Wavelet Transform"));
		wtPane.setLayout(new GridLayout(0, 2));

		JLabel wtEpochLabel = new JLabel("Epoch Size");
		wtPane.add(wtEpochLabel);
		wtEpochSpinner = new JSpinner(epochSnm);
		wtEpochSpinner.setEnabled(false);
		wtPane.add(wtEpochSpinner);

		JLabel wtSubsampleLable = new JLabel("Subsampling Factor");
		wtPane.add(wtSubsampleLable);
		wtSubsampleSpinner = new JSpinner(subsampleSnm);
		wtSubsampleSpinner.setEnabled(false);
		wtPane.add(wtSubsampleSpinner);

		JLabel wtSkipLabel = new JLabel("Skip Samples");
		wtPane.add(wtSkipLabel);
		wtSkipSpinner = new JSpinner(skipSnm);
		wtSkipSpinner.setEnabled(false);
		wtPane.add(wtSkipSpinner);

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

		JLabel mpEpochLabel = new JLabel("Epoch Size");
		mpPane.add(mpEpochLabel);
		mpEpochSpinner = new JSpinner(epochSnm);
		mpEpochSpinner.setEnabled(false);
		mpPane.add(mpEpochSpinner);

		JLabel mpSubsampleLable = new JLabel("Subsampling Factor");
		mpPane.add(mpSubsampleLable);
		mpSubsampleSpinner = new JSpinner(subsampleSnm);
		mpSubsampleSpinner.setEnabled(false);
		mpPane.add(mpSubsampleSpinner);

		JLabel mpSkipLabel = new JLabel("Skip Samples");
		mpPane.add(mpSkipLabel);
		mpSkipSpinner = new JSpinner(skipSnm);
		mpSkipSpinner.setEnabled(false);
		mpPane.add(mpSkipSpinner);
		return mpPane;
	}

	private JPanel createHhtPane() {
		JPanel hhtPane = new JPanel();
		hhtPane.setBorder(BorderFactory
				.createTitledBorder("Hilbert-Huang Transform"));
		hhtPane.setLayout(new GridLayout(0, 2));

		JLabel hhtEpochLabel = new JLabel("Epoch Size");
		hhtPane.add(hhtEpochLabel);
		hhtEpochSpinner = new JSpinner(epochSnm);
		hhtEpochSpinner.setEnabled(false);
		hhtPane.add(hhtEpochSpinner);

		JLabel hhtSubsampleLable = new JLabel("Subsampling Factor");
		hhtPane.add(hhtSubsampleLable);
		hhtSubsampleSpinner = new JSpinner(subsampleSnm);
		hhtSubsampleSpinner.setEnabled(false);
		hhtPane.add(hhtSubsampleSpinner);

		JLabel hhtSkipLabel = new JLabel("Skip Samples");
		hhtPane.add(hhtSkipLabel);
		hhtSkipSpinner = new JSpinner(skipSnm);
		hhtSkipSpinner.setEnabled(false);
		hhtPane.add(hhtSkipSpinner);
		return hhtPane;
	}

	private JPanel createBttnPane() {
		JPanel bttnPane = new JPanel();
		JButton trainBttn = new JButton("Train");
		trainBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("training");
			}
		});
		JButton okBttn = new JButton("OK");
		ChangeFeatureExtractionFrame c = this;
		okBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO how to create the classifier with its parameters
				if(fasBttn.isSelected()) {
					mainFrame.setTrained(false);
					
				} else if(wtBttn.isSelected()) {
					mainFrame.setTrained(false);
					
				} else if(mpBttn.isSelected()) {
					mainFrame.setTrained(false);
					
				} else if(hhtBttn.isSelected()) {
					mainFrame.setTrained(false);
					
				}
				
				if (mainFrame.isTrained() == false) {
					int dialogResult = JOptionPane
							.showConfirmDialog(
									null,
									"You have to train the classifier in order to use it\nWould you like to train it now?",
									"Classifier is not trained",
									JOptionPane.YES_NO_OPTION);
					if (dialogResult == JOptionPane.YES_OPTION) {
						c.dispose();
						trainBttn.doClick();
						mainFrame.setTrained(true);
					} else {
						c.dispose();
					}
				}
			}
		});
		bttnPane.setLayout(new BoxLayout(bttnPane, BoxLayout.LINE_AXIS));
		bttnPane.add(Box.createHorizontalGlue());
		bttnPane.add(trainBttn);
		bttnPane.add(okBttn);
		return bttnPane;
	}
}
