package icp.online.gui;

import icp.Const;
import icp.application.classification.CorrelationClassifier;
import icp.application.classification.IERPClassifier;
import icp.application.classification.IFeatureExtraction;
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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

/**
 * Window for changing classifier and its parameters
 * 
 * @author Jaroslav Klaus
 *
 */
@SuppressWarnings("serial")
public class ChangeClassifierFrame extends JFrame {

	/**
	 * Reference to mainFrame
	 */
	private MainFrame mainFrame;

	/**
	 * Feature Extraction method
	 */
	private IFeatureExtraction fe;

	/**
	 * Parameters for Feature Extraction method
	 */
	private List<String> feParams;

	/**
	 * Spinner for MLP's middle neurons
	 */
	private JSpinner middleNeuronsSpinner;

	/**
	 * Spinner for KNN's number of neighbors
	 */
	private JSpinner neighborsNumberSpinner;

	/**
	 * Spinner for SVM's cost
	 */
	private JSpinner svmCost;

	/**
	 * Radio button for selecting MLP classifier
	 */
	private JRadioButton mlpBttn;

	/**
	 * Radio button for selecting KNN classifier
	 */
	private JRadioButton knnBttn;

	/**
	 * Radio button for selecting LDA classifier
	 */
	private JRadioButton ldaBttn;

	/**
	 * Radio button for selecting SVM classifier
	 */
	private JRadioButton svmBttn;

	/**
	 * Radio button for selecting Correlation classifier
	 */
	private JRadioButton correlationBttn;

	/**
	 * Constructor for creating this window and creating its variables
	 * 
	 * @param mainFrame
	 *            - reference to mainFrame
	 * @param fe
	 *            - Feature Extraction method
	 * @param feParams
	 *            - parameters for Feature Extraction method
	 */
	public ChangeClassifierFrame(MainFrame mainFrame, IFeatureExtraction fe,
			List<String> feParams) {
		super("Choose Classifier and its Parameters");
		this.mainFrame = mainFrame;
		this.fe = fe;
		this.feParams = feParams;
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

	/**
	 * Sets listener to Esc key. After pressing Esc, this window closes and
	 * nothing is changed
	 * 
	 * @param frame
	 *            - this frame
	 */
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

	/**
	 * Creates the main layout and panel
	 * 
	 * @return main panel
	 */
	private JPanel createClassifierFrame() {
		GridLayout mainLayout = new GridLayout(1, 2);
		JPanel contentJP = new JPanel(mainLayout);
		contentJP.add(createRadioBttns());
		contentJP.add(createParameters());

		return contentJP;
	}

	/**
	 * Creates panel with radio buttons for selecting classifier and its actions
	 * when selected
	 * 
	 * @return panel with radio buttons or selecting classifier
	 */
	private JPanel createRadioBttns() {
		mlpBttn = new JRadioButton("MLP");
		mlpBttn.setSelected(false);
		mlpBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				middleNeuronsSpinner.setEnabled(true);
				neighborsNumberSpinner.setEnabled(false);
				svmCost.setEnabled(false);
			}
		});

		knnBttn = new JRadioButton("K Nearest Neighbors");
		knnBttn.setSelected(false);
		knnBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				middleNeuronsSpinner.setEnabled(false);
				neighborsNumberSpinner.setEnabled(true);
				svmCost.setEnabled(false);
			}
		});

		ldaBttn = new JRadioButton("Linear Discriminant Analysis");
		ldaBttn.setSelected(false);
		ldaBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				middleNeuronsSpinner.setEnabled(false);
				neighborsNumberSpinner.setEnabled(false);
				svmCost.setEnabled(false);
			}
		});

		svmBttn = new JRadioButton("Support Vector Machines");
		svmBttn.setSelected(false);
		svmBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				middleNeuronsSpinner.setEnabled(false);
				neighborsNumberSpinner.setEnabled(false);
				svmCost.setEnabled(true);
			}
		});

		correlationBttn = new JRadioButton("Correlation");
		correlationBttn.setSelected(false);
		correlationBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				middleNeuronsSpinner.setEnabled(false);
				neighborsNumberSpinner.setEnabled(false);
				svmCost.setEnabled(false);
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

	/**
	 * Create panel with parameters for classifiers
	 * 
	 * @return panel with parameters for classifiers
	 */
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

	/**
	 * Creates panel with parameters for MLP
	 * 
	 * @return panel with parameters for MLP
	 */
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

	/**
	 * Creates panel with parameters for KNN
	 * 
	 * @return panel with parameters for KNN
	 */
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

	/**
	 * Creates panel with parameters for LDA
	 * 
	 * @return panel with parameters for LDA
	 */
	private JPanel createLdaPane() {
		JPanel ldaPane = new JPanel();
		ldaPane.setBorder(BorderFactory
				.createTitledBorder("Linear Discriminant Analysis"));

		ldaPane.add(new JLabel("No parameters for this classifier"));

		return ldaPane;
	}

	/**
	 * Creates panel with parameters for SVM
	 * 
	 * @return panel with parameters for SVM
	 */
	private JPanel createSvmPane() {
		JPanel svmPane = new JPanel();
		svmPane.setBorder(BorderFactory
				.createTitledBorder("Support Vector Machines"));
		svmPane.setLayout(new GridLayout(0, 2));

		// Gamma
		JLabel svmCostLabel = new JLabel("Cost");
		svmPane.add(svmCostLabel);

		SpinnerNumberModel svmCostSnm = new SpinnerNumberModel(0, 0, 15000,
				0.001);
		svmCost = new JSpinner(svmCostSnm);
		svmCost.setEnabled(false);
		svmPane.add(svmCost);

		return svmPane;
	}

	/**
	 * Creates panel with parameters for Correlation
	 * 
	 * @return panel with parameters for Correlation
	 */
	private JPanel createCorrelationPane() {
		JPanel correlationPane = new JPanel();
		correlationPane.setBorder(BorderFactory
				.createTitledBorder("Correlation"));

		return correlationPane;
	}

	/**
	 * Creates panel with OK button
	 * 
	 * @return panel with OK button
	 */
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

					List<String> classifierParams = new ArrayList<String>();
					for (int p : nnStructure) {
						classifierParams.add(p + "");
					}

					trainingDialog(c, mainFrame, classifier, classifierParams);
				} else if (knnBttn.isSelected()) {
					int neighborsNumber = (Integer) neighborsNumberSpinner
							.getValue();

					IERPClassifier classifier = new KNNClassifier(
							neighborsNumber);
					classifier.setFeatureExtraction(fe);

					List<String> classifierParams = new ArrayList<String>();
					classifierParams.add(neighborsNumber + "");

					trainingDialog(c, mainFrame, classifier, classifierParams);
				} else if (ldaBttn.isSelected()) {
					IERPClassifier classifier = new LinearDiscriminantAnalysisClassifier();
					classifier.setFeatureExtraction(fe);

					List<String> classifierParams = new ArrayList<String>();

					trainingDialog(c, mainFrame, classifier, classifierParams);
				} else if (svmBttn.isSelected()) {
					IERPClassifier classifier;
					try {
						classifier = new SVMClassifier();
						// TODO Set parameters
						classifier.setFeatureExtraction(fe);

						List<String> classifierParams = new ArrayList<String>();
						classifierParams.add((int) svmCost.getValue() + "");

						trainingDialog(c, mainFrame, classifier,
								classifierParams);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				} else if (correlationBttn.isSelected()) {
					IERPClassifier classifier = new CorrelationClassifier();
					classifier.setFeatureExtraction(fe);

					List<String> classifierParams = new ArrayList<String>();

					trainingDialog(c, mainFrame, classifier, classifierParams);
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

	/**
	 * Creates training dialog that appears after choosing Classifier and
	 * clicking OK button
	 * 
	 * @param c
	 *            - this frame
	 * @param mainFrame
	 *            - reference to mainFrame
	 * @param classifier
	 *            - created Classifier
	 * @param classifierParams
	 *            - parameters for classifier
	 */
	private void trainingDialog(ChangeClassifierFrame c, MainFrame mainFrame,
			IERPClassifier classifier, List<String> classifierParams) {
		if (mainFrame.isTrained() == false) {
			int dialogResult = JOptionPane.showConfirmDialog(null,
					"You have to train the classifier in order to use it",
					"Classifier is not trained", JOptionPane.OK_CANCEL_OPTION);
			if (dialogResult == JOptionPane.OK_OPTION) {
				c.dispose();

				// TODO training
				// TrainUsingOfflineProvider train = new
				// TrainUsingOfflineProvider(c.fe, classifier);
				mainFrame.setFe(fe);
				mainFrame.setClassifier(classifier);
				mainFrame.setFeStatus("Feature Extraction: "
						+ fe.getClass().getSimpleName());
				mainFrame.setClassifierStatus("Classifier: "
						+ classifier.getClass().getSimpleName());

				writeLastTrainedClassifier(fe.getClass().getSimpleName(),
						feParams, classifier.getClass().getSimpleName(),
						classifierParams, Const.LAST_TRAINED_SETTINGS_FILE_NAME);
			}
		} else {
			c.dispose();
		}
	}

	/**
	 * Writes last trained Feature Extraction method and Classifier to a file
	 * 
	 * @param feName
	 *            - simple class name of Feature Extraction method
	 * @param feParams
	 *            - parameters for Feature Extraction method
	 * @param classifierName
	 *            - simple class name of Classifier
	 * @param classifierParams
	 *            - parameters for Classifier
	 * @param file
	 *            - name of the file to write into
	 */
	private void writeLastTrainedClassifier(String feName,
			List<String> feParams, String classifierName,
			List<String> classifierParams, String file) {
		try {
			File f = new File(file);
			FileWriter fw = new FileWriter(f);
			fw.write(feName + "\n");
			for (String param : feParams) {
				fw.write(param + "\n");
			}
			fw.write(classifierName + "\n");
			for (String param : classifierParams) {
				fw.write(param + "\n");
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
