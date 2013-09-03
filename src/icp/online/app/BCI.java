package icp.online.app;

import javax.swing.JTextField;
import javax.swing.JLabel;

import icp.online.tcpip.DataTokenizer;
import icp.online.tcpip.TCPIPClient;
import icp.online.tcpip.objects.RDA_Marker;
import icp.online.tcpip.objects.RDA_MessageData;

/**
 * Název úlohy: Jednoduché BCI
 * Třída: BCI
 * @author Bohumil Podlesák
 * První verze vytvořena: 25.3.2010
 * @version 2.0
 * 
 * Třída, která řídí celý proces získávání a zpracování dat. Vytvoří dvě vlákna - jedno pro
 * čtení dat, druhé pro jejich dekódování. Pak začne data ve smyčce číst a zároveň s tím je
 * zpracovávat. Zpracování probíhá přídáváním hodnot z Message objektů do Bufferu. Odtud se
 * vybírají jako pole hodnot a používají se k vytvoření epoch. Epochy, patřící do stejné
 * kategorie (podle stimulu) se zprůměrují. Index epochy, která se podobá P-300 vlně se zapíše
 * do třídy Zaposivac.
 */
public class BCI{
	private static final int DELKABUFFERU = 10000;
	private static final int POCETHODNOTPREDEPOCHOU = 100;
	private static final int POCETHODNOTZAEPOCHOU = 1000;
	private static final int SHODA = 400000;
	private static final int ZAKAZANYEXTREM = 350;

	/**
	 *  Počet stimulů, po jakém se zastaví hlavní test.
	 */
	private static final int POCETSTIMULU = 150;

	/**
	 * Pole, ve kterém budou uloženy průměrné hodnoty EEG signálu pro jednotlivé čislice
	 * index pole označuje dané čislo (0-9)
	 */
	private Epocha[] epochaCisla;

	/**
	 * Zapisovač pro ukládání počtu podobností jednotlivých (zprůměrovaných) epoch s P-300 vlnou.
	 */
	private Zapisovac zapisovac;


	/**
	 * Bufer, který se bude používat pro ukládání hodnot z datových objektů RDA_MessageData
	 */
	private Buffer buffer;

	private float[] p300;

	/**
	 * Konstruktor, který vytvoří instanci této řídící třídy.
	 * @param ip_adr - IP adresa serveru, na který se napojí client
	 * @param port - port, který se použije pro komunikaci se serverem
	 * @param log - loger pro logování událostí
	 * @param vystup - přijaté číslo (ze stimulu)
	 * @param reakce reference na zapisovač reakcí
	 * @param p300 - 'idealni' P300 vlna
	 * Tato vlna se nastaví pomocí O-Q testu, a náísledně se používá k porovnávání s
	 * průměrnými epochami číslic
	 */
	public BCI(String ip_adr, int port, Logger log, JTextField vystup, float[] p300, JLabel[] reakce){
		TCPIPClient client = new TCPIPClient(ip_adr, port, log);
		client.start();
		DataTokenizer dtk = new DataTokenizer(client, log);
		dtk.start();

		this.p300 = p300;

		this.epochaCisla = new Epocha[10];

		/* delku bufferu je nutno zvolit libovolne vhodne */
		this.buffer = new Buffer(DELKABUFFERU,POCETHODNOTPREDEPOCHOU,POCETHODNOTZAEPOCHOU);

		/* pokud zaznamenám reakci na dané číslo tak použiji třídu zapisovač a zaznamenám ji */
		this.zapisovac = new Zapisovac(log, reakce);

		int cisloStimulu = 0;
		while(cisloStimulu < POCETSTIMULU + 1){
			Object o = dtk.retrieveDataBlock();
			if(o instanceof RDA_Marker){
				/* takto získám příchozí číslo z markeru */
				int cislo = ((Integer.parseInt(((RDA_Marker) o).getsTypeDesc().substring(11,13).trim()))-1);
				vystup.setText("" + cislo);
				cisloStimulu++;
			}
			if(o instanceof RDA_MessageData){
				buffer.zapis((RDA_MessageData) o);

			}
			if(buffer.jePlny() || (cisloStimulu > POCETSTIMULU)){
				for(HodnotyVlny data = buffer.vyber(); data != null; data = buffer.vyber()){
					Epocha epocha = new Epocha(POCETHODNOTPREDEPOCHOU,POCETHODNOTZAEPOCHOU,data.getHodnoty(),log);
					if(!epocha.existujeArtefakt(ZAKAZANYEXTREM)){
						if(this.epochaCisla[data.getTypStimulu()] != null){
							this.epochaCisla[data.getTypStimulu()].zprumeruj(epocha);
						}else{
							this.epochaCisla[data.getTypStimulu()] = epocha;
						}
					}
				}
				buffer.vymaz();
				for(int i = 0; i < this.epochaCisla.length; i++){
					if(this.epochaCisla[i] != null){
						double shodujeSeNa = this.epochaCisla[i].porovnej(this.p300, 0, POCETHODNOTZAEPOCHOU);
						if(shodujeSeNa > SHODA){
							this.zapisovac.zaznamenejReakci(i);
						}
					}
				}
			}
		}
		int nejvetsiReakce = this.zapisovac.getNejvetsiReakci();

		vystup.setText("" + nejvetsiReakce);
		log.log("EXPERIMENT SKONČIL, můžete ukončit měření");
		log.log("Nejsilnější reakce byla zaznamenána na číslo " + nejvetsiReakce);
	}
}

