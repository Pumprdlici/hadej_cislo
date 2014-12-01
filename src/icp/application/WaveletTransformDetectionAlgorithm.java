package icp.application;

import icp.Const;
import icp.algorithm.cwt.CWT;
import icp.algorithm.cwt.wavelets.MexicanHat;
import icp.algorithm.cwt.wavelets.WaveletCWT;

import java.util.ArrayList;
import java.util.Arrays;

public class WaveletTransformDetectionAlgorithm extends Thread implements ProgressInterface
{
	private final int WAVELET_SCALE = 60, ROWS_COLUMNS = 4;
	private final int START = 300, END = 400, WAVELET_COEF = 20;
	private SessionManager appCore;
	private final WaveletCWT wavelet;
	private ArrayList<Element> elements;
	private ArrayList<double[][]> waveletCoeficientsOfElements;
	private ArrayList<double[]> highestCoeficientsOfElements;
	private ArrayList<double[]> highestCoefInIntervalOfElements;
	private ArrayList<int[]> indexesHighestCoefInIntervalOfElements;
	private int[] indexesHighestWCForElementsRows;
	private int[] indexesHighestWCForElementsColumns;
	private CWT cwt;
	private double progressUnit;
	private boolean enabledDetection;
	
	public WaveletTransformDetectionAlgorithm(SessionManager appCore, ArrayList<Element> elements)
	{
		this.appCore = appCore;
		this.elements = elements;
		wavelet = new MexicanHat();
		cwt = new CWT(WAVELET_SCALE, WAVELET_SCALE, 1, wavelet);
		cwt.setProgressInterface(this);
		enabledDetection = true;
	}
	
	/**
     * Vypoèítává jednotku progress baru pro jednotlivé typy waveletové transformace.
     */
	private void calculationProgressUnits()
	{		
		double countScales = ((cwt.getMaxScale()-cwt.getMinScale())/cwt.getStepScale())+1;
		this.progressUnit = Const.PROGRESS_MAX/(elements.size()*elements.get(0).getRowsAndColumnsEpoch().length*countScales);
		
	}
	
	private void detection()
	{
		waveletCoeficientsOfElements = new ArrayList<double[][]>();
		highestCoeficientsOfElements = new ArrayList<double[]>();
		highestCoefInIntervalOfElements = new ArrayList<double[]>();
		indexesHighestCoefInIntervalOfElements = new ArrayList<int[]>();
		indexesHighestWCForElementsRows = new int[waveletCoeficientsOfElements.size()];
		indexesHighestWCForElementsColumns = new int[waveletCoeficientsOfElements.size()];
		double[] highestCoefInInterval;
		int[] indexesHighestCoefInInterval;
		
		System.out.println("Elements size: " + elements.size());
		
		
		for(int i = 0; i < elements.size();i++)
		{
			System.out.println("Element: " + i + ": " + elements.get(i).getRowsAndColumnsEpoch().length);
			waveletTransformOfElement(elements.get(i));
			highestCoefInInterval = new double[Const.ROWS_COLS_COUNT_OF_ELEMENT];
			Arrays.fill(highestCoefInInterval, -Double.MAX_VALUE);
			indexesHighestCoefInInterval = new int[Const.ROWS_COLS_COUNT_OF_ELEMENT];
			highestCoefInIntervalOfElements.add(highestCoefInInterval);
			indexesHighestCoefInIntervalOfElements.add(indexesHighestCoefInInterval);
		}
		
		findIndexesHighestWaveletCoefForRows(waveletCoeficientsOfElements);
		findIndexesHighestWaveletCoefForColumns(waveletCoeficientsOfElements);
		
		
		for(int i = 0; i < elements.size();i++)
		{
			if(!enabledDetection)
				return;
				
			elements.get(i).setDetectedRow(indexesHighestWCForElementsRows[i]);
			elements.get(i).setDetectedColumn(indexesHighestWCForElementsColumns[i]);
			
		}		
	}
	
	public void waveletTransformOfElement(Element element)
	{
		double[][] rowsAndColumns = element.getRowsAndColumnsEpoch();
		double[][] waveletCoeficients = new double[rowsAndColumns.length][rowsAndColumns[0].length];
		double[] highestCoeficients = new double[rowsAndColumns.length];
		
		for(int i = 0; i < rowsAndColumns.length;i++)
		{
			cwt.transform(rowsAndColumns[i]);
			waveletCoeficients[i] = cwt.getCwtDataReal()[0];
			highestCoeficients[i] = cwt.getHighestCoeficients()[0];
		}
		
		waveletCoeficientsOfElements.add(waveletCoeficients);
		highestCoeficientsOfElements.add(highestCoeficients);
	}
	
	public void findIndexesHighestWaveletCoefForRows(ArrayList<double[][]> waveletCoeficientsOfElements)
	{
		int[] indexesHighestCoef = new int[waveletCoeficientsOfElements.size()];
		double waveletCoef; 
		int index;
		
		for(int i = 0;i < indexesHighestCoef.length;i++)
		{
			waveletCoef = -Double.MAX_VALUE;
			index = 0;
			
			for(int j = 0;j < ROWS_COLUMNS;j++)
			{
				for(int k = START;k < END;k++)
				{
					if(waveletCoef < waveletCoeficientsOfElements.get(i)[j][k])
					{
						waveletCoef = waveletCoeficientsOfElements.get(i)[j][k];
						index = j;
					}
					
					if(highestCoefInIntervalOfElements.get(i)[j] < waveletCoeficientsOfElements.get(i)[j][k])
					{
						highestCoefInIntervalOfElements.get(i)[j] = waveletCoeficientsOfElements.get(i)[j][k];
						indexesHighestCoefInIntervalOfElements.get(i)[j] = k;
					}
				}
			}
			
			indexesHighestCoef[i] = index;
		}
		
		indexesHighestWCForElementsRows = indexesHighestCoef;
	}
	
	public void findIndexesHighestWaveletCoefForColumns(ArrayList<double[][]> waveletCoeficientsOfElements)
	{
		int[] indexesHighestCoef = new int[waveletCoeficientsOfElements.size()];
		double waveletCoef; 
		int index;
		
		for(int i = 0;i < indexesHighestCoef.length;i++)
		{
			waveletCoef = -Double.MAX_VALUE;
			index = 0;
			
			for(int j = ROWS_COLUMNS;j < waveletCoeficientsOfElements.get(i).length;j++)
			{
				for(int k = START ;k < END;k++)
				{
					if(waveletCoef < waveletCoeficientsOfElements.get(i)[j][k])
					{
						waveletCoef = waveletCoeficientsOfElements.get(i)[j][k];
						index = j - ROWS_COLUMNS;
					}
					
					if(highestCoefInIntervalOfElements.get(i)[j] < waveletCoeficientsOfElements.get(i)[j][k])
					{
						highestCoefInIntervalOfElements.get(i)[j] = waveletCoeficientsOfElements.get(i)[j][k];
						indexesHighestCoefInIntervalOfElements.get(i)[j] = k;
					}
				}
			}
			
			indexesHighestCoef[i] = index;
		}
		
		indexesHighestWCForElementsColumns = indexesHighestCoef;
	}
	
	@Override
	public void run()
	{
		calculationProgressUnits();
		detection();
		
		if(enabledDetection)
			appCore.sendDetectionMessage();
	}
	
	public void stopWT()
	{
		enabledDetection = false;
		
		if(cwt != null)
			cwt.setTransform(enabledDetection);
	}

	@Override
	public void sendProgressUnits()
	{
		appCore.sendProgressUnits(progressUnit);		
	}
	
	public ArrayList<double[][]> getWaveletCoeficientsOfElements()
	{
		return waveletCoeficientsOfElements;
	}
	
	public ArrayList<double[]> getHighestCoeficientsOfElements()
	{
		return highestCoeficientsOfElements;
	}
	
	public ArrayList<double[]> getHighestCoefInIntervalOfElements()
	{
		return highestCoefInIntervalOfElements;
	}
	
	public ArrayList<int[]> getIndexesHighestCoefInIntervalOfElements()
	{
		return indexesHighestCoefInIntervalOfElements;
	}
	
	public CWT getCwt()
	{
		return cwt;
	}
	
	public int getStartIntervalDetection()
	{
		return START;
	}
	
	public int getEndIntervalDetection()
	{
		return END;
	}
}
