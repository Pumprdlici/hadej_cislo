package icp.online.app;

import org.apache.log4j.Logger;

/**
 * Název úlohy: Jednoduché BCI
 * Tøída: Epocha
 * @author Bohumil Podlesák
 * První verze vytvoøena: 8.3.2010
 * @version 2.0
 * 
 * Instance této tøídy reprezetují jednotlivé epochy signálu.
 * Epochy je nutno upravit, protože signál mùže pøicházet zkreslený, díky pocení subjektu.
 * V takovém pøípadì je nutno srovnat baseline (respektive se baseline srovnává vždy).
 * Epochy se spolu dokáží prùmìrovat a dále zde existuje metoda na vyhodnocení podobnosti
 * s polem floatù jiného signálu.
 * Je vhodné využívat rovnìž metodu, která upozoròuje na extrémní hodnoty v objektu epocha.
 * Na jejím základì se mùže vyhodnotit vznik artefaktu (mrknutí).
 *
 */
public class Epocha {
	/**
	 * Poèet položek v poli hodnoty, které jsou pøed markerem.
	 */
	private int predMarkerem;
	/**
	 * Poèet položek v poli hodnoty, které jsou za markerem.
	 */
	private int zaMarkerem;
	/**
	 * Hodnoty EEG signálu dané epochy.
	 */
	private float[] hodnoty;	
	/**
	 * Poèet jednotlivých epoch, které vytvoøily zprùmìrováním instancí této epochy.
	 */
	private int pocetPrumVln;
	
	private Logger logger = Logger.getLogger(Epocha.class);
	
	/**
	 * Konstruktor nové epochy
	 * @param pocetPred - poèet položek v poli hodnoty, které jsou pøed markerem
	 * @param pocetZa - poèet položek v poli hodnoty, které jsou za markerem
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
			logger.error("Chyba - obrácené poøadí indexù.");
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
