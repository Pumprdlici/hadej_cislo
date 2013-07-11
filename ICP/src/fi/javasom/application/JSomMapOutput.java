package fi.javasom.application;

//
// This is JSomMapOutput class that outputs the map in wanted format.
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.fop.apps.Driver;
import org.apache.log4j.Logger;
import org.xml.sax.XMLReader;

/**
 * This is JSomMapOutput class that outputs the map in wanted format.
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
public class JSomMapOutput {
	/** Log instance */
	private static final Logger _log = Logger.getLogger(JSomMapOutput.class);

	private File _folder;

	private File _cache;

	private JSomCreateDomTree _dom; // DOM tree.

	private String _fileName; // name of the file to be created.

	private String _xslfile; // XSL file to be used.

	private String _paper; // the size of a paper for pdf.

	private String _orientation; // orientation of a paper for pdf.

	private StringWriter _embedSVG; // embedded SVG code.

	private JsomMap _map; // information about the map.

	/**
	 * Constructor.
	 * 
	 * @param File
	 *            folder - Output folder.
	 * @param DocuemntImpl
	 *            dom - Document DOM tree.
	 * @param String
	 *            paper - the size of the paper :: a4 | letter
	 * @param String
	 *            orientation - the orientation of the paper :: portrait ||
	 *            landscape
	 * @param JsomMap
	 *            map - the information about the map
	 */
	public JSomMapOutput(File folder, JSomCreateDomTree dom, String paper,
			String orientation, JsomMap map) {
		_folder = folder;
		_dom = dom;
		_paper = paper;
		_orientation = orientation;
		_map = map;
	}

	/**
	 * Outputs the map in the specified format with a specific name.
	 * 
	 * @param String
	 *            type = Type of the map:: xml | svg | pdf.
	 * @param String
	 *            fileName = name of the file (without a suffix).
	 */
	public void outputMap(String type, String fileName) {
		_fileName = fileName;
		type = type.toLowerCase();
		if (type.equals("pdf")) {
			try {
				// Hierarchy hierarchy = Hierarchy.getDefaultHierarchy();
				// Logger log = hierarchy.getLoggerFor("fop");
				// log.setPriority(Priority.WARN);

				_cache = new File(_folder, fileName + "." + type);
				_cache.createNewFile();
				_xslfile = "conf/jsom_svg_pdf.xsl";
				_embedSVG = new StringWriter(); // Creates an empy StringWriter
				// object for the output.
				transform("pdf");
				XMLReader parser = (XMLReader) new org.apache.xerces.parsers.SAXParser();
				FopConstructor fc = new FopConstructor(_paper, _orientation,
						_embedSVG.toString());
				/*
				 * deprecated Driver driver = new Driver();
				 * driver.setRenderer("org.apache.fop.render.pdf.PDFRenderer",Version.getVersion());
				 * driver.addElementMapping("org.apache.fop.fo.StandardElementMapping");
				 * driver.addElementMapping("org.apache.fop.svg.SVGElementMapping");
				 * driver.addPropertyList("org.apache.fop.fo.StandardPropertyListMapping");
				 * driver.addPropertyList("org.apache.fop.svg.SVGPropertyListMapping");
				 * driver.setOutputStream(new FileOutputStream(cache));
				 * driver.buildFOTree(parser,new org.xml.sax.InputSource(new
				 * StringReader(fc.getFopString()))); driver.format();
				 * driver.render();
				 */
				Driver driver = new Driver();
				org.apache.avalon.framework.logger.Logger logger = new ConsoleLogger(
						ConsoleLogger.LEVEL_INFO);
				driver.setLogger(logger);
				driver.setRenderer(Driver.RENDER_PDF);
				FileOutputStream fos = new FileOutputStream(_cache
						.getAbsolutePath());
				driver.setOutputStream(fos);
				driver.render(parser, new org.xml.sax.InputSource(
						new StringReader(fc.getFopString())));
				fos.close();
			} catch (Exception e) {
				_log.error("outputMap()-pdf: " + e.getMessage(), e);
			}
		} else {// XML and SVG
			try {
				if (type.equals("xml")) {
					_xslfile = "conf/jsom_copier.xsl";
				} else {// svg
					_xslfile = "conf/jsom_svg.xsl";
				}
				_cache = new File(_folder, fileName + "." + type);
				_cache.createNewFile();
				transform("save");
			} catch (IOException ioe) {
				_log.error("outputMap()-xml|svg: " + ioe.getMessage(), ioe);
			}
		}
	}

	/*
	 * Makes the transformation.
	 * 
	 * @param String function - type of transformation. save (saves into a file) |
	 * pdf.
	 */
	private void transform(String function) {
		// Uses TRAX API to transform the data
		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer(new StreamSource(
					_xslfile));
			if (function.equals("save")) {
				transformer.transform(new DOMSource(_dom.getDomTreeDocument()),
						new javax.xml.transform.stream.StreamResult(
								new FileOutputStream(_cache)));
			} else {// pdf
				transformer.transform(new DOMSource(_dom.getDomTreeDocument()),
						new javax.xml.transform.stream.StreamResult(_embedSVG));
			}
		} catch (Exception e) {
			_log.error("transform(): " + e.getMessage(), e);
		}
	}

	/** *********************************************************************** */

	/*
	 * Constructs the XSL Formatting Objects as a DocumentImpl.
	 */
	private class FopConstructor {
		private StringBuffer row;

		/**
		 * Constructor.
		 * 
		 * @param String
		 *            paper - the size of the paper :: A4 | Letter
		 * @param String
		 *            orientation - the orientation of the paper :: Portrait ||
		 *            Landscape
		 */
		public FopConstructor(String paper, String orientation, String svg) {
			String height = "", width = "", papert = "";
			row = new StringBuffer();
			if (paper.toLowerCase().equals("a4")) {
				papert = "A4";
				if (orientation.toLowerCase().equals("portrait")) {
					height = "297mm";
					width = "210mm";
				} else {// landscape
					height = "210mm";
					width = "297mm";
				}
			} else {// letter
				papert = "Letter";
				if (orientation.toLowerCase().equals("portrait")) {
					height = "279mm";
					width = "216mm";
				} else {// landscape
					height = "216mm";
					width = "279mm";
				}
			}
			row.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			row
					.append("<fo:root xmlns:fo=\"http://www.w3.org/1999/XSL/Format\">");
			row.append("<fo:layout-master-set>");
			row
					.append("<fo:simple-page-master master-name=\""
							+ papert
							+ "\" page-height=\""
							+ height
							+ "\" page-width=\""
							+ width
							+ "\" margin-top=\"10mm\" margin-bottom=\"10mm\" margin-left=\"10mm\" margin-right=\"10mm\">");
			row.append("<fo:region-body margin-top=\"0mm\"/>");
			row.append("<fo:region-after extent=\"20mm\"/>");
			row.append("</fo:simple-page-master>");
			row.append("</fo:layout-master-set>");
			// row += "<fo:page-sequence master-name=\""+papert+"\" >";
			row.append("<fo:page-sequence master-reference=\"" + papert
					+ "\" >");
			row.append("<fo:static-content flow-name=\"xsl-region-after\">");
			row
					.append("<fo:block text-align=\"start\" font-size=\"10pt\" font-family=\"Helvetica\" line-height=\"12pt\" border-top-width=\"thin\">");
			row
					.append("<fo:inline font-weight=\"bold\">Project name:</fo:inline> "
							+ _map.getProjectName());
			row.append("</fo:block>");
			row
					.append("<fo:block text-align=\"start\" font-size=\"10pt\" font-family=\"Helvetica\" line-height=\"12pt\" >");
			row
					.append("<fo:inline font-weight=\"bold\">Project code:</fo:inline> "
							+ _map.getProjectCode());
			row.append("</fo:block>");
			row
					.append("<fo:block text-align=\"start\" font-size=\"10pt\" font-family=\"Helvetica\" line-height=\"12pt\" >");
			row
					.append("<fo:inline font-weight=\"bold\">Identifier:</fo:inline> "
							+ _fileName);
			row.append("</fo:block>");
			row
					.append("<fo:block text-align=\"end\" font-size=\"10pt\" font-family=\"Helvetica\" line-height=\"12pt\" >");
			row.append("p. <fo:page-number/>");
			row.append("</fo:block>");
			row.append("</fo:static-content>");
			row.append("<fo:flow flow-name=\"xsl-region-body\">");
			row.append("<fo:block>");
			row.append("<fo:instream-foreign-object>");
			// start of SVG data addition
			row.append(svg);
			// end of SVG data addition
			row.append("</fo:instream-foreign-object>");
			row.append("</fo:block>");
			row.append("</fo:flow>");
			row.append("</fo:page-sequence>");
			row.append("</fo:root>");
		}

		/**
		 * Builds the XSL Formatting Objects as a string.
		 * 
		 * @return String - the XSL FO document.
		 */
		public String getFopString() {
			return row.toString();
		}
	}
}
