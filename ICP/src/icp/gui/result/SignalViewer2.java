package icp.gui.result;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;
import javax.swing.JPanel;

import cz.zcu.kiv.rondik.mp.algorithm.Atom;
import cz.zcu.kiv.rondik.mp.algorithm.DecompositionCollection;
import cz.zcu.kiv.rondik.mp.algorithm.GaborsAtom;
import cz.zcu.kiv.rondik.mp.algorithm.UsersAtom;

@SuppressWarnings("serial")
public class SignalViewer2 extends JPanel implements MouseListener, MouseMotionListener
{
	private static Color[] COLORS = {Color.GREEN, Color.YELLOW, Color.ORANGE, Color.MAGENTA};
	
	public class ComunicationProvider extends Observable implements Observer
	{
		/**
		 * Metoda pro posílání zpráv registrovaným posluchaèùm.
		 * @param object Zpráva.
		 */
		public void sendPoint()
		{
			this.setChanged();
			this.notifyObservers(mousePosition);
		}
		@Override
		public void update(Observable sender, Object msg)
		{
			if (msg instanceof Point)
			{
				mousePosition = (Point) msg;
				repaint();
			}
		}
	}
	
	private int colorIndex;
	
	private static double COLOR_SIZE_DIFFERENCE_STEP = 20D;
	
	private ComunicationProvider cp;
	
	private DecompositionCollection atoms;
	
	private boolean settedZoomY;
	
	private double absMaxValue;
	
	private double canvasWidth;
	
	private double canvasHeight;
	
	private Graphics2D g2;
	
	private static final Insets INSETS = new Insets(10, 50, 10, 10);
	
	private double[] values;
	
	private JComponent background;
	
	private double zoomY;
	
	private Point mousePosition;
	
	private boolean mouseOver;
	
	public SignalViewer2(JComponent background)
	{
		super();
		this.background = background;
		addMouseListener(this);
		addMouseMotionListener(this);
		values = null;
		zoomY = 1;
		settedZoomY = false;
		mouseOver = false;
		cp = new ComunicationProvider();
	}
	
	public ComunicationProvider getComunicationProvider()
	{
		return cp;
	}
	
	public void addObserver(Observer observer)
	{
		cp.addObserver(observer);
	}
	
	public void setZoomY(double zoomY)
	{
		this.zoomY = zoomY;
		settedZoomY = true;
	}
	
	/**
	 * @param atoms the atoms to set
	 */
	public void setAtoms(DecompositionCollection atoms)
	{
		this.atoms = atoms;
		colorIndex = 0;
		repaint();
	}

	private void highlihtAtoms()
	{
		if (atoms != null && atoms.size() > 0)
		{
			Color oldColor = g2.getColor();
			Atom current;
			double step = (canvasWidth - INSETS.left - INSETS.right) / (values.length - 1);
			for (int i = 0; i < atoms.size(); i++)
			{
				current = atoms.getAtom(i);
				if (current instanceof UsersAtom)
				{
					g2.setColor(COLORS[colorIndex]);
					colorIndex = (colorIndex + 1) % COLORS.length;
					g2.fill(new Rectangle2D.Double(INSETS.left + step * current.getPosition(), INSETS.top + i * COLOR_SIZE_DIFFERENCE_STEP, 
							step * ((UsersAtom)current).getStretch(), canvasHeight - INSETS.top - INSETS.bottom - 2 * i * COLOR_SIZE_DIFFERENCE_STEP));
					g2.setColor(Color.RED);
					g2.drawString(((UsersAtom)current).getName(), (float) (INSETS.left + step * current.getPosition()), (float) (INSETS.top + 10 + i * COLOR_SIZE_DIFFERENCE_STEP));
				}
				else if (current instanceof GaborsAtom)
				{
					g2.setColor(COLORS[colorIndex]);
					colorIndex = (colorIndex + 1) % COLORS.length;
					g2.fill(new Rectangle2D.Double(INSETS.left + (step * current.getPosition()) - step *current.getScale(),
							INSETS.top + i * COLOR_SIZE_DIFFERENCE_STEP, 2 * step * current.getScale(), canvasHeight - INSETS.top - INSETS.bottom  - 2 * i * COLOR_SIZE_DIFFERENCE_STEP));
				}
			}
			g2.setColor(oldColor);
		}
	}
	
	private void paintAxis()
	{
		g2.setPaint(Color.GRAY);
		double zero = INSETS.top + ((canvasHeight - INSETS.top - INSETS.bottom) / 2);
		
		g2.draw(new Line2D.Double(INSETS.left - 10, zero, canvasWidth - INSETS.right, zero));
		g2.draw(new Line2D.Double(INSETS.left, canvasHeight - INSETS.bottom, INSETS.left, INSETS.top));
		
		double step = (canvasWidth - INSETS.left - INSETS.right) / (values.length - 1);
		double doubleI = 0;
		int intI = 0;
		while (doubleI < canvasWidth - INSETS.left - INSETS.right)
		{
			if (intI % 50 == 0)
			{
				g2.draw(new Line2D.Double(INSETS.left + doubleI, zero - 5, INSETS.left + doubleI, zero + 5));
				g2.drawString(String.valueOf(intI), new Float(INSETS.left + doubleI - 10), new Float(zero + 15));
			}
			else
			{
				g2.draw(new Line2D.Double(INSETS.left + doubleI, zero - 2, INSETS.left + doubleI, zero + 2));
			}
			doubleI += 10 * step;
			intI += 10;
		}
		
		step = 0.5;
		doubleI = INSETS.top + ((canvasHeight - INSETS.top - INSETS.bottom) / 2);
		doubleI -= step * zoomY;
		intI = (int) (10 * step);
		float[] dash = {1F, 5F};
		BasicStroke oldStroke = (BasicStroke) g2.getStroke();
		BasicStroke yValueStroke = new BasicStroke(
				oldStroke.getLineWidth(), 
				oldStroke.getEndCap(),
				oldStroke.getLineJoin(),
				oldStroke.getMiterLimit(),
				dash,
				0F);
		
		g2.setStroke(yValueStroke);
		
		while (doubleI > INSETS.top)
		{
			if (intI % 10 == 0)
			{
				g2.setStroke(oldStroke);
				g2.draw(new Line2D.Double(INSETS.left - 5, doubleI, INSETS.left + 5, doubleI));
				g2.setStroke(yValueStroke);
				g2.draw(new Line2D.Double(INSETS.left - 5, doubleI, canvasWidth - INSETS.right, doubleI));
				g2.drawString(String.valueOf(intI / 10), new Float(INSETS.left - 15), new Float(doubleI));
			}
			else
			{
				g2.setStroke(oldStroke);
				g2.draw(new Line2D.Double(INSETS.left - 2, doubleI, INSETS.left + 2, doubleI));
				g2.setStroke(yValueStroke);
			}
			doubleI -= step * zoomY;
			intI += 10 * step;
		}
		
		doubleI = INSETS.top + ((canvasHeight - INSETS.top - INSETS.bottom) / 2);
		doubleI += step * zoomY;
		intI = - ((int) (10 * step));
		
		while (doubleI < canvasHeight - INSETS.bottom)
		{
			if (intI % 10 == 0)
			{
				g2.setStroke(oldStroke);
				g2.draw(new Line2D.Double(INSETS.left - 5, doubleI, INSETS.left + 5, doubleI));
				g2.setStroke(yValueStroke);
				g2.draw(new Line2D.Double(INSETS.left - 5, doubleI, canvasWidth - INSETS.right, doubleI));
				g2.drawString(String.valueOf(intI / 10), new Float(INSETS.left - 15), new Float(doubleI));
			}
			else
			{
				g2.setStroke(oldStroke);
				g2.draw(new Line2D.Double(INSETS.left - 2, doubleI, INSETS.left + 2, doubleI));
				g2.setStroke(yValueStroke);
			}
			doubleI += step * zoomY;
			intI -= 10 * step;
		}
		
		g2.drawString("[ms]", (float) (canvasWidth - INSETS.right - 24), (float) ((canvasHeight / 2) + 32));
		g2.drawString("[mV]", INSETS.left + 8, INSETS.top + 12);
		
		g2.setStroke(oldStroke);
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		colorIndex = 0;
		g2 = (Graphics2D) g;
		Dimension size = background.getSize(); 
		canvasWidth = size.getWidth();
		canvasHeight = size.getHeight();
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setPaint(Color.WHITE);
		g2.fill(new Rectangle2D.Double(0, 0, canvasWidth, canvasHeight));
		
		if (values != null && values.length > 0)
		{
			if (!settedZoomY)
			{
				zoomY = ((canvasHeight / 2) - INSETS.top - INSETS.bottom) / Math.abs(absMaxValue);
			}
			
			highlihtAtoms();
			paintAxis();
			drawSignal();
			showMousePosition();
		}
	}
	
	private void showMousePosition()
	{
		if (mousePosition != null)
		{
			double mousePositionX = mousePosition.getX();
			if (mousePositionX >= INSETS.left && mousePositionX < canvasWidth - INSETS.right)
			{
				double step = (canvasWidth - INSETS.left - INSETS.right) / values.length;
				int index = (int) ((mousePositionX - INSETS.left) / step);
				
				g2.setColor(Color.RED);
				g2.draw(new Line2D.Double(mousePositionX, 0, mousePositionX, canvasHeight));
				g2.drawString(String.valueOf(values[index]) + " mV",  (int) mousePositionX + 2, 20);
				g2.drawString(String.valueOf(index) + " ms",  (int) mousePositionX + 2, 32);
			}
		}
	}
	
	private void drawSignal()
	{
		g2.setPaint(Color.BLUE);
		double zero = INSETS.top + ((canvasHeight - INSETS.top - INSETS.bottom) / 2);
		double step = (canvasWidth - INSETS.left - INSETS.right) / (values.length - 1);
		double x0 = INSETS.left;
		double x1 = step + INSETS.left;
		double y0 = 0;
		double y1;
		
		y1 = (-values[0] * zoomY + zero);
		
		for (int i = 1; i < values.length; i++) {

			y0 = y1;
			
			y1 = (-values[i] * zoomY + zero);
            
			g2.draw(new Line2D.Double(x0, y0, x1, y1));
			
			x0 = x1;
			x1 += step;
		}
		
	}
	
	public void setValues(double[] values)
	{
		this.values = values;
		absMaxValue = 0;
		if (values != null)
		{
			for (int i = 0; i < values.length; i++)
			{
				if (Math.abs(absMaxValue) < Math.abs(values[i]))
					absMaxValue = Math.abs(values[i]);
			}
		}
		
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent event)
	{
		if (values != null && values.length > 0)
		{
			//new SignalDetail(values);
		}
	}

	@Override
	public void mouseEntered(MouseEvent event)
	{
		mouseOver = true;
		mousePosition = event.getPoint();
	}

	@Override
	public void mouseExited(MouseEvent event)
	{
		mouseOver = false;
	}

	@Override
	public void mousePressed(MouseEvent event)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent event)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent event)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent event)
	{
		if (mouseOver && values != null && values.length > 0)
		{
			mousePosition = event.getPoint();
			cp.sendPoint();
			repaint();
		}
	}
}
