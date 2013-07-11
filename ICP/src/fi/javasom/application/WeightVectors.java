package fi.javasom.application;
//
//  This is the main container for the synaptic weight vectors.
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
import java.util.Random;

/**
 * This is the main container for the synaptic weight vectors.
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
public class WeightVectors extends ArrayList {

	private double[] values;
	private double[] location;
	private String lattice; //topology of the map
	private Random generator;
	private int xDim;
	private int yDim;
	private int dimension; //dimensionality of a node
	private final double YVALUE = 0.866;

	/**
	 * Main constructor. Used to contain the synaptic weight vectors during the learning phase.
	 * @param xDim X-dimension of the map constructed.
	 * @param yDim Y-dimension of the map constructed.
	 * @param dimension dimensionality of a node.
	 * @param type Lattice type of the map constructed (hexa or rect)
	*/
	public WeightVectors(int xDim, int yDim, int dimension, String type) {
        super(xDim * yDim);
        int size = xDim * yDim;
        this.xDim = xDim;
		this.yDim = yDim;
		this.dimension = dimension;
		values = new double[dimension];
		location = new double[2];
		generator = new Random();
		lattice = type;
		int yCounter = 0;
		int xCounter = 0;
		double xValue = 0;
		double yValue = 0;
		boolean evenRow = false; //for hexagonal lattice, cheking if the current row number is even or odd
		if (lattice.equals("rect")) { //rectangular lattice
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < dimension; j++) {
					values[j] = generator.nextDouble();
				}
				if (xCounter < xDim) {
					location[0] = xCounter;
					location[1] = yCounter;
					xCounter++;
				} else {
					xCounter = 0;
					yCounter++;
					location[0] = xCounter;
					location[1] = yCounter;
					xCounter++;
				}
				add(new SomNode(values, location));
			}
		} else { //hexagonal lattice
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < dimension; j++) {
					values[j] = generator.nextDouble();
				}
				if(xCounter < xDim) {
					location[0] = xValue;
					location[1] = yValue;
					xValue += 1.0;
					xCounter++;
				} else {
					xCounter = 0;
					yValue += YVALUE;
					if (evenRow) {
						xValue = 0.0;
						evenRow = false;
					} else {
						xValue = 0.5;
						evenRow = true;
					}
					location[0] = xValue;
					location[1] = yValue;
					xValue += 1.0;
					xCounter++;
				}
				add(new SomNode(values, location));
			}
		}
	}

	/**
	 * Returns the x-dimension of this map.
	 *
	 * @return int - X-dimensionality of the map.
	*/
	public int getXDimension() {
		return xDim;
	}

	/**
	 * Returns the y-dimension of this map.
	 *
	 * @return int - Yy-dimensionality of the map.
	*/
	public int getYDimension() {
		return yDim;
	}

	/**
	 * Returns the node values at a specific node.
	 *
	 * @param index Index of the SomNode
	 * @return double[] - Returns the Node values from the specified index.
	*/
	public double[] getNodeValuesAt(int index) {
		SomNode cache = (SomNode) get(index);
		return (cache.getValues());
	}

	/**
	 * Sets the node values at a specific node.
	 *
	 * @param index Index of the SomNode
	 * @param values Values of the SomNode
	*/
	public void setNodeValuesAt(int index,double[] values) {
		SomNode cache = (SomNode) get(index);
		cache.setValues(values);
		set(index, cache);
	}

	/**
	 * Returns the node values at a specific node.
	 *
	 * @param index Index of the SomNode
	 * @return double[] - Returns the Node location from the specified index.
	*/
	public double[] getNodeLocationAt(int index) {
		SomNode cache = (SomNode) get(index);
		return (cache.getLocation());
	}

	/**
	 * Returns the dimensionality of a node (it is the same for all of them).
	 *
	 * @return int - Dimensionality of nodes.
	*/
	public int getDimensionalityOfNodes() {
		return dimension;
	}

	/**
	 * Returns the number of weight vectors.
	 *
	 * @return int - Returns the number of weight vectors.
	*/
	public int getCount() {
		return size();
	}

	/**
	 * Sets the label of a specific weight vector at the specified index.
	 *
	 * @param index The index of SomNode.
	 * @param label The new label for this SomNode.
	 * @return String - Returns the Node label from the specified index.
	*/
	public void setNodeLabelAt(int index, String label) {
		//SomNode cache = (SomNode) weight.get(index);
        SomNode cache = (SomNode) get(index);
		if(cache.isLabeled()) {
			cache.addLabel(label);
		} else {
			cache.setLabel(label);
		}
		set(index, cache);
	}

	/**
	 * Returns a Node label of a specific weight vector from the specified index.
	 *
	 * @param index The index of SomNode.
	 * @return String - Returns the Node label from the specified index.
	*/
	public String getNodeLabelAt(int index) {
		SomNode cache = (SomNode) get(index);
		return (cache.getLabel());
	}


	/**
	 * Returns the lattice type used in initializing node locations.
	 *
	 * @return String - Lattice :: hexa | rect.
	*/
	public String getLatticeType() {
		return lattice;
	}
}
