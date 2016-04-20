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
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.deeplearning4j.eval.Evaluation;
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
	
	private IFeatureExtraction fe;
    private MultiLayerNetwork model;
    
    public SDAClassifier(){
    	
    }
    
	@Override
	public double classify(double[][] epoch) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void train(List<double[][]> epochs, List<Double> targets, int numberOfiter, IFeatureExtraction fe) {
		// TODO Auto-generated method stub
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
        	       .seed(seed)
        	       .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
        	       .gradientNormalizationThreshold(1.0)
        	       .iterations(iterations)
        	       .momentum(0.5)
        	       .momentumAfter(Collections.singletonMap(3, 0.9))
        	       .optimizationAlgo(OptimizationAlgorithm.CONJUGATE_GRADIENT)
        	       .list(4)
        	       .layer(0, new AutoEncoder.Builder().nIn(numRows * numColumns).nOut(500)
        	               .weightInit(WeightInit.XAVIER).lossFunction(LossFunction.RMSE_XENT)
        	               .corruptionLevel(0.3)
        	               .build())
        	            .layer(1, new AutoEncoder.Builder().nIn(500).nOut(250)
        	                    .weightInit(WeightInit.XAVIER).lossFunction(LossFunction.RMSE_XENT)
        	                    .corruptionLevel(0.3)

        	                    .build())
        	            .layer(2, new AutoEncoder.Builder().nIn(250).nOut(200)
        	                    .weightInit(WeightInit.XAVIER).lossFunction(LossFunction.RMSE_XENT)
        	                    .corruptionLevel(0.3)
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