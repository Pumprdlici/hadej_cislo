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
		System.out.println("LOWPASS: filtruju -> " + result);
		return result;
	}

}
