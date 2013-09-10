package icp.online.app;

class Baseline {
	
	static void correct(float[] epoch, int prefix){
		
		float baseline = 0;
		
		for (int i = 0; i < prefix; i++)
			baseline += epoch[i];
		
		baseline /= prefix;
		
		for (int i = 0; i < epoch.length; i++)
			epoch[i] -= baseline;
	}
	
	private Baseline(){}
}
