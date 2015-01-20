package icp.online.app;

import icp.Const;
import icp.online.tcpip.DataTokenizer;
import icp.online.tcpip.TCPIPClient;
import icp.online.tcpip.objects.RDA_Marker;
import icp.online.tcpip.objects.RDA_MessageData;
import icp.online.tcpip.objects.RDA_MessageStart;
import icp.online.tcpip.objects.RDA_MessageStop;

import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

public class OnLineDataProvider extends Observable implements IDataProvider, Runnable { 

    /**
     * Bufer, který se bude používat pro ukládání hodnot z datových objektù
     * RDA_MessageData
     */
    private Buffer buffer;
    private final Logger logger = Logger.getLogger(OnLineDataProvider.class);
    private final String ipAddress;
    private final int port;
    private final TCPIPClient client;
    private final DataTokenizer dtk;
    private final Observer obs;
    private boolean isRunning;

    /**
     * Konstruktor, který vytvoøí instanci této øídící tøídy.
     *
     * @param ip_adr - IP adresa serveru, na který se napojí client
     * @param port - port, který se použije pro komunikaci se serverem
     * @param obs
     * @throws java.lang.Exception
     */
    @SuppressWarnings("CallToThreadStartDuringObjectConstruction")
    public OnLineDataProvider(String ip_adr, int port, Observer obs) throws Exception {
        super();
        this.ipAddress = ip_adr;
        this.port = port;
        this.obs = obs;
        client = new TCPIPClient(this.ipAddress, this.port);
        client.start();
        dtk = new DataTokenizer(client);
        dtk.start();
        isRunning = true;
    }

    @Override
    public void run() {
        addObserver(obs);
        /* delku bufferu je nutno zvolit libovolne vhodne */
        this.buffer = new Buffer(Const.BUFFER_SIZE, Const.PREEPOCH_VALUES, Const.POSTEPOCH_VALUES);
        boolean stopped = false;
        int cisloStimulu = 0;
        while (isRunning && cisloStimulu < Const.NUMBER_OF_STIMULUS + 1) {
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
            } else if (o instanceof RDA_MessageStop) {
                client.requestStop();
                dtk.requestStop();
                isRunning = false;
                stopped = true;
            }
            if (buffer.jePlny() || (cisloStimulu > Const.NUMBER_OF_STIMULUS)) {
                for (EpochDataCarrier data = buffer.vyber(); data != null; data = buffer.vyber()) {
                    EpochMessenger em = new EpochMessenger();
                    em.setStimulusIndex(data.getStimulusType());
                    em.setFZ(data.getFzValues(), 0);
                    em.setCZ(data.getCzValues(), 0);
                    em.setPZ(data.getPzValues(), 0);

                    this.setChanged();
                    this.notifyObservers(em);
                    System.out.println(em);

                }
                buffer.vymaz();
            }
        }

        if (!stopped) {
            client.requestStop();
            dtk.requestStop();
        }

        logger.info("EXPERIMENT SKONÈIL, mùžete ukonèit mìøení");
    }

    @Override
    public synchronized void stop() {
        isRunning = false;
    }

}
