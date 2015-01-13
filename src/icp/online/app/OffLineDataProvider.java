package icp.online.app;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import cz.zcu.kiv.signal.DataTransformer;
import cz.zcu.kiv.signal.EEGDataTransformer;
import cz.zcu.kiv.signal.EEGMarker;

public class OffLineDataProvider extends Observable  implements IDataProvider{
	
	private String vhdrFile;
	private String vmrkFile;
	private final int FZ_INDEX = 17;
	private final int CZ_INDEX = 18;
	private final int PZ_INDEX = 19;
	
	private static final int POCETHODNOTPREDEPOCHOU = 100;
	private static final int POCETHODNOTZAEPOCHOU = 512;
	
	public OffLineDataProvider(String vhdrFile, String markerFile) {
		this.vhdrFile = vhdrFile;
		this.vmrkFile = markerFile;
	}

	public OffLineDataProvider(File eegFile) {
		int index = eegFile.getAbsolutePath().lastIndexOf(".");
		
		String ext = eegFile.getAbsolutePath().substring(index);
		String baseName = eegFile.getAbsolutePath().substring(0, index);
		
		this.vhdrFile = baseName + ".vhdr";
		this.vmrkFile = baseName + ".vmrk";
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
	public void readEpochData(Observer obs) {
		addObserver(obs);
		
		
		DataTransformer dt = new EEGDataTransformer();
		
		try {
			double[] fzChannel = dt.readBinaryData(vhdrFile, FZ_INDEX);
			double[] czChannel = dt.readBinaryData(vhdrFile, CZ_INDEX);
			double[] pzChannel = dt.readBinaryData(vhdrFile, PZ_INDEX);
			Map<String, EEGMarker> markers = dt.readMarkers(vmrkFile);
						


			for (Map.Entry<String, EEGMarker> entry : markers.entrySet()) {
				EEGMarker marker = entry.getValue();
				EpochMessenger em = new EpochMessenger();


				int stimulusIndex = Integer.parseInt(marker.getName().replaceAll("[\\D]", "")) - 1;
                em.setStimulusIndex(stimulusIndex);
                
                em.setFZ(toFloatArray(Arrays.copyOfRange(fzChannel, marker.getPosition() -  POCETHODNOTPREDEPOCHOU, marker.getPosition() + POCETHODNOTZAEPOCHOU  )));
                em.setCZ(toFloatArray(Arrays.copyOfRange(czChannel, marker.getPosition() -  POCETHODNOTPREDEPOCHOU, marker.getPosition() + POCETHODNOTZAEPOCHOU  )));
                em.setPZ(toFloatArray(Arrays.copyOfRange(pzChannel, marker.getPosition() -  POCETHODNOTPREDEPOCHOU, marker.getPosition() + POCETHODNOTZAEPOCHOU  )));
				
                this.setChanged();
                this.notifyObservers(em);
			}

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// TODO Auto-generated method stub
		
	}

}
