package fi.javasom.application;

//
//  This is the base handler for instruction properties.
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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This is the base handler for gui properties.
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
public class JobInstructionsHandler extends DefaultHandler {

	/** Log instance */
	private static final Logger _log = Logger
			.getLogger(JobInstructionsHandler.class);

	private Hashtable rules = new Hashtable();

	private Stack stack = new Stack();

	private WeightVectors reference;

	private JsomMap map;

	private JSomTraining job;

	// input element
	private boolean input;

	private SAXParser parser;

	private JsomHandler handler2;

	private InputVectors inputVectors;

	// initialization element
	private boolean init;

	private String lattice;

	private int xDim;

	private int yDim;

	private boolean normalization;

	private JSomNormalization norm;

	private String neighbourhood;

	// ordering element
	private boolean training;

	private int steps;

	private double lrate;

	private int radius;

	private String lrateType;

	// output element
	private boolean output;

	private JSomLabeling labels;

	private File folder;

	private String fileName; // name of the file to be created

	private String paper; // the size of a paper for pdf

	private String orientation; // orientation of a paper for pdf

	private boolean xml = false;

	private boolean svg = false;

	private boolean pdf = false;

	// progress monitors
	private ArrayList listeners;

	/**
	 * Constructor.
	 */
	public JobInstructionsHandler() {
		super();

		// input element
		input = false;
		inputVectors = new InputVectors();

		// initialization element
		init = false;
		lattice = "";
		xDim = 1;
		yDim = 1;
		normalization = true;
		neighbourhood = "";

		// ordering element
		training = false;

		// output element
		output = false;
		folder = null;

		// progress monitors
		listeners = new ArrayList();

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

	/**
	 * Adds a progress listener.
	 * 
	 * @param listener
	 *            New listener.
	 */
	public void addProgressListener(JSomProgressListener listener) {
		listeners.add(listener);
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
		if (handler != null) {
			handler.startElement(namespaceURI, name, qName, atts);
		}
	}

	/*
	 * End of an element.
	 */
	public void endElement(String uri, String name, String qName)
			throws SAXException {
		ElementHandler handler = (ElementHandler) stack.pop();
		if (handler != null) {
			handler.endElement(uri, name, qName);
		}
	}

	/*
	 * Character data.
	 */
	public void characters(char[] chars, int start, int len)
			throws SAXException {
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

	/** *********************************************************************** */

	/*
	 * Handles the input element events.
	 */
	private class InputHandler extends ElementHandler {

		/**
		 * Start of an element.
		 */
		public void startElement(String namespaceURI, String name,
				String qName, Attributes atts) throws SAXException {
			input = true;
		}

		/**
		 * End of an element.
		 */
		public void endElement(String uri, String name, String qName)
				throws SAXException {
			input = false;
		}
	}

	/** *********************************************************************** */

	/*
	 * Handles the file element events.
	 */
	private class DataFileHandler extends ElementHandler {

		/**
		 * Character data.
		 */
		public void characters(char[] chars, int start, int len)
				throws SAXException {
			File file = new File(String.valueOf(chars, start, len));
			if (file.isFile()) {
				if (input) {
					parser = new org.apache.xerces.parsers.SAXParser();
					handler2 = new JsomHandler();
					parser.setContentHandler(handler2);
					parser.setErrorHandler(handler2);
					try {
						Reader reader = new BufferedReader(new FileReader(file));
						parser.parse(new InputSource(reader));
					} catch (Exception e) {
						_log.error(e.getMessage(), e);
					}
					inputVectors = handler2.getInputVectors();
					map = handler2.getJsomMap();
				}
			}
		}
	}

	/** *********************************************************************** */

	/*
	 * Handles the initialization element events.
	 */
	private class InitializationHandler extends ElementHandler {

		/**
		 * Start of an element.
		 */
		public void startElement(String namespaceURI, String name,
				String qName, Attributes atts) throws SAXException {
			init = true;
		}

		/**
		 * End of an element.
		 */
		public void endElement(String uri, String name, String qName)
				throws SAXException {
			init = false;
			reference = new WeightVectors(xDim, yDim, handler2
					.getNodeValueCount(), lattice);
		}
	}

	/** *********************************************************************** */

	/*
	 * Handles the normalization element events.
	 */
	private class NormalizationHandler extends ElementHandler {

		/**
		 * Start of an element.
		 */
		public void startElement(String namespaceURI, String name,
				String qName, Attributes atts) throws SAXException {
			if (init) {
				normalization = (new Boolean(atts.getValue(0))).booleanValue();
				if (normalization) {
					norm = new JSomNormalization(inputVectors, handler2
							.getNodeValueCount());
					inputVectors = norm.doNormalization();
				}
			}
		}
	}

	/** *********************************************************************** */

	/*
	 * Handles the x-dimension element events.
	 */
	private class XDimensionHandler extends ElementHandler {

		/**
		 * Character data.
		 */
		public void characters(char[] chars, int start, int len)
				throws SAXException {
			if (init) {
				try {
					xDim = (Integer.valueOf(String.valueOf(chars, start, len)
							.toLowerCase())).intValue();
				} catch (NumberFormatException nfe) {
					_log.error(nfe.getMessage(), nfe);
				}
			}
		}
	}

	/** *********************************************************************** */

	/*
	 * Handles the y-dimension element events.
	 */
	private class YDimensionHandler extends ElementHandler {
		/**
		 * Character data.
		 */
		public void characters(char[] chars, int start, int len)
				throws SAXException {
			if (init) {
				try {
					yDim = (Integer.valueOf(String.valueOf(chars, start, len)
							.toLowerCase())).intValue();
				} catch (NumberFormatException nfe) {
					_log.error(nfe.getMessage(), nfe);
				}
			}
		}
	}

	/** *********************************************************************** */

	/*
	 * Handles the lattice element events.
	 */
	private class LatticeHandler extends ElementHandler {

		/**
		 * Start of an element.
		 */
		public void startElement(String namespaceURI, String name,
				String qName, Attributes atts) throws SAXException {
			if (init) {
				if (atts.getValue(0).toLowerCase().equals("rectangular")) {
					lattice = "rect";
				} else {// hexagonal
					lattice = "hexa";
				}
			}
		}
	}

	/** *********************************************************************** */

	/*
	 * Handles the neighbourhood element events.
	 */
	private class NeighbourhoodHandler extends ElementHandler {

		/**
		 * Start of an element.
		 */
		public void startElement(String namespaceURI, String name,
				String qName, Attributes atts) throws SAXException {
			if (init) {
				if (atts.getValue(0).toLowerCase().equals("gaussian")) {
					neighbourhood = "gaussian";
				} else {// step
					neighbourhood = "step";
				}
			}
		}
	}

	/** *********************************************************************** */

	/*
	 * Handles the training element events.
	 */
	private class TrainingHandler extends ElementHandler {

		/**
		 * Start of an element.
		 */
		public void startElement(String namespaceURI, String name,
				String qName, Attributes atts) throws SAXException {
			training = true;
			job = new JSomTraining(reference, inputVectors);
			for (int i = 0; i < listeners.size(); i++) {
				job.addListener((JSomProgressListener) listeners.get(i));
			}
		}

		/**
		 * End of an element.
		 */
		public void endElement(String uri, String name, String qName)
				throws SAXException {
			training = false;
			job.setTrainingInstructions(steps, lrate, radius, lrateType,
					neighbourhood);
			reference = job.doTraining();
		}
	}

	/** *********************************************************************** */

	/*
	 * Handles the steps element events.
	 */
	private class StepsHandler extends ElementHandler {

		/**
		 * Character data.
		 */
		public void characters(char[] chars, int start, int len)
				throws SAXException {
			if (training) {
				try {
					steps = (Integer.valueOf(String.valueOf(chars, start, len)
							.toLowerCase())).intValue();
				} catch (NumberFormatException nfe) {
					_log.error(nfe.getMessage(), nfe);
				}
			}
		}
	}

	/** *********************************************************************** */

	/*
	 * Handles the Lrate element events.
	 */
	private class LrateHandler extends ElementHandler {

		/**
		 * Start of an element.
		 */
		public void startElement(String namespaceURI, String name,
				String qName, Attributes atts) throws SAXException {
			if (training) {
				if (atts.getValue(0).toLowerCase().equals("exponential")) {
					lrateType = "exponential";
				} else if (atts.getValue(0).toLowerCase().equals("linear")) {
					lrateType = "linear";
				} else {
					lrateType = "inverse";
				}
			}
		}

		/**
		 * Character data.
		 */
		public void characters(char[] chars, int start, int len)
				throws SAXException {
			if (training) {
				try {
					lrate = (Double.valueOf(String.valueOf(chars, start, len)
							.toLowerCase())).doubleValue();
				} catch (NumberFormatException nfe) {
					_log.error(nfe.getMessage(), nfe);
				}
			}
		}
	}

	/** *********************************************************************** */

	/*
	 * Handles the radius element events.
	 */
	private class RadiusHandler extends ElementHandler {

		/**
		 * Character data.
		 */
		public void characters(char[] chars, int start, int len)
				throws SAXException {
			if (training) {
				try {
					radius = (Integer.valueOf(String.valueOf(chars, start, len)
							.toLowerCase())).intValue();
				} catch (NumberFormatException nfe) {
					_log.error(nfe.getMessage(), nfe);
				}
			}
		}
	}

	/** *********************************************************************** */

	/*
	 * Handles the output element events.
	 */
	private class OutputHandler extends ElementHandler {

		/**
		 * Start of an element.
		 */
		public void startElement(String namespaceURI, String name,
				String qName, Attributes atts) throws SAXException {
			output = true;
			for (int i = 0; i < atts.getLength(); i++) {
				if (atts.getLocalName(i).toLowerCase().equals("paper")) {
					paper = atts.getValue(i).toLowerCase().trim();
					if (paper.equals("a4") || paper.equals("letter")) {
						// left empty purposely
					} else {
						paper = "a4";
					}
				} else {// orientation
					orientation = atts.getValue(i).toLowerCase().trim();
					if (orientation.equals("portrait")
							|| orientation.equals("landscape")) {
						// left empty purposely
					} else {
						orientation = "landscape";
					}
				}
			}
			// labels the weight vectors
			labels = new JSomLabeling(reference, inputVectors);
			for (int i = 0; i < listeners.size(); i++) {
				labels.addListener((JSomProgressListener) listeners.get(i));
			}
			reference = labels.doLabeling();
		}

		/**
		 * End of an element.
		 */
		public void endElement(String uri, String name, String qName)
				throws SAXException {
			output = false;

			/* Create a DOM tree of the generated map */
			JSomCreateDomTree domino = new JSomCreateDomTree(reference, map,
					30.0);

			/* Output */
			JSomMapOutput gogo = new JSomMapOutput(folder, domino, paper,
					orientation, map);
			if (xml) {
				gogo.outputMap("xml", fileName);
			}
			if (svg) {
				gogo.outputMap("svg", fileName);
			}
			if (pdf) {
				gogo.outputMap("pdf", fileName);
			}
			gogo = null;
		}
	}

	/** *********************************************************************** */

	/*
	 * Handles the folder element events.
	 */
	private class FolderHandler extends ElementHandler {

		/**
		 * Character data.
		 */
		public void characters(char[] chars, int start, int len)
				throws SAXException {
			File cache = new File(String.valueOf(chars, start, len));
			if (cache.isDirectory()) {
				if (output) {
					try {
						folder = cache;
					} catch (NullPointerException npe) {
						_log.error(npe.getMessage(), npe);
					}
				}
			} else {
				_log.error("Output path is not a folder !");
			}
		}
	}

	/** *********************************************************************** */

	/*
	 * Handles the identifier element events.
	 */
	private class IdentifierHandler extends ElementHandler {

		/**
		 * Character data.
		 */
		public void characters(char[] chars, int start, int len)
				throws SAXException {
			if (output) {
				fileName = String.valueOf(chars, start, len);
			}
		}
	}

	/** *********************************************************************** */

	/*
	 * Handles the output format type element events.
	 */
	private class TypeHandler extends ElementHandler {

		/**
		 * Start of an element.
		 */
		public void startElement(String namespaceURI, String name,
				String qName, Attributes atts) throws SAXException {
			if (output) {
				if (atts.getValue(0).toLowerCase().equals("xml")) {
					xml = true;
				}
				if (atts.getValue(0).toLowerCase().equals("svg")) {
					svg = true;
				}
				if (atts.getValue(0).toLowerCase().equals("pdf")) {
					pdf = true;
				}
			}
		}
	}
}
