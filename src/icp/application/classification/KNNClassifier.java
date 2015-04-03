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

public class KNNClassifier extends ERPClassifierAdapter {
	
	/**
	 * Attribute for the instance of selected FeatureExtraction class.
	 */
	private IFeatureExtraction fe;
	
	/**
	 * Attribute for the instance of KNearestNeighborsLocal classifier.
	 */
	private KNearestNeighborsLocal classifier;
	
	/**
	 * Number of nearest neighbors that will be used for classification.
	 */
	private int k_cnt;
	
	/**
	 * Default number of nearest neighbors.	
	 */
	private static final int K_CNT_DEFAULT = 5;
	
	/**
	 * Constructor for this classifier that uses default number of nearest neighbors (5)
	 * and doesn't use weighted distances.
	 */
	public KNNClassifier() {
		this(K_CNT_DEFAULT, false);
	}
	
	/**
	 * Constructor for this classifier with variable number of nearest neighbors.
	 * @param k number of nearest neighbors
	 */
	public KNNClassifier(int k, boolean useWeightedDistances) {
		this.k_cnt = k;
		this.classifier = new KNearestNeighborsLocal(k_cnt, useWeightedDistances);
	}

	@Override
	public void setFeatureExtraction(IFeatureExtraction fe) {
		this.fe = fe;
	}

	@Override
	public void train(List<double[][]> epochs, List<Double> targets,
			int numberOfiter, IFeatureExtraction fe) {
		for(int i = 0; i < epochs.size(); i++) {
			double[] vector = fe.extractFeatures(epochs.get(i));
			classifier.addNeighbor(vector, targets.get(i));
		}
	}

	@Override
	public ClassificationStatistics test(List<double[][]> epochs,
			List<Double> targets) {
        ClassificationStatistics resultsStats = new ClassificationStatistics();
        
        for(int i = 0; i < epochs.size(); i++) {
        	double[][] epoch = epochs.get(i);
        	double result = this.classify(epoch);
        	resultsStats.add(result, targets.get(i));
        }
		
		return resultsStats;
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
				 
			 // deserialize the List
			 this.classifier = (KNearestNeighborsLocal)input.readObject();
			 input.close();
		} catch(ClassNotFoundException ex){
			   ex.printStackTrace();
		} catch(IOException ex){
			  ex.printStackTrace();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
