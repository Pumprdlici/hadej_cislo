package icp.application;

import java.util.ArrayList;
import java.util.List;

import icp.Const;

public class Element
{
	public static final char[][] calculatorChars= {	{'1','2','3','+'},
													{'4','5','6','-'},
													{'7','8','9','*'},
													{'0','=','c','/'}};
	private double[][] rowsAndColumnsEpochAverages;
	private List<double[][]>[] rowsAndColumnsRawData; // array of A cols / rows for list of B channels x C time samples 
	private int detectedRow;
	private int detectedColumn;
	private int channelIndex;
	
	
	
	public Element()
	{
		rowsAndColumnsEpochAverages = new double[Const.ROWS_COLS_COUNT_OF_ELEMENT][0];
		rowsAndColumnsRawData = new List[Const.ROWS_COLS_COUNT_OF_ELEMENT];
		//for (int i = 0; i < Const.ROWS_COLS_COUNT_OF_ELEMENT; i++)
		//	rowsAndColumnsRawData[i] = new ArrayList<double[][]>();
	}
	
	public void setRowsAndColumnsEpoch(int index, double[] epochs)
	{
		rowsAndColumnsEpochAverages[index] = epochs;
	}
	
	public void setRowsAndColumnsEpoch(double[][] epochs)
	{
		rowsAndColumnsEpochAverages = epochs;
	}
	
	
	
	
	public double[][] getRowsAndColumnsEpoch()
	{
		return rowsAndColumnsEpochAverages;
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

	public List<double[][]>[] getRowsAndColumnsRawData() {
		return rowsAndColumnsRawData;
	}

	public void setRowsAndColumnsRawData(
			int index, List<double[][]> rowsAndColumnsRawData) {
		this.rowsAndColumnsRawData[index] = rowsAndColumnsRawData;
	}
	
	
}
