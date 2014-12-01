package icp.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

/**
 * Tøída pro abstrakci zápisu dat do souboru s využitím <code>java.nio</code> knihoven.
 * @author Jiøí Kuèera
 */
public final class NioOutputStream {

    private final int BYTE_BUFFER_SIZE = 2 * 1024 * 1024;
    private FileChannel channel;
    private ByteBuffer buffer;
    private boolean opened;

    /**
     * Vytvoøí <code>NioOutputStream</code> nad souborem zadaným názvem.
     * Poøadí bytù je Big Endian.
     * @param file Název nebo cesta k souboru.
     * @throws FileNotFoundException 
     */
    public NioOutputStream(String file) throws FileNotFoundException {
        this(new File(file));
    }

    /**
     * Vytvoøí <code>NioOutputStream</code> nad souborem zadaným názvem.
     * @param file Název nebo cesta k souboru.
     * @param byteOrder Poøadí bytù.
     * @throws java.io.FileNotFoundException
     */
    public NioOutputStream(String file, ByteOrder byteOrder) throws FileNotFoundException {
        this(new File(file), byteOrder);
    }
    
    /**
     * Vytvoøí <code>NioOutputStream</code> nad souborem zadaným jako objekt <code>File</code>.
     * Poøadí bytù je Big Endian.
     * @param file Vstupní soubor.
     * @throws FileNotFoundException 
     */
    public NioOutputStream(File file) throws FileNotFoundException {
        this(new FileOutputStream(file));
    }

    /**
     * Vytvoøí <code>NioOutputStream</code> nad souborem zadaným jako objekt <code>File</code>.
     * @param file Vstupní soubor.
     * @param byteOrder Poøadí bytù.
     * @throws java.io.FileNotFoundException
     */
    public NioOutputStream(File file, ByteOrder byteOrder) throws FileNotFoundException {
        this(new FileOutputStream(file), byteOrder);
    }

    /**
     * Vytvoøí <code>NioOutputStream</code> nad otevøeným streamem typu <code>FileOutputStream</code>.
     * Poøadí bytù je Big Endian.
     * @param stream Vstupní stream.
     */
    public NioOutputStream(FileOutputStream stream) {
        this(stream.getChannel());
    }
    
    /**
     * Vytvoøí <code>NioOutputStream</code> nad otevøeným streamem typu <code>FileOutputStream</code>.
     * @param stream Vstupní stream.
     * @param byteOrder Poøadí bytù.
     */
    public NioOutputStream(FileOutputStream stream, ByteOrder byteOrder) {
        this(stream.getChannel(), byteOrder);
    }
    
    /**
     * Vytvoøí <code>NioOutputStream</code> nad kanálem zadaným objektem typu <code>FileChannel</code>.
     * Poøadí bytù je Big Endian.
     * @param channel Kanál.
     */
    public NioOutputStream(FileChannel channel) {
        this(channel, ByteOrder.BIG_ENDIAN);
    }

    /**
     * Vytvoøí <code>NioOutputStream</code> nad kanálem zadaným objektem typu <code>FileChannel</code>.
     * @param channel Kanál.
     * @param byteOrder Poøadí bytù.
     */
    public NioOutputStream(FileChannel channel, ByteOrder byteOrder) {
        this.channel = channel;
        buffer = ByteBuffer.allocate(BYTE_BUFFER_SIZE);
        buffer.order(byteOrder);
        opened = true;
    }

    /**
     * Zapíše data zbývající v bufferu a uzavøe výstupní soubor.
     * @throws java.io.IOException
     */
    public void close() throws IOException {
        if (!opened) {
            return;
        } else {
            opened = false;
        }
        if (channel != null && channel.isOpen()) {
            write();
            channel.force(true);
            channel.close();
        } else {
            while (buffer.position() < buffer.capacity()) {
                buffer.put((byte) 0);
            }
        }
    }

    /**
     * Zapíše hodnotu typu <code>float</code> do souboru.
     * @param f Hodnota k zápisu.
     * @throws java.io.IOException
     */
    public void writeFloat(float f) throws java.io.IOException {
        checkAndFlush(4);
        buffer.putFloat(f);
    }

    /**
     * Zkontroluje, zda je v bufferu dostatek místa pro <code>bytes</code> bytù. Pokud ne, vyprázdní buffer do výstupního souboru a vyprázdní buffer.
     * @param bytes Požadovaný poèet bytù.
     * @throws java.io.IOException
     */
    private void checkAndFlush(int bytes) throws IOException {
        if (buffer.remaining() < bytes) {
            write();
            buffer.clear();
        }
    }

    /**
     * Zapíše obsah bufferu do výstupního souboru.
     * @throws java.io.IOException
     */
    private void write() throws IOException {
        buffer.limit(buffer.position());
        buffer.position(0);
        if (channel != null && channel.isOpen()) {
            channel.write(buffer);
        }
    }
}


