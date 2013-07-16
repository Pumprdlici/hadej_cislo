package icp.application.classification;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Interface for supervised classifiers
 * 
 * @author Lukas Vareka
 *
 */
public interface IP300Classifier {
	
	/**
	 * 
	 * Predefine feature extraction method
	 * 
	 * @param fe
	 */
	public void init(IFeatureExtraction fe); 
	
	public void train(List<double[][]> epochs, List<Double> targets, IFeatureExtraction fe);
	
	public Stat test(List<double[][]> epochs, List<Double> targets);
	
	/**
	 *
	 * Calculated the output of the classifier for the selected epoch
	 * 
	 * @param epoch - number of channels x temporal samples
	 * @return  - probability of the epoch to be target; e.g. nontarget - 0, target - 0
	 */
	public double classify(List<double[]> epoch);
}
