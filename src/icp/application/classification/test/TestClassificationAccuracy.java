package icp.application.classification.test;

import icp.online.app.DataObjects.MessageType;
import icp.online.app.DataObjects.ObserverMessage;
import icp.online.app.OnlineDetection;
import icp.application.classification.FilterFeatureExtraction;
import icp.application.classification.IERPClassifier;
import icp.application.classification.IFeatureExtraction;
import icp.application.classification.MLPClassifier;
import icp.online.app.OffLineDataProvider;
import icp.online.gui.*;

import java.io.*;
import java.util.*;

/**
 * Created by stebjan on 20.1.2015.
 */
public class TestClassificationAccuracy implements Observer {

    private String dir = "data/numbers";
    private Map<String, Integer> results;
    private Integer[] result;
    private String filename;
    private boolean end;
    private int[] passOrFail = {0, 0};
    private List<String> okFiles = new ArrayList<String>();



    public static void main(String[] args) throws InterruptedException {
        new TestClassificationAccuracy();

    }

    public TestClassificationAccuracy() throws InterruptedException {

        try {
            results = loadExpectedResults("info.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        File directory = new File(dir);
        for (File file : directory.listFiles()) {
            if (file.getName().endsWith("eeg")) {

                end = false;
                filename = file.getName();
                IERPClassifier classifier = new MLPClassifier();
                classifier.load("data/classifier.txt");
                IFeatureExtraction fe = new FilterFeatureExtraction();
                classifier.setFeatureExtraction(fe);
                OnlineDetection detection = new OnlineDetection(classifier, this);
                OffLineDataProvider offLineData = new OffLineDataProvider(file, detection);
                Thread t = new Thread(offLineData);
                t.start();
                while(!end) {
                    Thread.sleep(500);
                }

            }
        }
        System.out.println(getPercentageResult(passOrFail));
        for (String name: okFiles) {
            System.out.println(name);
        }
    }

    private Map<String, Integer> loadExpectedResults(String filename) throws IOException {
        Map<String, Integer> results = new HashMap<String, Integer>();
        File file = new File(dir + File.separator + filename);
        FileInputStream fis = new FileInputStream(file);

        //Construct BufferedReader from InputStreamReader
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        String line = null;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(" ");
            results.put(parts[0], Integer.parseInt(parts[1]));
        }

        br.close();
        return results;
    }

    private Integer[] initProbabilities(double[] probabilities) {
        Integer[] ranks = new Integer[probabilities.length];
        for (int i = 0; i < ranks.length; ++i) {
            ranks[i] = i;
        }
        Comparator<Integer> gc = new ProbabilityComparator(probabilities);
        Arrays.sort(ranks, gc);
        return ranks;

    }

    private int getExpectedResult(String filename) {
        String fileWithoutExtension = (filename.split("[.]"))[0];
        int index = fileWithoutExtension.lastIndexOf("_");
        String fileNumber = fileWithoutExtension.substring(index + 1);
        return results.get(fileNumber);
    }

    private float getPercentageResult(int[] passOrFail) {
        return passOrFail[0];
    }

    @Override
    public void update(Observable o, Object message) {
        if (message instanceof OnlineDetection) {
            double[] probabilities = ((OnlineDetection) message).getWeightedResults();

            result = initProbabilities(probabilities);

        }
        if (message instanceof ObserverMessage) {
            System.out.println("Observer message");
            ObserverMessage msg = (ObserverMessage) message;
            if (msg.getMsgType() == MessageType.END) {
                System.out.println(filename);
                System.out.println("I think: " + (result[0] + 1) + " ,I should think: " +getExpectedResult(filename));
                if ((result[0] + 1) == getExpectedResult(filename)) {
                    okFiles.add(filename);
                    passOrFail[0]++;
                } else {
                    passOrFail[1]++;
                }
                end = true;

            }
        }
    }
}
