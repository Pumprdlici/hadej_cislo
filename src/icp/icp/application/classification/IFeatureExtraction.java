package icp.application.classification;

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
	 * @param epoch source epoch 
	 * @return feature vector
	 */
	public double[] extractFeatures(double[][] epoch);
	public int getFeatureDimension();
}
