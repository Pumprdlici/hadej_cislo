package icp.online.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class MainFrame extends JFrame implements Observer {
	
	private static final String APP_NAME = "Guess the number";
	
	private static final int MAIN_WINDOW_WIDTH = 640;
	
	private static final int MAIN_WINDOW_HEIGHT = 320;
	
	private AbstractTableModel data;
	
	private Logger log;
	
	public MainFrame() {
		super(APP_NAME);
		BasicConfigurator.configure();
		log = Logger.getLogger(MainFrame.class);
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().add(createContentJP());
		
		this.setVisible(true);
        this.pack();
        this.setSize(MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT);
	}
	
	private JPanel createContentJP() {
		GridLayout mainLayout = new GridLayout(1, 2);
		JPanel contentJP = new JPanel(mainLayout);
		contentJP.add(createStimuliJT());
		contentJP.add(createWinnerJTA());
		
		return contentJP;
	}
	
	private JTextPane createWinnerJTA(){
		JTextPane winnerJTA = new JTextPane();
		Font font = new Font("Arial", Font.BOLD, 250);
		winnerJTA.setFont(font);
		winnerJTA.setBackground(Color.BLACK);
		winnerJTA.setForeground(Color.WHITE);
		StyledDocument doc = winnerJTA.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		winnerJTA.setText("?");
		return winnerJTA;
	}
	
	private JScrollPane createStimuliJT() {
		data = new StimuliTableModel(); 
		JTable stimuliJT = new JTable(data);
		JScrollPane jsp = new JScrollPane(stimuliJT);
		stimuliJT.setFillsViewportHeight(true);
		
		return jsp;
	}
	
	private void updateWinner() {
		/*double maxValue = -1d;
		
		for (int i = 0; i < data.get)*/
	}
	
	@Override
	public void update(Observable sender, Object message) throws IllegalArgumentException {
		if (message instanceof double[]) {
			double[] probabilities = (double[]) message;
			
			for (int i = 0; i < probabilities.length; i++)
				data.setValueAt(probabilities[i], i, 1);
			
			updateWinner();
			
			this.validate();
			this.repaint();
		}
		else {
			log.error(MainFrame.class.toString() + ": Expencted array of doubles, but received something else.");
			throw new IllegalArgumentException("Expencted array of doubles, but received something else.");
		}
	}
}
