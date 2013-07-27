package icp.application;

import icp.algorithm.mp.*;

import java.util.*;


public final class MatchingPursuitDetectionAlgorithm extends Thread
{
	private SessionManager sm;
	
	private List<DetectionAlgorithm> convolutionResult;
	
	private double[] function;
	
	private int minPosition;
	
	private int maxPosition;
	
	private int numberOfIterations;
	
	private List<Element> elements;
	
	private int method;
	
	private double unit;
	
	private boolean stop;
	
	public void stopMP()
	{
		stop = true;
	}
	
	public MatchingPursuitDetectionAlgorithm(SessionManager sm, double[] function, int minPosition, int maxPosition, int numberOfIterations, List<Element> elements, int method)
	{
		this.sm = sm;
		this.function = function;
		this.minPosition = minPosition;
		this.maxPosition = maxPosition;
		this.numberOfIterations = numberOfIterations;
		this.elements = elements;
		this.method = method;
		convolutionResult = new ArrayList<DetectionAlgorithm>(elements.size() * 8);
	}
	
	public void run()
	{
		stop = false;
		FourierMP mp = new FourierMP(new GaborBase());
		DecompositionCollection dc;
		DetectionAlgorithm ca;
		double max;
		int maxIndex;
		//DetectionAlgorithm maxCA; //Bude se ukládat pro zdùvodnìní výsledkù detekce.
		unit = 100D / (8D * (double) elements.size());
		for (Element e: elements) // odpovida parametru prumerovani - 1
		{
			max = Double.MIN_VALUE;
			maxIndex = -1;
			//maxCA = null;
			for (int i = 0; i < 4; i++) //Magické èíslo 4 = pocet radek?
			{
				if (stop)
					return;
				
				dc = mp.doMP(Arrays.copyOfRange(e.getRowsAndColumnsEpoch()[i], Const.BEGIN_POSITION, Const.END_POSITION), numberOfIterations);
				sm.sendProgressUnits(unit);
				if (method == DetectionAlgorithm.CORELATION)
				{
					ca = new CorelationAlgorithm(
							dc.getReconstruction(numberOfIterations), 
							function, 
							minPosition, 
							maxPosition);
				}
				else
				{
					ca = new MinDistanceAlgorithm(
							dc.getReconstruction(numberOfIterations), 
							function, 
							minPosition, 
							maxPosition);
				}
				convolutionResult.add(ca);
				if (max < ca.getMaxEvaluation())
				{
					max = ca.getMaxEvaluation();
					maxIndex = i; // vyber vitezny radek
					//maxCA = ca;
				}
			}
			
			e.setDetectedRow(maxIndex); // nastav vitezny radek
			
			max = Double.MIN_VALUE;
			maxIndex = -1;
			//maxCA = null;
			
			for (int i = 4; i < e.getRowsAndColumnsEpoch().length; i++) //Magické èíslo 4 - pocet sloupcu
			{
				if (stop)
					return;
				
				dc = mp.doMP(Arrays.copyOfRange(e.getRowsAndColumnsEpoch()[i], Const.BEGIN_POSITION, Const.END_POSITION), numberOfIterations);
				sm.sendProgressUnits(unit);
				if (method == DetectionAlgorithm.CORELATION)
				{
					ca = new CorelationAlgorithm(
							dc.getReconstruction(numberOfIterations), 
							function, 
							minPosition, 
							maxPosition);
				}
				else
				{
					ca = new MinDistanceAlgorithm(
							dc.getReconstruction(numberOfIterations), 
							function, 
							minPosition, 
							maxPosition);
				}
				convolutionResult.add(ca);
				if (max < ca.getMaxEvaluation())
				{
					max = ca.getMaxEvaluation();
					maxIndex = i;
					//maxCA = ca;
				}
			}
			
			e.setDetectedColumn(maxIndex - 4);
		}
		sm.sendDetectionMessage();
	}
	
	public List<DetectionAlgorithm> getConvolutionResults()
	{
		return convolutionResult;
	}
}
