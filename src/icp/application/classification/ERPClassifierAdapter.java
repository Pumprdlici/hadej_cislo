package icp.application.classification;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
/**
 * Adapter of IERPClassifier interface. 
 * All methods throw NotImplementedException when called.
 * 
 * @author Tomas Rondik
 */
public class ERPClassifierAdapter implements IERPClassifier {

	@Override
	public void setFeatureExtraction(IFeatureExtraction fe) {
		throw new NotImplementedException();
	}

	@Override
	public void train(List<double[][]> epochs, List<Double> targets,
			int numberOfiter, IFeatureExtraction fe) {
		throw new NotImplementedException();
	}

	@Override
	public ClassificationStatistics test(List<double[][]> epochs,
			List<Double> targets) {
		throw new NotImplementedException();
	}

	@Override
	public double classify(double[][] epoch) {
		throw new NotImplementedException();
	}

	@Override
	public void load(InputStream is) {
		throw new NotImplementedException();
	}

	@Override
	public void save(OutputStream dest) {
		throw new NotImplementedException();
	}

}
