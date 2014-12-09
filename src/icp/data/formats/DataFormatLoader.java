package icp.data.formats;

import icp.data.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Rozhrani, ktere by melo byt implementovano kazdym programem
 * podporovanym formatem. Vynucuje implementaci metody load, se kterou
 * pracuje rozhrani mezi datovou a aplikacni vrstvou.
 * @author Jiri Kucera
 * @version 14. 11. 2007
 */
public interface DataFormatLoader {
    
    /**
     * Nacte data z datoveho souboru do <code>BufferCreator</code>u a vrati hlavicku <code>Header</code>.
     * Cestu k datovemu souboru poskytne <code>BufferCreator</code>.
     * @param loader
     * @return Hlavicka typu <code>Header</code> s informacemi o nactenem souboru.
     * @throws java.io.IOException
     * @throws cz.zcu.kiv.jerpstudio.data.formats.CorruptedFileException
     */
    public Header load(BufferCreator loader) throws IOException, CorruptedFileException;

    /**
     * Vraci ArrayList obsahujici markery.
     * @return ArrayList obsahujici markery.
     */
    public ArrayList<Epoch> getEpochs();
}
