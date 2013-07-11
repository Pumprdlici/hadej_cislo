package fi.javasom.gui;
//
//  This is the base handler for the settings properties. 
//  Copyright (C) 2001-2002  Tomi Suuronen
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

import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import fi.javasom.application.*;

import java.io.*;
import java.util.*;

/**
 * This is the base handler for the settings properties.
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
 * @since 1.1
 * @author Tomi Suuronen
*/
public class SettingsHandler extends DefaultHandler {
    
	private Hashtable rules = new Hashtable();
	private Stack stack = new Stack();
    
	//input element
	private boolean input;
	private SAXParser parser;
	private File inputFile;

	//initialization element
	private boolean init;
	private String lattice;
	private int xDim;
	private int yDim;
	private boolean normalization;
	private String neighbourhood;

	//ordering element
	private boolean training;
	private Vector steps;
	private Vector lrate;
	private Vector radius;
	private Vector lrateType;

	//output element
	private boolean output;
	private File folder;
	private String fileName; //name of the file to be created
	private String paper; //the size of a paper for pdf
	private String orientation; //orientation of a paper for pdf
	private boolean xml = false;
	private boolean svg = false;
	private boolean pdf = false;

    /**
	 * Constructor.
	*/
	public SettingsHandler() {
		super();
         
		//input element
		input = false;
	    inputFile = null;
        
		//initialization element
		init = false;
		lattice = "";
		xDim = 1;
		yDim = 1;
		normalization = true;
		neighbourhood = "";

		//ordering element
		training = false;
        steps = new Vector();
        lrate = new Vector();
        radius = new Vector();
        lrateType = new Vector();

		//output element
		output = false;
		folder = null;
        
        setElementHandler("input", new InputHandler());
		setElementHandler("file", new DataFileHandler());
		setElementHandler("initialization", new InitializationHandler());
		setElementHandler("normalization", new NormalizationHandler());
		setElementHandler("x.dimension", new XDimensionHandler());
		setElementHandler("y.dimension", new YDimensionHandler());
		setElementHandler("lattice", new LatticeHandler());
		setElementHandler("neighbourhood", new NeighbourhoodHandler());
		setElementHandler("training", new TrainingHandler());
		setElementHandler("steps", new StepsHandler());
		setElementHandler("lrate", new LrateHandler());
		setElementHandler("radius", new RadiusHandler());
		setElementHandler("output", new OutputHandler());
		setElementHandler("folder", new FolderHandler());
		setElementHandler("identifier", new IdentifierHandler());
		setElementHandler("type", new TypeHandler());
	}
    
    /*
     * Define processing for an element type.
    */
	private void setElementHandler(String name, ElementHandler handler) {
		rules.put(name, handler);
	}

	/*
	 * Start of an element. Decide what handler to use, and call it.
	*/
	public void startElement(String namespaceURI, String name, String qName, 
            Attributes atts) throws SAXException {
		ElementHandler handler = (ElementHandler) rules.get(name);
		stack.push(handler);
		if (handler!=null) {
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
     * Returns the input data file specified in a saved settings file.
     *
     * @return File - Saved input data file.
    */
    public File getInputFile() {
        return inputFile;
    }
    
    /**
     * Returns the information about possible normalization.
     *
     * @return boolean - True if normalization is used, otherwise false.
    */
    public boolean getNormalization() {
        return normalization;
    }
    
    /**
     * Returns the x-dimensionality of a map.
     *
     * @return int - dimension (size).
    */
    public int getXDim() {
        return xDim;
    }
    
    /**
     * Returns the y-dimensionality of a map.
     *
     * @return int - dimension (size).
    */
    public int getYDim() {
        return yDim;
    }
    
    /**
     * Returns the type of lattice for a map.
     *
     * @return String - Type of lattice.
    */
    public String getLattice() {
        return lattice;
    }
    
    /**
     * Returns the type of neighbourhood for a map.
     *
     * @return String - Type of neighbourhood.
    */
    public String getNeighbourhood() {
        return neighbourhood;
    }
    
    /**
     * Returns the type of paper used.
     *
     * @return String - Paper.
    */
    public String getPaper() {
        return paper;
    }
    
    /**
     * Returns the type of orientation for paper.
     *
     * @return String - Orientation.
    */
    public String getOrientation() {
        return orientation;
    }
    
    /**
     * Returns if XML type of output is selected.
     *
     * @return boolean - Typer of output.
    */
    public boolean getXml() {
        return xml;
    }
    
    /**
     * Returns if PDF type of output is selected.
     *
     * @return boolean - Typer of output.
    */
    public boolean getPdf() {
        return pdf;
    }
    
    /**
     * Returns if SVG type of output is selected.
     *
     * @return boolean - Typer of output.
    */
    public boolean getSvg() {
        return svg;
    }
    
    /**
     * Returns output filename (identifier).
     *
     * @return String - Identifier.
    */
    public String getOutputFileName() {
        return fileName;
    }
    
    /**
     * Returns the folder for saving the output.
     *
     * @return File - Save folder.
    */
    public File getSaveFolder() {
        return folder;
    }
    
    /**
     * Returns steps in all training sets.
     *
     * @return Vector - Steps in training sets.
    */
    public Vector getSteps() {
        return steps;
    }
    
    /**
     * Returns learning rates in all training sets.
     *
     * @return Vector - Learning rates in training sets.
    */
    public Vector getLearningRates() {
        return lrate;
    }
    
    /**
     * Returns radiuses in all training sets.
     *
     * @return Vector - Radiuses in training sets.
    */
    public Vector getRadiuses() {
        return radius;
    }
    
    /**
     * Returns learning rate types in all training sets.
     *
     * @return Vector - Learning rate types in training sets.
    */
    public Vector getLearningRateTypes() {
        return lrateType;
    }
    
    /**************************************************************************/

	/*
	 * Handles the input element events.
	*/
	private class InputHandler extends ElementHandler {
		
        /**
		 * Start of an element.
		*/
		public void startElement (String namespaceURI, String name,
                String qName, Attributes atts) throws SAXException {
			input = true;
		}

		/**
		 * End of an element.
		*/
		public void endElement (String uri, String name, String qName) throws SAXException {
			input = false;
		}
	}

	/**************************************************************************/

	/*
	 * Handles the file element events.
	*/
	private class DataFileHandler extends ElementHandler {
		
        /**
		* Character data.
		*/
		public void characters (char[] chars, int start, int len) throws SAXException {
			if (input) {
                inputFile = new File(String.valueOf(chars, start, len));
                if (!inputFile.isFile()) {
                    inputFile = null;				
                }
            }
		}
	}

	/**************************************************************************/

	/*
	 * Handles the initialization element events.
	*/
	private class InitializationHandler extends ElementHandler {
		
        /**
		 * Start of an element.
		*/
		public void startElement (String namespaceURI, String name,
                String qName, Attributes atts) throws SAXException {
			init = true;
		}

		/**
		 * End of an element.
		*/
		public void endElement (String uri, String name, String qName) throws SAXException {
			init = false;
		}
	}

	/**************************************************************************/

	/*
	 * Handles the normalization element events.
	*/
	private class NormalizationHandler extends ElementHandler {
		
        /**
		 * Start of an element.
		*/
		public void startElement (String namespaceURI, String name, 
                String qName, Attributes atts) throws SAXException {
			if (init) {
				normalization = (new Boolean(atts.getValue(0))).booleanValue();
			}
		}
	}

	/**************************************************************************/

	/*
	 * Handles the x-dimension element events.
	*/
	private class XDimensionHandler extends ElementHandler {
		
        /**
		* Character data.
		*/
		public void characters (char[] chars,int start,int len) throws SAXException {
			if (init) {
				try {
					xDim = (Integer.valueOf(String.valueOf(chars, start,
                            len).toLowerCase())).intValue();
				} catch(NumberFormatException nfe) {
					System.out.println(nfe.getMessage());
				}
			}
		}
	}

	/**************************************************************************/

	/*
	 * Handles the y-dimension element events.
	*/
	private class YDimensionHandler extends ElementHandler {
		/**
		* Character data.
		*/
		public void characters (char[] chars, int start, int len) throws SAXException {
			if (init) {
				try {
					yDim = (Integer.valueOf(String.valueOf(chars, start,
                            len).toLowerCase())).intValue();
				} catch(NumberFormatException nfe) {
					System.out.println(nfe.getMessage());
				}
			}
		}
	}

	/**************************************************************************/

	/*
	 * Handles the lattice element events.
	*/
	private class LatticeHandler extends ElementHandler {
		
        /**
		* Start of an element.
		*/
		public void startElement (String namespaceURI, String name,
                String qName, Attributes atts) throws SAXException {
			if (init) {
				if (atts.getValue(0).toLowerCase().equals("rectangular")) {
					lattice = "rect";
				} else {//hexagonal
					lattice = "hexa";
				}
			}
		}
	}

	/**************************************************************************/

	/*
	 * Handles the neighbourhood element events.
	*/
	private class NeighbourhoodHandler extends ElementHandler {
		
        /**
		* Start of an element.
		*/
		public void startElement (String namespaceURI, String name,
                String qName, Attributes atts) throws SAXException {
			if (init) {
				if (atts.getValue(0).toLowerCase().equals("gaussian")) {
					neighbourhood = "gaussian";
				} else {//step
					neighbourhood = "step";
				}
			}
		}
	}

	/**************************************************************************/

	/*
	 * Handles the training element events.
	*/
	private class TrainingHandler extends ElementHandler {
		
        /**
		 * Start of an element.
		*/
		public void startElement (String namespaceURI, String name,
                String qName, Attributes atts) throws SAXException {
			training = true;
		}

		/**
		 * End of an element.
		*/
		public void endElement (String uri, String name, String qName) throws SAXException {
			training = false;
		}
	}

	/**************************************************************************/

	/*
	 * Handles the steps element events.
	*/
	private class StepsHandler extends ElementHandler {
		
        /**
		* Character data.
		*/
		public void characters (char[] chars, int start, int len) throws SAXException {
			if (training) {
				try {
					steps.addElement("" + (Integer.valueOf(String.valueOf(chars, start,
                            len).toLowerCase())).intValue());
				} catch(NumberFormatException nfe) {
					System.out.println(nfe.getMessage());
				}
			}
		}
	}

	/**************************************************************************/

	/*
	 * Handles the Lrate element events.
	*/
	private class LrateHandler extends ElementHandler {
		
        /**
		 * Start of an element.
		*/
		public void startElement (String namespaceURI, String name,
                String qName, Attributes atts) throws SAXException {
			if (training) {
				if (atts.getValue(0).toLowerCase().equals("exponential")) {
					lrateType.addElement("exponential");
				} else if(atts.getValue(0).toLowerCase().equals("linear")) {
					lrateType.addElement("linear");
				} else {
					lrateType.addElement("inverse");
				}
			}
		}

		/**
		* Character data.
		*/
		public void characters (char[] chars, int start, int len) throws SAXException {
			if (training) {
				try {
					lrate.addElement("" + (Double.valueOf(String.valueOf(chars, start,
                            len).toLowerCase())).doubleValue());
				} catch(NumberFormatException nfe) {
					System.out.println(nfe.getMessage());
				}
			}
		}
	}

	/**************************************************************************/

	/*
	 * Handles the radius element events.
	*/
	private class RadiusHandler extends ElementHandler {
		
        /**
		* Character data.
		*/
		public void characters (char[] chars, int start, int len) throws SAXException {
			if (training) {
				try {
					radius.addElement("" + (Integer.valueOf(String.valueOf(chars, start,
                            len).toLowerCase())).intValue());
				} catch(NumberFormatException nfe) {
					System.out.println(nfe.getMessage());
				}
			}
		}
	}

	/**************************************************************************/

	/*
	 * Handles the output element events.
	*/
	private class OutputHandler extends ElementHandler {
		
        /**
		 * Start of an element.
		*/
		public void startElement (String namespaceURI, String name,
                String qName, Attributes atts) throws SAXException {
			output = true;
			for (int i = 0; i < atts.getLength(); i++) {
				if (atts.getLocalName(i).toLowerCase().equals("paper")) {
					paper = atts.getValue(i).toLowerCase().trim();
					if (paper.equals("a4") || paper.equals("letter")) {
						//left empty purposely
					} else {
						paper = "a4";
					}
				} else {//orientation
					orientation = atts.getValue(i).toLowerCase().trim();
					if (orientation.equals("portrait") || orientation.equals("landscape")) {
						//left empty purposely
					} else {
						orientation = "landscape";
					}
				}
			}
		}

		/**
		 * End of an element.
		*/
		public void endElement (String uri, String name, String qName) throws SAXException {
			output = false;
		}
	}

	/**************************************************************************/

	/*
	 * Handles the folder element events.
	*/
	private class FolderHandler extends ElementHandler {
		
        /**
		* Character data.
		*/
		public void characters (char[] chars, int start, int len) throws SAXException {
			if (output)	{
                File cache = new File(String.valueOf(chars, start, len));
			    if (cache.isDirectory()) {
				    folder = cache;
				} else {
				    folder = null;
				}
			}
		}
	}

	/**************************************************************************/

	/*
	 * Handles the identifier element events.
	*/
	private class IdentifierHandler extends ElementHandler {
		
        /**
		* Character data.
		*/
		public void characters (char[] chars, int start, int len) throws SAXException {
			if (output) {
				fileName = String.valueOf(chars, start, len);
			}
		}
	}

	/**************************************************************************/

	/*
	 * Handles the output format type element events.
	*/
	private class TypeHandler extends ElementHandler {
		
        /**
		 * Start of an element.
		*/
		public void startElement (String namespaceURI, String name,
                String qName, Attributes atts) throws SAXException {
			if (output) {
				if (atts.getValue(0).toLowerCase().equals("xml")) {
					xml = true;
				}
				if (atts.getValue(0).toLowerCase().equals("svg")) {
					svg = true;
				}
				if(atts.getValue(0).toLowerCase().equals("pdf")) {
					pdf = true;
				}
			}
		}
	}
}
