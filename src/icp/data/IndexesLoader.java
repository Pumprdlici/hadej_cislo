package icp.data;

import icp.Const;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Nahrává ze vstupního souboru indexy epoch, které mají být zahrnuty/odstranìny do/z prùmìrù.
 * 
 * @author Tomáš Øondík
 */
public class IndexesLoader
{
	/**
	 * Oddìlovaè indexù epoch ve vstupním souboru.
	 */
	private String indexesSeparator;
	/**
	 * Vytváøí instanci tøídy. Do atributu <i>indexesSeparator</i> nastavuje nejèastìji používané separátory:
	 * <code>indexesSeparator = new String("[,; ]");</code>.
	 */
	public IndexesLoader()
	{
		indexesSeparator = new String("[,; ]"); //nejèastìji používané separátory
	}
	/**
	 * Naèítá indexy epoch pro hromadné zahrnování/odstraòování do/z prùmìrù ze vstupního souboru.
	 * @param file Soubor s indexy epoch.
	 * @return List indexù epoch, které se mají zahrnout/odstranit do/z prùmìrù.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public List<Integer> loadIndexes(File file) throws FileNotFoundException, IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(file));
		List<Integer> indexes = new ArrayList<Integer>();
		
		String line = reader.readLine();
		
		while (line != null)
		{
			String[] parsed = line.split(indexesSeparator);
			
			for (String string: parsed)
			{
				/*
				 * Pro pøípad, že by jako oddìlovaè indexù byla použita nìjaká n-tice znakù.
				 * Napø. ", ".
				 */
				if (string.length() > 0)
                                {
					indexes.add(Integer.valueOf(string) - Const.ZERO_INDEX_SHIFT);
                                }
			}
			
			line = reader.readLine();
		}
		
		reader.close();
		return indexes;
	}
	
	/**
	 * Vrací referenci na atribut <i>indexesSeparator</i>.
	 * @return Obecnì n-tice znakù, která bude použita pro parsování souboru s indexy.
	 */
	public String getIndexesSeparator()
	{
		return indexesSeparator;
	}
	/**
	 * Nastavuje referenci na atribut <i>indexesSeparator</i>.
	 * @param indexesSeparator Obecnì n-tice znakù, která bude použita pro parsování souboru s indexy.
	 */
	public void setIndexesSeparator(String indexesSeparator)
	{
		if (indexesSeparator != null)
			this.indexesSeparator = indexesSeparator;
	}
}
