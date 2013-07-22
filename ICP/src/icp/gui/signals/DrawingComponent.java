package icp.gui.signals;


//import static java.awt.Container.dbg;
import icp.Const;
import icp.data.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.swing.JComponent;


/**
 * Komponenta zajišující vykreslování EEG signálu, epoch, artefaktù apod.
 * @author Jiøí Kuèera
 */
public class DrawingComponent extends JComponent implements Runnable {

    private static final int NANOSEC_IN_MIKROSEC = 1000;    // Poèet nanosekund v mikrosekundì

    private static final int DEFAULT_DRAWED_RANGE = 10000;  // Vıchozí délka vykreslovaného úseku v milisekundách.

    private static final int NO_DELAYS_PER_YIELD = 16;  // Minimální poèet cyklù pøed tím, ne je umonìno pokraèovat jinım vláknùm.

    private Thread animator;    // Vlákno pro vıpoèet a vykreslení signálù.

    private volatile boolean running = false;   // Urèuje, zda je spuštìno animaèní vlákno

    private volatile boolean paused = true;     // Urèuje, zda je zapauzováno animaèní vlákno

    private long period;    // Perioda snímkù v _nanosekundách_

    /**
     * TEAM: zde jsou ulozena data, bude to chtit tohle nahradit nejakym objektem z aplikacni vrstvy, z ktereho se data budou dolovat  
     */
    private Buffer buffer;
    private Header header;
    private SignalsWindowProvider signalsWindowProvider = null;
    private SignalsWindow winSignals; //okno se zobrazováním drawingComponenty

    private int[] drawedEpochs;
    private boolean[] drawedArtefacts;
    private int numberOfSignals;    // Poèet signálù k vykreslení (vèetnì signálù, které nejsou kvùli odzoomování vidìt).

    private volatile long startPos = 0; // Index poèáteèního framu (od zaèátku souboru).

    private volatile long playbackIndicatorPosition = 0;    // Index framu, na kterém se nachází ukazatel pøehrávání.

    private float vZoom = 10;
    private int skips = 0;  // Poèet pøeskoèení vykreslování.

    private int gridStep;   // Vzdálenost mezi linkami èasové møíky v milisekundách.

    private int drawedFrames;   // Délka vykreslovaného useku ve framech.

    private float framesStep;   // Vzdálenost mezi framy v pixelech.

    private Font font;
    private Font gridFont;
    private Graphics graphics;
    private BufferedImage dbImage = null;
    private int[] drawableChannels;
    private float dMax;
    private boolean invertedSignals;
    private boolean cursorExitedComponent = true;
    private int firstVisibleSignal; // Index prvního vykresleného signálu.

    private int numberOfVisibleSignals; // Poèet zobrazenıch signálù.


    /**
     * Vytvoøí instanci objektu typu <code>DrawingComponent</code>.
     * @param signalsWindowProvider Objekt typu <code>SignalsWindowProvider</code>
     */
    public DrawingComponent(final SignalsWindowProvider signalsWindowProvider) {
        this.signalsWindowProvider = signalsWindowProvider;
        buffer = null;
        header = null;

        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    signalsWindowProvider.setPressedPosition(getAbsolutePosition(e.getX()));
                    signalsWindowProvider.setStartSelection(e.getX());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3 && !cursorExitedComponent) {
                    signalsWindowProvider.setPopupmenu(DrawingComponent.this, e.getX(), e.getY(), getAbsolutePosition(e.getX()));
                }
                refresh();

                if (paused && !cursorExitedComponent) {
                    paintTimeCursor(e);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                cursorExitedComponent = false;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                cursorExitedComponent = true;

                if (paused && !signalsWindowProvider.getOptionMenu().isShowing()) {
                    refresh();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                if (header != null) {
                    if (paused && !signalsWindowProvider.getOptionMenu().isShowing()) {
                        refresh();
                        paintTimeCursor(e);
                    }
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (header != null) {

                    if (paused) {
                        signalsWindowProvider.setEndSelection(e.getX());
                        refresh();
                        paintTimeCursor(e);
                    }
                }
            }
        });

        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                gridStep = getGridRange();
                framesStep = getWidth() / (float) drawedFrames;
                dMax = (float) getHeight() / numberOfVisibleSignals;
                refresh();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                refresh();
            }
        });


        font = new Font("SansSerif", Font.BOLD, 24);
        gridFont = new Font(Const.DC_GRID_FONT_FAMILY, Const.DC_GRID_FONT_STYLE, Const.DC_GRID_FONT_SIZE);
        invertedSignals = false;
    }

    /**
     * Nastaví zdroj dat a metainformací.
     * @param buffer Zdroj dat.
     * @param header Zdroj metainformací.
     */
    public void setDataSource(Buffer buffer, Header header) {
        // TODO - predelat tak, aby to vyhovovalo chybe pri nacitani projektu
        if (buffer == null || header == null) {
            this.header = null;
            this.buffer = null;
            drawBackground();
            paintScreen(this.getGraphics());
            return;
        }

        this.header = header;
        period = (long) (header.getSamplingInterval() * NANOSEC_IN_MIKROSEC);
        this.buffer = buffer;
        startPos = 0;
        playbackIndicatorPosition = 0;
        if (header.getNumberOfSamples() < (int) timeToAbsoluteFrame(DEFAULT_DRAWED_RANGE)) {
            drawedFrames = (int) timeToAbsoluteFrame(header.getNumberOfSamples());
        } else {
            drawedFrames = (int) timeToAbsoluteFrame(DEFAULT_DRAWED_RANGE);
        }
        framesStep = this.getWidth() / (float) drawedFrames;
        drawedEpochs = new int[(int) (header.getNumberOfSamples())];
        drawedArtefacts = new boolean[(int) (header.getNumberOfSamples())];
        gridStep = getGridRange();
        dMax = (float) getHeight() / numberOfVisibleSignals;
    }

    /**
     * Znovu vyrendeju a pøekreslí zobrazenı signál.
     */
    private synchronized void refresh() {
        try {
            render();
        } catch (Exception e) {
            e.printStackTrace();
        }
        paintScreen(this.getGraphics());
    }

    /* (non-Javadoc)
     * @see javax.swing.JComponent#addNotify()
     */
    @Override
    public void addNotify() {
        super.addNotify(); // creates the peer

//        initializeCanvas();

        paused = true;
    }

    /**
     * TODO
     * @param e
     */
    private void paintTimeCursor(MouseEvent e) {
        if (dbImage != null) {
            graphics.setColor(Color.MAGENTA);
            graphics.drawLine(e.getX(), 0, e.getX(), getHeight());
            repaint();
            validate();
        }
    }

    /**
     * Nastaví seznam indexù kanálù, které jsou dostupné k vykreslení.
     * @param channels Seznam indexù kanálù, které mohou bıt vykresleny.
     */
    public synchronized void setDrawableChannels(List<Integer> channels) {
        Integer[] vc = channels.toArray(new Integer[channels.size()]);
        drawableChannels = new int[channels.size()];
        for (int i = 0; i < vc.length; i++) {
            drawableChannels[i] = vc[i];
        }
        numberOfSignals = drawableChannels.length;
        gridStep = getGridRange();
        framesStep = getWidth() / (float) drawedFrames;

        dMax = (float) getHeight() / numberOfVisibleSignals;
        refresh();
    }

    /** 
     * Spustí pøehrávání signálu od poèátku.
     */
    public synchronized void startDrawing() {
        if (animator == null || !running) {
            // System.out.println("new thread");
            animator = new Thread(this);
            animator.start();
        } else {
            // System.out.println("else");
            startPos = 0;
            resumeDrawing();
        }
        notify();

    }

    /** 
     * Znovu spustí pøehrávání signálu po pozastavení.
     */
    public synchronized void resumeDrawing() {
        paused = false;
        notify();
    }

    /** 
     * Pozastaví pøehrávání signálu.
     */
    public synchronized void pauseDrawing() {
        paused = true;
        notify();
    }

    /** 
     * Pozastaví nebo znova spustí pøehrávání signálu.
     */
    public synchronized void togglePause() {
        if (header == null) {
            return;
        }

        if (startPos + drawedFrames > header.getNumberOfSamples()) {
            paused = true;
        } else {
            paused = !paused;
        }
        notify();
    }

    /** 
     * Zastaví pøehrávání signálu a vrátí ukazatel pøehrávání na poèátek.
     */
    public synchronized void stopDrawing() {
        running = false;
        paused = true;
        notify();
        startPos = 0;
        playbackIndicatorPosition = 0;
        refresh();
    }

    /**
     * Nastaví poèátek vykreslovaného úseku.
     * @param value První snímek vykreslovaného úseku.
     */
    public synchronized void setFirstFrame(int value) {
        notify();
        startPos = value;
        refresh();
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        long beforeTime, afterTime, timeDiff, sleepTime;
        long overSleepTime = 0L;
        int noDelays = 0;
        long excess = 0L;

        beforeTime = System.nanoTime();

        running = true;

        paused = false;

        while (running) {

            update();
            winSignals.setHorizontalScrollbarValue((int) startPos);

            try {
                render();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            paintScreen(this.getGraphics());

            afterTime = System.nanoTime();
            timeDiff = afterTime - beforeTime;
            sleepTime = (period - timeDiff) - overSleepTime;

            if (sleepTime > 0) { // some time left in this cycle

                try {
                    Thread.sleep(sleepTime / 1000000L); // nano -> ms

                } catch (InterruptedException ex) {
                }
                overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
            } else { // sleepTime <= 0; the frame took longer than the period

                excess -= sleepTime; // store excess time value

                overSleepTime = 0L;

                if (++noDelays >= NO_DELAYS_PER_YIELD) {
                    Thread.yield(); // give another thread a chance to run

                    noDelays = 0;
                }
            }

            try {
                if (paused) {
                    synchronized (this) {
                        while (paused && running) {
                            wait();
                        }
                    }
                }
            } catch (InterruptedException e) {
                // nic
            }

            beforeTime = System.nanoTime();

            // Trva-li vykreslovani prilis dlouho, updatuje se stav bez
            // vykresleni tolikrat, aby se pocet updatu blizil cilovemu fps
            int tmpSkips = 0;
            while (excess > period) {
                excess -= period;
                update(); // update state but don't render

                tmpSkips++;
            }
            skips = tmpSkips;
        }
    }

    /**
     * Update posuvu pøehrávání signálu.
     */
    private void update() {
        /*
         * V teto metode se vykonava akce, ktera se ma stat pri dalsim updatu (nikoliv pri dalsim vykresleni),
         * tzn. nastaveni prvniho framu vykreslovaneho useku o jednicku vys, asi zde ale nebude nutno nic menit krome zdroje dat
         * 
         * Pozor na to, samotne vykreslovani je uplne oddeleno od updatovani,
         * tzn. updatovani se provadi tolikrat za sekundu, jako je vzorkovaci frekvence signalu,
         * vykreslovani se pak provadi tolikrat, kolikrat to stihne pocitac vykreslit ;-).
         * Takze ve vykreslovani se pak neni vubec treba zabyvat synchronizaci vykreslovani vuci casu, to je ucel teto komponenty.
         * 
         */
        if (header == null) {
            return;
        }

        if (!paused) {
            if (++playbackIndicatorPosition >= header.getNumberOfSamples()) {
                paused = true;
                return;
            }

            long center = drawedFrames / 2 + startPos;

            if (playbackIndicatorPosition == center) {
                if (startPos + drawedFrames < header.getNumberOfSamples()) {
                    startPos++;
                }
            } else if (playbackIndicatorPosition > startPos && playbackIndicatorPosition < center) {
                //nic
            } else if (playbackIndicatorPosition < startPos) {
                startPos = playbackIndicatorPosition;
            } else if (playbackIndicatorPosition > center && playbackIndicatorPosition < startPos + drawedFrames) {
                if (startPos + drawedFrames < header.getNumberOfSamples()) {
                    startPos += 2;
                }
            } else if (playbackIndicatorPosition >= startPos + drawedFrames) {
                startPos = playbackIndicatorPosition - drawedFrames + 2;
            }
        }
    }

    /**
     * Vykreslení dat.
     * @throws InvalidFrameIndexException 
     * @throws IOException 
     */
    private synchronized void render() throws InvalidFrameIndexException {

        if (buffer == null || header == null) {
            return;
        }

        try {
            dbImage = (BufferedImage) createImage(getWidth(), getHeight());
            gridStep = getGridRange();
            framesStep = this.getWidth() / (float) drawedFrames;
            dMax = (float) getHeight() / numberOfVisibleSignals;
        } catch (IllegalArgumentException e) {
            return;
        }

        if (dbImage != null) {
            graphics = dbImage.getGraphics();
        } else {
            return;
        }

        // Vımaz pozadí
        drawBackground();

        // vykreslení znaèek epoch
        drawEpochs();

        // vykreslení souøadnic
        drawGrid();

        // vykreslení signálù
        drawSignals();

        // vykreslení ukazatele
        drawPlaybackIndicator();

        // vypsání frame skips (debugovací informace)
        graphics.setColor(Color.GRAY);
        graphics.setFont(font);
        graphics.drawString("frame skips: " + skips, 20, 25);
    }

    /**
     * Vykreslení (resp. smazání) pozadí.
     */
    private void drawBackground() {
        graphics.setColor(Const.DC_BACKGROUND_COLOR);
        graphics.fillRect(0, 0, dbImage.getWidth(), dbImage.getHeight());
    }

    /**
     * Vykreslení èasové møíky.
     */
    private void drawGrid() {
        long firstGridLine = frameToTime(startPos);
        long lastGridLine = frameToTime(startPos + drawedFrames);

        graphics.setFont(gridFont);

        if (firstGridLine % gridStep != 0) {
            firstGridLine = firstGridLine - (firstGridLine % gridStep) + gridStep;
        }

        for (long i = firstGridLine; i <= lastGridLine; i += gridStep) {
            int x = getXCoordinate(timeToAbsoluteFrame(i));
            graphics.setColor(Const.DC_GRID_COLOR);
            graphics.drawLine(x, 0, x, getHeight());
            graphics.setColor(Const.DC_GRID_FONT_COLOR);
            graphics.drawString(timeToStr(i), x + 2, getHeight() - 2);
        }
    }

    /**
     * Vykreslení signálù.
     * @throws cz.zcu.kiv.jerpstudio.data.InvalidFrameIndexException
     */
    private void drawSignals() throws InvalidFrameIndexException {
        float x0;
        float y0;
        float x1;
        float y1;
        float value;

//        float sRange = Math.abs(sMax + sMax);

        for (int signal = 0; signal < numberOfVisibleSignals && signal < numberOfSignals; signal++) {
            x0 = 0;
            x1 = framesStep;

            value = buffer.getValue(drawableChannels[signal + firstVisibleSignal], startPos);

            y1 = dMax * (signal + 0.5f) + (invertedSignals ? value / vZoom : -value / vZoom);


            graphics.setColor(Const.DC_GRID_COLOR);
            for (int j = 0; j < getWidth(); j += 10) {
                graphics.drawLine(j, (int) (signal * dMax + dMax / 2), j + 5, (int) (signal * dMax + dMax / 2));
            }


            graphics.setColor(Const.DC_SIGNALS_COLORS[(signal + firstVisibleSignal) % Const.DC_SIGNALS_COLORS.length]);
            for (int i = 0; i < drawedFrames && startPos + i < header.getNumberOfSamples(); i++) {

                y0 = y1;

                value = buffer.getNextValue();

                y1 = dMax * (signal + 0.5f) + (invertedSignals ? value / vZoom : -value / vZoom);

//                graphics.drawLine((int) x0, (int) y0, (int) x1, (int) y1);
                graphics.drawLine(getXCoordinateR(i), (int) y0, getXCoordinateR(i + 1), (int) y1);
                x0 = x1;
                x1 += framesStep;
            }
        }
    }

    /**
     * Vykreslení ukazatele pøehrávání.
     */
    private void drawPlaybackIndicator() {
        graphics.setColor(Color.MAGENTA);
        graphics.setColor(Const.DC_PLAYBACK_POINTER_COLOR);
        graphics.drawLine(getXCoordinate(playbackIndicatorPosition), 0, getXCoordinate(playbackIndicatorPosition), this.getHeight());
    }

    /**
     * Vykreslení epoch.
     */
    private void drawEpochs() {
        float x0;
        x0 = 0;

        for (int i = 0; i < drawedFrames && startPos + i < header.getNumberOfSamples(); i++) {
            if (drawedArtefacts[(int) startPos + i]) {
                graphics.setColor(Color.RED);
                //graphics.drawLine((int) x0, 0, (int) x0, getHeight());   
                ((Graphics2D) graphics).fill(
                        new Rectangle2D.Double(x0, 0, framesStep, getHeight()));
            }
            x0 += framesStep;
        }

        x0 = 0;
        for (int i = 0; i < drawedFrames && startPos + i < header.getNumberOfSamples(); i++) {
            if (drawedEpochs[(int) startPos + i] == -1) {
                graphics.setColor(Color.GREEN);
                ((Graphics2D) graphics).fill(
                        new Rectangle2D.Double(x0, 0, framesStep, getHeight()));
            } else if (drawedEpochs[(int) startPos + i] == 1) {
                graphics.setColor(Color.GREEN);
                ((Graphics2D) graphics).fill(
                        new Rectangle2D.Double(x0, 0, framesStep, getHeight()));
                graphics.setColor(Color.BLACK);
                graphics.drawLine((int) x0, 0, (int) x0, getHeight());
            }
            x0 += framesStep;
        }

        if (signalsWindowProvider.isAreaSelection()) {
            graphics.setColor(signalsWindowProvider.getColorSelection());

            if (signalsWindowProvider.getEndSelection() > 0) {
                ((Graphics2D) graphics).fill(
                        new Rectangle2D.Double(signalsWindowProvider.getStartSelection(), 0,
                        signalsWindowProvider.getEndSelection(), getHeight()));
            } else {
                ((Graphics2D) graphics).fill(
                        new Rectangle2D.Double(signalsWindowProvider.getStartSelection() +
                        signalsWindowProvider.getEndSelection(), 0,
                        -signalsWindowProvider.getEndSelection(), getHeight()));
            }
        }
    }

    /**
     * Pøekreslí vyrenderovanı image.
     */
    private void paintScreen(Graphics g) {
        if (dbImage != null) {
            g.drawImage(dbImage, 0, 0, null);
            Toolkit.getDefaultToolkit().sync();
            g.dispose();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintScreen(g);
    }

    /**
     * Nastaví rodièovské okno.
     * @param winSignals
     */
    public void setSignalsWindow(SignalsWindow winSignals) {
        this.winSignals = winSignals;
    }

    /**
     * Zjistí, zda je pøehrávání signálu pozastaveno.
     * @return Stav pozastavení pøehrávání.
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Zjistí, zda je spuštìno pøehrávání signálu.<br/>
     * Vrací <code>true</code> i v pøípadì, e je pøehrávání pozastaveno.
     * @return <code>true</code>, pokud je pøehrávací smyèka spuštìna.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Vrátí délku vykreslovaného úseku jako poèet snímkù..
     * @return Poèet vykreslovanıch snímkù.
     */
    public int getDrawedFrames() {
        return drawedFrames;
    }

    /**
     * Vrátí délku vykreslovaného úseku v milisekundách.
     * @return Délka vykreslovaného úseku v milisekundách.
     */
    public long getDrawedLength() {
        return frameToTime(drawedFrames);
    }

    /**
     * TODO
     * @param epochsDraw
     */
    public void setDrawedEpochs(int[] epochsDraw) {
        this.drawedEpochs = epochsDraw;
        refresh();
    }

    /**
     * TODO
     * @param drawedArtefacts 
     */
    public void setDrawedArtefacts(boolean[] drawedArtefacts) {
        this.drawedArtefacts = drawedArtefacts;
        refresh();
    }

    /**
     * Vrací absolutní index framu nacházejícího se na zadané souøadnici.
     * @param xCoordinate Souøadnice bodu na X ose ve vykreslovací komponentì.
     * @return Absolutní index framu - èíslovanı od poèátku souboru.
     */
    public long getAbsolutePosition(int xCoordinate) {
        long position;
        position = (long) (startPos + xCoordinate / (float) getWidth() * drawedFrames);
        return position;
    }

    /**
     * Vrací relativní index framu nacházejícího se na zadané souøadnici.
     * @param xCoordinate Souøadnice bodu na X ose ve vykreslovací komponentì.
     * @return Relativní index framu - èíslovanı od prvního framu zobrazeného v komponentì.
     */
    public long getRelativePosition(int xCoordinate) {
        long position;
        position = (long) (xCoordinate / (float) getWidth() * drawedFrames);
        return position;
    }

    /**
     * Vrací X souøadnici náleící absolutnì zadanému framu.
     * @param absolutePosition
     * @return X souøadnice framu, kterı byl zadán absolutnì.
     */
    public int getXCoordinate(long absolutePosition) {
        int x;
        x = (int) (getWidth() * ((absolutePosition - startPos) / (float) drawedFrames));
        return x;
    }

    /**
     * Vrací X souøadnici náleící relativnì zadanému framu (poèátkem je první vykreslenı frame).
     * @param relativePosition
     * @return X souøadnice framu, kterı byl zadán relativnì.
     */
    public int getXCoordinateR(long relativePosition) {
        int x;
        x = (int) (getWidth() * (relativePosition / (float) drawedFrames));
        return x;
    }

    /**
     * Nastaví vertikální zoom.
     * @param value Hodnota vertikálního zoomu.
     */
    public synchronized void setVerticalZoom(float value) {
        vZoom = value;
        refresh();
    }

    /**
     * Vrátí hodnotu vertikálního zoomu.
     * @return Hodnota vertikálního zoomu.
     */
    public synchronized float getVerticalZoom() {
        return vZoom;
    }

    /**
     * Nastaví hodnotu horizontálního zoomu (resp. poèet vykreslovanıch snímkù).
     * @param value Poèet vykreslovanıch snímkù.
     */
    public synchronized void setHorizontalZoom(int value) {
        long center = drawedFrames / 2 + startPos;
        if (value < header.getNumberOfSamples()) {
            drawedFrames = value;
        } else {
            drawedFrames = (int) header.getNumberOfSamples();
        }
        startPos = center - drawedFrames / 2;
        if (startPos < 0) {
            startPos = 0;
        } else if (startPos + drawedFrames >= header.getNumberOfSamples()) {
            startPos = header.getNumberOfSamples() - drawedFrames;
        }
        winSignals.setHorizontalScrollbarValue((int) startPos);
        signalsWindowProvider.resetHorizontalScrollbarMaximum();
        gridStep = getGridRange();
        framesStep = this.getWidth() / (float) drawedFrames;
        dMax = (float) getHeight() / numberOfVisibleSignals;
        refresh();
    }

    /**
     * Vrací optimální møíkovou vzdálenost v milisekundách.
     * @return Vzdálenost mezi linkami møíky v milisekundách.
     */
    private int getGridRange() {
        int numberOfFrames = -1;
        int lastOptimalWidthDiff = Integer.MAX_VALUE;
        int currentOptimalWidthDiff;

        if (header == null) {
            return Const.DC_GRID_RANGE_TIMES[0];
        }

        for (int i = 0; i < Const.DC_GRID_RANGE_TIMES.length; i++) {
            numberOfFrames = (int) (Const.DC_GRID_RANGE_TIMES[i] * 1000 / header.getSamplingInterval());
            currentOptimalWidthDiff = Math.abs(Const.DC_OPTIMAL_GRID_RANGE - getXCoordinateR(numberOfFrames));
            if (currentOptimalWidthDiff > lastOptimalWidthDiff) {
                return Const.DC_GRID_RANGE_TIMES[i - 1];
            } else {
                lastOptimalWidthDiff = currentOptimalWidthDiff;
            }
        }

        return Const.DC_GRID_RANGE_TIMES[Const.DC_GRID_RANGE_TIMES.length - 1];
    }

    /**
     * Vrací èas na zadaném framu vyjádøenı jako øetìzec ve formátu m:ss:lll (m - minuty, s - sekundy, l - milisekundy).
     * @param frame Absolutní index framu (od zaèátku souboru).
     * @return Øetìzec s èasem.
     */
    public String frameToTimeStr(long frame) {
        long time = (long) (frame * header.getSamplingInterval() / 1000);

        return timeToStr(time);
    }

    /**
     * Pøevede zadanı èas zadanı v milisekundách na øetìzec ve formátu m:ss:lll (m - minuty, s - sekundy, l - milisekundy).
     * @param time Èas zadanı v milisekundách.
     * @return Øetìzec s èasem 
     */
    public String timeToStr(long time) {
        int minutes = (int) time / 60000;

        String seconds = String.valueOf((int) (time % 60000) / 1000);
        if (seconds.length() < 2) {
            seconds = "0" + seconds;
        }

        String millis = String.valueOf(time % 1000);
        while (millis.length() < 3) {
            millis = "0" + millis;
        }

        return String.valueOf(minutes) + ":" + seconds + ":" + millis;

    }

    /**
     * Vrací èas v milisekundách na zadaném framu.
     * @param frame Absolutní index framu (od zaèátku souboru).
     * @return Èas v milisekundách.
     */
    public long frameToTime(long frame) {
        return (long) (frame * header.getSamplingInterval() / 1000);
    }

    /**
     * Vrací absolutní index framu na zadaném èase.
     * @param time Èas v milisekundách.
     * @return Absolutní index framu - od zaèátku souboru.
     */
    public synchronized long timeToAbsoluteFrame(long time) {
        return (long) (time * 1000 / header.getSamplingInterval());
    }

    /**
     * Vrací relativní index framu na zadaném èase.
     * @param time Èas v milisekundách.
     * @return Relativní index framu - od prvního vykresleného framu.
     */
    public synchronized long timeToRelativeFrame(long time) {
        return timeToAbsoluteFrame(time) - startPos;
    }

    /**
     * Nastavuje, zda mají bıt signály vykresleny invertovanì.
     * @param inverted <code>true</code> - signály budou vykresleny invertovanì.
     */
    public void setInvertedView(boolean inverted) {
        invertedSignals = inverted;
        refresh();
    }

    /**
     * Zjišuje, zda jsou signály vykresleny invertovanì.
     * @return <code>true</code> - signály jsou vykresleny invertovanì.
     */
    public boolean isInvertedView() {
        return invertedSignals;
    }

    /**
     * TODO
     */
    protected synchronized void loadNumbersOfSignals() {
        numberOfVisibleSignals = signalsWindowProvider.getNumberOfVisibleChannels();
        firstVisibleSignal = signalsWindowProvider.getFirstVisibleChannel();

        gridStep = getGridRange();
        framesStep = this.getWidth() / (float) drawedFrames;
        dMax = (float) getHeight() / numberOfVisibleSignals;
        refresh();
    }

    /**
     * Nastaví ukazatel pøehrávání na pozici zadanou indexem vzorku.
     * @param position Index vzorku, na nìj se má nastavit pozice ukazatele.
     */
    public synchronized void setPlaybackIndicatorPosition(long position) {
        playbackIndicatorPosition = position;
        refresh();
    }
}
