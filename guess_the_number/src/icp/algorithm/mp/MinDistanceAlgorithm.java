package icp.algorithm.mp;

import java.util.Arrays;

public class MinDistanceAlgorithm extends DetectionAlgorithm
{
	private double minDistanceValue;
	
	private int maxPosition;
	
	public MinDistanceAlgorithm(double[] epoch, double[] function, int min, int max)
	{
		super(epoch, function, min, max);
		double sum;
		minDistanceValue = Double.MAX_VALUE;
		
		for (int position = minIndex; position <= maxIndex && position < (epoch.length - function.length); position++)
		{
			sum = 0;
			for (int i = 0; i < function.length; i++)
			{
				sum += Math.abs(epoch[position + i] - function[i]);
			}
			//System.out.println("Position: " + position + ", convolution: " + sum);
			if (sum < minDistanceValue)
			{
				minDistanceValue = sum;
				maxPosition = position;
			}
		}
	}

	@Override
	public double getMaxEvaluation()
	{
		return 50000 - minDistanceValue;
	}

	@Override
	public double[] getMaxFunction()
	{
		double[] maxFunction = new double[epoch.length];
		
		Arrays.fill(maxFunction, 0);
		
		for (int maxIndex = maxPosition; maxIndex < maxPosition + function.length; maxIndex++)
		{
			maxFunction[maxIndex] = function[maxIndex - maxPosition];
		}
		
		return maxFunction;
	}

	@Override
	public int getMaxPosition()
	{
		return maxPosition;
	}

}
