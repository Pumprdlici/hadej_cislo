package icp.aplication;

import icp.Const;
import icp.algorithm.cwt.CWT;
import icp.algorithm.dwt.DWT;
import icp.algorithm.math.Mathematic;
import icp.data.*;

import java.util.ArrayList;


public class Transformation extends Thread
{
	private ArrayList<double[][]> epochs;
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
	private boolean averaging, enabledWT;
	private int actualTransform;
	private DWT dwt;
	private CWT cwt;
	private double progressUnit;
	
	public Transformation(SessionManager app)
	{
		this.appCore = app;
		this.signalsSegmentation = appCore.getSignalsSegmentation();
		averaging = false;
		actualTransform = 0;
		enabledWT = true;
	}
	
	public void dwt() throws InvalidFrameIndexException
	{
		transformatedEpochsDWT = new ArrayList<double[][]>();
		highestCoeficientsDWT = new ArrayList<ArrayList<double[]>>();
		indexesHighestCoeficientsDWT = new ArrayList<ArrayList<int[]>>();
		calculationProgressUnits();
		double[][] transformedEpochs;
		ArrayList<double[]> highestCoeficients;
		ArrayList<int[]> indexesHighestCoeficients;
		
		for(int i = 0; i < epochs.size();i++)
		{			
			transformedEpochs = new double[epochs.get(i).length][epochs.get(i)[0].length];
			highestCoeficients = new ArrayList<double[]>();
			indexesHighestCoeficients = new ArrayList<int[]>();
			
			for(int j = 0; j < epochs.get(i).length;j++)
			{
				if(!enabledWT)
					return;
				
				transformedEpochs[j] = dwt.transform(epochs.get(i)[j]);
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
		cwt.setTransformation(this);
		transformatedEpochsCWT = new ArrayList<ArrayList<double[][]>>();
		highestCoeficientsCWT = new ArrayList<ArrayList<double[]>>();
		indexesHighestCoeficientsCWT = new ArrayList<ArrayList<int[]>>();
		calculationProgressUnits();
		ArrayList<double[][]> transformedEpochs;
		ArrayList<double[]> highestCoeficients;
		ArrayList<int[]> indexesHighestCoeficients;
		
		for(int i = 0; i < epochs.size();i++)
		{						
			transformedEpochs = new ArrayList<double[][]>();
			highestCoeficients = new ArrayList<double[]>();
			indexesHighestCoeficients = new ArrayList<int[]>();
			
			for(int j = 0; j < epochs.get(i).length;j++)
			{
				cwt.transform(epochs.get(i)[j]);
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
			this.progressUnit = Const.PROGRESS_MAX/ (double)(epochs.size()*epochs.get(0).length);
		}
		else
		{	
			double countScales = ((cwt.getMaxScale()-cwt.getMinScale())/cwt.getStepScale())+1;
			this.progressUnit = Const.PROGRESS_MAX/(epochs.size()*epochs.get(0).length*countScales);
		}
	}
	
	private void loadEpochValues() throws InvalidFrameIndexException
	{
		epochs = new ArrayList<double[][]>();
		int startEpoch = signalsSegmentation.getStartEpoch();
		int endEpoch = signalsSegmentation.getEndEpoch();
		int epochLength = endEpoch - startEpoch;
		int index = 0;
		ArrayList<Epoch> selectedEpochs = signalsSegmentation.getEpochs();
		double[][] epoch;
		long epochPosition;
		
		if(averaging)
		{			
			epoch = new double[selectedEpochs.size()][epochLength];
			
			for(int i = 0; i < selectedEpochs.size();i++)
			{		
				index = 0;
				epochPosition = selectedEpochs.get(i).getPosition();
				
				for(int j = (int)epochPosition - startEpoch; j < epochPosition + endEpoch; j++)
				{
					for(int k = 0; k < channelsIndexes.length;k++)
					{
						epoch[i][index] += buffer.getValue(channelsIndexes[k], j);
					}
					
					epoch[i][index] /= channelsIndexes.length;
					index++;
				}
			}					
			
			
			epochs.add(epoch);
		}	
		else
		{
		
			for(int i = 0; i < channelsIndexes.length;i++)
			{	
				epoch = new double[selectedEpochs.size()][epochLength];
				
				for(int j = 0; j < selectedEpochs.size();j++)
				{
					index = 0;
					epochPosition = selectedEpochs.get(j).getPosition();
					
					for(int k = (int)epochPosition - startEpoch; k < epochPosition + endEpoch; k++)
					{
						epoch[j][index++] = buffer.getValue(channelsIndexes[i], k);
					}
				}
				
				epochs.add(epoch);
			}
		}
	}
	
    public void detectErp(int start, int end, int indexScaleWavelet)
    {
    	totalPositiveDetection = 0;
    	detectedERP = new boolean[epochs.size()][epochs.get(0).length];
    	positionHighestCoeficients = new int[epochs.size()][epochs.get(0).length];
    	positiveDetectionInChannels = new int[epochs.size()];
    	
    	if(actualTransform == Const.DWT)
    	{
    		double factor = Math.pow(Mathematic.CONST_2, indexScaleWavelet+1);
    		int startErp =(int) (start / factor);
    		int endErp = (int) (end / factor);
    		int level = (dwt.getLevelsOfDecompositon()-1)-indexScaleWavelet;
    		
    		
    		for(int i = 0; i < indexesHighestCoeficientsDWT.size(); i++)
    		{
    			positiveDetectionInChannels[i] = 0;
    			
    			for(int j = 0; j < indexesHighestCoeficientsDWT.get(i).size(); j++)
    			{
    				positionHighestCoeficients[i][j] = (int) (indexesHighestCoeficientsDWT.get(i).get(j)[level]*factor);
    				
    				if(startErp <= indexesHighestCoeficientsDWT.get(i).get(j)[level] &&
    						indexesHighestCoeficientsDWT.get(i).get(j)[level] < endErp)
    				{
    					totalPositiveDetection++;
    					positiveDetectionInChannels[i]++;
    					detectedERP[i][j] = true;
    				}
    			}
    		}
    	}
    	else
    	{
    		int level = indexScaleWavelet;
    		
    		for(int i = 0; i < indexesHighestCoeficientsCWT.size(); i++)
    		{
    			positiveDetectionInChannels[i] = 0;
    			
    			for(int j = 0; j < indexesHighestCoeficientsCWT.get(i).size(); j++)
    			{
    				positionHighestCoeficients[i][j] = indexesHighestCoeficientsCWT.get(i).get(j)[level];
    				
    				if((start <= indexesHighestCoeficientsCWT.get(i).get(j)[level]) &&
    						(indexesHighestCoeficientsCWT.get(i).get(j)[level] < end))
    				{
    					totalPositiveDetection++;
    					positiveDetectionInChannels[i]++;
    					detectedERP[i][j] = true;
    				}
    			}
    		}
    	}

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
			loadEpochValues();
			
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
	
	public void setAveraging(boolean averaging)
	{
		this.averaging = averaging;		
	}
	
	public boolean getAveraging()
	{
		return this.averaging;		
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
	
	public ArrayList<double[][]> getEpochs()
	{
		return epochs;	
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
