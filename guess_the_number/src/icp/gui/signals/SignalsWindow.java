package icp.gui.signals;

import icp.Const;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;


/**
 * Okno pro zobrazení namìøených dat a jejich pøehrávání. Dále také slouží k
 * vybírání epoch na zobrazených signálech pro prùmìrování dat.
 * 
 * @author Petr Soukal
 */
public class SignalsWindow extends JPanel {

    private static final long serialVersionUID = 1L;
    private final int CH_BOX_PANEL_WIDTH = 150;
    private final int CH_BOXES_WIDTH = 80;
    static final int LABEL_LINE = 1;
    private final int SPINN_VALUE_START = 0, SPINN_VALUE_END = 1024,  SPINN_LOWER_LIMIT = 0,  SPINN_UPPER_LIMIT = Integer.MAX_VALUE,  SPINN_STEP = 1;
    private final int SPINNER_WIDTH = 70;
    private DrawingComponent drawingComponent;
    JPanel checkBoxesPanel; // checkBoxy zobrazených signálù

    JCheckBox[] drawableSignalsCheckBoxes;
    private JPanel drawingPanel;
    private JToolBar upperPanel;
    private JToolBar bottomPanel;
    private JPanel centerPanel;
    private JPanel leftPanel;
    JButton playBT;
    JButton stopBT;
    JButton saveEpochIntervalButton;
    JScrollBar horizontalScrollBar;
    JScrollBar verticalScrollBar;
    JSpinner startEpoch;
    JSpinner endEpoch;
    private SignalsWindowProvider signalsWindowProvider;
    ImageIcon playIcon;
    JToggleButton selectEpochTB;
    JToggleButton unselectEpochTB;
    JToggleButton selectPlaybackTB;
    private ButtonGroup buttonGroup;
    private GridBagConstraints centerPanelConstraints;
    private GridBagLayout centerPanelLayout;
//    private int countVisibleSignals;
    JButton decreaseNumberOfChannelsButton;
    JButton increaseNumberOfChannelsButton;
    JButton increaseVerticalZoomButton;
    JButton decreaseVerticalZoomButton;
    JButton increaseHorizontalZoomButton;
    JButton decreaseHorizontalZoomButton;
    //JToggleButton invertedSignalsButton;
    OptionMenu optionMenu;


    public SignalsWindow(SignalsWindowProvider signalsWindowProvider) {
        this.signalsWindowProvider = signalsWindowProvider;        
        playIcon = signalsWindowProvider.getPlayIcon();
        optionMenu = new OptionMenu(this.signalsWindowProvider);
        drawingComponent = signalsWindowProvider.getDrawingComponent();
        drawingComponent.setSignalsWindow(this);
        setLayout(new BorderLayout());
        

        add(createUpperPanel(), BorderLayout.PAGE_START);
        add(createCheckBoxPanel(), BorderLayout.LINE_START);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.PAGE_END);

        this.setSize(new Dimension(Const.MAIN_WINDOW_WIDTH / 2, Const.MAIN_WINDOW_HEIGHT - 90));
        this.setVisible(true);
        selectEpochTB.setEnabled(false);
        unselectEpochTB.setEnabled(false);
        selectPlaybackTB.setEnabled(false);
//        invertedSignalsButton.setEnabled(false);
        decreaseVerticalZoomButton.setEnabled(false);
        increaseVerticalZoomButton.setEnabled(false);
        decreaseHorizontalZoomButton.setEnabled(false);
        increaseHorizontalZoomButton.setEnabled(false);
        setNumberOfSelectedSignalsButtonsEnabled(false, false);
//        repaintVisibleSignals();

        addComponentListener(new ComponentAdapter() {

                         @Override
                         public void componentResized(ComponentEvent e) {
                             if (drawingComponent != null) {
                                 repaintVisibleSignals();
                             }
                         }
                     });
    }

    /**
     * Vytváøí panel s nástroji pro práci oznaèování epoch.
     */
    private JToolBar createUpperPanel() {
        if (upperPanel == null) {
            upperPanel = new JToolBar();

//            upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.X_AXIS));
            saveEpochIntervalButton = new JButton("Save epoch configuration");
            saveEpochIntervalButton.addActionListener(new FunctionSaveEpochInterval());
            int BUTT_HEIGHT = (int) saveEpochIntervalButton.getPreferredSize().getHeight();

            JLabel startEpochLB = new JLabel("Left border [ms]:");
            startEpochLB.setPreferredSize(new Dimension(startEpochLB.getPreferredSize().width, BUTT_HEIGHT));
            startEpochLB.setMinimumSize(new Dimension(startEpochLB.getPreferredSize().width, BUTT_HEIGHT));
            startEpochLB.setMaximumSize(new Dimension(startEpochLB.getPreferredSize().width, BUTT_HEIGHT));
            JLabel endEpochLB = new JLabel(" Right border [ms]:");
            endEpochLB.setPreferredSize(new Dimension(endEpochLB.getPreferredSize().width, BUTT_HEIGHT));
            endEpochLB.setMinimumSize(new Dimension(endEpochLB.getPreferredSize().width, BUTT_HEIGHT));
            endEpochLB.setMaximumSize(new Dimension(endEpochLB.getPreferredSize().width, BUTT_HEIGHT));
            startEpoch = new JSpinner(new SpinnerNumberModel(SPINN_VALUE_START, SPINN_LOWER_LIMIT,
                                                             0, SPINN_STEP));
            startEpoch.setPreferredSize(new Dimension(SPINNER_WIDTH, BUTT_HEIGHT));
            startEpoch.setMinimumSize(new Dimension(SPINNER_WIDTH, BUTT_HEIGHT));
            startEpoch.setMaximumSize(new Dimension(SPINNER_WIDTH, BUTT_HEIGHT));
            endEpoch = new JSpinner(new SpinnerNumberModel(SPINN_VALUE_END, SPINN_LOWER_LIMIT,
                                                           SPINN_UPPER_LIMIT, SPINN_STEP));
            endEpoch.setPreferredSize(new Dimension(SPINNER_WIDTH, BUTT_HEIGHT));
            endEpoch.setMinimumSize(new Dimension(SPINNER_WIDTH, BUTT_HEIGHT));
            endEpoch.setMaximumSize(new Dimension(SPINNER_WIDTH, BUTT_HEIGHT));
            FunctionSelectedFunction selectFunction = new FunctionSelectedFunction();
            buttonGroup = new ButtonGroup();
            selectEpochTB = new JToggleButton(signalsWindowProvider.getSelectionEpochIcon());
            selectEpochTB.addActionListener(selectFunction);
            selectEpochTB.setToolTipText("Epoch selection");
            selectEpochTB.setActionCommand("" + Const.SELECT_EPOCH);
            unselectEpochTB = new JToggleButton(signalsWindowProvider.getUnselectionEpochIcon());
            unselectEpochTB.addActionListener(selectFunction);
            unselectEpochTB.setToolTipText("Epoch unselection");
            unselectEpochTB.setActionCommand("" + Const.UNSELECT_EPOCH);
            selectPlaybackTB = new JToggleButton(signalsWindowProvider.getPlaybackIcon());
            selectPlaybackTB.addActionListener(selectFunction);
            selectPlaybackTB.setToolTipText("Playback");
            selectPlaybackTB.setActionCommand("" + Const.SELECT_PLAYBACK);
            buttonGroup.add(selectPlaybackTB);
            buttonGroup.add(selectEpochTB);
            buttonGroup.add(unselectEpochTB);
            
            upperPanel.setMargin(new Insets(50, 5, 5, 5));

            upperPanel.add(startEpochLB);
            upperPanel.add(startEpoch);
            upperPanel.add(endEpochLB);
            upperPanel.add(endEpoch);
            upperPanel.add(saveEpochIntervalButton);
            upperPanel.addSeparator();
            upperPanel.add(selectPlaybackTB);
            upperPanel.add(selectEpochTB);
            upperPanel.add(unselectEpochTB);
            upperPanel.addSeparator();
            
        }

        return upperPanel;
    }

    private JPanel createCenterPanel() {
        if (centerPanel == null) {
            centerPanel = new JPanel();
            centerPanelLayout = new GridBagLayout();
            centerPanel.setLayout(centerPanelLayout);
            centerPanelConstraints = new GridBagConstraints();

            addComponentToCenterPanel(createCheckBoxPanel(), 0, 0, 1, 1, 0, 1, GridBagConstraints.VERTICAL, GridBagConstraints.LINE_START, 20, 0);
            addComponentToCenterPanel(createDrawingPanel(), 1, 0, 1, 1, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER, 0, 0);
            addComponentToCenterPanel(createVerticalScrollBar(), 3, 0, 1, 1, 0, 0, GridBagConstraints.VERTICAL, GridBagConstraints.LINE_END, 0, 0);
            addComponentToCenterPanel(createHorizontalScrollBar(), 1, 1, 1, 1, 1, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.PAGE_END, 0, 0);
        }

        return centerPanel;
    }

    private void addComponentToCenterPanel(Component c, int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty, int fill, int anchor, int ipadx, int ipady) {
        centerPanelConstraints.gridx = gridx;
        centerPanelConstraints.gridy = gridy;
        centerPanelConstraints.gridwidth = gridwidth;
        centerPanelConstraints.gridheight = gridheight;
        centerPanelConstraints.weightx = weightx;
        centerPanelConstraints.weighty = weighty;
        centerPanelConstraints.fill = fill;
        centerPanelConstraints.anchor = anchor;
        centerPanelConstraints.ipadx = ipadx;
        centerPanelConstraints.ipady = ipady;
        centerPanelLayout.setConstraints(c, centerPanelConstraints);
        centerPanel.add(c);
    }

    private JToolBar createBottomPanel() {
        if (bottomPanel == null) {
            bottomPanel = new JToolBar();
//            bottomPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

            playBT = new JButton(playIcon);
            playBT.setEnabled(false);
            playBT.addActionListener(new FunctionPlayBT());
            playBT.setPreferredSize(new Dimension(playIcon.getIconWidth(), playIcon.getIconHeight()));
            playBT.setMaximumSize(new Dimension(playIcon.getIconWidth(), playIcon.getIconHeight()));

            stopBT = new JButton(signalsWindowProvider.getStopIcon());
            stopBT.addActionListener(new FunctionStopBT());
            stopBT.setEnabled(false);
            stopBT.setPreferredSize(new Dimension(playIcon.getIconWidth(), playIcon.getIconHeight()));
            stopBT.setMaximumSize(new Dimension(playIcon.getIconWidth(), playIcon.getIconHeight()));

            increaseNumberOfChannelsButton = new JButton(signalsWindowProvider.getIcon("chp.gif"));
            increaseNumberOfChannelsButton.setEnabled(false);
            increaseNumberOfChannelsButton.addActionListener(new ActionListener() {


                                                         public void actionPerformed(ActionEvent e) {
                                                             signalsWindowProvider.increaseNumberOfVisibleChannels();
                                                         }
                                                     });

            decreaseNumberOfChannelsButton = new JButton(signalsWindowProvider.getIcon("chm.gif"));
            decreaseNumberOfChannelsButton.setEnabled(false);
            decreaseNumberOfChannelsButton.addActionListener(new ActionListener() {


                                                         public void actionPerformed(ActionEvent e) {
                                                             signalsWindowProvider.decreaseNumberOfVisibleChannels();
                                                         }
                                                     });

            decreaseVerticalZoomButton = new JButton(signalsWindowProvider.getIcon("magvm.png"));
            decreaseVerticalZoomButton.addActionListener(new ActionListener() {


                                                     public void actionPerformed(ActionEvent e) {
                                                         signalsWindowProvider.decreaseVerticalZoom();
                                                     }
                                                 });

            increaseVerticalZoomButton = new JButton(signalsWindowProvider.getIcon("magvp.png"));
            increaseVerticalZoomButton.addActionListener(new ActionListener() {


                                                     public void actionPerformed(ActionEvent e) {
                                                         signalsWindowProvider.increaseVerticalZoom();
                                                     }
                                                 });

            decreaseHorizontalZoomButton = new JButton(signalsWindowProvider.getIcon("maghm.png"));
            decreaseHorizontalZoomButton.addActionListener(new ActionListener() {


                                                       public void actionPerformed(ActionEvent e) {
                                                           signalsWindowProvider.decreaseHorizontalZoom();
                                                       }
                                                   });

            increaseHorizontalZoomButton = new JButton(signalsWindowProvider.getIcon("maghp.png"));
//            increaseHorizontalZoomButton.setIcon(signalsWindowProvider.getIcon("maghp.png"));
            increaseHorizontalZoomButton.addActionListener(new ActionListener() {


                                                       public void actionPerformed(ActionEvent e) {
                                                           signalsWindowProvider.increaseHorizontalZoom();
                                                       }
                                                   });

//            invertedSignalsButton = new JToggleButton(signalsWindowProvider.getIcon("invert.png"));
//            invertedSignalsButton.setEnabled(false);
//            invertedSignalsButton.addActionListener(new ActionListener() {
//
//                @Override
//                                                public void actionPerformed(ActionEvent e) {
//                                                    signalsWindowProvider.toggleInvertedView();
//                                                }
//                                            });


            bottomPanel.add(playBT);
            bottomPanel.add(stopBT);
            bottomPanel.addSeparator();
            bottomPanel.add(decreaseNumberOfChannelsButton);
            bottomPanel.add(increaseNumberOfChannelsButton);
            bottomPanel.addSeparator();
            bottomPanel.add(decreaseVerticalZoomButton);
            bottomPanel.add(increaseVerticalZoomButton);
            bottomPanel.addSeparator();
            bottomPanel.add(decreaseHorizontalZoomButton);
            bottomPanel.add(increaseHorizontalZoomButton);
//            bottomPanel.addSeparator();
//            bottomPanel.add(invertedSignalsButton);
            
            bottomPanel.setRollover(true);
        }

        return bottomPanel;
    }

    private JPanel createCheckBoxPanel() {
        if (leftPanel == null) {
            leftPanel = new JPanel(new BorderLayout());
            checkBoxesPanel = new JPanel(null);
            checkBoxesPanel.setBackground(Const.SW_COLOR_CHECKBOX_PANEL);
            checkBoxesPanel.setPreferredSize(new Dimension(CH_BOX_PANEL_WIDTH, this.getHeight()));
            checkBoxesPanel.setMinimumSize(new Dimension(CH_BOX_PANEL_WIDTH, this.getHeight()));
            JPanel buttonsPanel = new JPanel();
            buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
            leftPanel.add(checkBoxesPanel, BorderLayout.CENTER);
            leftPanel.add(buttonsPanel, BorderLayout.SOUTH);
        }
        return leftPanel;
    }

    private JPanel createDrawingPanel() {
        if (drawingPanel == null) {
            drawingPanel = new JPanel();
            drawingPanel.setBackground(Const.SW_COLOR_DC_PANEL);
            drawingPanel.setLayout(new BorderLayout());
            drawingPanel.add(signalsWindowProvider.getDrawingComponent(), BorderLayout.CENTER);
            drawingPanel.updateUI();
        }

        return drawingPanel;
    }

    private JScrollBar createVerticalScrollBar() {
        if (verticalScrollBar == null) {
            verticalScrollBar = new JScrollBar(JScrollBar.VERTICAL);
            verticalScrollBar.addAdjustmentListener(new FunctionVerticalScrollBar());
            verticalScrollBar.setMinimum(0);
            verticalScrollBar.setEnabled(false);
        }

        return verticalScrollBar;
    }

    private JScrollBar createHorizontalScrollBar() {
        if (horizontalScrollBar == null) {
            horizontalScrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
            horizontalScrollBar.addAdjustmentListener(new FunctionScrollBar());
            horizontalScrollBar.setEnabled(false);
        }

        return horizontalScrollBar;
    }

    public void setPlayButtonIcon(ImageIcon icon) {
        playBT.setIcon(icon);
    }

    protected void repaintVisibleSignals() {
        if (drawableSignalsCheckBoxes == null) {
            return;
        }
        checkBoxesPanel.removeAll();
        float paintVolume = checkBoxesPanel.getHeight() / (float) signalsWindowProvider.getNumberOfVisibleChannels();
        int index = signalsWindowProvider.getFirstVisibleChannel();

        int checkBoxShift = (int) (drawableSignalsCheckBoxes[0].getPreferredSize().getHeight() / 2);
        
        for (int i = 0; i < signalsWindowProvider.getNumberOfVisibleChannels(); i++) {
            drawableSignalsCheckBoxes[index].setBounds(new Rectangle(0, (int) (i * paintVolume - checkBoxShift + paintVolume / 2f), CH_BOXES_WIDTH, drawableSignalsCheckBoxes[index].getPreferredSize().height));

            checkBoxesPanel.add(drawableSignalsCheckBoxes[index]);

            index++;
        }

        repaint();
        validate();
    }

    public void setNumberOfSelectedSignalsButtonsEnabled(boolean decreaseButtonEnabled, boolean increaseButtonEnabled) {
        increaseNumberOfChannelsButton.setEnabled(increaseButtonEnabled);
        decreaseNumberOfChannelsButton.setEnabled(decreaseButtonEnabled);
    }

    public int getSelectedFunctionIndex() {

        switch (Integer.valueOf(buttonGroup.getSelection().getActionCommand()).intValue()) {
            case Const.SELECT_EPOCH:
                return Const.SELECT_EPOCH;
            case Const.UNSELECT_EPOCH:
                return Const.UNSELECT_EPOCH;
            case Const.SELECT_ARTEFACT:
                return Const.SELECT_ARTEFACT;
            case Const.UNSELECT_ARTEFACT:
                return Const.UNSELECT_ARTEFACT;
            case Const.SELECT_PLAYBACK:
                return Const.SELECT_PLAYBACK;
            case Const.BASELINE_CORRECTION:
                return Const.BASELINE_CORRECTION;
            default:
                return Const.SELECT_NOTHING;
        }
    }

    public void setHorizontalScrollbarValue(int value) {
        horizontalScrollBar.setValue(value);
    }


    /**
     * Obsluha feedScrollBaru Pøi pøesouvání v záznamu pomocí scrollBaru pøedává
     * drawingComponente hodnotu scrollBaru.
     */
    private class FunctionScrollBar implements AdjustmentListener {

        public void adjustmentValueChanged(AdjustmentEvent arg0) {
            drawingComponent.setFirstFrame(horizontalScrollBar.getValue());
        }
    }

    /**
     * Obsluha tlaèítka SaveConfiguration Ukládá nastavene hodnoty do promìnných
     * start- a end- EpochValue a nastaví je v drawingComponente.
     */
    private class FunctionSaveEpochInterval implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            signalsWindowProvider.saveEpochInterval();
        }
    }

    /**
     * Obsluha toggleButtonù výbìru funkce 
     * Slouží k výbìru funkce, kterou chce uživatel využít.
     */
    private class FunctionSelectedFunction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            switch (Integer.valueOf(e.getActionCommand()).intValue()) {
                case Const.SELECT_EPOCH:
                    signalsWindowProvider.setSelectedFunction(Const.SELECT_EPOCH);
                    break;
                    
                case Const.UNSELECT_EPOCH:
                    signalsWindowProvider.setSelectedFunction(Const.UNSELECT_EPOCH);
                    break;
                    
                case Const.SELECT_ARTEFACT:
                    signalsWindowProvider.setSelectedFunction(Const.SELECT_ARTEFACT);
                    break;
                    
                case Const.UNSELECT_ARTEFACT:
                    signalsWindowProvider.setSelectedFunction(Const.UNSELECT_ARTEFACT);
                    break;
                    
                case Const.SELECT_PLAYBACK:
                    signalsWindowProvider.setSelectedFunction(Const.SELECT_PLAYBACK);
                    break;
                    
                case Const.BASELINE_CORRECTION:
                    signalsWindowProvider.setSelectedFunction(Const.BASELINE_CORRECTION);
                    break;    
                  
            }
        }
    }

    /**
     * Obsluha tlaèítka playBT Pøí stisknutí se záznam spustí, pøi stisknutí v
     * bìžícím režimu se data zastaví.
     */
    private class FunctionPlayBT implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            signalsWindowProvider.togglePause();
        }
    }

    /**
     * Obsluha tlaèítka stopBT Slouží k ukonèení pøehrávání. Pøesune pøehrávání
     * na zaèátek.
     */
    private class FunctionStopBT implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            signalsWindowProvider.stopPlayback();
        }
    }
        
    private class FunctionVerticalScrollBar implements AdjustmentListener {

        public void adjustmentValueChanged(AdjustmentEvent arg0) {
            signalsWindowProvider.setFirstVisibleChannel(verticalScrollBar.getValue());
        }
    }
}
