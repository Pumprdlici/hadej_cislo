package artifactDetection;

import static org.junit.Assert.*;
import icp.algorithm.math.CorrelationArtifactDet;

import org.junit.Before;
import org.junit.Test;

public class CorrelationArtifactDetTest {

	public CorrelationArtifactDet a;
	

	@Before
	public void setUp() throws Exception {
		
		a = new CorrelationArtifactDet();
	}

	@Test
	public void testCorrelationArtifactDet1() {
		
		assertEquals(CorrelationArtifactDet.DEFAULT_THRESHOLD, a.getThreshold(), 0.0003);
	}
	
	@Test
	public void testCorrelationArtifactDouble1() {
		
		a = new CorrelationArtifactDet(-1.0);
		assertEquals(-1.0, a.getThreshold(), 0.0003);
	}
	
	@Test
	public void testCorrelationArtifactDouble2() {
		
		a = new CorrelationArtifactDet(-0.3);
		assertEquals(-0.3, a.getThreshold(), 0.0003);
	}
	
	@Test
	public void testCorrelationArtifactDouble3() {
		
		a = new CorrelationArtifactDet(-1.1);
		assertEquals(CorrelationArtifactDet.DEFAULT_THRESHOLD, a.getThreshold(), 0.0003);
	}
	
	@Test
	public void testCorrelationArtifactDouble4() {
		
		a = new CorrelationArtifactDet(1.0);
		assertEquals(1.0, a.getThreshold(), 0.0003);
	}
	
	@Test
	public void testCorrelationArtifactDouble5() {
		
		a = new CorrelationArtifactDet(0.3);
		assertEquals(0.3, a.getThreshold(), 0.0003);
	}
	
	@Test
	public void testCorrelationArtifactDouble6() { 
		
		a = new CorrelationArtifactDet(1.1);
		assertEquals(CorrelationArtifactDet.DEFAULT_THRESHOLD, a.getThreshold(), 0.0003);
	}

	

	@Test
	public void testDetectArtifactEpochMessenger1() {

		assertEquals(null, a.detectArtifact(TestovaciData.eCor, TestovaciData.vzor1));
	}
	
	@Test
	public void testDetectArtifactEpochMessenger2() {

		assertEquals(TestovaciData.eCor, a.detectArtifact(TestovaciData.eCor, TestovaciData.vzor2));
	}

	@Test
	public void testGetThreshold() {
		
		a.getThreshold();
		assertEquals(a.threshold, a.getThreshold(), 0.0003);
	}

	
	@Test
	public void testSetThresholdDouble1() {
		
		a.setThreshold(1.0);
		assertEquals(1.0, a.getThreshold(), 0.0003);
	}
	
	@Test
	public void testSetThresholdDouble2() {
		
		a.setThreshold(0.3);
		assertEquals(0.3, a.getThreshold(), 0.0003);
	}

	
	@Test
	public void testSetThresholdDouble3() {
		
		a.setThreshold(1.1);
		assertEquals(CorrelationArtifactDet.DEFAULT_THRESHOLD, a.getThreshold(), 0.0003);
	}
}
