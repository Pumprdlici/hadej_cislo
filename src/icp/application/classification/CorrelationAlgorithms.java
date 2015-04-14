package icp.application.classification;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Implementation correlation of two signals.
 * @author Karel Silhavy
 *
 */
public class CorrelationAlgorithms {

	/**
	 * Path to the file which contains waveform of P300.
	 */
	private static final String FILE_CONTAINING_P3 = "Functions/P3_cut.txt";
	
	/**
	 * Here will be loaded waveform of P3. This array must be shorter than signal2 so 
	 * if it is needed they are switched for the duration of the main procedure.
	 */
	private double[] signal1;
	
	/**
	 * Here will be loaded signal which will be classified. This array must be longer than 
	 * signal1 so if it is needed they are switched for the duration of the main procedure.
	 */
	private double[] signal2;
	
	
	public CorrelationAlgorithms() {
		
	}
	
	/**
	 * Score of given feature vector.
	 * @param feature	feature vector
	 * @return	score of given feature vector
	 */
	public double getScore(double[] feature) {
		this.signal2 = feature;
		
		boolean switched = false;
		if(signal1.length > signal2.length) {
			switchSignals();
			switched = true;
		}
		
		double goal = countGoalRating(signal1);		
		
		int differenceOfSignalsLength = signal2.length - signal1.length;
		double result = 0;
		for(int padding = 0; padding <= differenceOfSignalsLength; padding++) {
			double partResult = 0;
			for(int i = 0; i < signal1.length; i++) {
				partResult += signal1[i] * signal2[padding+i];
			}
			double div;
			if(Math.abs(goal) < Math.abs(partResult)) {
				div = goal / partResult;
			} else {
				div = partResult / goal;
			}
			
			if(result < div) {
				result = div;
			}
		}
		if(result < 0) {
			result = 0;
		}
		
		if(switched) {
			switchSignals();		
		}
		
		return result;
		
	}
	
	/**
	 * Loading waveform of P3 from the file.
	 */
	public void loadP300() { 
		Scanner sc;
		sc = new Scanner(FILE_CONTAINING_P3);
		
		List<Double> loading = new ArrayList<Double>();
		
        while(sc.hasNextDouble())
        {
            loading.add(sc.nextDouble());
        }
        sc.close();
        
        this.signal1 = new double[loading.size()];
        
        for(int i = 0; i < loading.size(); i++) {
        	this.signal1[i] = loading.get(i);
        }
	}
	
	/**
	 * From variable signal1 to signal2 and from signal2 to signal1,
	 * because shorter vector must be the first one.
	 */
	private void switchSignals() {
		double[] tmp = signal1;
		signal1 = signal2;
		signal2 = tmp;
	}
	
	/**
	 * Count scalar product of given signal
	 * @param signal	signal which scalar product will be count
	 * @return	scalar product of given signal 
	 */
	private double countGoalRating(double[] signal) {
		double goal = 0;
		for(int i = 0; i < signal.length; i++) {
			goal  += signal[i] * signal[i];
		}
		return goal;
	}
	
}
