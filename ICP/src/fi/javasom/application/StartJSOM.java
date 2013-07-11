package fi.javasom.application;

//
// Starts JSom from console.
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.Timer;

import org.apache.log4j.Logger;

/**
 * Starts JSom from console.
 * <p>
 * Copyright (C) 2001-2004 Tomi Suuronen
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
public class StartJSOM implements JSomProgressListener {

	/** Log instance */
	private static final Logger _log = Logger.getLogger(StartJSOM.class);

	private File _file;

	private static StartJSOM _start;

	private JSom _main;

	private Timer _timer;

	/*
	 * Constructor.
	 */
	public StartJSOM(String absolutePath) {
		notification();
		try {
			_file = new File(absolutePath);
			if (_file.isFile()) {
				ArrayList listeners = new ArrayList();
				listeners.add(this);
				_timer = new Timer(1000, new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						_log.info(".");
						if (!_main.isAlive()) {
							_log.info(" 100%\n");
							_timer.stop();
							System.exit(0);
						}
					}
				});
				_log.info("0% ");
				_timer.start();
				_main = new JSom(_file, listeners);
				_main.start();
			}
		} catch (Exception e) {
			_log.error(e.getMessage(), e);
		}
	}

	/*
	 * Starts JSom from console. @param String[] - first argument is the file
	 * for the instructions.
	 */
	public static void start(String[] args) {
		_start = new StartJSOM(args[0]);
	}

	/*
	 * Prints out the copyright information.
	 */
	private void notification() {
		_log.info("");
		_log.info("JSOM and Clusoe (javasom package)");
		_log.info("Versions 1.2");
		_log.info("Copyright (C) 2001-2006 Tomi Suuronen");
		_log.info("tomi.suuronen@iki.fi");
		_log.info("");
		_log.info("This program comes with ABSOLUTELY NO WARRANTY.");
		_log.info("This is free software, and you are welcome to");
		_log.info("redistribute it under certain conditions. See");
		_log.info("the GNU General Public License for more details.");
		_log.info("(gpl.txt)");
		_log.info("");
		_log.info("This product includes software developed by the");
		_log.info("Apache Software Foundation (Xerces, Xalan, Batik,");
		_log.info("Avalon Framework, Log4j and FOP).");
		_log.info("(asl.txt and http://www.apache.org/)");
		_log.info("");
		_log.info("Special Thanks to:");
		_log.info("Jarno Elovirta, Kal Ahmed, Merle Maxine");
		_log.info("");
	}

	/*
	 * public void actionPerformed(ActionEvent e) {}
	 */
	/*
	 * Start batch.
	 */
	public void startBatch(String batchName) {
	}

	/*
	 * End batch.
	 */
	public void endBatch(String batchName) {
	}

	/*
	 * Progress of a batch.
	 */
	public void batchProgress(int count, int max) {
	}
}
