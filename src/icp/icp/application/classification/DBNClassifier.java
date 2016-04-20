package icp.application.classification;


import org.apache.commons.io.FileUtils;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.RBM;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.params.DefaultParamInitializer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.api.IterationListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;


public class DBNClassifier implements IERPClassifier {

    private IFeatureExtraction fe;
    private MultiLayerNetwork model;

    public DBNClassifier() {

    }
    
    @Override
    public double classify(double[][] epoch) {
        return 1; //tady by to chtìlo nìco udìlat
    }

    @Override
    public void train(List<double[][]> epochs, List<Double> targets,
                      int numberOfiter, IFeatureExtraction fe) {
        // Customizing params
        //Nd4j.MAX_SLICES_TO_PRINT = -1;
        //Nd4j.MAX_ELEMENTS_PER_SLICE = -1;

        final int numRows = fe.getFeatureDimension();
        final int numColumns = 1;
        int outputNum = 1;
        int numSamples = 120;
        int batchSize = 150;
        int iterations = 5;
        int splitTrainNum = (int) (batchSize * .8);
        int seed = 123;
        int listenerFreq = 1;

        double x = targets.get(0);

        INDArray data = Nd4j.ones(epochs.size(), fe.getFeatureDimension());
        double[][] outcomes = new double[targets.size()][(int) x];
        for (int i = 0; i < epochs.size(); i++) {
            double[][] epoch = epochs.get(i);
            double[] features = fe.extractFeatures(epoch);
            outcomes[i][0] = targets.get(i);
            data.putRow(i, Nd4j.create(features));
        }
        INDArray output_data = Nd4j.create(outcomes);
        DataSet next = new DataSet(data, output_data);

        next.normalizeZeroMeanZeroUnitVariance();
        SplitTestAndTrain splitedDataSet = next.splitTestAndTrain(20);
        DataSet train = splitedDataSet.getTrain();
        DataSet test = splitedDataSet.getTest();

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed) // Locks in weight initialization for tuning
                .iterations(iterations) // # training iterations predict/classify & backprop
                .learningRate(1e-6f) // Optimization step size
                .optimizationAlgo(OptimizationAlgorithm.CONJUGATE_GRADIENT) // Backprop to calculate gradients
                .l1(1e-1).regularization(true).l2(2e-4)
                .useDropConnect(true)
                .list(2) // # NN layers (doesn't count input layer)
                .layer(0, new RBM.Builder(RBM.HiddenUnit.RECTIFIED, RBM.VisibleUnit.GAUSSIAN)
                        .nIn(numRows * numColumns) // # input nodes
                        .nOut(3) // # fully connected hidden layer nodes. Add list if multiple layers.
                        .weightInit(WeightInit.XAVIER) // Weight initialization
                        .k(1) // # contrastive divergence iterations
                        .activation("relu") // Activation function type
                        .lossFunction(LossFunctions.LossFunction.RMSE_XENT) // Loss function type
                        .updater(org.deeplearning4j.nn.conf.Updater.ADAGRAD)
                        .dropOut(0.5)
                        .build()
                ) // NN layer type
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
                        .nIn(3) // # input nodes
                        .nOut(outputNum) // # output nodes
                        .activation("softmax")
                        .build()
                ) // NN layer type
                .build();
        //MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model = new MultiLayerNetwork(conf);
        model.init();
//        model.setListeners(Arrays.asList(new ScoreIterationListener(listenerFreq),
//                new GradientPlotterIterationListener(listenerFreq),
//                new LossPlotterIterationListener(listenerFreq)));


        model.setListeners(Arrays.asList((IterationListener) new ScoreIterationListener(listenerFreq)));
        System.out.println("Train model....");
        model.fit(train);

        System.out.println("Evaluate weights....");
        for (org.deeplearning4j.nn.api.Layer layer : model.getLayers()) {
            INDArray w = layer.getParam(DefaultParamInitializer.WEIGHT_KEY);
            System.out.println("Weights: " + w);
        }

        System.out.println("Evaluate model....");
        Evaluation eval = new Evaluation(outputNum);
        INDArray output = model.output(test.getFeatureMatrix());

        for (int i = 0; i < output.rows(); i++) {
            String actual = test.getLabels().getRow(i).toString().trim();
            String predicted = output.getRow(i).toString().trim();
            System.out.println("actual " + actual + " vs predicted " + predicted);
        }

        eval.eval(test.getLabels(), output);
        System.out.println(eval.stats());
        System.out.println("****************Example finished********************");


        OutputStream fos;
        MultiLayerConfiguration confFromJson = null;
        INDArray newParams = null;
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

            confFromJson = MultiLayerConfiguration.fromJson(FileUtils.readFileToString(new File("data/test_classifiers_and_settings/"+classifierName)));
            DataInputStream dis = new DataInputStream(new FileInputStream("data/test_classifiers_and_settings/"+coefficientsName));
            newParams = Nd4j.read(dis);
            dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MultiLayerNetwork savedNetwork = new MultiLayerNetwork(confFromJson);
        savedNetwork.init();
        savedNetwork.setParams(newParams);
        System.out.println("Original network params " + model.params());
        System.out.println(savedNetwork.params());
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
        // TODO Auto-generated method stub

    }

    @Override
    public void save(OutputStream dest) {
        // TODO Auto-generated method stub

    }

    @Override
    public void save(String file) {
        // TODO Auto-generated method stub

    }

    @Override
    public void load(String file) {
        // TODO Auto-generated method stub

    }

    @Override
    public IFeatureExtraction getFeatureExtraction() {
        // TODO Auto-generated method stub
        return fe;
    }

    @Override
    public void setFeatureExtraction(IFeatureExtraction fe) {
        this.fe = fe;
    }
}