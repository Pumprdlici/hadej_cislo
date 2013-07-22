package icp.algorithm.cwt.wavelets;

/**
 * Abstraktní tøída waveletu pro spojitou waveletovou transformaci.
 */
public abstract class WaveletCWT
{
	//název waveletu
	private String name;
	//hlavní délka waveletu
	private int mainLength;

    /**
     *  Konstruktor WaveletCWT
     *
     *  @param name - název waveletu.
    */
    public WaveletCWT(String name, int mainLength)
    {
    	this.name = name;
    	this.mainLength = mainLength;
    }

    /**
	 * Reálná èást mateøského waveletu.
	 */
    public abstract double reCoef(double t, double a);
    
    /**
	 * Imaginární èást mateøského waveletu.
	 */
    public abstract double imCoef(double t, double a);
    
    /**
     * @return název waveletu.
    */
    public String getName()
    {
        return name;
    }
    
    /**
     * @return hlavní délka waveletu.
    */
    public int getMainLength()
    {
        return mainLength;
    }
}
