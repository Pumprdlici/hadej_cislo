package icp.application.classification;

import icp.Const;
import icp.algorithm.math.SignalProcessing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import hht.HhtSimpleRunner;
import hht.HilbertHuangTransform;
import hht.hilbertTransform.HilbertTransform;

/**
 * Class using HHT library with Hilbert-Huang Transformation algorithm for feature extraction.
 * @author Vlada47
 *
 */
public class HHTFeatureExtraction implements IFeatureExtraction {

	private static final int[] CHANNELS = {1, 2, 3};
	private static final int SKIP_SAMPLES = 200;
	private static final int EPOCH_SIZE = 512;
	private static final int SAMPLING_FREQUENCY = 1000;
	private static final int DOWN_SMPL_FACTOR = 6;
	private static final String EMD_CONF_FILE = "configs//emd//Cauchybest.xml";
	
	/**
	 * constant meaning that features will be used from hilbert transform amplitudes
	 */
	public static final int AMPLITUDE_FEATURES = 1;
	
	/**
	 * constant meaning that features will be used from hilbert transform frequencies
	 */
	public static final int FREQUENCY_FEATURES = 2;
	
	/**
	 * variable for storing, which array (amplitudes or frequencies) will be used from
	 * hilbert transforms as features
	 */
	private int typeOfFeatures = 1;

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
	 * variable for storing the threshold for the amplitude, from which is signal considered as P3 component
	 */
	private double amplitudeThreshold = 3.0;
	
	/**
	 * variable for storing minimal frequency of P3 component 
	 */
	private double minFreq = 0.2;
	
	/**
	 * variable for storing maximal frequency of P3 component 
	 */
	private double maxFreq = 3.0;

	@Override
	public double[] extractFeatures(double[][] epoch) {
		
		double[] featureVector = new double[(maxSample - minSample) * CHANNELS.length];
		int featureIndex = 0;
		
		for(int channel : CHANNELS) {
			double[] epochSamples = selectEpochSamples(epoch[channel - 1]);
			double[] processedFeatures = processFeatures(epochSamples);
			
			for(int i = 0; i < (maxSample - minSample); i++) {
				featureVector[i+featureIndex] = processedFeatures[i];
			}
			
			featureIndex += (maxSample - minSample);
		}
		
		featureVector = SignalProcessing.decimate(featureVector, DOWN_SMPL_FACTOR);
		featureVector = SignalProcessing.normalize(featureVector);
		
		return featureVector;
	}

	@Override
	public int getFeatureDimension() {
		return ((maxSample - minSample) * CHANNELS.length / DOWN_SMPL_FACTOR);
	}
	
	/**
	 * Method for selection of particular part of the input epoch depending on values of EPOCH_SIZE and SKIP_SAMPLES.
	 * @param epochChannel - original channel of the epoch
	 * @return array with selected samples
	 */
	private double[] selectEpochSamples(double[] epochChannel) {
		double[] epochSamples = new double[EPOCH_SIZE];
		
		for(int i = 0; i < EPOCH_SIZE; i++) {
			epochSamples[i] = epochChannel[i+SKIP_SAMPLES];
		}
		
		return epochSamples;
	}
	
	/**
	 * Method, which calls processing from HHT library, gets hilbert transforms of epoch samples
	 * and then selects suitable features from them by calling selectFeatures method.
	 * In case none hilbert transform will be returned, method returns original signal between specified samples.
	 * @param epochSamples - array of input samples, which will by processed by HHT library 
	 * @return array with processed features
	 */
	private double[] processFeatures(double[] epochSamples) {
		
		double[] processedFeatures = new double[maxSample-minSample];
		
		try{
			HilbertHuangTransform hht = HhtSimpleRunner.runHht(EMD_CONF_FILE , epochSamples, SAMPLING_FREQUENCY);
			Vector<HilbertTransform> hTransforms = hht.getHilbertTransform();
			
			if(hTransforms.size() > 0) {
				processedFeatures = Arrays.copyOfRange(selectFeatures(hTransforms), minSample, maxSample);
			}
			else {
				int featureIndex = 0;
				
				for(int i = (minSample-SKIP_SAMPLES); i < (maxSample-SKIP_SAMPLES); i++) {
					processedFeatures[featureIndex] = epochSamples[i];
					featureIndex++;
				}
			}
		}
		catch(Exception e) {
			System.out.println("Error, while processing epoch samples: "+e.getMessage());
			e.printStackTrace();
		}
		
		return processedFeatures;
	}
	
	/**
	 * Method for selecting most suitable features from input hilbert transforms.
	 * It iterates through all HilbertTransform objects and their amplitudes and frequency arrays.
	 * It creates a sample window of specific size, which is shifted through aforementioned arrays
	 * and for each shift calculates average value of read frequencies and amplitudes and stores them into
	 * an ArrayList.
	 * When all possible shifts has been made, ArrayLists are passed to methods for calculation of score,
	 * which represents how much are frequencies and amplitudes in specified array of samples similar
	 * to frequency and amplitude of P3 component. Score is saved in arrays, where index 0 match with index
	 * of HilbertTransform object with relevant frequencies and amplitudes.
	 * Based on typeOfFeatures variable is determined what type of features should be returned.
	 * Then it's decided what HilbertTranform is best to get features from (based on its score) and from the
	 * one selected, we get the desired features.
	 * @param hTransforms - Vector with HilbertTransform object, which holds arrays with frequencies and amplitudes
	 * of relevant IMFs
	 * @return features from selected hilbert transform
	 */
	private double[] selectFeatures(Vector<HilbertTransform> hTransforms) {
		double[] selectedFeatures;
		double[] htAmplitudesScore = new double[hTransforms.size()]; //array of best window amplitudes
		double[] htFrequenciesScore = new double[hTransforms.size()]; //array of best window frequencies
		
		for(int i = 0; i < hTransforms.size(); i++) {
			
			double[] amplitudes = hTransforms.get(i).getAmplitudes();
			double[] frequencies = hTransforms.get(i).getFrequency();
			
			int currIndex = minSample;
			ArrayList<Double> avgWindowAmplitudes = new ArrayList<Double>();
			ArrayList<Double> avgWindowFrequencies = new ArrayList<Double>();
			
			/*
			 * loop for iterating through one hilbert transform (amplitudes / frequencies arrays)
			 */
			while(currIndex <= maxSample) {
				int windowIndex = 0;
				double windowAmplitude = 0.0;
				double windowFrequency = 0.0;
				
				/*
				 * loop for iterating through window in amplitudes / frequencies arrays
				 */
				while(windowIndex < sampleWindowSize) {
					if(!Double.isNaN(frequencies[currIndex - SKIP_SAMPLES])) {
						windowFrequency += frequencies[currIndex - SKIP_SAMPLES];
					}
					windowAmplitude += amplitudes[currIndex - SKIP_SAMPLES];
					windowIndex++;
					currIndex++;
					if(currIndex >= maxSample) break;
				}
				
				avgWindowAmplitudes.add(windowAmplitude / (double)windowIndex);
				avgWindowFrequencies.add(windowFrequency / (double)windowIndex);
				
				if(currIndex >= maxSample) break;
				else {
					currIndex = currIndex - sampleWindowSize + sampleWindowShift;
				}
			}
			
			htAmplitudesScore[i] = getWindowAmplitudeScore(avgWindowAmplitudes);
			htFrequenciesScore[i] = getWindowFrequencyScore(avgWindowFrequencies);
		}
		
		switch(typeOfFeatures) {
		case AMPLITUDE_FEATURES: 
			selectedFeatures = hTransforms.get(selectIndexOfBestHT(htAmplitudesScore, htFrequenciesScore)).getAmplitudes();
			break;
		case FREQUENCY_FEATURES:
			selectedFeatures = hTransforms.get(selectIndexOfBestHT(htAmplitudesScore, htFrequenciesScore)).getFrequency();
			break;
		default:
			selectedFeatures = hTransforms.get(selectIndexOfBestHT(htAmplitudesScore, htFrequenciesScore)).getFrequency();
		}
		
		return selectedFeatures;
	}

	private double getWindowFrequencyScore(ArrayList<Double> avgWindowFrequencies) {
		double score = 0.0;
		
		for(double freq : avgWindowFrequencies) {
			if(freq >= minFreq && freq <= maxFreq) {
				score += 1.0;
			}
			else {
				if(freq > maxFreq) {
					score -= Math.abs(freq - maxFreq) / Math.abs(Double.MAX_VALUE - maxFreq);
				}
				else {
					score -= Math.abs(freq - minFreq) / Math.abs(Double.MIN_VALUE - minFreq);
				}
			}
		}
		return score;
	}

	private double getWindowAmplitudeScore(ArrayList<Double> avgWindowAmplitudes) {
		double score = 0.0;
		
		for(double amp : avgWindowAmplitudes) {
			if(amp >= amplitudeThreshold) {
				score += 1.0;
			}
			else {
				score -= Math.abs(amp - amplitudeThreshold) / Math.abs(Double.MIN_VALUE - amplitudeThreshold);
			}
		}
		
		return score;
	}
	
	private int selectIndexOfBestHT(double[] htAmplitudesScore, double[] htFrequenciesScore) {
		int index = 0;
		double bestScore = Double.MIN_VALUE;
		
		for(int i = 0; i < htAmplitudesScore.length; i++) {
			double combinedScore = (htAmplitudesScore[i] + htFrequenciesScore[i]) / 2.0;
			
			if(combinedScore > bestScore) {
				bestScore = combinedScore;
				index = i;
			}
		}
		
		return index;
	}
	
	/**
	 * Setter for desired type of features (frequency or amplitudes of hilbert transformations).
	 * @param typeOfFeatures - desired type of features
	 */
	public void setTypeOfFeatures(int typeOfFeatures) {
		this.typeOfFeatures = typeOfFeatures;
	}
	
	/**
	 * Setter for minimal index of IMF array, from which should be algorithm determining desired features.
	 * @param minSample - starting sample of particular IMF, from which will be features evaluated.
	 */
	public void setMinSample(int minSample) {
		if(minSample < EPOCH_SIZE && minSample >= 0) {
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
		if(maxSample < EPOCH_SIZE && maxSample >= 0)
		{
			this.maxSample = maxSample;
		}
		else {
			throw new IllegalArgumentException("Wrong input value! Max. sample has to be in the interval <0, "+Const.POSTSTIMULUS_VALUES+").");
		}
	}
	
	/**
	 * Setter for minimal amplitude threshold of desired feature.
	 * @param amplitudeThreshold - minimal amplitude threshold
	 */
	public void setAmplitudeThreshold(double amplitudeThreshold) {
		this.amplitudeThreshold = amplitudeThreshold;
	}
	
	/**
	 * Setter for minimal frequency of desired feature.
	 * @param minFreq - minimal frequency of desired feature
	 */
	public void setMinFreq(double minFreq) {
		if(minFreq > 0.0) {
			this.minFreq = minFreq;
		}
		else {
			throw new IllegalArgumentException("Wrong input value! Frequency must have positive value.");
		}
	}
	
	/**
	 * Setter for maximal frequency of desired feature.
	 * @param maxFreq - maximal frequency of desired feature
	 */
	public void setMaxFreq(double maxFreq) {
		if(maxFreq > 0.0) {
			this.maxFreq = maxFreq;
		}
		else {
			throw new IllegalArgumentException("Wrong input value! Frequency must have positive value.");
		}
		
	}

	/**
	 * Setter for value representing size of the window, for which is average amplitude calculated.
	 * @param sampleWindowSize - size of the sample window
	 */
	public void setSampleWindowSize(int sampleWindowSize) {
		if(sampleWindowSize > 0 && sampleWindowSize <= EPOCH_SIZE) {
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
		if(sampleWindowShift > 0 && sampleWindowShift < EPOCH_SIZE) {
			this.sampleWindowShift = sampleWindowShift;
		}
		else {
			throw new IllegalArgumentException("Wrong input value! Shift of the sample window has to be in the interval (0, "+Const.POSTSTIMULUS_VALUES+").");
		}
	}
	
	public int getTypeOfFeatures() {
		return typeOfFeatures;
	}

	public int getMinSample() {
		return minSample;
	}

	public int getMaxSample() {
		return maxSample;
	}

	public int getSampleWindowSize() {
		return sampleWindowSize;
	}

	public int getSampleWindowShift() {
		return sampleWindowShift;
	}

	public double getAmplitudeThreshold() {
		return amplitudeThreshold;
	}

	public double getMinFreq() {
		return minFreq;
	}

	public double getMaxFreq() {
		return maxFreq;
	}
}
