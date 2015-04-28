package artifactDetection;

import icp.online.app.EpochMessenger;

public class TestovaciData {
	
	static double[][] pole1 = {{0,0,0,0,61,0,0,0},{0,0,0,0,61,0,0,0},{0,0,0,0,61,0,0,0}};
	static double[][] pole2 = {{3,5,60,21,57,39,47,22},{8,15,6,31,27,49,37,28},{8,15,6,31,27,49,37,28}};
	static double[][] pole3 = {{1,2,3,4,5,6,7,8},{1,2,3,4,5,6,7,8},{1,2,3,4,5,6,7,8}};
	
	static double[] vzor1 = {1,2,3,4,5};
	static double[] vzor2 = {10,2,30,4};
	
	static public EpochMessenger eSpatna = new EpochMessenger(pole1, -1);
	static public EpochMessenger eSpravna = new EpochMessenger(pole2, -1);
	static public EpochMessenger eCor = new EpochMessenger(pole3, -1);
	
}
