package fi.javasom.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import fi.javasom.gui.StartClusoe;



//
// Starter for javasom.
//
//  Copyright (C) 2006  Tomi Suuronen
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

/**
 * Starter for javasom.
 * <p>
 * Copyright (C) 2006  Tomi Suuronen
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
 * @since 1.2
 * @author Tomi Suuronen
*/
public class Starter {
	/** Logging connection. */
    private static Logger _log = null;
	
    /** Indicates the usage of Clusoe. */
    private static boolean _gui = false;
    
    /** Indicates the usage of help. */
    private static boolean _help = false;
    
    /**
     * Checks the arguments
     *
     * @param args Arguments to be checked.
     *
     * @return <CODE>true</CODE> if OK, otherwise <CODE>false</CODE>.
     */
    private static void checkArgs(String[] args) {
        int j = args.length;
        _help = false;
        _gui = false;
        
        if (args.length == 0) {
            _gui = true;
        } else if (args.length > 1) {
        	_help = true;
        } else {
        	if (args[0].equalsIgnoreCase("-h")
                    || args[0].equalsIgnoreCase("-help")) {
                _help = true;
             
            } else {
            	//must be an instructions file
                _gui = false;
            }
        }
    }
    
    /**
     * Prints help message.
     */
    private static void printHelp() {
        _log.info("\nFollowing arguments can be given:\n"
            + "  -h = This help. OR \n  [Instructions file] = Launches console OR \n  No arguments launches Clusoe.\n");
    }
    
    /**
     * 
     * @param args Arguments from console
     */
	public static void main(String[] args) {
		//properties
        Properties prop = new Properties();
        String cache = "";
        
        checkArgs(args);
        
        try {
            prop.load(new FileInputStream(new File("conf/log4j.properties")));
        } catch (FileNotFoundException fnfe) {
            cache = "Properties file: " + fnfe.getMessage();

            return;
        } catch (IOException ioe) {
            cache = "Properties file: " + ioe.getMessage();

            return;
        }

        _log = JavasomLog.getLogger(StartClusoe.class, prop);
        _log.info(Version.NAME + " ver. " + Version.VERSION + " started.");

        if (cache.length() > 0) {
            _log.error(cache);
        }
        
        if (_gui) {
        	StartClusoe.start(args);
        } else if (_help) {
        	printHelp();
        } else {
        	StartJSOM.start(args);
        }
	}
}
