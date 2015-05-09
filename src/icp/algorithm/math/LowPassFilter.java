package icp.algorithm.math;

/**
 * Implementation of Low-Pass filter. Very simple and low-computing time.
 * Uses moving average algorithm to filter the signal. 
 * Keeps previous output of filtered data for computing the next output.
 * Very close to IIR filter function from DSP-collection.
 * 
 * @author Jan Vampol
 * @version 1.00
 */
public class LowPassFilter implements IFilter{
	
	private double alpha, timeDelay, previousOutput;
	
	/**
	 * Constructor calculates {@link alpha} and {@link timeDelay} from given frequency and sample rate.
	 * 
	 * @param freq
	 * @param sampleRate
	 */
	public LowPassFilter(double freq, double sampleRate) {
		this.timeDelay = 1/sampleRate;
		double timeConstant = 1/(2*Math.PI*freq);
		this.alpha = timeDelay / (timeDelay + timeConstant);
		previousOutput = 0;
	}

	@Override
	/**
	 * Calculates next output from given input. Uses previous output and alpha for calculation.
	 * Output value is a sum of current input and previous output with given alpha. 
	 * 
	 * @param inputSample Input data
	 * @return double Output data
	 */
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
