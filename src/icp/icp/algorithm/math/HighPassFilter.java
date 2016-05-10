package icp.algorithm.math;

/**
 * Implementation of High-Pass filter. Very simple and low-computing time.
 * Uses moving average algorithm to filter the signal. 
 * Keeps previous output and input of filtered data for computing the next output.
 * Very close results to IIR filter function from DSP-collection.
 * 
 * @author Jan Vampol
 * @version 1.00
 */
public class HighPassFilter implements IFilter{
	
	private double alpha, timeDelay, previousOutput, previousInput;
	
	/**
	 * Constructor calculates {@link alpha} and {@link timeDelay} from given frequency and sample rate.
	 * 
	 * @param freq
	 * @param sampleRate
	 */
	public HighPassFilter(double freq, double sampleRate) {
		this.timeDelay = 1/sampleRate;
		double timeConstant = 1/(2*Math.PI*freq);
		this.alpha = timeConstant / (timeConstant + timeDelay);
		previousOutput = 0;
		previousInput = 0;
	}

	@Override
	/**
	 * Calculates next output from given input. Uses previous output, previous input and alpha for calculation.
	 * Output value is a sum of previous output and diff of previous and current input with given alpha. 
	 * 
	 * @param inputSample Input data
	 * @return double Output data
	 */
	public double getOutputSample(double currentInput) {
		double result = previousOutput;
		result = alpha * previousOutput + alpha * (currentInput - previousInput);
		previousInput = currentInput;
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
	
	public double getPreviousInput() {
		return previousInput;
	}

}
