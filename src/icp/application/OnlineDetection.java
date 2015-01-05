package icp.application;

import icp.application.classification.IERPClassifier;
import icp.online.app.EpochMessenger;

import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

public class OnlineDetection extends Observable implements Observer   {
	private IERPClassifier classifier;
	private double[] classificationResults;
	private int[]   classificationCounters;
	private static final int NUMBERS = 9;
	private Logger log;
	

	public OnlineDetection(IERPClassifier classifier, Observer observer) {
		super();
		this.addObserver(observer);
		this.classifier = classifier;
		this.classificationCounters = new int[NUMBERS];
		this.classificationResults  = new double[NUMBERS];
	//	this.log = Logger.getLogger(this.getClass());
		
		
		for (int i = 0; i < NUMBERS; i++) {
			classificationResults[i] = 0;
			classificationCounters[i] = 0;
		}
	}

	@Override
	public void update(Observable o, Object arg) throws IllegalArgumentException {
		if (!(arg instanceof EpochMessenger))
			throw new IllegalArgumentException("Unexpected reference received.");
		
		EpochMessenger epochMsg = (EpochMessenger) arg;
		double classificationResult = this.classifier.classify(epochMsg.getEpoch());
		int stimulusID = epochMsg.getStimulusIndex();
		
		classificationCounters[stimulusID]++;
		classificationResults[stimulusID] += classificationResult;
		double[] weightedResults = this.calcClassificationResults();
		//System.out.println(Arrays.toString(classificationCounters));
		setChanged();
		notifyObservers(weightedResults);
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
