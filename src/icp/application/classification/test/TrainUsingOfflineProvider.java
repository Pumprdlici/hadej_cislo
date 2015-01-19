package icp.application.classification.test;

import icp.online.app.EpochMessenger;
import icp.online.app.OffLineDataProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class TrainUsingOfflineProvider implements Observer {
	private static List<double[][]> epochs;
	private static List<Double> targets;
	
	
	
	public TrainUsingOfflineProvider() {
		String trainingFileName = "data/train/set2.eeg";
		epochs = new ArrayList<double[][]>();
		OffLineDataProvider offLineData = new OffLineDataProvider(new File(trainingFileName), this);
		Thread t = new Thread(offLineData);
		t.start();
	}

	public static void main(String[] args) {
		TrainUsingOfflineProvider train = new TrainUsingOfflineProvider();
	}

	@Override
	public void update(Observable sender, Object message) {
		if (message instanceof EpochMessenger) {
			double[][] epoch = ((EpochMessenger) message).getEpoch();
			int stimulus     = ((EpochMessenger) message).getStimulusIndex();
			
			// 1 = target, 3 = non-target
			epochs.add(epoch);
			if (stimulus == 1)
				targets.add(1.0);
			else if (stimulus == 3)
				targets.add(0.0);
			
		}
		
	}

}
