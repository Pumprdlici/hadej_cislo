package icp.online.tcpip.objects;

/**
 * Název úlohy: Jednoduché BCI Tøída: RDA_MessageStart
 *
 * @author Michal Patoèka První verze vytvoøena: 3.3.2010
 * @version 1.0
 *
 * Tento objekt pøichází obecnì pøi zapoèetí komunikace se serverem. Obsahuje
 * informace o poètu kanálù, snímkovací frekvenci pøístroje EEG a seznam
 * jednotlivých kanálù, spoleènì s jejich jmény a voltážemi.
 */
public class RDA_MessageStart extends RDA_MessageHeader {

    /**
     * Poèet kanálù pøístroje EEG. *
     */
    private final long nChannels;
    /**
     * Snímkovací frekvence. *
     */
    private final double dSamplingInterval;
    /**
     * Voltáže jednotlivých elektrod. *
     */
    private final double[] dResolutions;
    /**
     * Názvy jednotlivých elektrod. *
     */
    private final String[] sChannelNames;

    public RDA_MessageStart(long nSize, long nType, long nChannels,
            double dSamplingInterval, double[] dResolutions, String[] sChannelNames) {
        super(nSize, nType);
        this.nChannels = nChannels;
        this.dSamplingInterval = dSamplingInterval;
        this.dResolutions = dResolutions;
        this.sChannelNames = sChannelNames;
    }

    @Override
    public String toString() {

        String navrat = "RDA_MessageStart (size = " + nSize + ") \n"
                + "Sampling interval: " + dSamplingInterval + " µS \n"
                + "Number of channels: " + nChannels + "\n";

        for (int i = 0; i < dResolutions.length; i++) {
            navrat = navrat + (i + 1) + ": " + sChannelNames[i] + ": " + dResolutions[i] + "\n";
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
