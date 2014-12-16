package icp.online.app;

/**
 * Název úlohy: Jednoduché BCI
 * Tøída: HodnotyVlny
 * @author Bohumil Podlesák
 * První verze vytvoøena: 8.4.2010
 * @version 1.2
 * 
 * Tøída nahrazující v javì neexistující datový typ záznam.
 * Je nutná kvùli metodì vracející pole hodnot a typ stimulu.
 * @author Bohumil Podlesák
 */
public class HodnotyVlny {
	private float[] hodnotyFZ;
	private float[] hodnotyCZ;
	private float[] hodnotyPZ;
	private int typStimulu;
	
	public HodnotyVlny(float[] hodnotyFZ, float[] hodnotyCZ, float[] hodnotyPZ, int typStimulu){
		this.hodnotyFZ = hodnotyFZ;
		this.hodnotyCZ = hodnotyCZ;
		this.hodnotyPZ = hodnotyPZ;
		this.typStimulu = typStimulu;
	}
	
	public int getTypStimulu(){
		return this.typStimulu;
	}

	public float[] getHodnotyFZ() {
		return hodnotyFZ;
	}

	public float[] getHodnotyCZ() {
		return hodnotyCZ;
	}

	public float[] getHodnotyPZ() {
		return hodnotyPZ;
	}
}
