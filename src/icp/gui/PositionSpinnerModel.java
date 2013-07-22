package icp.gui;

import javax.swing.SpinnerNumberModel;

public class PositionSpinnerModel extends SpinnerNumberModel
{
	private int min, max, step;
	
	public PositionSpinnerModel(int min, int step, int max)
	{	
		this.min = min;
		this.step = step;
		this.max = max;
		this.setMinimum(min);
		this.setValue(min);
		this.setStepSize(step);
	}
	
	@Override
	public void setValue(Object value)
	{
		int intValue = ((Integer)value).intValue();		
		int multipleValue = intValue/this.step;
		
		if(intValue > this.max)
			super.setValue(this.max);
		else if(intValue < this.min)
			super.setValue(this.min);
		else
			super.setValue(this.step*multipleValue);
	}
}
