package icp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class OnlineDialog extends JDialog implements ActionListener {

	private JFrame mainWindow;
	private MainWindowProvider mainWindowProvider;
	private ImageComponent resultCanvas;
	private JLabel currEventLabel;
	private JLabel statusLabel;
	private JButton resetJB;
	
	
	private static final int SIZE_X = 400;
	private static final int SIZE_Y = 400;
	private static final int NUMBER_OF_ROWS = 3;
	private static final int NUMBER_OF_COLS = 3;
	private static final int MAX_RGB = 255;
	private JButton[][] calculatorButtons;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7060519312663555962L;

		
	public OnlineDialog(final JFrame mainWindow, final MainWindowProvider mainWindowProvider)
	{
		super(mainWindow);
		this.setTitle("On-line detection");
		this.setSize(SIZE_X, SIZE_Y);
		this.mainWindow = mainWindow;
		this.mainWindowProvider = mainWindowProvider;
		
		//this.resultCanvas = new ImageComponent(SIZE_X, SIZE_Y);
		//this.add(resultCanvas);
		//this.paintComponent(this.getGraphics());
		JPanel masterPanel = new JPanel(new BorderLayout());
		GridLayout calculatorLayout = new GridLayout(NUMBER_OF_ROWS, NUMBER_OF_COLS, 5, 5);
		JPanel calculatorPanel = new JPanel(calculatorLayout);
		//this.setLayout(calculatorLayout);
		this.calculatorButtons = new JButton[NUMBER_OF_ROWS][NUMBER_OF_COLS];
		
		for (int i = 0; i < NUMBER_OF_ROWS; i++) {
			for (int j = 0; j < NUMBER_OF_COLS; j++) {
				this.calculatorButtons[i][j] = new JButton("" + (i * NUMBER_OF_ROWS + j));
				this.calculatorButtons[i][j].setForeground(Color.RED);
				this.calculatorButtons[i][j].addActionListener(this);
				this.calculatorButtons[i][j].setActionCommand("" + i + "_" + j);
				calculatorPanel.add(this.calculatorButtons[i][j]);
			}
		}
		
		JPanel infoPanel = new JPanel(new BorderLayout()); 
		currEventLabel = new JLabel("Current stimulus: Classifier prediction:");
		statusLabel    = new JLabel("Winning number: 0. Weight: 0");
		resetJB        = new JButton("Reset");
		resetJB.addActionListener(this);
		resetJB.setActionCommand("Reset");
		infoPanel.add(currEventLabel, BorderLayout.NORTH );
		infoPanel.add(statusLabel, BorderLayout.CENTER);
		infoPanel.add(resetJB, BorderLayout.SOUTH);
		
		
		
		masterPanel.add(infoPanel, BorderLayout.NORTH);
		masterPanel.add(calculatorPanel, BorderLayout.SOUTH);
		this.add(masterPanel);
	}
	
	public void update(int row, int col, double diffPercent) {
		int diffColor = (int) ((diffPercent / 100) * MAX_RGB);
		Color color = this.calculatorButtons[row][col].getForeground();
		this.calculatorButtons[row][col].setForeground(new Color(Math.max(color.getRed() - diffColor, 0), Math.min(color.getGreen() + diffColor, MAX_RGB), color.getBlue()));
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("Reset"))
			System.out.println("Reset");
		else {
			String[] tokens = command.split("_");
			if (tokens.length < 2)
				throw new IllegalArgumentException("Unexpected action command: " + command + "!");
			int row = Integer.parseInt(tokens[0]);
			int col = Integer.parseInt(tokens[1]);
			this.update(row, col, 5);
		}
	}
	
	
	
	
}


class ImageComponent extends JComponent{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Image image;
    private static final int NUMBER_OF_ROWS = 2;
    private static final int NUMBER_OF_COLS = 5;
    
    public ImageComponent(int sizeX, int sizeY){
    	this.setSize(sizeX, sizeY);
        /*try{
            File image2 = new File("bishnu.jpg");
            image = ImageIO.read(image2);

        }
        catch (IOException e){
            e.printStackTrace();
        }*/
    }
    
    @Override
    public void paintComponent (Graphics g){
    	int fieldLength = 50;
    	Random r = new Random();
        for(int i = 0; i < NUMBER_OF_COLS * NUMBER_OF_ROWS; i++) {
			g.setColor(new Color(r.nextFloat(), r.nextFloat(), r.nextFloat()));
			g.fillRect((i % NUMBER_OF_ROWS) * fieldLength, (i % NUMBER_OF_COLS) * fieldLength,
					fieldLength, fieldLength);
		}
    }

}