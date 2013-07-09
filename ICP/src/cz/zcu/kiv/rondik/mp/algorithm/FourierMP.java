package cz.zcu.kiv.rondik.mp.algorithm;

import java.util.Arrays;

/**
 * 
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
public class FourierMP 
{	
	private Base[] bases;
	
	public FourierMP(Base ... bases)
	{
		this.bases = bases;
	}

	/**
	 * Slouží k provedení algoritmu Matching Pursuit. Algoritmus používá k aproximaci 
	 * vstupního signálu slovník Gaborových funkcí. K urychlení výpoètu byla použita 
	 * rychlá Fourierova transformace, jejíž dùsledkem je požadavek na délku vstupního 
	 * signálu (viz <b>Parameters</b>). 
	 * @param signal Vstupní signál délky <code>2^n</code>, kde <code>n</code> je 
	 * pøirozené èíslo. Nárok na délku vstupního signálu je dán použitím rychlé Fourierovy 
	 * transformace v algoritmu Matching Pursuit. 
	 * @param iterations Poèet iterací algoritmu Matching Pursuit.
	 * @return Atomy, které jsou výsledkem algoritmu Matching Pursuit.
	 * @throws IllegalArgumentException Pokud je zadán signál délky, která není mocninou èísla dvì
	 * nebo pokud je poèet iterací menší nebo roven nule.
	 */
	public DecompositionCollection doMP(double[] signal, int iterations) throws IllegalArgumentException
	{
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
		
		if (iterations <= 0 || (Math.abs(nDouble - nLong)) > ALLOWANCE)
			throw new IllegalArgumentException();
		//END- kontrola vlastností parametrù metody
		
		DecompositionCollection collection = new DecompositionCollection(Arrays.copyOf(signal, signal.length));
		//double energy1 = 0, energy2 = 0;
		
		
//		// Výpoèet energie vstupního signálu.
//		for (int i = 0; i < signal.length; i++)
//		{
//			energy1 += Math.pow(signal[i], 2);
//		}
		
		Atom currentAtom = null;
		Atom bestAtom;
		
		for (int i = 0; i < iterations; i++)
		{
			bestAtom = null;
			System.out.println(i + "^th iteration");
			
			for (Base b: bases)
			{
				currentAtom = b.getOptimalAtom(signal);
				
				if (bestAtom == null || bestAtom.getDifference() > currentAtom.getDifference())
				{
					bestAtom = currentAtom;
				}
			}
			
			collection.addAtom(bestAtom);
			bestAtom.subtrackFrom(signal);
			
//			energy2 = 0;
//			for (int j = 0; j < signal.length; j++)
//			{
//				energy2 += Math.pow(signal[j], 2);
//			}
			/*if (energy2 > energy1)
				return collection;*/
		}
		return collection;
	}
}
