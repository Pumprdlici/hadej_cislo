package icp.application;

import icp.application.classification.IERPClassifier;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * General offline classification of
 * P300 calculator data using a custom classifier
 * 
 * @author Lukas Vareka
 *
 */
public class ERPDetectionAlgorithm extends Thread implements ProgressInterface {
	private SessionManager sm;
	private List<Element> elements;
	private IERPClassifier classifier;
	private boolean stop;
	private double unit;
	
	
	
	
	public ERPDetectionAlgorithm(SessionManager sm, IERPClassifier classifier, List<Element> elements) {
		this.sm = sm;
		this.classifier = classifier;
		this.elements = elements;
	}


	
	/**
	 * Determines appropriate rows and columns associated with
	 * each element
	 * 
	 */
	@Override
	public void run() {
		stop = false;
		unit = 100D / (8D * (double) elements.size()); // ?
		int numberOfRowsOrCols = 4; // 4 x 4 matrix
		
		for (Element e: elements) // for each character to be detected
		{
			
			double[] rowResults = new double[numberOfRowsOrCols]; // sums up rows classification results - the bigger the value
																  // the more likely the correct detection	
			double[] rowCounters = new double[numberOfRowsOrCols]; // counts the number of epochs
			double[] colsResults = new double[numberOfRowsOrCols]; // -||- for columns
			double[] colsCounters = new double[numberOfRowsOrCols]; // -||- for columns
			for (int i = 0; i < numberOfRowsOrCols; i++) {
				rowResults[i] = 0;
				rowCounters[i] = 0;
				colsResults[i] = 0;
				colsCounters[i] = 0;
			}
				
			
			List<double[][]>[] epochs = e.getRowsAndColumnsRawData();
			
			// find the correct row
			for (int i = 0; i < numberOfRowsOrCols; i++) //Magické èíslo 4 = pocet radek?
			{
				
				if (stop)
					return;
				sm.sendProgressUnits(unit);
				for (double[][] epoch: epochs[i]) {
					double result = classifier.classify(epoch);
					rowResults[i] += result;
					rowCounters[i]++;
				}
			}
			
			
			// find the correct column
			for (int i = numberOfRowsOrCols; i < numberOfRowsOrCols * 2; i++) //Magické èíslo 4 = pocet sloupcu?
			{
				
				if (stop)
					return;
				sm.sendProgressUnits(unit);
				for (double[][] epoch: epochs[i]) {
					double result = classifier.classify(epoch);
					colsResults[i - numberOfRowsOrCols] += result;
					colsCounters[i - numberOfRowsOrCols]++;
				}
			}
			
			// evaluate the results
			int winningRow = -1 ;
			double maxValue = Double.MIN_VALUE;
			for (int i = 0; i < numberOfRowsOrCols; i++) {
				if (rowResults[i] / rowCounters[i] > maxValue) {
					maxValue = rowResults[i] / rowCounters[i];
					winningRow = i;
				}
					
			}
			e.setDetectedRow(winningRow);
			System.out.println(Arrays.toString(rowResults));
			
			
			int winningCol = -1 ;
			maxValue = -1;
			for (int i = numberOfRowsOrCols; i < numberOfRowsOrCols * 2; i++) {
				if (colsResults[i - numberOfRowsOrCols] / colsCounters[i - numberOfRowsOrCols] > maxValue) {
					maxValue = colsResults[i - numberOfRowsOrCols] / colsCounters[i - numberOfRowsOrCols];
					winningCol = i;
				}
					
			}
			System.out.println(Arrays.toString(rowResults));
			e.setDetectedColumn(winningCol - numberOfRowsOrCols);
			
				
		}
		sm.sendDetectionMessage();

	}
	
	public void stopDetection() {
		this.stop = true;
	}



	@Override
	public void sendProgressUnits() {
		// TODO Auto-generated method stub
		
	}
}