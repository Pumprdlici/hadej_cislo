package icp.application.classification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.stat.correlation.Covariance;

import Jama.Matrix;

public class LinearDiscriminantAnalysisAlgorithms {

	/**
	 * Discovered linear coefficients
	 */
	public Matrix w;
	/**
	 * Linear scores for training data
	 */
	public Matrix l;
	/**
	 * Probabilities for classification
	 */
	public Matrix p;
	/**
	 * Training data matrix
	 */
	public Matrix input;
	/**
	 * Training targets vector
	 */
	public Matrix targets;
	/**
	 * Size of training data matrix
	 */
	public int[] sizeOfInput;
	/**
	 * Unique target classes
	 */
	public double[] classes;
	/**
	 * Number of unique target classes
	 */
	public int classCount;
	/**
	 * Group counts
	 */
	public Matrix nGroup;
	/**
	 * Group sample means
	 */
	public Matrix groupMean;
	/**
	 * Pooled covariance
	 */
	public Matrix pooledCovariance;
	/**
	 * Prior probabilities
	 */
	public Matrix priorProb;

	public LinearDiscriminantAnalysisAlgorithms() {

	}

	/**
	 * Classifier training
	 * 
	 * @param input
	 *            - raw epochs - list of M channels x N time samples
	 * @param targets
	 *            - target classes - list of expected classes (0 or 1)
	 * @param fe
	 *            - method for feature extraction
	 */
	public void train(List<double[][]> input, List<Double> targets,
			IFeatureExtraction fe) {
		/* createInputAndTargetMatrix(input, targets, fe); */
		sizeOfInput = determineSizeOfInput();
		classes = determineClasses();
		classCount = determineClassCount();
		init();
		calculateW();
	}

	/**
	 * Classifies feature vector
	 * 
	 * @param featureVector
	 *            - feature vector
	 * @return probability of epoch to be target or non-target
	 */
	public double classify(double[] featureVector) {
		double[][] feature = new double[1][featureVector.length];
		feature[0] = featureVector;
		input = new Matrix(feature);
		calculateL();
		calculateP();
		return p.get(0, 1);
	}

	/**
	 * Saves data needed for classification
	 * 
	 * @param file
	 *            - name of the file to save into
	 */
	public void save(String file) {
		try {
			File f = new File(file);
			PrintWriter pw = new PrintWriter(f);

			// Writing W
			pw.write(w.getRowDimension() + "\n");
			pw.write(w.getColumnDimension() + "\n");
			w.print(pw, 3, 21);

			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads data needed for classification
	 * 
	 * @param file
	 *            - name of the file
	 */
	public void load(String file) {
		try {
			File f = new File(file);
			FileReader fr;
			fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			int rows = Integer.parseInt(br.readLine());
			int columns = Integer.parseInt(br.readLine());
			String[] numbers = br.readLine().split(" ");
			double[][] matrix = new double[rows][columns];
			int currRow = 0, currColumn = 0;
			for (int i = 0; i < rows; i++) {
				numbers = br.readLine().split(" ");
				for (int j = 0; j < numbers.length; j++) {
					if (numbers[j].equals("-â�ž")) {
						matrix[currRow][currColumn] = -Double.MAX_VALUE;
					} else if (numbers[j].equals("â�ž")) {
						matrix[currRow][currColumn] = Double.MAX_VALUE;
					} else if (numbers[j].equals("")) {
						continue;
					} else {
						matrix[currRow][currColumn] = Double
								.parseDouble(numbers[j]);
					}
					currColumn++;
					if (currColumn >= columns) {
						currColumn = 0;
					}
				}
				currRow++;
				if (currRow >= rows) {
					currRow = 0;
				}
			}

			br.close();
			fr.close();
			w = Matrix.constructWithCopy(matrix);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialization of variables
	 */
	public void init() {
		nGroup = new Matrix(classCount, 1, Double.NaN);
		groupMean = new Matrix(classCount, sizeOfInput[1], Double.NaN);
		pooledCovariance = new Matrix(sizeOfInput[1], sizeOfInput[1], 0);
		w = new Matrix(classCount, sizeOfInput[1] + 1, Double.NaN);
		priorProb = new Matrix(classCount, 1);
	}

	/**
	 * Determines size of input matrix
	 * 
	 * @return size of input matrix
	 */
	public int[] determineSizeOfInput() {
		int[] size = new int[2];
		size[0] = input.getRowDimension();
		size[1] = input.getColumnDimension();
		return size;
	}

	/**
	 * Creates matrices from input and target lists
	 * 
	 * @param input
	 *            - raw epochs - list of M channels x N time samples
	 * @param targets
	 *            - target classes - list of expected classes (0 or 1)
	 * @param fe
	 *            - method for feature extraction
	 */
	public void createInputAndTargetMatrix(List<double[][]> input,
			List<Double> targets, IFeatureExtraction fe) {
		double[][] features = new double[input.size()][fe.getFeatureDimension()];
		double[][] target = new double[targets.size()][1];
		for (int i = 0; i < input.size(); i++) {
			double[][] epoch = input.get(i);
			features[i] = fe.extractFeatures(epoch);
			target[i][0] = targets.get(i);
		}
		this.input = Matrix.constructWithCopy(features);
		this.targets = Matrix.constructWithCopy(target);
	}

	/**
	 * Finds target classes from targets matrix
	 * 
	 * @return unique and sorted classes
	 */
	public double[] determineClasses() {
		List<Double> classesList = new ArrayList<Double>();
		for (int i = 0; i < targets.getRowDimension(); i++) {
			if (classesList.contains(targets.get(i, 0))) {
				continue;
			} else {
				classesList.add(targets.get(i, 0));
			}
		}

		double[] classes = new double[classesList.size()];
		for (int i = 0; i < classesList.size(); i++) {
			classes[i] = classesList.get(i);
		}
		Arrays.sort(classes);
		return classes;
	}

	/**
	 * Counts number of target classes
	 * 
	 * @return number of unique target classes
	 */
	public int determineClassCount() {
		return classes.length;
	}

	/**
	 * Calculates matrix W
	 */
	public void calculateW() {
		// Find out which vectors belongs to which class
		Matrix group = new Matrix(targets.getRowDimension(), 1);
		for (int i = 0; i < classCount; i++) {
			group = calculateGroup(group, i);

			calculateNGroup(group, i);

			// For each column, calculate average of vectors that belong to
			// class i
			for (int j = 0; j < groupMean.getColumnDimension(); j++) {
				groupMean.set(i, j, calculateMean(group, i, j));
			}

			// Calculate covariance matrix
			double[][] covData = getMatrixForCovariance(group);
			if (covData.length == 1) {
				double[][] data = { covData[0], covData[0] };
				covData = data;
			}
			RealMatrix covMatrix = new Covariance(covData)
					.getCovarianceMatrix();
			Matrix cov = new Matrix(covMatrix.getData());

			// For each column, accumulate pooled covariance
			calculatePooledCovariance(cov, i);
		}
		// Calculate prior probabilities
		for (int i = 0; i < priorProb.getRowDimension(); i++) {
			priorProb.set(i, 0, nGroup.get(i, 0) / sizeOfInput[0]);
		}

		for (int i = 0; i < classCount; i++) {
			createW(i);
		}
	}

	/**
	 * Calculates average of column
	 * 
	 * @param group
	 *            - vector, that has 1 in group matrix where index match class
	 * @param i
	 *            - currently processed class
	 * @param j
	 *            - currently processed column of matrix GroupMean
	 * @return average of vector
	 */
	public double calculateMean(Matrix group, int i, int j) {
		double avg = 0;
		int count = 0;
		for (int k = 0; k < input.getRowDimension(); k++) {
			if (group.get(k, 0) == 1) {
				avg += input.get(k, j);
				count++;
			}
		}
		avg /= count;
		return avg;
	}

	/**
	 * Selects submatrix of input with vector that belong to currently processed
	 * class
	 * 
	 * @param group
	 *            - group matrix
	 * @return submatrix to calculate covariance matrix from
	 */
	public double[][] getMatrixForCovariance(Matrix group) {
		int rows = 0;
		for (int i = 0; i < group.getRowDimension(); i++) {
			if (group.get(i, 0) == 1) {
				rows++;
			}
		}
		double[][] cov = new double[rows][sizeOfInput[1]];
		for (int i = 0, j = 0; i < input.getRowDimension(); i++) {
			if (group.get(i, 0) == 1) {
				for (int k = 0; k < sizeOfInput[1]; k++) {
					cov[j][k] = input.get(i, k);
				}
				j++;
			}
		}
		return cov;
	}

	public void calculatePooledCovariance(Matrix cov, int i) {
		pooledCovariance = pooledCovariance
				.plus(cov.times((nGroup.get(i, 0) - 1)
						/ (sizeOfInput[0] - classCount)));
	}

	/**
	 * Calculates group for currently processed class
	 * 
	 * @param group
	 *            - group matrix
	 * @param i
	 *            - currently processed class
	 * @return group matrix with newly filled column
	 */
	public Matrix calculateGroup(Matrix group, int i) {
		for (int j = 0; j < targets.getRowDimension(); j++) {
			if (targets.get(j, 0) == classes[i]) {
				group.set(j, 0, 1);
			} else {
				group.set(j, 0, 0);
			}
		}
		return group;
	}

	/**
	 * Calculate number of vectors that belongs to class i
	 * 
	 * @param group
	 *            - group matrix
	 * @param i
	 *            - currently processed class
	 */
	public void calculateNGroup(Matrix group, int i) {
		double sum = 0;
		for (int j = 0; j < group.getRowDimension(); j++) {
			sum += group.get(j, 0);
		}
		nGroup.set(i, 0, sum);
	}

	/**
	 * Creates part of matrix W for currently processed class
	 * 
	 * @param temp
	 *            - coefficients for matrix W
	 * @param i
	 *            - currently processed class
	 */
	public void createW(int i) {
		Matrix temp = new Matrix(1, groupMean.getColumnDimension());
		temp = multiplyVectorWithMatrix(groupMean.getMatrix(i, i, 0,
				groupMean.getColumnDimension() - 1), pooledCovariance.inverse());
		Matrix temp2 = temp.copy();
		double temp2Number = multiplyTwoVectors(temp2,
				groupMean
						.getMatrix(i, i, 0, groupMean.getColumnDimension() - 1)
						.transpose());
		temp2Number *= -0.5;
		w.set(i, 0, temp2Number + Math.log(priorProb.get(i, 0)));
		w.setMatrix(i, i, 1, w.getColumnDimension() - 1, temp);
	}

	/**
	 * Multiplies vector with matrix
	 * 
	 * @param v
	 *            - matrix with one row (vector)
	 * @param m
	 *            - matrix
	 * @return matrix with one row (vector), the result of multiplication
	 */
	public Matrix multiplyVectorWithMatrix(Matrix v, Matrix m) {
		Matrix ans = new Matrix(1, v.getColumnDimension());
		for (int i = 0; i < v.getColumnDimension(); i++) {
			double sum = 0;
			for (int j = 0, k = 0; j < v.getColumnDimension(); j++, k++) {
				sum += v.get(0, j) * m.get(k, i);
			}
			ans.set(0, i, sum);
		}
		return ans;
	}

	/**
	 * Multiplies two vectors
	 * 
	 * @param v
	 *            - vector with one row
	 * @param w
	 *            - vector with one column
	 * @return result of multiplication
	 */
	public double multiplyTwoVectors(Matrix v, Matrix w) {
		double ans = 0;
		for (int i = 0; i < v.getColumnDimension(); i++) {
			ans += v.get(0, i) * w.get(i, 0);
		}
		return ans;
	}

	/**
	 * Calculates matrix L
	 */
	public void calculateL() {
		l = new Matrix(input.getRowDimension(), input.getColumnDimension() + 1,
				1);
		l.setMatrix(0, l.getRowDimension() - 1, 1, l.getColumnDimension() - 1,
				input);
		l = l.times(w.transpose());
	}

	/**
	 * Calculates matrix P
	 */
	public void calculateP() {
		Matrix expL = createExpL();
		Matrix sumL = new Matrix(expL.getRowDimension(), 1);
		createSumL(expL, sumL);
		Matrix repmatL = new Matrix(sumL.getRowDimension(),
				2 * sumL.getColumnDimension());
		createRepmatL(repmatL, sumL);
		p = expL.arrayRightDivide(repmatL);
	}

	/**
	 * Creates matrix expL which element on position i, j is e^L(i, j)
	 * 
	 * @return expL matrix
	 */
	public Matrix createExpL() {
		Matrix expL = l.copy();
		for (int i = 0; i < expL.getRowDimension(); i++) {
			for (int j = 0; j < expL.getColumnDimension(); j++) {
				expL.set(i, j, Math.exp(expL.get(i, j)));
			}
		}
		return expL;
	}

	/**
	 * Creates matrix which elements are sums of elements in each row of matrix
	 * expL
	 * 
	 * @param expL
	 *            - expL matrix
	 * @param sumL
	 *            - sumL matrix
	 */
	public void createSumL(Matrix expL, Matrix sumL) {
		for (int i = 0; i < expL.getRowDimension(); i++) {
			double sum = 0;
			for (int j = 0; j < expL.getColumnDimension(); j++) {
				sum += expL.get(i, j);
			}
			sumL.set(i, 0, sum);
		}
	}

	/**
	 * Creates matrix which has matrix sumL twice in it, one next to the other
	 * one
	 * 
	 * @param repmatL
	 *            - repmatL matrix
	 * @param sumL
	 *            - sumL matrix
	 */
	public void createRepmatL(Matrix repmatL, Matrix sumL) {
		for (int i = 0; i < repmatL.getColumnDimension(); i++) {
			repmatL.setMatrix(0, repmatL.getRowDimension() - 1, i, i, sumL);
		}
	}
}