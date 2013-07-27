package icp.application;

import icp.Const;
import icp.data.*;

import java.util.*;

public class Averaging
{
	private final int ARTEFACT_LEVEL = 70;
	private SignalsSegmentation sigSegmentation;
	private SessionManager appCore;
	private Buffer buffer;
	private Header header;
	private ArrayList<Element> elements;
	private boolean[] artefactInEpoch;
	private String[] epochTypes = {"S  1", "S  2", "S  3", "S  4", "S  5", "S  6", "S  7", "S  8"};
	

	
	public Averaging(SessionManager appCore, SignalsSegmentation sigSegmentation)
	{
		this.appCore = appCore;
		this.sigSegmentation = sigSegmentation;
		buffer = appCore.getBuffer();
		header = appCore.getHeader();	
		elements = new ArrayList<Element>();
	}
	
	public void averagingElements(int epochsCountForElement, int channelIndex, boolean useBaselineCorection, int shiftValue)
	{
		elements = new ArrayList<Element>();
		
		// contains list of epochs for the given element - M channels x N time samples 
		List<double[][]> allEpochs = new ArrayList<double[][]>(); 
		
		int start = sigSegmentation.getStartEpoch();
		int end = sigSegmentation.getEndEpoch();
		int interval = start+end;
		ArrayList<Epoch> epochs = header.getEpochs();
		double[] epoch = new double[interval];
		double[][] wholeEpoch = new double[Const.NUMBER_OF_CHANNELS][interval]; // complete epoch - M x N
		Arrays.fill(epoch, 0);
		Element element = new Element();
		int index = 0, epochsCount = 0, realEpochsCount = 0;
		double totalShiftValue = 0;
		findArtefacts(channelIndex);
			
		
		try
		{
			if(useBaselineCorection)
			{
				double baselineValue = 0;
				
				for(int i = 0; i < header.getNumberOfSamples();i++)
				{
					baselineValue += buffer.getValue(channelIndex, i);
				}
				
				baselineValue /= (double)header.getNumberOfSamples();
				totalShiftValue = baselineValue+shiftValue;
			}
			
			for(int i = 0;i < epochs.size();i++)
			{
				if(epochs.get(i).getDescription().equals(epochTypes[0]))
				{
					epochsCount++;				
					
					
					if(!artefactInEpoch[i])
					{
						realEpochsCount++;
						
						for (int l = 0; l < Const.NUMBER_OF_CHANNELS; l++) { // for all EEG channels
							index = 0;
							for(int j = (int)epochs.get(i).getPosition()-start; j < (int)epochs.get(i).getPosition()+ end - 1;j++) // for all time samples
							{
								wholeEpoch[l][index] = buffer.getValue(l, j)-totalShiftValue; // complete epoch data
								if (l == channelIndex) 
									epoch[index] += buffer.getValue(l, j)-totalShiftValue; // single epoch data - may become obsolete
								index++;
							}
						}
						
						allEpochs.add(wholeEpoch); // add a new valid epoch
						System.out.println("Adding epoch: " + allEpochs);
						
					}
					
					if(epochsCount%epochsCountForElement == 0)
					{
						for(int k = 0; k < epoch.length;k++)
						{
							epoch[k] = (double)epoch[k]/(double)realEpochsCount;
						}
						
						// set new element parameters
						element.setRowsAndColumnsEpoch(0, epoch); 
						element.setRowsAndColumnsRawData(0, allEpochs);
						System.out.println("Adding all epochs");
						elements.add(element);
						
						
						// init data for a new element
						element = new Element();
						element.setChannelIndex(channelIndex);
						epoch = new double[interval];
						allEpochs = new ArrayList<double[][]>();
						wholeEpoch = new double[Const.NUMBER_OF_CHANNELS][interval];
						Arrays.fill(epoch, 0);
						realEpochsCount = 0;
					}
				}				
			}
			
			realEpochsCount = 0;
			for(int type = 1;type < epochTypes.length; type++)
			{
				epochsCount = 0;
				
				for(int i = 0; i < epochs.size() ;i++)
				{
					if(epochs.get(i).getDescription().equals(epochTypes[type]))
					{
						epochsCount++;
						
						if(!artefactInEpoch[i])
						{
							realEpochsCount++;
							index = 0;
							for (int l = 0; l < Const.NUMBER_OF_CHANNELS; l++) { // for all EEG channels
								index = 0;
								for(int j = (int)epochs.get(i).getPosition()-start; j < (int)epochs.get(i).getPosition()+ end - 1;j++) // for all time samples
								{
									wholeEpoch[l][index] = buffer.getValue(l, j)-totalShiftValue; // complete epoch data
									if (l == channelIndex) 
										epoch[index] += buffer.getValue(l, j)-totalShiftValue; // single epoch data - may become obsolete
									index++;
								}
							}
							allEpochs.add(wholeEpoch); // add a new valid epoch
							System.out.println("Adding epoch: " + allEpochs);
						}
						
						if(epochsCount%epochsCountForElement == 0)
						{
							for(int k = 0; k < epoch.length;k++)
							{
								epoch[k] = (double)epoch[k]/(double)realEpochsCount;
							}
							
							element = elements.get((epochsCount/epochsCountForElement) - 1);
							
							// correct rows and cols
							element.setRowsAndColumnsEpoch(type, epoch);
							element.setRowsAndColumnsRawData(type, allEpochs);
							epoch = new double[interval];
							allEpochs = new ArrayList<double[][]>();
							Arrays.fill(epoch, 0);
							realEpochsCount = 0;
						}
					}
				}
			}
		}
		catch (InvalidFrameIndexException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void findArtefacts(int channelIndex)
	{
		ArrayList<Epoch> epochs = header.getEpochs();
		artefactInEpoch = new boolean[epochs.size()];
		Arrays.fill(artefactInEpoch, false);
		int start = sigSegmentation.getStartEpoch();
		int end = sigSegmentation.getEndEpoch();
		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;
		
		for(int i = 0; i < artefactInEpoch.length;i++)
		{
			min = Double.MAX_VALUE;
			max = -Double.MAX_VALUE;
			
			for(int j = (int)epochs.get(i).getPosition()-start; j < (int)epochs.get(i).getPosition()+ end;j++)
			{
				try
				{
					if(min > buffer.getValue(channelIndex, j))
						min = buffer.getValue(channelIndex, j);
					
					if(max < buffer.getValue(channelIndex, j))
						max = buffer.getValue(channelIndex, j);
				}
				catch (InvalidFrameIndexException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(Math.abs(max - min) >= ARTEFACT_LEVEL)
				artefactInEpoch[i] = true;
		}
	}
	
	public ArrayList<Element> getElements()
	{
		return elements;	
	}
	
	public void setBuffer(Buffer buffer)
	{
		this.buffer = buffer;	
	}
	
	public void setHeader(Header header)
	{
		this.header = header;	
	}
}
