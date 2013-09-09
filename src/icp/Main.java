package icp;

import icp.online.app.OnLineDataProvider;

/**
 * Hlavn� spou�t�c� t��da aplikace.
 * 
 */
public class Main {

	public static void main(String[] args) {
		//new SessionManager().startGui();
		OnLineDataProvider odp = new OnLineDataProvider(args[0], Integer.valueOf(args[1]).intValue());
	}
}
