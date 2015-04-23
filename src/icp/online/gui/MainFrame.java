package icp.online.gui;



import icp.Const;

import icp.application.classification.FilterAndSubsamplingFeatureExtraction;

import icp.application.classification.IERPClassifier;

import icp.application.classification.IFeatureExtraction;

import icp.application.classification.MLPClassifier;

import icp.online.app.IDataProvider;

import icp.online.app.OffLineDataProvider;

import icp.online.app.OnLineDataProvider;

import icp.online.app.OnlineDetection;



import java.awt.Color;

import java.awt.Dimension;

import java.awt.Font;

import java.awt.GridLayout;

import java.awt.Toolkit;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;

import java.awt.event.KeyEvent;

import java.io.File;

import java.util.Arrays;

import java.util.Comparator;

import java.util.Observable;

import java.util.Observer;



import javax.swing.AbstractAction;

import javax.swing.JFileChooser;

import javax.swing.JFrame;

import javax.swing.JMenu;

import javax.swing.JMenuBar;

import javax.swing.JMenuItem;

import javax.swing.JOptionPane;

import javax.swing.JPanel;

import javax.swing.JScrollPane;

import javax.swing.JTable;

import javax.swing.JTextPane;

import javax.swing.KeyStroke;

import javax.swing.filechooser.FileNameExtensionFilter;

import javax.swing.table.AbstractTableModel;

import javax.swing.text.SimpleAttributeSet;

import javax.swing.text.StyleConstants;

import javax.swing.text.StyledDocument;



import org.apache.log4j.BasicConfigurator;

import org.apache.log4j.Logger;



@SuppressWarnings("serial")

public class MainFrame extends JFrame implements Observer {



	private AbstractTableModel data;



	private JTextPane winnerJTA;



	private JFileChooser chooser;



	private Observer detection;



	private IDataProvider dp;



	public File eegFile;



	private Thread dataProvider;



	private final Logger log;



	private IFeatureExtraction fe;



	private IERPClassifier classifier;



	private final ShowChart epochCharts;



	private boolean trained = false;



	public MainFrame() {

		super(Const.APP_NAME);

		BasicConfigurator.configure();

		log = Logger.getLogger(MainFrame.class);

		epochCharts = new ShowChart(this);



		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		getContentPane().add(createContentJP());

		this.setJMenuBar(createMenu());



		this.setVisible(true);

		this.pack();

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

		this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height

				/ 2 - this.getSize().height / 2);

		this.setSize(Const.MAIN_WINDOW_WIDTH, Const.MAIN_WINDOW_HEIGHT);

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);



		classifier = new MLPClassifier();

		classifier.load(Const.TRAINING_FILE_NAME);

		fe = new FilterAndSubsamplingFeatureExtraction();

		classifier.setFeatureExtraction(fe);

	}



	private JMenuBar createMenu() {

		JMenuBar menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("File");

		JMenuItem offlineMenuItem = new JMenuItem();

		offlineMenuItem.setAction((new LoadOfflineData()));

		JMenuItem onlineMenuItem = new JMenuItem();

		onlineMenuItem.setAction(new LoadOnlineData());

		JMenuItem chartMenuItem = new JMenuItem();

		chartMenuItem.setAction(this.epochCharts);

		JMenuItem endMenuItem = new JMenuItem("Close");

		endMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,

				ActionEvent.CTRL_MASK));

		endMenuItem.addActionListener(new ActionListener() {



			@Override

			public void actionPerformed(ActionEvent e) {

				System.exit(0);

			}

		});

		final MainFrame mf = this;

		JMenu settingsMenu = new JMenu("Settings");

		JMenuItem featureMenuItem = new JMenuItem("Feature Extraction and Classifier");

		featureMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,

				ActionEvent.CTRL_MASK));

		featureMenuItem.addActionListener(new ActionListener() {



			@Override

			public void actionPerformed(ActionEvent e) {

				ChangeFeatureExtractionFrame f = new ChangeFeatureExtractionFrame(

						mf);

				f.setVisible(true);

			}

		});



		menuBar.add(fileMenu);

		fileMenu.add(onlineMenuItem);

		fileMenu.add(offlineMenuItem);

		fileMenu.add(chartMenuItem);

		fileMenu.addSeparator();

		fileMenu.add(endMenuItem);



		menuBar.add(settingsMenu);

		settingsMenu.add(featureMenuItem);



		return menuBar;

	}



	private JPanel createContentJP() {

		GridLayout mainLayout = new GridLayout(1, 2);

		JPanel contentJP = new JPanel(mainLayout);

		contentJP.add(createStimuliJT());

		contentJP.add(createWinnerJTA());



		return contentJP;

	}



	private JTextPane createWinnerJTA() {

		winnerJTA = new JTextPane();

		Font font = new Font(Const.RESULT_FONT_NAME, Const.RESULT_FONT_STYLE,

				Const.RESULT_FONT_SIZE);

		winnerJTA.setFont(font);

		winnerJTA.setBackground(Color.BLACK);

		winnerJTA.setForeground(Color.WHITE);

		StyledDocument doc = winnerJTA.getStyledDocument();

		SimpleAttributeSet center = new SimpleAttributeSet();

		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);

		doc.setParagraphAttributes(0, doc.getLength(), center, false);

		winnerJTA.setText(Const.UNKNOWN_RESULT);

		return winnerJTA;

	}



	private JScrollPane createStimuliJT() {

		data = new StimuliTableModel();

		JTable stimuliJT = new JTable(data);

		JScrollPane jsp = new JScrollPane(stimuliJT);

		stimuliJT.setFillsViewportHeight(true);



		return jsp;

	}



	private void initProbabilities(double[] probabilities) {

		Integer[] ranks = new Integer[probabilities.length];

		for (int i = 0; i < ranks.length; ++i) {

			ranks[i] = i;

		}

		Comparator<Integer> gc = new ProbabilityComparator(probabilities);

		Arrays.sort(ranks, gc);



		winnerJTA.setText(String.valueOf(ranks[0] + 1));

		for (int i = 0; i < probabilities.length; i++) {

			data.setValueAt(probabilities[ranks[i]], i, 1);

			data.setValueAt(ranks[i] + 1, i, 0);

		}



		this.validate();

		this.repaint();

	}



	@Override

	public void update(Observable sender, Object message)

			throws IllegalArgumentException {

		if (message instanceof OnlineDetection) {

			double[] probabilities = ((OnlineDetection) message)

					.getWeightedResults();



			initProbabilities(probabilities);



			this.epochCharts.update(((OnlineDetection) message).getPzAvg());

		} /*

		 * else { log.error(MainFrame.class.toString() +

		 * ": Expencted online detection, but received something else."); throw

		 * new IllegalArgumentException(

		 * "Expencted online detection, but received something else."); }

		 */

	}



	private void initGui() {

		double[] zeros = new double[data.getRowCount()];

		Arrays.fill(zeros, 0);

		initProbabilities(zeros);

		winnerJTA.setText(Const.UNKNOWN_RESULT);

	}



	private void stopRunningThread() {

		try {

			if (dataProvider != null) {

				if (dp != null) {

					dp.stop();

				} else {

					dataProvider.interrupt();

				}

			}

		} catch (Exception ex) {



		}

	}



	public IERPClassifier getClassifier() {

		return classifier;

	}



	public void setClassifier(IERPClassifier classifier) {

		this.classifier = classifier;

	}



	public IFeatureExtraction getFe() {

		return fe;

	}



	public void setFe(IFeatureExtraction fe) {

		this.fe = fe;

	}



	public boolean isTrained() {

		return trained;

	}



	public void setTrained(boolean trained) {

		this.trained = trained;

	}



	public class LoadOfflineData extends AbstractAction {



		MainFrame mainFrame;



		@Override

		public void actionPerformed(ActionEvent actionevent) {

			initGui();

			chooser = new JFileChooser();

			FileNameExtensionFilter filter = new FileNameExtensionFilter(

					"EEG files .eeg", "EEG", "eeg");

			chooser.addChoosableFileFilter(filter);

			chooser.setFileFilter(filter);

			chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

			int i = chooser.showDialog(mainFrame, "Open");

			if (i == 0) {

				eegFile = chooser.getSelectedFile();

				detection = new OnlineDetection(classifier, mainFrame);

				stopRunningThread();



				try {

					dp = new OffLineDataProvider(eegFile, detection);

					dataProvider = new Thread((OffLineDataProvider) dp);

					dataProvider.start();

				} catch (Exception ex) {

					JOptionPane.showMessageDialog(mainFrame, ex.getMessage(),

							"Error", JOptionPane.ERROR_MESSAGE);

				}

			}

		}



		public LoadOfflineData() {

			super();

			mainFrame = MainFrame.this;

			putValue("AcceleratorKey", KeyStroke.getKeyStroke(KeyEvent.VK_O,

					ActionEvent.CTRL_MASK));

			putValue("Name", "Offline data");

		}

	}



	public class LoadOnlineData extends AbstractAction {



		MainFrame mainFrame;



		@Override

		public void actionPerformed(ActionEvent actionevent) {

			initGui();



			SetupDialogContent content = new SetupDialogContent();

			int result;

			boolean isOk = false;

			String recorderIPAddress = null;

			int port = -1;

			while (!isOk) {

				result = JOptionPane

						.showConfirmDialog(null, content,

								"Guess the Number: Setup",

								JOptionPane.OK_CANCEL_OPTION,

								JOptionPane.PLAIN_MESSAGE);

				if (result == JOptionPane.CANCEL_OPTION) {

					return;

				}



				recorderIPAddress = content.getIP();

				port = content.getPort();

				if (port != -1 && recorderIPAddress != null) {

					isOk = true;

				} else {

					if (port == -1) {

						JOptionPane.showMessageDialog(null,

								"Invalid port number!", "Error",

								JOptionPane.ERROR_MESSAGE);

					}

					if (recorderIPAddress == null) {

						JOptionPane.showMessageDialog(null,

								"Invalid IP address!", "Error",

								JOptionPane.ERROR_MESSAGE);

					}

				}

			}



			if (isOk) {

				stopRunningThread();



				detection = new OnlineDetection(classifier, mainFrame);

				try {

					dp = new OnLineDataProvider(recorderIPAddress, port,

							detection);

					dataProvider = new Thread((OnLineDataProvider) dp);

					dataProvider.start();

				} catch (Exception ex) {

					JOptionPane.showMessageDialog(mainFrame, ex.getMessage(),

							"Connection Error", JOptionPane.ERROR_MESSAGE);

				}

			}

		}



		public LoadOnlineData() {

			super();

			mainFrame = MainFrame.this;

			putValue("AcceleratorKey", KeyStroke.getKeyStroke(KeyEvent.VK_P,

					ActionEvent.CTRL_MASK));

			putValue("Name", "Online data");

		}

	}

}


