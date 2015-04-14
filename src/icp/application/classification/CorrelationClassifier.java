package icp.application.classification;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Classifier using correlation.
 * @author Karel Silhavy
 *
 */
public class CorrelationClassifier extends ERPClassifierAdapter {
	
	/**
	 * Feature extractor
	 */
	private IFeatureExtraction fe;
	
	/**
	 * Attribute for the instance of Correlation classifier.
	 */
	private CorrelationAlgorithms classifier;
	
	
	public CorrelationClassifier() {
		this.classifier = new CorrelationAlgorithms();
	}
	
	
	/** 
	 * Sets feature extraction method
	 * @param fe	feature extraction method
	 */
	@Override
	public void setFeatureExtraction(IFeatureExtraction fe) {
		this.fe = fe;
	}

	/**
	 * Training this classifier is just loading waveform of P3 from the file.
	 */
	public void train() {
		this.classifier.loadP300();
	}
	
	/**
	 * Training this classifier is just loading waveform of P3 from the file.
	 * There are no needed parameters to train this classifier, so if you call
	 * this method is used {@link #train()}.
	 */
	@Override
	public void train(List<double[][]> epochs, List<Double> targets,
			int numberOfiter, IFeatureExtraction fe) {
		train();
	}


	@Override
	public double classify(double[][] epoch) {
		
		double[] feature = fe.extractFeatures(epoch);
		double score = classifier.getScore(feature);
		
		return score;
	}
	

	@Override
	public void load(String file) {
		try   {
			 InputStream fileF = new FileInputStream(file);
			 InputStream buffer = new BufferedInputStream(fileF);
			 ObjectInput input = new ObjectInputStream (buffer);
			 
			 this.classifier = (CorrelationAlgorithms)input.readObject();
			 input.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e){
			  e.printStackTrace();
		}
	}

	@Override
	public void save(String file) {
		OutputStream fileF;
		try {
			fileF = new FileOutputStream(file);
			OutputStream buffer = new BufferedOutputStream(fileF);
		    ObjectOutput output = new ObjectOutputStream(buffer);
		    
		    output.writeObject(classifier);
		    output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
