package icp.algorithm.cwt.wavelets;

/**
 * Tøída waveletu MexicanHat
 */
public class MexicanHat extends WaveletCWT
{
	//název waveletu
	private final static String NAME = "Mexican_Hat";
	//hlavní délka waveletu
	private final static int MAIN_LENGTH = 16;
	
	/**
	 * Konstruktor waveletu
	 */
	public MexicanHat()
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
		
		return (1.0/Math.sqrt(a))* ((1-tPow2)*Math.exp(-tPow2/2)); 
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
