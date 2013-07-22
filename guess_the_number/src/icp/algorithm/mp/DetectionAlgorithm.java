package icp.algorithm.mp;

public abstract class DetectionAlgorithm
{
	public static final int CORELATION = 0;
	
	public static final int MIN_DISTANCE = 1;
	
	protected int minIndex;
	
	protected int maxIndex;
	
	protected double[] function;
	
	protected double[] epoch;
	
	public DetectionAlgorithm(double[] epoch, double[] function, int min, int max)
	{
		this.epoch = epoch;
		this.function = function;
		minIndex = min;
		maxIndex = max;
	}
	
	public abstract double getMaxEvaluation();
	
	public abstract double[] getMaxFunction();
	
	public abstract int getMaxPosition();
	
	public double[] getFunction()
	{
		return function;
	}
	
	public double[] getEpoch()
	{
		return epoch;
	}
}
