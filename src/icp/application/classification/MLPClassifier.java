package icp.application.classification;

import icp.Const;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.DataSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;

/**
 *
 * Classification based on multi-layer perceptron using Neuroph library
 *
 * @author Lukas Vareka
 *
 */
public class MLPClassifier extends ERPClassifierAdapter {

    private NeuralNetwork neuralNetwork; 		/* neural network implementation */

    private IFeatureExtraction fe; /* feature extraction used to decompose each epoch */

    private int numberOfIters = 0;

    public MLPClassifier() {
        neuralNetwork = new MultiLayerPerceptron(Const.DEFAULT_OUTPUT_NEURONS);
    }

    /**
     *
     * @param params contains number of neurons in the layers
     */
    public MLPClassifier(ArrayList<Integer> params) {
        neuralNetwork = new MultiLayerPerceptron(params);
        neuralNetwork.randomizeWeights();

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
        for (int i = 0; i < epochs.size(); i++) {
            double[][] epoch = epochs.get(i);
            double[] features = this.fe.extractFeatures(epoch);
            double[] target = new double[targetsSize];
            target[0] = targets.get(i);
            dataset.addRow(features, target);
        }
        // shuffle the resulting dataset
        dataset.shuffle();

        // train the NN
        this.train(dataset, numberOfIter, Const.LEARNING_RATE);
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
}
