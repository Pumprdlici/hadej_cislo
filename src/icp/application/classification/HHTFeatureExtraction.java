package icp.application.classification;

import icp.Const;
import icp.algorithm.math.SignalProcessing;
import java.util.Vector;
import hht.HhtSimpleRunner;
import hht.HilbertHuangTransform;

/**
 * Class using HHT library with Hilbert-Huang Transformation algorithm for feature extraction.
 * @author Vlada47
 *
 */
public class HHTFeatureExtraction implements IFeatureExtraction {
	
	private static final int[] CHANNELS = {1, 2, 3};
	private static final int SKIP_SAMPLES = 200;
	private static final int EPOCH_SIZE = 300;
	private static final int SAMPLING_FREQUENCY = 1000;
	private static final int DOWN_SMPL_FACTOR = 2;
	private static final String EMD_CONF_FILE = "configs//emd//Cauchybest.xml";
	
	/**
	 * variable for storing the index of the first sample of evaluated signal
	 */
	private int minSample = 200;
	
	/**
	 * variable for storing the index of the last sample of evaluated signal
	 */
	private int maxSample = 500;
	
	/**
	 * variable for storing number of samples, which will be evaluated in one shift
	 */
	private int sampleWindowSize = 150;
	
	/**
	 * variable for storing the number of samples, which will be the window shifted for 
	 */
	private int sampleWindowShift = 5;
	
	/**
	 * variable for storing the average amplitude of P3 wave
	 */
	private double optimalAvgAmplitude = 3.0;

	@Override
	public double[] extractFeatures(double[][] epoch) {
		
		double[] features = new double[EPOCH_SIZE * CHANNELS.length]; //feature vector
		int featureIndex = 0; //index for saving selected IMFs into feature vector
		
		/*
		 * epoch is divided into its channels
		 */
		for(int channel : CHANNELS) {
			try {
				/*
				 * calling the method of HHT library and getting decomposed signal through EMD (IMFs)
				 */
				HilbertHuangTransform hht = HhtSimpleRunner.runHht(EMD_CONF_FILE , epoch[channel - 1], SAMPLING_FREQUENCY);
				Vector<double[]> imfs = hht.getImfs();
				double[] selectedImf;
				
				/*
				 * if no IMFs aren't returned, the original signal will be used instead
				 */
				if(imfs.size() > 0) {
					/*
					 * calling the method for selection of best IMF
					 */
					selectedImf = selectImf(imfs);
				}
				else {
					selectedImf = epoch[channel - 1];
				}
				
				/*
				 * adding selected IMF to feature vector
				 */
				for(int i = 0; i < EPOCH_SIZE; i++) {
					features[i+featureIndex] = selectedImf[i+SKIP_SAMPLES];
				}
				featureIndex += EPOCH_SIZE;	
			} 
			catch (Exception e) {
				e.printStackTrace();
			}	
		}
		
		/*
		 * sub-sampling of the feature vector to get acceptable dimension
		 */
		features = SignalProcessing.decimate(features, DOWN_SMPL_FACTOR);
		
		/*
		 * normalize of final feature vector
		 */
		features = SignalProcessing.normalize(features);
		return features;
	}

	@Override
	public int getFeatureDimension() {
		return (EPOCH_SIZE * CHANNELS.length / DOWN_SMPL_FACTOR);
	}
	
	/**
	 * Method for selection of the best IMF suited to set parameters. If reads individual samples of
	 * each IMF vector and calculates average amplitude in window of samples of set size in set interval of the vector.
	 * Then it compares these average "window amplitudes" and the one closest to set optimal value is saved into an array
	 * to represents best average amplitude of particular IMF. From this array is again selected the amplitude
	 * with value closest to set optimum and index of this element is used as index for selecting the best IMF.
	 * @param imfs - Vector with IMFs got from EMD decomposition
	 * @return IMF that has average amplitude closest to set optimum
	 */
	private double[] selectImf(Vector<double[]> imfs) {
		double[] selectedImf;
		double[] avgAmplitudes = new double[imfs.size()];
			
		for(int i = 0; i < imfs.size(); i++) {
				
			int currIndex = minSample;
			double bestWindowAmplitude = Double.MAX_VALUE;
				
			while(currIndex <= maxSample) {
				int windowIndex = 0;
				double avgWindowAmplitude = 0.0;
				
				//System.out.println(currIndex);
					
				while(windowIndex < sampleWindowSize) {
					avgWindowAmplitude += imfs.get(i)[currIndex];
					windowIndex++;
					currIndex++;
					if(currIndex >= maxSample) break;
				}
				avgWindowAmplitude = avgWindowAmplitude / (double)windowIndex;
					
				if(Math.abs(optimalAvgAmplitude - avgWindowAmplitude) < Math.abs(optimalAvgAmplitude - bestWindowAmplitude)) {
					bestWindowAmplitude = avgWindowAmplitude;
				}
				
				if(currIndex >= maxSample) break;
				else {
					currIndex = currIndex - sampleWindowSize + sampleWindowShift;
				}
			}
			avgAmplitudes[i] = bestWindowAmplitude;
		}
			
		int bestAmplitudeIndex = selectBestAmplitudeIndex(avgAmplitudes);
		selectedImf = imfs.get(bestAmplitudeIndex);
		return selectedImf;
	}

		
		
	
	/**
	 * Method for selection of an index of the array containing average amplitudes of IMFs.
	 * This index should correlate with index of particular IMF in the collection with IMFs.
	 * @param amplitudes - an array with average amplitudes
	 * @return index of the best amplitude
	 */
	private int selectBestAmplitudeIndex(double[] amplitudes) {
		int index = 0;
		double bestAmplitude = Double.MAX_VALUE;
		
		for(int i = 0; i < amplitudes.length; i++){
			if(Math.abs(optimalAvgAmplitude - amplitudes[i]) < Math.abs(optimalAvgAmplitude - bestAmplitude)) {
				bestAmplitude = amplitudes[i];
				index = i;
			}
		}
		return index;
	}
	
	/**
	 * Setter for minimal index of IMF array, from which should be algorithm determining desired features.
	 * @param minSample - starting sample of particular IMF, from which will be features evaluated.
	 */
	public void setMinSample(int minSample) {
		if(minSample < Const.POSTSTIMULUS_VALUES && minSample >= 0) {
			this.minSample = minSample;
		}
		else {
			throw new IllegalArgumentException("Wrong input value! Max. sample has to be in the interval <0, "+Const.POSTSTIMULUS_VALUES+").");
		}
	}

	/**
	 * Setter for maximal index of IMF array, from which should be algorithm determining desired feature.
	 * @param maxSample - ending sample of particular IMF, to which will be features evaluated.
	 */
	public void setMaxSample(int maxSample) {
		if(maxSample < Const.POSTSTIMULUS_VALUES && maxSample >= 0)
		{
			this.maxSample = maxSample;
		}
		else {
			throw new IllegalArgumentException("Wrong input value! Max. sample has to be in the interval <0, "+Const.POSTSTIMULUS_VALUES+").");
		}
	}
	
	/**
	 * Setter for optimal average amplitude of desired feature.
	 * @param optimalAvgAmplitude - optimal average amplitude
	 */
	public void setOptimalAvgAmplitude(double optimalAvgAmplitude) {
		this.optimalAvgAmplitude = optimalAvgAmplitude;
	}
	
	/**
	 * Setter for value representing size of the window, for which is average amplitude calculated.
	 * @param sampleWindowSize - size of the sample window
	 */
	public void setSampleWindowSize(int sampleWindowSize) {
		if(sampleWindowSize > 0 && sampleWindowSize < Const.POSTSTIMULUS_VALUES) {
			this.sampleWindowSize = sampleWindowSize;
		}
		else {
			throw new IllegalArgumentException("Wrong input value! Size of the sample window has to be in the interval (0, "+Const.POSTSTIMULUS_VALUES+").");
		}
	}
	
	/**
	 * Setter for value representing number of samples, for which will be the sample window
	 * shifted in each cycle.
	 * @param sampleWindowShift - number of samples for shift of the sample window
	 */
	public void setSampleWindowShift(int sampleWindowShift) {
		if(sampleWindowShift > 0 && sampleWindowShift < Const.POSTSTIMULUS_VALUES) {
			this.sampleWindowShift = sampleWindowShift;
		}
		else {
			throw new IllegalArgumentException("Wrong input value! Shift of the sample window has to be in the interval (0, "+Const.POSTSTIMULUS_VALUES+").");
		}
	}
}
