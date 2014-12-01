package icp.application.classification;

import icp.algorithm.mp.Base;
import icp.algorithm.mp.FourierMP;

public class MPClassifier extends ERPClassifierAdapter {
	
	private FourierMP mp;
	
	public MPClassifier(Base ...bases)
	{
		mp = new FourierMP(bases);
	}
	
	public double classify(double[][] epoch) {
		
		
		return 0;
	}
}
