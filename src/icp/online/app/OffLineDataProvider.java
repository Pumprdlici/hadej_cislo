package icp.online.app;

import java.io.IOException;
import java.util.HashMap;
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
	
	public OffLineDataProvider(String vhdrFile, String markerFile) {
		this.vhdrFile = vhdrFile;
		this.vmrkFile = markerFile;
	}

	@Override
	public void readEpochData(Observer obs) {
		
		
		
		DataTransformer dt = new EEGDataTransformer();
		
		try {
			double[] fzChannel = dt.readBinaryData(vhdrFile, FZ_INDEX);
			double[] czChannel = dt.readBinaryData(vhdrFile, CZ_INDEX);
			double[] pzChannel = dt.readBinaryData(vhdrFile, PZ_INDEX);
			Map<String, EEGMarker> markers = dt.readMarkers(vmrkFile);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// TODO Auto-generated method stub
		
	}

}
