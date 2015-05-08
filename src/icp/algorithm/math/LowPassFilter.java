package icp.algorithm.math;

public class LowPassFilter implements IFilter{
	
	private double alpha, timeDelay, previousOutput;
	
	public LowPassFilter(double freq, double sampleRate) {
		this.timeDelay = 1/sampleRate;
		double timeConstant = 1/(2*Math.PI*freq);
		this.alpha = timeDelay / (timeDelay + timeConstant);
		previousOutput = 0;
	}

	@Override
	public double getOutputSample(double currentInput) {
		double result = previousOutput;
		result = alpha * currentInput + (1-alpha) * previousOutput;
		previousOutput = result;
		return result;
	}
	
	public double getAlpha() {
		return alpha;
	}
	
	public double getTimeDelay() {
		return timeDelay;
	}
	
	public double getPreviousOutput() {
		return previousOutput;
	}
}
