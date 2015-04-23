package icp.application.classification;

import icp.algorithm.math.SignalProcessing;
import cz.zcu.kiv.eegdsp.common.ISignalProcessingResult;
import cz.zcu.kiv.eegdsp.common.ISignalProcessor;
import cz.zcu.kiv.eegdsp.main.SignalProcessingFactory;
import cz.zcu.kiv.eegdsp.wavelet.discrete.WaveletResultDiscrete;
import cz.zcu.kiv.eegdsp.wavelet.discrete.WaveletTransformationDiscrete;
import cz.zcu.kiv.eegdsp.wavelet.discrete.algorithm.wavelets.WaveletDWT;

/**
 * 
 * Features extraction based on discrete wavelet transformation using eegdsp
 * library
 * 
 * @author Jaroslav Klaus
 *
 */
public class WaveletTransformFeatureExtraction implements IFeatureExtraction {

	/**
	 * EEG channels to be transformed to feature vectors
	 */
	private static final int[] CHANNELS = { 1, 2, 3 };

	/**
	 * Number of samples to be used - Fs = 1000 Hz expected
	 */
	private static final int EPOCH_SIZE = 512;

	/**
	 * Subsampling factor
	 */
	private static final int DOWN_SMPL_FACTOR = 1;

	/**
	 * Skip initial samples in each epoch
	 */
	private static final int SKIP_SAMPLES = 200;

	/**
	 * Name of the wavelet
	 */
	private int NAME;

	/**
	 * Size of feature vector
	 */
	private static final int FEATURE_SIZE = 32;

	/**
	 * Constructor for the wavelet transform feature extraction with default
	 * wavelet
	 */
	public WaveletTransformFeatureExtraction() {
		this.NAME = 7;
	}

	/**
	 * Constructor for the wavelet transform feature extraction with user
	 * defined wavelet
	 * 
	 * @param name
	 *            - name of the wavelet transform method
	 */
	public WaveletTransformFeatureExtraction(int name) {
		setWaveletName(name);
	}

	/**
	 * Method that creates a wavelet by a name using SignalProcessingFactory and
	 * processes the signal
	 * 
	 * @param epoch
	 *            - source epochs
	 * @return - normalized feature vector with only approximation coefficients
	 */
	@Override
	public double[] extractFeatures(double[][] epoch) {
		ISignalProcessor dwt = SignalProcessingFactory.getInstance()
				.getWaveletDiscrete();
		String[] names = ((WaveletTransformationDiscrete) dwt)
				.getWaveletGenerator().getWaveletNames();
		WaveletDWT wavelet = null;
		try {
			wavelet = ((WaveletTransformationDiscrete) dwt)
					.getWaveletGenerator().getWaveletByName(names[NAME]);
		} catch (Exception e) {
			System.out
					.println("Exception loading wavelet " + names[NAME] + ".");
		}
		((WaveletTransformationDiscrete) dwt).setWavelet(wavelet);

		ISignalProcessingResult res;
		int numberOfChannels = CHANNELS.length;
		double[] features = new double[FEATURE_SIZE * numberOfChannels];
		int i = 0;
		for (int channel : CHANNELS) {
			double[] currChannelData = new double[EPOCH_SIZE];
			for (int j = 0; j < EPOCH_SIZE; j++) {
				currChannelData[j] = epoch[channel - 1][j
						+ SKIP_SAMPLES];
			}
			res = dwt.processSignal(currChannelData);
			for (int j = 0; j < FEATURE_SIZE; j++) {
				features[i * FEATURE_SIZE + j] = ((WaveletResultDiscrete) res)
						.getDwtCoefficients()[j];
			}
			i++;
		}
		features = SignalProcessing.normalize(features);
		
		return features;
	}

	/**
	 * Gets feature vector dimension
	 * 
	 * @return - feature vector dimension
	 */
	@Override
	public int getFeatureDimension() {
		return FEATURE_SIZE * CHANNELS.length / DOWN_SMPL_FACTOR;
	}

	public void setWaveletName(int name) {
		if (name >= 0 && name <= 17) {
			this.NAME = name;
		} else
			throw new IllegalArgumentException(
					"Wavelet name must be >= 0 and <= 17");
	}

}
