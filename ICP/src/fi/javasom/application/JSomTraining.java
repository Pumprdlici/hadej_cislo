package fi.javasom.application;
//
// This is JSomTraining class that does the actual ordering of a dataset into a map.
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

import java.util.Random;

/**
 * This is JSomTraining class that does the actual ordering of a dataset into a map.
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
public class JSomTraining extends JSomBatchProcess {
    
	private double length; //caching
	private double lcache; //caching
	private int index; //caching
	private JSomMath math;
	private WeightVectors wVector;
	private InputVectors iVector;
	private String neigh; //the neighborhood function type used :: step(bubble) | gaussian
	private int steps; //running length (number of steps) in training
	private double lrate; //initial learning rate parameter value
	private String lrateType; //learning rate parameter type :: exponential | linear | inverse
	private double width; //initial "width" of training area
	private int xDim; //number of units in the x-direction
	private int yDim; //number of units in the y-direction
	private Random generator;
	private int wVectorSize; //the number of weight vectors
	private int iVectorSize; //the number of input vectors

	/**
	 * Constructor.
	 *
	 * @param WeightVectors wVector - weight vectors.
	 * @param InputVectors iVector - input vectors.
	*/
	public JSomTraining(WeightVectors wVector, InputVectors iVector) {
		this.wVector = wVector;
		this.iVector = iVector;
		math = new JSomMath(wVector.getDimensionalityOfNodes());
		xDim = wVector.getXDimension();
		yDim = wVector.getYDimension();
		generator = new Random();
	}

	/**
	 * Sets the ordering instructions for the ordering process.
	 *
	 * @param int steps - number of steps in this ordering phase.
	 * @param double lrate - initial value for learning rate (usually near 0.1).
	 * @param int radius - initial radius of neighbors.
	 * @param String lrateType - states which learning-rate parameter function is used :: exponential | linear | inverse
	 * @param String neigh - the neighborhood function type used :: step(bubble) | gaussian
	*/
	public void setTrainingInstructions(int steps, double lrate, int radius,
            String lrateType, String neigh) {
		this.steps = steps;
		this.lrate = lrate;
		this.lrateType = lrateType;
		this.neigh = neigh;
		width = radius;
	}

	/**
	 * Does the training phase.
	 *
	 * @return WeightVectors - Returns the trained weight vectors.
	*/
	public WeightVectors doTraining() {
        fireBatchStart("Training phase");
		iVectorSize = iVector.getCount();
		wVectorSize = wVector.getCount();
		if(lrateType.equals("exponential") && neigh.equals("step")) {
			doBubbleExpAdaptation();
		}
		else if(lrateType.equals("linear") && neigh.equals("step")) {
			doBubbleLinAdaptation();
		}
		else if(lrateType.equals("inverse") && neigh.equals("step")) {
			doBubbleInvAdaptation();
		}
		else if(lrateType.equals("exponential") && neigh.equals("gaussian")) {
			doGaussianExpAdaptation();
		}
		else if(lrateType.equals("linear") && neigh.equals("gaussian")) {
			doGaussianLinAdaptation();
		} else {
			//inverse and gaussian
			doGaussianInvAdaptation();
		}
        fireBatchEnd("Training phase");
		return wVector;
	}

	/*
	 * Does the Bubble Exponential Adaptation to the Weight Vectors.
	*/
	private void doBubbleExpAdaptation() {
		double[] input;
		double[] wLocation; //location of a winner node
		double s = (double)steps;
		double wCache; // width cache
		double exp;
		for(int n = 0; n < steps; n++) {
			wCache = Math.ceil(width * (1 - (n / s))); //adapts the width function as it is a function of time.
			exp = math.expLRP(n,lrate,steps);
			input = iVector.getNodeValuesAt(generator.nextInt(iVectorSize));
			index = resolveIndexOfWinningNeuron(input);
			wLocation = wVector.getNodeLocationAt(index);
			for(int h = 0; h < wVectorSize; h++) {
				wVector.setNodeValuesAt(h, math.bubbleAdaptation(input, 
                        wVector.getNodeValuesAt(h), wLocation,
                        wVector.getNodeLocationAt(h), wCache, exp));
			}
            fireBatchProgress(n, steps);
		}
	}

	/*
	 * Does the Bubble Linear Adaptation to the Weight Vectors.
	*/
	private void doBubbleLinAdaptation() {
		double[] input;
		double[] wLocation; //location of a winner node
		double s = (double)steps;
		double wCache; // width cache
		double lin;
		for(int n = 0; n < steps; n++) {
			wCache = Math.ceil(width * (1 - (n / s))); //adapts the width function as it is a function of time.
			lin = math.linLRP(n, lrate, steps);
			input = iVector.getNodeValuesAt(generator.nextInt(iVectorSize));
			index = resolveIndexOfWinningNeuron(input);
			wLocation = wVector.getNodeLocationAt(index);
			for(int h = 0; h < wVectorSize; h++) {
				wVector.setNodeValuesAt(h, math.bubbleAdaptation(input, 
                        wVector.getNodeValuesAt(h), wLocation, 
                        wVector.getNodeLocationAt(h), wCache, lin));
			}
            fireBatchProgress(n, steps);
		}
	}

	/*
	 * Does the Bubble Inverse-time Adaptation to the Weight Vectors.
	*/
	private void doBubbleInvAdaptation() {
		double[] input;
		double[] wLocation; //location of a winner node
		double A; //constants A and B which are considered equal
		double s = (double)steps;
		double wCache; // width cache
		double inv;
		A = steps / 100.0;
		for(int n = 0; n < steps; n++) {
			wCache = Math.ceil(width * (1 - (n / s))); //adapts the width function as it is a function of time.
			inv = math.invLRP(n, lrate, A, A);
			input = iVector.getNodeValuesAt(generator.nextInt(iVectorSize));
			index = resolveIndexOfWinningNeuron(input);
			wLocation = wVector.getNodeLocationAt(index);
			for(int h = 0; h < wVectorSize; h++) {
				wVector.setNodeValuesAt(h, math.bubbleAdaptation(input, 
                        wVector.getNodeValuesAt(h), wLocation, 
                        wVector.getNodeLocationAt(h), wCache, inv));
			}
            fireBatchProgress(n, steps);
		}
	}

	/*
	 * Does the Gaussian Exponential Adaptation to the Weight Vectors.
	*/
	private void doGaussianExpAdaptation() {
		double[] input;
		double[] wLocation; //location of a winner node
		double wCache; // width cache
		double exp;
		for(int n = 0; n < steps; n++) {
			wCache = math.gaussianWidth(width, n, steps);
			exp = math.expLRP(n, lrate, steps);
			input = iVector.getNodeValuesAt(generator.nextInt(iVectorSize));
			index = resolveIndexOfWinningNeuron(input);
			wLocation = wVector.getNodeLocationAt(index);
			for(int h = 0; h < wVectorSize; h++) {
				wVector.setNodeValuesAt(h, math.gaussianAdaptation(input, 
                        wVector.getNodeValuesAt(h), wLocation, 
                        wVector.getNodeLocationAt(h), wCache, exp));
			}
            fireBatchProgress(n, steps);
		}
	}

	/*
	 * Does the Gaussian Linear Adaptation to the Weight Vectors.
	*/
	private void doGaussianLinAdaptation() {
		double[] input;
		double[] wLocation; //location of a winner node
		double wCache; // width cache
		double lin;
		for(int n = 0; n < steps; n++) {
			wCache = math.gaussianWidth(width, n, steps);
			lin = math.linLRP(n, lrate, steps);
			input = iVector.getNodeValuesAt(generator.nextInt(iVectorSize));
			index = resolveIndexOfWinningNeuron(input);
			wLocation = wVector.getNodeLocationAt(index);
			for(int h = 0; h < wVectorSize; h++) {
				wVector.setNodeValuesAt(h, math.gaussianAdaptation(input, 
                        wVector.getNodeValuesAt(h), wLocation, 
                        wVector.getNodeLocationAt(h), wCache, lin));
			}
            fireBatchProgress(n, steps);
		}
	}

	/*
	 * Does the Gaussian Inverse-time Adaptation to the Weight Vectors.
	*/
	private void doGaussianInvAdaptation() {
		double[] input;
		double[] wLocation; //location of a winner node
		double A; //constants A and B which are considered equal
		double wCache; // width cache
		double inv;
		A = steps / 100.0;
		for(int n = 0; n < steps; n++) {
			wCache = math.gaussianWidth(width, n, steps);
			inv = math.invLRP(n, lrate, A, A);
			input = iVector.getNodeValuesAt(generator.nextInt(iVectorSize));
			index = resolveIndexOfWinningNeuron(input);
			wLocation = wVector.getNodeLocationAt(index);
			for(int h = 0; h < wVectorSize; h++) {
				wVector.setNodeValuesAt(h, math.gaussianAdaptation(input, 
                        wVector.getNodeValuesAt(h), wLocation, 
                        wVector.getNodeLocationAt(h), wCache, inv));
			}
            fireBatchProgress(n, steps);
		}
	}
    
    /*
	 * Finds the winning neuron for this input vector.
	 *
	 * @param double[] values - values of an input vector.
	 * @return int - index of the winning neuron.
	*/
	private int resolveIndexOfWinningNeuron(double[] values) {
		length = math.getSquareDistance(values, wVector.getNodeValuesAt(0));
		index = 0;
		for (int i = 1; i < wVectorSize; i++) {
			lcache = math.getSquareDistance(values, wVector.getNodeValuesAt(i));
			if (lcache < length) {
				index = i;
				length = lcache;
			}
		}
		return index;
	}
}
