package icp.application.classification.test;

import icp.application.classification.FilterFeatureExtraction;
import icp.application.classification.IERPClassifier;
import icp.application.classification.IFeatureExtraction;
import icp.application.classification.MLPClassifier;
import icp.online.app.EpochMessenger;
import icp.online.app.OffLineDataProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

public class TrainUsingOfflineProvider implements Observer {
	private List<double[][]> epochs;
	private List<Double> targets;
	
	
	
	public TrainUsingOfflineProvider() {
		String trainingFileName = "data/train/set2.eeg";
		epochs = new ArrayList<double[][]>();
        targets = new ArrayList<Double>();
		OffLineDataProvider offLineData = new OffLineDataProvider(new File(trainingFileName), this);
		Thread t = new Thread(offLineData);
		t.start();
	}

	public static void main(String[] args) {
		TrainUsingOfflineProvider train = new TrainUsingOfflineProvider();
	}

	@Override
	public void update(Observable sender, Object message) {
		if (message == null) {
			this.train();
		}
		if (message instanceof EpochMessenger) {
			double[][] epoch = ((EpochMessenger) message).getEpoch();
			int stimulus     = ((EpochMessenger) message).getStimulusIndex();
			
			// 1 = target, 3 = non-target
			epochs.add(epoch);
			if (stimulus == 1)
				targets.add(1.0);
			else
				targets.add(0.0);
			
		}
		
	}

	private void train() {
		// create classifiers
		IFeatureExtraction fe = new FilterFeatureExtraction();
		int numberOfInputNeurons = fe.getFeatureDimension();
		int middleNeurons        = 25;
		int outputNeurons 	     = 1;
		Vector<Integer> nnStructure = new Vector<Integer>();
		nnStructure.add(numberOfInputNeurons); nnStructure.add(middleNeurons); nnStructure.add(outputNeurons);
		IERPClassifier classifier = new MLPClassifier(nnStructure);
		classifier.setFeatureExtraction(fe);
		
		// training
		System.out.println("Training started.");
		classifier.train(this.epochs, this.targets, 2000, fe);
		classifier.save("data/classifier.txt");
		
		
		
	}

}
