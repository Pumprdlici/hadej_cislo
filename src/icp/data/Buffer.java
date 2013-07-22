package icp.data;

import java.io.*;
import java.nio.BufferUnderflowException;
import java.util.Observable;

/**
 * Rozhraní mezi aplikaèní a datovou vrstvou.<br/>
 * Obstarává vyrovnávací pamì pro naèítání dat z doèasného souboru.
 * @author Jiøí Kuèera
 */
public class Buffer extends Observable {

    private final static int WORD_LENGTH = Integer.SIZE / 8;
    private File tmpFile = null;
    private NioInputStream ist;
    private int numberOfSignals;
    private long numberOfSamples = 0;
    private boolean frameLast;
    private boolean valueLast;
    private long seekPos;
    private boolean closed;
    private int numberOfParents;
    
    /**
     * Konstruktor vytvoøí Buffer nad doèasnım souborem.<br/>
     * Doèasnı soubor musí mít uloena data v binárním formátu:<br/>
     * A0, B0, C0, A1, B1, C1, A2, B2, C2...,<br/>
     * kde A, B, C (D, E...) je oznaèení signálu, 0, 1, 2... je èíslo framu.<br/>
     * A0, B0, C0... jsou hodnoty o délce <code>WORD_LENGTH</code> bytù.
     * @param tmpFile Doèasnı soubor s uloenımi daty.
     * @param numberOfSamples Poèet vzorkù (framù, samplù) signálu.
     * @param numberOfSignals Poèet signálù (kanálù).
     * @throws java.io.IOException
     */
    protected Buffer(File tmpFile, long numberOfSamples, int numberOfSignals) throws IOException {
        this.numberOfSignals = numberOfSignals;
        this.tmpFile = tmpFile;
        this.numberOfSamples = numberOfSamples;
        this.ist = new NioInputStream(tmpFile);
        frameLast = false;
        valueLast = false;
        closed = false;
        numberOfParents = 0;
    }

    /**
     * Vrátí snímek s danım indexem.
     * @param frameIndex Index snímku.
     * @return Vrací snímek na daném indexu v souboru.
     * @throws cz.zcu.kiv.jerpstudio.data.InvalidFrameIndexException
     */
    public float[] getFrame(long frameIndex) throws InvalidFrameIndexException {

        if (frameIndex < 0 || frameIndex >= numberOfSamples) {
            throw new InvalidFrameIndexException("Frame index out of data length: " + frameIndex);
        }

        float[] frame = loadFrame(frameIndex);

        frameLast = true;
        valueLast = false;

        return frame;
    }

    /**
     * Vrátí hodnotu signálu nacházející se na snímku se zadanım indexem.
     * @param channelIndex Index signálu.
     * @param frameIndex Index snímku.
     * @return Hodnota signálu na daném indexu.
     * @throws cz.zcu.kiv.jerpstudio.data.InvalidFrameIndexException
     */
    public float getValue(int channelIndex, long frameIndex) throws InvalidFrameIndexException {
        if (frameIndex < 0 || frameIndex >= numberOfSamples) {
            throw new InvalidFrameIndexException("Frame index out of data length: " + frameIndex);
        }
        if (channelIndex < 0 || channelIndex >= numberOfSignals) {
            throw new InvalidFrameIndexException("Channel index out of data length: " + channelIndex);
        }

        float value = loadValue(channelIndex, frameIndex);

        frameLast = false;
        valueLast = true;

        return value;
    }

    /**
     * Vrátí následující snímek od poslednì vráceného snímku.
     * @return Vrací následující snímek.
     * @throws cz.zcu.kiv.jerpstudio.data.InvalidFrameIndexException
     */
    public float[] getNextFrame() throws InvalidFrameIndexException {
        if (frameLast) {
            return loadNextFrame();
        } else {
            throw new InvalidFrameIndexException("Should read whole frame first.");
        }
    }
    
    /**
     * Vrátí hodnotu (stejného) signálu z následujícího snímku od naposledy ètené hodnoty.
     * @return Následující hodnota snímku pro danı naposledy ètenı kanál.
     * @throws cz.zcu.kiv.jerpstudio.data.InvalidFrameIndexException
     */
    public float getNextValue() throws InvalidFrameIndexException {
        if (valueLast) {
            return loadNextValue();
        } else {
            throw new InvalidFrameIndexException("Should read single value first.");
        }
    }

    /**
     * Metoda pro uzavøení doèasného souboru a jeho smazání.<br/>
     * Nutno zavolat pøed zrušením instance tøídy Buffer.
     * @throws IOException
     */
    public void closeBuffer() throws IOException {

        if (numberOfParents-- > 1) {
            return;
        }
        
        if (closed) {
            return;
        }
        
        if (!tmpFile.delete()) {
            throw new IOException("Could not delete temporary file: " + tmpFile.getAbsolutePath());
        }
        
        closed = true;
    }

    /**
     * Naète z doèasného souboru snímek zadanı indexem.
     * @param frameIndex index snímku.
     * @return snímek naètenı z doèasného souboru.
     */
    private float[] loadFrame(long frameIndex) {
        seekPos = frameIndex * WORD_LENGTH * numberOfSignals;

        float[] frame = new float[numberOfSignals];

        if (seekPos < 0 || seekPos >= numberOfSamples * WORD_LENGTH * numberOfSignals) {
            for (int i = 0; i < numberOfSignals; i++) {
                frame[i] = 0;
            }
            return frame;
        }

        try {
            ist.seek((int) seekPos);
        } catch (IOException e) {
            for (int i = 0; i < numberOfSignals; i++) {
                frame[i] = 0;
            }
            return frame;
        } catch (IllegalArgumentException e) {
            for (int i = 0; i < numberOfSignals; i++) {
                frame[i] = 0;
            }
            return frame;

        }

        try {
            for (int i = 0; i < numberOfSignals; i++) {
                frame[i] = ist.readFloat();
            }
        } catch (IOException e) {
            for (int i = 0; i < numberOfSignals; i++) {
                frame[i] = 0;
            }
        } catch (BufferUnderflowException e) {
            for (int i = 0; i < numberOfSignals; i++) {
                frame[i] = 0;
            }
        }

        return frame;
    }

    /**
     * Naète z doèasného souboru snímek následující za poslednì ètenım snímkem.
     * @return Vrací snímek naètenı z doèasného souboru.
     * @throws IOException
     */
    private float[] loadNextFrame() {
        float[] frame = new float[numberOfSignals];

        try {
            for (int i = 0; i < numberOfSignals; i++) {
                frame[i] = ist.readFloat();
            }
        } catch (IOException e) {
            for (int i = 0; i < numberOfSignals; i++) {
                frame[i] = 0;
            }
        }
            
        return frame;
    }

    /**
     * Naète z doèasného souboru hodnotu následující za poslednì ètenou hodnotou.
     * @return
     */
    private float loadNextValue() {
        seekPos += WORD_LENGTH * numberOfSignals;

        float value;

        if (seekPos < 0 || seekPos >= WORD_LENGTH * numberOfSamples * numberOfSignals) {
            return 0;
        }

        try {
            ist.seek((int) seekPos);
        } catch (IOException e) {
            return 0;
        } catch (IllegalArgumentException e) {
            return 0;
        }

        try {
            value = ist.readFloat();
        } catch (EOFException e) {
            value = 0;
        } catch (IOException e) {
            value = 0;
        } catch (BufferUnderflowException e) {
            value = 0;
        }

        return value;
    }

    /**
     * Naète z doèasného souboru hodnotu signálu na snímku zadaném indexem.
     * @param channelIndex Index signálu, jeho hodnota má bıt ètena.
     * @param frameIndex Index snímku, jeho hodnota má bıt pro zadanı signál ètena.
     * @return 
     */
    private float loadValue(int channelIndex, long frameIndex) {
        seekPos = WORD_LENGTH * (frameIndex * numberOfSignals + channelIndex);

        float value;

        if (seekPos < 0 || seekPos >= WORD_LENGTH * numberOfSamples * numberOfSignals) {
            return 0;
        }

        try {
            ist.seek((int) seekPos);
        } catch (IOException e) {
            return 0;
        } catch (IllegalArgumentException e) {
            return 0;
        }

        try {
            value = ist.readFloat();
        } catch (EOFException e) {
            value = 0;
        } catch (IOException e) {
            value = 0;
        } catch (BufferUnderflowException e) {
            value = 0;
        }

        return value;
    }

    /**
     * Vrací poèet signálù vèetnì synchronizaèních.
     * @return Poèet signálù
     */
    public int getNumberOfSignals() {
        return numberOfSignals;
    }

    /**
     * Metoda pro pøímé seekování v datovém úloišti. Nastaví pozici v datovém úloišti na zadanou hodnotu<br/>
     * Slouí pouze pro usnadnìní kopírování raw dat bez specifikace jejich vıznamu.
     * @param seekPos Pozice v datovém úloišti.
     * @throws java.io.IOException
     */
    protected void seek(int seekPos) throws IOException {
        ist.seek(seekPos);
    }
    
    /**
     * Vrátí další hodnotu typu <code>float</code> z datového úloištì a posune ukazatel od stávající pozice o ètyøi bajty vpøed.<br/>
     * Slouí pouze pro usnadnìní kopírování raw dat bez specifikace jejich vıznamu.
     * @return Hodnota nacházející se na aktuální pozici ukazatele.
     * @throws java.io.IOException
     */
    protected float getFloat() throws IOException {
        return ist.readFloat();
    }
    
    /**
     * Vrátí poèet zbıvajících bajtù od aktuální pozice ukazatele po konec datového úloištì.
     * Slouí pouze pro usnadnìní kopírování raw dat bez specifikace jejich vıznamu.
     * @return Poèet zbıvajících bajtù.
     */
    protected int remaining() {
        return ist.getRemaining();
    }
    
    public void addParent() {
        numberOfParents++;
    }
    
//    public void removeParent() {
//        numberOfParents--;
//    }
    
    public int getNumberOfParents() {
        return numberOfParents;
    }
    
    public boolean isClosed() {
        return closed;
    }
    
    @Override
    protected void finalize() {
        tmpFile.delete();
        closed = true;
    }
}
//FIXME osetrit ve vsech metodach presazeni konce nebo zacatku souboru (kalwi - osobne to udelam)
