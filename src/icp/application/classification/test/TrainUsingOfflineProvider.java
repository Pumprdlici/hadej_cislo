package icp.application.classification.test;

import icp.Const;
import icp.application.classification.FilterAndSubsamplingFeatureExtraction;
import icp.application.classification.IERPClassifier;
import icp.application.classification.IFeatureExtraction;
import icp.application.classification.MLPClassifier;
import icp.application.classification.WaveletTransformFeatureExtraction;
import icp.online.app.EpochMessenger;
import icp.online.app.OffLineDataProvider;
import icp.online.app.DataObjects.MessageType;
import icp.online.app.DataObjects.ObserverMessage;
import icp.online.gui.Chart;
import icp.online.gui.EpochCharts;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

public class TrainUsingOfflineProvider implements Observer {

    private final List<double[][]> epochs;
    private final List<Double> targets;
    private int numberOfTargets;
    private int numberOfNonTargets;
    private int iters;
    private int middleNeurons;
    private static IFeatureExtraction fe;
    private static IERPClassifier classifier;
    private static String file;

    public TrainUsingOfflineProvider(IFeatureExtraction fe,
            IERPClassifier classifier, String file) {
        TrainUsingOfflineProvider.fe = fe;
        TrainUsingOfflineProvider.classifier = classifier;
        TrainUsingOfflineProvider.file = file;

        epochs = new ArrayList<double[][]>();
        targets = new ArrayList<Double>();
        numberOfTargets = 0;
        numberOfNonTargets = 0;
        this.iters = 2000;
        this.middleNeurons = 0;

        OffLineDataProvider offLineData;
        offLineData = new OffLineDataProvider(new File(
                Const.TRAINING_RAW_DATA_FILE_NAME), this);
        Thread t = new Thread(offLineData);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public TrainUsingOfflineProvider(int iters, int middleNeurons) {

        epochs = new ArrayList<double[][]>();
        targets = new ArrayList<Double>();
        numberOfTargets = 0;
        numberOfNonTargets = 0;
        this.iters = iters;
        this.middleNeurons = middleNeurons;

        OffLineDataProvider offLineData = new OffLineDataProvider(new File(
                Const.TRAINING_RAW_DATA_FILE_NAME), this);
        Thread t = new Thread(offLineData);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (classifier == null) {
            TrainUsingOfflineProvider train = new TrainUsingOfflineProvider(
                    2000, 8);
        } else {
            TrainUsingOfflineProvider train = new TrainUsingOfflineProvider(fe,
                    classifier, file);
        }
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

        if (classifier == null) {
            setDefaultClassifier();
        }

        double[][] tAvg = new double[epochs.get(0).length][epochs.get(0)[0].length];
        double[][] nAvg = new double[epochs.get(0).length][epochs.get(0)[0].length];
        //Arrays.fill(tAvg, 0);
        //Arrays.fill(nAvg, 0);
        int cnt = 0;
        for (double[][] epoch : epochs) {
            for (int i = 0; i < epoch.length; i++) {
                for (int j = 0; j < epoch[i].length; j++) {
                    if (targets.get(cnt) == 1) {
                        tAvg[i][j] += epoch[i][j];
                    } else {
                        nAvg[i][j] += epoch[i][j];
                    }
                }
            }

            cnt++;
        }

        for (int i = 0; i < tAvg.length; i++) {
            for (int j = 0; j < tAvg[i].length; j++) {
                tAvg[i][j] = tAvg[i][j] / cnt;
                nAvg[i][j] = nAvg[i][j] / cnt;
            }
        }

        Chart chart = new Chart("Target training data average");
        chart.update(tAvg);
        chart.pack();
        chart.setVisible(true);

        Chart chart2 = new Chart("Non-target training data average");
        chart2.update(nAvg);
        chart2.pack();
        chart2.setVisible(true);

        // training
        System.out.println("Training started.");
        classifier.train(this.epochs, this.targets, this.iters, fe);
        if (file == null || file.equals("")) {
            classifier.save(Const.TRAINING_FILE_NAME);
        } else {
            classifier.save(file);
        }
        System.out.println("Training finished.");
    }

    /**
     *
     * If no classifier is set, create a default classifier with empirically set
     * parameters
     *
     */
    private void setDefaultClassifier() {
        Random r = new Random(System.nanoTime());
        fe = new WaveletTransformFeatureExtraction(14, 512, 20, 8);
        int numberOfInputNeurons = fe.getFeatureDimension();
        int middleNeurons = this.middleNeurons;
        int outputNeurons = 1;
        ArrayList<Integer> nnStructure = new ArrayList<Integer>();
        nnStructure.add(numberOfInputNeurons);
        nnStructure.add(middleNeurons);
        nnStructure.add(outputNeurons);
        classifier = new MLPClassifier(nnStructure);
        classifier.setFeatureExtraction(fe);
    }

    public IERPClassifier getClassifier() {
        return this.classifier;
    }

}
