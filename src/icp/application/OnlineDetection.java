package icp.application;

import icp.application.classification.IERPClassifier;
import icp.application.classification.test.ObserverMessage;
import icp.online.app.EpochMessenger;
import icp.online.app.IDataProvider;
import java.util.Arrays;

import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

public class OnlineDetection extends Observable implements Observer {

    private final IERPClassifier classifier;
    private final double[] classificationResults;
    private final int[] classificationCounters;
    private final double[][] pzSum;
    private final double[][] pzAvg;

    private static final int NUMBERS = 9;
    private Logger log;

    private double[] weightedResults;

    public OnlineDetection(IERPClassifier classifier, Observer observer) {
        super();
        this.addObserver(observer);
        this.classifier = classifier;
        this.classificationCounters = new int[NUMBERS];
        this.classificationResults = new double[NUMBERS];
        this.pzSum = new double[NUMBERS][IDataProvider.POCETHODNOTZAEPOCHOU];
        this.pzAvg = new double[NUMBERS][IDataProvider.POCETHODNOTZAEPOCHOU];

        Arrays.fill(classificationCounters, 0);
        Arrays.fill(classificationResults, 0);
        for (int i = 0; i < pzSum.length; i++) {
            Arrays.fill(pzSum[i], 0);
            Arrays.fill(pzAvg[i], 0);
        }
    }

    @Override
    public void update(Observable o, Object arg) throws IllegalArgumentException {
        if (arg instanceof EpochMessenger) {

            EpochMessenger epochMsg = (EpochMessenger) arg;
            double classificationResult = this.classifier.classify(epochMsg.getEpoch());
            int stimulusID = epochMsg.getStimulusIndex();

            classificationCounters[stimulusID]++;
            classificationResults[stimulusID] += classificationResult;

            for (int i = 0; i < IDataProvider.POCETHODNOTZAEPOCHOU; i++) {
                pzSum[stimulusID][i] += epochMsg.getEpoch()[2][i]; // Pz
                pzAvg[stimulusID][i] = pzSum[stimulusID][i] / classificationCounters[stimulusID];
            }

            this.weightedResults = this.calcClassificationResults();
            setChanged();
            notifyObservers(this);
        } else if (arg instanceof ObserverMessage) {
            //TODO some action when data loading ends
        } else {
            throw new IllegalArgumentException("Unexpected reference received.");
        }
    }

    private double[] calcClassificationResults() {
        double[] wResults = new double[NUMBERS];
        for (int i = 0; i < wResults.length; i++) {
            if (classificationCounters[i] == 0) {
                wResults[i] = 0;
            } else {
                wResults[i] = classificationResults[i] / classificationCounters[i];
            }
        }

        return wResults;
    }

    public double[][] getPzAvg() {
        return this.pzAvg;
    }

    public double[] getWeightedResults() {
        return weightedResults;
    }

}
