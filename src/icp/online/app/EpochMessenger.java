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
		this.epoch = new double[3][IDataProvider.POCETHODNOTZAEPOCHOU];
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
	
	public void setFZ(float[] fz, int offset) {
		for (int i = 0; i < IDataProvider.POCETHODNOTZAEPOCHOU; i++)
			epoch[0][i] = (double) fz[i + offset];
		
	}
	
	public void setCZ(float[] cz, int offset) {
		for (int i = 0; i < IDataProvider.POCETHODNOTZAEPOCHOU; i++)
			epoch[1][i] = (double) cz[i + offset];
		
	}
	
	public void setPZ(float[] pz, int offset) {
		for (int i = 0; i < IDataProvider.POCETHODNOTZAEPOCHOU; i++)
			epoch[2][i] = (double) pz[i + offset];
		
	}
	@Override
	public String toString()
	{
		return	"FZ: " + Arrays.toString(epoch[0]) + "\n" + 
				"CZ: " + Arrays.toString(epoch[1]) + "\n" + 
				"PZ: " + Arrays.toString(epoch[2]) + "\n\n";
	}
}
