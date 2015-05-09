package icp.application.classification;

import icp.Const;
import icp.algorithm.math.FirFilter;
import icp.algorithm.math.IFilter;
import icp.algorithm.math.SignalProcessing;
import icp.online.gui.MainFrame;

/**
 *
 * Feature extraction based on low-pass-filtering and subsampling of the input
 * epochs
 *
 * @author Lukas Vareka
 *
 */
public class FilterAndSubsamplingFeatureExtraction implements IFeatureExtraction {
	
	 private static int[] CHANNELS = {1, 2, 3}; /* EEG channels to be transformed to feature vectors */

	 private int EPOCH_SIZE = 650; /* number of samples to be used - Fs = 1000 Hz expected */

	 private int DOWN_SMPL_FACTOR = 2;  /* subsampling factor */

	 private int SKIP_SAMPLES = 0; /* skip initial samples in each epoch */
	 
	 private IFilter filter = null;

    @Override
    public double[] extractFeatures(double[][] epoch) {
        // use 3 EEG channels
        int numberOfChannels = CHANNELS.length;
        double[] features = new double[EPOCH_SIZE * numberOfChannels];
        int i = 0;

        // filter the data
        filter = MainFrame.dataFilter;
        for (int channel : CHANNELS) {
            double[] currChannelData = epoch[channel - 1];
            for (int j = 0; j < EPOCH_SIZE; j++) {
            	if(filter == null)
            		features[i * EPOCH_SIZE + j] = currChannelData[j + SKIP_SAMPLES];
            	else
            		features[i * EPOCH_SIZE + j] = filter.getOutputSample(currChannelData[j + SKIP_SAMPLES]);
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
        return CHANNELS.length * EPOCH_SIZE / DOWN_SMPL_FACTOR /* subsampling */;
    }
    
    public void setEpochSize(int epochSize) {
		if (epochSize > 0 && epochSize <= Const.POSTSTIMULUS_VALUES) {
			this.EPOCH_SIZE = epochSize;
		} else {
			throw new IllegalArgumentException("Epoch Size must be > 0 and <= "
					+ Const.POSTSTIMULUS_VALUES);
		}
	}

	public void setSkipSamples(int skipSamples) {
		if (skipSamples > 0 && skipSamples <= Const.POSTSTIMULUS_VALUES) {
			this.SKIP_SAMPLES = skipSamples;
		} else {
			throw new IllegalArgumentException(
					"Skip Samples must be > 0 and <= "
							+ Const.POSTSTIMULUS_VALUES);
		}
	}

	public void setSubsampling(int subsampling) {
		if (subsampling > 0) {
			this.DOWN_SMPL_FACTOR = subsampling;
		} else {
			throw new IllegalArgumentException(
					"Subsampling must be > 0");
		}
	}
}
