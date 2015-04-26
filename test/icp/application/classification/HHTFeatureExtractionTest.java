package icp.application.classification;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class HHTFeatureExtractionTest {
	
	private HHTFeatureExtraction hht; 

	@Before
	public void setUp() throws Exception {
		hht = new HHTFeatureExtraction();
		
		hht.setEpochSize(512);
		hht.setDownSmplFactor(4);
		hht.setAmplitudeThreshold(3.0);
		hht.setMinFreq(0.2);
		hht.setMaxFreq(3.0);
	}

	@Test
	public void testGetFeatureDimension() {
		assertEquals(512*3 / 4, hht.getFeatureDimension());
	}

	@Test
	public void testSetMinFreq1() {
		hht.setMinFreq(3.0);
		assertEquals(3.0, hht.getMinFreq(), 0.00001);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSetMinFreq2() {
		hht.setMinFreq(-1.0);
	}

	@Test
	public void testSetMaxFreq1() {
		hht.setMaxFreq(3.0);
		assertEquals(3.0, hht.getMaxFreq(), 0.00001);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSetMaxFreq2() {
		hht.setMaxFreq(-1.0);
	}

	@Test
	public void testSetSampleWindowSize1() {
		hht.setSampleWindowSize(150);
		assertEquals(150, hht.getSampleWindowSize());
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSetSampleWindowSize2() {
		hht.setSampleWindowSize(0);
	}

	@Test
	public void testSetSampleWindowShift1() {
		hht.setSampleWindowShift(5);
		assertEquals(5, hht.getSampleWindowShift());
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSetSampleWindowShift2() {
		hht.setSampleWindowShift(0);
	}
	
	@Test
	public void testSelectIndexOfBestHT() {
		double[] htAmplitudesScore = {2.0, -5.2, 1.2, 3.1, -6.4};
		double[] htFrequenciesScore = {1.2, 3.4, -2.4, 2.8, -5.1};
		int expectedIndex = 3;
		
		int index = hht.selectIndexOfBestHT(htAmplitudesScore, htFrequenciesScore);
		
		assertEquals(expectedIndex, index);
	}
	
	@Test
	public void testGetWindowAmplitudeScore() {
		ArrayList<Double> amplitudes1 = new ArrayList<Double>();
		amplitudes1.add(2.5); 
		amplitudes1.add(3.1);
		amplitudes1.add(3.2);
		amplitudes1.add(2.9);
		amplitudes1.add(3.4);
		
		ArrayList<Double> amplitudes2 = new ArrayList<Double>();
		amplitudes2.add(1.6); 
		amplitudes2.add(0.9);
		amplitudes2.add(1.1);
		amplitudes2.add(1.2);
		amplitudes2.add(0.8);
		
		double score1 = hht.getWindowAmplitudeScore(amplitudes1);
		double score2 = hht.getWindowAmplitudeScore(amplitudes2);
		
		assertTrue(score1 > score2);
	}
	
	@Test
	public void testGetWindowFrequencyScore1() {		
		ArrayList<Double> frequencies1 = new ArrayList<Double>();
		frequencies1.add(3.1); 
		frequencies1.add(2.9);
		frequencies1.add(2.8);
		frequencies1.add(3.0);
		frequencies1.add(3.2);
		
		ArrayList<Double> frequencies2 = new ArrayList<Double>();
		frequencies2.add(4.6); 
		frequencies2.add(3.8);
		frequencies2.add(3.9);
		frequencies2.add(3.1);
		frequencies2.add(4.0);
		
		double score1 = hht.getWindowFrequencyScore(frequencies1);
		double score2 = hht.getWindowFrequencyScore(frequencies2);
		
		assertTrue(score1 > score2);
	}
	
	@Test
	public void testGetWindowFrequencyScore2() {		
		ArrayList<Double> frequencies1 = new ArrayList<Double>();
		frequencies1.add(3.1); 
		frequencies1.add(2.9);
		frequencies1.add(2.8);
		frequencies1.add(3.0);
		frequencies1.add(3.2);
		
		ArrayList<Double> frequencies2 = new ArrayList<Double>();
		frequencies2.add(0.1); 
		frequencies2.add(0.2);
		frequencies2.add(0.15);
		frequencies2.add(0.05);
		frequencies2.add(0.3);
		
		double score1 = hht.getWindowFrequencyScore(frequencies1);
		double score2 = hht.getWindowFrequencyScore(frequencies2);
		
		assertTrue(score1 > score2);
	}

}
