package cz.zcu.kiv.rondik.mp.algorithm;

public abstract class Atom
{
	/**
	 * Mìøítko
	 */
	protected double scale;
	/**
	 * Posunutí
	 */
	protected double position;
	/**
	 * Koeficient podobnosti Gaborova atomu se vstupním signálem
	 */
	protected double difference;
	
	public abstract double[] getValues(int length);
	
	public abstract void subtrackFrom(double[] signal);

	public abstract double evaluate(double numberOfSample);
	
	public double getScale()
	{
		return scale;
	}
	
	public void setScale(double scale)
	{
		this.scale = scale;
	}
	public double getPosition()
	{
		return position;
	}
	
	public void setPosition(double position)
	{
		this.position = position;
	}
	
	public double getDifference()
	{
		return difference;
	}
	
	public void setDifference(double difference)
	{
		this.difference = difference;
	}
}
