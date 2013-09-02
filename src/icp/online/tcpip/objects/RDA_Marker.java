package icp.online.tcpip.objects;
/**
 * Název úlohy: Jednoduché BCI
 * Třída: RDA_Marker
 * @author Michal Patočka
 * První verze vytvořena: 3.3.2010
 * @version 1.0
 * 
 * Tento objekt reprezentuje příchozí markery. Obsahuje informace o jeho velikosti, relativním
 * odsazením v datovém bloku (může být od 0 až po velikost tohoto bloku) a počtu obsažených
 * datových bloků (toto číslo je standartně 1). Dále obsahuje inforamci o tom ke které elektrodě 
 * přísluší. Jelikož však server doposud nemá implemetovanou funkci pro zasílání markerů pouze z
 * určitého počtu elektrod, je tato hodnota standartně nastavena na -1, což znamená, že platí pro
 * všechny elektrody. Nejdůležitější částí tohoto objektu je však informace o názvu tohoto impulzu,
 * která je oddělená nulovými znaky (/0).
 */

public class RDA_Marker {
	/** Velikost tohoto bloku v bajtech. **/
	private long nSize;
	/** Relativní odsazení v datovém bloku. **/
	private long nPosition;
	/** Počet obsažených bloků (standartně 1). **/
	private long nPoints;
	/** Zasažený kanál (standartně -1 - všechny kanály). **/
	private long nChannel;
	/** Název příchozího markeru. **/
	private String sTypeDesc;
	
	public RDA_Marker(long nSize, long nPosition, long nPoints, long nChannel,
			String sTypeDesc) {
		super();
		this.nSize = nSize;
		this.nPosition = nPosition;
		this.nPoints = nPoints;
		this.nChannel = nChannel;
		this.sTypeDesc = sTypeDesc;
	}

	public String toString() {
		return "RDA_Marker (size = " + nSize +")\n" +
				"Channel= " + nChannel + "\n" +
				"Points= " + nPoints + "\n" 
				+ "Position= " + nPosition  + "\n" 
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
