package fi.javasom.gui;

//
// About dialog for the Clusoe GUI.
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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fi.javasom.application.Version;



/**
 * About dialog for the Clusoe GUI.
 * <p>
 * Copyright (C) 2001-2006 Tomi Suuronen
 * <p>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * @version 1.2
 * @since 1.0
 * @author Tomi Suuronen
 */
public class AboutDialog extends JDialog {

	/*
	 * Constructor.
	 */
	public AboutDialog(JFrame parent, Dimension screenSize) {
		super(parent, "About JSOM and Clusoe", true);
		setResizable(false);
		int width = 350;
		int height = 375;
		Box b = Box.createVerticalBox();
		b.add(new JLabel(" "));
		b.add(new JLabel("     JSOM and Clusoe (javasom package)"));
		b.add(new JLabel("     Versions " + Version.VERSION));
		b.add(new JLabel("     Copyright (C) 2001-2006 Tomi Suuronen"));
		b.add(new JLabel("     tomi.suuronen@iki.fi"));
		b.add(new JLabel(" "));
		b
				.add(new JLabel(
						"     This program comes with ABSOLUTELY NO WARRANTY."));
		b.add(new JLabel("     This is free software, and you are welcome to"));
		b.add(new JLabel("     redistribute it under certain conditions. See"));
		b.add(new JLabel(
				"     the GNU General Public License for more details."));
		b.add(new JLabel("     (gpl.txt)"));
		b.add(new JLabel(" "));
		b
				.add(new JLabel(
						"     This product includes software developed by the"));
		b.add(new JLabel(
				"     Apache Software Foundation (Xerces, Xalan, Batik,"));
		b.add(new JLabel("     Avalon Framework, Log4j and FOP)."));
		b.add(new JLabel("     (asl.txt and http://www.apache.org/)"));
		b.add(new JLabel(" "));
		b.add(new JLabel("     Special Thanks to:"));
		b.add(new JLabel("     Jarno Elovirta, Kal Ahmed, Merle Maxine"));
		b.add(Box.createGlue());
		getContentPane().add(b, "Center");

		JPanel buttonPanel = new JPanel();
		JButton closeButton = new JButton("Close");
		buttonPanel.add(closeButton);
		getContentPane().add(buttonPanel, "South");

		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				setVisible(false);
			}
		});
		setSize(width, height);
		setLocation((screenSize.width - width) / 2,
				(screenSize.height - height) / 2);
	}
}
