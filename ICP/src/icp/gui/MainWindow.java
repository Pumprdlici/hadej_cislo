package icp.gui;

import icp.Const;
import icp.gui.WaveletTransformDialog;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import javax.swing.*;


import java.beans.PropertyChangeListener;

/**
 * Hlavní okno.
 * Obsahuje hlavní tlaèítka a udržuje rozmístìní hlavních komponent.
 * @author Petr Soukal
 */
public class MainWindow extends JFrame {

    private static final int SPLIT_DIVIDER_SIZE = 7;
    private static final long serialVersionUID = 1L;
    private MainWindowProvider mainWindowProvider;
    private MainWindow mainWindow = this;
    private JMenuBar menu;
    private JToolBar toolBar;
    private GuiController guiController = null;
    protected JSplitPane splitVerticalLeft;
    protected JSplitPane splitVerticalRight;
    protected JSplitPane split;
    private JMenu fileMenu;
    private JMenu editMenu;
    private JMenu helpMenu;
    protected JMenuItem openMenuItem;
    protected JMenuItem waveletTransformItem;
    private JMenuItem aboutMenuItem;
    protected JButton openButton;
    protected JButton infoButton;
    protected JButton waveletDialogBT;
    protected WaveletTransformDialog waveletDialog;

    /**
     * Vytváøí instanci tøídy.
     * @param guiController - rozhraní pro komunikaci hlavního okna s aplikaèní vrstvou a
     * ostatními komponentami
     * @param mainWindowProvider 
     */
    public MainWindow(final GuiController guiController, final MainWindowProvider mainWindowProvider) {
        super(Const.APP_NAME);
        this.mainWindowProvider = mainWindowProvider;
        this.guiController = guiController;
        this.setIconImage(guiController.loadIcon("icon.gif").getImage());
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setJMenuBar(getMenu());
        this.setLayout(new BorderLayout());
        this.add(getInterior());
        this.setLocationByPlatform(true);
        this.setVisible(true);
        this.pack();
        this.setSize(Const.MAIN_WINDOW_WIDTH, Const.MAIN_WINDOW_HEIGHT);
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                guiController.exitAplication();
            }
        });

        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
//                mainWindowProvider.setSplitPaneHistory();
                //mainWindowProvider.toggleWindows();
            }
        });
    }

    public void createWaveletDialog() {
    	waveletDialog = new WaveletTransformDialog(mainWindow, mainWindowProvider);
    }

    /**
     * Vytváøí menu aplikace.
     */
    private JMenuBar getMenu() {
        menu = new JMenuBar();


        fileMenu = new JMenu("File");
        helpMenu = new JMenu("Help");
        editMenu = new JMenu("Edit");

        openMenuItem = new JMenuItem("Open...");

        waveletTransformItem = new JMenuItem("Wavelet Transform");

        aboutMenuItem = new JMenuItem("About");

        openMenuItem.addActionListener(new OpenFileListener());

        waveletTransformItem.addActionListener(new WaveletTRListener());

        aboutMenuItem.addActionListener(new AboutListener());

        JMenuItem exitMenuItem = new JMenuItem("Exit");

        exitMenuItem.addActionListener(new ExitListener());

        waveletTransformItem.setEnabled(false);

        fileMenu.add(openMenuItem);
        fileMenu.add(new JSeparator());
        fileMenu.add(exitMenuItem);
        
        editMenu.add(waveletTransformItem);

        helpMenu.add(aboutMenuItem);

        menu.add(fileMenu);
        menu.add(editMenu);
        menu.add(helpMenu);

        return menu;
    }

    /**
     * Vytváøí prostor pro vnitøek aplikace a toolBar.
     */
    private JPanel getInterior() {
        JPanel interior = new JPanel(new BorderLayout());

        interior.add(getToolBar(), BorderLayout.NORTH);
        interior.add(guiController.getSignalsWindow(), BorderLayout.CENTER);

        return interior;
    }

    /**
     * Vytvoøí a vrátí toolbar.
     * @return Toolbar.
     */
    private JToolBar getToolBar() {
        if (toolBar == null) {
            toolBar = new JToolBar();

            openButton = new JButton(guiController.loadIcon("open24.gif"));
            infoButton = new JButton(guiController.loadIcon("information24.gif"));
            waveletDialogBT = new JButton(guiController.loadIcon("invert.png"));
            waveletDialogBT.setEnabled(false);

            Font buttonsFont = new Font(Font.SANS_SERIF, Font.BOLD, 16);


            openButton.addActionListener(new OpenFileListener());
            infoButton.addActionListener(new AboutListener());
            waveletDialogBT.addActionListener(new WaveletTRListener());

            toolBar.add(openButton);
            toolBar.addSeparator();
            toolBar.add(waveletDialogBT);
            toolBar.addSeparator();
            toolBar.add(infoButton);
            toolBar.setFloatable(false);
        }

        return toolBar;
    }

    /**
     * Obsluhuje tlaèítko openFile
     * Otevírá openFile pomocí presentationProveidera.
     */
    private class OpenFileListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            guiController.openFile();
        }
    }

    /**
     * Obsluhuje tlaèítko exit
     * Otevírá exit pomocí presentationProveidera.
     */
    private class ExitListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            guiController.exitAplication();
        }
    }

    private class WaveletTRListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            waveletDialog.setActualLocationAndVisibility();
        }
    }

    private class AboutListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            mainWindowProvider.about();
        }
    }



}
