package icp;

import icp.aplication.SessionManager;

/**
 * Hlavní spouštìcí tøída aplikace.
 */
public class Main {

	public static void main(String[] args) {
		new SessionManager().startGui();
	}
}
