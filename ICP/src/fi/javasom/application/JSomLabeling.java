package fi.javasom.application;
//
// This is JSomLabeling class that labels the weight vectors.
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
 * This is JSomLabeling class that labels the weight vectors.
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
public class JSomLabeling extends JSomBatchProcess {
    
	private WeightVectors wVector;
	private InputVectors iVector;
	private double distCache;
	private double length;
	private double lcache;
	private int	distCacheSize;
	private int iSize; //the number of input vectors
	private int wSize; //the number of weight vectors
	private int index;

	/**
	 * Constructor.
	 *
	 * @param WeightVectors wVector - weight vectors.
	 * @param InputVectors iVector - input vectors.
	*/
	public JSomLabeling(WeightVectors wVector, InputVectors iVector) {
		this.wVector = wVector;
		this.iVector = iVector;
		distCacheSize = wVector.getDimensionalityOfNodes();
		iSize = iVector.getCount();
		wSize = wVector.getCount();
	}

	/**
	 * Does the labeling phase.
	 *
	 * @return WeightVectors - Returns the labeled weight vectors.
	*/
	public WeightVectors doLabeling() {
        fireBatchStart("Labelling");
		for (int i = 0; i < iSize; i++) {
			wVector.setNodeLabelAt(resolveIndexOfWinningNeuron(iVector.getNodeValuesAt(i)), iVector.getNodeLabelAt(i));
            fireBatchProgress(i, iSize - 1);
		}
        fireBatchEnd("Labelling");
		return wVector;
	}

	/*
	 * Finds the winning neuron for this input vector. Determines the winning
     * neuron by calculating the square of Eclidean distance of two vectors as
     * it will give the same result as the Euclidean distance.
	 *
	 * @param double[] values - values of an input vector.
	 * @return int - index of the winning neuron.
	*/
	private int resolveIndexOfWinningNeuron(double[] values) {
		length = getSquareDistance(values, wVector.getNodeValuesAt(0));
		index = 0;
		for (int i = 1; i < wSize; i++) {
			lcache = getSquareDistance(values, wVector.getNodeValuesAt(i));
			if (lcache < length) {
				index = i;
				length = lcache;
			}
		}
		return index;
	}

	/**
	 * Calculates the square of Euclidean distance between two vectors. It is
     * faster to calculate the square of Euclidean distance than the distance
     * itself.
	 *
	 * @param double[] x - 1st vector.
	 * @param double[] y - 2nd vector.
	 * @return double - returns the square of distance between x and y vectors.
	*/
	private double getSquareDistance(double[] x, double[] y) {
		distCache = 0.0;
		for (int i = 0; i < distCacheSize; i++) {
			distCache += (x[i] - y[i]) * (x[i] - y[i]);
		}
        return distCache;
	}
}
