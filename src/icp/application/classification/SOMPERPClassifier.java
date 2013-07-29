package icp.application.classification;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;


/**
 * Self-organizing map based classifier
 * 
 * @author Lukas Vareka
 *
 */
public class SOMPERPClassifier extends ERPClassifierAdapter {

	@Override
	public double classify(double[][] epoch) {
		// TODO Auto-generated method stub 
		return 0;
	}

	@Override
	public void setFeatureExtraction(IFeatureExtraction fe) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ClassificationStatistics test(List<double[][]> epochs, List<Double> targets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void train(List<double[][]> epochs, List<Double> targets,  int numberOfiter, IFeatureExtraction fe) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void load(InputStream source) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(OutputStream dest) {
		// TODO Auto-generated method stub
		
	}

}
