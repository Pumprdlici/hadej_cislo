package icp.online.app;


/**
 * Serves as a data container transfered to observers
 * 
 * @author Lukas Vareka
 *
 */
public class EpochMessenger {
	/**
	 * Channels [Fz, Cz, Pz] * time samples 	
	 */
	private double[][] epoch; // klasifikator pracuje s polem typu double, ne float
	
	/**
	 * Guessed number-related stimulus 0 - 8
	 */
	private int stimulusIndex;
	
	public EpochMessenger(double[][] epoch, int stimulusIndex) {
		this.epoch = epoch;
		this.stimulusIndex = stimulusIndex;
	}

	public double[][] getEpoch() {
		return epoch;
	}

	public int getStimulusIndex() {
		return stimulusIndex;
	}

	
}
