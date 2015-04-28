package artifactDetection;

import static org.junit.Assert.*;
import icp.algorithm.math.GradientArtifactDet;

import org.junit.Before;
import org.junit.Test;

public class GradientArtifactDetTest {

	public GradientArtifactDet a;

	@Before
	public void setUp() throws Exception {
		
		a = new GradientArtifactDet();
	}

	@Test
	public void testGradientArtifactDet() {
		
		assertEquals(GradientArtifactDet.DEFAULT_MAXDIFF, a.getMaxDiff(), 0.0003);
	}

	@Test
	public void testGradientArtifactDetDouble() {
		
		a = new GradientArtifactDet(-0.1);
		assertEquals(GradientArtifactDet.DEFAULT_MAXDIFF, a.getMaxDiff(), 0.0003);
	}
	
	@Test
	public void testGradientArtifactDetDouble2() { 
		
		a=new GradientArtifactDet(0);
		assertEquals(0, a.getMaxDiff(), 0.0003);
	}

	@Test
	public void testDetectArtifact1() {
		a = new GradientArtifactDet(40);
		assertEquals(null, a.detectArtifact(TestovaciData.eSpatna));
	}
	
	@Test
	public void testDetectArtifact2() {
		
		a = new GradientArtifactDet(60);
		assertEquals(TestovaciData.eSpravna, a.detectArtifact(TestovaciData.eSpravna));
	}

	@Test
	public void testGetMaxDiff() {
		
		assertEquals(a.getMaxDiff(), a.getMaxDiff(), 0.0003);
	}

	@Test
	public void testSetMaxDiff() {
		
		a.setMaxDiff(-0.1);
		assertEquals(GradientArtifactDet.DEFAULT_MAXDIFF, a.getMaxDiff(), 0.0003);
	}
	
	@Test
	public void testSetMaxDiff2() {
		
		a.setMaxDiff(0);
		assertEquals(0, a.getMaxDiff(), 0.0003);
	}

}
