package icp.algorithm.math;

import biz.source_code.dsp.filter.FilterCharacteristicsType;
import biz.source_code.dsp.filter.FilterPassType;
import biz.source_code.dsp.filter.IirFilter;
import biz.source_code.dsp.filter.IirFilterCoefficients;
import biz.source_code.dsp.filter.IirFilterDesignFisher;

public class ButterWorthFilter implements IFilter {
	
	IirFilter filter;
	IirFilterCoefficients coeffs;
	
	public ButterWorthFilter(double Fa, double Fb, int sampleRate) {
		coeffs = setupFilter(Fa, Fb, sampleRate);
		filter = new IirFilter(coeffs);
	}
	
	public ButterWorthFilter() {
		this(0.1, 8, 1024);
	}

	@Override
	public double getOutputSample(double inputSample) {
		return filter.step(inputSample);
	}
	
	public IirFilterCoefficients setupFilter(double Fa, double Fb, int sampleRate) {
		IirFilterCoefficients coef = IirFilterDesignFisher.design(FilterPassType.bandpass, 
				FilterCharacteristicsType.butterworth,
				1, -1, Fa/sampleRate, Fb/sampleRate);
		
		return coef;
	}
	
	public static double[] calculateImpulseResponce(double Fa, double Fb, int sampleRate) {
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
