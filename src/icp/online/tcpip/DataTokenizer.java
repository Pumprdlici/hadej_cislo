package icp.online.tcpip;

import icp.online.tcpip.objects.RDA_Marker;
import icp.online.tcpip.objects.RDA_MessageData;
import icp.online.tcpip.objects.RDA_MessageHeader;
import icp.online.tcpip.objects.RDA_MessageStart;
import icp.online.tcpip.objects.RDA_MessageStop;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

/**
 * Název úlohy: Jednoduché BCI Tøída: DataTokenizer
 *
 * @author Michal Patoèka První verze vytvoøena: 3.3.2010
 * @version 2.0
 *
 * Tato tøída formuje z toku bajtù získané od klienta TCPIP datové
 * objekty. Celı proces detekce zaèíná hledáním unikátní posloupnosti 12
 * bajtù, které oznaèují hlavièku datového objektu. Toto je
 * naimplementováno tak, e v nekoneèném cyklu pøidávám poslední a
 * ubírám první z pole 12 bajtù a hledám shodu mezi tímto polem a polem
 * oznaèujicím hlavièku. Je - li tato posloupnost nalezena, znamená to, e
 * pøišel jeden z pìti typù datovıch objektù. Jakı typ pøišel a jaká
 * je jeho délka zjistím pøeètením následujících 8 bajtù, které
 * následují po hlavièce. Obecnì platí, e na zaèátku kadého pøenosu
 * pøijde objekt typu RDA_MessageStart, ve kterém jsou deklarovány pouité
 * parametry pro následující datovı pøenos. Poté chodí znaèné
 * mnoství objektù typu RDA_MessageData, pøièem kadı z nich mùe
 * obsahovat nìkolik objektù typu RDA_Marker (nejèastìji však pouze jeden).
 * Kdy zjistím typ objektu, jakı pøichází, není problém do nìj
 * naèíst data konverzí pole urèitého mnoství bajtù, do poadovaného
 * datového typu. Pokud je objekt neznámého typu, tak ho nezpracovávám
 * (pøi testech chodily objekty typu nType = 10000 jako vıplò mezi
 * jednotlivımi objekty). Všechny objekty naèítám do bufferu, kde jsou
 * pøipraveny k vyzvednutí pomocí metody retriveDataBlock().
 */
public class DataTokenizer extends Thread {

    /**
     * Poèet kanálù EEG *
     */
    private int noOfChannels;
    /**
     * Buffer jako vyrovnávací pamì pro doèasné uloení objektù *
     */
    private final SynchronizedLinkedListObject buffer = new SynchronizedLinkedListObject();
    /**
     * Unikátní posloupnost 12 bajtù, která oznaèuje hlavièku datového
     * objektu. *
     */
    private static final byte[] UID = {-114, 69, 88, 67, -106, -55, -122, 76, -81, 74, -104, -69, -10, -55, 20, 80};
    /**
     * Reference na TCP/IP klienta, ze kterého získávám bajty ke
     * zpracování. *
     */
    private final TCPIPClient client;

    private RDA_MessageStart start;
    
    private boolean isRunning;

    /**
     * Reference na logger událostí. *
     */
    private static final Logger logger = Logger.getLogger(DataTokenizer.class);

    /**
     * Zjišuje jestli jsou dvì pole bajtù shodná.
     *
     * @param one první pole bajtù
     * @param two drué pole bajtù
     * @return shoda/neshoda
     */
    private boolean comparator(byte[] one, byte[] two) {
        for (int i = 0; i < one.length; i++) {
            if (one[i] != two[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Poli bajtù pøidá na konec novı bajt a posune index celého pole o 1,
     * èím vymae odkaz na první bajt.
     *
     * @param field pole bajtù
     * @param ap pøidávanı bajt
     * @return posunuté pole s bajtem navíc
     */
    private byte[] appendByte(byte[] field, byte ap) {
        for (int i = 0; i < (field.length - 1); i++) {
            field[i] = field[i + 1];
        }
        field[field.length - 1] = ap;
        return field;
    }

    /**
     * Tato metoda zapisuje objekty typu RDA_Marker a pøedává na nì
     * reference pøíslušnému datovému objektu.
     *
     * @param markerCount poèet markerù, které se zpracovávají.
     * @return pole markerù
     */
    private RDA_Marker[] writeMarkers(int markerCount) {
        RDA_Marker[] nMarkers = new RDA_Marker[markerCount];
        for (int i = 0; i < markerCount; i++) {
            byte[] nSize = client.read(4);
            long size = getInt(nSize);

            byte[] nPosition = client.read(4);
            long position = getInt(nPosition);

            byte[] nPoints = client.read(4);
            long points = getInt(nPoints);

            client.read(4);
            /*
             * Tuto funkci doposud servr nemá implementovanou.
             * Proto vrací blbost. V návodu je e se defaultnì jedná
             * o všechny kanály, proto hodnota -1.
             * long channel = arr2long(nChannel);*/
            long channel = -1;

            byte[] sTypeDesc = client.read((int) size - 16);
            String typeDesc = "";
            for (int j = 0; j < sTypeDesc.length; j++) {
                char znak = (char) sTypeDesc[j];
                typeDesc = typeDesc + znak;
            }
            nMarkers[i] = new RDA_Marker(size, position, points, channel, typeDesc);
        }
        return nMarkers;
    }

    /**
     * Konstruktor, kterému je pøedávám odkaz na TCP/IP clienta. Je pouit
     * defaultní logger.
     *
     * @param client TCP/IP client pro získávání dat
     */
    public DataTokenizer(TCPIPClient client) {
        this.client = client;
    }

    private int getInt(byte[] buff) {
        ByteBuffer bf = ByteBuffer.wrap(buff);
        bf.order(ByteOrder.LITTLE_ENDIAN);
        return bf.getInt();
    }

    private double getDouble(byte[] buff) {
        ByteBuffer bf = ByteBuffer.wrap(buff);
        bf.order(ByteOrder.LITTLE_ENDIAN);
        return bf.getDouble();
    }

    private float getFloat(byte[] buff) {
        ByteBuffer bf = ByteBuffer.wrap(buff);
        bf.order(ByteOrder.LITTLE_ENDIAN);
        return bf.getFloat();
    }

    /**
     * Metoda pro spuštìní vlákna DataTokenizeru. Jeliko proces
     * získávání dat a jejich pøevádìní na datové objekty musí bıt
     * paraelizován, musí bıt pouito vláknového zpracování.
     */
    @Override
    public void run() {
        isRunning = true;
        byte[] value = client.read(16);
        while (isRunning) {

            if (comparator(value, UID)) {

                byte[] nSize = client.read(4);
                byte[] nType = client.read(4);
                int size = getInt(nSize);
                int type = getInt(nType);
                RDA_MessageHeader pHeader = new RDA_MessageHeader(size, type);

                //RDA_MessageStart
                if (pHeader.getnType() == 1) {

                    byte[] nChannels = client.read(4);
                    long channels = getInt(nChannels);
                    noOfChannels = (int) channels;

                    byte[] dSamplingInterval = client.read(8);
                    double samplingInterval = getDouble(dSamplingInterval);

                    String[] typeDesc = new String[(int) channels];
                    double[] resolutions = new double[(int) channels];

                    for (int j = 0; j < channels; j++) {
                        byte[] dResolutions = client.read(8);
                        resolutions[j] = getDouble(dResolutions);
                    }

                    //jména kanálù mohou mít promìnnou délku, jsou oddìlená znakem \0
                    for (int j = 0; j < channels; j++) {
                        byte[] b = client.read(1);
                        char rd = (char) b[0];
                        String channelName = "";
                        while (rd != '\0') {
                            channelName = channelName + rd;
                            b = client.read(1);
                            rd = (char) b[0];
                        }
                        typeDesc[j] = channelName;
                    }

                    RDA_MessageStart pMsgStart = new RDA_MessageStart(pHeader.getnSize(), pHeader.getnType(),
                            channels, samplingInterval, resolutions, typeDesc);
                    start = pMsgStart;
                    buffer.addLast(pMsgStart);
                    logger.debug("Zahájena komunikace se serverem.");

                    //RDA_MessageStop	
                } else if (pHeader.getnType() == 3) {
                    RDA_MessageStop pMsgStop = new RDA_MessageStop(pHeader.getnSize(), pHeader.getnType());
                    buffer.addLast(pMsgStop);
                    logger.debug("Ukonèena komunikace se serverem.");

                    break;

                    //RDA_MessageData	
                } else if (pHeader.getnType() == 4) {

                    byte[] nBlock = client.read(4);
                    int block = getInt(nBlock);

                    byte[] nPoints = client.read(4);
                    int points = getInt(nPoints);

                    byte[] nMarkers = client.read(4);
                    int markers = getInt(nMarkers);

                    float[] data = new float[noOfChannels * (int) points];

                    int ch = 0;
                    for (int j = 0; j < data.length; j++) {
                        byte[] fData = client.read(4);
                        data[j] = getFloat(fData) * (float)start.getdResolutions()[0];
                    }

                    //RDA_Marker
                    RDA_Marker[] markerField = null;
                    if (markers > 0) {
                        markerField = writeMarkers((int) markers);
                    }

                    RDA_MessageData pMsgData = new RDA_MessageData(pHeader.getnSize(), pHeader.getnType(),
                            block, points, markers, data, markerField);

                    buffer.addLast(pMsgData);

                    for (int j = 0; j < markers; j++) {
                        buffer.addLast(markerField[j]);
                        logger.debug("Pøíchozí marker: " + markerField[j].getsTypeDesc());
                    }

                } else {
                    //všechny neznámé typy objektù se ignorují
                }

            }
            byte[] ap = client.read(1);
            value = appendByte(value, ap[0]);
        }
    }
    
    public void requestStop(){
        isRunning = false;
    }

    /**
     * Tato metoda vrací první objekt na vrcholu bufferu, do kterého jsou
     * naèítány datové bloky.
     *
     * @return datovı objekt
     */
    public synchronized Object retrieveDataBlock() {

        Object o = null;
        while (true) {
            if (!buffer.isEmpty()) {
                try {
                    o = buffer.removeFirst();
                    break;
                } catch (NoSuchElementException e) {
                    //OVERFLOW
                    e.printStackTrace();
                }
            }
        }
        return o;
    }

    /**
     * Tato metoda zjišuje, jetli je prázdnı buffer.
     *
     * @return zda - li je prázdnı buffer.
     */
    public boolean hasNext() {
        return buffer.isEmpty();
    }

}
