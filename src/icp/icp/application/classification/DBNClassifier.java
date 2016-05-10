package icp.application.classification;

import org.apache.commons.io.FileUtils;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.RBM;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

// creates instance of Deep Belief Network @author Pumprdlici group
public class DBNClassifier implements IERPClassifier {
    private final int NEURON_COUNT = 24; //default number of neurons
    private IFeatureExtraction fe;        //type of feature extraction (MatchingPursuit, FilterAndSubampling or WaveletTransform)
    private MultiLayerNetwork model;    //multi layer neural network with a logistic output layer and multiple hidden neuralNets
    private int iterations;             //Iterations used to classify
    private int neuronCount;            // Number of neurons

    /*Default constructor*/
    public DBNClassifier() {
        this.neuronCount = NEURON_COUNT; // sets count of neurons to default number
    }

    /*Parametric constructor */
    public DBNClassifier(int neuronCount) {
        this.neuronCount = neuronCount;     // sets count of neurons to parameter
    }

    /*Classifying features*/
    @Override
    public double classify(double[][] epoch) {
        double[] featureVector = this.fe.extractFeatures(epoch); // Extracting features to vector
        INDArray features = Nd4j.create(featureVector); // Creating INDArray with extracted features
        return model.output(features, Layer.TrainingMode.TEST).getDouble(0); // Result of classifying
    }

    @Override
    public void train(List<double[][]> epochs, List<Double> targets, int numberOfIter, IFeatureExtraction fe) {

        // Customizing params of classifier
        final int numRows = fe.getFeatureDimension();   // number of targets on a line
        final int numColumns = 2;   // number of labels needed for classifying
        int seed = 123; //  seed - one of parameters. For more info check http://deeplearning4j.org/iris-flower-dataset-tutorial
        this.iterations = numberOfIter; // number of iteration in the learning phase
        int listenerFreq = this.iterations / 10;  // frequency of output strings

        //Load Data - when target is 0, label[0] is 0 and label[1] is 1.
        double[][] labels = new double[targets.size()][numColumns]; // Matrix of labels for classifier
        double[][] features_matrix = new double[targets.size()][numRows]; // Matrix of features
        for (int i = 0; i < epochs.size(); i++) { // Iterating through epochs
            double[][] epoch = epochs.get(i); // Each epoch
            double[] features = fe.extractFeatures(epoch); // Feature of each epoch
            for (int j = 0; j < numColumns; j++) {   //setting labels for each column
                labels[i][0] = targets.get(i); // Setting label on position 0 as target
                labels[i][1] = Math.abs(1 - targets.get(i));  // Setting label on position 1 to be different from label[0]
            }
            features_matrix[i] = features; // Saving features to features matrix
        }

        INDArray output_data = Nd4j.create(labels); // Create INDArray with labels(targets)
        INDArray input_data = Nd4j.create(features_matrix); // Create INDArray with features(data)
        DataSet dataSet = new DataSet(input_data, output_data); // Create dataSet with features and labels

        // Building a neural net
        build(numRows, numColumns, seed, listenerFreq);

        System.out.println("Train model....");
        model.fit(dataSet); // Learning of neural net with training data
    }

    //  initialization of neural net with params. For more info check http://deeplearning4j.org/iris-flower-dataset-tutorial where is more about params
    private void build(int numRows, int outputNum, int seed, int listenerFreq) {
        System.out.print("Build model....");
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder() // Starting builder pattern
                .seed(seed) // Locks in weight initialization for tuning
                .iterations(this.iterations) // # training iterations predict/classify & backprop
                .learningRate(0.001) // Optimization step size
                .optimizationAlgo(OptimizationAlgorithm.CONJUGATE_GRADIENT) // Backprop to calculate gradients
                .l1(0.001).regularization(true).l2(0.0001) // Setting regularization, decreasing model size and speed of learning
                .useDropConnect(true) // Generalizing neural net, dropping part of connections
                .list(2) // # NN layers (doesn't count input layer)
                .layer(0, new RBM.Builder(RBM.HiddenUnit.RECTIFIED, RBM.VisibleUnit.GAUSSIAN) // Setting layer to Restricted Boltzmann machine
                        .nIn(numRows) // # input nodes
                        .nOut(neuronCount) // # fully connected hidden layer nodes. Add list if multiple layers.
                        .weightInit(WeightInit.XAVIER) // Weight initialization
                        .k(3) // # contrastive divergence iterations
                        .activation("relu") // Activation function type
                        .lossFunction(LossFunction.RMSE_XENT) // Loss function type
                        .updater(Updater.ADAGRAD) // Updater type
                        .dropOut(0.5) // Dropping part of connections
                        .build() // Build on set configuration
                ) // NN layer type
                .layer(1, new OutputLayer.Builder(LossFunction.MCXENT) //Override default output layer that classifies input by Iris label using softmax
                        .nIn(neuronCount) // # input nodes
                        .nOut(outputNum) // # output nodes
                        .activation("softmax") // Activation function type
                        .build() // Build on set configuration
                ) // NN layer type
                .build(); // Build on set configuration
        model = new MultiLayerNetwork(conf); // Passing built configuration to instance of multilayer network
        model.init(); // Initialize model
        model.setListeners(new ScoreIterationListener(listenerFreq)); // Setting listeners
    }

    // method for testing the classifier.
    @Override
    public ClassificationStatistics test(List<double[][]> epochs, List<Double> targets) {
        ClassificationStatistics resultsStats = new ClassificationStatistics(); // initialization of classifier statistics
        for (int i = 0; i < epochs.size(); i++) {   //iterating epochs
            double output = this.classify(epochs.get(i));  //   output means score of a classifier from method classify
            resultsStats.add(output, targets.get(i));   // calculating statistics
        }
        return resultsStats;    //  returns classifier statistics
    }

    // method not implemented. For saving use method save(String file)
    @Override
    public void load(InputStream is) {

    }

    // method not implemented. For loading use load(String file)
    @Override
    public void save(OutputStream dest) {

    }

    @Override
    public void save(String file) {
        OutputStream fos;
        // Choose the name of classifier and coefficient file to save
        String coefficientsName = "wrong.bin";
        if (fe.getClass().getSimpleName().equals("FilterAndSubsamplingFeatureExtraction")) {
            coefficientsName = "coefficients16.bin";
        } else if (fe.getClass().getSimpleName().equals("WaveletTransformFeatureExtraction")) {
            coefficientsName = "coefficients17.bin";
        } else if (fe.getClass().getSimpleName().equals("MatchingPursuitFeatureExtraction")) {
            coefficientsName = "coefficients18.bin";
        }
        try {
            // Save classifier and coefficients
            fos = Files.newOutputStream(Paths.get("data/test_classifiers_and_settings/" + coefficientsName));
            DataOutputStream dos = new DataOutputStream(fos);
            Nd4j.write(model.params(), dos);
            dos.flush();
            dos.close();
            FileUtils.writeStringToFile(new File(file), model.getLayerWiseConfigurations().toJson());
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
        if (fe.getClass().getSimpleName().equals("FilterAndSubsamplingFeatureExtraction")) {
            coefficientsName = "coefficients16.bin";
        } else if (fe.getClass().getSimpleName().equals("WaveletTransformFeatureExtraction")) {
            coefficientsName = "coefficients17.bin";
        } else if (fe.getClass().getSimpleName().equals("MatchingPursuitFeatureExtraction")) {
            coefficientsName = "coefficients18.bin";
        }
        try {
            // Load classifier and coefficients
            confFromJson = MultiLayerConfiguration.fromJson(FileUtils.readFileToString(new File(file)));
            DataInputStream dis = new DataInputStream(new FileInputStream("data/test_classifiers_and_settings/" + coefficientsName));
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
