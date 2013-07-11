package fi.javasom.application;
//
// This is the main container for the whole final map.
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

/**
 * This is the main container for the whole final map.
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
public class JsomMap {
    
	private String fileCode; // identification code for this set of input data (from meta ELEMENT code ATTRIBUTE)
	private String fileDate; // the date when the input data .xml-file was created for this map
	private String projectName; //name of this project
	private String projectCode; // code of this project
	private ArrayList authorName; // name od the author
	private ArrayList authorOrg; // organization of the author

	/**
	 * Main constructor.
	*/
	public JsomMap() {
		fileCode = "";
		fileDate = "";
		projectName = "";
		projectCode = "";
		authorName = new ArrayList();
		authorOrg = new ArrayList();
	}

	/**
	 * Sets the identification code for this set of input data .xml-file.
	 *
	 * @param code Code from .xml-file.
	*/
	public void setCode(String code) {
		fileCode = code;
	}

	/**
	 * Sets the date when the input data .xml-file was created.
	 *
	 * @param date Date when the .xml-file was created.
	*/
	public void setDate(String date) {
		fileDate = date;
	}

	/**
	 * Sets the project information.
	 *
	 * @param name Name of the project.
	 * @param code Identification code of this project.
	*/
	public void setProject(String name,String code) {
		projectName = name;
		projectCode = code;
	}

	/**
	 * Adds the .xml-file author who created it.
	 *
	 * @param name Name of the author.
	 * @param organization Organization of this author.
	*/
	public void addAuthor(String name, String organization) {
		authorName.add(name);
		authorOrg.add(organization);
	}

	/**
	 * Returns the number of authors in this map.
	 *
	 * @return int - Number of authors.
	*/
	public int getAuthorCount() {
		return authorName.size();
	}

	/**
	 * Returns the name of the author at specific index.
	 *
	 * @param index Index number .
	 * @return String - Name of the author.
	*/
	public String getAuthorNameAt(int index) {
		return authorName.get(index).toString();
	}

	/**
	 * Returns the file code.
	 *
	 * @return String - Code of the .xml-file.
	*/
	public String getFileCode()	{
		return fileCode;
	}

	/**
	 * Returns the date when the .xml-file was created.
	 *
	 * @return String - Code of the .xml-file.
	*/
	public String getFileDate() {
		return fileDate;
	}

	/**
	 * Returns the name of the project in which this .xml-file is part of.
	 *
	 * @return String - Name of the project.
	*/
	public String getProjectName() {
		return projectName;
	}

	/**
	 * Returns the code of the project in which this .xml-file is part of.
	 *
	 * @return String - Code of the project.
	*/
	public String getProjectCode() {
		return projectCode;
	}

	/**
	 * Returns the organization of the author at specific index.
	 *
	 * @param index Index number .
	 * @return String - Organization of the author.
	*/
	public String getAuthorOrganizationAt(int index) {
		return authorOrg.get(index).toString();
	}
}
