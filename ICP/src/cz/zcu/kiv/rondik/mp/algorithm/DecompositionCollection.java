package cz.zcu.kiv.rondik.mp.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class DecompositionCollection implements Iterable<Atom>
{
	private List<Atom> atoms;
	
	private double[] signal;
	
	public DecompositionCollection(double[] signal)
	{
		atoms = new ArrayList<Atom>();
		this.signal = signal;
	}
	
	public double[] getSignalHistory(int index)
	{
		double[] retval = Arrays.copyOf(signal, signal.length);
		double[] current = null;
		
		for (int i = 0; i < index && index <= atoms.size(); i++)
		{
			current = atoms.get(i).getValues(retval.length);
			for (int j = 0; j < retval.length; j++)
			{
				retval[j] -= current[j];
			}
		}
		
		return retval;
	}
	
	public double[] getReconstruction(int level)
	{
		double[] retval = new double[signal.length];
		Arrays.fill(retval, 0);
		double[] current;
		for (int j = 0; j < level && j < atoms.size(); j++)
		{
			current = atoms.get(j).getValues(retval.length);
			for (int i = 0; i < retval.length; i++)
			{
				retval[i] += current[i];
			}
		}
		
		return retval;
	}
	
	public double[] getReconstructionDifference(int level)
	{
		double[] reconstruction = getReconstruction(level);
		double[] difference = new double[reconstruction.length];
		Arrays.fill(difference, 0);
		
		for (int i = 0; i < difference.length; i++)
		{
			difference[i] = signal[i] - reconstruction[i];
		}
		
		return difference;
	}
	
	public double[] getAtomHistory(int index)
	{
		return atoms.get(index).getValues(signal.length);
	}
	
	public int size()
	{
		return atoms.size();
	}
	
	public void addAtom(Atom atom)
	{
		atoms.add(atom);
	}
	
	public Atom getAtom(int index)
	{
		return atoms.get(index);
	}
	
	@Override
	public String toString()
	{
		String retval = "";
		
		for (Atom a: atoms)
		{
			retval += a.toString() + "\n";
		}
		
		return retval;
	}
	
	private class DecompositionCollectionIterator implements Iterator<Atom>
	{
		private DecompositionCollection dc;
		
		private int index;
		
		public DecompositionCollectionIterator(DecompositionCollection dc)
		{
			this.dc = dc;
			index = 0;
		}
		@Override
		public boolean hasNext()
		{
			return index < dc.size();
		}

		@Override
		public Atom next()
		{
			return dc.getAtom(index++);
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
		
	}
	
	@Override
	public Iterator<Atom> iterator()
	{
		return new DecompositionCollectionIterator(this);
	}
}
