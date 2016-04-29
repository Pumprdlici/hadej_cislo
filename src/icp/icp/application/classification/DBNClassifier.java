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

    private IFeatureExtraction fe;
    private MultiLayerNetwork model;

    public DBNClassifier() {

    }

    //public DBNClassifier(INDArray params){
    //	model.setParams(params);
    //}

    @Override
    public double classify(double[][] epoch){
        double[] featureVector = this.fe.extractFeatures(epoch);
        INDArray features = Nd4j.create(featureVector);
        //---------------------------------------------------tady už musí bejt model inicializovanej !
        return model.output(features, Layer.TrainingMode.TEST).getDouble(0);
    }

    @Override
    public void train(List<double[][]> epochs, List<Double> targets,
                      int numberOfIter, IFeatureExtraction fe) {


        Nd4j.MAX_SLICES_TO_PRINT = -1;
        Nd4j.MAX_ELEMENTS_PER_SLICE = -1;
        final int numRows = fe.getFeatureDimension();
        final int numColumns = 2;
        int outputNum = 2;
        int iteration = 10;
        int seed = 123;
        int listenerFreq = 1;

        //Load Data
        double[][] outcomes = new double[targets.size()][numColumns];
        double [][]data =  new double[targets.size()][fe.getFeatureDimension()];
        for (int i = 0; i < epochs.size(); i++) {
            double[][] epoch = epochs.get(i);
            double[] features = fe.extractFeatures(epoch);
            for(int j=0;j<numColumns;j++) {
                outcomes[i][0] = targets.get(i);
                outcomes[i][1] = targets.get(i);
            }
            data[i]=features;
        }

        INDArray output_data = Nd4j.create(outcomes);
        INDArray input_data = Nd4j.create(data);
        DataSet dataSet = new DataSet(input_data, output_data);
        dataSet.shuffle();
        dataSet.normalizeZeroMeanZeroUnitVariance();

        SplitTestAndTrain testAndTrain = dataSet.splitTestAndTrain(0.90);

        DataSet train = testAndTrain.getTrain();
        DataSet test = testAndTrain.getTest();
        Nd4j.ENFORCE_NUMERICAL_STABILITY = true;

        //build
        build(numRows, numColumns, outputNum, iteration, seed, listenerFreq);



        System.out.println("Train model....");
        model.fit(train);

        System.out.println("Evaluate weights....");
        for (org.deeplearning4j.nn.api.Layer layer : model.getLayers()) {
            INDArray w = layer.getParam(DefaultParamInitializer.WEIGHT_KEY);
            System.out.println("Weights: " + w);
        }

        double a = model.score(test);
        //Evaluation eval = new Evaluation(outputNum);
        Iterator<DataSet> iter = test.iterator();
        Evaluation eval =new Evaluation(outputNum);
        while(iter.hasNext()) {
            DataSet pom = iter.next();
            INDArray labels = pom.getLabels();
            INDArray output = model.output(pom.getFeatureMatrix(), Layer.TrainingMode.TEST);
            //INDArray probability = model.labelProbabilities(pom.getFeatureMatrix());
            int actual = (int)labels.getDouble(0);
            int predict = (int)Math.round(output.getDouble(0));
            eval.eval(predict,actual);
        }
        System.out.println("Evaluate model....");
        System.out.println(eval.stats());
        System.out.println("****************Example finished********************");


        save(fe);
    }

    

    private void build(int numRows, int numColumns, int outputNum, int iterations, int seed, int listenerFreq) {
        System.out.print("Build model....");
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed) // Locks in weight initialization for tuning
                .iterations(iterations) // # training iterations predict/classify & backprop
                .learningRate(1e-6f) // Optimization step size
                .optimizationAlgo(OptimizationAlgorithm.CONJUGATE_GRADIENT) // Backprop to calculate gradients
                .l1(1e-1).regularization(true).l2(2e-4)
                .useDropConnect(true)
                .list(2) // # NN layers (doesn't count input layer)
                .layer(0, new RBM.Builder(RBM.HiddenUnit.RECTIFIED, RBM.VisibleUnit.GAUSSIAN)
                        .nIn(numRows) // # input nodes
                        .nOut(3) // # fully connected hidden layer nodes. Add list if multiple layers.
                        .weightInit(WeightInit.XAVIER) // Weight initialization
                        .k(1) // # contrastive divergence iterations
                        .activation("relu") // Activation function type
                        .lossFunction(LossFunction.RMSE_XENT) // Loss function type
                        .updater(Updater.ADAGRAD)
                        .dropOut(0.5)
                        .build()
                ) // NN layer type
                .layer(1, new OutputLayer.Builder(LossFunction.MCXENT)
                        .nIn(3) // # input nodes
                        .nOut(outputNum) // # output nodes
                        .activation("softmax")
                        .build()
                ) // NN layer type
                .build();
        model = new MultiLayerNetwork(conf);
        model.init();

        model.setListeners(new ScoreIterationListener(listenerFreq));
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
    private void save(IFeatureExtraction fe) {
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
    public void save(String file) {
//        // TODO Auto-generated method stub
//    	OutputStream fos;
//        String classifierName = "wrong.classifier";
//        String coefficientsName = "wrong.bin";
//    	if (fe.getClass().getSimpleName().equals("FilterAndSubsamplingFeatureExtraction")){
//    		classifierName = "16_F&S_DBN.classifier";
//    		coefficientsName = "coefficients16.bin";
//    	} else if(fe.getClass().getSimpleName().equals("WaveletTransformFeatureExtraction")){
//    		classifierName = "17_DWT_DBN.classifier";
//    		coefficientsName = "coefficients17.bin";
//    	}else if(fe.getClass().getSimpleName().equals("MatchingPursuitFeatureExtraction")){
//    		classifierName = "18_MP_DBN.classifier";
//    		coefficientsName = "coefficients18.bin";
//    	}
//        try {
//            fos = Files.newOutputStream(Paths.get("data/test_classifiers_and_settings/"+coefficientsName));
//            DataOutputStream dos = new DataOutputStream(fos);
//            Nd4j.write(model.params(), dos);
//            dos.flush();
//            dos.close();
//            FileUtils.writeStringToFile(new File("data/test_classifiers_and_settings/"+classifierName), model.getLayerWiseConfigurations().toJson());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void load(String file) {
        MultiLayerConfiguration confFromJson = null;
        try {
        	confFromJson = MultiLayerConfiguration.fromJson(FileUtils.readFileToString(new File(file)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        model = new MultiLayerNetwork(confFromJson);
    }

    @Override
    public IFeatureExtraction getFeatureExtraction() {
        return fe;
    }

    @Override
    public void setFeatureExtraction(IFeatureExtraction fe) {
        this.fe = fe;
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
        	DataInputStream dis = new DataInputStream(new FileInputStream("data/test_classifiers_and_settings/"+coefficientsName));
        	newParams = Nd4j.read(dis);
        	dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        model.init();
        model.setParams(newParams);
        System.out.println("Original network params " + model.params());
    }
}
