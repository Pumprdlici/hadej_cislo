package icp.online.gui;

import icp.Const;

import java.awt.Component;
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
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;

@SuppressWarnings("serial")
public class ChangeFeatureExtractionFrame extends JFrame {

	private MainFrame mainFrame;
	private JPanel parameters;

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
		parameters = createParameters();
		contentJP.add(parameters);

		return contentJP;
	}

	private JPanel createRadioBttns() {
		JRadioButton fasBttn = new JRadioButton("Filter and Subsample");
		fasBttn.setSelected(false);
		fasBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO set extractor
				Component cmp[] = parameters.getComponents();
				for (Component c : cmp) {
					if (c.getClass().equals(JPanel.class)) {
						Component[] newCmp = ((JPanel) c).getComponents();
						for (Component newC : newCmp) {
							if (newC.getName() != null
									&& newC.getName().contains("fas")) {
								newC.setEnabled(true);
							} else if (newC.getClass().equals(JTextField.class)
									|| newC.getClass().equals(JSpinner.class)) {
								newC.setEnabled(false);
							}
						}
					} else {
						if (c.getName() != null
								&& c.getName().contains("fas")) {
							c.setEnabled(true);
						} else if (c.getClass().equals(JTextField.class)
								|| c.getClass().equals(JSpinner.class)) {
							c.setEnabled(false);
						}
					}
				}
			}
		});

		JRadioButton wtBttn = new JRadioButton("Wavelet Transform");
		wtBttn.setSelected(false);
		wtBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO set extractor
				Component cmp[] = parameters.getComponents();
				for (Component c : cmp) {
					if (c.getClass().equals(JPanel.class)) {
						Component[] newCmp = ((JPanel) c).getComponents();
						for (Component newC : newCmp) {
							if (newC.getName() != null
									&& newC.getName().contains("wt")) {
								newC.setEnabled(true);
							} else if (newC.getClass().equals(JTextField.class)
									|| newC.getClass().equals(JSpinner.class)) {
								newC.setEnabled(false);
							}
						}
					} else {
						if (c.getName() != null
								&& c.getName().contains("wt")) {
							c.setEnabled(true);
						} else if (c.getClass().equals(JTextField.class)
								|| c.getClass().equals(JSpinner.class)) {
							c.setEnabled(false);
						}
					}
				}
			}
		});

		JRadioButton mpBttn = new JRadioButton("Matching Pursuit");
		mpBttn.setSelected(false);
		mpBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO set extractor
				Component cmp[] = parameters.getComponents();
				for (Component c : cmp) {
					if (c.getClass().equals(JPanel.class)) {
						Component[] newCmp = ((JPanel) c).getComponents();
						for (Component newC : newCmp) {
							if (newC.getName() != null
									&& newC.getName().contains("mp")) {
								newC.setEnabled(true);
							} else if (newC.getClass().equals(JTextField.class)
									|| newC.getClass().equals(JSpinner.class)) {
								newC.setEnabled(false);
							}
						}
					} else {
						if (c.getName() != null
								&& c.getName().contains("mp")) {
							c.setEnabled(true);
						} else if (c.getClass().equals(JTextField.class)
								|| c.getClass().equals(JSpinner.class)) {
							c.setEnabled(false);
						}
					}
				}
			}
		});

		JRadioButton hhtBttn = new JRadioButton("Hilbert-Huang Transform");
		hhtBttn.setSelected(false);
		hhtBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO set extractor
				Component cmp[] = parameters.getComponents();
				for (Component c : cmp) {
					if (c.getClass().equals(JPanel.class)) {
						Component[] newCmp = ((JPanel) c).getComponents();
						for (Component newC : newCmp) {
							if (newC.getName() != null
									&& newC.getName().contains("hht")) {
								newC.setEnabled(true);
							} else if (newC.getClass().equals(JTextField.class)
									|| newC.getClass().equals(JSpinner.class)) {
								newC.setEnabled(false);
							}
						}
					} else {
						if (c.getName() != null
								&& c.getName().contains("hht")) {
							c.setEnabled(true);
						} else if (c.getClass().equals(JTextField.class)
								|| c.getClass().equals(JSpinner.class)) {
							c.setEnabled(false);
						}
					}
				}
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
		SpinnerNumberModel epochSnm = new SpinnerNumberModel(1, 1, Const.POSTSTIMULUS_VALUES, 1);
		SpinnerNumberModel subsampleSnm = new SpinnerNumberModel(0, 0, 512, 1);
		SpinnerNumberModel skipSnm = new SpinnerNumberModel(0, 0, 512, 1);
		
		// Filter and Subsample
		JPanel fasPane = new JPanel();
		fasPane.setBorder(BorderFactory
				.createTitledBorder("Filter and Subsample"));
		fasPane.setLayout(new GridLayout(0, 2));
		
		JLabel fasEpochLabel = new JLabel("Epoch Size");
		fasPane.add(fasEpochLabel);
		JSpinner fasEpochSpinner = new JSpinner(epochSnm);
		fasEpochSpinner.setName("fasParameter");
		fasEpochSpinner.setEnabled(false);
		fasPane.add(fasEpochSpinner);
		
		JLabel fasSubsampleLable = new JLabel("Subsampling Factor");
		fasPane.add(fasSubsampleLable);
		JSpinner fasSubsampleSpinner = new JSpinner(subsampleSnm);
		fasSubsampleSpinner.setName("fasParameter");
		fasSubsampleSpinner.setEnabled(false);
		fasPane.add(fasSubsampleSpinner);
		
		JLabel fasSkipLabel = new JLabel("Skip Samples");
		fasPane.add(fasSkipLabel);
		JSpinner fasSkipSpinner = new JSpinner(skipSnm);
		fasSkipSpinner.setName("fasParameter");
		fasSkipSpinner.setEnabled(false);
		fasPane.add(fasSkipSpinner);

		// Wavelet Transform
		JPanel wtPane = new JPanel();
		wtPane.setBorder(BorderFactory.createTitledBorder("Wavelet Transform"));
		wtPane.setLayout(new GridLayout(0, 2));
		
		JLabel wtEpochLabel = new JLabel("Epoch Size");
		wtPane.add(wtEpochLabel);
		JSpinner wtEpochSpinner = new JSpinner(epochSnm);
		wtEpochSpinner.setName("wtParameter");
		wtEpochSpinner.setEnabled(false);
		wtPane.add(wtEpochSpinner);
		
		JLabel wtSubsampleLable = new JLabel("Subsampling Factor");
		wtPane.add(wtSubsampleLable);
		JSpinner wtSubsampleSpinner = new JSpinner(subsampleSnm);
		wtSubsampleSpinner.setName("wtParameter");
		wtSubsampleSpinner.setEnabled(false);
		wtPane.add(wtSubsampleSpinner);
		
		JLabel wtSkipLabel = new JLabel("Skip Samples");
		wtPane.add(wtSkipLabel);
		JSpinner wtSkipSpinner = new JSpinner(skipSnm);
		wtSkipSpinner.setName("wtParameter");
		wtSkipSpinner.setEnabled(false);
		wtPane.add(wtSkipSpinner);
		
		JLabel waveletNameLabel = new JLabel("Wavelet Name");
		wtPane.add(waveletNameLabel);
		SpinnerNumberModel waveletSnm = new SpinnerNumberModel(0, 0, 17, 1);
		JSpinner waveletNameSpinner = new JSpinner(waveletSnm);
		waveletNameSpinner.setName("wtParameter");
		waveletNameSpinner.setEnabled(false);
		wtPane.add(waveletNameSpinner);

		// Matching Pursuit
		JPanel mpPane = new JPanel();
		mpPane.setBorder(BorderFactory.createTitledBorder("Matching Pursuit"));
		mpPane.setLayout(new GridLayout(0, 2));
		
		JLabel mpEpochLabel = new JLabel("Epoch Size");
		mpPane.add(mpEpochLabel);
		JSpinner mpEpochSpinner = new JSpinner(epochSnm);
		mpEpochSpinner.setName("mpParameter");
		mpEpochSpinner.setEnabled(false);
		mpPane.add(mpEpochSpinner);
		
		JLabel mpSubsampleLable = new JLabel("Subsampling Factor");
		mpPane.add(mpSubsampleLable);
		JSpinner mpSubsampleSpinner = new JSpinner(subsampleSnm);
		mpSubsampleSpinner.setName("mpParameter");
		mpSubsampleSpinner.setEnabled(false);
		mpPane.add(mpSubsampleSpinner);
		
		JLabel mpSkipLabel = new JLabel("Skip Samples");
		mpPane.add(mpSkipLabel);
		JSpinner mpSkipSpinner = new JSpinner(skipSnm);
		mpSkipSpinner.setName("mpParameter");
		mpSkipSpinner.setEnabled(false);
		mpPane.add(mpSkipSpinner);

		// Hilbert-Huang Transform
		JPanel hhtPane = new JPanel();
		hhtPane.setBorder(BorderFactory
				.createTitledBorder("Hilbert-Huang Transform"));
		hhtPane.setLayout(new GridLayout(0, 2));
		
		JLabel hhtEpochLabel = new JLabel("Epoch Size");
		hhtPane.add(hhtEpochLabel);
		JSpinner hhtEpochSpinner = new JSpinner(epochSnm);
		hhtEpochSpinner.setName("hhtParameter");
		hhtEpochSpinner.setEnabled(false);
		hhtPane.add(hhtEpochSpinner);
		
		JLabel hhtSubsampleLable = new JLabel("Subsampling Factor");
		hhtPane.add(hhtSubsampleLable);
		JSpinner hhtSubsampleSpinner = new JSpinner(subsampleSnm);
		hhtSubsampleSpinner.setName("hhtParameter");
		hhtSubsampleSpinner.setEnabled(false);
		hhtPane.add(hhtSubsampleSpinner);
		
		JLabel hhtSkipLabel = new JLabel("Skip Samples");
		hhtPane.add(hhtSkipLabel);
		JSpinner hhtSkipSpinner = new JSpinner(skipSnm);
		hhtSkipSpinner.setName("hhtParameter");
		hhtSkipSpinner.setEnabled(false);
		hhtPane.add(hhtSkipSpinner);

		JButton okBttn = new JButton("OK");

		JPanel bttnPane = new JPanel();
		bttnPane.setLayout(new BoxLayout(bttnPane, BoxLayout.LINE_AXIS));
		bttnPane.add(Box.createHorizontalGlue());
		bttnPane.add(okBttn);

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
}
