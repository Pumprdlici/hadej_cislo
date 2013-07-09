package icp.data;

import java.util.*;
import java.util.GregorianCalendar;
import java.util.ArrayList;

/**
 * Tøída uchovávající informace o naèteném souboru.
 */
public class Header implements Cloneable {

    /**
     * Informace o jednotlivych kanalech.
     */
    private List<Channel> channels;
    /**
     * Pocet vzorku celkem.
     */
    private long numberOfSamples;
    /**
     * Slouží pro uchování data a èasu, kdy došlo k vytvoøení zpracovávaného souboru (je získáváno z dat v souboru)
     */
    private GregorianCalendar dateOfAcquisition;
    /**
     * Vzorkovaci interval v mikrosekundach, tzn. pocet mikrosekund mezi dvema snimky.
     */
    private float samplingInterval;
    /**
     * Delka segmentu.
     */
    private int segmentLength;
    /**
     * Rodne cislo. (pod personal number ale muze byt i jine cislo... kori)
     */
    private String personalNumber;
    /**
     * Jméno a pøíjmení (v tomto poøadí) mìøené osoby (pacienta) v jednom øetìzci oddìlené mezerou
     */
    private String subjectName;
    /**
     * Jméno a pøíjmení lékaøe v jednom øetìzci
     */
    private String docName;
    /**
     * Délka mìøení
     */
    private String length;

    /**
     * Konstruktor tridy Header.java.
     */
    public Header() {
        numberOfSamples = Integer.MIN_VALUE;
        dateOfAcquisition = new GregorianCalendar();
        samplingInterval = Integer.MIN_VALUE;
        segmentLength = Integer.MIN_VALUE;
        personalNumber = "";
        subjectName = "";
        docName = "";
        length = "";
        channels = null;
    }

    /**
     * deklaruje pole senzorù
     * @param point 
     */
    public void addChannel(Channel point) {
        if (channels == null) {
            channels = new ArrayList<Channel>();
        }
        channels.add(point);
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    /**
     * @return pole senzorù 
     */
    public List<Channel> getChannels() {
        if (channels == null) {
            channels = new ArrayList<Channel>();
        }
        return channels;
    }

    /**
     * @return the docName
     */
    public String getDocName() {
        return docName;
    }

    /**
     * @param docName the docName to set
     */
    public void setDocName(String docName) {
        this.docName = docName;
    }

    /**
     * @return the numberOfChannels
     */
    public int getNumberOfChannels() {
        return channels.size();
    }

    /**
     * @return the numberOfSamples
     */
    public long getNumberOfSamples() {
        return numberOfSamples;
    }

    /**
     * @param numberOfSamples the numberOfSamples to set
     */
    public void setNumberOfSamples(long numberOfSamples) {
        this.numberOfSamples = numberOfSamples;
    }

    /**
     * @return the personalNumber
     */
    public String getPersonalNumber() {
        return personalNumber;
    }

    /**
     * @param personalNumber the personalNumber to set
     */
    public void setPersonalNumber(String personalNumber) {
        this.personalNumber = personalNumber;
    }

    /**
     * Vrací vzorkovací interval v mikrosekundách, tzn. poèet mikrosekund mezi dvìma snímky.
     * @return Vzorkovací interval v mikrosekundách, tzn. poèet mikrosekund mezi dvìma snímky.
     */
    public float getSamplingInterval() {
        return samplingInterval;
    }

    /**
     * Nastavuje vzorkovací interval v mikrosekundách, tzn. poèet mikrosekund mezi dvìma snímky.
     * @param samplingInterval Vzorkovací interval v mikrosekundách, tzn. poèet mikrosekund mezi dvìma snímky.
     */
    public void setSamplingInterval(float samplingInterval) {
        this.samplingInterval = samplingInterval;
    }

    /**
     * @return the segmentLength
     */
    public int getSegmentLength() {
        return segmentLength;
    }

    /**
     * @param segmentLength the segmentLength to set
     */
    public void setSegmentLength(int segmentLength) {
        this.segmentLength = segmentLength;
    }

    /**
     * Vrací hodnotu atributu subjectName
     * @return jméno a pøíjmení mìøené osoby (pacienta) v jednom øetìzci
     */
    public String getSubjectName() {
        return subjectName;
    }

    /**
     * Nastavuje hodnotu atributu subjectName
     * @param subjectName jméno a pøíjmení mìøené osoby (pacienta) v jednom øetìzci
     */
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    /**
     * Vrací objekt atributu calendar
     * @return objekt tøídy java.util.Calendar
     */
    public GregorianCalendar getDateOfAcquisition() {
        return dateOfAcquisition;
    }

    /**
     * Nastavuje atribut calendar
     * @param date 
     */
    public void setDateOfAcquisition(GregorianCalendar date) {
        //Debug.print("Gregorian Calendar: " + gregorianCalendar + this.getCalendar());
        this.dateOfAcquisition = date;
    }

    /**
     * Vrací øetìzec èasu délky mìøení
     * @return øetìzec èasu délky mìøení
     */
    public String getLength() {
        if (length == null || length.equals("")) {
            long l = (long) (numberOfSamples * (double) samplingInterval) / 1000;
            String hours = String.valueOf((int) (l / 3600 / 1000));
            String minutes = String.valueOf((int) (l % (3600 * 1000)) / 60000);
            String seconds = String.valueOf((int) ((l % (3600 * 1000) % 60000) / 1000));
            String millis = String.valueOf((int) ((l % (3600 * 1000) % 60000) % 1000));
            
            while (hours.length() < 2) {
                hours = "0" + hours;
            }
            
            while (minutes.length() < 2) {
                minutes = "0" + minutes;
            }
            
            while (seconds.length() < 2) {
                seconds = "0" + seconds;
            }
            
            while (millis.length() < 3) {
                millis = "0" + millis;
            }
            length = hours + ":" + minutes + ":" + seconds + "." + millis;
        }
        return length;
    }

    /**
     * Nastavuje atribut èasu délky mìøení.
     * @param length Nastavuje øetìzec vyjadøující délku mìøení.
     */
    public void setLength(String length) {
        this.length = length;
    }

    /**
     * Metoda equals porovná objet s jiným zadaným, vrací true, pokud jsou shodné
     * shodné jsou v této implementaci, pokud se shodují ve všech svých atributech
     * @param obj porovnávaný objekt
     * @return true v pøípadì shody, jinak false
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Header)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        Header h = (Header) obj;

        return docName == null ? h.docName == null : docName.equals(h.docName) &&
                dateOfAcquisition == null ? h.dateOfAcquisition == null : dateOfAcquisition.equals(h.dateOfAcquisition) &&
                channels == null ? h.channels == null : channels.equals(h.channels) &&
                length == null ? h.length == null : length.equals(h.length) &&
                numberOfSamples == h.numberOfSamples &&
                personalNumber == null ? h.personalNumber == null : personalNumber.equals(h.personalNumber) &&
                samplingInterval == h.samplingInterval &&
                segmentLength == h.segmentLength &&
                subjectName == null ? h.subjectName == null : subjectName.equals(h.subjectName);
    }

    /**
     * Vypoèítá nejvhodnìjší hashovací funkci pro danou instanci
     * @return hash code
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + (docName == null ? 0 : docName.hashCode());
        result = 37 * result + (dateOfAcquisition == null ? 0 : dateOfAcquisition.hashCode());
        result = 37 * result + (channels == null ? 0 : channels.hashCode());
        result = 37 * result + (length == null ? 0 : length.hashCode());
        result = 37 * result + (int) (numberOfSamples ^ (numberOfSamples >>> 32));
        result = 37 * result + (personalNumber == null ? 0 : personalNumber.hashCode());
        result = 37 * result + Float.floatToIntBits(samplingInterval);
        result = 37 * result + segmentLength;
        result = 37 * result + (subjectName == null ? 0 : subjectName.hashCode());
        return result;
    }

    /**
     * Vytvoøí hlubokou kopii tøídy.
     * @return Hluboká kopie tøídy.
     */
    @Override
    public Header clone() {
        Header header;
        try {
            header = (Header) super.clone();
        } catch(CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
        
        header.dateOfAcquisition = (GregorianCalendar) dateOfAcquisition.clone();

        List<Channel> newChannels = new ArrayList<Channel>();
        for (Channel ch : channels) {
            newChannels.add(ch.clone());
        }
        
        header.channels = newChannels;
        
        return header;
    }

    @Override
    public String toString() {

        String str = "size: " + (channels == null ? "null" : channels.size()) + ", number of samples: " + numberOfSamples + ", milis from Epoch: " + dateOfAcquisition.getTimeInMillis() + ", sampling interval: " + samplingInterval + ", segment length: " + segmentLength + ", personal number: " + personalNumber + ", subject name: " + subjectName + ", doc name: " + docName + ", length: " + length;

        return str;
    }
}
