package icp.application.classification;

import icp.Const;
import icp.online.gui.Chart;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.util.random.NguyenWidrowRandomizer;

/**
 *
 * Classification based on multi-layer perceptron using Neuroph library
 *
 * @author Lukas Vareka
 *
 */
public class MLPClassifier extends ERPClassifierAdapter implements LearningEventListener {

    private NeuralNetwork<BackPropagation> neuralNetwork; 		/* neural network implementation */

    private IFeatureExtraction fe; /* feature extraction used to decompose each epoch */

    private int numberOfIters = 0;
    
    private double[] featureAverage;
    
    private boolean log = true;
    
    private double lastIteration = 0;
    private double maxValidationAccuracy = 0;
    private DataSet[] trainingTesting;

    public MLPClassifier() {
        neuralNetwork = new MultiLayerPerceptron(Const.DEFAULT_OUTPUT_NEURONS);
    }

    /**
     *
     * @param params contains number of neurons in the layers
     */
    public MLPClassifier(ArrayList<Integer> params) {
        neuralNetwork = new MultiLayerPerceptron(params);
        neuralNetwork.randomizeWeights(new NguyenWidrowRandomizer(0.3, 0.7));

    }

    /**
     * Train with the original dataset
     *
     * @param dataset dataset containing training data (feature vectors and
     * expected classes)
     * @param maxIters maximum allowed number of iterations
     * @param learningRate learning rate
     */
    private void train(DataSet dataset, int maxIters, double learningRate) {
        BackPropagation backP = new BackPropagation();
        backP.setMaxIterations(maxIters);
        backP.setLearningRate(learningRate);
        backP.addListener(this);
        neuralNetwork.learn(dataset, backP);
    }

    @Override
    public double classify(double[][] epoch) {
    	double[] featureVector = this.fe.extractFeatures(epoch);

        // feature vector dimension must correspond to the number of input neurons
        if (featureVector.length != neuralNetwork.getInputsCount()) {
            throw new ArrayIndexOutOfBoundsException("Feature vector dimension "
                    + featureVector.length + " must be the same as the number of input neurons: "
                    + neuralNetwork.getInputsCount() + ".");
        }
        
        neuralNetwork.setInput(featureVector);
        neuralNetwork.calculate();
        double[] output = neuralNetwork.getOutput();
        return output[0];
    }

    @Override
    public void setFeatureExtraction(IFeatureExtraction fe) {
        this.fe = fe;
    }

    @Override
    public ClassificationStatistics test(List<double[][]> epochs, List<Double> targets) {
        ClassificationStatistics resultsStats = new ClassificationStatistics();
        for (int i = 0; i < epochs.size(); i++) {
            double output = this.classify(epochs.get(i));
            resultsStats.add(output, targets.get(i));
        }
        return resultsStats;
    }

    @Override
    public void train(List<double[][]> epochs, List<Double> targets, int numberOfIter, IFeatureExtraction fe) {
        this.fe = fe;
        int targetsSize = neuralNetwork.getOutputsCount();
        this.numberOfIters = numberOfIter;

        // fill in the neuroph data structure for holding the training set
        DataSet dataset = new DataSet(fe.getFeatureDimension(), targetsSize);
        double[] sumTarget = new double[fe.getFeatureDimension()];
        double[] sumNonTarget = new double[fe.getFeatureDimension()];
        int countTarget = 0;
        int countNonTarget = 0;
        Arrays.fill(sumTarget, 0);
        Arrays.fill(sumNonTarget, 0);
        for (int i = 0; i < epochs.size(); i++) {
            double[][] epoch = epochs.get(i);
            double[] features = this.fe.extractFeatures(epoch);
            double[] target = new double[targetsSize];
            target[0] = targets.get(i);
            if (target[0] == 0) {
            	
            	/*if (i < 10) {
            		Chart chartNonTarget = new Chart("Non-Target feature training data trial " + i);
            		chartNonTarget.update(features);
            		chartNonTarget.pack();
            		chartNonTarget.setVisible(true);
            	}*/
                 
            	countNonTarget++;
            	for (int j = 0; j < sumNonTarget.length; j++) {
            		sumNonTarget[j] += features[j];
            	}
            	/*if (countNonTarget == 10) {
            		
            		for (int j = 0; j < sumNonTarget.length; j++) {
            	    	sumNonTarget[j] /= countNonTarget;
            		}
            		countNonTarget = 0;
            		dataset.addRow(sumNonTarget, target);
            		Arrays.fill(sumNonTarget, 0);
            	}*/
            	
            } else {
            	/*if (i < 10) {
            		Chart chartTarget = new Chart("Target feature training data trial " + i);
            		chartTarget.update(features);
            		chartTarget.pack();
            		chartTarget.setVisible(true);
            	}*/
            	countTarget++;
            	for (int j = 0; j < sumTarget.length; j++) {
            		sumTarget[j] += features[j];
            	}
            	/*if (countTarget == 10) {
            		
            		for (int j = 0; j < sumTarget.length; j++) {
            	    	sumTarget[j] /= countTarget;
            		}
            		countTarget = 0;
            		dataset.addRow(sumTarget, target);
            		Arrays.fill(sumTarget, 0);
            	}*/
            }
            
           
            
            
            
            dataset.addRow(features, target);
        }
        
        for (int j = 0; j < sumNonTarget.length; j++) {
    		sumNonTarget[j] /= countNonTarget;
    	}
        
        for (int j = 0; j < sumTarget.length; j++) {
    		sumTarget[j] /= countTarget;
    	}
        
        
        Chart chartNonTarget = new Chart("Non-Target feature training data average");
        chartNonTarget.update(sumNonTarget);
        chartNonTarget.pack();
        chartNonTarget.setVisible(true);
        
        Chart chartTarget = new Chart("Target feature training data average");
        chartTarget.update(sumTarget);
        chartTarget.pack();
        chartTarget.setVisible(true);
        
        // shuffle the resulting dataset
        
        //dataset.save("default_training_dataset");
        dataset.shuffle();
        dataset.save("default_training_dataset");
        trainingTesting = dataset.createTrainingAndTestSubsets(80, 20);
        

        // train the NN
        this.maxValidationAccuracy = 0;
        this.train(trainingTesting[0], numberOfIter, Const.LEARNING_RATE);
        this.load("best.txt");
        
        System.out.println("-----------------------------\nEnd of training: training data accuracy: " + this.testNeuralNetwork(trainingTesting[0]) +  ", testing data accuracy: " + this.testNeuralNetwork(trainingTesting[1]));
       
    }

    @Override
    public void load(InputStream is) {
        this.neuralNetwork = NeuralNetwork.load(is);
    }

    @Override
    public void save(String file) {
        this.neuralNetwork.save(file);
    }

    @Override
    public void load(String file) {
        this.neuralNetwork = NeuralNetwork.load(file);
    }
    
    @Override
    public String toString() {
    	String returnString =  "MLP: ( ";
    	for (Layer layer: this.neuralNetwork.getLayers()) {
    		returnString += layer.getNeuronsCount() + " ";
    	}
    	returnString  += ")";
    	returnString += ": iters: " + this.numberOfIters;
    	return returnString;
    }
    
    @Override
    public IFeatureExtraction getFeatureExtraction() {
    	return fe;
    }

	@Override
	public void handleLearningEvent(LearningEvent learningEvent) {
	    BackPropagation bp = (BackPropagation) learningEvent.getSource();
	    lastIteration = bp.getTotalNetworkError();
	    if (log && bp.getCurrentIteration() % 50 == 0) {
	    	System.out.println("Current iteration: " + bp.getCurrentIteration());
	    	System.out.println("Error: " + bp.getTotalNetworkError());
	    	double validationAccuracy = testNeuralNetwork(trainingTesting[1]);
	    	//System.out.println("Validation accuracy: " + validationAccuracy);
	    	if (this.maxValidationAccuracy < validationAccuracy) {
	    		this.maxValidationAccuracy = validationAccuracy;
	    		System.out.println("-----------------------------\nBest validation accuracy: " + this.maxValidationAccuracy);
	    		this.save("best.txt");
	    	}
	    }
       
        lastIteration = bp.getTotalNetworkError();
		
	}
	
	 public double testNeuralNetwork(DataSet testSet) {
		 	int correct = 0, incorrect = 0;
		    for(DataSetRow dataRow : testSet.getRows()) {
		        this.neuralNetwork.setInput(dataRow.getInput());
		        this.neuralNetwork.calculate();
		        double[] networkOutput = this.neuralNetwork.getOutput();
		        if (Math.round(dataRow.getDesiredOutput()[0]) == Math.round(networkOutput[0])) {
		        	correct++;
		        } else {
		        	incorrect++;
		        }
		        
		       // System.out.print("Input: " + Arrays.toString(dataRow.getInput()) );
		       // System.out.println(" Output: " + Arrays.toString(networkOutput) );
		        

		    }
		    return ((double)correct) / (correct + incorrect);

		}
}
