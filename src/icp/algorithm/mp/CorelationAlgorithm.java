package icp.algorithm.mp;

import java.util.Arrays;

public final class CorelationAlgorithm extends DetectionAlgorithm
{
	public static double convolution(double[] signal1, double[] signal2)
	{
		double[] first = signal1;
		double[] second = signal2;
		
		if (signal1.length > signal2.length)
		{
			second = Arrays.copyOf(signal2, signal1.length);
			Arrays.fill(second, signal2.length - 1, second.length, 0);
		}
		else if (signal1.length < signal2.length)
		{
			first = Arrays.copyOf(signal1, signal2.length);
			Arrays.fill(first, signal1.length - 1, first.length, 0);
		}
		
		double sum = 0;
		
		for (int i = 0; i < first.length; i++)
		{
			sum += first[i] * second[i];
		}
		
		return sum;
	}
	
	private int maxPosition;
	
	private double maxConvolutionValue;
	
	public CorelationAlgorithm(double[] epoch, double[] function, int min, int max)
	{
		super(epoch, function, min, max);
		double sum;
		maxConvolutionValue = Double.MIN_VALUE;
		
		for (int position = minIndex; position <= maxIndex && position < (epoch.length - function.length); position++)
		{
			sum = 0;
			for (int i = 0; i < function.length; i++)
			{
				sum += epoch[position + i] * function[i];
			}
			//System.out.println("Position: " + position + ", convolution: " + sum);
			if (sum > maxConvolutionValue)
			{
				maxConvolutionValue = sum;
				maxPosition = position;
			}
		}
		//System.out.println("Konec algoritmu - maxPosition: " + maxPosition + ", maxValue: " + maxConvolutionValue);
	}

	/**
	 * @return the maxPosition
	 */
	@Override
	public int getMaxPosition()
	{
		return maxPosition;
	}

	/**
	 * @return the maxConvolutionValue
	 */
	@Override
	public double getMaxEvaluation()
	{
		return maxConvolutionValue;
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
}
