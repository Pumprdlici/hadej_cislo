package fi.javasom.application;
//
// This is JSomMath class that contains mathematical functions for cooperative
// and adaptive processes.
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
 * This is JSomMath class that contains mathematical functions for cooperative
 * and adaptive processes.
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
public class JSomMath {
    
	private double[] cacheVector; //cache vector for temporary storage.
	private int sizeVector; //size of the cache vector.
	private double distCache; //distance cache.
	private double gaussianCache; //double cache for gaussian neighbourhood function operations.
	private int distCacheSize; //cache for the length of the two vectors.

	/**
	 * Constructor.
	 *
	 * @param int vectorSize - Size of a weight/input vector.
	*/
	public JSomMath(int vectorSize) {
		cacheVector = new double[vectorSize];
		for(int i = 0; i < vectorSize; i++) {
			cacheVector[i] = 0.0;
		}
		sizeVector = cacheVector.length;
	}

	/**
	 * Calculates the Euclidean distance between two vectors.
	 *
	 * @param double[] x - 1st vector.
	 * @param double[] y - 2nd vector.
	 * @return double - returns the distance between two vectors, x and y
	*/
	public double getDistance(double[] x, double[] y) {
		return Math.sqrt(getSquareDistance(x, y));
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
	public double getSquareDistance(double[] x, double[] y) {
		distCache = 0.0;
		distCacheSize = x.length;
        for (int i = 0; i < distCacheSize; i++) {
			distCache += (x[i] - y[i]) * (x[i] - y[i]);
		}
        return distCache;
	}

	/**
	 * Calculates the exponential learning-rate parameter value.
	 *
	 * @param int n - current step (time).
	 * @param double a - initial value for learning-rate parameter (should be close to 0.1).
	 * @param int A - time constant (usually the number of iterations in the learning process).
	 * @return double - exponential learning-rate parameter value.
	*/
	public double expLRP(int n, double a, int A) {
		return (a * Math.exp(-1.0 * ((double) n) / ((double) A)));
	}

	/**
	 * Calculates the linear learning-rate parameter value.
	 *
	 * @param int n - current step (time).
	 * @param double a - initial value for learning-rate parameter (should be close to 0.1).
	 * @param int A - another constant (usually the number of iterations in the learning process).
	 * @return double - linear learning-rate parameter value.
	*/
	public double linLRP(int n, double a, int A) {
		return (a * (1 - ((double) n) / ((double) A)));
	}

	/**
	 * Calculates the inverse time learning-rate parameter value.
	 *
	 * @param int n - current step (time).
	 * @param double a - initial value for learning-rate parameter (should be close to 0.1).
	 * @param double A - another constant.
	 * @param double B - another constant.
	 * @return double - inverse time learning-rate parameter value.
	*/
	public double invLRP(int n, double a, double A, double B) {
		return (a * (A / (B + n)));
	}


	/**
	 * Calculates the gaussian neighbourhood width value.
	 *
	 * @param double g - initial width value of the neighbourhood.
	 * @param int n - current step (time).
	 * @param int t - time constant (usually the number of iterations in the learning process).
	 * @return double - adapted gaussian neighbourhood function value.
	*/
	public double gaussianWidth(double g, int n, int t) {
		return (g * Math.exp(-1.0 * ((double) n) / ((double) t)));
	}

	/**
	 * Calculates the Gaussian neighbourhood value.
	 *
	 * @param double[] i - winning neuron location in the lattice.
	 * @param double[] j - excited neuron location in the lattice.
	 * @param double width - width value of the neighbourhood.
	 * @return double - Gaussian neighbourhood value.
	*/
	private double gaussianNF(double[] i, double[] j, double width) {
		gaussianCache = getDistance(i, j);
		return (Math.exp(-1.0 * gaussianCache * gaussianCache / (2.0 * width * width)));
	}

	/**
	 * Calculates whether the excited neuron is in the Bubble neighbourhood set.
	 *
	 * @param double[] i - winning neuron location in the lattice.
	 * @param double[] j - excited neuron location in the lattice.
	 * @param double g - width value of the neighbourhood.
	 * @return boolean - true if located in the Bubble neighbourhood set.
	*/
	private boolean bubbleNF(double[] i,double[] j, double g) {
		if(getDistance(i, j) <= g) {
			return true;
		}
		return false;
	}

	/**
	 * Calculates the new adapted values for a weight vector, based on Bubble neighbourhood.
	 *
	 * @param double[] x - input vector.
	 * @param double[] w - weight vector.
	 * @param double[] i - winning neuron location in the lattice.
	 * @param double[] j - excited neuron location in the lattice.
	 * @param double g - adapted width value of the neighbourhood.
	 * @param double lrp - adapted learning-rate parameter value.
	 * @return double[] - Returns the adapted neuron values.
	*/
	public double[] bubbleAdaptation(double[] x, double[] w, double[] i, 
            double[] j, double g, double lrp) {
		if(bubbleNF(i, j, g)) {
			for(int k = 0; k < sizeVector; k++) {
				cacheVector[k] = w[k] + lrp * (x[k] - w[k]);
			}
		} else {
			return w;
		}
		return cacheVector;
	}

	/**
	 * Calculates the new adapted values for a weight vector, based on Gaussian neighbourhood.
	 *
	 * @param double[] x - input vector.
	 * @param double[] w - weight vector.
	 * @param double[] i - winning neuron location in the lattice.
	 * @param double[] j - excited neuron location in the lattice.
	 * @param double width - adapted width value of the neighbourhood.
	 * @param double lrp - adapted learning-rate parameter value.
	 * @return double[] - Returns the adapted neuron values.
	*/
	public double[] gaussianAdaptation(double[] x, double[] w, double[] i, 
            double[] j, double width, double lrp) {
		gaussianCache = gaussianNF(i, j, width);
		for(int k = 0; k < sizeVector; k++) {
			cacheVector[k] = w[k] + lrp * gaussianCache * (x[k] - w[k]);
		}
		return cacheVector;
	}
}
