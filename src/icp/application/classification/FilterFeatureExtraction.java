package icp.application.classification;

import icp.algorithm.math.FirFilter;
import icp.algorithm.math.IFilter;
import icp.algorithm.math.SignalProcessing;

/**
 * 
 * Feature extraction based on low-pass-filtering and
 * subsampling of the input epochs
 * 
 * @author Lukas Vareka
 *
 */
public class FilterFeatureExtraction implements IFeatureExtraction {
	private final int[] CHANNELS       = {17, 18, 19}; /* EEG channels to be transformed to feature vectors */
	private final int EPOCH_SIZE       = 256; /* number of samples to be used - Fs = 1000 Hz expected */
	private final int DOWN_SMPL_FACTOR = 16;  /* subsampling factor */
	private final int SKIP_SAMPLES     = 180; /* skip initial samples in each epoch */
	
	private final double[] lowPassCoeffs = {0.000308, // low pass 0 - 8 Hz, M = 19 
			                                0.001094, 
	                                        0.002410, 
	                                        0.004271, 
	                                        0.006582, 
	                                        0.009132, 
	                                        0.011624, 
	                                        0.013726, 
	                                        0.015131, 
	                                        0.015625, 
	                                        0.015131, 
	                                        0.013726, 
	                                        0.011624, 
	                                        0.009132, 
	                                        0.006582, 
	                                        0.004271, 
	                                        0.002410, 
	                                        0.001094, 
	                                        0.000308} ; 
	

	@Override
	public double[] extractFeatures(double[][] epoch) {
		// use 3 EEG channels
		int numberOfChannels = CHANNELS.length;
		double[] features = new double[EPOCH_SIZE * numberOfChannels];
		int i = 0;
		
		// filter the data
		IFilter lowPassFilter = new FirFilter(lowPassCoeffs);
		for (int channel: CHANNELS) {
			double[] currChannelData = epoch[channel - 1];
			for (int j = 0; j < EPOCH_SIZE; j++) {
				features[i * EPOCH_SIZE + j] = lowPassFilter.getOutputSample(currChannelData[j + SKIP_SAMPLES]);
			}
			i++;	
		}
		
		// subsample the filtered data and return the feature vectors after vector normalization
		features = SignalProcessing.decimate(features, DOWN_SMPL_FACTOR);
		features = SignalProcessing.normalize(features);
		return features;
	
	}


	@Override
	public int getFeatureDimension() {
		return CHANNELS.length  * EPOCH_SIZE  / DOWN_SMPL_FACTOR /* subsampling */;
	}

}
