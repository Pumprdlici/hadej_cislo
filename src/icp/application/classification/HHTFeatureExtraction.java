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
 */
public class HHTFeatureExtraction implements IFeatureExtraction {
	
	private static final int[] CHANNELS = {1, 2, 3};
	
	/**
	 * configuration file for EMD decomposition
	 */
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
	 * variable, which sets the number of samples of original epoch that will be skipped
	 */
	private int skipSamples = 200;
	
	/**
	 * variable, which sets number of samples of original epoch that will be used
	 */
	private int epochSize = 512;
	
	/**
	 * variable, which sets a factor for sub-sampling method that is used on output features 
	 */
	private int downSmplFactor = 4;
	
	/**
	 * variable for storing, which array (amplitudes or frequencies) will be used from
	 * hilbert transforms as features
	 */
	private int typeOfFeatures = 1;
	
	/**
	 * variable for storing number of samples, which will be evaluated in one shift
	 */
	private int sampleWindowSize = 256;
	
	/**
	 * variable for storing the number of samples, which will be the window shifted for 
	 */
	private int sampleWindowShift = 8;
	
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
		
		double[] featureVector = new double[epochSize * CHANNELS.length];
		int featureIndex = 0;
		
		for(int channel : CHANNELS) {
			double[] epochSamples = Arrays.copyOfRange(epoch[channel - 1], skipSamples, skipSamples + epochSize);
			double[] processedFeatures = processFeatures(epochSamples);
			
			for(int i = 0; i < epochSize; i++) {
				featureVector[i+featureIndex] = processedFeatures[i];
			}
			
			featureIndex += epochSize;
		}
		
		featureVector = SignalProcessing.decimate(featureVector, downSmplFactor);
		featureVector = SignalProcessing.normalize(featureVector);
		
		return featureVector;
	}

	@Override
	public int getFeatureDimension() {
		return (epochSize * CHANNELS.length / downSmplFactor);
	}
	
	/**
	 * Method, which calls processing from HHT library, gets Hilbert transforms of epoch samples
	 * and then selects suitable features from them by calling selectFeatures method.
	 * In the case none Hilbert transform will be returned, method returns original signal for set epoch size.
	 * @param epochSamples - array of input samples, which will by processed by HHT library 
	 * @return array with processed features
	 */
	private double[] processFeatures(double[] epochSamples) {
		
		double[] processedFeatures = new double[epochSize];
		
		try{
			HilbertHuangTransform hht = HhtSimpleRunner.runHht(EMD_CONF_FILE , epochSamples, Const.SAMPLING_FQ);
			Vector<HilbertTransform> hTransforms = hht.getHilbertTransform();
			
			if(hTransforms.size() > 0) {
				processedFeatures = Arrays.copyOf(selectFeatures(hTransforms), epochSize);
			}
			else {
				processedFeatures = epochSamples;
			}
		}
		catch(Exception e) {
			System.out.println("Error, while processing epoch samples: "+e.getMessage());
			e.printStackTrace();
		}
		
		return processedFeatures;
	}
	
	/**
	 * Method for selecting the most suitable features from input Hilbert transformations.
	 * It iterates through all HilbertTransform objects and their amplitudes and frequency arrays.
	 * It creates a sample window of specific size, which is shifted through aforementioned arrays
	 * and for each shift calculates average value of read frequencies and amplitudes and stores them into
	 * an {@link ArrayList}.
	 * When all possible shifts have been made, ArrayLists are passed to methods for calculation of the score,
	 * which represents how much are frequencies and amplitudes in array of samples similar
	 * to frequency and amplitude of P3 component. Score is saved in arrays, where index match with index in Vector
	 * of HilbertTransform object with relevant frequencies and amplitudes.
	 * Based on typeOfFeatures variable is determined what type of features should be returned.
	 * Then it's decided what HilbertTranform is best to get features from (based on its score) and from the
	 * one selected, we get the desired features.
	 * @param hTransforms - {@link Vector} with HilbertTransform objects, which hold arrays with frequencies and amplitudes
	 * of relevant IMFs
	 * @return features from selected Hilbert transform
	 */
	private double[] selectFeatures(Vector<HilbertTransform> hTransforms) {
		double[] selectedFeatures;
		double[] htAmplitudesScore = new double[hTransforms.size()]; //array of amplitude score for each HilbertTranform
		double[] htFrequenciesScore = new double[hTransforms.size()]; //array frequency score for each HilbertTranform
		
		for(int i = 0; i < hTransforms.size(); i++) {
			
			double[] amplitudes = hTransforms.get(i).getAmplitudes();
			double[] frequencies = hTransforms.get(i).getFrequency();
			
			int currIndex = 0;
			ArrayList<Double> avgWindowAmplitudes = new ArrayList<Double>();
			ArrayList<Double> avgWindowFrequencies = new ArrayList<Double>();
			
			/*
			 * loop for iterating through one hilbert transform (amplitudes / frequencies arrays)
			 */
			while(currIndex < epochSize) {
				int windowIndex = 0;
				int windowAmplitudeIndex = 0;
				int windowFrequencyIndex = 0;
				double windowAmplitude = 0.0;
				double windowFrequency = 0.0;
				
				/*
				 * loop for iterating through window in amplitudes / frequencies arrays
				 */
				while(windowIndex < sampleWindowSize) {
					if(!Double.isNaN(frequencies[currIndex])) {
						windowFrequency += frequencies[currIndex];
						windowFrequencyIndex++;
					}
					if(!Double.isNaN(amplitudes[currIndex])) {
						windowAmplitude += amplitudes[currIndex];
						windowAmplitudeIndex++;
					}
					windowIndex++;
					currIndex++;
					if(currIndex >= epochSize-1) break;
				}
				
				avgWindowAmplitudes.add(windowAmplitude / (double)windowAmplitudeIndex);
				avgWindowFrequencies.add(windowFrequency / (double)windowFrequencyIndex);
				
				if(currIndex >= epochSize-1) break;
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
	
	/**
	 * Method for calculation of the score for frequencies of current Hilbert transform.
	 * It starts at 0.0 and increases by 1.0 for every frequency in avgWindowFrequencies {@link ArrayList}
	 * that is between minFreq and maxFreq variables. It decreases in interval between 0.0 and 1.0
	 * for every frequency that isn't between those variables. More difference is between the frequency and
	 * those numbers, the more will be the score decreased (by 1.0 should the frequency be Double.MAX_VALUE or Double.MIN_VALUE). 
	 * @param avgWindowFrequencies - {@link ArrayList} with average frequencies gotten from sample window
	 * @return final score for frequencies of current Hilbert transform
	 */
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
	
	/**
	 * Method for calculation of the score for amplitudes of current Hilbert transform.
	 * It starts at 0.0 and increases by 1.0 for every amplitude in avgWindowAmplitudes {@link ArrayList}
	 * that is at least equals to amplitudeThreshold variable. It decreases in interval between 0.0 and 1.0
	 * for every amplitude that is lesser than that variable. More difference is between the amplitude and
	 * the variable, the more will be the score decreased (by 1.0 should the amplitude be Double.MIN_VALUE). 
	 * @param avgWindowAmplitudes - {@link ArrayList} with average amplitudes gotten from sample window
	 * @return final score for amplitudes of current Hilbert transform
	 */
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
	
	/**
	 * This method determines the index of the best Hilbert transform via score of theirs amplitudes and frequencies.
	 * It iterates through arrays with scores, calculates arithmetic average for scores on the same index
	 * and the compares it with the last greatest score and sets the index.
	 * @param htAmplitudesScore - array with scores for amplitudes of all transforms
	 * @param htFrequenciesScore - array with scores for frequencies of all transforms
	 * @return index (in {@link Vector}) of HilberTransform with greatest score
	 */
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
	
	public void setSkipSamples(int skipSamples) {
		if(skipSamples >= 0) {
			this.skipSamples = skipSamples;
		}
		else {
			throw new IllegalArgumentException("Wrong input value! You cannot skip negative number of samples.");
		}
	}
	
	public void setEpochSize(int epochSize) {
		if(epochSize > 0) {
			this.epochSize = epochSize;
		}
		else {
			throw new IllegalArgumentException("Wrong input value! Size of the epoch has to be positive.");
		}
	}
	
	public void setDownSmplFactor(int downSmplFactor) {
		if(downSmplFactor >= 0) {
			this.downSmplFactor = downSmplFactor;
		}
		else {
			throw new IllegalArgumentException("Wrong input value! You cannot set negative sub-sampling factor.");
		}
	}

	public void setTypeOfFeatures(int typeOfFeatures) {
		this.typeOfFeatures = typeOfFeatures;
	}

	public void setAmplitudeThreshold(double amplitudeThreshold) {
		this.amplitudeThreshold = amplitudeThreshold;
	}
	
	public void setMinFreq(double minFreq) {
		if(minFreq > 0.0) {
			this.minFreq = minFreq;
		}
		else {
			throw new IllegalArgumentException("Wrong input value! Frequency must have positive value.");
		}
	}
	
	public void setMaxFreq(double maxFreq) {
		if(maxFreq > 0.0) {
			this.maxFreq = maxFreq;
		}
		else {
			throw new IllegalArgumentException("Wrong input value! Frequency must have positive value.");
		}
		
	}

	public void setSampleWindowSize(int sampleWindowSize) {
		if(sampleWindowSize > 0) {
			this.sampleWindowSize = sampleWindowSize;
		}
		else {
			throw new IllegalArgumentException("Wrong input value! Size of the sample window has to be greater than 0.");
		}
	}
	
	public void setSampleWindowShift(int sampleWindowShift) {
		if(sampleWindowShift > 0) {
			this.sampleWindowShift = sampleWindowShift;
		}
		else {
			throw new IllegalArgumentException("Wrong input value! Shift of the sample window has to be greater than 0.");
		}
	}
	
	public int getSkipSamples() {
		return skipSamples;
	}
	
	public int getEpochSize() {
		return epochSize;
	}
	
	public int getDownSmplFactor() {
		return downSmplFactor;
	}
	
	public int getTypeOfFeatures() {
		return typeOfFeatures;
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