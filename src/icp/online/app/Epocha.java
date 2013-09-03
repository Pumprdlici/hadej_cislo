package icp.online.app;

import java.io.IOException;

import presentation.Logger;

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
	
	private Logger log;
	
	/**
	 * Statická metoda, která vytvoří zprůměrovanou epochu z pole epoch.
	 * Tyto epochy musí mít stejnou délku pole před a za markerem. Mohou již být zprůměrované.
	 * @param epochy - pole epoch, které bude zprůměrováno
	 * @return nová epocha vzniklá zprůměrováním epoch v poli
	 */
	public static Epocha zprumerujEpochy(Epocha[] epochy){
		for(int i = 1; i < epochy.length; i++){
			if((epochy[0].predMarkerem != epochy[i].predMarkerem) && (epochy[0].zaMarkerem != epochy[i].zaMarkerem)){
				try {
					throw new IOException("Chyba, pokus o prumerovani ruzne dlouhych epoch!");
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
		}
		
		//vytvori se pomocne pole doubleu, ktere se bude pouzivat pri vypoctu vazeneho prumeru
		//diky nasebeni koeficienty vazeneho prumeru bychom se mohli dostat mimo datovy typ float
		//po vydeleni se opet dostaneme na nizsi hodnoty
		double[] vysledneDoubleHodnoty = new double[epochy[0].predMarkerem + epochy[0].zaMarkerem];
		//vynulovani
		for(int i = 0; i < vysledneDoubleHodnoty.length; i++){
			vysledneDoubleHodnoty[i] = 0;
		}
		
		//citatel zlomku vazeneho prumeru
		for(int j = 0; j < epochy.length; j++){
			for(int i = 0; i < vysledneDoubleHodnoty.length; i++){
				vysledneDoubleHodnoty[i] += epochy[j].hodnoty[i] * epochy[j].pocetPrumVln;
			}
		}
		
		//vypocteni vahy
		int vaha = 0;
		for(int j = 0; j < epochy.length; j++){
			vaha += epochy[j].pocetPrumVln;
		}
		//lomeni vahou
		for(int i = 0; i < vysledneDoubleHodnoty.length; i++){
			vysledneDoubleHodnoty[i] /= vaha;
		}
		//pretypovani na pole floatu
		float[] vysledneFloatHodnoty = new float[epochy[0].predMarkerem + epochy[0].zaMarkerem];
		for(int i = 0; i < vysledneDoubleHodnoty.length; i++){
			vysledneFloatHodnoty[i] = (float)vysledneDoubleHodnoty[i];
		}
		
		return new Epocha(epochy[0].predMarkerem, epochy[0].zaMarkerem, vysledneFloatHodnoty, vaha, epochy[0].getLog());
	}
	
	/**
	 * Konstruktor nové epochy
	 * @param pocetPred - počet položek v poli hodnoty, které jsou před markerem
	 * @param pocetZa - počet položek v poli hodnoty, které jsou za markerem
	 * @param hodnoty - hodnoty EEG signálu dane Epochy
	 */
	public Epocha(int pocetPred, int pocetZa, float[] hodnoty, Logger log){
		this.predMarkerem = pocetPred;
		this.zaMarkerem = pocetZa;	
		this.log = log;
		this.hodnoty = hodnoty;
		this.pocetPrumVln = 1;
		srovnej();
	}
	
	/**
	 * Konstruktor nové epochy
	 * @param pocetPred - počet položek v poli hodnoty, které jsou před markerem
	 * @param pocetZa - počet položek v poli hodnoty, které jsou za markerem
	 * @param hodnoty - hodnoty EEG signálu dané Epochy
	 * @param vaha - váha vlny (počet vln, které byly použity na zprůměrování této vlny)
	 */
	public Epocha(int pocetPred, int pocetZa, float[] hodnoty, int vaha, Logger log){
		this.predMarkerem = pocetPred;
		this.zaMarkerem = pocetZa;
		this.log = log;
		for(int i = 0; i < (this.predMarkerem + this.zaMarkerem); i++){
			this.hodnoty[i] = hodnoty[i];
		}		
		this.pocetPrumVln = vaha;
		srovnej();
	}
	
	public Logger getLog(){
		return this.log;
	}
	
	public int getPocetPrumVln(){
		return this.pocetPrumVln;
	}
	
	public float[] getHodnoty(int odIndexu, int doIndexu){
		if((odIndexu >= this.zaMarkerem) || (doIndexu > this.zaMarkerem)
				|| (odIndexu < 0) || (doIndexu < 0)){
			log.log("Chyba - pokus o vybrání hodnot vlny na indexech mimo rozsah epochy.");
			return null;
		}else if((doIndexu - odIndexu) < 0){
			log.log("Chyba - obrácené pořadí indexů.");
			return null;
		}
		float[] hodnoty = new float[doIndexu - odIndexu];
		for(int i = 0; i < doIndexu - odIndexu; i++){
			hodnoty[i] = this.hodnoty[odIndexu + i];
		}
		return hodnoty;
	}
	
	public int getPocetPred(){
		return this.predMarkerem;
	}
	
	public int getPocetZa(){
		return this.zaMarkerem;
	}
	
	/**
	 * Zpruměruje novou vlnu se stávající
	 * @param dalsiEpocha - vlna, se kterou se průměrování bude provádět; tato vlna může již
	 * sama být zprůměrovaná
	 */
	public void zprumeruj(Epocha dalsiEpocha){
		if((this.predMarkerem != dalsiEpocha.getPocetPred()) && (this.zaMarkerem != dalsiEpocha.getPocetZa())){
			log.log("Chyba, pokus o prumerovani ruzne dlouhych epoch!");
			return;
		}
		
		//vytvori se pomocne pole intu, ktere se bude pouzivat pri vypoctu vazaneho prumeru
		//diky nasebeni koeficienty vazeneho prumeru bychom se mohli dostat mimo datovy typ float
		//po vydeleni se opet dostaneme na nizsi hodnoty
		double[] vysledneIntHodnoty = new double[this.hodnoty.length];
		
		//vazeny prumer
		for(int i = 0; i < this.hodnoty.length; i++){
			vysledneIntHodnoty[i] = (this.hodnoty[i] * this.pocetPrumVln +
				dalsiEpocha.hodnoty[i] * dalsiEpocha.pocetPrumVln) / (this.pocetPrumVln + dalsiEpocha.pocetPrumVln);
		}
		//prehrani prumeru do teto epochy
		for(int i = 0; i < this.hodnoty.length; i++){
			this.hodnoty[i] = (float)vysledneIntHodnoty[i];
		}
		//tato vlna bude mit ted spolecnou vahu obou vln
		this.pocetPrumVln += dalsiEpocha.pocetPrumVln;
	}
	
	/**
	 * Provede srovnáni hodnot pole do nové base line.
	 * Tato metoda bude používat metodu nejmenších čtverců.
	 * Spočte se rovnice přímky z dat uložených v hodnoty[0] - hodnoty[pred].
	 * Tato přímka (její hodnoty) se pak odečtou z celého rozsahu hodnot
	 * (hodnoty[0] - hodnoty[pred+za])
	 * 
	 * x ~ i
	 * y ~ hodnoty[i]
	 * 
	 * primka = koeficientA * parametr + koeficientB
	 * 
	 */
	private void srovnej(){
		double koeficientA, koeficientB;		
		/*
		 *    n*suma(xi*yi) - suma(xi)*suma(yi)
		 *a = --------------------------------
		 *      n*suma(xi^2) - (suma(xi))^2
		 */
		double sumaXxY = 0;
		for(int i = 0; i < this.predMarkerem; i++){
			sumaXxY += i * this.hodnoty[i];
		}
		/* sumaX nemusí být double, mohlo by to tak být rychlejší */
		/* nemusí se tak často provádět přetypování */
		int sumaX = 0;
		double sumaY = 0;
		for(int i = 0; i < this.predMarkerem; i++){
			sumaX += i;
			sumaY += this.hodnoty[i];
		}
		double sumaXxsumaY = sumaX * sumaY;
		double citatel = this.predMarkerem * sumaXxY - sumaXxsumaY;
		/* sumaX2 nemusí být double, mohlo by to tak být rychlejší */
		/* nemusí se tak často provádět přetypování */
		int sumaX2 = 0;
		for(int i = 0; i < this.predMarkerem; i++){
			sumaX2 += i * i;
		}
		double jmenovatel = this.predMarkerem * sumaX2 - sumaX * sumaX;		
		koeficientA = citatel / jmenovatel;		
		/*
		 *    suma(xi^2)*suma(yi) - suma(xi)*suma(xi*yi)
		 *b = -----------------------------------------
		 *          n*suma(xi^2) - (suma(xi))^2
		 */		
		citatel = sumaX2 * sumaY - sumaX * sumaXxY;
		/* jmenovatel zustava stejny */		
		koeficientB = citatel / jmenovatel;		
		/* odecteni primky - srovnani baseline */
		for(int i = 0; i < (this.predMarkerem + this.zaMarkerem); i++){
			this.hodnoty[i] -= (float)(koeficientA * i + koeficientB);
		}
	}
	
	/**
	 * Porovná epochu s danou vlnou.
	 * Není možné porovnávat vlnu s tou částí epochy, která je před markerem.
	 * @param vlna - pole floatu, ve kterém je uložena vlna
	 * @param odIndexu - index, na kterém se může poprvé zaznamenat shoda epochy s vlnou;
	 * tento index je číslován od markeru epochy a značí první porovnávání s vlna[0]
	 * @param doIndexu - index, před kterým jako posledním může být nalezena shoda;
	 * tento index je číslován od markeru epochy a značí místo, kde již nebude probíhat
	 * porovnávání s vlna[vlna.length]
	 * @return - vrací hodnotu, která určuje míru podobnosti epochy na zadaném
	 * intervalu se zadanou vlnou (uloženou v poli float)
	 * pokud nastane chyba, metoda vypíše chybovou hlášku a vrátí -Double.MAX_VALUE
	 */
	public double porovnej(float[] vlna, int odIndexu, int doIndexu){
		double nejvetsiSoucet = -Double.MAX_VALUE;		
		if((odIndexu >= this.zaMarkerem) || (doIndexu > this.zaMarkerem)
				|| (odIndexu < 0) || (doIndexu < 0)){
			log.log("Chyba - pokus o porovnavani vlny s epochou na indexech mimo rozsah epochy");
			return nejvetsiSoucet;
		}else if((doIndexu - odIndexu) < vlna.length){
			log.log("Chyba - pokus o porovnavani vlny s epochou na indexech, jejichz rozpeti je mensi nez delka vlny");
			return nejvetsiSoucet;
		}
		int pocetOpakovani = doIndexu - odIndexu - vlna.length;
		for(int j = 0; j <= pocetOpakovani; j++){
			double soucet = 0;
			for(int i = odIndexu; i < vlna.length; i++){
				soucet += vlna[i] * this.hodnoty[this.predMarkerem + i + j];
			}
			if(nejvetsiSoucet < soucet){
				nejvetsiSoucet = soucet;
			}
		}
		return nejvetsiSoucet;
	}
	
	/**
	 * Vyhodnotí, zda se v poli vyskytuje artefakt, na základě výskytu extrémní hodnoty.
	 * @param extremniHodnota - maximální možná vzdálenost od nuly všech hodnot v epoše
	 * pokud je zadáno záporné číslo, převede se na kladné
	 * @return pokud se vyskytuje extrémní hodnota true, jinak false
	 */
	public boolean existujeArtefakt(float extremniHodnota){
		extremniHodnota = Math.abs(extremniHodnota);
		for(int i = 0; i < this.hodnoty.length; i++){
			if(Math.abs(this.hodnoty[i]) > extremniHodnota){
				return true;
			}
		}
		return false;
	}
}
