package icp.online.tcpip.objects;

/**
 * Název úlohy: Jednoduché BCI Tøída: RDA_MessageHeader
 *
 * @author Michal Patoèka První verze vytvoøena: 3.3.2010
 * @version 1.0
 *
 * Tato tøída reprezentuje datový objekt který pøichází ze serveru. Je vždy
 * oznaèen unikátní posloupností bajtù. Nese informace o typu a velikosti
 * následujícího datového bloku. Tuto hlavièku obsahují všechny ostatní datové
 * objekty (s výjimkou objektu typu RDA_Marker). Díky této tøídì vím, jaká data
 * mám zpracovávat.
 */
public class RDA_MessageHeader {

    /**
     * Velikost celého datového bloku. *
     */
    protected long nSize;
    /**
     * Typ datového bloku. *
     */
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

    @Override
    public String toString() {
        return "RDA_MessageHeader [nSize=" + nSize + ", nType=" + nType + "]";
    }
}
