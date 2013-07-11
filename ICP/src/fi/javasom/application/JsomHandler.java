package fi.javasom.application;
//
// This is the base handler for javasom.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This is the base handler for javasom.
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
public class JsomHandler extends DefaultHandler {
    
	private Hashtable rules = new Hashtable();
	private Stack stack = new Stack();

	private ArrayList dimensions;
	private double[] doubleCache;
	private String labelCache;
	private String charCache;
	private String nameCache;
	private int indexCache;
	private InputVectors iVector;
	private JsomMap map;

	private boolean data;
	private boolean node;

	/**
	 * Constructor.
	*/
	public JsomHandler() {
		super();
		dimensions = new ArrayList();
		labelCache = "";
		charCache = "";
		nameCache= "";
		iVector = new InputVectors();
		map = new JsomMap();
		data = false;
		node = false;

		setElementHandler("meta", new MetaHandler());
		setElementHandler("author", new AuthorHandler());
		setElementHandler("project", new ProjectHandler());
		setElementHandler("code", new CodeHandler());
		setElementHandler("name", new NameHandler());
		setElementHandler("organization", new OrganizationHandler());
		setElementHandler("dimension", new DimensionHandler());
		setElementHandler("dim_type", new DimTypeHandler());
		setElementHandler("data", new DataHandler());
		setElementHandler("node", new NodeHandler());
		setElementHandler("dim", new DimHandler());
	}
    
	/*
    * Define processing for an element type.
    */
	private void setElementHandler(String name, ElementHandler handler) {
		rules.put(name, handler);
	}

	/**
	 * Start of an element. Decide what handler to use, and call it.
	*/
	public void startElement(String namespaceURI, String name, String qName,
            Attributes atts) throws SAXException {
		ElementHandler handler = (ElementHandler) rules.get(name);
		stack.push(handler);
		if (handler != null) {
		  handler.startElement(namespaceURI, name, qName, atts);
		}
	}

	/*
	 * End of an element.
	*/
	public void endElement(String uri, String name, String qName) throws SAXException {
		ElementHandler handler = (ElementHandler) stack.pop();
		if (handler != null) {
			handler.endElement(uri, name, qName);
		}
	}

	/*
	 * Character data.
	*/
	public void characters(char[] chars, int start, int len) throws SAXException {
		ElementHandler handler = (ElementHandler) stack.peek();
		if (handler != null) {
			handler.characters(chars, start, len);
        }
	}

	/*
	 * End of document.
	*/
	public void endDocument() throws SAXException {
	}

	/**
	 * Returns input vectors.
	 * @return InputVector - returns the input vectors.
	*/
	public InputVectors getInputVectors() {
		return iVector;
	}

	/**
	 * Returns node value count.
	 * @return int - returns dimensionality of a node in this map (this applies to all nodes).
	*/
	public int getNodeValueCount() {
		return dimensions.size();
	}

	/**
	 * Returns the JsomMAp object.
	 * @return JsomMap - returns JsomMap object.
	*/
	public JsomMap getJsomMap() {
		return map;
	}

	/**************************************************************************/

	/*
	 * Handles the dimension element events.
	*/
	private class MetaHandler extends ElementHandler {
		public void startElement (String namespaceURI, String name,
                String qName, Attributes atts) throws SAXException {
			if (atts.getValue("code") != null) {
				map.setCode(atts.getValue("code"));
			}
			if (atts.getValue("date") != null) {
				map.setDate(atts.getValue("date"));
			}
		}
	}

	/**************************************************************************/

	/*
	 * Handles the dimension element events.
	*/
	private class AuthorHandler extends ElementHandler {
		public void startElement (String namespaceURI, String name,
                String qName, Attributes atts) throws SAXException {
			nameCache = "";
			charCache = ""; // organization
		}

		public void endElement (String uri, String name, String qName) throws SAXException {
			map.addAuthor(nameCache, charCache);
		}
	}

	/**************************************************************************/

	/*
	 * Handles the project element events.
	*/
	private class ProjectHandler extends ElementHandler {
		public void startElement (String namespaceURI, String name,
                String qName, Attributes atts) throws SAXException {
			nameCache = "";
			charCache = ""; // project code
		}

		public void endElement (String uri, String name, String qName) throws SAXException {
			map.setProject(nameCache, charCache);
		}
	}

	/**************************************************************************/

	/*
	 * Handles the name element events.
	*/
	private class NameHandler extends ElementHandler {
		public void characters (char[] chars, int start, int len) throws SAXException {
			nameCache = String.valueOf(chars, start, len);
		}
	}

	/**************************************************************************/

	/*
	 * Handles the code element events.
	*/
	private class CodeHandler extends ElementHandler {
		public void characters (char[] chars, int start, int len) throws SAXException {
			charCache = String.valueOf(chars, start, len); // project code
		}
	}

	/**************************************************************************/

	/*
	 * Handles the organization element events.
	*/
	private class OrganizationHandler extends ElementHandler {
		public void characters (char[] chars, int start, int len) throws SAXException {
			charCache = String.valueOf(chars, start, len); // organization
		}
	}

	/**************************************************************************/

	/*
	 * Handles the dimension element events.
	*/
	private class DimensionHandler extends ElementHandler {
		public void endElement (String uri, String name, String qName) throws SAXException {
			doubleCache = new double[dimensions.size()];
			Arrays.fill(doubleCache, 0.0);
		}
	}

	/**************************************************************************/

	/*
	 * Handles the dim_type element events.
	*/
	private class DimTypeHandler extends ElementHandler {
		public void characters (char[] chars, int start, int len) throws SAXException {
			if (!dimensions.contains(String.valueOf(chars, start, len))) {
				dimensions.add(String.valueOf(chars, start, len));
			}
		}
	}

	/**************************************************************************/

	/*
	 * Handles the data element events.
	*/
	private class DataHandler extends ElementHandler {
		public void startElement (String namespaceURI, String name,
                String qName, Attributes atts) throws SAXException {
			data = true;
		}

		public void endElement (String uri, String name, String qName) throws SAXException {
			data = false;
		}
	}

	/**************************************************************************/

	/*
	 * Handles the node element events.
	*/
	private class NodeHandler extends ElementHandler {
		public void startElement (String namespaceURI, String name,
                String qName, Attributes atts) throws SAXException {
			if (data == true) {
				node = true;
				labelCache = atts.getValue(0);
			}
		}

		public void endElement (String uri, String name, String qName) throws SAXException {
			if (data == true) {
				node = false;
				iVector.addInputVector(new SomNode(labelCache, doubleCache));
				Arrays.fill(doubleCache, 0.0);
			}
		}
	}

	/**************************************************************************/

	/*
	 * Handles the events in the dim element
	*/
	private class DimHandler extends ElementHandler {
		public void startElement (String namespaceURI, String name, String qName,Attributes atts) throws SAXException {
			if (node == true) {
				indexCache = dimensions.indexOf(atts.getValue(0));
			}
		}

		public void characters (char[] chars, int start, int len) throws SAXException {
			if (node == true) {
				try {
					doubleCache[indexCache] = Double.parseDouble(String.valueOf(chars, start, len));
				} catch(NumberFormatException nfe) {
					System.out.println(nfe.getMessage());
				}
			}
		}
	}
}
