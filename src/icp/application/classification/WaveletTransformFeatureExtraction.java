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
	private static final int SKIP_SAMPLES = 0;

	/**
	 * Name of the wavelet
	 */
	private static int NAME;

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
		this.NAME = name;
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
		double[] features = new double[EPOCH_SIZE * numberOfChannels];
		int i = 0;
		for (int channel : CHANNELS) {
			res = dwt.processSignal(epoch[channel - 1]);
			for (int j = 0; j < EPOCH_SIZE; j++) {
				features[i * EPOCH_SIZE + j] = ((WaveletResultDiscrete) res)
						.getDwtCoefficients()[j + SKIP_SAMPLES];
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
		// TODO Auto-generated method stub
		return EPOCH_SIZE * CHANNELS.length / DOWN_SMPL_FACTOR;
	}

}
