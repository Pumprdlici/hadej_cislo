package icp.gui;

import icp.Const;
import icp.aplication.SessionManager;
import icp.data.formats.CorruptedFileException;
import icp.gui.signals.SignalsWindowProvider;

import java.io.*;
import java.net.URLClassLoader;
import java.util.Observable;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;


public class GuiController extends Observable {

    /**
     * Byl zavøen projekt a žádný další není otevøený.
     */
    public static final int MSG_PROJECT_CLOSED = 1;
    /**
     * Byl zmìnìn aktuálnì otevøený projekt (potažmo Buffer a Header apod.).
     */
    public static final int MSG_CURRENT_PROJECT_CHANGED = 2;
    /**
     * Spuštìno pøehrávání signálu.
     */
    public static final int MSG_SIGNAL_PLAYBACK_START = 3;
    /**
     * Zastaveno pøehrávání signálu.
     */
    public static final int MSG_SIGNAL_PLAYBACK_STOP = 4;
    /**
     * Pozastaveno pøehrávání signálu.
     */
    public static final int MSG_SIGNAL_PLAYBACK_PAUSE = 5;
    /**
     * Pøehrávání signálu po pozastavení opìt spuštìno.
     */
    public static final int MSG_SIGNAL_PLAYBACK_RESUME = 6;
    /**
     * Byl zmìnìn výbìr kanálù urèených k zobrazení.
     */
    public static final int MSG_CHANNEL_SELECTED = 7;
    /**
     * Jsou k dispozici nové prùmìry.
     */
    public static final int MSG_NEW_AVERAGES_AVAILABLE = 8;
    /**
     * Je požadováno spuštìní exportního okna.
     */
    public static final int MSG_RUN_AVERAGES_EXPORT = 9;
    /**
     * Byla vyvoláno undo, redo a nebo akce, která undo/redo umožní.
     */
    public static final int MSG_UNDOABLE_COMMAND_INVOKED = 10;
    /**
     * Bylo stisknuto tlaèítko pro automatické oznaèování artefaktù.
     */
    public static final int MSG_WAVELET_TRANSFORM = 11;
    /**
     * Jsou dostupné indexy epoch, které jsou urèeny k prùmìrování.
     */
    public static final int MSG_NEW_INDEXES_FOR_AVERAGING_AVAILABLE = 12;
    /**
     * Byla provedena oprava base-line a vytvoøil se nový buffer.
     */
    public static final int MSG_NEW_BUFFER = 13;
    /**
     * Zmìna ve zpùsobu zobrazení prùbìhu signálu.
     */
    public static final int MSG_INVERTED_SIGNALS_VIEW_CHANGED = 15;
    /**
     * Zobrazení okna pro import.
     */
    public static final int MSG_SHOW_IMPORT_DIALOG = 16;
    /**
     * Byl zavøen modální dialog.
     */
    public static final int MSG_MODAL_DIALOG_CLOSED = 17;
    /**
     * Zpráva o chybì pøi wt.
     */
    public static final int MSG_WAVELET_ERROR = 18;
    /**
     * Zpráva o zrušení wt.
     */
    public static final int MSG_WAVELET_STORNO = 19;
    /**
     * Zpráva o neprovedení dwt.
     */
    public static final int MSG_DWT_DISABLED = 20;
    
    public static final int MSG_AVERAGING = 21;
    
    public static final int MSG_DETECTION = 22;
    
    public static final int MSG_MP_PREPROCESSING = 23;
    
    public static final int MSG_DETECTION_STOP = 24;
    
    SessionManager app = null;
    //DataFileInfoWindow dfiw;
    MainWindow gui = null;
    private SignalsWindowProvider signalsWindowProvider;
    private MainWindowProvider mainWindowProvider;

    /**
     * Konstruktor tridy PresentationInterface.
     * @param app 
     */
    public GuiController(SessionManager app) {
        this.app = app;

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception f) {
                JOptionPane.showMessageDialog(
                        null,
                        "Nastaven\u00ed platformov\u011b nez\u00e1visl\u00e9ho vzhledu se nepoda\u0159ilo.",
                        "Neo\u010dek\u00e1van\u00e1 chyba",
                        JOptionPane.WARNING_MESSAGE);
            }
        }


        signalsWindowProvider = new SignalsWindowProvider(app, this);
        mainWindowProvider = new MainWindowProvider(app, this);

        this.addObserver(mainWindowProvider);
        this.addObserver(signalsWindowProvider);
//        this.addObserver(this);
    }


    public void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open file");
        fileChooser.setFileFilter(new FileNameExtensionFilter("European Data Format (*.edf, *.rec)", "edf", "rec"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Pseudo signal generator (*.generator)", "generator"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("BrainStudio Format (*.xml)", "xml"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Brain Vision Data Exchange Header File (*.vhdr)", "vhdr"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("All supported files (*.edf, *.esp, *.generator, *.rec, *.vhdr, *.xml)", "edf", "esp", "generator", "rec", "vhdr", "xml"));

        fileChooser.setAcceptAllFileFilterUsed(false);

        if (fileChooser.showOpenDialog(fileChooser) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            loadFile(file);
        }

    }

    /**
     * Rozešle zprávu všem registrovaným providerùm.
     * @param message Hodnota zprávy:<br/>
     * <code>MSG_PROJECT_CLOSED</code> - Projekt byl uzavøen a není otevøen žádný projekt.<br/>
     * <code>MSG_CURRENT_PROJECT_CHANGED</code> - Aktuální projekt byl zmìnìn na jiný.<br/>
     * <code>MSG_SIGNAL_PLAYBACK_START</code> - Pøehrávání signálu bylo spuštìno.<br/>
     * <code>MSG_SIGNAL_PLAYBACK_STOP</code> - Pøehrávání signálu bylo zastaveno.<br/>
     * <code>MSG_SIGNAL_PLAYBACK_PAUSE</code> - Pøehrávání signálu bylo pozastaveno.<br/>
     * <code>MSG_SIGNAL_PLAYBACK_RESUME</code> - Pøehrávání signálu bylo po pozastavení opìt spuštìno.<br/>
     * <code>MSG_CHANNEL_SELECTED</code> - Byl zmìnìn poèet signálù vybraných k zobrazení.<br/>
     * <code>MSG_NEW_AVERAGES_AVAILABLE</code> - Byly vytvoøeny nové prùmìry.<br/>
     * <code>MSG_RUN_AVERAGES_EXPORT</code> - Je požadováno spuštìní exportního okna.<br/>
     * <code>MSG_UNDOABLE_COMMAND_INVOKED</code> - Byla vyvoláno undo, redo a nebo akce, která undo/redo umožní.<br/>
     * <code>MSG_AUTOMATIC_ARTEFACT_SELECTION</code> - Byla vyvoláno undo, redo a nebo akce, která undo/redo umožní.<br/>
     * <code>MSG_NEW_INDEXES_FOR_AVERAGING_AVAILABLE</code> - Jsou dostupné indexy epoch, které jsou urèeny k prùmìrování.<br/>
     * <code>MSG_NEW_BUFFER</code> - Byla provedena oprava base-line a vytvoøil se nový buffer.<br/>
     * <code>MSG_BASELINE_CORRECTION_INTERVAL_SELECTED</code> - Byla oznaèen interval pro opravu baseliny.<br/>
     * <code>MSG_INVERTED_SIGNALS_VIEW_CHANGED</code> - Zmìna ve zpùsobu zobrazení prùbìhu signálu.<br/>
     * <code>MSG_SHOW_IMPORT_DIALOG</code> - Zobrazení okna pro import prùmìrù.
     * <code>MSG_MODAL_DIALOG_CLOSED</code> - Byl zavøen modální dialog.
     */
    public void sendMessage(int message) {
        this.setChanged();
        this.notifyObservers(message);
    }

    public void exitAplication() {
        Object[] buttons = {"Yes", "No"};

        int select = JOptionPane.showOptionDialog(gui, "Exit " + Const.APP_NAME + "?",
                                                  "Closing aplication", JOptionPane.YES_NO_CANCEL_OPTION,
                                                  JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[1]);

        if (select == 0) {
            
            app.deleteUnusedTemporaryFiles();
            
            System.exit(0);
        }
    }

    private void loadFile(File file) {

        try {
            app.loadFile(file);
            
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "File not found", JOptionPane.ERROR_MESSAGE);
            return;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "I/O error", JOptionPane.ERROR_MESSAGE);
            return;
        } catch (CorruptedFileException e) {
            JOptionPane.showMessageDialog(null, "File " + file.getAbsolutePath() + " is corrupted:\n" + e.getMessage(), "Corrupted file", JOptionPane.ERROR_MESSAGE);
            return;
        }

        sendMessage(GuiController.MSG_CURRENT_PROJECT_CHANGED);
    }

    protected JPanel getSignalsWindow() {
        return signalsWindowProvider.getWindow();
    }

    /**
     * Naèítá ikony ze souboru.
     * @param name 
     * @return Naètená ikona.
     */
    public ImageIcon loadIcon(String name) {
        ImageIcon imageIcon;
        URLClassLoader urlLoader = (URLClassLoader) this.getClass().getClassLoader();
        imageIcon = new ImageIcon(urlLoader.getResource("images/" + name));
        return imageIcon;
    }
    
    public void sendWaveletProgressUnits(double units) {
        mainWindowProvider.sendWaveletProgressUnits(units);
    }
    
    public void sendDetectionProgressUnits(double units) {
        mainWindowProvider.sendDetectionProgressUnits(units);
    }
}
