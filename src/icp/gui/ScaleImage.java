package icp.gui;

import icp.Const;
import icp.algorithm.cwt.CWT;
import icp.algorithm.math.Mathematic;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

import javax.swing.*;

public class ScaleImage extends JPanel
{
	private static final long serialVersionUID = 1L;
	private final DialogInterface scaleDialog;
	private final int VERTICAL = 1, HORIZONT = 5, MARK_LENGTH = 5, X_COEF_DIF = 5, X_TIME_DIF = 2; 
	private final int LEFT = 70, BOTTOM = 20, STR_MARGIN = 2, STR_LOC = 4, INDENT_LABELS = 5, COUNT_FRACT = 6; 	
	private final int[] XAXIS_SCALES = {1, 10, 100, 1000, 10000};
	private final int[] LABELS_COUNTS = {25, 20, 10, 10, 10};
	private int xAxisScale;
	private JComponent background;
	private Graphics2D g2;
	private double[] signal;
	private double[][] signals;
	private float canvasWidth;
	private float canvasHeight;
	private final Insets scalogramMargin = new Insets(HORIZONT, VERTICAL, HORIZONT, VERTICAL);
	private double[]  colorCoeficient;
	private static final int CONST_COLOR = 255;
	private double minScale, scaleStep, scale;
	private int actualWT;
	private int cursorX = -1;
	private int cursorY = -1;
	
	
	public ScaleImage(JComponent background, DialogInterface scaleDialog)
	{
		this.scaleDialog = scaleDialog;
		this.background = background;
		
		addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                cursorX = -1;
                cursorY = -1;
                sendCursorPosition(cursorX, cursorY);
            }
        });
		
		addMouseMotionListener(new MouseMotionAdapter() {
			
	        @Override
	        public void mouseMoved(MouseEvent e) {
	            cursorX = e.getX();
	            cursorY = e.getY();
	            sendCursorPosition(cursorX, cursorY);
	        }
	    });
	}
	
	protected void paintComponent(Graphics g)
	{
		Dimension size = background.getPreferredSize(); 
		canvasWidth = (float) size.getWidth() - scalogramMargin.left - scalogramMargin.right;
		canvasHeight = (float) size.getHeight() - scalogramMargin.top - scalogramMargin.bottom;
		
		
		g2 = (Graphics2D) g;
		
		g2.setPaint(Color.BLACK); // nastavení barvy pozadí (plátna), na které se signál vykresluje
		
		g2.fill(new Rectangle2D.Double(scalogramMargin.left, scalogramMargin.top,
				canvasWidth, canvasHeight));
		
		if(actualWT == Const.DWT)
		{
			paintScalogramDWT();
			paintAxisDWT();
		}
		else
		{
			paintScalogramCWT();
			paintAxisCWT();
		}
	}
	
	private void paintScalogramDWT()
	{		
		float xstep;
		float ystep = (float) ((canvasHeight- scalogramMargin.bottom - scalogramMargin.top - BOTTOM) / colorCoeficient.length);
		float x;
		float y = scalogramMargin.top + (ystep*colorCoeficient.length);
		int start = signal.length/Mathematic.CONST_2;
		int end = signal.length;
		int level = Mathematic.CONST_2;
		int color, i, j;
		
		for(i = colorCoeficient.length - 1;i >= 0;i--)
		{
			xstep = (float) ((canvasWidth - scalogramMargin.left - scalogramMargin.right - LEFT) / (signal.length/level));
			x = scalogramMargin.left + LEFT;
			
			for (j = start; j < end; j++) {			
				
				color = (int)((Math.abs(signal[j])*CONST_COLOR)/colorCoeficient[i]);
				g2.setPaint(new Color(color,color,color));							
				g2.fill(new Rectangle2D.Float(x, y - ystep, xstep, ystep));
				
				x += xstep;
			}
			
			start /= Mathematic.CONST_2;
			end /= Mathematic.CONST_2;
			level *= Mathematic.CONST_2;
			y -= ystep;
		}
	}
	
	private void paintAxisDWT()
	{
		g2.setPaint(Color.WHITE);
		
		float ystep = (float) ((canvasHeight- scalogramMargin.bottom - scalogramMargin.top - BOTTOM) / colorCoeficient.length);
		float yAxis1 = scalogramMargin.top;
		float xAxis1 = scalogramMargin.left + LEFT - 1;
		float yAxis2 = canvasHeight - BOTTOM + MARK_LENGTH;
		float xAxis2 = xAxis1 - MARK_LENGTH - (LEFT/2);
		float yAxis3 = canvasHeight - BOTTOM - scalogramMargin.bottom;
		float xAxis3 = canvasWidth - scalogramMargin.right;
		float fontLocation = g2.getFont().getSize();
		int colorCoefStep = 1;
		
		g2.drawString("L", INDENT_LABELS, fontLocation + INDENT_LABELS);
		g2.drawString("t", canvasWidth - scalogramMargin.left - INDENT_LABELS, canvasHeight);
		
		if((int)(ystep - (g2.getFont().getSize() + STR_MARGIN)) < 0)
		{
			double scaleCoef = (g2.getFont().getSize() + STR_MARGIN) / ystep;
			
			if(scaleCoef > (int) scaleCoef)
				colorCoefStep = ((int) scaleCoef) + 1;
			else
				colorCoefStep = (int) scaleCoef;
		}
		
		g2.draw(new Line2D.Float(xAxis1, yAxis1, xAxis1, yAxis2));
		g2.draw(new Line2D.Float(xAxis1, yAxis3, xAxis3, yAxis3));
		
		//osa y
		float y = scalogramMargin.top + ystep/2;
		
		for(int i = colorCoeficient.length; i > 0; i--)
		{
			if(i % colorCoefStep == 0)
			{
				g2.draw(new Line2D.Float(xAxis2, y, xAxis1, y));
				g2.drawString(""+i, xAxis2 - X_COEF_DIF, y + fontLocation);
			}
			
			y += ystep;
		}
		
		//osa x
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
		
		float xstep = (float) ((canvasWidth - LEFT) / markerCoef);
		float x = xAxis1;
		
		for(int i = 0;i < countMarker;i++)
		{
			g2.draw(new Line2D.Float(x, yAxis2, x, yAxis3));
			g2.drawString(""+i*xAxisScale, x + X_TIME_DIF, yAxis2 + STR_LOC);
			x += xstep;
		}
		
	}
	
	private void paintScalogramCWT()
	{
		g2.setPaint(Color.BLACK);
		
		float xstep;
		float ystep = (float) ((canvasHeight- scalogramMargin.bottom - scalogramMargin.top - BOTTOM) / colorCoeficient.length);
		float x;
		float y = scalogramMargin.top + ystep*colorCoeficient.length + 1;
		int index;
		double baseColor;
		int finalColor;
		
		for(int i = 0;i < colorCoeficient.length ;i++)
		{
			xstep = (float) ((canvasWidth - scalogramMargin.left - scalogramMargin.right - LEFT) / 
					signals[i].length);
			x = scalogramMargin.left + LEFT;
			
			for (index = 0; index < signals[i].length; index++) {			
				
				baseColor = Math.abs(signals[i][index])/colorCoeficient[i];
				finalColor = (int) (baseColor*CONST_COLOR);
				g2.setPaint(new Color(finalColor,finalColor,finalColor));
							
				g2.fill(new Rectangle2D.Float(x, y - ystep, xstep, ystep));
				
				x += xstep;
			}
			y -= ystep;
		}
	}
	
	private void paintAxisCWT()
	{
		g2.setPaint(Color.WHITE);
		
		float ystep = (float) ((canvasHeight- scalogramMargin.bottom - scalogramMargin.top - BOTTOM) / colorCoeficient.length);
		float yAxis1 = scalogramMargin.top;
		float xAxis1 = scalogramMargin.left + LEFT - 1;
		float yAxis2 = canvasHeight - BOTTOM + MARK_LENGTH;
		float xAxis2 = xAxis1 - MARK_LENGTH - (LEFT/2);
		float yAxis3 = canvasHeight - BOTTOM - scalogramMargin.bottom;
		float xAxis3 = canvasWidth - scalogramMargin.right;
		float fontLocation = g2.getFont().getSize();
		int colorCoefStep = 1;
		scale = minScale;
		
		g2.drawString("S", INDENT_LABELS, fontLocation + INDENT_LABELS);
		g2.drawString("t", canvasWidth - scalogramMargin.left - INDENT_LABELS, canvasHeight);
		
		if((int)(ystep - (g2.getFont().getSize() + STR_MARGIN)) < 0)
		{
			double scaleCoef = (g2.getFont().getSize() + STR_MARGIN) / ystep;
			
			if(scaleCoef > (int) scaleCoef)
				colorCoefStep = ((int) scaleCoef) + 1;
			else
				colorCoefStep = (int) scaleCoef;
		}
		
		g2.draw(new Line2D.Float(xAxis1, yAxis1, xAxis1, yAxis2));
		g2.draw(new Line2D.Float(xAxis1, yAxis3, xAxis3, yAxis3));
		
		//osa y
		float y = scalogramMargin.top + ystep*colorCoeficient.length - (ystep/2);
		
		for(int i = 1; i <= colorCoeficient.length; i++)
		{
			if(i % colorCoefStep == 0)
			{
				g2.draw(new Line2D.Float(xAxis2, y, xAxis1, y));
				g2.drawString(""+scale, xAxis2 - X_COEF_DIF, y + fontLocation);
			}
			
			scale += scaleStep;
			y -= ystep;
		}
		
		//osa x
		int countMarker = 0;
		float markerCoef = 0;
		
		for(int i = 0; i < XAXIS_SCALES.length; i++)
		{
			markerCoef = (float) (signals[0].length/(double)XAXIS_SCALES[i]);
			countMarker = (int)Math.round(markerCoef);
			xAxisScale = XAXIS_SCALES[i];
			
			if(countMarker <= LABELS_COUNTS[i])
				break;
		}
		
		float xstep = (float) ((canvasWidth - LEFT) / markerCoef);
		float x = xAxis1;
		
		for(int i = 0;i < countMarker;i++)
		{
			g2.draw(new Line2D.Float(x, yAxis2, x, yAxis3));
			g2.drawString(""+i * xAxisScale, x + X_TIME_DIF, yAxis2 + STR_LOC);
			x += xstep;
		}
		
	}
	
	/**
	 * DWT
	 * 
	 * @param values
	 * @param levelOfDecomposition
	 */
	public void setValues(double[] values, double[] highestCoeficients)
	{
		this.actualWT = Const.DWT;
		this.signal = values;		
		this.colorCoeficient = highestCoeficients;
		
		this.repaint();		
	}
	
	
	/**
	 * CWT
	 * 
	 * @param values
	 * @param cwt
	 */
	public void setValues(double[][] values, double[] highestCoeficients, CWT cwt)
	{
		this.actualWT = Const.CWT;
		this.scaleStep = cwt.getStepScale();
		this.minScale = cwt.getMinScale();
		this.signals = values;
		this.colorCoeficient = highestCoeficients;
		
		this.repaint();		
	}
	
	private void sendCursorPosition(int x, int y)
	{
		if(x > LEFT && x < canvasWidth && y > scalogramMargin.top && y < canvasHeight-BOTTOM-scalogramMargin.bottom)
		{
			int yPosition;
			int xPosition;
			double value;
			
			if(actualWT == Const.DWT)
			{
				int level;
				int start;
				int end;
				yPosition = (colorCoeficient.length - 1)-(int)((y-scalogramMargin.top)/
						((canvasHeight - scalogramMargin.bottom - scalogramMargin.top - BOTTOM)/colorCoeficient.length));
				
				level = (int)Math.pow(Mathematic.CONST_2, yPosition+1);
				
				start = signal.length/level;
				end = 2*start;
				
				xPosition = (int)((x-LEFT)*((end-start)/(canvasWidth-LEFT)));
				
				value = signal[start+xPosition];
				
				String[] valueString = (""+value).split("[.]");
				
				if(valueString[1].length() > COUNT_FRACT)
					valueString[1] = valueString[1].substring(0, COUNT_FRACT);
				
				scaleDialog.setValueOfScalogram(valueString[0]+"."+valueString[1], 
						""+(yPosition+1), ""+(xPosition*level));
			}
			else
			{
				yPosition = (signals.length-1)-(int)((y-scalogramMargin.top)/
						((canvasHeight - scalogramMargin.bottom - scalogramMargin.top - BOTTOM)/signals.length));
				xPosition = (int)((x-LEFT)*(signals[0].length/(canvasWidth-LEFT)))-1;
				
				value = signals[yPosition][xPosition];
				
				String[] valueString = (""+value).split("[.]");
								
				if(valueString[1].length() > COUNT_FRACT)
					valueString[1] = valueString[1].substring(0, COUNT_FRACT);
				
				scaleDialog.setValueOfScalogram(valueString[0]+"."+valueString[1], 
						""+((yPosition*this.scaleStep)+this.minScale), ""+(xPosition+1));
			}
		}
		else
		{
			scaleDialog.clearValueOfScalogram();
		}
	}
		
}
