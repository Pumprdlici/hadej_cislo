package fi.javasom.application;

//
// This is JSomNormalization class that normalizes the input vectors.
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

/**
 * This is JSomNormalization class that normalizes the input vectors.
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
public class JSomNormalization {

	private InputVectors _iVector;

	private int _dimension; // dimensionality of a node

	private int _iSize; // number of input vectors

	private double[] _values; // cache for the node values

	/**
	 * Constructor.
	 * 
	 * @param InputVectors
	 *            iVector - input vectors.
	 * @param int
	 *            dimension - dimensionality of a node.
	 */
	public JSomNormalization(InputVectors iVector, int dimension) {
		_iVector = iVector;
		_dimension = dimension;
		_iSize = iVector.getCount();
	}

	/**
	 * Does the normalization phase.
	 * 
	 * @return InputVectors - Returns the normalized input vectors.
	 */
	public InputVectors doNormalization() {
		double cache = 0.0;
		for (int j = 0; j < _dimension; j++) {
			// resolve largest node value inside a column
			for (int i = 0; i < _iSize; i++) {
				_values = _iVector.getNodeValuesAt(i);
				if (_values[j] > cache) {
					cache = _values[j];
				}
			}
			// normalize if necessary
			if (cache > 1) {
				for (int i = 0; i < _iSize; i++) {
					_values = _iVector.getNodeValuesAt(i);
					_values[j] = _values[j] / cache;

					_iVector.setNodeValuesAt(i, _values);
				}
			}
			cache = 0.0;
		}
		return _iVector;
	}
}
