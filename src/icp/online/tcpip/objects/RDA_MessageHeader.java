package icp.online.tcpip.objects;

/**
 * Název úlohy: Jednoduché BCI
 * Třída: RDA_MessageHeader
 * @author Michal Patočka
 * První verze vytvořena: 3.3.2010
 * @version 1.0
 * 
 * Tato třída reprezentuje datový objekt který přichází ze serveru. Je vždy označen
 * unikátní posloupností bajtů. Nese informace o typu a velikosti následujícího datového
 * bloku. Tuto hlavičku obsahují všechny ostatní datové objekty (s výjimkou objektu typu
 * RDA_Marker). Díky této třídě vím, jaká data mám zpracovávat.
 */
public class RDA_MessageHeader {
	/** Velikost celého datového bloku. **/ 
	protected long nSize;
	/** Typ datového bloku. **/
	protected long nType;
	
	public RDA_MessageHeader(long nSize, long nType) {
		this.nSize = nSize;
		this.nType = nType;
	
	}

	public long getnSize() {
		return nSize;
	}

	public long getnType() {
		return nType;
	}

	public String toString() {
		return "RDA_MessageHeader [nSize=" + nSize + ", nType=" + nType + "]";
	}

	
	
	
}
