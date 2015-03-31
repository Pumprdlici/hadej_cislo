package icp.application.classification;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Implementation of the code necessary for classification via K Nearest Neighbors algorithm.
 * As of now the implementation and its methods are set directly for the data given by structures
 * in Guess the Number application. It can be altered to more general approach in the future.
 * @author Vlada47
 *
 */
public class KNearestNeighborsLocal implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Attribute containing number of neighbors 
	 * that are used for determination of the class for classified element.
	 */
	private int k;

	/**
	 * ArrayList containing all instances of the Neighbor class
	 * that are used for determination of the class for classified element.
	 * They represents training data for this classifier.
	 */
	private ArrayList<Neighbor> neighbors;
	
	/**
	 * Initialization of the instance. It sets given number of neighbors
	 *  and initialize ArrayList for training data.
	 * @param k given number of neighbors
	 */
	public KNearestNeighborsLocal(int k) {
		this.k = k;
		this.neighbors = new ArrayList<Neighbor>();
	}
	
	/**
	 * Method for adding another neighbor to the ArrayList.
	 * Given parameters are set to new instance of the Neighbor class.
	 * @param vector given feature vector
	 * @param classValue given target (class of the element)
	 */
	public void addNeighbor(double[] vector, double classValue) {
		Neighbor n = new Neighbor(vector, classValue);
		this.neighbors.add(n);
	}
	
	/**
	 * Method for getting the score of given feature vector.
	 * First it calls method for calculating the distances between given element
	 * and all neighbors from saved training data.
	 * Then it calls method for sorting those data in ascending order.
	 * Lastly the method looks for the class of first k specific elements,
	 * where k is saved number of chose neighbors. Method expects two possible classes
	 * class with double value greater than 0.0 (in the case of Guess the Number application it's 1.0)
	 * and class with less or equal to 0.0. If the class is greater, the matches variable is incremented.
	 * Score is calculated as division of matches variable and number of relevant neighbors (k). 
	 * @param featureVector element that will be classified
	 * @return score of given element
	 */
	public double getScore(double[] featureVector) {
		createDistances(featureVector);
		sortNeighbors();
		int maxNeighbors = k;
		
		/*in case of test data having fewer elements than k*/
		if(neighbors.size() < k) {
			maxNeighbors = neighbors.size();
		}
		
		int matches = 0;
		for(int i = 0; i < maxNeighbors; i++) {
			double classValue = neighbors.get(i).getClassValue();
			if(classValue > 0.0) matches++;
		}
		
		double score = (double)matches / (double)maxNeighbors;
		return score;
	}
	
	/**
	 * Method for sorting the ArrayList with training data based on calculated distances.
	 * It uses custom Comparator class to sort elements in ascending order.
	 */
	void sortNeighbors() {
		NeighborsComparator comparator = new NeighborsComparator();
		Collections.sort(neighbors, comparator);
	}
	
	/**
	 * Method for setting distance values to training data for given element.
	 * It calls another method for calculating the particular distance between two vectors.
	 * If the method is called without the training data being already set, it throws NullPointerException. 
	 * @param featureVector element for which are distances from all neighbors in training data calculated
	 */
	void createDistances(double[] featureVector) {
		if(neighbors != null) {
			for(Neighbor n : neighbors) {
				double distance = calculateDistanceEuclidian(n.getVector(), featureVector);
				n.setDistance(distance);
			}
		}
		else {
			throw new NullPointerException("No neighbors!");
		}
	}
	
	/**
	 * Method for calculating  the euclidian distance between two given vectors.
	 * @param vector1
	 * @param vector2
	 * @return distance between two given vector
	 */
	double calculateDistanceEuclidian(double[] vector1, double[] vector2) {
		double distance = 0.0;
		
		if(vector1.length == vector2.length) {
			double sum = 0.0;
			for(int i = 0; i < vector1.length; i++) {
				sum += Math.pow((vector1[i] - vector2[i]), 2.0);
			}
			
			distance = Math.sqrt(sum);
		}
		else {
			throw new IndexOutOfBoundsException("Can't calculate distance between vectors!\n"
					+ "Vector 1 length: "+vector1.length+", Vector 2 length: "+vector2.length+".");
		}
		
		return distance;
	}
	
	/**
	 * Private class implementing the Comparator interface for sorting collections with
	 * the Neighbor instances in ascending order.
	 * @author Vlada47
	 *
	 */
	private class NeighborsComparator implements Comparator<Neighbor> {

		@Override
		public int compare(Neighbor o1, Neighbor o2) {
			int result;
			
			if(o1.getDistance() > o2.getDistance()) result = 1;
			else if(o1.getDistance() < o2.getDistance()) result = -1;
			else result = 0;
			
			return result;
		}
	}
	
	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

	public ArrayList<Neighbor> getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(ArrayList<Neighbor> neighbors) {
		this.neighbors = neighbors;
	}
}
