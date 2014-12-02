package icp.online.app;

import org.apache.log4j.Logger;

/**
 * Název úlohy: Jednoduché BCI
 * Třída: Epocha
 * @author Bohumil Podlesák
 * První verze vytvořena: 8.3.2010
 * @version 2.0
 * 
 * Instance této třídy reprezetují jednotlivé epochy signálu.
 * Epochy je nutno upravit, protože signál může přicházet zkreslený, díky pocení subjektu.
 * V takovém případě je nutno srovnat baseline (respektive se baseline srovnává vždy).
 * Epochy se spolu dokáží průměrovat a dále zde existuje metoda na vyhodnocení podobnosti
 * s polem floatů jiného signálu.
 * Je vhodné využívat rovněž metodu, která upozorňuje na extrémní hodnoty v objektu epocha.
 * Na jejím základě se může vyhodnotit vznik artefaktu (mrknutí).
 *
 */
public class Epocha {
	/**
	 * Počet položek v poli hodnoty, které jsou před markerem.
	 */
	private int predMarkerem;
	/**
	 * Počet položek v poli hodnoty, které jsou za markerem.
	 */
	private int zaMarkerem;
	/**
	 * Hodnoty EEG signálu dané epochy.
	 */
	private float[] hodnoty;	
	/**
	 * Počet jednotlivých epoch, které vytvořily zprůměrováním instancí této epochy.
	 */
	private int pocetPrumVln;
	
	private Logger logger = Logger.getLogger(Epocha.class);
	
	/**
	 * Konstruktor nové epochy
	 * @param pocetPred - počet položek v poli hodnoty, které jsou před markerem
	 * @param pocetZa - počet položek v poli hodnoty, které jsou za markerem
	 * @param hodnoty - hodnoty EEG signálu dane Epochy
	 */
	public Epocha(int pocetPred, int pocetZa, float[] hodnoty){
		this.predMarkerem = pocetPred;
		this.zaMarkerem = pocetZa;	
		this.hodnoty = hodnoty;
		this.pocetPrumVln = 1;
	}
	
	public int getPocetPrumVln(){
		return this.pocetPrumVln;
	}
	
	public float[] getHodnoty(int odIndexu, int doIndexu){
		if((odIndexu >= this.zaMarkerem) || (doIndexu > this.zaMarkerem)
				|| (odIndexu < 0) || (doIndexu < 0)){
			logger.error("Chyba - pokus o vybrání hodnot vlny na indexech mimo rozsah epochy.");
			return null;
		}else if((doIndexu - odIndexu) < 0){
			logger.error("Chyba - obrácené pořadí indexů.");
			return null;
		}
		float[] vals = new float[doIndexu - odIndexu];
		for(int i = 0; i < doIndexu - odIndexu; i++){
			vals[i] = this.hodnoty[odIndexu + i];
		}
		return vals;
	}
	
	public int getPocetPred(){
		return this.predMarkerem;
	}
	
	public int getPocetZa(){
		return this.zaMarkerem;
	}
}
