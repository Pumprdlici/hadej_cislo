package icp.gui;

import icp.gui.result.ResultPanel;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class ResultDialog extends JDialog
{
	private JFrame mainWindow;
	private MainWindowProvider mainWindowProvider;
	private ResultPanel resultPanel;
	
	public ResultDialog(final JFrame mainWindow, final MainWindowProvider mainWindowProvider)
	{
		super(mainWindow);
		this.setTitle("Results of detection");
		this.mainWindow = mainWindow;
		this.mainWindowProvider = mainWindowProvider;
		resultPanel = new ResultPanel(this, mainWindowProvider.app.getAveraging().getElements(), mainWindowProvider.app, mainWindow);
		this.setSize(new Dimension(resultPanel.getPreferredSize().width, 
				resultPanel.getPreferredSize().height + 70));
		this.setResizable(false);
		
		this.add(createInterior());
		
		this.addWindowListener(new WindowAdapter() {
            @Override
			public void windowClosing(WindowEvent e)
			{				
				mainWindow.setEnabled(true);		
			}
		});
	}
	
	public JPanel createInterior()
	{
		JPanel interior = new JPanel(new BorderLayout());
		
		JButton okBT = new JButton("Ok");
		okBT.addActionListener(new FunctionOkBT());
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(okBT);
		
		interior.add(resultPanel, BorderLayout.NORTH);
		interior.add(buttonPanel, BorderLayout.SOUTH);
		
		return interior;
	}
	
	/**
	 * Nastavuje viditelnost dialogu a jeho umístìní na monitoru.
	 */
	public void setActualLocationAndVisibility()
	{
		mainWindow.setEnabled(false);
		this.setLocationRelativeTo(mainWindow);
		this.setVisible(true);
	}
	
	public MainWindowProvider getMainWindowProvider()
	{
		return mainWindowProvider;
	}
	
	/**
	 * Obsluhuje tlaèítko pro stornování akce a zavøení dialogu.
	 */
	private class FunctionOkBT implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	mainWindow.setEnabled(true);
        	ResultDialog.this.setVisible(false);
        }
    }
}
