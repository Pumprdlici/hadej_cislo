package icp.online.tcpip.objects;

/**
 * Název úlohy: Jednoduché BCI Tøída: RDA_MessageStop
 *
 * @author Michal Patoèka První verze vytvoøena: 3.3.2010
 * @version 1.0
 *
 * Tento prázdný objekt pøichází pøi ukonèení komunikace se serverem.
 * @author Michal Patoèka.
 */
public class RDA_MessageStop extends RDA_MessageHeader {

    public RDA_MessageStop(long nSize, long nType) {
        super(nSize, nType);
    }

    @Override
    public String toString() {
        return "RDA_MessageStop []";
    }

}
