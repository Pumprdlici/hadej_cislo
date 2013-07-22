package icp.application;

import icp.Const;

public class Element
{
	public static final char[][] calculatorChars= {	{'1','2','3','+'},
													{'4','5','6','-'},
													{'7','8','9','*'},
													{'0','=','c','/'}};
	private double[][] rowsAndColumnsEpoch;
	private int detectedRow;
	private int detectedColumn;
	private int channelIndex;
	
	public Element()
	{
		rowsAndColumnsEpoch = new double[Const.ROWS_COLS_COUNT_OF_ELEMENT][0];
	}
	
	public void setRowsAndColumnsEpoch(int index, double[] epochs)
	{
		rowsAndColumnsEpoch[index] = epochs;
	}
	
	public void setRowsAndColumnsEpoch(double[][] epochs)
	{
		rowsAndColumnsEpoch = epochs;
	}
	
	public double[][] getRowsAndColumnsEpoch()
	{
		return rowsAndColumnsEpoch;
	}
	
	public char getDetectedChar()
	{
		return calculatorChars[detectedRow][detectedColumn];
	}
	
	public void setDetectedRow(int detectedRow)
	{
		this.detectedRow = detectedRow;
	}
	
	public int getDetectedRow()
	{
		return detectedRow;
	}
	
	public void setDetectedColumn(int detectedColumn)
	{
		this.detectedColumn = detectedColumn;
	}
	
	public int getDetectedColumn()
	{
		return detectedColumn;
	}
	
	public void setChannelIndex(int channelIndex)
	{
		this.channelIndex = channelIndex;
	}
	
	public int getChannelIndex()
	{
		return channelIndex;
	}
}
