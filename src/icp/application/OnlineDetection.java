package icp.application;

import icp.application.classification.IERPClassifier;
import icp.online.app.EpochMessenger;

import java.util.Observable;
import java.util.Observer;

public class OnlineDetection implements Observer  {
	private IERPClassifier classifier;
	private double[] classificationResults;
	private int[]   classificationCounters;
	private static final int NUMBERS = 10;
	

	public OnlineDetection(IERPClassifier classifier) {
		this.classifier = classifier;
		this.classificationCounters = new int[NUMBERS];
		this.classificationResults  = new double[NUMBERS];
		
		for (int i = 0; i < NUMBERS; i++) {
			classificationResults[i] = 0;
			classificationCounters[i] = 0;
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		EpochMessenger epochCrane = (EpochMessenger) arg;
		double classificationResult = this.classifier.classify(epochCrane.getEpoch());
		int stimulusID = epochCrane.getStimulusIndex();
		
		classificationCounters[stimulusID]++;
		classificationResults[stimulusID] += classificationResult;
	}
	
	
	public synchronized double[] calcClassificationResults() {
		double[] weightedResults = new double[NUMBERS];
		for (int i = 0; i < weightedResults.length; i++) {
			if (classificationCounters[i] == 0)
				weightedResults[i] = 0;
			else
				weightedResults[i] = classificationResults[i] / classificationCounters[i];
		}
		return weightedResults;
		
	}

}
