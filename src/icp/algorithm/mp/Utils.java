package icp.algorithm.mp;

/**
 * Tøída obsahuje funkce používané pro výpoèet metody Matching pursuit.
 * 
 * Tøída byla pøevzata z diplomové práce Ing. Jaroslava Svobody 
 * (<cite>Svoboda Jaroslav.  Metody zpracování evokovaných potenciálù,  Plzeò,  2008.  
 * Diplomová práce  práce  na Katedøe   informatiky a výpoèetní  
 * techniky Západoèeské univerzity v Plzni. Vedoucí diplomové práce Ing. Pavel 
 * Mautner, Ph.D.</cite>) a následnì upravena.
 * 
 * @author Tomáš Øondík
 * @version 17. 11. 2008
 * 
 */
public class Utils
{
	/**
	 * TODO - komentáø
	 */
	private static Complex[] wtbl = null;
	/**
	 * TODO - komentáø
	 */
	private static int last_n = 0;
	
	
	/**
	 * Method generates complex values for synthese of FFT algorithm.
	 * 
	 * @param n
	 *            nuber of input vector points
	 * @return array of generated complex values
	 */
	private static Complex[] make_wtbl(int n)
	{
		if (last_n == n)
			return wtbl;
		
		int n2 = n >> 1;
		wtbl = new Complex[n2 + 1];
		wtbl[0] = new Complex(1, 0);
		
		double t = 0;
		for (int i = 1; i < n2; i++)
		{
			t = 2 * Math.PI * i / n;
			wtbl[i] = new Complex(Math.cos(t), Math.sin(t));
		}
		
		wtbl[n2] = new Complex(-1, 0);
		last_n = n;
		
		return wtbl;
	}
	/***
	 * Vrací funkèní hodnotu Gaussova okénka (poèítáno podle vztahu: <i>e^(-pi * t^2))</i>v èase daném parametrem <code>t</code>.
	 * @param t èas, pro který je spoètena funkèní hodnota Gaussova okénka
	 * @return funkèní hodnota Gaussova okénka v èase <code>t</code>
	 */
	public static double gaussianWindow(double t)
	{
		return Math.exp(-Math.PI * Math.pow(t, 2));
	}
	/**
	 * Vypoètení skalárního souèinu dvou signálù pøedaných jako parametry metody.
	 * 
	 * @param signal1 diskrétní signál
	 * @param signal2 diskrétní signál
	 * @return skalární souèin signálù <code>signal1</code> a <code>signal2</code>
	 */
	public static double inerProduct(double[] signal1, double[] signal2)
	{
		int output = 0;
		if (signal1.length == signal2.length)
		{
			for (int i = 0; i < signal1.length; i++)
				output += signal1[i] * signal2[i];
		}
		return output;
	}
	/**
	 * Method mekes Fourier transform (or inverse transform) of input data using
	 * FFT algorithm
	 * 
	 * @param data
	 *            input array of values
	 * @param inverse
	 *            true if we want tu run inverse fourier transform on data
	 * @return transformed data
	 */
	public static Complex[] doFFT(double[] data, boolean inverse)
	{
		Complex[] f = new Complex[data.length];
		
		for (int i = 0; i < data.length; i++)
		{
			f[i] = new Complex(data[i], 0);
		}
		// preparation
		int n = f.length;
		int n2 = n >> 1;
		Complex[] wtbl = make_wtbl(n);
		
		// Bit inversion
		for (int i = 0, j = 0, k;;) // for-cyklus použitý kvùli omezení viditelnosti promìnných i,j,k
		{
			if (i < j)
			{
				Complex t = f[i];
				f[i] = f[j];
				f[j] = t;
			}
			if (++i >= n)
				break;
			
			for (k = n2; k <= j; k >>= 1)
			{
				j -= k;
			}
			j += k;
		}
		Complex tm = f[0].clone();
		
		// Synthese of frequency spectra
		int k = 1;
		while (k < n)
		{
			int h = 0;
			int k2 = k << 1;
			int d = n / k2;
			
			for (int j = 0; j < k; j++)
			{
				if (inverse)
				{
					for (int i = j; i < n; i += k2)
					{
						int ik = i + k;
						f[ik].timesBy(wtbl[h]);
						tm.set(f[ik]).addTo(f[i]);
						f[ik].negate().addTo(f[i]);
						f[i].set(tm);
					}
				}
				else
				{
					for (int i = j; i < n; i += k2)
					{
						int ik = i + k;
						f[ik].timesBy(wtbl[n2 - h]);
						tm.set(f[ik]).negate().addTo(f[i]);
						f[ik].addTo(f[i]);
						f[i].set(tm);
					}
				}
				h += d;
			}
			k = k2;
		}
		return f;
	}
	/**
	 * Privátní konstruktor. Neexistuje racionální dùvod k vytváøení instancí této tøídy.
	 */
	private Utils(){}
}
