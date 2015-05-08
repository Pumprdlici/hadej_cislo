package icp.application.classification;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import Jama.Matrix;

public class LinearDiscriminantAnalysisAlgorithmsClassifyTest {

	private static LinearDiscriminantAnalysisAlgorithms lda;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		lda = new LinearDiscriminantAnalysisAlgorithms();
		// First comment line createInputAndTargetMatrix(input, targets, fe); in
		// LinearDiscriminantAnalysisAlgorithms
		Matrix input = new Matrix(6, 3);
		input.set(0, 0, 0.3252);
		input.set(0, 1, -1.7115);
		input.set(0, 2, 0.3192);
		input.set(1, 0, -0.7549);
		input.set(1, 1, -0.1022);
		input.set(1, 2, 0.3129);
		input.set(2, 0, 1.3703);
		input.set(2, 1, -0.2414);
		input.set(2, 2, -0.8649);
		input.set(3, 0, 0.9699);
		input.set(3, 1, 2.0933);
		input.set(3, 2, 1.0774);
		input.set(4, 0, 0.8351);
		input.set(4, 1, 2.1093);
		input.set(4, 2, -0.2141);
		input.set(5, 0, 1.6277);
		input.set(5, 1, 0.1363);
		input.set(5, 2, -0.1135);

		Matrix target = new Matrix(6, 1);
		target.set(0, 0, 0);
		target.set(1, 0, 0);
		target.set(2, 0, 0);
		target.set(3, 0, 1);
		target.set(4, 0, 1);
		target.set(5, 0, 1);

		lda.input = input;
		lda.targets = target;

		lda.train(null, null, null);
	}

	@Before
	public void setUp() {
		double[] featureVector = { 3.5, 2.15, 1.125 };
		double[][] feature = new double[1][featureVector.length];
		feature[0] = featureVector;
		lda.input = new Matrix(feature);
	}

	@After
	public void tearDown() throws Exception {
		setUpBeforeClass();
		setUp();
	}

	@Test
	public void testCalculateL1() {
		lda.calculateL();

		assertEquals(-1.0961, lda.l.get(0, 0), 0.0001);
	}

	@Test
	public void testCalculateL2() {
		lda.calculateL();

		assertEquals(
				21.2140,
				lda.l.get(lda.l.getRowDimension() - 1,
						lda.l.getColumnDimension() - 1), 0.0001);
	}

	@Test
	public void testCreateExpL1() {
		lda.calculateL();
		Matrix expL = lda.createExpL();

		assertEquals(0.0000000003341E9, expL.get(0, 0), 0.0001);
	}

	@Test
	public void testCreateExpL2() {
		lda.calculateL();
		Matrix expL = lda.createExpL();

		assertEquals(1.633562805497062E9, expL.get(expL.getRowDimension() - 1,
				expL.getColumnDimension() - 1), 0.0001);
	}

	@Test
	public void testCreateSumL() {
		lda.calculateL();
		Matrix expL = lda.createExpL();
		Matrix sumL = new Matrix(expL.getRowDimension(), 1);
		lda.createSumL(expL, sumL);

		assertEquals(1.633562805831228E9, sumL.get(0, 0), 0.0001);
	}

	@Test
	public void testCreateRepmatL() {
		lda.calculateL();
		Matrix expL = lda.createExpL();
		Matrix sumL = new Matrix(expL.getRowDimension(), 1);
		lda.createSumL(expL, sumL);
		Matrix repmatL = new Matrix(sumL.getRowDimension(),
				2 * sumL.getColumnDimension());
		lda.createRepmatL(repmatL, sumL);

		assertEquals(
				1.633562805831228E9,
				repmatL.get(repmatL.getRowDimension() - 1,
						repmatL.getColumnDimension() - 1), 0.0001);
	}

	@Test
	public void testCalculateP1() {
		lda.calculateL();
		Matrix expL = lda.createExpL();
		Matrix sumL = new Matrix(expL.getRowDimension(), 1);
		lda.createSumL(expL, sumL);
		Matrix repmatL = new Matrix(sumL.getRowDimension(),
				2 * sumL.getColumnDimension());
		lda.createRepmatL(repmatL, sumL);
		lda.p = expL.arrayRightDivide(repmatL);

		assertEquals(0.000000000204563, lda.p.get(0, 0), 0.0001);
	}

	@Test
	public void testCalculateP2() {
		lda.calculateL();
		Matrix expL = lda.createExpL();
		Matrix sumL = new Matrix(expL.getRowDimension(), 1);
		lda.createSumL(expL, sumL);
		Matrix repmatL = new Matrix(sumL.getRowDimension(),
				2 * sumL.getColumnDimension());
		lda.createRepmatL(repmatL, sumL);
		lda.p = expL.arrayRightDivide(repmatL);

		assertEquals(
				0.999999999795437,
				lda.p.get(lda.p.getRowDimension() - 1,
						lda.p.getColumnDimension() - 1), 0.0001);
	}

	@Test
	public void testClassify() throws Exception {
		lda = new LinearDiscriminantAnalysisAlgorithms();
		setUpBeforeClass();
		double[] featureVector = { 3.5, 2.15, 1.125 };
		lda.classify(featureVector);

		assertEquals(
				0.999999999795437,
				lda.p.get(lda.p.getRowDimension() - 1,
						lda.p.getColumnDimension() - 1), 0.0001);
	}

}
