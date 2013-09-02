package icp.online.tcpip.objects;
		
/**
 * Název úlohy: Jednoduché BCI
 * Třída: RDA_MessageStop
 * @author Michal Patočka
 * První verze vytvořena: 3.3.2010
 * @version 1.0
 * 
 * Tento prázdný objekt přichází při ukončení komunikace se serverem.
 * @author Michal Patočka.
 */
public class RDA_MessageStop extends RDA_MessageHeader {

	public RDA_MessageStop(long nSize, long nType) {
		super(nSize, nType);
	}

	public String toString() {
		return "RDA_MessageStop []";
	}
	
}
