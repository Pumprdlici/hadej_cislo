package icp.application.classification.test;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OptimizeMLP {
	
	
	public static void main(String[] args) throws InterruptedException {
		double accuracy = 0;
		double maxAccuracy = 0;
		Random random = new Random();
		while (accuracy < 0.7) { 
			int numberOfIters = 200 + random.nextInt(5000);
			int middleNeurons =  1 + random.nextInt(3);
			TrainUsingOfflineProvider trainOfflineProvider = new TrainUsingOfflineProvider(numberOfIters, middleNeurons);
			System.out.println("New classifier: " + trainOfflineProvider.getClassifier());
			System.out.println("New feature extraction: " + trainOfflineProvider.getClassifier().getFeatureExtraction());
			TestClassificationAccuracy testAccuracy = null;
			try {
				testAccuracy = new TestClassificationAccuracy( trainOfflineProvider.getClassifier());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			} catch (ExecutionException ex) {
                        Logger.getLogger(OptimizeMLP.class.getName()).log(Level.SEVERE, null, ex);
                    }
			Map<String, Statistics> stats = testAccuracy.getStats();
			int okNumber = 0;
			for (Map.Entry<String, Statistics> entry : stats.entrySet()) {
				if (entry.getValue().getRank() == 1) {
					okNumber++;
			}
			
			}
			accuracy = (double) okNumber / stats.size();
			if (accuracy > maxAccuracy) {
				maxAccuracy = accuracy;
				System.out.println("New accuracy record: " + accuracy);
				trainOfflineProvider.getClassifier().save("data/bestOfAllTimes.txt");
				
			}
			else {
				System.out.println("No record: current accuracy = " + accuracy * 100 + ", max_accuracy = " + maxAccuracy * 100);
			}
		}
		
	}

}
