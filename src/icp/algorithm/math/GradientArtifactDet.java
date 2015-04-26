package icp.algorithm.math;

import icp.online.app.EpochMessenger;

/**
 * Class for removal of artifacts in EEG signal using the 
 * gradient method. The EEG signal is averaged and than each 
 * individual point is compared with the average.
 * 
 * @author Michal Veverka
 * @version 1.00
 */
public class GradientArtifactDet implements IArtifactDetection{
	
	/**
	 * The default maximal difference.
	 */
	private static final double DEFAULT_MAXDIFF = 100;
	
	/**
	 * The maximal difference between average and value of a point 
	 * in the epoch.
	 */
	private double maxDiff;
	
	/**
	 * Creates an instance of GradientArtifactDet. The maximal difference is set to {@link #DEFAULT_MAXDIFF}.
	 */
	public GradientArtifactDet(){
		this.maxDiff = DEFAULT_MAXDIFF;
	}
	
	/**
	 * Creates an instance of GradientArtifactDet with the maximal difference given as parameter. 
	 * If the given parameter is lower than zero, then the {@link #DEFAULT_MAXDIFF} is used instead.
	 */
	public GradientArtifactDet(double maxDiff){
		if(maxDiff < 0)this.maxDiff = DEFAULT_MAXDIFF;
		else this.maxDiff = maxDiff;
	}
	
	/**
	 * The points in the epoch are averaged and than one by one compared to the average. If the 
	 * difference between the value of the epoch point and the average is higher than the maximal difference 
	 * then null is returned instead.
	 * 
	 * @param epoch Epoch from which to remove artifacts.
	 * @return Epoch or null.
	 */
	public EpochMessenger detectArtifact(EpochMessenger epochMes){
		double[][] epoch = epochMes.getEpoch();
		for(int channel = 0; channel<epoch.length; channel++){
			double average = 0;
			for(int point = 0; point<epoch[0].length; point++){
				average++;
			}
			average /= epoch[0].length;
			for(int point = 0; point < epoch[0].length; point++){
				double diff = Math.abs(epoch[channel][point] - average);
				if(diff > this.maxDiff) return null;
			}
		}
		return epochMes;
	}
	
	/**
	 * Returns the threshold (maximal difference between average of epoch values and 
	 * one point in epoch).
	 * 
	 * @return The threshold.
	 */
	public double getMaxDiff(){
		return this.maxDiff;
	}
	
	/**
	 * Sets the threshold (maximal difference between average of epoch values and 
	 * one point in epoch). If the given parameter is lower than zero, the threshold 
	 * is set to default value of {@link #DEFAULT_MAXDIFF}.
	 * 
	 * @param threshold
	 */
	public void setMaxDiff(double maxDiff){
		if(maxDiff < 0){
			this.maxDiff = DEFAULT_MAXDIFF;
		}
	}
}

