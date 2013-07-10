package icp.gui;

import icp.Const;
import icp.aplication.SessionManager;
import icp.data.formats.CorruptedFileException;
import icp.gui.signals.SignalsWindowProvider;

import java.io.*;
import java.net.URLClassLoader;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;


public class GuiController extends Observable {

    /**
     * Byl zav�en projekt a ��dn� dal�� nen� otev�en�.
     */
    public static final int MSG_PROJECT_CLOSED = 1;
    /**
     * Byl zm�n�n aktu�ln� otev�en� projekt (pota�mo Buffer a Header apod.).
     */
    public static final int MSG_CURRENT_PROJECT_CHANGED = 2;
    /**
     * Spu�t�no p�ehr�v�n� sign�lu.
     */
    public static final int MSG_SIGNAL_PLAYBACK_START = 3;
    /**
     * Zastaveno p�ehr�v�n� sign�lu.
     */
    public static final int MSG_SIGNAL_PLAYBACK_STOP = 4;
    /**
     * Pozastaveno p�ehr�v�n� sign�lu.
     */
    public static final int MSG_SIGNAL_PLAYBACK_PAUSE = 5;
    /**
     * P�ehr�v�n� sign�lu po pozastaven� op�t spu�t�no.
     */
    public static final int MSG_SIGNAL_PLAYBACK_RESUME = 6;
    /**
     * Byl zm�n�n v�b�r kan�l� ur�en�ch k zobrazen�.
     */
    public static final int MSG_CHANNEL_SELECTED = 7;
    /**
     * Jsou k dispozici nov� pr�m�ry.
     */
    public static final int MSG_NEW_AVERAGES_AVAILABLE = 8;
    /**
     * Je po�adov�no spu�t�n� exportn�ho okna.
     */
    public static final int MSG_RUN_AVERAGES_EXPORT = 9;
    /**
     * Byla vyvol�no undo, redo a nebo akce, kter� undo/redo umo�n�.
     */
    public static final int MSG_UNDOABLE_COMMAND_INVOKED = 10;
    /**
     * Bylo stisknuto tla��tko pro automatick� ozna�ov�n� artefakt�.
     */
    public static final int MSG_WAVELET_TRANSFORM = 11;
    /**
     * Jsou dostupn� indexy epoch, kter� jsou ur�eny k pr�m�rov�n�.
     */
    public static final int MSG_NEW_INDEXES_FOR_AVERAGING_AVAILABLE = 12;
    /**
     * Byla provedena oprava base-line a vytvo�il se nov� buffer.
     */
    public static final int MSG_NEW_BUFFER = 13;
    /**
     * Zm�na ve zp�sobu zobrazen� pr�b�hu sign�lu.
     */
    public static final int MSG_INVERTED_SIGNALS_VIEW_CHANGED = 15;
    /**
     * Zobrazen� okna pro import.
     */
    public static final int MSG_SHOW_IMPORT_DIALOG = 16;
    /**
     * Byl zav�en mod�ln� dialog.
     */
    public static final int MSG_MODAL_DIALOG_CLOSED = 17;
    /**
     * Zpr�va o chyb� p�i wt.
     */
    public static final int MSG_WAVELET_ERROR = 18;
    /**
     * Zpr�va o zru�en� wt.
     */
    public static final int MSG_WAVELET_STORNO = 19;
    /**
     * Zpr�va o neproveden� dwt.
     */
    public static final int MSG_DWT_DISABLED = 20;
    
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
     * Roze�le zpr�vu v�em registrovan�m provider�m.
     * @param message Hodnota zpr�vy:<br/>
     * <code>MSG_PROJECT_CLOSED</code> - Projekt byl uzav�en a nen� otev�en ��dn� projekt.<br/>
     * <code>MSG_CURRENT_PROJECT_CHANGED</code> - Aktu�ln� projekt byl zm�n�n na jin�.<br/>
     * <code>MSG_SIGNAL_PLAYBACK_START</code> - P�ehr�v�n� sign�lu bylo spu�t�no.<br/>
     * <code>MSG_SIGNAL_PLAYBACK_STOP</code> - P�ehr�v�n� sign�lu bylo zastaveno.<br/>
     * <code>MSG_SIGNAL_PLAYBACK_PAUSE</code> - P�ehr�v�n� sign�lu bylo pozastaveno.<br/>
     * <code>MSG_SIGNAL_PLAYBACK_RESUME</code> - P�ehr�v�n� sign�lu bylo po pozastaven� op�t spu�t�no.<br/>
     * <code>MSG_CHANNEL_SELECTED</code> - Byl zm�n�n po�et sign�l� vybran�ch k zobrazen�.<br/>
     * <code>MSG_NEW_AVERAGES_AVAILABLE</code> - Byly vytvo�eny nov� pr�m�ry.<br/>
     * <code>MSG_RUN_AVERAGES_EXPORT</code> - Je po�adov�no spu�t�n� exportn�ho okna.<br/>
     * <code>MSG_UNDOABLE_COMMAND_INVOKED</code> - Byla vyvol�no undo, redo a nebo akce, kter� undo/redo umo�n�.<br/>
     * <code>MSG_AUTOMATIC_ARTEFACT_SELECTION</code> - Byla vyvol�no undo, redo a nebo akce, kter� undo/redo umo�n�.<br/>
     * <code>MSG_NEW_INDEXES_FOR_AVERAGING_AVAILABLE</code> - Jsou dostupn� indexy epoch, kter� jsou ur�eny k pr�m�rov�n�.<br/>
     * <code>MSG_NEW_BUFFER</code> - Byla provedena oprava base-line a vytvo�il se nov� buffer.<br/>
     * <code>MSG_BASELINE_CORRECTION_INTERVAL_SELECTED</code> - Byla ozna�en interval pro opravu baseliny.<br/>
     * <code>MSG_INVERTED_SIGNALS_VIEW_CHANGED</code> - Zm�na ve zp�sobu zobrazen� pr�b�hu sign�lu.<br/>
     * <code>MSG_SHOW_IMPORT_DIALOG</code> - Zobrazen� okna pro import pr�m�r�.
     * <code>MSG_MODAL_DIALOG_CLOSED</code> - Byl zav�en mod�ln� dialog.
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
     * Na��t� ikony ze souboru.
     * @param name 
     * @return Na�ten� ikona.
     */
    public ImageIcon loadIcon(String name) {
        ImageIcon imageIcon;
        URLClassLoader urlLoader = (URLClassLoader) this.getClass().getClassLoader();
        imageIcon = new ImageIcon(urlLoader.getResource("images" + File.separator + name));
        return imageIcon;
    }
    
    public void sendProgressUnits(double units) {
        mainWindowProvider.sendProgressUnits(units);
    }
}
