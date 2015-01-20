package icp.online.app;

import icp.algorithm.math.Baseline;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import cz.zcu.kiv.signal.ChannelInfo;
import cz.zcu.kiv.signal.DataTransformer;
import cz.zcu.kiv.signal.EEGDataTransformer;
import cz.zcu.kiv.signal.EEGMarker;
import icp.Const;
import icp.online.app.DataObjects.MessageType;
import icp.online.app.DataObjects.ObserverMessage;

public class OffLineDataProvider extends Observable implements Runnable, IDataProvider {

    private final String vhdrFile;
    private final String vmrkFile;
    private int FZIndex;
    private int CZIndex;
    private int PZIndex;

    private boolean running;

    public OffLineDataProvider(String vhdrFile, String markerFile) {
        this.vhdrFile = vhdrFile;
        this.vmrkFile = markerFile;
        this.running = true;
    }

    public OffLineDataProvider(File eegFile, Observer obs) {
        this.addObserver(obs);
        int index = eegFile.getAbsolutePath().lastIndexOf(".");
        String baseName = eegFile.getAbsolutePath().substring(0, index);

        this.vhdrFile = baseName + Const.VHDR_EXTENSION;
        this.vmrkFile = baseName + Const.VMRK_EXTENSION;
        this.running = true;
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
        DataTransformer dt = new EEGDataTransformer();

        try {
            List<ChannelInfo> channels = dt.getChannelInfo(vhdrFile);
            for (ChannelInfo channel : channels) {
                switch (channel.getName().toLowerCase()) {
                    case "fz":
                        FZIndex = channel.getNumber();
                        break;
                    case "cz":
                        CZIndex = channel.getNumber();
                        break;
                    case "pz":
                        PZIndex = channel.getNumber();
                        break;
                }
            }
            
            System.out.println(FZIndex + " " + CZIndex + " " + PZIndex);
            double[] fzChannel = dt.readBinaryData(vhdrFile, FZIndex);
            double[] czChannel = dt.readBinaryData(vhdrFile, CZIndex);
            double[] pzChannel = dt.readBinaryData(vhdrFile, PZIndex);
            Map<String, EEGMarker> markers = dt.readMarkers(vmrkFile);
            for (Map.Entry<String, EEGMarker> entry : markers.entrySet()) {
                if (!running) {
                    break;
                }
                EEGMarker marker = entry.getValue();
                EpochMessenger em = new EpochMessenger();

                String stimulusNumber = marker.getStimulus().replaceAll("[\\D]", "");
                int stimulusIndex = -1;
                if (stimulusNumber.length() > 0) {
                    stimulusIndex = Integer.parseInt(stimulusNumber) - 1;
                }
                em.setStimulusIndex(stimulusIndex);
                float[] ffzChannel = toFloatArray(Arrays.copyOfRange(fzChannel,
                        marker.getPosition() - Const.PREESTIMULUS_VALUES, marker.getPosition() + Const.POSTSTIMULUS_VALUES));
                float[] fczChannel = toFloatArray(Arrays.copyOfRange(czChannel,
                        marker.getPosition() - Const.PREESTIMULUS_VALUES, marker.getPosition() + Const.POSTSTIMULUS_VALUES));
                float[] fpzChannel = toFloatArray(Arrays.copyOfRange(pzChannel,
                        marker.getPosition() - Const.PREESTIMULUS_VALUES, marker.getPosition() + Const.POSTSTIMULUS_VALUES));

                Baseline.correct(ffzChannel, Const.PREESTIMULUS_VALUES);
                Baseline.correct(fczChannel, Const.PREESTIMULUS_VALUES);
                Baseline.correct(fpzChannel, Const.PREESTIMULUS_VALUES);

                em.setFZ(ffzChannel, 100);
                em.setCZ(fczChannel, 100);
                em.setPZ(fpzChannel, 100);

                this.setChanged();
                this.notifyObservers(em);
            }
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
}
