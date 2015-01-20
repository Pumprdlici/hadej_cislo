package icp.online.tcpip;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

/**
 * Název úlohy: Jednoduché BCI Tøída: SynchronizedLinkedListByte
 *
 * @author Michal Patoèka První verze vytvoøena: 3.3.2010
 * @version 2.0
 *
 * TCP/IP klient pro napojení na RDA server. Pøipojení je zajištìno pouitím
 * tøídy Socket a ošetøením patøiènıch vıjimek. Data jsou zpracovávána po
 * jednotlivıch bajtech, protoe je tak server (bohuel) zasílá. Vyuívá
 * vyrovnávací pamì (linkedlist) do které zapisuje získané bajty ze serveru.
 * Nadstavbou této tøídy je bìnì tøída typu dataTokenizer, která získané bajty
 * pøevádí do podoby srozumitelnìjších objektù. Bajty lze získat pomocí metody
 * read().
 */
public class TCPIPClient extends Thread {

    /**
     * Datovı stream pøíchozích bajtù. *
     */
    private DataInputStream Sinput;
    /**
     * Instance tøídy socket pro navázání spojení se serverem. *
     */
    private Socket socket;
    /**
     * Linked list jako vyrovnávací pamì pro získávání bajtù. *
     */
    private SynchronizedLinkedListByte buffer = new SynchronizedLinkedListByte();
    /**
     * Reference na logger událostí. *
     */
    private static final Logger logger = Logger.getLogger(TCPIPClient.class);
    
    private boolean isRunning;

    /**
     * Konstruktor TCP/IP clienta. V parametrech má na jakou IP a na jakı port
     * se napojuje. Je pouit defaultní logger.
     *
     * @param ip na jakou ip se má pøipojit
     * @param port na jakém portu má naslouchat
     * @throws java.lang.Exception
     */
    public TCPIPClient(String ip, int port) throws Exception {

        //vytvoøení instance tøídy socket - napojení na server
        try {
            socket = new Socket(ip, port);
        } catch (Exception e) {
            logger.error("Chyba pøi pøipojování na server:" + e);
            throw new Exception(e.getMessage());
        }
        logger.debug("Pøipojení navázáno: "
                + socket.getInetAddress() + ":"
                + socket.getPort());

        //vytváøím datastream pro ètení ze serveru
        try {
            Sinput = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            logger.error("Chyba pøi vytváøení nového input streamu: " + e);
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Metoda pro spuštìní vlákna pro ètení bajtù ze serveru. Jeliko tento
     * proces musí proíhat paraelnì s pøevádìním jednotlivıch bajtù na datové
     * bloky, muselo bıt pouito vláknového pøístupu.
     */
    @Override
    public void run() {
        // ètu data ze serveru a ukládám je do bufferu
        Byte response;
        try {
            isRunning = true;
            while (isRunning) {
                try {
                    response = Sinput.readByte();
                    buffer.addLast(response);
                } catch (Exception e) {
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("Problém pøi ètení ze serveru: " + e);
        }

        try {
            Sinput.close();
        } catch (Exception e) {
        }
    }
    
    public void requestStop(){
        isRunning = false;
    }

    /**
     * Vrátí pole bajtù o zadané velikosti. Jednotlivé bajty získává z bufferu.
     *
     * @param value pole jaké velikost potøebuji
     * @return pole bajtù o zadané velikosti
     */
    public byte[] read(int value) {
        byte[] response = new byte[value];
        for (int i = 0; i < value; i++) {
            while (true) {
                if (!buffer.isEmpty()) {
                    try {
                        response[i] = buffer.removeFirst();
                        break;
                    } catch (NoSuchElementException e) {
                        logger.error("Vıjimka pøi vytváøení nového input streamu: " + e.getMessage());
                    }
                }
            }
        }
        return response;
    }

    /**
     * Tato metoda zjišuje, jestli je zásobník prázdnı.
     *
     * @return zda-li je zásobník prázdnı
     */
    public boolean hasNext() {
        return buffer.isEmpty();
    }

}
