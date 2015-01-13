package icp.online.app;

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

public class OffLineDataProvider extends Observable  implements Runnable, IDataProvider{
	
	private String vhdrFile;
	private String vmrkFile;
	private final int FZ_INDEX = 1;
	private final int CZ_INDEX = 2;
	private final int PZ_INDEX = 3;
	
	private static final int POCETHODNOTPREDEPOCHOU = 100;
	private static final int POCETHODNOTZAEPOCHOU = 512;
	
	private boolean running;
	
	public OffLineDataProvider(String vhdrFile, String markerFile) {
		this.vhdrFile = vhdrFile;
		this.vmrkFile = markerFile;
		this.running = true;
	}

	public OffLineDataProvider(File eegFile, Observer obs) {
		this.addObserver(obs);
		int index = eegFile.getAbsolutePath().lastIndexOf(".");
		
		String ext = eegFile.getAbsolutePath().substring(index);
		String baseName = eegFile.getAbsolutePath().substring(0, index);
		
		this.vhdrFile = baseName + ".vhdr";
		this.vmrkFile = baseName + ".vmrk";
		this.running = true;
	}
	
	private float[] toFloatArray(double[] arr) {
		  if (arr == null) return null;
		  int n = arr.length;
		  float[] ret = new float[n];
		  for (int i = 0; i < n; i++) {
		    ret[i] = (float)arr[i];
		  }
		  return ret;
	}

	
	

	@Override
	public void run() {
		DataTransformer dt = new EEGDataTransformer();
		
		try {
			
			double[] fzChannel = dt.readBinaryData(vhdrFile, FZ_INDEX);
			double[] czChannel = dt.readBinaryData(vhdrFile, CZ_INDEX);
			double[] pzChannel = dt.readBinaryData(vhdrFile, PZ_INDEX);
			List<ChannelInfo> channels = dt.getChannelInfo();
			Map<String, EEGMarker> markers = dt.readMarkers(vmrkFile);
			for (Map.Entry<String, EEGMarker> entry : markers.entrySet()) {
				if (!running)
					break;
				EEGMarker marker = entry.getValue();
				EpochMessenger em = new EpochMessenger();
				
				int stimulusIndex = Integer.parseInt(marker.getStimulus().replaceAll("[\\D]", "")) - 1;
                em.setStimulusIndex(stimulusIndex);
                float[] ffzChannel = toFloatArray(Arrays.copyOfRange(fzChannel, marker.getPosition() -  POCETHODNOTPREDEPOCHOU, marker.getPosition() + POCETHODNOTZAEPOCHOU  ));
                float[] fczChannel = toFloatArray(Arrays.copyOfRange(czChannel, marker.getPosition() -  POCETHODNOTPREDEPOCHOU, marker.getPosition() + POCETHODNOTZAEPOCHOU  ));
                float[] fpzChannel = toFloatArray(Arrays.copyOfRange(pzChannel, marker.getPosition() -  POCETHODNOTPREDEPOCHOU, marker.getPosition() + POCETHODNOTZAEPOCHOU  ));
				
                
                Baseline.correct(ffzChannel, POCETHODNOTPREDEPOCHOU);
        		Baseline.correct(fczChannel, POCETHODNOTPREDEPOCHOU);
        		Baseline.correct(fpzChannel, POCETHODNOTPREDEPOCHOU);
        		
        		em.setFZ(ffzChannel);
        		em.setCZ(fczChannel);
        		em.setPZ(fpzChannel);
                
                this.setChanged();
                this.notifyObservers(em);
			}

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		this.running = false;
		
	}

}
