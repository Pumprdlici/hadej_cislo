package icp.online.gui;

import icp.application.classification.CorrelationClassifier;
import icp.application.classification.IERPClassifier;
import icp.application.classification.IFeatureExtraction;
import icp.application.classification.KNNClassifier;
import icp.application.classification.LinearDiscriminantAnalysisClassifier;
import icp.application.classification.MLPClassifier;
import icp.application.classification.SVMClassifier;
import icp.application.classification.test.TrainUsingOfflineProvider;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

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
public class ChangeClassifierFrame extends JFrame {

	private MainFrame mainFrame;

	private IFeatureExtraction fe;

	private JSpinner middleNeuronsSpinner;

	private JSpinner neighborsNumberSpinner;

	private JSpinner svmGamma;

	private JRadioButton mlpBttn;

	private JRadioButton knnBttn;

	private JRadioButton ldaBttn;

	private JRadioButton svmBttn;

	private JRadioButton correlationBttn;

	public ChangeClassifierFrame(MainFrame mainFrame, IFeatureExtraction fe) {
		super("Choose Classifier and its Parameters");
		this.mainFrame = mainFrame;
		this.fe = fe;
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

	private void setEscListener(final ChangeClassifierFrame frame) {
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
		mlpBttn = new JRadioButton("MLP");
		mlpBttn.setSelected(false);
		mlpBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				middleNeuronsSpinner.setEnabled(true);
				neighborsNumberSpinner.setEnabled(false);
				svmGamma.setEnabled(false);
			}
		});

		knnBttn = new JRadioButton("K Nearest Neighbors");
		knnBttn.setSelected(false);
		knnBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				middleNeuronsSpinner.setEnabled(false);
				neighborsNumberSpinner.setEnabled(true);
				svmGamma.setEnabled(false);
			}
		});

		ldaBttn = new JRadioButton("Linear Discriminant Analysis");
		ldaBttn.setSelected(false);
		ldaBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				middleNeuronsSpinner.setEnabled(false);
				neighborsNumberSpinner.setEnabled(false);
				svmGamma.setEnabled(false);
			}
		});

		svmBttn = new JRadioButton("Support Vector Machines");
		svmBttn.setSelected(false);
		svmBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				middleNeuronsSpinner.setEnabled(false);
				neighborsNumberSpinner.setEnabled(false);
				svmGamma.setEnabled(true);
			}
		});

		correlationBttn = new JRadioButton("Correlation");
		correlationBttn.setSelected(false);
		correlationBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				middleNeuronsSpinner.setEnabled(false);
				neighborsNumberSpinner.setEnabled(false);
				svmGamma.setEnabled(false);
			}
		});

		ButtonGroup group = new ButtonGroup();
		group.add(mlpBttn);
		group.add(knnBttn);
		group.add(ldaBttn);
		group.add(svmBttn);
		group.add(correlationBttn);

		JPanel pane = new JPanel();
		pane.setBorder(BorderFactory.createTitledBorder("Classifier"));
		pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
		pane.add(mlpBttn);
		pane.add(knnBttn);
		pane.add(ldaBttn);
		pane.add(svmBttn);
		pane.add(correlationBttn);

		return pane;
	}

	private JPanel createParameters() {
		// MLP
		JPanel mlpPane = createMlpPane();

		// KNN
		JPanel knnPane = createKnnPane();

		// LDA
		JPanel ldaPane = createLdaPane();

		// SVM
		JPanel svmPane = createSvmPane();

		// Correlation
		JPanel correlationPane = createCorrelationPane();

		// Buttons
		JPanel bttnPane = createBttnPane();

		JPanel pane = new JPanel();
		pane.setBorder(BorderFactory.createTitledBorder("Parameters"));
		pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
		pane.add(mlpPane);
		pane.add(knnPane);
		pane.add(ldaPane);
		pane.add(svmPane);
		pane.add(correlationPane);
		pane.add(bttnPane);

		return pane;
	}

	private JPanel createMlpPane() {
		JPanel mlpPane = new JPanel();
		mlpPane.setBorder(BorderFactory.createTitledBorder("MLP"));
		mlpPane.setLayout(new GridLayout(0, 2));

		JLabel middleNeuronsLabel = new JLabel("Number of Middle Neurons");
		mlpPane.add(middleNeuronsLabel);

		SpinnerNumberModel middleNeuronsSnm = new SpinnerNumberModel(8, 1, 750,
				1);
		middleNeuronsSpinner = new JSpinner(middleNeuronsSnm);
		middleNeuronsSpinner.setEnabled(false);
		mlpPane.add(middleNeuronsSpinner);

		return mlpPane;
	}

	private JPanel createKnnPane() {
		JPanel knnPane = new JPanel();
		knnPane.setBorder(BorderFactory
				.createTitledBorder("K Nearest Neighbors"));
		knnPane.setLayout(new GridLayout(0, 2));

		JLabel neighborsNumberLabel = new JLabel("Number of Neighbors");
		knnPane.add(neighborsNumberLabel);

		SpinnerNumberModel neighborsNumberSnm = new SpinnerNumberModel(1, 1,
				750, 1);
		neighborsNumberSpinner = new JSpinner(neighborsNumberSnm);
		neighborsNumberSpinner.setEnabled(false);
		knnPane.add(neighborsNumberSpinner);

		return knnPane;
	}

	private JPanel createLdaPane() {
		JPanel ldaPane = new JPanel();
		ldaPane.setBorder(BorderFactory
				.createTitledBorder("Linear Discriminant Analysis"));

		ldaPane.add(new JLabel("No parameters for this classifier"));

		return ldaPane;
	}

	private JPanel createSvmPane() {
		JPanel svmPane = new JPanel();
		svmPane.setBorder(BorderFactory
				.createTitledBorder("Support Vector Machines"));
		svmPane.setLayout(new GridLayout(0, 2));

		// Gamma
		JLabel svmGammeLabel = new JLabel("Gamma");
		svmPane.add(svmGammeLabel);

		SpinnerNumberModel svmGammaSnm = new SpinnerNumberModel(0, 0, 15000,
				0.001);
		svmGamma = new JSpinner(svmGammaSnm);
		svmGamma.setEnabled(false);
		svmPane.add(svmGamma);

		return svmPane;
	}

	private JPanel createCorrelationPane() {
		JPanel correlationPane = new JPanel();
		correlationPane.setBorder(BorderFactory
				.createTitledBorder("Correlation"));

		return correlationPane;
	}

	private JPanel createBttnPane() {
		JPanel bttnPane = new JPanel();

		JButton okBttn = new JButton("OK");
		final ChangeClassifierFrame c = this;
		okBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (mlpBttn.isSelected()) {
					int input = fe.getFeatureDimension();
					int output = 1;
					int middle = (Integer) middleNeuronsSpinner.getValue();
					ArrayList<Integer> nnStructure = new ArrayList<Integer>();
					nnStructure.add(input);
					nnStructure.add(middle);
					nnStructure.add(output);

					IERPClassifier classifier = new MLPClassifier(nnStructure);
					classifier.setFeatureExtraction(fe);

					mainFrame.setFe(fe);
					mainFrame.setClassifier(classifier);
					mainFrame.setFeStatus("Feature Extraction: "
							+ fe.getClass().getSimpleName());
					mainFrame.setClassifierStatus("Classifier: "
							+ classifier.getClass().getSimpleName());

					trainingDialog(c, classifier);
				} else if (knnBttn.isSelected()) {
					int neighborsNumber = (Integer) neighborsNumberSpinner
							.getValue();

					IERPClassifier classifier = new KNNClassifier(
							neighborsNumber);
					classifier.setFeatureExtraction(fe);

					mainFrame.setFe(fe);
					mainFrame.setClassifier(classifier);
					mainFrame.setFeStatus("Feature Extraction: "
							+ fe.getClass().getSimpleName());
					mainFrame.setClassifierStatus("Classifier: "
							+ classifier.getClass().getSimpleName());

					trainingDialog(c, classifier);
				} else if (ldaBttn.isSelected()) {
					IERPClassifier classifier = new LinearDiscriminantAnalysisClassifier();
					classifier.setFeatureExtraction(fe);

					mainFrame.setFe(fe);
					mainFrame.setClassifier(classifier);
					mainFrame.setFeStatus("Feature Extraction: "
							+ fe.getClass().getSimpleName());
					mainFrame.setClassifierStatus("Classifier: "
							+ classifier.getClass().getSimpleName());

					trainingDialog(c, classifier);
				} else if (svmBttn.isSelected()) {
					IERPClassifier classifier;
					try {
						classifier = new SVMClassifier();
						classifier.setFeatureExtraction(fe);

						mainFrame.setFe(fe);
						mainFrame.setClassifier(classifier);
						mainFrame.setFeStatus("Feature Extraction: "
								+ fe.getClass().getSimpleName());
						mainFrame.setClassifierStatus("Classifier: "
								+ classifier.getClass().getSimpleName());

						trainingDialog(c, classifier);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				} else if (correlationBttn.isSelected()) {
					IERPClassifier classifier = new CorrelationClassifier();
					classifier.setFeatureExtraction(fe);

					mainFrame.setFe(fe);
					mainFrame.setClassifier(classifier);
					mainFrame.setFeStatus("Feature Extraction: "
							+ fe.getClass().getSimpleName());
					mainFrame.setClassifierStatus("Classifier: "
							+ classifier.getClass().getSimpleName());

					trainingDialog(c, classifier);
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

	private void trainingDialog(ChangeClassifierFrame c,
			IERPClassifier classifier) {
		if (mainFrame.isTrained() == false) {
			int dialogResult = JOptionPane
					.showConfirmDialog(
							null,
							"You have to train the classifier in order to use it\nWould you like to train it now?",
							"Classifier is not trained",
							JOptionPane.YES_NO_OPTION);
			if (dialogResult == JOptionPane.YES_OPTION) {
				c.dispose();

				// training
				new TrainUsingOfflineProvider(c.fe, classifier);
			} else {
				c.dispose();
			}
		} else {
			c.dispose();
		}
	}
}
