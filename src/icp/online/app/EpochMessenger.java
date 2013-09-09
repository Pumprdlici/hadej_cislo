package icp.online.app;

import java.util.Arrays;


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
	 * Guessed number-related stimulus 1 - 9
	 */
	private int stimulusIndex;
	
	public EpochMessenger() {
		this.epoch = new double[3][512];
		this.stimulusIndex = -1;
	}
	
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
	
	public void setStimulusIndex(int stimulusIndex)
	{
		this.stimulusIndex = stimulusIndex;
	}
	
	public void setFZ(float[] fz) {
		double[] fzD =	new double[fz.length];
		
		for (int i = 0; i < fzD.length; i++)
			fzD[i] = (double) fz[i];
		
		epoch[0] = fzD;
	}
	
	public void setCZ(float[] cz) {
        double[] czD =	new double[cz.length];
		
		for (int i = 0; i < czD.length; i++)
			czD[i] = (double) cz[i];
		
		epoch[1] = czD;
	}
	
	public void setPZ(float[] pz) {
		 double[] pzD =	new double[pz.length];
			
		for (int i = 0; i < pzD.length; i++)
			pzD[i] = (double) pz[i];
		
		epoch[2] = pzD;
	}
	@Override
	public String toString()
	{
		return	"FZ: " + Arrays.toString(epoch[0]) + "\n" + 
				"CZ: " + Arrays.toString(epoch[1]) + "\n" + 
				"PZ: " + Arrays.toString(epoch[2]) + "\n\n";
	}
}
