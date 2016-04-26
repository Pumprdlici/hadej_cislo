package icp.application.classification;


import org.apache.commons.io.FileUtils;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.GradientNormalization;
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
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class DBNClassifier implements IERPClassifier {

    private IFeatureExtraction fe;
    private MultiLayerNetwork model;

    public DBNClassifier() {
    	
    }
    
    @Override
    public double classify(double[][] epoch){
    	double[] featureVector = this.fe.extractFeatures(epoch);
        INDArray labels = Nd4j.create(featureVector.length,2);
        INDArray features = Nd4j.create(featureVector.length,1);
        for(int i=0;i<featureVector.length;i++){
            double pom[] = new double[]{0,1};
            double x[] = new double[]{featureVector[i]};
            labels.putRow(i,Nd4j.create(pom));
            features.putRow(i,Nd4j.create(x));
        }
        DataSet dataSet = new DataSet(features,labels);
        INDArray data = dataSet.getFeatureMatrix();
       // INDArray output = model.output(dataSet.getFeatureMatrix());
        model.output(Nd4j.create(featureVector), Layer.TrainingMode.TEST);
        return 0;
    }

    @Override
    public void train(List<double[][]> epochs, List<Double> targets,
                      int numberOfiter, IFeatureExtraction fe) {

        final int numRows = fe.getFeatureDimension();
        final int numColumns = 10;
        int outputNum = 10;
        int batchSize = 50;
        int iterations = 10;
        int seed = 123;
        int listenerFreq = batchSize / 5;

        //Load Data
        INDArray data = Nd4j.ones(epochs.size(), fe.getFeatureDimension());
        double[][] outcomes = new double[targets.size()][numColumns];
        for (int i = 0; i < epochs.size(); i++) {
            double[][] epoch = epochs.get(i);
            double[] features = fe.extractFeatures(epoch);
            for(int j=0;j<numColumns;j++)
            outcomes[i][j] = targets.get(i);
            data.putRow(i, Nd4j.create(features));
        }
        INDArray output_data = Nd4j.create(outcomes);
        DataSet dataSet = new DataSet(data, output_data);
        dataSet.shuffle();
        //Split test/train
        SplitTestAndTrain splitedDataSet = dataSet.splitTestAndTrain(308);
        List<DataSet> testovani = splitedDataSet.getTest().batchBy(50);
        DataSet train = splitedDataSet.getTrain();


        //build
        System.out.print("Build model....");
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
                .gradientNormalizationThreshold(1.0)
                .iterations(iterations)
                .momentum(0.5)
                .momentumAfter(Collections.singletonMap(3, 0.9))
                .optimizationAlgo(OptimizationAlgorithm.CONJUGATE_GRADIENT)
                .list(4)
                .layer(0, new RBM.Builder().nIn(numRows).nOut(500)
                        .weightInit(WeightInit.XAVIER).lossFunction(LossFunction.RMSE_XENT)
                        .visibleUnit(RBM.VisibleUnit.BINARY)
                        .hiddenUnit(RBM.HiddenUnit.BINARY)
                        .build())
                .layer(1, new RBM.Builder().nIn(500).nOut(250)
                        .weightInit(WeightInit.XAVIER).lossFunction(LossFunction.RMSE_XENT)
                        .visibleUnit(RBM.VisibleUnit.BINARY)
                        .hiddenUnit(RBM.HiddenUnit.BINARY)
                        .build())
                .layer(2, new RBM.Builder().nIn(250).nOut(200)
                        .weightInit(WeightInit.XAVIER).lossFunction(LossFunction.RMSE_XENT)
                        .visibleUnit(RBM.VisibleUnit.BINARY)
                        .hiddenUnit(RBM.HiddenUnit.BINARY)
                        .build())
                .layer(3, new OutputLayer.Builder(LossFunction.NEGATIVELOGLIKELIHOOD).activation("softmax")
                        .nIn(200).nOut(outputNum).build())
                .pretrain(true).backprop(false)
                .build();
        model = new MultiLayerNetwork(conf);
        model.init();

        model.setListeners(Arrays.asList((IterationListener) new ScoreIterationListener(listenerFreq)));


        System.out.println("Train model....");
        model.fit(train);

        System.out.println("Evaluate weights....");
        for (org.deeplearning4j.nn.api.Layer layer : model.getLayers()) {
            INDArray w = layer.getParam(DefaultParamInitializer.WEIGHT_KEY);
            System.out.println("Weights: " + w);
        }

        Evaluation eval = new Evaluation(outputNum);

        System.out.print("Evaluate model....");
        for(DataSet d : testovani){
            DataSet test=d;
            INDArray predict2 = model.output(d.getFeatureMatrix(),false);
            eval.eval(d.getLabels(),predict2);
        }
        System.out.print(eval.stats());
        System.out.print("****************Example finished********************");


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
//        // TODO Auto-generated method stub
//        MultiLayerConfiguration confFromJson = null;
//        INDArray newParams = null;
//
//        try {
//        	confFromJson = MultiLayerConfiguration.fromJson(FileUtils.readFileToString(new File(file)));
//        	DataInputStream dis = new DataInputStream(new FileInputStream(file));
//        	newParams = Nd4j.read(dis);
//        	dis.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        MultiLayerNetwork savedNetwork = new MultiLayerNetwork(confFromJson);
//        savedNetwork.init();
//        savedNetwork.setParams(newParams);
//        System.out.println("Original network params " + model.params());
//        System.out.println(savedNetwork.params());
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
