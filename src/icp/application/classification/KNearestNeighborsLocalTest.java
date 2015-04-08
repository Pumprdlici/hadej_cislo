package icp.application.classification;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class KNearestNeighborsLocalTest {
	
	private static final int K_CNT = 5;
	
	private KNearestNeighborsLocal knn;
	
	@Before
	public void setup() {
		this.knn = new KNearestNeighborsLocal(K_CNT);
		
		double[] testVector1 = {2.0, 2.0, 2.0};
		double[] testVector2 = {3.0, 3.0, 3.0};
		double[] testVector3 = {4.0, 4.0, 4.0};
		double[] testVector4 = {5.0, 5.0, 5.0};
		double[] testVector5 = {6.0, 6.0, 6.0};
		double[] testVector6 = {7.0, 7.0, 7.0};
		double[] testVector7 = {8.0, 8.0, 8.0};
		double[] testVector8 = {9.0, 9.0, 9.0};
		double testClass1 = 1.0;
		double testClass2 = 0.0;
		double testClass3 = 1.0;
		double testClass4 = 1.0;
		double testClass5 = 1.0;
		double testClass6 = 0.0;
		double testClass7 = 0.0;
		double testClass8 = 0.0;
		
		knn.addNeighbor(testVector8, testClass8);
		knn.addNeighbor(testVector7, testClass7);
		knn.addNeighbor(testVector1, testClass1);
		knn.addNeighbor(testVector2, testClass2);
		knn.addNeighbor(testVector5, testClass5);
		knn.addNeighbor(testVector3, testClass3);
		knn.addNeighbor(testVector6, testClass6);
		knn.addNeighbor(testVector4, testClass4);
	}

	@Test
	public void constructorTest1() {
		assertEquals(K_CNT, knn.getK());
	}
	
	@Test
	public void constructorTest2() {
		assertNotNull(knn.getNeighbors());
	}
	
	@Test
	public void getScoreTest1() {
		double[] vector = {1.0, 1.0, 1.0};
		double expectedScore = 0.8;
		
		double realScore = knn.getScore(vector);
		assertTrue((expectedScore == realScore));
	}
	
	@Test
	public void getScoreTest2() {
		double[] vector = {10.0, 10.0, 10.0};
		double expectedScore = 0.4;
		
		double realScore = knn.getScore(vector);
		assertTrue((expectedScore == realScore));
	}
	
	@Test
	public void getScoreTest3() {
		double[] vector = {1.0, 1.0, 1.0};
		double expectedScore = 0.5;
		
		knn.setK(10);
		double realScore = knn.getScore(vector);
		assertTrue((expectedScore == realScore));
	}
	
	@Test
	public void sortNeighborsTest() {
		double[] vector = {1.0, 1.0, 1.0};
		knn.createDistances(vector);
		knn.sortNeighbors();
		ArrayList<Neighbor> neighbors = knn.getNeighbors();
		double smallestDistance = neighbors.get(0).getDistance();
		double bigestDistance = neighbors.get(neighbors.size()-1).getDistance();
		
		assertTrue(bigestDistance >= smallestDistance);
	}
	
	@Test
	public void calculateDistanceEuclidianTest() {
		double[] vector1 = {1.0, 1.0};
		double[] vector2 = {2.0, 2.0};
		
		double expectedDistance = Math.sqrt(Math.pow((vector1[0]-vector2[0]), 2.0) + Math.pow((vector1[1]-vector2[1]), 2.0));
		double calculatedDistance = knn.calculateDistanceEuclidian(vector1, vector2);
		
		assertTrue(calculatedDistance == expectedDistance);
	}
}
