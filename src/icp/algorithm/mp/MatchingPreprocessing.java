package icp.algorithm.mp;

import icp.aplication.Element;
import icp.aplication.SessionManager;

import java.util.List;


public class MatchingPreprocessing extends Thread
{
	private SessionManager sm;
	
	private List<Element> elements;
	
	private double unit;
	
	private boolean stop;
	
	public void stopMP()
	{
		stop = true;
	}
	
	public MatchingPreprocessing(SessionManager sm)
	{
		this.sm = sm;
	}
	
	public void init(List<Element> elements)
	{
		this.elements = elements;
		unit = 100D / ((double) (elements.size() * 8D));
	}
	
	@Override
	public void run()
	{
		stop = false;
		double[][] rowsAndCols;
		DecompositionCollection dc;
		FourierMP mp = new FourierMP(new GaborBase());
		
		for (Element e: elements)
		{
			rowsAndCols = e.getRowsAndColumnsEpoch();
			
			for (int i = 0; i < rowsAndCols.length; i++)
			{
				if (stop)
					return;
				
				dc = mp.doMP(rowsAndCols[i], 5);
				rowsAndCols[i] = dc.getReconstruction(5);
				sm.sendProgressUnits(unit);
			}
		}
		sm.sendMPPreprocessingMessage();
	}
}
