package icp.application.classification.test;

import icp.application.classification.FilterFeatureExtraction;
import icp.application.classification.IFeatureExtraction;
import icp.application.classification.IP300Classifier;
import icp.application.classification.MLPClassifier;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 
 * Testing class to evaluate if the selected classifier
 * works correctly
 * 
 * @author Lukas Vareka
 *
 */
public class TestClassificationMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String dataFileName = "data/tagged_training_data/raw_epochs_Cz.txt";
		String targetsFileName = "data/tagged_training_data/targets.txt";
		
		InputStream isData = null;
		InputStream isTargets = null;
		
		try {
			// open source data streams
			isData = new BufferedInputStream(new FileInputStream(dataFileName));
			isTargets = new BufferedInputStream(new FileInputStream(targetsFileName));
			
			
			// load training data
			TrainingSetParser parser = new TrainingSetParser();
			List<double[]> epochsCz = parser.readEpochs(isData);
			List<Double> targets = parser.readTargets(isTargets);
			parser.join(epochsCz, 18);
			List<double[][]> epochs = parser.getEpochs();
			
			
			// create classifiers
			IP300Classifier classifier = new MLPClassifier();
			IFeatureExtraction fe = new FilterFeatureExtraction();
			
			// training
			classifier.train(epochs, targets, fe);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			// close streams
			try {
				if (isData != null) 
					isData.close();
				if (isTargets != null) 
					isTargets.close(); 
			} catch (IOException e) {
				e.printStackTrace();
				
			}
			
			
		}

	}

}
