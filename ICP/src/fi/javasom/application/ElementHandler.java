package fi.javasom.application;
//
// This is ElementHandler class that process the start and end tags and
// character data. This class itself does nothing, the
// real processing is done by its subclasses.
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

import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 * This is ElementHandler class that process the start and end tags and
 * character data. This class itself does nothing, the
 * real processing is done by its subclasses.
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
 * @version 1.0
 * @since 1.0
 * @author Tomi Suuronen
*/
public class ElementHandler extends DefaultHandler {
	
    /**
	 * Start of an element.
	*/
	public void startElement (String namespaceURI, String name, String qName,
            Attributes atts) throws SAXException {}

	/**
	 * End of an element.
	*/
	public void endElement (String uri, String name, String qName) throws SAXException {}

	/**
	* Character data.
	*/
	public void characters (char[] chars, int start, int len) throws SAXException {}

	/**
	 * Warning message.
	*/
	public void warning (SAXParseException e) throws SAXException {
		System.out.println(e.getMessage());
	}

	/**
	 * Error message.
	*/
	public void error (SAXParseException e) throws SAXException {
		System.out.println(e.getMessage());
	}

	/**
	 * Fatal Error message.
	*/
	public void fatalError (SAXParseException e) throws SAXException {
		System.out.println(e.getMessage());
    }
}

