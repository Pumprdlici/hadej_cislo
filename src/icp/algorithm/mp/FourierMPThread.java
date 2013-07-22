package icp.algorithm.mp;

import icp.aplication.SessionManager;

import java.util.Arrays;

public class FourierMPThread extends Thread
{
	private SessionManager sm;
	
	private Base[] bases;
	
	private double[] signal;
	
	private int numberOfIterations;
	
	private DecompositionCollection dc;
	
	private double unit;
	
	public FourierMPThread(SessionManager sm, Base ... bases)
	{
		this.sm = sm;
		dc = null;
		this.bases = bases;
	}
	
	public void init(double[] signal, int numberOfIterations)
	{
		this.signal = signal;
		this.numberOfIterations = numberOfIterations;
		//BEGIN - kontrola vlastností parametrù metody
		/*
		 * Kontroluje se, že délka vstupního signálu (tj. pole "signal") je rovna nìkteré 
		 * mocninì dvou. Dále se kontroluje, jestli je poèet iterací kladné èíslo.
		 * 
		 * Do promìnné "nDouble" se uloží výsledek logaritmu z èísla "signal.length" o základu dva jako
		 * desetinné èíslo.
		 */
		double nDouble = (Math.log(signal.length)) / Math.log(2);
		/*
		 * Do promìnné "nLong" se uloží výsledek logaritmu z èísla "signal.length" o základu dva jako
		 * celé èíslo.
		 */
		long nLong = Math.round((Math.log(signal.length)) / Math.log(2));
		/*
		 * Délka vstupního signálu je mocninou dvou právì tehdy, když "nDoulbe" je rovno nule. 
		 * Aby se pøedešlo porovnání doubleového èísla s nulou, definuje se tolerance "ALLOWANCE",
		 * která je jistì dost malá.
		 */
		final double ALLOWANCE = 1e-32;
		
		if (numberOfIterations <= 0 || (Math.abs(nDouble - nLong)) > ALLOWANCE)
			throw new IllegalArgumentException();
		//END- kontrola vlastností parametrù metody
		
		dc = new DecompositionCollection(Arrays.copyOf(signal, signal.length));
		unit = 100D / ((double) (numberOfIterations * bases.length));
	}
	
	@Override
	public void run()
	{
		Atom currentAtom = null;
		Atom bestAtom;
		
		for (int i = 0; i < numberOfIterations; i++)
		{
			bestAtom = null;
			
			for (Base b: bases)
			{
				currentAtom = b.getOptimalAtom(signal);
				sm.sendProgressUnits(unit);
				if (bestAtom == null || bestAtom.getDifference() > currentAtom.getDifference())
				{
					bestAtom = currentAtom;
				}
			}
			
			dc.addAtom(bestAtom);
			bestAtom.subtrackFrom(signal);
		}
	}
	
	public DecompositionCollection getDC()
	{
		return dc;
	}
}
