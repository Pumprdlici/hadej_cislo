package icp.algorithm.mp;

/**
 * Instance tøídy slouí k reprezentaci komplexních èísel a umoòují provádìt 
 * s nimi základní operace. 
 * 
 * Tøída byla pøevzata z diplomové práce Ing. Jaroslava Svobody 
 * (<cite>Svoboda Jaroslav.  Metody zpracování evokovanıch potenciálù,  Plzeò,  2008.  
 * Diplomová práce  práce  na Katedøe   informatiky a vıpoèetní  
 * techniky Západoèeské univerzity v Plzni. Vedoucí diplomové práce Ing. Pavel 
 * Mautner, Ph.D.</cite>) a následnì upravena.
 *
 * @author Tomáš Øondík
 * @version 17. 11. 2008 
 */
public class Complex
{
	/**
	 * Reálná èást komplexního èísla.
	 */
	private double re;
	/**
	 * Imaginární èást komplexního èísla.
	 */
	private double im;
	/**
	 * Povolenı rozdíl od nulové hodnoty. Hodnoty (<code>0 +- ZERO_ALLOWANCE</code>) jsou povaovány za rovné nule.
	 */
	public static final double ZERO_ALLOWANCE = 1.0e-7F;
	
	/**
	 * Vytvoøení instance reprezentující komplexní èíslo.
	 * 
	 * @param re reálná èást komplexního èísla
	 * @param im imaginární èást komplexního èísla
	 */
	public Complex(double re, double im)
	{
		this.re = re;
		this.im = im;
	}
	
	/**
	 * Nastavuje reálnou èást komplexního èísla na hodnotu reálné èásti komplexního èísla pøedaného jako parametr 
	 * metody. Nastavuje imaginární èást komplexního èísla na hodnotu imaginární èásti komplexního èísla pøedaného 
	 * jako parametr metody.
	 * 
	 * @param complex komplexní èíslo podle nìj se nastavují hodnoty reálné a imaginární èásti
	 * @return instance tøídy <code>Complex</code> s pøíslušnì nastavenou reálnou i imaginární èástí
	 */
	public Complex set(Complex complex)
	{
		this.re = complex.re;
		this.im = complex.im;
		return this;
	}

	/**
	 * Zjišuje, jestli je komplexní èíslo nulové, tj. jestli jsou jeho reálná a imaginární èást rovny nule. Od 
	 * èástí èísla pøitom není poadováno, aby byly pøesnì rovny nule - staèí, aby byly v absolutní hodnotì ostøe 
	 * menší ne konstanta <code>ZERO_ALLOWANCE</code>.
	 * 
	 * @return <b>true</b>, pokud je komplexní èíslo nulové, jinak <b>false</b>.
	 */
	public boolean isZero()
	{
		return (Math.abs(this.re) < ZERO_ALLOWANCE && Math.abs(this.im) < ZERO_ALLOWANCE);
	}

	/**
	 * Seète komplexní èíslo s komplexním èíslem pøedanım jako parametr. Reálnou èást èísla navıší o reálnou èást 
	 * pøedaného èísla, imaginární èást èísla navıší o imaginární èást pøedaného èísla. Vrací komplexní èíslo, které je souètem 
	 * obou komplexních èísel.
	 * 
	 * @param complex komplexní èíslo jeho hodnoty budou pøièítány
	 * @return komplexní èíslo, které je vısledkem souètu
	 */
	public Complex addTo(Complex complex)
	{
		this.re += complex.re;
		this.im += complex.im;
		return this;
	}

	/**
	 * Vynásobí komplexní èíslo s komplexním èíslem pøedanım jako parametr. Vrací komplexní èíslo, které je souèinem 
	 * obou komplexních èísel.
	 * 
	 * @param complex komplexní èíslo, které bude pouito jako souèinitel
	 * @return komplexní èíslo, které je vısledkem souèinu
	 */
	public Complex timesBy(Complex complex)
	{
		double tmp = this.re * complex.re - this.im * complex.im;
		this.im = this.im * complex.re + this.re * complex.im;
		this.re = (double) tmp;
		return this;
	}

	/**
	 * Negace reálné a imaginární èásti komplexního èísla.
	 * @return negovaná instance komplexního èísla
	 */
	public Complex negate()
	{
		this.re = -this.re;
		this.im = -this.im;
		return this;
	}

	/**
	 * Vydìlení reálné a imaginární èásti komplexního èísla hodnotou pøedanou jako parametr metody.
	 * @param n dìlitel
	 * @return vydìlená instance komplexního èísla
	 */
	public Complex div(int n)
	{
		this.re /= n;
		this.im /= n;
		return this;
	}

	/**
	 * Metoda vrací velikost komplexního èísla.
	 * @return velikost komplexního èísla
	 */
	public double size()
	{
		return (double) Math.sqrt(this.re * this.re + this.im * this.im);
	}

	/**
	 * Vrací reálnou èást komplexního èísla.
	 * @return reálná èást
	 */
	public double getRe()
	{
		return re;
	}

	/**
	 * Vrací imaginární èást komplexního èísla.
	 * @return imaginární èást
	 */
	public double getIm()
	{
		return im;
	}
	
	/**
	 * Metoda vytváøí kopii instance. Pøekrıvá metodu ze tøídy <code>Object</code>.
	 * @return kopie instance
	 */
	@Override
	public Complex clone()
	{
		return new Complex(re, im);
	}
	
	/**
	 * Vrací textovou reprezentaci komplexního èísla. Pøekrıvá metodu ze tøídy <code>Object</code>.
	 * @return textová reprezentace komplexního èísla
	 */
	@Override
	public String toString()
	{
		return re + " " + im + "i";
	}
}
