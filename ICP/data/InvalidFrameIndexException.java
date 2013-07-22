package icp.data;

/**
 * Výjimka vyhozená pøi chybnì zadaném indexu v <code>Buffer</code>u.
 * @author Jiøí Kuèera
 */
public class InvalidFrameIndexException extends Exception {
	public InvalidFrameIndexException(String message) {
		super(message);
	}
}
