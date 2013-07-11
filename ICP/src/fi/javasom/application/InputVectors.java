package fi.javasom.application;
//
// This is the main container for the input vectors.
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
 * This is the main container for the input vectors.
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
public class InputVectors extends ArrayList {
    
	//private ArrayList input; //input vectors

	/**
	 * Main constructor for this map. Used to contain all the input vectors.
	*/
	public InputVectors() {
		super(1000);
	}
    
    /**
	 * Main constructor for this map. Used to contain all the input vectors.
     *
     * @param capacity Number of input vectors.
	*/
	public InputVectors(int capacity) {
		super(capacity);
	}

	/**
	 * Adds a new input vector.
	 *
	 * @param node The SomNode object added.
	*/
	public void addInputVector(SomNode node) {
		//input.add(node);
        add(node);
	}

	/**
	 * Returns a input vector from the specified index.
	 *
	 * @param index The index of SomNode.
	 * @return SomNode - returns the SomNode object at the specified index.
	*/
	public SomNode getSomNodeAt(int index) {
		//return ((SomNode) input.get(index));
        return ((SomNode) get(index));
	}

	/**
	 * Returns a Node values of a specific input vector from the specified index.
	 *
	 * @param index The index of SomNode.
	 * @return double[] - returns the Node values from the specified index.
	*/
	public double[] getNodeValuesAt(int index) {
		//SomNode cache = (SomNode) input.get(index);
        SomNode cache = (SomNode) get(index);
		return (cache.getValues());
	}

	/**
	 * Sets the node values at a specific node.
	 *
	 * @param index Index of the SomNode
	 * @param values Values of the SomNode
	*/
	public void setNodeValuesAt(int index, double[] values) {
		//SomNode cache = (SomNode) input.get(index);
		SomNode cache = (SomNode) get(index);
        cache.setValues(values);
		//input.set(index, cache);
        set(index, cache);
	}

	/**
	 * Returns a Node label of a specific input vector from the specified index.
	 *
	 * @param index The index of SomNode.
	 * @return String - returns the Node label from the specified index.
	*/
	public String getNodeLabelAt(int index) {
		//SomNode cache = (SomNode) input.get(index);
        SomNode cache = (SomNode) get(index);
		return (cache.getLabel());
	}

	/**
	 * Returns the number of input vectors.
	 *
	 * @return int - returns the number of input vectors.
	*/
	public int getCount() {
		//return input.size();
        return size();
	}
}
