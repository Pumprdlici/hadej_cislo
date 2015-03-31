package icp.application.classification;

import java.io.Serializable;

/**
 * Class for creating objects that represents elements in training data
 * of the custom classifier based on K Nearest Neighbors algorithm.
 * @author Vlada47
 *
 */
public class Neighbor implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Feature vector of the element.
	 */
	private double[] vector;
	
	/**
	 * Class value of the element.
	 */
	private double classValue;
	
	/**
	 * Distance of the element from vector that is being classified.
	 * It's set during the classification process. 
	 */
	private double distance;
	
	/**
	 * Constructor for the Neighbor instances. It sets given vector and  its class value.
	 * Distance is initiated to infinity.
	 * @param vector
	 * @param classValue
	 */
	public Neighbor(double[] vector, double classValue) {
		this.vector = vector;
		this.classValue = classValue;
		this.distance = Double.MAX_VALUE;
	}
	
	public double[] getVector() {
		return vector;
	}

	public double getClassValue() {
		return classValue;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}
}
