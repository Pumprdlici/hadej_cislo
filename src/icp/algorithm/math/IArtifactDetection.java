package icp.algorithm.math;

import icp.online.app.EpochMessenger;

/**
 * Interface which makes sure every artifact detector
 * implements detection method.
 * 
 * @author Jan Vampol
 * @version 1.00
 */
public interface IArtifactDetection {
	public EpochMessenger detectArtifact(EpochMessenger epoch);
}
