package icp.online.app;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import cz.zcu.kiv.signal.DataTransformer;
import cz.zcu.kiv.signal.EEGDataTransformer;
import cz.zcu.kiv.signal.EEGMarker;

public class OffLineDataProvider extends Observable  implements IDataProvider{
	
	private File vhdrFile;
	private File vmrkFile;
	private final int FZ_INDEX = 17;
	private final int CZ_INDEX = 18;
	private final int PZ_INDEX = 19;
	
	public OffLineDataProvider(File vhdrFile, File markerFile) {
		this.vhdrFile = vhdrFile;
		this.vmrkFile = markerFile;
	}

	public OffLineDataProvider(File eegFile) {
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public void readEpochData(Observer obs) {
		
		
		
		DataTransformer dt = new EEGDataTransformer();
		
		try {
			double[] fzChannel = dt.readBinaryData(vhdrFile.getAbsolutePath(), FZ_INDEX);
			double[] czChannel = dt.readBinaryData(vhdrFile.getAbsolutePath(), CZ_INDEX);
			double[] pzChannel = dt.readBinaryData(vhdrFile.getAbsolutePath(), PZ_INDEX);
			Map<String, EEGMarker> markers = dt.readMarkers(vmrkFile.getAbsolutePath());
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// TODO Auto-generated method stub
		
	}

}
