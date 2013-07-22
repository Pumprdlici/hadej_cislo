package icp;

import icp.aplication.SessionManager;

/**
 * Hlavní spouštìcí tøída aplikace.
 */
public class Main {

	public static void main(String[] args) {
		//System.out.println("Hello");
		new SessionManager().startGui();
	}
}
