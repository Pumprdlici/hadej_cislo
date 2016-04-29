package icp.application.classification;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
/**
 * Adapter of IERPClassifier interface. 
 * All methods throw NotImplementedException when called.
 * 
 * @author Tomas Rondik
 */
public class ERPClassifierAdapter implements IERPClassifier {

	@Override
	public void setFeatureExtraction(IFeatureExtraction fe) {
		throw new NullPointerException();
	}

	@Override
	public void train(List<double[][]> epochs, List<Double> targets,
			int numberOfiter, IFeatureExtraction fe) {
		throw new NullPointerException();
	}

	@Override
	public ClassificationStatistics test(List<double[][]> epochs,
			List<Double> targets) {
		throw new NullPointerException();
	}

	@Override
	public double classify(double[][] epoch) {
		throw new NullPointerException();
	}

	@Override
	public void load(InputStream is) {
		throw new NullPointerException();
	}

	@Override
	public void save(OutputStream dest) {
		throw new NullPointerException();
	}
	
	@Override
	public void load(String file) {
		throw new NullPointerException();
	}

	@Override
	public void save(String file) {
		throw new NullPointerException();
	}

	@Override
	public IFeatureExtraction getFeatureExtraction() {
		throw new NullPointerException();
	}
	
	@Override
	public void loadConf() {
		//throw new NullPointerException();
	}
}
