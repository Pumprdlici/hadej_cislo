package icp.algorithm.mp;

import java.util.Arrays;

public class UsersAtom extends Atom
{
	protected String name;
	
	protected double stretch;
	
	private double[] values;
	
	public double getStretch()
	{
		return stretch;
	}
	
	public void setStretch(double stretch)
	{
		this.stretch = stretch;
	}
	
	public void setValues(double[] values)
	{
		this.values = values;
	}
	
	public double[] getValues(int length)
	{
		double[] retval = Arrays.copyOf(values, length);
		
		return retval;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	@Override
	public double evaluate(double numberOfSample)
	{
		// TODO Auto-generated method stub
		return Double.MIN_VALUE;
	}

	@Override
	public void subtrackFrom(double[] signal)
	{
		for (int i = 0; i < signal.length; i++)
		{
			signal[i] -= values[i];
		}
	}
	
	@Override
	public String toString()
	{
		return name + " - position: " + position + ", scale: " + scale + ", stretch: " + stretch + ", difference: " + difference;
	}
}
