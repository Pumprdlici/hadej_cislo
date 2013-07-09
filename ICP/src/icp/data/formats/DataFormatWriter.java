package icp.data.formats;


import icp.data.*;

import java.io.File;
import java.io.IOException;

/**
 * Rozhraní modulù pro ukládání datových souborù.
 * @author Jiøí Kuèera
 */
public interface DataFormatWriter {
    /**
     * Zapíše data do souboru.
     * @param header Hlavièka s informacemi o souboru.
     * @param buffer Tøída s daty.
     * @param outputFile Výstupní soubor.
     * @throws java.io.IOException
     */
    public void write(Header header, Buffer buffer, File outputFile)
            throws IOException;

}
