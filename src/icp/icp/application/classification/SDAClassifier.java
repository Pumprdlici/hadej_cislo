package icp.application.classification;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.AutoEncoder;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.params.DefaultParamInitializer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.api.IterationListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

public class SDAClassifier implements IERPClassifier{

    private IFeatureExtraction fe;		//type of feature extraction (MatchingPursuit, FilterAndSubampling or WaveletTransform)
    private MultiLayerNetwork model;	//multi layer neural network with a logistic output layer and multiple hidden neuralNets
    int iterations;                   //Iterations used to classify
    private final int ITER_DEFAULT = 10; // Default number of iterations
    
    /*Default constructor*/
    public SDAClassifier(){
    	this.iterations = ITER_DEFAULT;
    }
    
    /*Parametric constructor */
    public SDAClassifier(int iterations) {
    	this.iterations = iterations;
    }
    
    /*Classifying features*/
    @Override
    public double classify(double[][] epoch) {
    	double[] featureVector = this.fe.extractFeatures(epoch); // Extracting features to vector
        INDArray features = Nd4j.create(featureVector); // Creating INDArray with extracted features
        double x = model.output(features, Layer.TrainingMode.TEST).getDouble(0); // Result of classifying 
        return x;
    }

    @Override
    public void train(List<double[][]> epochs, List<Double> targets, int numberOfiter, IFeatureExtraction fe) {
    	// Customizing params
        final int numRows = fe.getFeatureDimension();
        final int numColumns = 2;
        int outputNum = 2;
        int seed = 123;
        int listenerFreq = 1;

        //Load Data
        double[][] outcomes = new double[targets.size()][numColumns]; // Matrix of outcomes
        double[][] data = new double[targets.size()][numRows]; // Matrix of data
        for (int i = 0; i < epochs.size(); i++) { // Iterating through epochs
            double[][] epoch = epochs.get(i); // Each epoch
            double[] features = fe.extractFeatures(epoch); // Feature of each epoch
            for(int j = 0; j < numColumns; j++) {
                outcomes[i][0] = targets.get(i); // Setting outcome to target
                outcomes[i][1] = targets.get(i); // Setting outcome to target
            }
            data[i]=features; // Saving feature to data matrix
        }

        INDArray output_data = Nd4j.create(outcomes); // Create INDArray with outcomes
        INDArray input_data = Nd4j.create(data); // Create INDArray with data
        DataSet dataSet = new DataSet(input_data, output_data); // Create dataSet with input and output data
        dataSet.shuffle(); // In place shuffle of an ndarray along a specified set of dimensions
        dataSet.normalizeZeroMeanZeroUnitVariance(); // Subtract by the column means and divide by the standard deviation
        
        SplitTestAndTrain testAndTrain = dataSet.splitTestAndTrain(0.90); // Spliting testing and training data

        DataSet train = testAndTrain.getTrain(); // Training data
        DataSet test = testAndTrain.getTest(); // Testing data
        Nd4j.ENFORCE_NUMERICAL_STABILITY = true; // Setting to enforce numerical stability

        // Build neural net
        build(numRows, numColumns, outputNum, seed, listenerFreq);

        System.out.println("Train model....");
        model.fit(train); // Learning of neural net with training data

        System.out.println("Evaluate weights....");
        for (org.deeplearning4j.nn.api.Layer layer : model.getLayers()) {
            INDArray w = layer.getParam(DefaultParamInitializer.WEIGHT_KEY);
            System.out.println("Weights: " + w);
        }

        //double a = model.score(test);
        Iterator<DataSet> iter = test.iterator(); // Initialize iterator for testing data
        Evaluation eval =new Evaluation(outputNum); // Initialize evaluation of neural net
        while(iter.hasNext()) {	// Iterating through testing data
            DataSet pom = iter.next(); // One line of data
            INDArray labels = pom.getLabels(); // Labels of each line
            INDArray output = model.output(pom.getFeatureMatrix(), Layer.TrainingMode.TEST); // List of ground truth labels for the actual Iris species that each input sample refers to
            //INDArray probability = model.labelProbabilities(pom.getFeatureMatrix());
            int actual = (int)labels.getDouble(0); // Real value
            int predict = (int)Math.round(output.getDouble(0)); // Predicted value
            eval.eval(predict,actual); // Evaluation of prediction
        }
        
        System.out.println("Evaluate model....");
        System.out.println(eval.stats()); // Statistics of evaluation
    }
    
    private void build(int numRows, int numColumns, int outputNum, int seed, int listenerFreq) {
        System.out.print("Build model....");
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder() // Starting builder pattern
                .seed(seed) // Locks in weight initialization for tuning
                .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue) // Gradient normalization strategy
                .gradientNormalizationThreshold(1.0) // Treshold for gradient normalization
                .iterations(iterations) // # training iterations predict/classify & backprop
                .momentum(0.5) // Momentum rate
                .momentumAfter(Collections.singletonMap(3, 0.9)) //Map of the iteration to the momentum rate to apply at that iteration
                .optimizationAlgo(OptimizationAlgorithm.CONJUGATE_GRADIENT) // Backprop to calculate gradients
                .list(4) // # NN layers (doesn't count input layer)
                .layer(0, new AutoEncoder.Builder().nIn(numRows).nOut(500) // Setting layer to Autoencoder
                        .weightInit(WeightInit.XAVIER).lossFunction(LossFunction.RMSE_XENT) // Weight initialization
                        .corruptionLevel(0.3) // Set level of corruption
                        .build() // Build on set configuration
                ) // NN layer type
                .layer(1, new AutoEncoder.Builder().nIn(500).nOut(250) // Setting layer to Autoencoder
                        .weightInit(WeightInit.XAVIER).lossFunction(LossFunction.RMSE_XENT)
                        .corruptionLevel(0.3) // Set level of corruption
                        .build() // Build on set configuration
                ) // NN layer type
                .layer(2, new AutoEncoder.Builder().nIn(250).nOut(200) // Setting layer to Autoencoder
                        .weightInit(WeightInit.XAVIER).lossFunction(LossFunction.RMSE_XENT)
                        .corruptionLevel(0.3) // Set level of corruption
                        .build() // Build on set configuration
                ) // NN layer type
                .layer(3, new OutputLayer.Builder(LossFunction.NEGATIVELOGLIKELIHOOD)//Override default output layer that classifies input using softmax
                		.activation("softmax") // Activation function type
                        .nIn(200) // # input nodes
                        .nOut(outputNum) // # output nodes
                        .build() // Build on set configuration
                 ) // NN layer type
                .pretrain(true) // Do pre training
                .backprop(false) // Don't do back proping
                .build(); // Build on set configuration
        model = new MultiLayerNetwork(conf); // Passing built configuration to instance of multilayer network
        model.init(); // Initialize model
        model.setListeners(Arrays.asList((IterationListener) new ScoreIterationListener(listenerFreq))); // Setting listeners
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
    public void load(InputStream is) {

    }

    @Override
    public void save(OutputStream dest) {

    }
    
    @Override
    public void save(String file) {
        OutputStream fos;
        // Choose the name of classifier and coefficient file to save
        String classifierName = "wrong.classifier";
        String coefficientsName = "wrong.bin";
        if (fe.getClass().getSimpleName().equals("FilterAndSubsamplingFeatureExtraction")){
            classifierName = "19_F&S_SDA.classifier";
            coefficientsName = "coefficients19.bin";
        } else if(fe.getClass().getSimpleName().equals("WaveletTransformFeatureExtraction")){
            classifierName = "20_DWT_SDA.classifier";
            coefficientsName = "coefficients20.bin";
        }else if(fe.getClass().getSimpleName().equals("MatchingPursuitFeatureExtraction")){
            classifierName = "21_MP_SDA.classifier";
            coefficientsName = "coefficients21.bin";
        }
        try {
        	// Save classifier and coefficients 
        	fos = Files.newOutputStream(Paths.get("data/test_classifiers_and_settings/"+coefficientsName));
            DataOutputStream dos = new DataOutputStream(fos);
            Nd4j.write(model.params(), dos);
            dos.flush();
            dos.close();
            FileUtils.writeStringToFile(new File("data/test_classifiers_and_settings/"+classifierName), model.getLayerWiseConfigurations().toJson());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load(String file) {
    	MultiLayerConfiguration confFromJson = null;
    	INDArray newParams = null;
    	// Choose the name of coefficient file to load
    	String coefficientsName = "wrong.bin";
        if (fe.getClass().getSimpleName().equals("FilterAndSubsamplingFeatureExtraction")){
            coefficientsName = "coefficients19.bin";
        } else if(fe.getClass().getSimpleName().equals("WaveletTransformFeatureExtraction")){
            coefficientsName = "coefficients20.bin";
        }else if(fe.getClass().getSimpleName().equals("MatchingPursuitFeatureExtraction")){
            coefficientsName = "coefficients21.bin";
        }
    	try {
    		// Load classifier and coefficients
    		confFromJson = MultiLayerConfiguration.fromJson(FileUtils.readFileToString(new File(file)));
        	DataInputStream dis = new DataInputStream(new FileInputStream("data/test_classifiers_and_settings/"+coefficientsName));
        	newParams = Nd4j.read(dis);
        	dis.close();
    	} catch (IOException e) {
            e.printStackTrace();
        }
    	// Initialize network with loaded params
    	model = new MultiLayerNetwork(confFromJson);
        model.init();
        model.setParams(newParams);
        System.out.println("Original network params " + model.params());
        System.out.println("Loaded");
    }

    @Override
    public IFeatureExtraction getFeatureExtraction() {
        return fe;
    }

    @Override
    public void setFeatureExtraction(IFeatureExtraction fe) {
        this.fe = fe;
    }
}
