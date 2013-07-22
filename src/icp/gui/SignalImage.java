package icp.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

import javax.swing.*;

public class SignalImage extends JPanel
{
	private static final long serialVersionUID = 1L;
	private final int VERTICAL = 1, HORIZONT = 5; 
	private final int LEFT = 70, INDENT_LABELS = 5, COUNT_FRACT = 5; 	
	private final int[] XAXIS_SCALES = {1, 10, 100, 1000, 10000};
	private final int[] LABELS_COUNTS = {25, 20, 10, 10, 10};
	private int xAxisScale;
	private JComponent background;
	private Graphics2D g2;
	private double[] signal;
	private float canvasWidth;
	private float canvasHeight;
	private final Insets signalMargin = new Insets(HORIZONT, VERTICAL, HORIZONT, VERTICAL);
	public final float ZOOM_Y_SIG = 0.5f;
	//public final float ZOOM_Y_COEF = 0.3f;//0.3
	private double zoomY;
	private boolean existERP;	
	private int cursorX = -1;
	private String captionValue;
	
	public SignalImage(JComponent background)
	{
		this.background = background;
		
		addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                cursorX = -1;
                refresh();
            }
        });
		
		addMouseMotionListener(new MouseMotionAdapter() {
			
	        @Override
	        public void mouseMoved(MouseEvent e) {
	        	cursorX = e.getX();
	        	refresh();
	        }
	
	    });
	}
	
	protected void paintComponent(Graphics g)
	{		
		g2 = (Graphics2D) g;
				
		paintAxis();
		paintSignal();
				
	}
	
	/**
	 * Metoda slouží k vykreslení interpolaèní køivky mezi funkèními hodnotami signálu.
	 */
	private void paintSignal()
	{		
		if(signal != null)
		{
			g2.setPaint(Color.BLACK);
			
			float step = (canvasWidth - signalMargin.left - signalMargin.right - LEFT) / (signal.length - 1);
			float x0 = signalMargin.left +LEFT;
			float x1 = step + signalMargin.left+LEFT;
			float y0 = 0;
			float y1 = 0;
			
			if(signal.length > 0)
				y1 = (float)(-signal[0] * zoomY + (canvasHeight / 2));
			
			for (int i = 1; i < signal.length; i++) {
	
				y0 = y1;
				
				y1 = (float)(-signal[i] * zoomY + (canvasHeight / 2));
				
				g2.draw(new Line2D.Float(x0, y0, x1, y1));
				
				x0 = x1;
				x1 += step;
			}
			
			if(cursorX >= LEFT && cursorX < canvasWidth)
			{
				if(existERP)
					g2.setPaint(Color.YELLOW);
				else
					g2.setPaint(Color.RED);
				
				captionValue = getValue(cursorX);
				g2.drawString(captionValue, cursorX-(captionValue.length()*6), 20);
				g2.drawLine(cursorX, signalMargin.top, cursorX, getHeight() - signalMargin.bottom - 1);
			}
			//paintHistogram();
		}
	}
	
	/*private void paintHistogram()
	{
		g2.setPaint(Color.BLACK);
		
		float step = (canvasWidth - signalMargin.left - signalMargin.right) / (signal.length - 1);
		float x = signalMargin.left;
		float y0 = (canvasHeight / 2);
		float y;
		
		
		for (int i = 0; i < signal.length; i++) {			
			
			y = (float)(-signal[i] * ZOOM_Y_COEF + (canvasHeight / 2));
			
			g2.draw(new Line2D.Float(x, y0, x, y));
			
			x += step;
		}
	}*/
	
	private void paintAxis()
	{
		if(existERP)
			g2.setPaint(Color.RED);		
		else
			g2.setPaint(Color.WHITE);
		
		g2.fill(new Rectangle2D.Float(signalMargin.left, signalMargin.top, 
				canvasWidth, canvasHeight));	
		
		g2.setPaint(Color.LIGHT_GRAY);
		
		float yAxis1 = signalMargin.top;
		float xAxis1 = signalMargin.left+LEFT-1;
		float yAxis2 = canvasHeight - signalMargin.bottom;
		float xAxis2 = xAxis1 - HORIZONT;
		float yAxis3 = canvasHeight/2;
		float xAxis3 = canvasWidth-signalMargin.right;
		
		g2.draw(new Line2D.Float(xAxis1, yAxis1, xAxis1, yAxis2));
		g2.draw(new Line2D.Float(xAxis2, yAxis3, xAxis3, yAxis3));
		
		int countMarker = 0;
		float markerCoef = 0;

		for(int i = 0; i < XAXIS_SCALES.length; i++)
		{
			markerCoef = (float) (signal.length/(double)XAXIS_SCALES[i]);
			countMarker = (int)Math.round(markerCoef);
			xAxisScale = XAXIS_SCALES[i];
			
			if(countMarker <= LABELS_COUNTS[i])
				break;
		}
			
		float xstep = (float) ((canvasWidth - LEFT) / (float)markerCoef);
		
		float x = xAxis1;
		
		for(int i = 0;i < countMarker;i++)
		{
			g2.setPaint(Color.LIGHT_GRAY);
			g2.draw(new Line2D.Float(x, yAxis2, x, yAxis3));
			g2.setPaint(Color.GRAY);
			g2.drawString(""+i*xAxisScale, x + 2, yAxis2+5);
			x += xstep;
		}
		
		g2.drawString("t", canvasWidth - signalMargin.left - INDENT_LABELS, canvasHeight);
		
	}
	
	public void setValues(double[] values)
	{
		this.signal = values;
		double highestValue = Double.MIN_VALUE;
		double lowestValue = Double.MAX_VALUE;
		Dimension size = background.getPreferredSize(); 
		canvasWidth = (float) size.getWidth() - signalMargin.left - signalMargin.right;
		canvasHeight = (float) size.getHeight() - signalMargin.top - signalMargin.bottom;
		double height = canvasHeight - 15;
		
		for(int i=0;i < signal.length;i++)
		{
			if(signal[i] > highestValue)
				highestValue = signal[i];
			
			if(signal[i] < lowestValue)
				lowestValue = signal[i];
		}
		
		if(Math.abs(highestValue) > Math.abs(lowestValue))
			zoomY = Math.abs((height/2)/highestValue);
		else
			zoomY = Math.abs((height/2)/lowestValue);
				
		this.repaint();
		
	}
	
	public String getValue(int cursorValueX)
	{
		double coef = this.signal.length/(canvasWidth-LEFT);
		int index = (int)((cursorValueX-LEFT)*coef);

		String[] valueString = (""+this.signal[index]).split("[.]");
		
		if(valueString[1].length() > COUNT_FRACT)
			valueString[1] = valueString[1].substring(0, COUNT_FRACT);
		
		return valueString[0]+"."+valueString[1];
	}
	
	public void setDetectionERP(boolean existERP)
	{
		this.existERP = existERP;	
		this.repaint();
	}
	
	private void refresh()
	{
		this.repaint();
	}
	
}
