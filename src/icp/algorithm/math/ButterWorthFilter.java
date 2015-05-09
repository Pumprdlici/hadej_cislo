package icp.algorithm.math;

import biz.source_code.dsp.filter.FilterCharacteristicsType;
import biz.source_code.dsp.filter.FilterPassType;
import biz.source_code.dsp.filter.IirFilter;
import biz.source_code.dsp.filter.IirFilterCoefficients;
import biz.source_code.dsp.filter.IirFilterDesignFisher;

/**
 * This class simulates function of IIR ButterWorthFilter using DSP-collection library.
 * @author Jan Vampol
 * @version 1.00
 */
public class ButterWorthFilter implements IFilter {
	
	/** Instance of IirFilter */
	IirFilter filter;
	/** Instance of IirFilterCoefficients with impulse response values stored */
	IirFilterCoefficients coeffs;
	
	/**
	 * Constructor calculates impulse response using DSP-collection and creates instance of IirFilter. 
	 * 
     * @param Fa Lower frequency - must be lower than {@link Fb}
     * @param Fb Upper frequency - must be lower than {@link sampleRate}/2
     * @param sampleRate Sampling rate of VisionRecorder
	 */
	public ButterWorthFilter(double Fa, double Fb, int sampleRate) {
		coeffs = setupFilter(Fa, Fb, sampleRate);
		filter = new IirFilter(coeffs);
	}
	
    /**
     * Default constructor with recommended values.
     */
	public ButterWorthFilter() {
		this(0.1, 8, 1024);
	}
	
	@Override
	 /**
     * Method for filtering signal. Gets input sample and filters it
     * using DSP-collection library.
     * 
     * @param inputSample Input data
     */
	public double getOutputSample(double inputSample) {
		return filter.step(inputSample);
	}
	
	/**
     * Method sets up filter so it is ready to use. Calculates impulse response.
     * 
     * @param Fa Lower frequency
     * @param Fb Upper frequency
     * @param sampleRate Sampling rate of VisionRecorder
     * @return double[] IirFilterCoefficients instance
     */
	public IirFilterCoefficients setupFilter(double Fa, double Fb, int sampleRate) {
		IirFilterCoefficients coef = IirFilterDesignFisher.design(FilterPassType.bandpass, 
				FilterCharacteristicsType.butterworth,
				1, -1, Fa/sampleRate, Fb/sampleRate);
		
		return coef;
	}
	
	/**
	 * Method for calculating impulse response using DSP-collection library. When the calculation is finished
	 * both A and B fields of impulse response are merged for easier use.
	 * 
     * @param Fa Lower frequency - must be lower than {@link Fb}
     * @param Fb Upper frequency - must be lower than {@link sampleRate}/2
     * @param sampleRate Sampling rate of VisionRecorder
	 * @return
	 */
	public static double[] calculateImpulseResponse(double Fa, double Fb, int sampleRate) {
		double[] result;
		
		IirFilterCoefficients temp = IirFilterDesignFisher.design(FilterPassType.bandpass, 
				FilterCharacteristicsType.butterworth,
				1, -1, Fa/sampleRate, Fb/sampleRate);
		
		result = new double[temp.a.length + temp.b.length];
		for(int i = 0; i < temp.a.length + temp.b.length; i++) {
			if(i < temp.a.length)
				result[i] = temp.a[i];
			else
				result[i] = temp.b[i - temp.a.length];
		}
		
		return result;
	}
}
