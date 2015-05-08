package icp.application.classification;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import libsvm.svm;
import net.sf.javaml.core.DenseInstance;

import org.apache.commons.lang.NotImplementedException;

import icp.Const;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.converters.LibSVMSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;
import weka.filters.unsupervised.instance.NonSparseToSparse;

public class SVMClassifier extends ERPClassifierAdapter {

	private Classifier classifier; /* classifier from WEKA */

	private IFeatureExtraction fe; /* feature extraction used to decompose each epoch */

	private Instances instances; /* dataset WEKA */
	
	private final String ARFF_DATASET = "dataset.arff"; /* file name for WEKA dataset */

	/**
	 * Constructor for settings of SVM and disable output to console.
	 * 
	 * 
	 * 
	 * @throws Exception
	 */
	public SVMClassifier() throws Exception {

		this.classifier = new LibSVM();
		/*String[] options = weka.core.Utils
				.splitOptions("-S 0 -K 0 -C 242 -M 40.0 -seed 1 -W 1");//  -S(SVMType) 0(C-SVC) -K(kernel) 2(RBF) -G(gamma) 0.0625 -C(cost) 0.33*/
		double cost = 0.03125;
		 String[] options = new String[10];
		 options[0] = "-S";
		 options[1] = "0";
		 options[2] = "-K";
		 options[3] = "0";
		 options[4] = "-C";
		 options[5] = Double.toString(cost);
		 options[6] = "-G";
		 options[7] = "0.125";
		 options[8] = "-W";
		 options[9] = "1";
		this.classifier.setOptions(options);
		svm.svm_set_print_string_function(new libsvm.svm_print_interface(){
		    @Override public void print(String s) {} // Disables svm output
		});

	}
	
	public SVMClassifier(double cost) throws Exception {

		this.classifier = new LibSVM();
		 String[] options = new String[8];
		 options[0] = "-S";
		 options[1] = "0";
		 options[2] = "-K";
		 options[3] = "0";
		 options[4] = "-C";
		 options[5] = Double.toString(cost);
		 options[6] = "-W";
		 options[7] = "1";
		this.classifier.setOptions(options);
		svm.svm_set_print_string_function(new libsvm.svm_print_interface(){
		    @Override public void print(String s) {} // Disables svm output
		});

	}

	/**
	 * Training and saving of classifier
	 */
	@Override
	public void train(List<double[][]> epochs, List<Double> targets,
			int numberOfiter, IFeatureExtraction fe) {
		createDataset(epochs, targets);//create dataset.arff (WEKA data format)
		
		try {
			classifier.buildClassifier(instances);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		save(Const.TRAINING_FILE_NAME);

	}

	/**
	 * Method for creating arff dataset file used for training LibSVM classifier
	 * 
	 * @param epochs list of epochs
	 * @param targets list of targets
	 */
	private void createDataset(List<double[][]> epochs, List<Double> targets) {
		FastVector attributes;
		Instances helpDataset;
		double[] values;
		attributes = new FastVector();
		double[][] firstEpoch = epochs.get(0);
		double[] firstFeature = fe.extractFeatures(firstEpoch);
		Instance firstInstance = new Instance(targets.get(0), firstFeature);
		int numValues = firstInstance.numValues();
		for (int i = 0; i < firstInstance.numValues(); i++) {
			attributes.addElement(new Attribute("att" + (i + 1)));
		}
		attributes.addElement(new Attribute("target"));

		helpDataset = new Instances("ESDN", attributes, 0);

		for (int j = 0; j < epochs.size(); j++) {
			double[][] epoch = epochs.get(j);
			double[] features = fe.extractFeatures(epoch);
			values = new double[helpDataset.numAttributes()];
			for (int i = 0; i < numValues; i++) {
				values[i] = features[i];
			}
			values[numValues] = (double) targets.get(j);
			helpDataset.add(new Instance(1.0, values));

		}
		instances = helpDataset;
		NumericToNominal convert = new NumericToNominal();
		String[] options = new String[2];
		options[0] = "-R";
		options[1] = "last"; // range of variables to make numeric
		try {
			convert.setOptions(options);
			convert.setInputFormat(instances);
			instances = Filter.useFilter(instances, convert);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		instances.setClassIndex(numValues);

		ArffSaver arffSaverInstance = new ArffSaver();
		arffSaverInstance.setInstances(instances);
		File file = new File(ARFF_DATASET);
		file.delete();
		try {
			arffSaverInstance.setFile(new File(ARFF_DATASET));
			arffSaverInstance.writeBatch();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public double classify(double[][] epoch) {
		double[] feature = fe.extractFeatures(epoch);
		loadDataset();
		load(Const.TRAINING_FILE_NAME);
		
		Instance instance = new Instance(1.0, feature);
		instances.add(instance);
		instances.setClassIndex(instances.numAttributes() - 1);

		Object predictedClassValue = null;
		try {

			predictedClassValue = this.classifier.classifyInstance(instances.lastInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return (Double) predictedClassValue;
	}

	@Override
	public void setFeatureExtraction(IFeatureExtraction fe) {
		this.fe = fe;
	}

	@Override
	public ClassificationStatistics test(List<double[][]> epochs,
			List<Double> targets) {
		ClassificationStatistics resultsStats = new ClassificationStatistics();
		loadDataset();

		for (int i = 0; i < instances.numInstances() - 1; i++) {
			Object predictedClassValue = null;
			try {
				predictedClassValue = classifier.classifyInstance(instances
						.instance(i));
			} catch (Exception e) {
				e.printStackTrace();
			}
			Object realClassValue = instances.instance(i).classValue();
			System.out.println(instances.instance(i).classValue());
			resultsStats.add((Double) realClassValue,
					(Double) predictedClassValue);
		}
		
		return resultsStats;
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

	/**
	 * Method for reading dataset and saving data to instances
	 */
	public void loadDataset() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					ARFF_DATASET));
			ArffReader arff = new ArffReader(reader);
			Instances data = arff.getData();
			data.setClassIndex(data.numAttributes() - 1);
			this.instances = data;

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void load(String file) {
		try {
			InputStream fileF = new FileInputStream(file);
			InputStream buffer = new BufferedInputStream(fileF);
			ObjectInput input = new ObjectInputStream(buffer);

			// deserialize the List
			this.classifier = (Classifier) input.readObject();
			input.close();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
