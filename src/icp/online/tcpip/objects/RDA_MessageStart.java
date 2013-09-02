package icp.online.tcpip.objects;

/**
 * Název úlohy: Jednoduché BCI
 * Třída: RDA_MessageStart
 * @author Michal Patočka
 * První verze vytvořena: 3.3.2010
 * @version 1.0
 * 
 * Tento objekt přichází obecně při započetí komunikace se serverem. Obsahuje
 * informace o počtu kanálů, snímkovací frekvenci přístroje EEG a seznam jednotlivých
 * kanálů, společně s jejich jmény a voltážemi.
 */

public class RDA_MessageStart extends RDA_MessageHeader {
	/** Počet kanálů přístroje EEG. **/
	private long nChannels;
	/** Snímkovací frekvence. **/
	private double dSamplingInterval;
	/** Voltáže jednotlivých elektrod. **/
	private double[] dResolutions;
	/** Názvy jednotlivých elektrod. **/
	private String[] sChannelNames;
	
	public RDA_MessageStart(long nSize, long nType, long nChannels,
			double dSamplingInterval, double[] dResolutions, String[] sChannelNames) {
		super(nSize, nType);
		this.nChannels = nChannels;
		this.dSamplingInterval = dSamplingInterval;
		this.dResolutions = dResolutions;
		this.sChannelNames = sChannelNames;
	}


	public String toString() {
		
		String navrat = "RDA_MessageStart (size = "+ nSize +  ") \n" +
				"Sampling interval: " + dSamplingInterval + " µS \n" +
				"Number of channels: " + nChannels + "\n"; 
		
		for(int i = 0; i < dResolutions.length; i++){
			navrat = navrat + (i+1) + ": " + sChannelNames[i] + ": " + dResolutions[i] + "\n";
		}
				
		return navrat;
	}


	public long getnChannels() {
		return nChannels;
	}


	public double getdSamplingInterval() {
		return dSamplingInterval;
	}


	public double[] getdResolutions() {
		return dResolutions;
	}


	public String[] getsChannelNames() {
		return sChannelNames;
	}

	
	
	
}
