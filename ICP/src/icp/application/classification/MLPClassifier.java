package icp.application.classification;

import java.util.List;
import java.util.Vector;

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
public class MLPClassifier implements IP300Classifier {
	private NeuralNetwork mlp;
	private IFeatureExtraction fe;
	private final int DEFAULT_OUTPUT_NEURONS = 1;
	
	public MLPClassifier() {
		mlp = new MultiLayerPerceptron(DEFAULT_OUTPUT_NEURONS);
	}


	public MLPClassifier(Vector<Integer> params) {
       mlp = new MultiLayerPerceptron(params);
	   mlp.randomizeWeights();
	        
	}

	/**
	 * Train with the original dataset 
	 * 
	 * @param t
	 * @param max_iteration
	 * @param learning_rate
	 */
    public void train(DataSet t, int max_iteration, double learning_rate) {
	   BackPropagation backP = new BackPropagation();
	   backP.setMaxIterations(max_iteration);
	   backP.setLearningRate(learning_rate);
	   mlp.learnInNewThread(t, backP);
	}

	@Override
	public double classify(List<double[]> epoch) {
		// TODO Auto-generated method stub
		double[] values = this.fe.extractFeatures(epoch);
		 
		if (values.length != mlp.getInputsCount())
            throw new ArrayIndexOutOfBoundsException("Feature vector dimension must be the same as the number of input neurons.");
        mlp.setInput(values);
        mlp.calculate();
        double[] output = mlp.getOutput();
        return output[0];
	}


	@Override
	public void init(IFeatureExtraction fe) {
		this.fe = fe;
		
	}


	@Override
	public Stat test(List<double[][]> epochs, List<Double> targets) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void train(List<double[][]> epochs, List<Double> targets, IFeatureExtraction fe) {
		this.fe = fe;
		// TODO Auto-generated method stub
		
	}
}
