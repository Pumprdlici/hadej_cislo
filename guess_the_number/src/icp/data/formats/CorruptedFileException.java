package icp.data.formats;

/**
 * Výjimka signalizující poškozenou logickou strukturu naèítaného souboru.
 * @author Jiøí Kuèera
 */
public class CorruptedFileException extends Exception {
	public CorruptedFileException(String message) {
		super(message);
	}
        
        /**
         * Vyhodi vyjimku kdyz je preruseno cteni vstupu
         * @param message doplnujici chybova hlaska
         * @param e vyjimka samotna
         */
        public CorruptedFileException(String message, Exception e) {
            super(message, e);
        }
}
