package icp.data;


import java.util.Arrays;

/**
 * Tøída reprezentující epochu nebo marker.
 * @author Jiøí Kuèera
 */
public class Epoch implements Comparable<Epoch> {

    /**
     * Pozice støedu epochy ve framech.
     */
    private long position;
    
    /**
     * TODO
     */
    private boolean[] selected;
    
    /**
     * TODO
     */
    private int[] weights;
    
    /**
     * Délka znaèky (ve framech).<br/>
     * Není vzdálenost od levého po pravý okraj epochy(!), ale nejspíše délka trvání události.
     */
    private int length;
    
    /**
     * Typ markeru.
     */
    private String type;
    
    /**
     * Èíslo kanálu, kterému znaèka náleží.
     * 0 - znaèka patøí všem kanálùm.
     */
    private int channelNumber;
    
    /**
     * Datum.
     * Tato položka je vyhodnocována pouze v pøípadì, že typ markeru je "New Segment".
     */
    private String date;
    
    /**
     * Popis markeru.   
     */
    private String description;

    /**
     * Vytvoøí instanci tøídy.
     * @param numberOfChannels - poèet signálù.
     */
    public Epoch(int numberOfChannels) {
        position = -1;
        selected = new boolean[numberOfChannels];
        weights = new int[numberOfChannels];
        
        initArrays();
    }
    
    /**
     * Vytvoøí instanci tøídy.
     * @param numberOfChannels - poèet signálù.
     * @param position - pozice epochy.
     */
    public Epoch(int numberOfChannels, long position) {
        this.position = position;
        selected = new boolean[numberOfChannels];
        weights = new int[numberOfChannels];
        
        initArrays();
    }
    
    private void initArrays()
    {
    	Arrays.fill(selected, false);
    	Arrays.fill(weights, 1);
    }
    
    public boolean isEpochSelected(int channelOrderInInputFile) {
        return selected[channelOrderInInputFile];
    }

    public void setEpochSelected(boolean selected, int channelOrderInInputFile) {
        this.selected[channelOrderInInputFile] = selected;
    }

    public int getEpochWeight(int channelOrderInInputFile) {
        return weights[channelOrderInInputFile];
    }

    public void setEpochWeight(int weight, int channelOrderInInputFile) {
        weights[channelOrderInInputFile] = weight;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    /**
     * @return the frame
     */
    public long getPosition() {
        return position;
    }

    /**
     * @param position 
     */
    public void setPosition(long position) {
        this.position = position;
    }

    /**
     * @return the selected
     */
    public boolean[] getSelected() {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean[] selected) {
        this.selected = selected;
    }

    /**
     * @return the weights
     */
    public int[] getWeights() {
        return weights;
    }

    /**
     * @param weights the weights to set
     */
    public void setWeights(int[] weights) {
        this.weights = weights;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getChannelNumber() {
        return channelNumber;
    }

    public void setChannelNumber(int channelNumber) {
        this.channelNumber = channelNumber;
    }

    /**
     * Slouží k øazení epoch podle umístìní.
     */
    public int compareTo(Epoch epoch)
	{
		if (this.equals(epoch))
		{
			return 0;
		}
		return (int)(this.position - epoch.getPosition());
	}

    
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
