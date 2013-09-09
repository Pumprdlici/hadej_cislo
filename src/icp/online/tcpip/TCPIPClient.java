package icp.online.tcpip;


import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 * Název úlohy: Jednoduché BCI
 * Třída: SynchronizedLinkedListByte
 * @author Michal Patočka
 * První verze vytvořena: 3.3.2010
 * @version 2.0
 * 
 * TCP/IP klient pro napojení na RDA server. Připojení je zajištěno použitím třídy
 * Socket a ošetřením patřičných výjimek. Data jsou zpracovávána po jednotlivých bajtech,
 * protože je tak server (bohužel) zasílá. Využívá vyrovnávací paměť (linkedlist) do které
 * zapisuje získané bajty ze serveru. Nadstavbou této třídy je běžně třída typu dataTokenizer,
 * která získané bajty převádí do podoby srozumitelnějších objektů. Bajty lze získat pomocí
 * metody read().
 */

public class TCPIPClient extends Thread{
	/** Datový stream příchozích bajtů. **/
	private DataInputStream Sinput;	
	/** Instance třídy socket pro navázání spojení se serverem. **/
	private Socket socket;
	/** Linked list jako vyrovnávací paměť pro získávání bajtů. **/
	private  SynchronizedLinkedListByte buffer = new SynchronizedLinkedListByte();
	/** Reference na logger událostí. **/
	private static Logger logger = Logger.getLogger(TCPIPClient.class);
	/** IP adresa, na které běží server. **/
	private String ip;
	/** Port na kterém server naslouchá. **/
	private int port;

	/**
	 * Konstruktor TCP/IP clienta. V parametrech má na jakou IP a na jaký port se napojuje.
	 * Je použit defaultní logger.
	 * @param ip na jakou ip se má připojit
	 * @param port na jakém portu má naslouchat
	 */
	public TCPIPClient(String ip,int port){
		this.ip= ip;
		this.port = port;
		BasicConfigurator.configure();
	}

	/**
	 * Metoda pro spuštění vlákna pro čtení bajtů ze serveru. Jelikož tento proces musí proíhat
	 * paraelně s převáděním jednotlivých bajtů na datové bloky, muselo být použito vláknového 
	 * přístupu.
	 */
	public void run() {

		//vytvoření instance třídy socket - napojení na server

		try {
			socket = new Socket(ip, port);
		} catch(Exception e) {
			logger.error("Error při připojování na server:" + e);
			return;
		}
		logger.debug("Připojení navázáno: " +
				socket.getInetAddress() + ":" +
				socket.getPort());

		//vytvářím datastream pro čtení ze serveru
		try{
			Sinput  = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			logger.error("Výjimka při vytváření nového input streamu: " + e);
			return;
		}

		// čtu data ze serveru a ukládám je do bufferu
		Byte response;
		try {
			while(true){
				try{
					response = Sinput.readByte();
					buffer.addLast(response);
				} catch(Exception e){
					break;
				}
			}
		} catch(Exception e) {
			logger.error("Problém při čtení ze serveru: " + e);
		}

		try{
			Sinput.close();
		} catch(Exception e) {}
	}  

	/**
	 * Vrátí pole bajtů o zadané velikosti. Jednotlivé bajty získává z bufferu.
	 * @param value pole jaké velikost potřebuji
	 * @return pole bajtů o zadané velikosti
	 */
	public byte[] read(int value){
		byte[] response = new byte[value];
		for(int i =0; i < value; i++){
			while(true){
				if(!buffer.isEmpty()){
					try{
					response[i] = buffer.removeFirst();
					break;
					} catch (NoSuchElementException e){
						//OVERFLOW
						e.printStackTrace();
					}
				} else {
					continue;
				}
			}
		}
		return response;
	}

	/**
	 * Tato metoda zjišťuje, jestli je zásobník prázdný. 
	 * @return zda-li je zásobník prázdný
	 */
	public boolean hasNext(){
		return buffer.isEmpty();
	}



}
