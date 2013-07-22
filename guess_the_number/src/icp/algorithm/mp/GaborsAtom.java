package icp.algorithm.mp;

/**
 * Tøída reprezentující jeden Gaborùv atom.
 * 
 * Tøída byla pøevzata z diplomové práce Ing. Jaroslava Svobody 
 * (<cite>Svoboda Jaroslav.  Metody zpracování evokovaných potenciálù,  Plzeò,  2008.  
 * Diplomová práce  práce  na Katedøe   informatiky a výpoèetní  
 * techniky Západoèeské univerzity v Plzni. Vedoucí diplomové práce Ing. Pavel 
 * Mautner, Ph.D.</cite>) a následnì upravena.
 *
 * @author Tomáš Øondík
 * @version 19. 11. 2008
 */
public class GaborsAtom extends Atom
{
	/**
	 * Frekvence
	 */
	private double frequency;
	/**
	 * Fázový posun
	 */
	private double phase;
	/**
	 * Modulus
	 */
	private double modulus;

	/**
	 * Vytváøí instanci Gaborova atomu.
	 * 
	 * @param scale mìøítko Gaborova atomu
	 * @param position posunutí Gaborova atomu
	 * @param frequency frekvence Gaborova atomu
	 * @param phase fázový posun Gaborova atomu
	 */
	public GaborsAtom(double scale, double position, double frequency, double phase)
	{
		this.scale = scale;
		this.position = position;
		this.frequency = frequency;
		this.phase = phase;
	}
	
	public double[] getValues(int length)
	{
		double sum = 0;
		
		// Výpoèet energie Gaborova atomu v bodech, ve kterých je urèený vstupní signál.
		for (int j = 0; j < length; j++)
		{
			sum += Math.pow(this.evaluate(j), 2);
		}
		
		sum = this.getModulus() / Math.sqrt(sum);
		
		double dec;
		double[] decArray = new double[length]; 
		// Odeètení Gaborova atomu od signálu a výpoèet energie takto vzniklého signálu.
		for (int j = 0; j < length; j++)
		{
			dec = this.evaluate(j) * sum;
			decArray[j] = dec;
		}
		return decArray;
	}
	
	@Override
	public void subtrackFrom(double[] signal)
	{
		double sum = 0;
		
		// Výpoèet energie Gaborova atomu v bodech, ve kterých je urèený vstupní signál.
		for (int j = 0; j < signal.length; j++)
		{
			sum += Math.pow(this.evaluate(j), 2);
		}
		
		sum = this.getModulus() / Math.sqrt(sum);
		
		double dec;
		
		// Odeètení Gaborova atomu od signálu a výpoèet energie takto vzniklého signálu.
		for (int j = 0; j < signal.length; j++)
		{
			dec = this.evaluate(j) * sum;
			signal[j] -= dec;
		}
	}
	
	public GaborsAtom(){};

	/**
	 * Metoda vyèísluje funkèní hodnotu Gaussova okénka v èase v závislosti na hodnotách atributù <code>scale</code>, 
	 * <code>position</code>, <code>frequency</code> a <code>phase</code>.
	 * 
	 * @param numberOfSample èas ve kterém bude hodnota vyèíslena
	 * @return hodnota Gaborova atomu v požadovaném bodì
	 */
	@Override
	public double evaluate(double numberOfSample)
	{
		return Utils.gaussianWindow((numberOfSample - position) / scale)
				* Math.cos(numberOfSample * frequency + phase);
	}
	/**
	 * Vrací hodnotu frekvence Gaborova atomu (atributu <code>frequency</code>).
	 * @return frekvence Gaborova atomu
	 */
	public double getFrequency()
	{
		return frequency;
	}
	/**
	 * Nastavuje hodnotu frekvence Gaborova atomu (atributu <code>frequency</code>).
	 * @param frequency frekvence Gaborova atomu
	 */
	public void setFrequency(double frequency)
	{
		this.frequency = frequency;
	}
	/**
	 * Vrací hodnotu fázového posunu Gaborova atomu (atributu <code>phase</code>).
	 * @return fázový posun Gaborova atomu
	 */
	public double getPhase()
	{
		return phase;
	}
	/**
	 * Nastavuje hodnotu fázového posunu Gaborova atomu (atributu <code>phase</code>).
	 * @param phase fázový posun Gaborova atomu
	 */
	public void setPhase(double phase)
	{
		this.phase = phase;
	}
	/**
	 * @return the modulus
	 */
	public double getModulus()
	{
		return modulus;
	}

	/**
	 * @param modulus the modulus to set
	 */
	public void setModulus(double modulus)
	{
		this.modulus = modulus;
	}
	/**
	 * Vrací textovou reprezentaci Gaborova atomu. Pøekrývá metodu ze tøídy <code>Object</code>.
	 * @return textová reprezentace Gaborova atomu
	 */
	@Override
	public String toString()
	{
		return "Gabors atom - position: " + position 
		+ ", scale: " + scale 
		+ ",  frequency: "  + frequency
		+ ", modulus: " + modulus
		+ ", phase: "
		+ phase;
	}
}
