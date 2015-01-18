package icp.online.gui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import org.jfree.ui.RefineryUtilities;

public class ShowChart extends AbstractAction {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MainFrame mainFrame;
	private EpochCharts chart;

    @Override
    public void actionPerformed(ActionEvent actionevent) {
    	  
          this.chart.pack();
          RefineryUtilities.centerFrameOnScreen(this.chart);
          this.chart.setVisible(true);

    }

    public ShowChart (MainFrame mainFrame) {
        super();
        this.chart = new EpochCharts("Epoch averages");
        this.mainFrame = mainFrame;
        putValue("AcceleratorKey", KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        putValue("Name", "Show charts");
    }

	public void update(double[][] pzAvg) {
		this.chart.update(pzAvg);
		
	}
}