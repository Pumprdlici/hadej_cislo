package icp.application.classification.test;

import icp.application.classification.ClassificationStatistics;
import icp.application.classification.FilterFeatureExtraction;
import icp.application.classification.IERPClassifier;
import icp.application.classification.IFeatureExtraction;
import icp.application.classification.MLPClassifier;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

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
		// set data paths
		String basePath = "data/split_set/";
		Map<Integer, String> trainingSourceFiles = new HashMap<Integer, String>();
		trainingSourceFiles.put(1, basePath + "tagged_training_data/raw_epochs_Fz.txt");
		trainingSourceFiles.put(2, basePath + "tagged_training_data/raw_epochs_Cz.txt");
		trainingSourceFiles.put(3, basePath + "tagged_training_data/raw_epochs_Pz.txt");
		
		
		String targetsTrainFileName =  basePath + "tagged_training_data/targets.txt";
		Map<Integer, String> testingSourceFiles = new HashMap<Integer, String>();
		testingSourceFiles.put(1, basePath + "tagged_testing_data/raw_epochs_Fz.txt");
		testingSourceFiles.put(2, basePath + "tagged_testing_data/raw_epochs_Cz.txt");
		testingSourceFiles.put(3, basePath + "tagged_testing_data/raw_epochs_Pz.txt");
		String targetsTestFileName =  basePath + "tagged_testing_data/targets.txt";
		
		InputStream[] isTrainData = new InputStream[testingSourceFiles.keySet().size()];
		InputStream   isTrainTargets = null;
		InputStream[] isTestData = new InputStream[testingSourceFiles.keySet().size()];
		InputStream   isTestTargets = null;
		
		TrainingSetParser trainingParser = new TrainingSetParser();
		TrainingSetParser testingParser = new TrainingSetParser();
		
		System.out.println("Data loaded.");
		try {
			// open source data streams and load data
			int counter = 0;
			System.out.println("Training data.");
			for (Integer channel: trainingSourceFiles.keySet() ) {
				isTrainData[counter] = new BufferedInputStream(new FileInputStream(trainingSourceFiles.get(channel)));
				List<double[]> epochs = trainingParser.readEpochs(isTrainData[counter]);
				trainingParser.join(epochs, channel);
				System.out.println(counter);
				counter++;
			}
			counter = 0;
			for (Integer channel: testingSourceFiles.keySet() ) {
				isTestData[counter] = new BufferedInputStream(new FileInputStream(testingSourceFiles.get(channel)));
				List<double[]> epochs = testingParser.readEpochs(isTestData[counter]);
				testingParser.join(epochs, channel);
				counter++;
			}
			isTrainTargets = new BufferedInputStream(new FileInputStream(targetsTrainFileName));
			List<Double> trainingTargets = trainingParser.readTargets(isTrainTargets);
			List<double[][]> trainingEpochs = trainingParser.getEpochs();
			
			isTestTargets = new BufferedInputStream(new FileInputStream(targetsTestFileName));
			List<Double> testingTargets = testingParser.readTargets(isTestTargets);
			List<double[][]> testingEpochs = testingParser.getEpochs();
			
			// create classifiers
			IFeatureExtraction fe = new FilterFeatureExtraction();
			int numberOfInputNeurons = fe.getFeatureDimension();
			int middleNeurons        = 25;
			int outputNeurons 	     = 1;
			Vector<Integer> nnStructure = new Vector<Integer>();
			nnStructure.add(numberOfInputNeurons); nnStructure.add(middleNeurons); nnStructure.add(outputNeurons);
			IERPClassifier classifier = new MLPClassifier(nnStructure);
			
			
			// training
			System.out.println("Training started.");
			classifier.train(trainingEpochs, trainingTargets, 2000, fe);
			
			//classifier.load("data/classifier.txt");
			classifier.setFeatureExtraction(fe);
			
			// testing
			ClassificationStatistics statistics = classifier.test(testingEpochs, testingTargets);
			
			// print the results of classification
			System.out.println(statistics);
			
			classifier.save("data/classifier.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			// close streams
			try {
				for (int i = 0; i < trainingSourceFiles.keySet().size(); i++) {
					if (isTrainData[i] != null)
						isTrainData[i].close();
				}
				if (isTrainTargets != null) 
					isTrainTargets.close();
				for (int i = 0; i < testingSourceFiles.keySet().size(); i++) {
					if (isTestData[i] != null)
						isTestData[i].close();
				}
				if (isTestTargets != null) 
					isTestTargets.close(); 
			} catch (IOException e) {
				e.printStackTrace();
				
			}
			
			
		}

	}

}
