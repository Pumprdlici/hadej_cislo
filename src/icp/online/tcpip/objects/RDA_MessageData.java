package icp.online.tcpip.objects;

/**
 * Název úlohy: Jednoduché BCI
 * Tøída: RDA_MessageData
 * @author Michal Patoèka
 * První verze vytvoøena: 3.3.2010
 * @version 1.0
 * 
 * Tato tøída je obecnou datovou tøídou zasílanou serverem. Obsahuje informace o poètu obsažených
 * datových blokù, poètu obsažených markerù a relativní poøadí tohoto bloku od zaèátku komunikace.
 * Dále samozøejmì obsahuje samotná data, která jsou uložena v poli o délce (poèet kanálù * poèet 
 * datových blokù). Rovnìž obsahuje informace o pøítomných markerech.
 */
public class RDA_MessageData extends RDA_MessageHeader {
	/** Poøadí tohoto bloku od poèátku komunikace. **/
	private long nBlock;
	/** Poèet obsažených datových blokù. **/
	private long nPoints;
	/** Poèet obsažených markerù. **/
	private long nMarkers;
	/** Pole s uloženými hodnotami (samotná data). **/
	private float[] fData;
	/** Pole s referencemi na obsažené markery. **/
	private RDA_Marker[] markers;
	
	public RDA_MessageData(long nSize, long nType, long nBlock,
			long nPoints, long nMarkers, float[] fData, RDA_Marker[] markers) {
		super(nSize, nType);
		this.nBlock = nBlock;
		this.nPoints = nPoints;
		this.nMarkers = nMarkers;
		this.fData = fData;
		this.markers = markers;
	}

	public String toString() {
		
		String navrat = "RDA_MessageData (size = "+ super.getnSize() +  ") \n" +
		"block NO.: " + nBlock + " \n" +
		"points: " + nPoints + "\n" +
		"NO of markers: " + nMarkers + "\n";
		int nChannels = fData.length/(int)nPoints;
		
		for(int i = 0; i < nChannels; i++){
			navrat = navrat + (i+1) + ": ";
			for(int j = i; j < fData.length; j += nPoints){
				navrat = navrat + fData[j] + ", ";
			}
			navrat = navrat + "\n";
		}
		navrat = navrat + "\n";
		
		for(int i = 0; i < nMarkers; i++){
			navrat = navrat + markers[i].toString();
		}
		
		return navrat;
	}

	public long getnBlock() {
		return nBlock;
	}

	public long getnPoints() {
		return nPoints;
	}

	public long getnMarkers() {
		return nMarkers;
	}

	public float[] getfData() {
		return fData;
	}

	public RDA_Marker[] getMarkers() {
		return markers;
	}

	
	
	
}
