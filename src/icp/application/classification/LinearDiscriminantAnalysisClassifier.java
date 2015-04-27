package icp.application.classification;

import java.util.List;

public class LinearDiscriminantAnalysisClassifier extends ERPClassifierAdapter {

	/**
	 * Feature extractor
	 */
	private IFeatureExtraction fe;

	private LinearDiscriminantAnalysisAlgorithms lda;

	public LinearDiscriminantAnalysisClassifier() {
		this.lda = new LinearDiscriminantAnalysisAlgorithms();
	}

	/**
	 * 
	 * Predefine feature extraction method
	 * 
	 * @param fe
	 */
	public void setFeatureExtraction(IFeatureExtraction fe) {
		this.fe = fe;
	}

	/**
	 * Train the classifier using information from the supervisor
	 * 
	 * @param epochs
	 *            - raw epochs - list of M channels x N time samples
	 * @param targets
	 *            - target classes - list of expected classes (0 or 1)
	 * @param numberOfiter
	 *            - number of training iterations
	 * @param fe
	 *            - method for feature extraction
	 */
	public void train(List<double[][]> epochs, List<Double> targets,
			int numberOfiter, IFeatureExtraction fe) {
		lda.train(epochs, targets, fe);
	}

	/**
	 * Test the classifier using the data with known resulting classes
	 * 
	 * @param epochs
	 *            - raw epochs - list of M channels x N time samples
	 * @param targets
	 *            - target classes - list of expected classes (0 or 1)
	 * @return
	 */
	public ClassificationStatistics test(List<double[][]> epochs,
			List<Double> targets) {
		ClassificationStatistics resultsStats = new ClassificationStatistics();

		for (int i = 0; i < epochs.size(); i++) {
			double[][] epoch = epochs.get(i);
			double result = this.classify(epoch);
			resultsStats.add(result, targets.get(i));
		}

		return resultsStats;
	}

	/**
	 *
	 * Calculate the output of the classifier for the selected epoch
	 * 
	 * @param epoch
	 *            - number of channels x temporal samples
	 * @return probability of the epoch to be target{} e.g. nontarget - 0,
	 *         target - 1
	 */
	public double classify(double[][] epoch) {
		double[] featureVector = this.fe.extractFeatures(epoch);
		return lda.classify(featureVector);
	}

	public void save(String file) {
		lda.save(file);
	}

	public void load(String file) {
		lda.load(file);
	}
}
