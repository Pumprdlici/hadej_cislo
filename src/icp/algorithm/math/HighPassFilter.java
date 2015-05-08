package icp.algorithm.math;

public class HighPassFilter implements IFilter{
	
	private double alpha, timeDelay, previousOutput, previousInput;
	
	public HighPassFilter(double freq, double sampleRate) {
		this.timeDelay = 1/sampleRate;
		double timeConstant = 1/(2*Math.PI*freq);
		this.alpha = timeConstant / (timeConstant + timeDelay);
		previousOutput = 0;
		previousInput = 0;
	}

	@Override
	public double getOutputSample(double currentInput) {
		double result = previousOutput;
		result = alpha * previousOutput + alpha * (currentInput - previousInput);
		previousInput = currentInput;
		previousOutput = result;
		System.out.println("HIGHPASS: filtruju -> " + result);
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
	
	public double getPreviousInput() {
		return previousInput;
	}

}
