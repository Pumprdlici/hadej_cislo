package icp.data;

/**
 * Tøída reprezentující artefakt.
 * @author Petr - Soukal
 */
public class Artefact
{
	private int artefactStart;
	private int artefactEnd;
	
	/**
	 * Vytváøí instanci tøídy.
	 * 
	 * @param artefactStart - poèáteèní hodnota intervalu artefaktu.
	 * @param artefactEnd - koneèná hodnota intervalu artefaktu.
	 */
	public Artefact(int artefactStart, int artefactEnd)
	{
		this.artefactStart = artefactStart;
		this.artefactEnd = artefactEnd;
	}

	/**
	 * Nastavuje poèáteèní hodnotu intervalu artefaktu.
	 * 
	 * @param artefactStart - poèáteèní hodnota intervalu artefaktu.
	 */
	public void setArtefactStart(int artefactStart)
	{
		this.artefactStart = artefactStart;
	}
	
	/**
	 * Nastavuje koneènou hodnotu intervalu artefaktu.
	 * 
	 * @param artefactEnd - koneènou hodnota intervalu artefaktu.
	 */
	public void setArtefactEnd(int artefactEnd)
	{
		this.artefactEnd = artefactEnd;
	}
	
	/**
	 * @return poèáteèní hodnota intervalu artefaktu.
	 */
	public int getArtefactStart()
	{
		return artefactStart;
	}	
	
	/**
	 * @return koneèná hodnota intervalu artefaktu.
	 */
	public int getArtefactEnd()
	{
		return artefactEnd;
	}
}
