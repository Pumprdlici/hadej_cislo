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

import java.io.File;
import java.util.ArrayList;
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
		this.fe = fe;
		this.classifier = classifier;
		this.file = file;

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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
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
		
			Random r = new Random(System.nanoTime());
			//fe = new WaveletTransformFeatureExtraction(r.nextInt(16) + 1, 512, 10 * r.nextInt(18), (int) Math.round(Math.pow(2, 3 + r.nextInt(4))) );
			fe = new WaveletTransformFeatureExtraction(14, 512, 20, 8 );
			int numberOfInputNeurons = fe.getFeatureDimension();
			int middleNeurons = this.middleNeurons;
			int outputNeurons = 1;
			ArrayList<Integer> nnStructure = new ArrayList<Integer>();
			nnStructure.add(numberOfInputNeurons);
			nnStructure.add(middleNeurons);
			nnStructure.add(outputNeurons);
			// classifier = new KNNClassifier();
			classifier = new MLPClassifier(nnStructure);
			// classifier = new JavaMLClassifier();
			classifier.setFeatureExtraction(fe);
		

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

	public IERPClassifier getClassifier() {
		return this.classifier;
	}

}
