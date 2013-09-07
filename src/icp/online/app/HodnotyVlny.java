package icp.online.app;

/**
 * Název úlohy: Jednoduché BCI
 * Třída: HodnotyVlny
 * @author Bohumil Podlesák
 * První verze vytvořena: 8.4.2010
 * @version 1.2
 * 
 * Třída nahrazující v javě neexistující datový typ záznam.
 * Je nutná kvůli metodě vracející pole hodnot a typ stimulu.
 * @author Bohumil Podlesák
 */
public class HodnotyVlny {
	private float[] hodnoty;
	private int typStimulu;
	
	public HodnotyVlny(float[] hodnoty, int typStimulu){
		this.hodnoty = hodnoty;
		this.typStimulu = typStimulu;
	}
	
	public float[] getHodnoty(){
		return this.hodnoty;
	}
	
	public int getTypStimulu(){
		return this.typStimulu;
	}
}
