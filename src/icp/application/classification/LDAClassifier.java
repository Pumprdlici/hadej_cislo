package icp.application.classification;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class LDAClassifier implements IERPClassifier {
	private IFeatureExtraction fe; /* feature extraction used to decompose each epoch */
	private LDA lda;
	
	public LDAClassifier() {
		this.lda = null;
	}

	@Override
	public void setFeatureExtraction(IFeatureExtraction fe) {
		this.setFeatureExtraction(fe);
		
	}

	@Override
	public void train(List<double[][]> epochs, List<Double> targets,
			int numberOfiter, IFeatureExtraction fe) {
		//this.lda = new LDA
		// TODO Auto-generated method stub
		
	}

	@Override
	public ClassificationStatistics test(List<double[][]> epochs,
			List<Double> targets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double classify(double[][] epoch) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void load(InputStream is) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(OutputStream dest) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(String file) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void load(String file) {
		// TODO Auto-generated method stub
		
	}

}
