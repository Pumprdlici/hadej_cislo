package icp.application.classification;

import java.util.List;

import icp.algorithm.mp.*;

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
