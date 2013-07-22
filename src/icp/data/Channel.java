package icp.data;

/**
 *	Tøída uchovávající informace o jednom kanálu (signálu).
 */
public class Channel implements Cloneable {

    private static final int MICROSECONDS = 1000000;
    /**
     * Name of sensor.
     */
    private String name;

    //Values.
    private short nV_bit;
    private String edfTransducerType;
    private String unit;
    private String edfPrefiltering;

    /**
     * Frekvence vzorku signalu v Hz.
     */
    private float frequency;
    /**
     * Perioda vzorku signalu v mikrosekundach.
     */
    private float period;
    /**
     * Urcuje, ktera z hodnot frequency nebo period je puvodni.
     * Druha hodnota je dopocitana.
     */
    private String original;

    public Channel() {
        this.name = "unknown";
        this.nV_bit = 0;
        this.edfTransducerType = "unknown";
        this.unit = "unknown";
        this.edfPrefiltering = "unknown";
        this.frequency = 0;
        this.period = 0;
        this.original = "unknown";
    }

    /**
     * Pøekrytí metody toString() zdìdìné od tøídy Object.
     */
    @Override
    public String toString() {
//        return name + "\t" + nV_bit + "\t" + edfTransducerType + "\t" + unit + "\t" + edfPrefiltering + "\t" + edfPhysicalMinimum + "\t" + edfPhysicalMaximum + "\t" + edfDigitalMinimum + "\t" + edfDigitalMaximum + "\t";
        return name + "\t" + nV_bit + "\t" + edfTransducerType + "\t" + unit + "\t" + edfPrefiltering;
    }


    /**
     * @return the edfPhysicalDimension
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @param unit 
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * @return the edfPrefiltering
     */
    public String getEdfPrefiltering() {
        return edfPrefiltering;
    }

    /**
     * @param edfPrefiltering
     *            the edfPrefiltering to set
     */
    public void setEdfPrefiltering(String edfPrefiltering) {
        this.edfPrefiltering = edfPrefiltering;
    }

    /**
     * @return the edfTransducerType
     */
    public String getEdfTransducerType() {
        return edfTransducerType;
    }

    /**
     * @param edfTransducerType
     *            the edfTransducerType to set
     */
    public void setEdfTransducerType(String edfTransducerType) {
        this.edfTransducerType = edfTransducerType;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the nV_bit
     */
    public short getNV_bit() {
        return nV_bit;
    }

    /**
     * @param nv_bit
     *            the nV_bit to set
     */
    public void setNV_bit(short nv_bit) {
        nV_bit = nv_bit;
    }

    /**
     * @return the frequency
     */
    public float getFrequency() {
        return frequency;
    }

    /**
     * @param frequency - the frequency to set.
     */
    public void setFrequency(float frequency) {
        this.frequency = frequency;
        this.period = MICROSECONDS / frequency;
        this.original = "frequency";
    }

    /**
     * @return the original
     */
    public String getOriginal() {
        return original;
    }

    /**
     * @param original - the original to set.
     */
    public void setOriginal(String original) {
        this.original = original;
    }

    /**
     * @return the period
     */
    public float getPeriod() {
        return period;
    }

    /**
     * @param period - the period to set.
     */
    public void setPeriod(float period) {
        this.period = period;
        this.frequency = MICROSECONDS / period;
        this.original = "period";
    }
    
    public void setPeriodOnly(float period) {
        this.period = period;
    }
    
    public void setFrequencyOnly(float frequency) {
        this.frequency = frequency;
    }
    
    /**
     * Vytvoøí hlubokou kopii tøídy.
     * @return Hluboká kopie tøídy.
     */
    @Override
    public Channel clone() {
        Channel channel;
        try {
            channel = (Channel) super.clone();
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
            return null;
        }
        return channel;
    }
}
