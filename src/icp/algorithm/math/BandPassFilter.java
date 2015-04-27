package icp.algorithm.math;

public class BandPassFilter implements IFilter {
	
	IFilter lowPass, highPass;
	
	public BandPassFilter(double Fa, double Fb, int sampleRate) {
		lowPass = new LowPassFilter(Fb, sampleRate);
		highPass = new HighPassFilter(Fa, sampleRate);	
	}
	
	public BandPassFilter() {
		this(0.1, 30, 1000);
	}

	@Override
	public double getOutputSample(double inputSample) {
		double result;
		result = lowPass.getOutputSample(inputSample);
		result = highPass.getOutputSample(inputSample);
		return result;
	}
}
