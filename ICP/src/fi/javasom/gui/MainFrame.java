package fi.javasom.gui;
//
// Main frame for the Clusoe GUI.
//
//  Copyright (C) 2001-2004  Tomi Suuronen
//
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; either version 2 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * Main frame for the Clusoe GUI.
 * <p>
 * Copyright (C) 2001-2004  Tomi Suuronen
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * @version 1.1
 * @since 1.0
 * @author Tomi Suuronen
*/
public class MainFrame extends JFrame implements ActionListener {
    
	private JMenuBar menuBar;
	private JMenu helpMenu;
	private JMenu settingsMenu;
	private JMenuItem aboutItem;
    private JMenuItem restoreItem;
    private JMenuItem saveItem;
	private JMenuItem clearItem;
	private JMenuItem pdfItem;
	private AboutDialog aboutDialog;
	private Dimension screenSize;
	private MainPanel mainPanel;

	/*
	 * Creates MenuBar.
	*/
	private void makeMenuBar() {
		menuBar = new JMenuBar();
		helpMenu = new JMenu("Help");
		settingsMenu = new JMenu("Settings");
		aboutItem = new JMenuItem("About");
        restoreItem = new JMenuItem("Restore");
        saveItem = new JMenuItem("Save");
		clearItem = new JMenuItem("Clear");
		pdfItem = new JMenuItem("PDF");
        restoreItem.addActionListener(this);
        saveItem.addActionListener(this);
		clearItem.addActionListener(this);
		aboutItem.addActionListener(this);
		pdfItem.addActionListener(this);
		helpMenu.add(aboutItem);
        settingsMenu.add(restoreItem);
        settingsMenu.add(saveItem);
		settingsMenu.add(clearItem);
		settingsMenu.add(pdfItem);
		menuBar.add(settingsMenu);
		menuBar.add(helpMenu);
	}

	/*
	 * Performs actions.
	*/
	public void actionPerformed(ActionEvent ev) {
		Object source = ev.getSource();

		//about item action
		if (source == aboutItem) {
			if (aboutDialog == null) {
				aboutDialog = new AboutDialog(this, screenSize);
			}
			aboutDialog.setVisible(true);
		}

		//clear all fields action
		if (source == clearItem) {
			mainPanel.clearAllFields();
		}

		//pdf item action
		if (source == pdfItem) {
			mainPanel.activatePdfSettings();
		}
        
        //save all fields action
		if (source == saveItem) {
			mainPanel.saveSettings();
		}
        
        //restore settings
		if (source == restoreItem) {
			mainPanel.restoreSettings();
		}
	}

	/*
	 * Constructor for MainFrame.
	*/
	public MainFrame() {
		int width = 510;
        int height = 600;
		setTitle("Clusoe");
		setSize(width,height);
		setResizable(false);
		Toolkit tk = Toolkit.getDefaultToolkit();
		screenSize = tk.getScreenSize();
		setLocation((screenSize.width - width) / 2 , (screenSize.height - height) / 2);
		Image img = tk.getImage(System.getProperty("user.dir")+"/img/logo.gif");
		setIconImage(img);

		//adds a windows listener
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		//adds the menubar
		makeMenuBar();
		setJMenuBar(menuBar);

		// add the main panel
		mainPanel = new MainPanel(this, screenSize);
		Container contentPane = getContentPane();
		contentPane.add(mainPanel);
	}
}
