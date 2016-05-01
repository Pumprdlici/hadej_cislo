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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;

import libsvm.LibSVM;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.classification.KNearestNeighbors;
import net.sf.javaml.classification.MeanFeatureVotingClassifier;
import net.sf.javaml.classification.SOM;
import net.sf.javaml.clustering.SOM.GridType;
import net.sf.javaml.clustering.SOM.LearningType;
import net.sf.javaml.clustering.SOM.NeighbourhoodFunction;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.DistanceMeasure;

public class JavaMLClassifier implements IERPClassifier {
	private Classifier classifier;
	private IFeatureExtraction fe;
	
	public JavaMLClassifier() {
		//this.classifier = new  SOM(10, 10, GridType.HEXAGONAL, 1000, 0.1, 3, LearningType.EXPONENTIAL, NeighbourhoodFunction.GAUSSIAN);
		//this.classifier = new  KNearestNeighbors(50);
		this.classifier = new  LibSVM();
		
	}

	@Override
	public void setFeatureExtraction(IFeatureExtraction fe) {
		this.fe = fe;
		
	}

	@Override
	public void train(List<double[][]> epochs, List<Double> targets,
			int numberOfiter, IFeatureExtraction fe) {
		Dataset dataset = createDataset(epochs, targets);
		classifier.buildClassifier(dataset);
	}

	@Override
	public ClassificationStatistics test(List<double[][]> epochs,
			List<Double> targets) {
        ClassificationStatistics resultsStats = new ClassificationStatistics();
		Dataset dataset = createDataset(epochs, targets);
		for (Instance inst : dataset) {
			Object predictedClassValue = classifier.classify(inst);
			Object realClassValue = inst.classValue();
			resultsStats.add((Double)realClassValue, (Double)predictedClassValue);
		}
		return resultsStats;
	}

	@Override
	public double classify(double[][] epoch) {
		double[] feature = fe.extractFeatures(epoch);
		Instance instance = new DenseInstance(feature);
		Object predictedClassValue = classifier.classify(instance);
		
		// TODO Auto-generated method stub
		return (Double)predictedClassValue;
	}

	@Override
	public void load(InputStream is) {
		throw new NotImplementedException();
		
	}

	@Override
	public void save(OutputStream dest) {
		throw new NotImplementedException();
		
	}

	@Override
	public void save(String file)  {
		
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

	@Override
	public void load(String file) {
		  try   {
			 InputStream fileF = new FileInputStream(file);
			 InputStream buffer = new BufferedInputStream(fileF);
			 ObjectInput input = new ObjectInputStream (buffer);
				 
			 // deserialize the List
			 this.classifier = (Classifier)input.readObject();
			 input.close();
		} catch(ClassNotFoundException ex){
			   ex.printStackTrace();
		} catch(IOException ex){
			  ex.printStackTrace();
		}
		
		
	}
	
	private Dataset createDataset(List<double[][]> epochs, List<Double> targets) {
		Dataset dataset= new DefaultDataset();;
		for (int i = 0; i < epochs.size(); i++ ) {
			double[][] epoch = epochs.get(i);
			double[] features = fe.extractFeatures(epoch);
			Instance instance = new DenseInstance(features, targets.get(i));
			dataset.add(instance);
			
		}
		return dataset;
		
	}

	@Override
	public IFeatureExtraction getFeatureExtraction() {
		return this.fe;
	}
}
