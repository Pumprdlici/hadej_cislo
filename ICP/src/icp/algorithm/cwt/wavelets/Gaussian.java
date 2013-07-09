package icp.algorithm.cwt.wavelets;

/**
 * Tøída waveletu Gaussian
 */
public class Gaussian extends WaveletCWT
{
	//název waveletu
	private final static String NAME = "Gaussian";
	//hlavní délka waveletu
	private final static int MAIN_LENGTH = 10;
	
	/**
	 * Konstruktor waveletu
	 */
	public Gaussian()
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
		
		return (1.0/Math.sqrt(a))*(-t)*Math.exp(-tPow2/2);
	}
	
	/**
	 * Imaginární èást mateøského waveletu.
	 */
	@Override
	public double imCoef(double t, double a)
	{
		return 0;
	}
}
