package icp.data;

import java.io.*;
import java.util.*;

public class RawDataLoader
{
	public static double[] loadAtom(File file) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(file));
		List<Double> buffer = new ArrayList<Double>(256);
		String line;
		String[] lineSplit;
		
		while ((line = br.readLine()) != null)
		{
			lineSplit = line.split("[ ]");
			for (int i = 0; i < lineSplit.length; i++)
			{
				buffer.add(Double.parseDouble(lineSplit[i]));
			}
		}
		br.close();
		
		double[] valuesOfAtom = new double[buffer.size()];
		Arrays.fill(valuesOfAtom, 0);
		
		for (int i = 0; i < valuesOfAtom.length; i++)
		{
			valuesOfAtom[i] = buffer.get(i);
		}
		
		return valuesOfAtom;
	}
}
