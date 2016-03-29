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
	private int epochSize = 512;
	
	/**
	 * Subsampling factor
	 */
	private int downSmplFactor = 8;

	/**
	 * Skip initial samples in each epoch
	 */
	private int skipSamples = 200;
	
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
		double[] processingPart = new double[epochSize / downSmplFactor];
		
		int k = 0;
		for(int i = 0; i < numberOfChannels; i++) {
			for(int j = 0; j < epochSize / downSmplFactor; j++) {
				processingPart[j] = epoch[i][j * downSmplFactor + skipSamples];
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
		int lenghtOfProcessedEpoch = epochSize / downSmplFactor;
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
	 * Sets size of epoch to use for feature extraction
	 * 
	 * @param epochSize	size of epoch to use
	 */
	public void setEpochSize(int epochSize) {
		if (epochSize > 0) {
			this.epochSize = epochSize;
		} else {
			throw new IllegalArgumentException("Epoch Size must be > 0");
		}
	}
	
	/**
	 * Setter for downSmplFactor attribute. It requires value greater than 0.
	 * @param downSmplFactor
	 * @throws IllegalArgumentException
	 */
	public void setDownSmplFactor(int downSmplFactor) {
		if(downSmplFactor >= 1) {
			this.downSmplFactor = downSmplFactor;
		}
		else {
			throw new IllegalArgumentException("Wrong input value! Sub-sampling factor must be >= 1.");
		}
	}
	
	/**
	 * Setter for skipSamples attribute. It requires value equal or greater than 0.
	 * @param skipSamples
	 * @throws IllegalArgumentException
	 */
	public void setSkipSamples(int skipSamples) {
		if(skipSamples >= 0) {
			this.skipSamples = skipSamples;
		}
		else {
			throw new IllegalArgumentException("Number of skip samples must be >=0.");
		}
	}
	
	/**
	 * Gets number of iterations.
	 * 
	 * @return iterationCount number of iterations
	 */
	public int getIterationCount() {
		return this.instance.getIterationCount();
	}
	
	/**
	 * Getter for epochSize attribute.
	 * @return epochSize epochSize
	 */
	public int getEpochSize() {
		return epochSize;
	}
	
	/**
	 * Getter for downSmplFactor attribute.
	 * @return downSmplFactor downSmplFactor
	 */
	public int getDownSmplFactor() {
		return downSmplFactor;
	}
	
	/**
	 * Getter for skipSamples attribute.
	 * @return skipSamples downSmplFactor
	 */
	public int getSkipSamples() {
		return skipSamples;
	}

}
