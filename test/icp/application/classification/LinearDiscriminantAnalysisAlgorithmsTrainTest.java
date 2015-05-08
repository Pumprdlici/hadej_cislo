package icp.application.classification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.stat.correlation.Covariance;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import Jama.Matrix;

public class LinearDiscriminantAnalysisAlgorithmsTrainTest {

	private static LinearDiscriminantAnalysisAlgorithms lda;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		lda = new LinearDiscriminantAnalysisAlgorithms();
		Matrix input = new Matrix(3, 3);
		input.set(0, 0, 1);
		input.set(0, 1, 2);
		input.set(0, 2, 3);
		input.set(1, 0, 4);
		input.set(1, 1, 5);
		input.set(1, 2, 6);
		input.set(2, 0, 7);
		input.set(2, 1, 8);
		input.set(2, 2, 9);

		Matrix target = new Matrix(3, 1);
		target.set(0, 0, 0);
		target.set(1, 0, 1);
		target.set(2, 0, 1);

		lda.input = input;
		lda.targets = target;
	}
	
	@After
	public void tearDown() throws Exception {
		setUpBeforeClass();
	}

	@Test
	public void testDetermineSizeOfInput1() {
		lda.sizeOfInput = lda.determineSizeOfInput();

		assertEquals(3, lda.sizeOfInput[0]);
	}

	@Test
	public void testDetermineSizeOfInput2() {
		lda.sizeOfInput = lda.determineSizeOfInput();

		assertEquals(3, lda.sizeOfInput[1]);
	}

	@Test
	public void testDetermineClasses1() {
		lda.sizeOfInput = lda.determineSizeOfInput();
		lda.classes = lda.determineClasses();

		assertEquals(0, lda.classes[0], 0.0001);
	}

	@Test
	public void testDetermineClasses2() {
		lda.classes = lda.determineClasses();
		lda.sizeOfInput = lda.determineSizeOfInput();

		assertEquals(1, lda.classes[1], 0.0001);
	}

	@Test
	public void testDetermineClassCount() {
		lda.classes = lda.determineClasses();
		lda.sizeOfInput = lda.determineSizeOfInput();
		lda.classCount = lda.determineClassCount();

		assertEquals(2, lda.classCount);
	}

	@Test
	public void testInit1() {
		lda.classes = lda.determineClasses();
		lda.sizeOfInput = lda.determineSizeOfInput();
		lda.classCount = lda.determineClassCount();
		Matrix ngroup = new Matrix(2, 1, Double.NaN);
		lda.init();

		assertEquals(ngroup.get(0, 0), lda.nGroup.get(0, 0), 0.0001);
	}

	@Test
	public void testInit2() {
		lda.classes = lda.determineClasses();
		lda.sizeOfInput = lda.determineSizeOfInput();
		lda.classCount = lda.determineClassCount();
		Matrix ngroup = new Matrix(2, 1, Double.NaN);
		lda.init();

		assertEquals(
				ngroup.get(ngroup.getRowDimension() - 1,
						ngroup.getColumnDimension() - 1),
				lda.nGroup.get(lda.nGroup.getRowDimension() - 1,
						lda.nGroup.getColumnDimension() - 1), 0.0001);
	}

	@Test
	public void testCalculateGroup1() {
		lda.classes = lda.determineClasses();
		lda.sizeOfInput = lda.determineSizeOfInput();
		lda.classCount = lda.determineClassCount();
		lda.init();
		Matrix group = new Matrix(3, 1);
		group.set(0, 0, 1);
		group.set(1, 0, 0);
		group.set(2, 0, 0);
		Matrix groupMat = new Matrix(3, 1);
		groupMat = lda.calculateGroup(groupMat, 0);

		assertEquals(group.get(0, 0), groupMat.get(0, 0), 0.0001);
	}

	@Test
	public void testCalculateGroup2() {
		lda.classes = lda.determineClasses();
		lda.sizeOfInput = lda.determineSizeOfInput();
		lda.classCount = lda.determineClassCount();
		lda.init();
		Matrix group = new Matrix(3, 1);
		group.set(0, 0, 1);
		group.set(1, 0, 0);
		group.set(2, 0, 0);
		Matrix groupMat = new Matrix(3, 1);
		groupMat = lda.calculateGroup(groupMat, 0);

		assertEquals(
				group.get(group.getRowDimension() - 1,
						group.getColumnDimension() - 1),
				groupMat.get(groupMat.getRowDimension() - 1,
						groupMat.getColumnDimension() - 1), 0.0001);
	}

	@Test
	public void testCalculateGroup3() {
		lda.classes = lda.determineClasses();
		lda.sizeOfInput = lda.determineSizeOfInput();
		lda.classCount = lda.determineClassCount();
		lda.init();
		Matrix group = new Matrix(3, 1);
		group.set(0, 0, 0);
		group.set(1, 0, 1);
		group.set(2, 0, 1);
		Matrix groupMat = new Matrix(3, 1);
		groupMat = lda.calculateGroup(groupMat, 1);

		assertEquals(group.get(0, 0), groupMat.get(0, 0), 0.0001);
	}

	@Test
	public void testCalculateGroup4() {
		lda.classes = lda.determineClasses();
		lda.sizeOfInput = lda.determineSizeOfInput();
		lda.classCount = lda.determineClassCount();
		lda.init();
		Matrix group = new Matrix(3, 1);
		group.set(0, 0, 0);
		group.set(1, 0, 1);
		group.set(2, 0, 1);
		Matrix groupMat = new Matrix(3, 1);
		groupMat = lda.calculateGroup(groupMat, 1);

		assertEquals(
				group.get(group.getRowDimension() - 1,
						group.getColumnDimension() - 1),
				groupMat.get(groupMat.getRowDimension() - 1,
						groupMat.getColumnDimension() - 1), 0.0001);
	}

	@Test
	public void testCalculateNGroup1() {
		lda.classes = lda.determineClasses();
		lda.sizeOfInput = lda.determineSizeOfInput();
		lda.classCount = lda.determineClassCount();
		lda.init();
		Matrix group = new Matrix(3, 1);
		group = lda.calculateGroup(group, 0);
		lda.calculateNGroup(group, 0);

		assertEquals(1, lda.nGroup.get(0, 0), 0.0001);
	}

	@Test
	public void testCalculateNGroup2() {
		lda.classes = lda.determineClasses();
		lda.sizeOfInput = lda.determineSizeOfInput();
		lda.classCount = lda.determineClassCount();
		lda.init();
		Matrix group = new Matrix(3, 1);
		group = lda.calculateGroup(group, 1);
		lda.calculateNGroup(group, 1);

		assertEquals(2, lda.nGroup.get(1, 0), 0.0001);
	}

	@Test
	public void testCalculateGroupMean1() {
		lda.classes = lda.determineClasses();
		lda.sizeOfInput = lda.determineSizeOfInput();
		lda.classCount = lda.determineClassCount();
		lda.init();
		Matrix group = new Matrix(3, 1);
		group = lda.calculateGroup(group, 0);
		lda.calculateNGroup(group, 0);
		for (int i = 0; i < lda.groupMean.getColumnDimension(); i++) {
			lda.groupMean.set(0, i, lda.calculateMean(group, 0, i));
		}

		assertEquals(1, lda.groupMean.get(0, 0), 0.0001);
	}

	@Test
	public void testCalculateGroupMean2() {
		lda.classes = lda.determineClasses();
		lda.sizeOfInput = lda.determineSizeOfInput();
		lda.classCount = lda.determineClassCount();
		lda.init();
		Matrix group = new Matrix(3, 1);
		group = lda.calculateGroup(group, 0);
		lda.calculateNGroup(group, 0);
		for (int i = 0; i < lda.groupMean.getColumnDimension(); i++) {
			lda.groupMean.set(0, i, lda.calculateMean(group, 0, i));
		}

		assertEquals(3,
				lda.groupMean.get(0, lda.groupMean.getColumnDimension() - 1),
				0.0001);
	}

	@Test
	public void testCalculateGroupMean3() {
		lda.classes = lda.determineClasses();
		lda.sizeOfInput = lda.determineSizeOfInput();
		lda.classCount = lda.determineClassCount();
		lda.init();
		Matrix group = new Matrix(3, 1);
		group = lda.calculateGroup(group, 0);
		lda.calculateNGroup(group, 0);
		for (int i = 0; i < lda.groupMean.getColumnDimension(); i++) {
			lda.groupMean.set(0, i, lda.calculateMean(group, 0, i));
		}
		group = lda.calculateGroup(group, 1);
		lda.calculateNGroup(group, 1);
		for (int i = 0; i < lda.groupMean.getColumnDimension(); i++) {
			lda.groupMean.set(1, i, lda.calculateMean(group, 1, i));
		}

		assertEquals(11. / 2, lda.groupMean.get(1, 0), 0.0001);
	}

	@Test
	public void testCalculateGroupMean4() {
		lda.classes = lda.determineClasses();
		lda.sizeOfInput = lda.determineSizeOfInput();
		lda.classCount = lda.determineClassCount();
		lda.init();
		Matrix group = new Matrix(3, 1);
		group = lda.calculateGroup(group, 0);
		lda.calculateNGroup(group, 0);
		for (int i = 0; i < lda.groupMean.getColumnDimension(); i++) {
			lda.groupMean.set(0, i, lda.calculateMean(group, 0, i));
		}
		group = lda.calculateGroup(group, 1);
		lda.calculateNGroup(group, 1);
		for (int i = 0; i < lda.groupMean.getColumnDimension(); i++) {
			lda.groupMean.set(1, i, lda.calculateMean(group, 1, i));
		}

		assertEquals(15. / 2, lda.groupMean.get(
				lda.groupMean.getRowDimension() - 1,
				lda.groupMean.getColumnDimension() - 1), 0.0001);
	}

	@Test
	public void testGetMatrixForCovariance1() {
		lda.classes = lda.determineClasses();
		lda.sizeOfInput = lda.determineSizeOfInput();
		lda.classCount = lda.determineClassCount();
		lda.init();
		Matrix group = new Matrix(3, 1);
		group = lda.calculateGroup(group, 0);
		lda.calculateNGroup(group, 0);
		for (int i = 0; i < lda.groupMean.getColumnDimension(); i++) {
			lda.groupMean.set(0, i, lda.calculateMean(group, 0, i));
		}
		double[][] cov = { { 1, 2, 3 } };
		double[][] covMat = lda.getMatrixForCovariance(group);

		assertEquals(cov[0][0], covMat[0][0], 0.0001);
	}

	@Test
	public void testGetMatrixForCovariance2() {
		lda.classes = lda.determineClasses();
		lda.sizeOfInput = lda.determineSizeOfInput();
		lda.classCount = lda.determineClassCount();
		lda.init();
		Matrix group = new Matrix(3, 1);
		group = lda.calculateGroup(group, 0);
		lda.calculateNGroup(group, 0);
		for (int i = 0; i < lda.groupMean.getColumnDimension(); i++) {
			lda.groupMean.set(0, i, lda.calculateMean(group, 0, i));
		}
		double[][] cov = { { 1, 2, 3 } };
		double[][] covMat = lda.getMatrixForCovariance(group);

		assertEquals(cov[0][cov[0].length - 1],
				covMat[0][covMat[0].length - 1], 0.0001);
	}

	@Test
	public void testGetMatrixForCovariance3() {
		lda.classes = lda.determineClasses();
		lda.sizeOfInput = lda.determineSizeOfInput();
		lda.classCount = lda.determineClassCount();
		lda.init();
		Matrix group = new Matrix(3, 1);
		group = lda.calculateGroup(group, 0);
		lda.calculateNGroup(group, 0);
		for (int i = 0; i < lda.groupMean.getColumnDimension(); i++) {
			lda.groupMean.set(0, i, lda.calculateMean(group, 0, i));
		}
		double[][] cov = { { 1, 2, 3 } };
		double[][] covMat = lda.getMatrixForCovariance(group);

		assertEquals(cov[cov.length - 1][0], covMat[covMat.length - 1][0],
				0.0001);
	}

	@Test
	public void testGetMatrixForCovariance4() {
		lda.classes = lda.determineClasses();
		lda.sizeOfInput = lda.determineSizeOfInput();
		lda.classCount = lda.determineClassCount();
		lda.init();
		Matrix group = new Matrix(3, 1);
		group = lda.calculateGroup(group, 0);
		lda.calculateNGroup(group, 0);
		for (int i = 0; i < lda.groupMean.getColumnDimension(); i++) {
			lda.groupMean.set(0, i, lda.calculateMean(group, 0, i));
		}
		double[][] cov = { { 1, 2, 3 } };
		double[][] covMat = lda.getMatrixForCovariance(group);

		assertEquals(cov[cov.length - 1][cov[0].length - 1],
				covMat[covMat.length - 1][covMat[0].length - 1], 0.0001);
	}

	@Test
	public void testGetMatrixForCovariance5() {
		lda.classes = lda.determineClasses();
		lda.sizeOfInput = lda.determineSizeOfInput();
		lda.classCount = lda.determineClassCount();
		lda.init();
		Matrix group = new Matrix(3, 1);
		group = lda.calculateGroup(group, 0);
		lda.calculateNGroup(group, 0);
		for (int i = 0; i < lda.groupMean.getColumnDimension(); i++) {
			lda.groupMean.set(0, i, lda.calculateMean(group, 0, i));
		}
		group = lda.calculateGroup(group, 1);
		lda.calculateNGroup(group, 1);
		for (int i = 0; i < lda.groupMean.getColumnDimension(); i++) {
			lda.groupMean.set(1, i, lda.calculateMean(group, 1, i));
		}
		double[][] cov = { { 4, 5, 6 }, { 7, 8, 9 } };
		double[][] covMat = lda.getMatrixForCovariance(group);

		assertEquals(cov[cov.length - 1][0], covMat[covMat.length - 1][0],
				0.0001);
	}

	@Test
	public void testGetMatrixForCovariance6() {
		lda.classes = lda.determineClasses();
		lda.sizeOfInput = lda.determineSizeOfInput();
		lda.classCount = lda.determineClassCount();
		lda.init();
		Matrix group = new Matrix(3, 1);
		group = lda.calculateGroup(group, 0);
		lda.calculateNGroup(group, 0);
		for (int i = 0; i < lda.groupMean.getColumnDimension(); i++) {
			lda.groupMean.set(0, i, lda.calculateMean(group, 0, i));
		}
		group = lda.calculateGroup(group, 1);
		lda.calculateNGroup(group, 1);
		for (int i = 0; i < lda.groupMean.getColumnDimension(); i++) {
			lda.groupMean.set(1, i, lda.calculateMean(group, 1, i));
		}
		double[][] cov = { { 4, 5, 6 }, { 7, 8, 9 } };
		double[][] covMat = lda.getMatrixForCovariance(group);

		assertEquals(cov[cov.length - 1][cov[0].length - 1],
				covMat[covMat.length - 1][covMat[0].length - 1], 0.0001);
	}

	@Test
	public void testCalculatePooledCovariance1() {
		lda.classes = lda.determineClasses();
		lda.sizeOfInput = lda.determineSizeOfInput();
		lda.classCount = lda.determineClassCount();
		lda.init();
		Matrix group = new Matrix(lda.targets.getRowDimension(), 1);
		for (int i = 0; i < 1; i++) {
			group = lda.calculateGroup(group, i);

			lda.calculateNGroup(group, i);

			// For each column, calculate average of vectors that belong to
			// class i
			for (int j = 0; j < lda.groupMean.getColumnDimension(); j++) {
				lda.groupMean.set(i, j, lda.calculateMean(group, i, j));
			}

			// Calculate covariance matrix
			double[][] covData = lda.getMatrixForCovariance(group);
			if (covData.length == 1) {
				double[][] data = { covData[0], covData[0] };
				covData = data;
			}
			RealMatrix covMatrix = new Covariance(covData)
					.getCovarianceMatrix();
			Matrix cov = new Matrix(covMatrix.getData());

			// For each column, accumulate pooled covariance
			lda.calculatePooledCovariance(cov, i);
		}

		assertEquals(0, lda.pooledCovariance.get(0, 0), 0.0001);
	}

	@Test
	public void testCalculatePooledCovariance2() {
		lda.classes = lda.determineClasses();
		lda.sizeOfInput = lda.determineSizeOfInput();
		lda.classCount = lda.determineClassCount();
		lda.init();
		Matrix group = new Matrix(lda.targets.getRowDimension(), 1);
		for (int i = 0; i < 1; i++) {
			group = lda.calculateGroup(group, i);

			lda.calculateNGroup(group, i);

			// For each column, calculate average of vectors that belong to
			// class i
			for (int j = 0; j < lda.groupMean.getColumnDimension(); j++) {
				lda.groupMean.set(i, j, lda.calculateMean(group, i, j));
			}

			// Calculate covariance matrix
			double[][] covData = lda.getMatrixForCovariance(group);
			if (covData.length == 1) {
				double[][] data = { covData[0], covData[0] };
				covData = data;
			}
			RealMatrix covMatrix = new Covariance(covData)
					.getCovarianceMatrix();
			Matrix cov = new Matrix(covMatrix.getData());

			// For each column, accumulate pooled covariance
			lda.calculatePooledCovariance(cov, i);
		}

		assertEquals(
				0,
				lda.pooledCovariance.get(0,
						lda.pooledCovariance.getColumnDimension() - 1), 0.0001);
	}

	@Test
	public void testCalculatePooledCovariance3() {
		lda.classes = lda.determineClasses();
		lda.sizeOfInput = lda.determineSizeOfInput();
		lda.classCount = lda.determineClassCount();
		lda.init();
		Matrix group = new Matrix(lda.targets.getRowDimension(), 1);
		for (int i = 0; i < lda.classCount; i++) {
			group = lda.calculateGroup(group, i);

			lda.calculateNGroup(group, i);

			// For each column, calculate average of vectors that belong to
			// class i
			for (int j = 0; j < lda.groupMean.getColumnDimension(); j++) {
				lda.groupMean.set(i, j, lda.calculateMean(group, i, j));
			}

			// Calculate covariance matrix
			double[][] covData = lda.getMatrixForCovariance(group);
			if (covData.length == 1) {
				double[][] data = { covData[0], covData[0] };
				covData = data;
			}
			RealMatrix covMatrix = new Covariance(covData)
					.getCovarianceMatrix();
			Matrix cov = new Matrix(covMatrix.getData());

			// For each column, accumulate pooled covariance
			lda.calculatePooledCovariance(cov, i);
		}

		assertEquals(4.5, lda.pooledCovariance.get(0, 0), 0.0001);
	}

	@Test
	public void testCalculatePooledCovariance4() {
		lda.classes = lda.determineClasses();
		lda.sizeOfInput = lda.determineSizeOfInput();
		lda.classCount = lda.determineClassCount();
		lda.init();
		Matrix group = new Matrix(lda.targets.getRowDimension(), 1);
		for (int i = 0; i < lda.classCount; i++) {
			group = lda.calculateGroup(group, i);

			lda.calculateNGroup(group, i);

			// For each column, calculate average of vectors that belong to
			// class i
			for (int j = 0; j < lda.groupMean.getColumnDimension(); j++) {
				lda.groupMean.set(i, j, lda.calculateMean(group, i, j));
			}

			// Calculate covariance matrix
			double[][] covData = lda.getMatrixForCovariance(group);
			if (covData.length == 1) {
				double[][] data = { covData[0], covData[0] };
				covData = data;
			}
			RealMatrix covMatrix = new Covariance(covData)
					.getCovarianceMatrix();
			Matrix cov = new Matrix(covMatrix.getData());

			// For each column, accumulate pooled covariance
			lda.calculatePooledCovariance(cov, i);
		}

		assertEquals(4.5, lda.pooledCovariance.get(
				lda.pooledCovariance.getRowDimension() - 1,
				lda.pooledCovariance.getColumnDimension() - 1), 0.0001);
	}

	@Test
	public void testPriorProb1() {
		lda.classes = lda.determineClasses();
		lda.sizeOfInput = lda.determineSizeOfInput();
		lda.classCount = lda.determineClassCount();
		lda.init();
		Matrix group = new Matrix(lda.targets.getRowDimension(), 1);
		for (int i = 0; i < 1; i++) {
			group = lda.calculateGroup(group, i);

			lda.calculateNGroup(group, i);

			// For each column, calculate average of vectors that belong to
			// class i
			for (int j = 0; j < lda.groupMean.getColumnDimension(); j++) {
				lda.groupMean.set(i, j, lda.calculateMean(group, i, j));
			}

			// Calculate covariance matrix
			double[][] covData = lda.getMatrixForCovariance(group);
			if (covData.length == 1) {
				double[][] data = { covData[0], covData[0] };
				covData = data;
			}
			RealMatrix covMatrix = new Covariance(covData)
					.getCovarianceMatrix();
			Matrix cov = new Matrix(covMatrix.getData());

			// For each column, accumulate pooled covariance
			lda.calculatePooledCovariance(cov, i);
		}
		// Calculate prior probabilities
		for (int i = 0; i < lda.priorProb.getRowDimension(); i++) {
			lda.priorProb.set(i, 0, lda.nGroup.get(i, 0) / lda.sizeOfInput[0]);
		}

		assertEquals(0.3333, lda.priorProb.get(0, 0), 0.0001);
	}

	@Test
	public void testPriorProb2() {
		lda.classes = lda.determineClasses();
		lda.sizeOfInput = lda.determineSizeOfInput();
		lda.classCount = lda.determineClassCount();
		lda.init();
		Matrix group = new Matrix(lda.targets.getRowDimension(), 1);
		for (int i = 0; i < lda.classCount; i++) {
			group = lda.calculateGroup(group, i);

			lda.calculateNGroup(group, i);

			// For each column, calculate average of vectors that belong to
			// class i
			for (int j = 0; j < lda.groupMean.getColumnDimension(); j++) {
				lda.groupMean.set(i, j, lda.calculateMean(group, i, j));
			}

			// Calculate covariance matrix
			double[][] covData = lda.getMatrixForCovariance(group);
			if (covData.length == 1) {
				double[][] data = { covData[0], covData[0] };
				covData = data;
			}
			RealMatrix covMatrix = new Covariance(covData)
					.getCovarianceMatrix();
			Matrix cov = new Matrix(covMatrix.getData());

			// For each column, accumulate pooled covariance
			lda.calculatePooledCovariance(cov, i);
		}
		// Calculate prior probabilities
		for (int i = 0; i < lda.priorProb.getRowDimension(); i++) {
			lda.priorProb.set(i, 0, lda.nGroup.get(i, 0) / lda.sizeOfInput[0]);
		}

		assertEquals(0.6667,
				lda.priorProb.get(lda.priorProb.getRowDimension() - 1, 0),
				0.0001);
	}

	@Test
	public void testMultiplyVectorWithMatrix1() {
		Matrix v = new Matrix(1, 2);
		v.set(0, 0, 1);
		v.set(0, 1, 2);
		Matrix m = new Matrix(2, 2);
		m.set(0, 0, 1);
		m.set(0, 1, 2);
		m.set(1, 0, 1);
		m.set(1, 1, 2);
		Matrix vm = lda.multiplyVectorWithMatrix(v, m);

		assertEquals(3, vm.get(0, 0), 0.0001);
	}

	@Test
	public void testMultiplyVectorWithMatrix2() {
		Matrix v = new Matrix(1, 2);
		v.set(0, 0, 1);
		v.set(0, 1, 2);
		Matrix m = new Matrix(2, 2);
		m.set(0, 0, 1);
		m.set(0, 1, 2);
		m.set(1, 0, 1);
		m.set(1, 1, 2);
		Matrix vm = lda.multiplyVectorWithMatrix(v, m);

		assertEquals(6, vm.get(0, vm.getColumnDimension() - 1), 0.0001);
	}

	@Test
	public void testMultiplyTwoVectors() {
		Matrix v = new Matrix(1, 2);
		v.set(0, 0, 1);
		v.set(0, 1, 2);
		Matrix m = new Matrix(2, 1);
		m.set(0, 0, 3);
		m.set(1, 0, 4);
		double vm = lda.multiplyTwoVectors(v, m);

		assertEquals(11, vm, 0.0001);
	}

	@Test
	public void testCreateW1() {
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

		lda.classes = lda.determineClasses();
		lda.sizeOfInput = lda.determineSizeOfInput();
		lda.classCount = lda.determineClassCount();
		lda.init();
		Matrix group = new Matrix(lda.targets.getRowDimension(), 1);
		for (int i = 0; i < lda.classCount; i++) {
			group = lda.calculateGroup(group, i);

			lda.calculateNGroup(group, i);

			// For each column, calculate average of vectors that belong to
			// class i
			for (int j = 0; j < lda.groupMean.getColumnDimension(); j++) {
				lda.groupMean.set(i, j, lda.calculateMean(group, i, j));
			}

			// Calculate covariance matrix
			double[][] covData = lda.getMatrixForCovariance(group);
			if (covData.length == 1) {
				double[][] data = { covData[0], covData[0] };
				covData = data;
			}
			RealMatrix covMatrix = new Covariance(covData)
					.getCovarianceMatrix();
			Matrix cov = new Matrix(covMatrix.getData());

			// For each column, accumulate pooled covariance
			lda.calculatePooledCovariance(cov, i);
		}
		// Calculate prior probabilities
		for (int i = 0; i < lda.priorProb.getRowDimension(); i++) {
			lda.priorProb.set(i, 0, lda.nGroup.get(i, 0) / lda.sizeOfInput[0]);
		}
		lda.createW(0);

		assertEquals(-0.9345, lda.w.get(0, 0), 0.0001);
	}

	@Test
	public void testCreateW2() {
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

		lda.classes = lda.determineClasses();
		lda.sizeOfInput = lda.determineSizeOfInput();
		lda.classCount = lda.determineClassCount();
		lda.init();
		Matrix group = new Matrix(lda.targets.getRowDimension(), 1);
		for (int i = 0; i < lda.classCount; i++) {
			group = lda.calculateGroup(group, i);

			lda.calculateNGroup(group, i);

			// For each column, calculate average of vectors that belong to
			// class i
			for (int j = 0; j < lda.groupMean.getColumnDimension(); j++) {
				lda.groupMean.set(i, j, lda.calculateMean(group, i, j));
			}

			// Calculate covariance matrix
			double[][] covData = lda.getMatrixForCovariance(group);
			if (covData.length == 1) {
				double[][] data = { covData[0], covData[0] };
				covData = data;
			}
			RealMatrix covMatrix = new Covariance(covData)
					.getCovarianceMatrix();
			Matrix cov = new Matrix(covMatrix.getData());

			// For each column, accumulate pooled covariance
			lda.calculatePooledCovariance(cov, i);
		}
		// Calculate prior probabilities
		for (int i = 0; i < lda.priorProb.getRowDimension(); i++) {
			lda.priorProb.set(i, 0, lda.nGroup.get(i, 0) / lda.sizeOfInput[0]);
		}
		lda.createW(0);
		lda.createW(1);

		assertEquals(-5.8351, lda.w.get(1, 0), 0.0001);
	}

	@Test
	public void testCalculateW() {
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

		lda.classes = lda.determineClasses();
		lda.sizeOfInput = lda.determineSizeOfInput();
		lda.classCount = lda.determineClassCount();
		lda.init();
		lda.calculateW();

		assertEquals(-5.8351, lda.w.get(1, 0), 0.0001);
	}

	@Test
	public void testTrain1() {
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

		assertEquals(-0.9345, lda.w.get(0, 0), 0.0001);
	}

	@Test
	public void testTrain2() {
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

		assertEquals(-5.8351, lda.w.get(1, 0), 0.0001);
	}

}
