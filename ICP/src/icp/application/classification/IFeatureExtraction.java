package icp.application.classification;

import icp.data.Epoch;

/**
 * 
 * Interface for extracting features from an epoch
 * 
 * @author Lukas Vareka
 *
 */
public interface IFeatureExtraction {
	
	/**
	 * 
	 * @param epoch
	 * @return feature vector
	 */
	public double[] extractFeatures(Epoch epoch);
}
