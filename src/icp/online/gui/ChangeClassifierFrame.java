package icp.online.gui;

import icp.application.classification.CorrelationClassifier;
import icp.application.classification.IERPClassifier;
import icp.application.classification.KNNClassifier;
import icp.application.classification.LinearDiscriminantAnalysisClassifier;
import icp.application.classification.MLPClassifier;
import icp.application.classification.SVMClassifier;

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
	private JSpinner middleNeuronsSpinner;
	private JSpinner neighborsNumberSpinner;
	private JRadioButton mlpBttn;
	private JRadioButton knnBttn;
	private JRadioButton ldaBttn;
	private JRadioButton svmBttn;
	private JRadioButton correlationBttn;

	public ChangeClassifierFrame(MainFrame mainFrame) {
		super("Choose Classifier and its Parameters");

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

	private void setEscListener(ChangeClassifierFrame frame) {
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
				// TODO set classifier
				middleNeuronsSpinner.setEnabled(true);
				neighborsNumberSpinner.setEnabled(false);
			}
		});

		knnBttn = new JRadioButton("K Nearest Neighbors");
		knnBttn.setSelected(false);
		knnBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO set classifier
				middleNeuronsSpinner.setEnabled(false);
				neighborsNumberSpinner.setEnabled(true);
			}
		});

		ldaBttn = new JRadioButton("Linear Discriminant Analysis");
		ldaBttn.setSelected(false);
		ldaBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO set classifier
				middleNeuronsSpinner.setEnabled(false);
				neighborsNumberSpinner.setEnabled(false);
			}
		});

		svmBttn = new JRadioButton("Support Vector Machines");
		svmBttn.setSelected(false);
		svmBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO set classifier)
				middleNeuronsSpinner.setEnabled(false);
				neighborsNumberSpinner.setEnabled(false);
			}
		});

		correlationBttn = new JRadioButton("Correlation");
		correlationBttn.setSelected(false);
		correlationBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO set classifier
				middleNeuronsSpinner.setEnabled(false);
				neighborsNumberSpinner.setEnabled(false);
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
		JButton trainBttn = new JButton("Train");
		trainBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("training");
			}
		});
		JButton okBttn = new JButton("OK");
		ChangeClassifierFrame c = this;
		okBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO create classifiers
				if (mlpBttn.isSelected()) {
					mainFrame.setTrained(false);

					int input = mainFrame.getFe().getFeatureDimension();
					int output = 1;
					int middle = (int) middleNeuronsSpinner.getValue();
					ArrayList<Integer> nnStructure = new ArrayList<Integer>();
					nnStructure.add(input);
					nnStructure.add(middle);
					nnStructure.add(output);

					IERPClassifier classifier = new MLPClassifier(nnStructure);
					classifier.setFeatureExtraction(mainFrame.getFe());
					mainFrame.setClassifier(classifier);
				} else if (knnBttn.isSelected()) {
					mainFrame.setTrained(false);

					int neighborsNumber = (int) neighborsNumberSpinner
							.getValue();

					IERPClassifier classifier = new KNNClassifier(
							neighborsNumber);
					classifier.setFeatureExtraction(mainFrame.getFe());
					mainFrame.setClassifier(classifier);
				} else if (ldaBttn.isSelected()) {
					mainFrame.setTrained(false);

					IERPClassifier classifier = new LinearDiscriminantAnalysisClassifier();
					classifier.setFeatureExtraction(mainFrame.getFe());
					mainFrame.setClassifier(classifier);
				} else if (svmBttn.isSelected()) {
					mainFrame.setTrained(false);

					IERPClassifier classifier;
					try {
						classifier = new SVMClassifier();
						classifier.setFeatureExtraction(mainFrame.getFe());
						mainFrame.setClassifier(classifier);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				} else if (correlationBttn.isSelected()) {
					mainFrame.setTrained(false);

					IERPClassifier classifier = new CorrelationClassifier();
					classifier.setFeatureExtraction(mainFrame.getFe());
					mainFrame.setClassifier(classifier);
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
