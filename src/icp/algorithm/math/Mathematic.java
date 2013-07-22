package icp.algorithm.math;

/**
 * Tøída pro matematické operace.
 * 
 * @author Petr Soukal
 */
public class Mathematic
{
	//konstanty
	public final static int CONST_2 = 2;
	public final static int ZERO = 0;
	
	/**
	 * Metoda vypoèítává logaritmus o základu 2 z vloeného èísla.
	 * 
	 * @param x - èíslo ze kterého se poèítá logaritmus o základu 2.
	 * @return log2 z x.
	 */
	public static double log2(int x){
		double log2 = Math.log(x)/Math.log(CONST_2);
		
		return log2;
	}
	
	/**
	 * Pokud neni vloené èíslo mocninou základu 2, tak vrátí první vìtší èíslo,
	 * které je mocninou základu 2.
	 * 
	 * @param x - èíslo, u kterého se zjišuje zda je základu 2.
	 * @return èíslo x nebo první vìtší èíslo, které je mocninou základu 2.
	 */
	public static int newMajorNumberOfPowerBase2(int x){
		double number = log2(x);
		int temp = (int)number;
		
		if(number%temp == 0)		
			return x;
		else
		{
			temp += 1;
			int newNumber = (int) Math.pow(CONST_2, temp);
			return newNumber;
		}
	}
	
	/**
	 * Pokud neni vloené èíslo mocninou základu 2, tak vrátí první menší èíslo,
	 * které je mocninou základu 2.
	 * 
	 * @param x - èíslo, u kterého se zjišuje zda je základu 2.
	 * @return èíslo x nebo první menší èíslo, které je mocninou základu 2.
	 */
	public static int newMinorNumberOfPowerBase2(int x){
		double number = (int)log2(x);
		
		return (int) Math.pow(CONST_2, number);
	}
}
