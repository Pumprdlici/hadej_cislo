package icp.application;

import icp.Const;
import icp.algorithm.cwt.CWT;
import icp.algorithm.dwt.DWT;
import icp.algorithm.math.Mathematic;
import icp.data.Buffer;
import icp.data.Header;
import icp.data.InvalidFrameIndexException;

import java.util.ArrayList;

public class Transformation extends Thread implements ProgressInterface
{
	private final int ROWS_COLUMNS = 4;
	private ArrayList<double[][]> elementsData;
	private ArrayList<double[][]> transformatedEpochsDWT;
	private ArrayList<ArrayList<double[][]>> transformatedEpochsCWT;
	private SignalsSegmentation signalsSegmentation;
	private SessionManager appCore;
	private Buffer buffer;
	private Header header;
	private int[] channelsIndexes;
	private String[] channelsNames;
	private boolean[][] detectedERP;
	private ArrayList<ArrayList<double[]>> highestCoeficientsDWT;
	private ArrayList<ArrayList<int[]>> indexesHighestCoeficientsDWT;
	private ArrayList<ArrayList<double[]>> highestCoeficientsCWT;
	private ArrayList<ArrayList<int[]>> indexesHighestCoeficientsCWT;
	private int[][] positionHighestCoeficients;
	private int[] positiveDetectionInChannels;
	private int totalPositiveDetection;
	private boolean enabledWT;
	private int actualTransform;
	private DWT dwt;
	private CWT cwt;
	private double progressUnit;
	
	public Transformation(SessionManager app)
	{
		this.appCore = app;
		this.signalsSegmentation = appCore.getSignalsSegmentation();
		actualTransform = 0;
		enabledWT = true;
	}
	
	public void dwt() throws InvalidFrameIndexException
	{
		transformatedEpochsDWT = new ArrayList<double[][]>();
		highestCoeficientsDWT = new ArrayList<ArrayList<double[]>>();
		indexesHighestCoeficientsDWT = new ArrayList<ArrayList<int[]>>();
		double[][] transformedEpochs;
		ArrayList<double[]> highestCoeficients;
		ArrayList<int[]> indexesHighestCoeficients;
		
		for(int i = 0; i < elementsData.size();i++)
		{			
			transformedEpochs = new double[elementsData.get(i).length][elementsData.get(i)[0].length];
			highestCoeficients = new ArrayList<double[]>();
			indexesHighestCoeficients = new ArrayList<int[]>();
			
			for(int j = 0; j < elementsData.get(i).length;j++)
			{
				if(!enabledWT)
					return;
				
				transformedEpochs[j] = dwt.transform(elementsData.get(i)[j]);
				highestCoeficients.add(dwt.getHighestCoeficients());
				indexesHighestCoeficients.add(dwt.getIndexesHighestCoeficients());
				sendProgressUnits();
			}
			
			transformatedEpochsDWT.add(transformedEpochs);
			highestCoeficientsDWT.add(highestCoeficients);
			indexesHighestCoeficientsDWT.add(indexesHighestCoeficients);
		}
	}
	
	public void cwt() throws InvalidFrameIndexException
	{
		cwt.setProgressInterface(this);
		transformatedEpochsCWT = new ArrayList<ArrayList<double[][]>>();
		highestCoeficientsCWT = new ArrayList<ArrayList<double[]>>();
		indexesHighestCoeficientsCWT = new ArrayList<ArrayList<int[]>>();
		ArrayList<double[][]> transformedEpochs;
		ArrayList<double[]> highestCoeficients;
		ArrayList<int[]> indexesHighestCoeficients;
		
		for(int i = 0; i < elementsData.size();i++)
		{						
			transformedEpochs = new ArrayList<double[][]>();
			highestCoeficients = new ArrayList<double[]>();
			indexesHighestCoeficients = new ArrayList<int[]>();
			
			for(int j = 0; j < elementsData.get(i).length;j++)
			{
				cwt.transform(elementsData.get(i)[j]);
				transformedEpochs.add(cwt.getCwtDataReal());
				highestCoeficients.add(cwt.getHighestCoeficients());
				indexesHighestCoeficients.add(cwt.getIndexesHighestCoeficients());
			}
			
			transformatedEpochsCWT.add(transformedEpochs);
			highestCoeficientsCWT.add(highestCoeficients);
			indexesHighestCoeficientsCWT.add(indexesHighestCoeficients);
		}
		
	}
	
	private void calculationProgressUnits()
	{		
		if(actualTransform == Const.DWT)
		{
			this.progressUnit = Const.PROGRESS_MAX/ (double)(elementsData.size()*elementsData.get(0).length);
		}
		else
		{	
			double countScales = ((cwt.getMaxScale()-cwt.getMinScale())/cwt.getStepScale())+1;
			this.progressUnit = Const.PROGRESS_MAX/(elementsData.size()*elementsData.get(0).length*countScales);
		}
	}
	
	private void loadEpochValuesElements()
	{
		elementsData = new ArrayList<double[][]>();
		ArrayList<Element> elements = appCore.getAveraging().getElements();
		int count = elements.get(0).getRowsAndColumnsEpoch().length;
		double[][] epochsOfElements;
		

		for(int i = 0; i < elements.size();i++)
		{
			epochsOfElements = new double[count][0];
			
			for(int j = 0; j < epochsOfElements.length;j++)
			{
				epochsOfElements[j] = elements.get(i).getRowsAndColumnsEpoch()[j]; 
			}
			
			elementsData.add(epochsOfElements);
		}		
	}
	
	
	
	public void detectErp(int start, int end, int indexScaleWavelet)
    {
    	totalPositiveDetection = 0;
    	detectedERP = new boolean[elementsData.size()][elementsData.get(0).length];
    	positiveDetectionInChannels = new int[elementsData.size()];

    	
    	if(actualTransform == Const.DWT)
    		detectERPofDWT(start, end, indexScaleWavelet);
    	else
    		detectERPofCWT(start, end, indexScaleWavelet);
    	    	
    }
	
	public void detectERPofDWT(int start, int end, int indexScaleWavelet)
    {
    	double factor = Math.pow(Mathematic.CONST_2, indexScaleWavelet+1);
		int startErp =(int) (start / factor);
		int endErp = (int) (end / factor);

		int[] indexesHighestWCForElementsRows = findIndexesHighestWaveletCoefForRowsDWT(startErp, endErp);
		int[] indexesHighestWCForElementsColumns = findIndexesHighestWaveletCoefForColumnsDWT(startErp, endErp);
		
		for(int i = 0; i < elementsData.size();i++)
		{
			detectedERP[i][indexesHighestWCForElementsRows[i]] = true;
			detectedERP[i][indexesHighestWCForElementsColumns[i]] = true;	
		}
    }
	
	public int[] findIndexesHighestWaveletCoefForRowsDWT(int start, int end)
	{
		int[] indexesHighestCoef = new int[transformatedEpochsDWT.size()];
		double waveletCoef; 
		int index;
		
		for(int i = 0;i < indexesHighestCoef.length;i++)
		{
			waveletCoef = -Double.MAX_VALUE;
			index = 0;
			
			for(int j = 0;j < ROWS_COLUMNS;j++)
			{
				for(int k = start;k < end;k++)
				{
					if(waveletCoef < transformatedEpochsDWT.get(i)[j][k])
					{
						waveletCoef = transformatedEpochsDWT.get(i)[j][k];
						index = j;
					}
				}
			}
			
			indexesHighestCoef[i] = index;
		}
		
		return indexesHighestCoef;
	}
	
	public int[] findIndexesHighestWaveletCoefForColumnsDWT(int start, int end)
	{
		int[] indexesHighestCoef = new int[transformatedEpochsDWT.size()];
		double waveletCoef; 
		int index;
		
		for(int i = 0;i < indexesHighestCoef.length;i++)
		{
			waveletCoef = -Double.MAX_VALUE;
			index = 0;
			
			for(int j = ROWS_COLUMNS;j < transformatedEpochsDWT.get(i).length;j++)
			{
				for(int k = start;k < end;k++)
				{
					if(waveletCoef < transformatedEpochsDWT.get(i)[j][k])
					{
						waveletCoef = transformatedEpochsDWT.get(i)[j][k];
						index = j;
					}
				}
			}
			
			indexesHighestCoef[i] = index;
		}
		
		return indexesHighestCoef;
	}
	
	
	public void detectERPofCWT(int start, int end, int indexScaleWavelet)
    {
		int[] indexesHighestWCForElementsRows = findIndexesHighestWaveletCoefForRowsCWT(start, end, indexScaleWavelet);
		int[] indexesHighestWCForElementsColumns = findIndexesHighestWaveletCoefForColumnsCWT(start, end, indexScaleWavelet);
		
		for(int i = 0; i < elementsData.size();i++)
		{
			detectedERP[i][indexesHighestWCForElementsRows[i]] = true;
			detectedERP[i][indexesHighestWCForElementsColumns[i]] = true;	
		}
    }
	
	public int[] findIndexesHighestWaveletCoefForRowsCWT(int start, int end, int indexScale)
	{
		int[] indexesHighestCoef = new int[transformatedEpochsCWT.size()];
		double waveletCoef; 
		int index;
		
		for(int i = 0;i < indexesHighestCoef.length;i++)
		{
			waveletCoef = -Double.MAX_VALUE;
			index = 0;
			
			for(int j = 0;j < ROWS_COLUMNS;j++)
			{
				for(int k = start;k < end;k++)
				{
					if(waveletCoef < transformatedEpochsCWT.get(i).get(j)[indexScale][k])
					{
						waveletCoef = transformatedEpochsCWT.get(i).get(j)[indexScale][k];
						index = j;
					}
				}
			}
			
			indexesHighestCoef[i] = index;
		}
		
		return indexesHighestCoef;
	}
	
	public int[] findIndexesHighestWaveletCoefForColumnsCWT(int start, int end, int indexScale)
	{
		int[] indexesHighestCoef = new int[transformatedEpochsCWT.size()];
		double waveletCoef; 
		int index;
		
		for(int i = 0;i < indexesHighestCoef.length;i++)
		{
			waveletCoef = -Double.MAX_VALUE;
			index = 0;
			
			for(int j = ROWS_COLUMNS;j < transformatedEpochsCWT.get(i).size();j++)
			{
				for(int k = start;k < end;k++)
				{
					if(waveletCoef < transformatedEpochsCWT.get(i).get(j)[indexScale][k])
					{
						waveletCoef = transformatedEpochsCWT.get(i).get(j)[indexScale][k];
						index = j;
					}
				}
			}
			
			indexesHighestCoef[i] = index;
		}
		
		return indexesHighestCoef;
	}
    
	public void stopWT()
	{
		enabledWT = false;
		
		if(cwt != null)
			cwt.setTransform(enabledWT);
	}
	
	@Override
	public void run()
	{
		try
		{
			loadEpochValuesElements();
			calculationProgressUnits();
			
			if(actualTransform == Const.DWT)
				dwt();
			else
				cwt();
			
			if(enabledWT)
				appCore.sendWtMessage();
		}
		catch (InvalidFrameIndexException e)
		{
			appCore.sendWtErrorMessage();
		}
	}
	
	public void sendProgressUnits()
	{
		appCore.sendProgressUnits(progressUnit);
	}
	
	public void setChannelsIndexes(int[] indexes)
	{
		channelsIndexes = indexes;	
		
		channelsNames = new String[channelsIndexes.length];
		
		for(int i = 0; i < channelsNames.length;i++)
		{
			channelsNames[i] = header.getChannels().get(channelsIndexes[i]).getName();
		}
	}
	
	public void setBuffer(Buffer buffer)
	{
		this.buffer = buffer;	
	}
	
	public void setHeader(Header header)
	{
		this.header = header;	
	}
	
	public String[] getChannelsNames()
	{
		return channelsNames;	
	}
	
	public boolean[][] getDetectionERP()
	{
		return detectedERP;	
	}
	
	public int[][] getPositionHighestCoeficients()
	{
		return positionHighestCoeficients;	
	}
	
	public int getTotalPositiveDetection()
	{
		return totalPositiveDetection;	
	}
	
	public int[] getPositiveDetectionInChannels()
	{
		return positiveDetectionInChannels;	
	}
	
	public ArrayList<ArrayList<double[]>> getHighestCoeficientsDWT()
	{
		return highestCoeficientsDWT;	
	}
	
	public ArrayList<ArrayList<double[]>> getHighestCoeficientsCWT()
	{
		return highestCoeficientsCWT;	
	}
	
	public ArrayList<ArrayList<int[]>> getIndexesHighestCoeficientsDWT()
	{
		return indexesHighestCoeficientsDWT;	
	}
	
	public ArrayList<ArrayList<int[]>> getIndexesHighestCoeficientsCWT()
	{
		return indexesHighestCoeficientsCWT;	
	}
	
	public int getActualTransform()
	{
		return actualTransform;	
	}
	
	public ArrayList<double[][]> getElementsData()
	{
		return elementsData;	
	}
	
	public ArrayList<double[][]> getTransformedEpochsDWT()
	{
		return transformatedEpochsDWT;	
	}
	
	public ArrayList<ArrayList<double[][]>> getTransformedEpochsCWT()
	{
		return transformatedEpochsCWT;	
	}
	
	public DWT getDWT()
	{
		return dwt;	
	}
	
	public CWT getCWT()
	{
		return cwt;	
	}
	
	public boolean setDWT(DWT dwt)
	{
		actualTransform = Const.DWT;
		this.dwt = dwt;	
		
		if(Mathematic.newMajorNumberOfPowerBase2(signalsSegmentation.getStartEpoch() 
			+ signalsSegmentation.getEndEpoch()) < dwt.getWavelet().getIWaveletArray().length)
			return false;
		else
			return true;
	}
	
	public void setCWT(CWT cwt)
	{
		actualTransform = Const.CWT;
		this.cwt = cwt;	
	}
}
