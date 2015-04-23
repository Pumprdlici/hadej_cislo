package icp.application.classification;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class HHTFeatureExtractionTest {
	
	private HHTFeatureExtraction hht; 

	@Before
	public void setUp() throws Exception {
		hht = new HHTFeatureExtraction();
	}

	@Test
	public void testExtractFeatures() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFeatureDimension() {
		hht.setMinSample(200);
		hht.setMaxSample(500);
		
		int setSubsampling = 6;
		long expectedDimension = 300*3 / setSubsampling;
		
		assertEquals(expectedDimension, hht.getFeatureDimension());
	}

	@Test
	public void testSetMinSample1() {
		hht.setMinSample(200);
		assertEquals(200, hht.getMinSample());
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSetMinSample2() {
		hht.setMinSample(-10);
	}
	
	@Test
	public void testSetMaxSample1() {
		hht.setMaxSample(500);
		assertEquals(500, hht.getMaxSample());
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSetMaxSample2() {
		hht.setMaxSample(-50);
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
		assertEquals(3.0, hht.getMaxSample(), 0.00001);
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
		hht.setSampleWindowSize(5);
		assertEquals(5, hht.getSampleWindowShift());
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testSetSampleWindowShift2() {
		hht.setSampleWindowSize(0);
	}

}
