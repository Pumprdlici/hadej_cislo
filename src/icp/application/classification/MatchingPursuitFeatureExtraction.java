package icp.application.classification;

import cz.zcu.kiv.eegdsp.matchingpursuit.MatchingPursuit;

/**
 * Feature extractor using Matching Pursuit algorithm implemented in eegdsp library.
 * @author Karel Silhavy
 *
 */
public class MatchingPursuitFeatureExtraction implements IFeatureExtraction {

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
	private static final int DOWN_SMPL_FACTOR = 8;

	
	/**
	 * Skip initial samples in each epoch
	 */
	private static final int SKIP_SAMPLES = 200;
	
	/**
	 * Private instance of singleton.
	 */
	private MatchingPursuit instance;
		
	/**
	 * Prepare instance for use.
	 * Default number of iterations is 4.
	 */
	public MatchingPursuitFeatureExtraction() {
		this.instance = MatchingPursuit.getInstance();
		this.instance.setIterationCount(4);
	}
	
	/**
	 * Prepare instance for use, adjustable number of iterations.
	 */
	public MatchingPursuitFeatureExtraction(int numberOfIterations) {
		this.instance = MatchingPursuit.getInstance();
		this.instance.setIterationCount(numberOfIterations);
	}
	
	
	@Override
	public double[] extractFeatures(double[][] epoch) {
		
		int numberOfChannels = CHANNELS.length;
		double[] signal = new double[getFeatureDimension()];
		double[] processingPart = new double[EPOCH_SIZE / DOWN_SMPL_FACTOR];
		
		int k = 0;
		for(int i = 0; i < numberOfChannels; i++) {
			for(int j = 0; j < EPOCH_SIZE / DOWN_SMPL_FACTOR; j++) {
				processingPart[j] = epoch[i][j * DOWN_SMPL_FACTOR + SKIP_SAMPLES];
			}
			processingPart = instance.processSignal(processingPart).getReconstruction();
			for(int j = 0; j < processingPart.length; j++) {
				signal[k] = processingPart[j];
				k++;
			}
		}

		return signal;
		
	}

	
	@Override
	public int getFeatureDimension() {
		int lenghtOfProcessedEpoch = EPOCH_SIZE / DOWN_SMPL_FACTOR;
		int i = 1;
		while(lenghtOfProcessedEpoch > Math.pow(2, i)) {
			i++;
		}
		return ((int) Math.pow(2, i) * CHANNELS.length);
	}
	
	
	/**
	 * Sets number of iterations.
	 * 
	 * Each iteration adds one atom.
	 * 
	 * @param iterationCount number of iterations
	 */
	public void setIterationCount(int iterationCount) {
		if (iterationCount < 1) {
			throw new IllegalArgumentException("Number of iterations must be >= 1");
		}
		this.instance.setIterationCount(iterationCount);
	}
	
	/**
	 * Gets number of iterations.
	 * 
	 * @return iterationCount number of iterations
	 */
	public int getIterationCount() {
		return this.instance.getIterationCount();
	}

}
