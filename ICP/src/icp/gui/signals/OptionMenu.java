package icp.gui.signals;

import java.awt.event.*;

import javax.swing.*;

/**
 * Tøída vytváøející popup-menu, 
 * které umožòuje funkce segmentace signálù a oznaèení artefaktù.
 * 
 * @author Petr - Soukal
 */
public class OptionMenu extends JPopupMenu
{
	private SignalsWindowProvider signalsWindowProvider;
	private JMenuItem selectEpoch;
	private JMenuItem setPlaybackIndicator;
	private JMenuItem unselectEpoch;
	private JMenuItem unselectAllEpochs;
	private long frame;
	
	/**
	 * Vytváøí objekt dané tøídy a tlaèítka menu.
	 * 
	 * @param signalsWindowProvider - objekt tøídy SignalsWindowProvider pro komunikaci
	 * s ostatními tøídami prezentaèní vrstvy.
	 */
	public OptionMenu(SignalsWindowProvider signalsWindowProvider)
	{
		this.signalsWindowProvider = signalsWindowProvider;
		selectEpoch = new JMenuItem("Select Epoch");
		selectEpoch.addActionListener(new FunctionSelectEpoch());
		setPlaybackIndicator = new JMenuItem("Set Playback Indicator");
		setPlaybackIndicator.addActionListener(new FunctionSetPlaybackIndicator());
		unselectEpoch = new JMenuItem("Unselect Epoch");
		unselectEpoch.addActionListener(new FunctionUnselectEpoch());
		unselectAllEpochs = new JMenuItem("Unselect All Epochs");
		unselectAllEpochs.addActionListener(new FunctionUnselectAllEpochs());
		
		this.add(setPlaybackIndicator);	
		this.add(selectEpoch);
		this.addSeparator();
		this.add(unselectEpoch);
		this.addSeparator();
		this.add(unselectAllEpochs);
	}
	
	/**
	 * Nastavuje zobrazení popup-menu a jeho umístìní.
	 * 
	 * @param visualComponent - komponenta, ke které se menu váže.
	 * @param xAxis - x-ová souøadnice zobrazení menu.
	 * @param yAxis - y-ová souøadnice zobrazení menu.
	 * @param frame - místo v souboru, pøepoèítané ze souøadnic kliku.
	 */
	public void setVisibleMenu(JComponent visualComponent, int xAxis, int yAxis, long frame)
	{
		this.frame = frame;
		this.show(visualComponent, xAxis, yAxis);
	}
	
	/**
	 * Nastavuje povolení/zakázání jednotlivých tlaèítek.
	 * 
	 * @param enabledSelEpoch - povolení/zakázání oznaèení epochy.
	 * @param enabledUnselEpoch - povolení/zakázání odznaèení epochy.
	 * @param enabledUnselArtefact - povolení/zakázání odznaèení artefaktu.
	 * @param enabledUnselAllEpochs - povolení/zakázání odznaèení všech epoch.
	 * @param enabledUnselAllArtefacts - povolení/zakázání odznaèení všech artefaktù.
	 * @param enabledUnselAll - povolení/zakázání odznaèení všeho.
	 */
	public void setEnabledItems(boolean enabledSelEpoch, boolean enabledUnselEpoch, boolean enabledUnselArtefact,
			boolean enabledUnselAllEpochs, boolean enabledUnselAllArtefacts, boolean enabledUnselAll)
	{
		selectEpoch.setEnabled(enabledSelEpoch);
		unselectEpoch.setEnabled(enabledUnselEpoch);
		unselectAllEpochs.setEnabled(enabledUnselAllEpochs);
	}
	
	/**
	 * Obsluhuje akci pøi stisknutí tlaèítka oznaèení epochy.
	 */
	private class FunctionSelectEpoch implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	signalsWindowProvider.selectEpoch(frame);
        }
    }
	
	/**
	 * Obsluhuje akci pøi stisknutí tlaèítka nastavení ukazatele pøehrávání.
	 */
	private class FunctionSetPlaybackIndicator implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	signalsWindowProvider.getDrawingComponent().setPlaybackIndicatorPosition(frame);
        }
    }
	
	/**
	 * Obsluhuje akci pøi stisknutí tlaèítka odznaèení epochy.
	 */
	private class FunctionUnselectEpoch implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	signalsWindowProvider.unselectEpoch(frame);
        }
    }
	
	/**
	 * Obsluhuje akci pøi stisknutí tlaèítka odznaèení všech epoch.
	 */
	private class FunctionUnselectAllEpochs implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	signalsWindowProvider.unselectAllEpochs();
        }
    }
	
	
	/**
	 * Obsluhuje akci pøi stisknutí tlaèítka odznaèení všeho.
	 */
	private class FunctionUnselectAll implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	signalsWindowProvider.unselectAllEpochsAndArtefacts();
        }
    }
}
