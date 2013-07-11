package fi.javasom.application;
//
// This is JSomCreateDomTree class that creates a DOM tree of the map.
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

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This is JSomCreateDomTree class that creates a DOM tree of the map.
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
 * @version 1.1
 * @since 1.0
 * @author Tomi Suuronen
*/
public class JSomCreateDomTree {
    
	//private WeightVectors wVector;
	//private JsomMap mapData;
	private int wSize; //the number of weight vectors
	private int aSize; //the number of authors
	private int x; //x cache
	private int y; //y cache
	private Document d;
	private Element mapnode; //cache
	private Element author; //cache
	private double[] locationCache;
	private String labelCache;
	private String projectName;
	private String projectCode;
	private String fileCode;
	private String fileDate;
	//private double distance; //distance between nodes

	/**
	 * Constructor.
	 *
	 * @param WeightVectors wVector - weight vectors.
	 * @param JsomMap mapData - metadata information about the map.
	 * @param double distance - the distance between two nodes.
	*/
	public JSomCreateDomTree(WeightVectors wVector, JsomMap mapData, double distance) {
		//this.wVector = wVector;
		//this.mapData = mapData;
		//this.distance = distance;
		wSize = wVector.getCount();
		aSize = mapData.getAuthorCount();
		projectName = mapData.getProjectName();
		projectCode = mapData.getProjectCode();
		fileCode = mapData.getFileCode();
		fileDate = mapData.getFileDate();

		// resolves the current date and format
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy'-'MM'-'dd");

		// starts creating the DOM tree
		try {
			DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dfactory.newDocumentBuilder();
			d = dBuilder.newDocument();
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		Element jsommap = d.createElement("jsommap");
		Element meta = d.createElement("meta");

		meta.setAttribute("creationdate",formatter.format(currentTime));
		Element map = d.createElement("map");
		if (wVector.getLatticeType().equals("rect")) {
			map.setAttribute("width","" + (wVector.getXDimension() * (int)distance + 40));
			map.setAttribute("height","" + (wVector.getYDimension() * (int)distance + 40));
		} else { //hexa
			map.setAttribute("width","" + (wVector.getXDimension() * (int)distance + 40 + (int)(distance / 2.0)));
			map.setAttribute("height","" + (wVector.getYDimension() * (int)(distance * 0.87) + 40));
		}

		// constructs the metadata part
		if (!fileCode.equals("") || !fileDate.equals("")) {
			Element datafile = d.createElement("datafile");
			if (!fileCode.equals("")) {
				datafile.setAttribute("code", fileCode);
			}
			if (!fileDate.equals("")) {
				datafile.setAttribute("date", fileDate);
			}
			meta.appendChild(datafile);
		}
		if (!projectName.equals("")) {
			Element project = d.createElement("project");
			project.setAttribute("name", projectName);
			if (!projectCode.equals("")) {
				project.setAttribute("code", projectCode);
			}
			meta.appendChild(project);
		}
		if (aSize > 0) { //checks if there is any authors
			Element author = d.createElement("author");
			for (int i = 0; i < aSize; i++) {
				author = d.createElement("author");
				author.setAttribute("name", mapData.getAuthorNameAt(i));
				author.setAttribute("organization", mapData.getAuthorOrganizationAt(i));
				meta.appendChild(author.cloneNode(false));
			}
		}

		/* constructs the map part */
		for (int i = 0; i < wSize; i++) {
			labelCache = wVector.getNodeLabelAt(i);
			locationCache = wVector.getNodeLocationAt(i);
			mapnode = d.createElement("mapnode");
			x = (int)(locationCache[0] * distance) + (int) distance;
			y = (int)(locationCache[1] * distance) + (int) distance;
			mapnode.setAttribute("x", "" + x);
			mapnode.setAttribute("y", "" + y);
			if (!labelCache.equals("")) {
				mapnode.setAttribute("label", labelCache);
			}
			map.appendChild(mapnode.cloneNode(false));
		}
		jsommap.appendChild(meta);
		jsommap.appendChild(map);
		d.appendChild(jsommap);
	}

	/**
	 *	Returns the created DOM document tree.
	 *
	 * @return Document - document DOM tree.
	*/
	public Document getDomTreeDocument() {
		return d;
	}
}
