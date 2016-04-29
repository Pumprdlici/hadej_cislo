package icp.application.classification;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;



/**
 * 
 * Interface for supervised classifiers
 * in the field of ERP classification
 * 
 * @author Lukas Vareka
 *
 */
public interface IERPClassifier {
	
	/**
	 * 
	 * Predefine feature extraction method
	 * 
	 * @param fe
	 */
	public void setFeatureExtraction(IFeatureExtraction fe); 
	
	/**
	 * Train the classifier using information from the supervizor
	 * @param epochs raw epochs  - list of M channels x N time samples
	 * @param targets target classes - list of expected classes (0 or 1)
	 * @param numberOfiter number of training iterations
	 * @param fe method for feature extraction
	 */
	public void train(List<double[][]> epochs, List<Double> targets,  int numberOfiter, IFeatureExtraction fe);
	
	/**
	 * Test the classifier using the data with known resulting classes
	 * @param epochs raw epochs  - list of M channels x N time samples
	 * @param targets target classes - list of expected classes (0 or 1)
	 * @return
	 */
	public ClassificationStatistics test(List<double[][]> epochs, List<Double> targets);
	
	/**
	 *
	 * Calculate the output of the classifier for the selected epoch
	 * 
	 * @param epoch - number of channels x temporal samples
	 * @return  - probability of the epoch to be target; e.g. nontarget - 0, target - 1
	 */
	public double classify(double[][] epoch);
	
	/**
	 * 
	 * Load the classifier from configuration
	 * @param is configuration of the classifier
	 */
	public void load(InputStream is);
	
	/**
	 * Save the classifier
	 * @param dest destination stream
	 */
	public void save(OutputStream dest);
	
	public void save(String file);
	
	public void load(String file);

	public IFeatureExtraction getFeatureExtraction();
	
	public void loadConf();
}
