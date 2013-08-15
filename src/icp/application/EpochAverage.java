package icp.application;

/**
 * 
 * Implements ensamble averaging of epochs
 * defined as M channels x N samples
 * 
 * 
 * @author Lukas Vareka
 *
 */
public class EpochAverage {
	private double[][] epochSum;
	private int counter;
	
	public EpochAverage() {
		this.counter = 0;
	}
	
	
	/**
	 * 
	 * Average a new epoch
	 * 
	 * @param epoch
	 */
	public void addToAverage(double[][] epoch) {
		if (counter == 0) {
			this.epochSum = epoch;
		} else {
			// for each channel
			for (int i = 0; i < epoch.length; i++) {
				// for each sample
				for (int j = 0; j < epoch[0].length; j++) {
					epochSum[i][j] += epoch[i][j];
				}
			}
		
		}
		this.counter++;
		
	}
	
	/**
	 * 
	 * Calculates an averaged epoch
	 * 
	 * @return
	 */
	public double[][] getAveragedEpoch() {
		if (counter == 0)
			return null;
		double[][] epochAvg = new double[epochSum.length][epochSum[0].length];
		for (int i = 0; i < epochSum.length; i++) {
			for (int j = 0; j < epochSum[0].length; j++) {
				epochAvg[i][j] = epochSum[i][j] / counter;
			}
		}
		return epochAvg;
	}



	public int getCounter() {
		return counter;
	}
	
	public void reset() {
		this.counter = 0;
		this.epochSum = null;
	}
}
