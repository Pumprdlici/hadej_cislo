package icp.data;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;


/**
 * Tøída pro abstrakci ètení dat ze souboru s využitím <code>java.nio</code> knihoven.
 * @author Jiøí Kuèera.
 */
public final class NioInputStream {

    private ByteBuffer buffer;

    private ByteOrder byteOrder;
    
    /**
     * Vytvoøí <code>NioInputStream</code> nad souborem zadaným objektem typu <code>File</code>.<br/>
     * Poøadí bytù je Big Endian.
     * @param file Vstupní soubor.
     * @throws java.io.IOException
     */
    public NioInputStream(String file) throws IOException {
        this(new File(file));
    }
    
    /**
     * Vytvoøí <code>NioInputStream</code> nad souborem zadaným názvem.
     * @param file Název nebo cesta k souboru.
     * @param byteOrder Poøadí bytù.
     * @throws java.io.IOException
     */
    public NioInputStream(String file, ByteOrder byteOrder) throws IOException {
        this(new File(file), byteOrder);
    }
    
    /**
     * Vytvoøí <code>NioInputStream</code> nad souborem zadaným názvem.<br/>
     * Poøadí bytù je Big Endian.
     * @param file Název nebo cesta k souboru.
     * @throws java.io.IOException
     */
    public NioInputStream(File file) throws IOException {
            this(new FileInputStream(file.getAbsolutePath()).getChannel());
    }
    
    /**
     * Vytvoøí <code>NioInputStream</code> nad souborem zadaným objektem typu <code>File</code>.
     * @param file Vstupní soubor.
     * @param byteOrder Poøadí bytù.
     * @throws java.io.IOException
     */
    public NioInputStream(File file, ByteOrder byteOrder) throws IOException {
            this(new FileInputStream(file.getAbsolutePath()).getChannel(), byteOrder);
    }
    
    /**
     * Vytvoøí <code>NioInputStream</code> nad otevøeným streamem typu <code>FileInputStream</code>.<br/>
     * Poøadí bytù je Big Endian.
     * @param fileInputStream Vstupní stream.
     * @throws java.io.IOException
     */
    public NioInputStream(FileInputStream fileInputStream) throws IOException {
        this(fileInputStream.getChannel());
    }
    
    /**
     * Vytvoøí <code>NioInputStream</code> nad otevøeným streamem typu <code>FileInputStream</code>.
     * @param fileInputStream Vstupní stream.
     * @param byteOrder Poøadí bytù.
     * @throws java.io.IOException
     */
    public NioInputStream(FileInputStream fileInputStream, ByteOrder byteOrder) throws IOException {
        this(fileInputStream.getChannel(), byteOrder);
    }
    
    /**
     * Vytvoøí <code>NioInputStream</code> nad kanálem zadaným objektem typu <code>FileChannel</code>.<br/>
     * Poøadí bytù je Big Endian.
     * @param fileChannel Kanál.
     * @throws java.io.IOException
     */
    public NioInputStream(FileChannel fileChannel) throws IOException {
        this(fileChannel, ByteOrder.BIG_ENDIAN);
    }

    /**
     * Vytvoøí <code>NioInputStream</code> nad kanálem zadaným objektem typu <code>FileChannel</code>.
     * @param fileChannel Kanál.
     * @param byteOrder Poøadí bytù.
     * @throws java.io.IOException
     */
    public NioInputStream(FileChannel fileChannel, ByteOrder byteOrder) throws IOException {
        buffer = ByteBuffer.allocateDirect((int) fileChannel.size());
        fileChannel.read(buffer);
        fileChannel.close();
        buffer.position(0);
        buffer.order(byteOrder);
        this.byteOrder = byteOrder;
    }

    /**
     * Vytvoøí <code>NioInputStream</code> nad polem bajtù.
     * @param input Pole bajtù, nad kterým se otevøe instance tøídy.
     */
    public NioInputStream(byte[] input) {
        buffer = ByteBuffer.wrap(input);
        buffer.position(0);
        buffer.order(ByteOrder.nativeOrder());
    }

    /**
     * Pøeète ètyøi bajty od aktuální pozice ve streamu, vrátí hodnotu float pøepoètenou podle nastaveného poøadí bytù a nastaví pozici o ètyøi bajty vpøed.
     * @return Hodnota float na aktuální pozici ve streamu.
     * @throws java.io.IOException
     */
    public float readFloat() throws java.io.IOException {
        return buffer.getFloat();
    }

    /**
     * Nastaví pozici ve streamu na zadanou hodnotu.
     * @param pos Pozice ve streamu.
     * @throws java.io.IOException
     */
    public void seek(int pos) throws IOException {
        buffer.position(pos);
    }
    
    /**
     * Vrátí poèet zbývajících bajtù ve streamu.
     * @return Poèet zbývajícíh bajtù.
     */
    public int getRemaining() {
        return buffer.remaining();
    }
    
    /**
     * Pøeète nejvýše <code>len</code> znakù ze streamu do pole bajtù.
     * Vrací poèet pøeètených znakù.
     * @param buf Pole, do kterého jsou data naètena.
     * @param off Poèáateèní pozice v poli <code>buff</code>, od které se naètou data.
     * @param len Maximální poèet pøeštených bajtù.
     * @return Poèet naètených bajtù, popø. -1 v pøípadì, že nejsou k dispozici žádná data z dùvodu dosažení konce souboru.
     * @throws EOFException 
     */
    public int read(byte[] buf, int off, int len) throws EOFException {
        int remaining = buffer.remaining();
        
        if (len <= remaining) {
            buffer.get(buf, off, len);
            return len;
        } else if (remaining > 0) {
            buffer.get(buf, off, len);
            return remaining;
        } else {
            return -1;
        }
    }
}
