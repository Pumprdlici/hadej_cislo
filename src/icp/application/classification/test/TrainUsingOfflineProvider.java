package icp.application.classification.test;

import icp.Const;
import icp.online.app.DataObjects.MessageType;
import icp.online.app.DataObjects.ObserverMessage;
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

public class TrainUsingOfflineProvider implements Observer {

    private final List<double[][]> epochs;
    private final List<Double> targets;
    private int numberOfTargets;
    private int numberOfNonTargets;

    public TrainUsingOfflineProvider() {
        
        epochs = new ArrayList<>();
        targets = new ArrayList<>();
        numberOfTargets = 0;
        numberOfNonTargets = 0;

        OffLineDataProvider offLineData = new OffLineDataProvider(new File(Const.TRAINING_RAW_DATA_FILE_NAME), this);
        Thread t = new Thread(offLineData);
        t.start();
    }

    public static void main(String[] args) {
        TrainUsingOfflineProvider train = new TrainUsingOfflineProvider();
    }

    @Override
    public void update(Observable sender, Object message) {
        if (message instanceof ObserverMessage) {
            ObserverMessage msg = (ObserverMessage) message;
            if (msg.getMsgType() == MessageType.END) {
                this.train();
            }
        }
        if (message instanceof EpochMessenger) {
            double[][] epoch = ((EpochMessenger) message).getEpoch();
            int stimulus = ((EpochMessenger) message).getStimulusIndex();

            // 1 = target, 3 = non-target
            if (stimulus == 1 && numberOfTargets <= numberOfNonTargets) {
                epochs.add(epoch);
                targets.add(1.0);
                numberOfTargets++;
            } else if (stimulus == 3 && numberOfTargets >= numberOfNonTargets) {
                epochs.add(epoch);
                targets.add(0.0);
                numberOfNonTargets++;
            }
        }
    }

    private void train() {
        // create classifiers
        IFeatureExtraction fe = new FilterFeatureExtraction();
        int numberOfInputNeurons = fe.getFeatureDimension();
        int middleNeurons = 10;
        int outputNeurons = 1;
        ArrayList<Integer> nnStructure = new ArrayList<>();
        nnStructure.add(numberOfInputNeurons);
        nnStructure.add(middleNeurons);
        nnStructure.add(outputNeurons);
        IERPClassifier classifier = new MLPClassifier(nnStructure);
        classifier.setFeatureExtraction(fe);

        // training
        System.out.println("Training started.");
        classifier.train(this.epochs, this.targets, Const.NUMBER_OF_ITERATIONS, fe);
        classifier.save(Const.TRAINING_FILE_NAME);
        System.out.println("Training finished.");
    }

}
