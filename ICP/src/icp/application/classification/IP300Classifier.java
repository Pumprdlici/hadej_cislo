package icp.application.classification;

import icp.data.Epoch;

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
	 * Calculated the output of the classifier for the selected epoch
	 * 
	 * @param epoch
	 * @return  - probability of the epoch to be target; e.g. nontarget - 0, target - 0
	 */
	public double classify(Epoch epoch);
}
