package icp.online.app;

import icp.online.tcpip.DataTokenizer;
import icp.online.tcpip.TCPIPClient;
import icp.online.tcpip.objects.RDA_Marker;
import icp.online.tcpip.objects.RDA_MessageData;

import javax.swing.JTextField;

import org.apache.log4j.Logger;

/**
 * Název úlohy: Jednoduché BCI
 * Třída: OQ
 * @author Bohumil Podlesák
 * První verze vytvořena: 18.4.2010
 * @version 2.0
 * 
 * Třída, která řídí celý proces získávání a zpracování dat. Vytvoří dvě vlákna - jedno pro
 * čtení dat, druhé pro jejich dekódování. Pak začne data ve smyčce číst a zároveň s tím je
 * zpracovávat. Zpracování probíhá přídáváním hodnot z Message objektů do Bufferu. Odtud se
 * vybírají jako pole hodnot a používají se k vytvoření epoch. Epochy, které vznikly z objektů
 * příchozích po stimulu Q se zprůměrují. Po skončení experimentu jsou dostupné ve float poli
 * p300, kde je uložena P300 vlna daného subjektu.
 */
public class OQ {
	private static final int DELKABUFFERU = 3000;
	private static final int POCETHODNOTPREDEPOCHOU = 100;
	private static final int POCETHODNOTZAEPOCHOU = 1000;
	private static final int ZAKAZANYEXTREM = 350;

	/*OQ test musí proběhnout konečně-početkrát, jinak by nebylo možné předat parametry
	  do vlastního experimentu*/
	private static final int POCETSTIMULU = 150;
	/* do tohoto pole se bude průměrovat p300 */
	private float[] p300;
	private Epocha epocha;
	private Buffer buffer;
	private boolean Q = false;
	private int pocetDatBloku = 0;
	private int poradiQ = 0;
	private int poradiO = 0;
	private int pocetZapsanychVln = 0;
	private int pocetNezapsanychVln = 0;
	
	private Logger logger = Logger.getLogger(OQ.class);
	
	/**
	 * Konstruktor, který vytvoří instanci této řídící třídy.
	 * @param ip_adr - IP adresa serveru, na který se napojí client
	 * @param port - port, který se použije pro komunikaci se serverem
	 * @param log - loger pro logování událostí
	 * @param vystup - přijatý znak (ze stimulu)
	 */
	public OQ(String ip_adr, int port, JTextField vystup){
		TCPIPClient client = new TCPIPClient(ip_adr, port);
		client.start();
		DataTokenizer dtk = new DataTokenizer(client);
		dtk.start();

		/* delku bufferu je nutno zvolit libovolne vhodne */
		/* prilis kratky by nemohl slouzit pro slozeni dostatecneho mnozstvi hodnot pro epochu */
		/* prilis dlouhy by pak zabiralo zpracovani hodnot pri naplneni procesorovy cas prilis narazove */
		this.buffer = new Buffer(DELKABUFFERU,POCETHODNOTPREDEPOCHOU,POCETHODNOTZAEPOCHOU);

		int cisloStimulu = 0;
		/* kdyby se skončilo v okamžiku, kdy přišel třicátý stimul (marker), pak by data
		   pro třicátou epochu nebyla kompletní a mohlo by se vybrat pouze 29 epoch*/
		while(cisloStimulu < POCETSTIMULU + 1){
			Object o = dtk.retrieveDataBlock();
			if(o instanceof RDA_Marker){
				int cislo = ((Integer.parseInt(((RDA_Marker) o).getsTypeDesc().substring(11,13).trim()))-1);
				if(cislo == 0){
					vystup.setText("O");
					this.Q = false;
					pocetDatBloku = 0;
					poradiO++;
					/* prislo O, nic se neděje */
				} else if(cislo == 1){
					vystup.setText("Q");
					this.Q = true;
					pocetDatBloku = 0;
					poradiQ++;
					/* prislo Q, nastane průměrování */					
				} else {
					vystup.setText("E");
					throw new NumberFormatException("Příchozí marker není kompatibilní s OQ testem.");
				}
				cisloStimulu++;
			}
			if(o instanceof RDA_MessageData){
				if(Q){
					buffer.zapis((RDA_MessageData) o);
				}
				pocetDatBloku++;
			}
			if(buffer.jePlny() || (cisloStimulu > POCETSTIMULU)){
				for(HodnotyVlny data = buffer.vyber(); data != null; data = buffer.vyber()){
					Epocha epocha = new Epocha(POCETHODNOTPREDEPOCHOU,POCETHODNOTZAEPOCHOU,data.getHodnoty());
					if(!epocha.existujeArtefakt(ZAKAZANYEXTREM)){
						if(this.epocha != null){
							this.epocha.zprumeruj(epocha);
						}else{
							this.epocha = epocha;
						}
						pocetZapsanychVln++;
					}else{
						pocetNezapsanychVln++;
					}
				}
				buffer.vymaz();
			}

		}
		logger.debug("OQ TEST SKONČIL, můžete ukončit měření");

		if(this.epocha != null){
			this.p300 = this.epocha.getHodnoty(0, POCETHODNOTZAEPOCHOU);
			for(int i = 0; i < this.p300.length; i++){
				System.out.print(this.p300[i] + "f,");}
		}
	}
	public float[] getP300(){
		return this.p300;
	}
}
