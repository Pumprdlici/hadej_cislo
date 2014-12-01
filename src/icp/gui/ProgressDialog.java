package icp.gui;

import icp.Const;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;


public class ProgressDialog extends JDialog
{
	private int DWIDTH = 300, DHEIGHT = 100;
	private int PWIDTH = 250, PHEIGHT = 20;
	private Component window;
	private JProgressBar progressBar;
	private double totalProgressUnits;
	private MainWindowProvider mainWindowProvider;
	
	
	public ProgressDialog(Component window, String title, MainWindowProvider mainWindowProvider)
	{
		super(mainWindowProvider.mainWindow);
		this.mainWindowProvider = mainWindowProvider;
		this.setTitle(title);
		this.window = window;
		this.add(interior());
		this.setSize(DWIDTH, DHEIGHT);
		this.setResizable(false);
		this.setActualLocationAndVisibility();
		
		this.addWindowListener(new WindowAdapter() {
		    @Override
			public void windowClosing(WindowEvent e)
			{				
				stornoDialog();	
			}
		});
	}
	
	private JPanel interior()
	{
		JPanel panel = new JPanel(new BorderLayout());
		
		JPanel progressPanel = new JPanel();
		progressBar = new JProgressBar(0, Const.PROGRESS_MAX);
		progressBar.setStringPainted(true);
		progressBar.setPreferredSize(new Dimension(PWIDTH, PHEIGHT));
		progressBar.setMaximumSize(new Dimension(PWIDTH, PHEIGHT));
		progressBar.setMinimumSize(new Dimension(PWIDTH, PHEIGHT));
		progressPanel.add(progressBar);
		
		JPanel buttonPanel = new JPanel();
		JButton stornoBT = new JButton("Storno");
		stornoBT.addActionListener(new FunctionStornoBT());
		buttonPanel.add(stornoBT);
		
		panel.add(progressPanel, BorderLayout.NORTH);
		panel.add(buttonPanel, BorderLayout.SOUTH);
		
		return panel;
	}
	
	public void setProgressUnits(double units) {
		
		totalProgressUnits += units;
		progressBar.setValue((int) totalProgressUnits);
		
		if(Math.round(totalProgressUnits) >= Const.PROGRESS_MAX)
		{
			window.setEnabled(true);        	
        	this.setVisible(false);
        	progressBar.setValue(0);
		}
    }
	
	/**
	 * Nastavuje viditelnost dialogu a jeho umístìní na monitoru.
	 */
	public void setActualLocationAndVisibility()
	{
		window.setEnabled(false);
		this.setLocationRelativeTo(window);
		this.setVisible(true);
	}
	
	public void stornoDialog()
	{
		window.setEnabled(true);
    	ProgressDialog.this.setVisible(false);
    	this.mainWindowProvider.app.stopProcess();
	}
	
	private class FunctionStornoBT implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	stornoDialog();
        }
    }
}
