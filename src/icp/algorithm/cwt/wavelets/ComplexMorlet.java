package icp.algorithm.cwt.wavelets;

/**
 * Tøída waveletu ComplexMorlet
 */
public class ComplexMorlet extends WaveletCWT
{
	//název waveletu
	private final static String NAME = "Complex_Morlet";
	//hlavní délka waveletu
	private final static int MAIN_LENGTH = 16;
	//konstanta parametru pásmové šíøky
	private double FB;
	//konstanta støední frekvence waveletu
	private double FC;
	//konstanta pro normování
	private final double NORM;
	
	/**
	 * Konstruktor waveletu
	 */
	public ComplexMorlet(double fb, double fc)
	{
		super(NAME, MAIN_LENGTH);
		this.FB = fb;
		this.FC = fc;
		this.NORM = 1.0 / Math.sqrt(Math.PI * fb);	
	}
	
	/**
	 * Reálná èást mateøského waveletu.
	 */
	@Override
	public double reCoef(double t, double a)
	{
		double tPow2 = t*t;
		
		return NORM * Math.cos(2.0 * Math.PI * FC * t) * Math.exp(-tPow2/FB); 
	}
	
	/**
	 * Imaginární èást mateøského waveletu.
	 */
	@Override
	public double imCoef(double t, double a)
	{
		double tPow2 = t*t;
		
		return NORM * Math.sin(2.0 * Math.PI * FC * t) * Math.exp(-tPow2/FB); 
	}
	
	/**
	 * Nastavuje konstantu FB.
	 */
	public void setFB(double fb)
	{
		this.FB = fb;
	}
	
	/**
	 * Nastavuje konstantu FC.
	 */
	public void setFC(double fc)
	{
		this.FC = fc;
	}
}
