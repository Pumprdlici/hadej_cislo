package icp.application.classification;

import icp.Const;
import icp.algorithm.math.FirFilter;
import icp.algorithm.math.IFilter;
import icp.algorithm.math.SignalProcessing;

/**
 *
 * Feature extraction based on low-pass-filtering and subsampling of the input
 * epochs
 *
 * @author Lukas Vareka
 *
 */
public class FilterFeatureExtraction implements IFeatureExtraction {

    @Override
    public double[] extractFeatures(double[][] epoch) {
        // use 3 EEG channels
        int numberOfChannels = Const.CHANNELS.length;
        double[] features = new double[Const.EPOCH_SIZE * numberOfChannels];
        int i = 0;

        // filter the data
        IFilter lowPassFilter = new FirFilter(Const.lowPassCoeffs);
        for (int channel : Const.CHANNELS) {
            double[] currChannelData = epoch[channel - 1];
            for (int j = 0; j < Const.EPOCH_SIZE; j++) {
                features[i * Const.EPOCH_SIZE + j] = lowPassFilter.getOutputSample(currChannelData[j + Const.SKIP_SAMPLES]);
            }
            i++;
        }

        // subsample the filtered data and return the feature vectors after vector normalization
        features = SignalProcessing.decimate(features, Const.DOWN_SMPL_FACTOR);
        features = SignalProcessing.normalize(features);
        return features;
    }

    @Override
    public int getFeatureDimension() {
        return Const.CHANNELS.length * Const.EPOCH_SIZE / Const.DOWN_SMPL_FACTOR /* subsampling */;
    }

}
