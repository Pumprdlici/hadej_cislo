package icp.online.app;

import cz.zcu.kiv.signal.ChannelInfo;
import cz.zcu.kiv.signal.DataTransformer;
import cz.zcu.kiv.signal.EEGDataTransformer;
import cz.zcu.kiv.signal.EEGMarker;
import icp.Const;
import icp.algorithm.math.Baseline;
import icp.algorithm.math.IArtifactDetection;
import icp.online.app.DataObjects.MessageType;
import icp.online.app.DataObjects.ObserverMessage;
import icp.online.gui.MainFrame;

import java.io.*;
import java.nio.ByteOrder;
import java.util.*;

public class OffLineDataProvider extends Observable implements Runnable, IDataProvider {

    private String vhdrFile;
    private String vmrkFile;
    private String eegFile;
    private int FZIndex;
    private int CZIndex;
    private int PZIndex;
    private Map<String, Integer> files;


    private IArtifactDetection artifactDetector;

    private boolean running;

//    public OffLineDataProvider(String vhdrFile, String markerFile) {
//        this.vhdrFile = vhdrFile;
//        this.vmrkFile = markerFile;
//
//        String baseName = vhdrFile.substring(0, vhdrFile.lastIndexOf("."));
//        this.eegFile = baseName + Const.EEG_EXTENSION;
//        //this.eegFile = baseName + ".dat";
//
//        this.running = true;
//    }

    public OffLineDataProvider(File eegFile, Observer obs) {
        this.addObserver(obs);

        files = new HashMap<String, Integer>();
        files.put(eegFile.getAbsolutePath(), 2);
        //setFileNames(eegFile.getAbsolutePath());
        //this.eegFile = baseName + ".dat";
        this.running = true;
    }

    public OffLineDataProvider(String trainDir, Observer obs) throws IOException {
        this.addObserver(obs);
        File dir = new File(trainDir);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new FileNotFoundException(dir + " is not a directory");
        }
        this.files = loadExpectedResults(trainDir);
        this.running = true;
    }

    private void setFileNames(String filename) {
        int index = filename.lastIndexOf(".");
        String baseName = filename.substring(0, index);
        this.vhdrFile = baseName + Const.VHDR_EXTENSION;
        this.vmrkFile = baseName + Const.VMRK_EXTENSION;
        this.eegFile = baseName + Const.EEG_EXTENSION;
    }

    private float[] toFloatArray(double[] arr) {
        if (arr == null) {
            return null;
        }
        int n = arr.length;
        float[] ret = new float[n];
        for (int i = 0; i < n; i++) {
            ret[i] = (float) arr[i];
        }
        return ret;
    }

    @Override
    public void run() {


        try {
            for (Map.Entry<String, Integer> fileEntry: files.entrySet()) {
                DataTransformer dt = new EEGDataTransformer();
                setFileNames(fileEntry.getKey());
                File file = new File(fileEntry.getKey());
                if (!file.exists()) {
                    System.out.println(file.getAbsolutePath() + " not exists!");
                    continue;
                }

                List<ChannelInfo> channels = dt.getChannelInfo(vhdrFile);
                for (ChannelInfo channel : channels) {
                    if ("fz".equals(channel.getName().toLowerCase())) {
                        FZIndex = channel.getNumber();
                    } else if ("cz".equals(channel.getName().toLowerCase())) {
                        CZIndex = channel.getNumber();
                    }
                    if ("pz".equals(channel.getName().toLowerCase())) {
                        PZIndex = channel.getNumber();
                    }
                }

                //create a File class object and give the file the name employees.csv
//            java.io.File file = new java.io.File("RawData_BaselineCorrect.csv");
//            java.io.File pzFile = new java.io.File("RawPzChannel.csv");
//            //Create a Printwriter text output stream and link it to the CSV File
//            java.io.PrintWriter outfile = new java.io.PrintWriter(file);
//            java.io.PrintWriter pzPw = new java.io.PrintWriter(pzFile);
                ByteOrder order = ByteOrder.LITTLE_ENDIAN;
                //System.out.println(eegFile);
                double[] fzChannel = dt.readBinaryData(vhdrFile, eegFile, FZIndex, order);
                double[] czChannel = dt.readBinaryData(vhdrFile, eegFile, CZIndex, order);
                double[] pzChannel = dt.readBinaryData(vhdrFile, eegFile, PZIndex, order);

                //writePzIntoCsv(pzChannel, pzPw);

                List<EEGMarker> markers = dt.readMarkerList(vmrkFile);
                Collections.shuffle(markers);
                for (EEGMarker marker : markers) {
                    if (!running) {
                        break;
                    }

                    EpochMessenger em = new EpochMessenger();

                    String stimulusNumber = marker.getStimulus().replaceAll("[\\D]", "");
                    int stimulusIndex = -1;
                    if (stimulusNumber.length() > 0) {
                        stimulusIndex = Integer.parseInt(stimulusNumber) - 1;
                    }
                    em.setStimulusIndex(stimulusIndex);
                    try {
                        float[] ffzChannel = toFloatArray(Arrays.copyOfRange(fzChannel,
                                marker.getPosition() - Const.PREESTIMULUS_VALUES, marker.getPosition() + Const.POSTSTIMULUS_VALUES));
                        float[] fczChannel = toFloatArray(Arrays.copyOfRange(czChannel,
                                marker.getPosition() - Const.PREESTIMULUS_VALUES, marker.getPosition() + Const.POSTSTIMULUS_VALUES));
                        float[] fpzChannel = toFloatArray(Arrays.copyOfRange(pzChannel,
                                marker.getPosition() - Const.PREESTIMULUS_VALUES, marker.getPosition() + Const.POSTSTIMULUS_VALUES));

                        Baseline.correct(ffzChannel, Const.PREESTIMULUS_VALUES);
                        Baseline.correct(fczChannel, Const.PREESTIMULUS_VALUES);
                        Baseline.correct(fpzChannel, Const.PREESTIMULUS_VALUES);

                        //writeCsv(fpzChannel, stimulusNumber, outfile);

                        em.setFZ(ffzChannel, 100);
                        em.setCZ(fczChannel, 100);
                        em.setPZ(fpzChannel, 100);

                        if (em.getStimulusIndex() + 1 == fileEntry.getValue()) {
//                            System.out.println(em.getStimulusIndex());
                            em.setTarget(true);
                        }

                        artifactDetector = MainFrame.artifactDetection;
                        if (artifactDetector != null) {
                            em = artifactDetector.detectArtifact(em);
                        }

                        this.setChanged();
                        this.notifyObservers(em);
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        ex.printStackTrace();
                    }
                }
            }
//            outfile.close();
            this.setChanged();
            this.notifyObservers(new ObserverMessage(MessageType.END, "EEG file loaded."));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        this.running = false;
    }

    private void writeCsv(float[] vals, String stimulus, PrintWriter outfile) throws FileNotFoundException {

        //Iterate the elements actually being used
        for (int i = 0; i < vals.length; i++) {
            outfile.append(vals[i] + ",");
        }
        outfile.append(stimulus);
        outfile.append("\n");

    }

    private void writePzIntoCsv(double[] vals, PrintWriter outfile) {
        //Iterate the elements actually being used
        for (int i = 0; i < vals.length; i++) {
            outfile.append(vals[i] + ",");
        }
        //outfile.append(stimulus);
        outfile.append("\n");
    }
    private Map<String, Integer> loadExpectedResults(String dir) throws IOException {
        Map<String, Integer> res = new HashMap<>();
        File file = new File(dir + File.separator + "infoTrain.txt");
        //File file = new File(dir + File.separator + "info.txt");
        FileInputStream fis = new FileInputStream(file);

        //Construct BufferedReader from InputStreamReader
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        String line;
        int num;
        while ((line = br.readLine()) != null) {
            if (line.charAt(0) == '#') { //comment in info txt
                continue;
            }
            String[] parts = line.split(" ");
            if (parts.length > 1) {
                try {
                    num = Integer.parseInt(parts[1]);
                    res.put(dir + File.separator + parts[0], num);
                } catch (NumberFormatException ex) {
                    //NaN
                }
            }
        }

        br.close();
        return res;
    }
}
