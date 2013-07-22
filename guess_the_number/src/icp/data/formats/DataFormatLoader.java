package icp.data.formats;

import icp.data.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Rozhraní, které by melo být implementováno každým programem
 * podporovaným formátem. Vynucuje implementaci metody load, se kterou
 * pracuje rozhraní mezi datovou a aplikaèní vrstvou.
 * @author Jiøí Kuèera
 * @version 14. 11. 2007
 */
public interface DataFormatLoader {
    
    /**
     * Naète data z datového souboru do <code>BufferCreator</code>u a vrátí hlavièku <code>Header</code>.
     * Cestu k datovému souboru poskytne <code>BufferCreator</code>.
     * @param loader
     * @return Hlavièka typu <code>Header</code> s informacemi o naèteném souboru.
     * @throws java.io.IOException
     * @throws cz.zcu.kiv.jerpstudio.data.formats.CorruptedFileException
     */
    public Header load(BufferCreator loader) throws IOException, CorruptedFileException;

    /**
     * Vrací ArrayList obsahující markery.
     * @return ArrayList obsahující markery.
     */
    public ArrayList<Epoch> getEpochs();
}
