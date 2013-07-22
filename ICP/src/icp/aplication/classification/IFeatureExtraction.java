package icp.aplication.classification;

import java.util.List;

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
	public double[] extractFeatures(List<double[]> epoch);
}
