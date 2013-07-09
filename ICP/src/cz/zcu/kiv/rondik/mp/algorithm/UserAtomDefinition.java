package cz.zcu.kiv.rondik.mp.algorithm;


public class UserAtomDefinition
{
	private double[] values;
	
	private String name;
	
	private double minScale;
	
	private double maxScale;
	
	private int minPosition;
	
	private int maxPosition;
	
	private double treshold;
	
	private int stretch;
	
	public UserAtomDefinition(double[] values, String name, int minPosition, int maxPosition, double minScale, double maxScale,  double threshold, int stretch)
	{
		init(values, name, minPosition, maxPosition, minScale, maxScale, threshold, stretch);
	}
	
	public UserAtomDefinition(double[] values, String name, int minPosition, int maxPosition, double scale,  double threshold, int stretch)
	{
		init(values, name, minPosition, maxPosition, scale, scale, threshold, stretch);
	}
	
	private void init(double[] values, String name, int minPosition, int maxPosition, double minScale, double maxScale,  double threshold, int stretch)
	{
		this.values = values;
		this.name = name;
		this.minPosition = minPosition;
		this.maxPosition = maxPosition;
		this.minScale = minScale;
		this.maxScale = maxScale;
		this.treshold = threshold;
		this.stretch = stretch;
	}
	
	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	public int getOriginalLength()
	{
		return values.length;
	}
	/**
	 * @return the values
	 */
	public double[] getValues(int length)
	{
		double[] array = new double[length];
		double step = ((double) (values.length - 1)) / (length - 1);
		double stepSum = step;
		int valuesIndex = 0;
		int arrayIndex = 0;
		double difference;
		array[arrayIndex++] = values[valuesIndex];
		double leftValue;
		double rightValue;
		while (arrayIndex < length)
		{
			leftValue = values[valuesIndex];
			rightValue = values[valuesIndex + 1];
			/*System.out.println("length: " + length + ", arrayIndex: " + arrayIndex 
					+ ", valuesIndex: " + valuesIndex + ", stepSum: " + stepSum 
					);*/
			difference = ((double) (stepSum - valuesIndex)) * Math.abs(leftValue - rightValue);
			//System.out.println("difference: " + difference);
			if (leftValue < rightValue)
			{
				array[arrayIndex] = leftValue + difference;
			}
			else if (leftValue > rightValue)
			{
				array[arrayIndex] = leftValue - difference;
			}
			else
			{
				array[arrayIndex] = leftValue;
			}
			
			stepSum += step;
			
			if (stepSum > valuesIndex + 1 && valuesIndex + 2 < values.length)
			{
				//System.out.println("valuesIndex: " + valuesIndex + ", values.length: " + values.length);
				valuesIndex++;
			}
			
			arrayIndex++;
		}
		//System.out.println(Arrays.toString(array));
		return array;
	}

	/**
	 * @param values the values to set
	 */
	public void setValues(double[] values)
	{
		this.values = values;
	}

	/**
	 * @return the minScale
	 */
	public double getMinScale()
	{
		return minScale;
	}

	/**
	 * @param minScale the minScale to set
	 */
	public void setMinScale(double minScale)
	{
		this.minScale = minScale;
	}

	/**
	 * @return the maxScale
	 */
	public double getMaxScale()
	{
		return maxScale;
	}

	/**
	 * @param maxScale the maxScale to set
	 */
	public void setMaxScale(double maxScale)
	{
		this.maxScale = maxScale;
	}

	/**
	 * @return the minPosition
	 */
	public int getMinPosition()
	{
		return minPosition;
	}

	/**
	 * @param minPosition the minPosition to set
	 */
	public void setMinPosition(int minPosition)
	{
		this.minPosition = minPosition;
	}

	/**
	 * @return the maxPosition
	 */
	public int getMaxPosition()
	{
		return maxPosition;
	}

	/**
	 * @param maxPosition the maxPosition to set
	 */
	public void setMaxPosition(int maxPosition)
	{
		this.maxPosition = maxPosition;
	}

	/**
	 * @return the minStretch
	 */
	public double getThreshold()
	{
		return treshold;
	}

	/**
	 * @param trheshold the minStretch to set
	 */
	public void setThreshold(double trheshold)
	{
		this.treshold = trheshold;
	}

	/**
	 * @return the maxStretch
	 */
	public int getStretch()
	{
		return stretch;
	}

	/**
	 * @param stretch the maxStretch to set
	 */
	public void setStretch(int stretch)
	{
		this.stretch = stretch;
	}
}
