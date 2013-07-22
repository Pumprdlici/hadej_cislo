package icp.algorithm.mp;

import java.util.*;

public class UserBase extends Observable implements Base, Iterable<UserAtomDefinition>
{
	private List<UserAtomDefinition> atoms;
	
	public UserBase()
	{
		this.atoms = new ArrayList<UserAtomDefinition>();
	}
	
	@Override
	public Atom getOptimalAtom(double[] signal)
	{
		double innerProduct;
		UserAtomDefinition currentAtom;
		double[] valuesOfAtom;
		double maxProduct = Double.MAX_VALUE;
		UsersAtom maxAtom = null;
		//double threshold;
		
		for (int indexOfAtom = 0; indexOfAtom < atoms.size(); indexOfAtom++)
		{
			currentAtom = atoms.get(indexOfAtom);
			
			for (int stretchOfAtom = currentAtom.getOriginalLength(); stretchOfAtom <= currentAtom.getOriginalLength() + currentAtom.getStretch(); stretchOfAtom++)
			{
				//System.out.println("min position:" + currentAtom.getMinPosition() + ", max position: " + currentAtom.getMaxPosition());
				for (int position = currentAtom.getMinPosition(); position <= signal.length - currentAtom.getValues(stretchOfAtom).length && position <= currentAtom.getMaxPosition(); position++)
				//for (int position = 0; position <= signal.length - currentAtom.getValues().length; position++)
				{
					
					for (double scale = currentAtom.getMinScale(); scale <= currentAtom.getMaxScale(); scale += 0.1)
					{
						innerProduct = 0;
						valuesOfAtom = new double[signal.length];
						
						/*for (int signalIndex = 0; signalIndex < signal.length; signalIndex++)
						{		
							if (signalIndex >= position && signalIndex < currentAtom.length + position)
							{
								valuesOfAtom[signalIndex] = currentAtom[signalIndex - position] * scale;
								innerProduct += Math.abs(signal[signalIndex] - valuesOfAtom[signalIndex]);
							}
							else
							{
								valuesOfAtom[signalIndex] = 0;
								innerProduct += Math.abs(signal[signalIndex]);
							}
						}*/
						//threshold = currentAtom.getValues(stretchOfAtom).length * 0.5;
						
						for (int signalIndex = position; signalIndex < currentAtom.getValues(stretchOfAtom).length + position; signalIndex++)
						{
							valuesOfAtom[signalIndex] = currentAtom.getValues(stretchOfAtom)[signalIndex - position] * scale;
							innerProduct += Math.abs(signal[signalIndex] - valuesOfAtom[signalIndex]);
							if (innerProduct > currentAtom.getThreshold())
							{
								innerProduct += ((signal.length - currentAtom.getValues(stretchOfAtom).length) * 2);
								break;
							}
						}
						
						if (innerProduct < maxProduct)
						{
							maxProduct = innerProduct;
							maxAtom = new UsersAtom();
							maxAtom.setName(currentAtom.getName());
							maxAtom.setDifference(innerProduct);
							maxAtom.setPosition(position);
							maxAtom.setValues(valuesOfAtom);
							maxAtom.setScale(scale);
							maxAtom.setStretch(stretchOfAtom);
						}
					}
				}
			}
		}
		/*if (maxAtom != null)
			System.out.println("MAX atom: " + Arrays.toString(maxAtom.getValues()));*/
		return (Atom) maxAtom;
	}
	
	public void addAtom(UserAtomDefinition atom)
	{
		atoms.add(atom);
		setChanged();
		notifyObservers(this);
	}
	
	public UserAtomDefinition getAtom(int index)
	{
		return atoms.get(index);
	}
	
	public void removeAtom(int index)
	{
		atoms.remove(index);
		setChanged();
		notifyObservers(this);
	}
	
	public void removeAllAtoms()
	{
		atoms = new ArrayList<UserAtomDefinition>();
		setChanged();
		notifyObservers(this);
	}
	
	public int size()
	{
		return atoms.size();
	}
	
	private class UserBaseIterator implements Iterator<UserAtomDefinition>
	{
		private UserBase userBase;
		
		private int index;
		
		
		public UserBaseIterator(UserBase userBase)
		{
			this.userBase = userBase;
			index = 0;
		}
		@Override
		public boolean hasNext()
		{
			
			return index < userBase.size();
		}

		@Override
		public UserAtomDefinition next()
		{
			return userBase.getAtom(index++);
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
		
	}
	
	@Override
	public Iterator<UserAtomDefinition> iterator()
	{
		return new UserBaseIterator(this);
	}
}
