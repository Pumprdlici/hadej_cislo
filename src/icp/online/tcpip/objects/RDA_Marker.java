package icp.online.tcpip.objects;

/**
 * Název úlohy: Jednoduché BCI Tøída: RDA_Marker
 *
 * @author Michal Patoèka První verze vytvoøena: 3.3.2010
 * @version 1.0
 *
 * Tento objekt reprezentuje pøíchozí markery. Obsahuje informace o jeho
 * velikosti, relativním odsazením v datovém bloku (mùže být od 0 až po velikost
 * tohoto bloku) a poètu obsažených datových blokù (toto èíslo je standartnì 1).
 * Dále obsahuje inforamci o tom ke které elektrodì pøísluší. Jelikož však
 * server doposud nemá implemetovanou funkci pro zasílání markerù pouze z
 * urèitého poètu elektrod, je tato hodnota standartnì nastavena na -1, což
 * znamená, že platí pro všechny elektrody. Nejdùležitìjší èástí tohoto objektu
 * je však informace o názvu tohoto impulzu, která je oddìlená nulovými znaky
 * (/0).
 */

public class RDA_Marker {

    /**
     * Velikost tohoto bloku v bajtech. *
     */
    private final long nSize;
    /**
     * Relativní odsazení v datovém bloku. *
     */
    private final long nPosition;
    /**
     * Poèet obsažených blokù (standartnì 1). *
     */
    private final long nPoints;
    /**
     * Zasažený kanál (standartnì -1 - všechny kanály). *
     */
    private final long nChannel;
    /**
     * Název pøíchozího markeru. *
     */
    private final String sTypeDesc;

    public RDA_Marker(long nSize, long nPosition, long nPoints, long nChannel,
            String sTypeDesc) {
        super();
        this.nSize = nSize;
        this.nPosition = nPosition;
        this.nPoints = nPoints;
        this.nChannel = nChannel;
        this.sTypeDesc = sTypeDesc;
    }

    @Override
    public String toString() {
        return "RDA_Marker (size = " + nSize + ")\n"
                + "Channel= " + nChannel + "\n"
                + "Points= " + nPoints + "\n"
                + "Position= " + nPosition + "\n"
                + "TypeDesc=" + sTypeDesc + "\n";
    }

    public long getnSize() {
        return nSize;
    }

    public long getnPosition() {
        return nPosition;
    }

    public long getnPoints() {
        return nPoints;
    }

    public long getnChannel() {
        return nChannel;
    }

    public String getsTypeDesc() {
        return sTypeDesc;
    }

}
