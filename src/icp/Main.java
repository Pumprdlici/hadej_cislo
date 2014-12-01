package icp;

import icp.application.OnlineDetection;
import icp.application.classification.FilterFeatureExtraction;
import icp.application.classification.IERPClassifier;
import icp.application.classification.IFeatureExtraction;
import icp.application.classification.MLPClassifier;
import icp.online.app.OnLineDataProvider;
import icp.online.gui.MainFrame;

/**
 * Hlavn� spou�t�c� t��da aplikace.
 * 
 */
public class Main {

	public static void main(String[] args) {
		//new SessionManager().startGui();
		IERPClassifier classifier = new MLPClassifier();
		classifier.load("data/classifier.txt");
		IFeatureExtraction fe = new FilterFeatureExtraction();
		classifier.setFeatureExtraction(fe);
		
		MainFrame gui = new MainFrame();
		OnlineDetection detection = new OnlineDetection(classifier, gui);
		OnLineDataProvider odp = new OnLineDataProvider("147.228.64.220", 51244, detection);		
		
		
		
	}
}
