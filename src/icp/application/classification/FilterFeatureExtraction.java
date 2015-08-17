package icp.application.classification;

import icp.algorithm.math.ButterWorthFilter;
import icp.algorithm.math.FirFilter;
import icp.algorithm.math.IFilter;
import icp.algorithm.math.SignalProcessing;

public class FilterFeatureExtraction implements IFeatureExtraction {

	private static final int[] CHANNELS = {1, 2, 3}; /* EEG channels to be transformed to feature vectors */

	private static final int EPOCH_SIZE = 512; /* number of samples to be used - Fs = 1000 Hz expected */

	private static final int DOWN_SMPL_FACTOR = 32;  /* subsampling factor */

	private static final int SKIP_SAMPLES = 0; /* skip initial samples in each epoch */
	
	private IFilter filter;

	
	public FilterFeatureExtraction() {
		this.filter = new ButterWorthFilter();
	}

   @Override
   public double[] extractFeatures(double[][] epoch) {
       // use 3 EEG channels
       int numberOfChannels = CHANNELS.length;
       double[] features = new double[EPOCH_SIZE * numberOfChannels];
       int i = 0;

       
       for (int channel : CHANNELS) {
           double[] currChannelData = epoch[channel - 1];
           for (int j = 0; j < EPOCH_SIZE; j++) {
               features[i * EPOCH_SIZE + j] = filter.getOutputSample(currChannelData[j + SKIP_SAMPLES]);
           }
           i++;
       }

              
       features = SignalProcessing.normalize(features);
       return features;
   }

   @Override
   public int getFeatureDimension() {
       return CHANNELS.length * EPOCH_SIZE / DOWN_SMPL_FACTOR /* subsampling */;
   }

}
