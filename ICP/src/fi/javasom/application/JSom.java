package fi.javasom.application;
//
//  Main JSom class.
//
//  Copyright (C) 2001-2006  Tomi Suuronen
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.InputSource;

/**
 * Main JSom class which starts the whole training process based on the
 * instructions.
 * <p>
 * Copyright (C) 2001-2006  Tomi Suuronen
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
 * @version 1.2
 * @since 1.0
 * @author Tomi Suuronen
*/
public class JSom extends Thread {
	/** Log instance */
	private static final Logger _log = Logger.getLogger(JSom.class);
	
	private boolean error;
	private SAXParser parser;
	private JobInstructionsHandler handler;
    private InputSource src;
    
	/**
	 * Constructor.
	 *
	 * @param file The instructions in a file (follows the instructions.dtd).
     * @param listeners Listeners to follow the progress of process.
	*/
	public JSom(File file, ArrayList listeners) {
		error = false;
        parser = new org.apache.xerces.parsers.SAXParser();
		handler = new JobInstructionsHandler();
        int j = listeners.size();
        for (int i = 0; i < j; i++) {
		    handler.addProgressListener((JSomProgressListener) listeners.get(i));
		}
		parser.setContentHandler(handler);
		parser.setErrorHandler(handler);
		try {
			Reader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
			src = new InputSource(reader);
		} catch (Exception e) {
			error = true;
			_log.error("JSom(File,ArrayList): " + e.getMessage());
		}
	}

	/**
	 * Constructor.
	 *
	 * @param instructions The instructions in a string, formatted as the
     * instructions.dtd specifies.
     * @param listeners Listeners to follow the progress of process.
     */
	public JSom(String instructions, ArrayList listeners) {
		error = false;
		parser = new org.apache.xerces.parsers.SAXParser();
		handler = new JobInstructionsHandler();
        for (int i = 0; i < listeners.size(); i++) {
		    handler.addProgressListener((JSomProgressListener) listeners.get(i));
		}
		parser.setContentHandler(handler);
		parser.setErrorHandler(handler);
		try {
			Reader reader = new StringReader(instructions);
			src = new InputSource(reader);
		} catch (Exception e) {
			error = true;
			_log.error("JSom(String,ArrayList): " + e.getMessage());
		}
	}

	/**
	 * A convenient way to enquire if an error has occured during processing the instructions.
	 *
	 * @return boolean - true if an error has occurred otherwise false.
	 */
	public boolean anyExceptions() {
		return error;
	}
    
    /**
     * Starts executing the instructions.
    */
    public void run() {
        try {
            parser.parse(src);
	    } catch (Exception e) {
            error = true;
            _log.error("run(): " + e.getMessage(), e);
	    }
	}
}
