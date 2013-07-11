package fi.javasom.application;

//
//Javasom logger.
//
//Copyright (C) 2006  Tomi Suuronen
//
//This program is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation; either version 2 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.Properties;

/**
 * Logger.
 * <p>
 * Copyright (C) 2006 Tomi Suuronen
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
 * @since 1.2
 * @author Tomi Suuronen
 */
public class JavasomLog {
	/**
	 * Returns Logger instance. If not already created then creates new one.
	 * 
	 * @param aClass
	 *            class which uses logger.
	 * @param prop
	 *            Properties for the logger.
	 * 
	 * @return Logger instance
	 */
	public static Logger getLogger(Class aClass, Properties prop) {
		configureLogger(prop);

		return Logger.getLogger(aClass);
	}

	/**
	 * Configures logger. param prop Properties for the logger.
	 * 
	 * @param prop
	 *            Properties for the logger.
	 */
	private static void configureLogger(Properties prop) {
		Properties p = new Properties();

		p
				.put("log4j.rootLogger", prop.getProperty("log.level")
						+ ", stdout, R");
		p.put("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
		p.put("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");

		// Pattern to output the caller's file name and line number.
		p.put("log4j.appender.stdout.layout.ConversionPattern", "%m%n");
		p.put("log4j.appender.R", "org.apache.log4j.RollingFileAppender");
		p.put("log4j.appender.R.File", prop.getProperty("log.path"));
		p.put("log4j.appender.R.MaxFileSize", "500KB");

		// Keep one backup file
		p.put("log4j.appender.R.MaxBackupIndex", "2");
		p.put("log4j.appender.R.layout", "org.apache.log4j.PatternLayout");
		p.put("log4j.appender.R.layout.ConversionPattern",
				"%d %5p [%t] (%C{1}) - %m%n");

		PropertyConfigurator.configure(p);
	}
}
