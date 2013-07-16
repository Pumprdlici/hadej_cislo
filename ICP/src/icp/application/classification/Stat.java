package icp.application.classification;
 
/**
 * 
 * Holds results of classification
 * if the target data with expected classes
 * is provided.
 * Allows to calculated accuracy.
 * 
 * @author Lukas Vareka
 *
 */
public class Stat {
	private int truePositives;
	private int trueNegatives;
	private int falsePositives;
	private int falseNegatives;
	
	
	public Stat(int truePositives, int trueNegatives, int falsePositives, int falseNegatives) {
		this.truePositives = truePositives;
		this.trueNegatives = trueNegatives;
		this.falsePositives = falsePositives;
		this.falseNegatives = falseNegatives;
	}
	
	public double calcAccuracy() {
		return ((double) truePositives + trueNegatives) / (truePositives + trueNegatives + falsePositives + falseNegatives);
	}
	

}
