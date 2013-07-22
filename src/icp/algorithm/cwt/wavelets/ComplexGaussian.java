package icp.algorithm.cwt.wavelets;

/**
 * Tøída waveletu ComplexGaussian
 */
public class ComplexGaussian extends WaveletCWT
{
	//název waveletu
	private final static String NAME = "Complex_Gaussian";
	//hlavní délka waveletu
	private final static int MAIN_LENGTH = 16;
	
	/**
	 * Konstruktor waveletu
	 */
	public ComplexGaussian()
	{
		super(NAME, MAIN_LENGTH);
	}
	
	/**
	 * Reálná èást mateøského waveletu.
	 */
	@Override
	public double reCoef(double t, double a)
	{
		double tPow2 = t*t;
		
		return (1.0/Math.sqrt(a))*Math.exp(-tPow2)*(-Math.sin(t)-(2.0 * t * Math.cos(t))); 
	}
	
	/**
	 * Imaginární èást mateøského waveletu.
	 */
	@Override
	public double imCoef(double t, double a)
	{
		double tPow2 = t*t;
		
		return (1.0/Math.sqrt(a))*Math.exp(-tPow2)*(-Math.cos(t)+(2.0 * t * Math.sin(t))); 
	}	
}
