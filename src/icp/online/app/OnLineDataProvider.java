package icp.online.app;

import icp.online.tcpip.DataTokenizer;
import icp.online.tcpip.TCPIPClient;
import icp.online.tcpip.objects.RDA_Marker;
import icp.online.tcpip.objects.RDA_MessageData;
import icp.online.tcpip.objects.RDA_MessageStart;

import java.io.File;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;

import org.apache.log4j.Logger;

public class OnLineDataProvider extends Observable implements IDataProvider {

    private static final int DELKABUFFERU = 10000;
    private static final int POCETHODNOTPREDEPOCHOU = 100;
    private static final int POCETHODNOTZAEPOCHOU = 512;

    /**
     * Poèet stimulù, po jakém se zastaví hlavní test.
     */
    private static final int POCETSTIMULU = 400;

    /**
     * Bufer, který se bude používat pro ukládání hodnot z datových objektù
     * RDA_MessageData
     */
    private Buffer buffer;
    private final Logger logger = Logger.getLogger(OnLineDataProvider.class);
    private final String ipAddress;
    private final int port;
    private final TCPIPClient client;
    private DataTokenizer dtk;

    /**
     * Konstruktor, který vytvoøí instanci této øídící tøídy.
     *
     * @param ip_adr - IP adresa serveru, na který se napojí client
     * @param port - port, který se použije pro komunikaci se serverem
     * @throws java.lang.Exception
     */
    public OnLineDataProvider(String ip_adr, int port) throws Exception {
        super();
        this.ipAddress = ip_adr;
        this.port = port;
        client = new TCPIPClient(this.ipAddress, this.port);
        client.start();
        dtk = new DataTokenizer(client);
        dtk.start();
    }

    @Override


	public void readEpochData(Observer obs) {
        addObserver(obs);
        /* delku bufferu je nutno zvolit libovolne vhodne */
        this.buffer = new Buffer(DELKABUFFERU, POCETHODNOTPREDEPOCHOU, POCETHODNOTZAEPOCHOU);

        int cisloStimulu = 0;
        while (cisloStimulu < POCETSTIMULU + 1) {
            Object o = dtk.retrieveDataBlock();
            if (o instanceof RDA_Marker) {
                /* takto získám pøíchozí èíslo z markeru */
                int cislo = ((Integer.parseInt(((RDA_Marker) o).getsTypeDesc().substring(11, 13).trim())) - 1);
                logger.debug("" + cislo);
                cisloStimulu++;
            } else if (o instanceof RDA_MessageData) {
                buffer.zapis((RDA_MessageData) o);
            } else if (o instanceof RDA_MessageStart) {
                RDA_MessageStart msg = (RDA_MessageStart) o;
                String[] chNames = msg.getsChannelNames();
                Buffer.numChannels = (int) msg.getnChannels();
                for (int i = 0; i < chNames.length; i++) {
                    if (chNames[i].equalsIgnoreCase("cz")) {
                        Buffer.indexCz = i;
                    } else if (chNames[i].equalsIgnoreCase("pz")) {
                        Buffer.indexPz = i;
                    } else if (chNames[i].equalsIgnoreCase("fz")) {
                        Buffer.indexFz = i;
                    }
                }
            }
            if (buffer.jePlny() || (cisloStimulu > POCETSTIMULU)) {
                for (HodnotyVlny data = buffer.vyber(); data != null; data = buffer.vyber()) {
                    EpochMessenger em = new EpochMessenger();
                    em.setStimulusIndex(data.getTypStimulu());
                    em.setFZ(data.getHodnotyFZ());
                    em.setCZ(data.getHodnotyCZ());
                    em.setPZ(data.getHodnotyPZ());

                    this.setChanged();
                    this.notifyObservers(em);
                    System.out.println(em);

                }
                buffer.vymaz();
            }
        }
        logger.info("EXPERIMENT SKONÈIL, mùžete ukonèit mìøení");
    }

}
