package icp.online.gui;

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
import javax.swing.JTextField;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class ChangeClassifierFrame extends JFrame {

	private MainFrame mainFrame;
	private JPanel parameters;

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
		
		this.getRootPane().registerKeyboardAction(escListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
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
		JRadioButton mlpBttn = new JRadioButton("MLP");
		mlpBttn.setSelected(false);
		mlpBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO set classifier
				Component cmp[] = parameters.getComponents();
				for (Component c : cmp) {
					if (c.getClass().equals(JPanel.class)) {
						Component[] newCmp = ((JPanel) c).getComponents();
						for (Component newC : newCmp) {
							if (newC.getName() != null
									&& newC.getName().contains("mlp")) {
								newC.setEnabled(true);
							} else if (newC.getClass().equals(JTextField.class)) {
								newC.setEnabled(false);
							}
						}
					}
				}
			}
		});

		JRadioButton knnBttn = new JRadioButton("K Nearest Neighbors");
		knnBttn.setSelected(false);
		knnBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO set classifier
				Component cmp[] = parameters.getComponents();
				for (Component c : cmp) {
					if (c.getClass().equals(JPanel.class)) {
						Component[] newCmp = ((JPanel) c).getComponents();
						for (Component newC : newCmp) {
							if (newC.getName() != null
									&& newC.getName().contains("knn")) {
								newC.setEnabled(true);
							} else if (newC.getClass().equals(JTextField.class)) {
								newC.setEnabled(false);
							}
						}
					}
				}
			}
		});

		JRadioButton ldaBttn = new JRadioButton("Linear Discriminant Analysis");
		ldaBttn.setSelected(false);
		ldaBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO set classifier
				Component cmp[] = parameters.getComponents();
				for (Component c : cmp) {
					if (c.getClass().equals(JPanel.class)) {
						Component[] newCmp = ((JPanel) c).getComponents();
						for (Component newC : newCmp) {
							if (newC.getName() != null
									&& newC.getName().contains("lda")) {
								newC.setEnabled(true);
							} else if (newC.getClass().equals(JTextField.class)) {
								newC.setEnabled(false);
							}
						}
					}
				}
			}
		});

		JRadioButton svmBttn = new JRadioButton("Support Vector Machines");
		svmBttn.setSelected(false);
		svmBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO set classifier
				Component cmp[] = parameters.getComponents();
				for (Component c : cmp) {
					if (c.getClass().equals(JPanel.class)) {
						Component[] newCmp = ((JPanel) c).getComponents();
						for (Component newC : newCmp) {
							if (newC.getName() != null
									&& newC.getName().contains("svm")) {
								newC.setEnabled(true);
							} else if (newC.getClass().equals(JTextField.class)) {
								newC.setEnabled(false);
							}
						}
					}
				}
			}
		});

		JRadioButton correlationBttn = new JRadioButton("Correlation");
		correlationBttn.setSelected(false);
		correlationBttn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO set classifier
				Component cmp[] = parameters.getComponents();
				for (Component c : cmp) {
					if (c.getClass().equals(JPanel.class)) {
						Component[] newCmp = ((JPanel) c).getComponents();
						for (Component newC : newCmp) {
							if (newC.getName() != null
									&& newC.getName().contains("correlation")) {
								newC.setEnabled(true);
							} else if (newC.getClass().equals(JTextField.class)) {
								newC.setEnabled(false);
							}
						}
					}
				}
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
		JPanel mlpPane = new JPanel();
		mlpPane.setBorder(BorderFactory.createTitledBorder("MLP"));
		mlpPane.setLayout(new GridLayout(0, 2));
		JLabel inputNeuronsLabel = new JLabel("Number of Input Neurons");
		mlpPane.add(inputNeuronsLabel);
		JTextField inputNeuronsText = new JTextField();
		inputNeuronsText.setName("mlpParameter");
		inputNeuronsText.setEnabled(false);
		mlpPane.add(inputNeuronsText);
		JLabel middleNeuronsLabel = new JLabel("Number of Middle Neurons");
		mlpPane.add(middleNeuronsLabel);
		JTextField middleNeuronsText = new JTextField();
		middleNeuronsText.setName("mlpParameter");
		middleNeuronsText.setEnabled(false);
		mlpPane.add(middleNeuronsText);
		JLabel outputNeuronsLabel = new JLabel("Number of Output Neurons");
		mlpPane.add(outputNeuronsLabel);
		JTextField outputNeuronsText = new JTextField();
		outputNeuronsText.setName("mlpParameter");
		outputNeuronsText.setEnabled(false);
		mlpPane.add(outputNeuronsText);

		JPanel knnPane = new JPanel();
		knnPane.setBorder(BorderFactory
				.createTitledBorder("K Nearest Neighbors"));
		knnPane.setLayout(new GridLayout(0, 2));
		JLabel neighborsNumberLabel = new JLabel("Number of Neighbors");
		knnPane.add(neighborsNumberLabel);
		JTextField neighborsNumberText = new JTextField();
		neighborsNumberText.setName("knnParameter");
		neighborsNumberText.setEnabled(false);
		knnPane.add(neighborsNumberText);

		JPanel ldaPane = new JPanel();
		ldaPane.setBorder(BorderFactory
				.createTitledBorder("Linear Discriminant Analysis"));
		ldaPane.add(new JLabel("No parameters for this classifier"));

		JPanel svmPane = new JPanel();
		svmPane.setBorder(BorderFactory
				.createTitledBorder("Support Vector Machines"));

		JPanel correlationPane = new JPanel();
		correlationPane.setBorder(BorderFactory
				.createTitledBorder("Correlation"));

		JButton okBttn = new JButton("OK");

		JPanel bttnPane = new JPanel();
		bttnPane.setLayout(new BoxLayout(bttnPane, BoxLayout.LINE_AXIS));
		bttnPane.add(Box.createHorizontalGlue());
		bttnPane.add(okBttn);

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
}
