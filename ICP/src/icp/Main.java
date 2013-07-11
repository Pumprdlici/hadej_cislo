package icp;

import icp.application.SessionManager;

/**
 * Hlavní spouštìcí tøída aplikace.
 * @author Jiøí Kuèera
 */
public class Main {

	public static void main(String[] args) {
		new SessionManager().startGui();
	}
}
