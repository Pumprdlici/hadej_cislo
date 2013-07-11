package fi.javasom.application;
//
// This is a node object known as weight vector or input vector.
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

/**
 * This is a node object known as weight vector or input vector.
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
public class SomNode {
    
	private String label;
	private double[] values;
	private double[] location;
	
	/**
	 * Main constructor.
	*/
	public SomNode() {
		label = "";
		values = new double[1];
		location = new double[1];
	}

	/**
	 * Main constructor (for input vectors).
	 *
	 * @param String label - Name of this node.
	 * @param double[] values - All the values of this node.
	*/
	public SomNode(String label, double[] values) {
		this.label = label;
		this.values = (double[]) values.clone();
		location = new double[1];
	}

	/**
	 * Main constructor (for weight vectors).
	 *
	 * @param String label - Name of this node.
	 * @param double[] values - All the values of this node.
	 * @param double[] location - The location of this node.
	*/
	public SomNode(double[] values, double[] location) {
		label = "";
		this.values = (double[]) values.clone();
		this.location = (double[]) location.clone();
	}

	/**
	 * Sets values for every dimension in this node.
	 *
	 * @param double[] values - Sets all the values for this node.
	*/
	public void setValues(double[] values) {
		this.values = (double[]) values.clone();
	}

	/**
	 * Returns all the values of this node.
	 *
	 * @return double[] - Returns the numerical presentation of this node.
	*/
	public double[] getValues() {
		return ((double[]) values.clone());
	}

	/**
	 * Set the label name for this node.
	 *
	 * @param String - Label of this node.
	*/
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Set the secondary label(s) for this node.
	 *
	 * @param String - Another label of this node.
	*/
	public void addLabel(String label) {
		this.label += ", " + label;
	}

	/**
	 * Returns the label of this node.
	 *
	 * @return String - Returns the label of this node if any.
	*/
	public String getLabel() {
		return label;
	}

	/**
	 * Returns the location of this node.
	 *
	 * @return double[] - Returns the location of this node if any.
	*/
	public double[] getLocation() {
		return ((double[]) location.clone());
	}

	/**
	 * Returns the information about wether labeling has been done.
	 *
	 * @return boolean - Returns true if this node has been labeled otherwise false.
	*/
	public boolean isLabeled() {
		return label.length() > 0;
	}
}
