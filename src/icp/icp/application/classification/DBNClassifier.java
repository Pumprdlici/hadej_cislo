package icp.application.classification;

import org.apache.commons.io.FileUtils;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.RBM;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.params.DefaultParamInitializer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class DBNClassifier implements IERPClassifier {

    private IFeatureExtraction fe;		//type of feature extraction (MatchingPursuit, FilterAndSubampling or WaveletTransform)
    private MultiLayerNetwork model;	//multi layer neural network with a logistic output layer and multiple hidden neuralNets
    int iterations;                   //Iterations used to classify
    private final int ITER_DEFAULT = 10; // Default number of iterations
    
    /*Default constructor*/
    public DBNClassifier() {
    	this.iterations = ITER_DEFAULT;
    }
    
    /*Parametric constructor */
    public DBNClassifier(int iterations) {
    	this.iterations = iterations;
    }
    
    /*Classifying features*/
    @Override
    public double classify(double[][] epoch){
        double[] featureVector = this.fe.extractFeatures(epoch); // Extracting features to vector
        INDArray features = Nd4j.create(featureVector); // Creating INDArray with extracted features
        double x = model.output(features, Layer.TrainingMode.TEST).getDouble(0); // Result of classifying 
        return x;
    }

    @Override
    public void train(List<double[][]> epochs, List<Double> targets, int numberOfIter, IFeatureExtraction fe) {
    	// Customizing params
        Nd4j.MAX_SLICES_TO_PRINT = -1;
        Nd4j.MAX_ELEMENTS_PER_SLICE = -1;
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
                .iterations(iterations) // # training iterations predict/classify & backprop
                .learningRate(1e-6f) // Optimization step size
                .optimizationAlgo(OptimizationAlgorithm.CONJUGATE_GRADIENT) // Backprop to calculate gradients
                .l1(1e-1).regularization(true).l2(2e-4) // Setting regularization, decreasing model size and speed of learning
                .useDropConnect(true) // Generalizing neural net, dropping part of connections
                .list(2) // # NN layers (doesn't count input layer)
                .layer(0, new RBM.Builder(RBM.HiddenUnit.RECTIFIED, RBM.VisibleUnit.GAUSSIAN) // Setting layer to Restricted Boltzmann machine
                        .nIn(numRows) // # input nodes
                        .nOut(3) // # fully connected hidden layer nodes. Add list if multiple layers.
                        .weightInit(WeightInit.XAVIER) // Weight initialization
                        .k(1) // # contrastive divergence iterations
                        .activation("relu") // Activation function type
                        .lossFunction(LossFunction.RMSE_XENT) // Loss function type
                        .updater(Updater.ADAGRAD) // Updater type
                        .dropOut(0.5) // Dropping part of connections
                        .build() // Build on set configuration
                ) // NN layer type
                .layer(1, new OutputLayer.Builder(LossFunction.MCXENT) //Override default output layer that classifies input by Iris label using softmax
                        .nIn(3) // # input nodes
                        .nOut(outputNum) // # output nodes
                        .activation("softmax") // Activation function type
                        .build() // Build on set configuration
                ) // NN layer type
                .build(); // Build on set configuration
        model = new MultiLayerNetwork(conf); // Passing built configuration to instance of multilayer network
        model.init(); // Initialize model
        model.setListeners(new ScoreIterationListener(listenerFreq)); // Setting listeners
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
        String classifierName = "wrong.classifier";
        String coefficientsName = "wrong.bin";
        if (fe.getClass().getSimpleName().equals("FilterAndSubsamplingFeatureExtraction")){
            classifierName = "16_F&S_DBN.classifier";
            coefficientsName = "coefficients16.bin";
        } else if(fe.getClass().getSimpleName().equals("WaveletTransformFeatureExtraction")){
            classifierName = "17_DWT_DBN.classifier";
            coefficientsName = "coefficients17.bin";
        }else if(fe.getClass().getSimpleName().equals("MatchingPursuitFeatureExtraction")){
            classifierName = "18_MP_DBN.classifier";
            coefficientsName = "coefficients18.bin";
        }
        try {
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
        String coefficientsName = "wrong.bin";
        if (fe.getClass().getSimpleName().equals("FilterAndSubsamplingFeatureExtraction")){
            coefficientsName = "coefficients16.bin";
        } else if(fe.getClass().getSimpleName().equals("WaveletTransformFeatureExtraction")){
            coefficientsName = "coefficients17.bin";
        }else if(fe.getClass().getSimpleName().equals("MatchingPursuitFeatureExtraction")){
            coefficientsName = "coefficients18.bin";
        }
        try {
        	confFromJson = MultiLayerConfiguration.fromJson(FileUtils.readFileToString(new File(file)));
        	DataInputStream dis = new DataInputStream(new FileInputStream("data/test_classifiers_and_settings/"+coefficientsName));
        	newParams = Nd4j.read(dis);
        	dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
