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

import Jama.Matrix;

public class LinearDiscriminantAnalysisAlgorithms {

	/**
	 * Discovered linear coefficients
	 */
	private Matrix w;
	/**
	 * 
	 */
	private Matrix l;
	/**
	 * 
	 */
	private Matrix p;
	/**
	 * Training data matrix
	 */
	private Matrix input;
	/**
	 * Training targets vector
	 */
	private Matrix targets;
	/**
	 * Size of training data matrix
	 */
	private int[] sizeOfInput;
	/**
	 * Unique target classes
	 */
	private double[] classes;
	/**
	 * Number of unique target classes
	 */
	private int classCount;
	/**
	 * Group counts
	 */
	private Matrix nGroup;
	/**
	 * Group sample means
	 */
	private Matrix groupMean;
	/**
	 * Pooled covariance
	 */
	private Matrix pooledCovariance;
	/**
	 * Prior probabilities
	 */
	private Matrix priorProb;

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
		createInputAndTargetMatrix(input, targets, fe);
		sizeOfInput = determineSizeOfInput();
		classes = determineClasses();
		classCount = determineClassCount();
		init();
		calculateW();
	}

	public double classify(double[] featureVector) {
		double[][] feature = new double[1][featureVector.length];
		feature[0] = featureVector;
		input = new Matrix(feature);
		calculateL();
		calculateP();
		System.out.println(p.get(0, 1));
		return p.get(0, 1);
	}

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
					if (numbers[j].equals("-∞")) {
						matrix[currRow][currColumn] = -Double.MAX_VALUE;
					} else if (numbers[j].equals("∞")) {
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
	private void init() {
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
	private int[] determineSizeOfInput() {
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
	private void createInputAndTargetMatrix(List<double[][]> input,
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
	private double[] determineClasses() {
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
	private int determineClassCount() {
		return classes.length;
	}

	/**
	 * Calculates matrix W
	 */
	private void calculateW() {
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

			// For each column, calculate covariance
			Matrix cov = new Matrix(sizeOfInput[1], sizeOfInput[1]);
			for (int j = 0, k = 0; j < cov.getRowDimension(); j++, k++) {
				cov.set(j, k, calculateCovariance(group, i, k));
			}

			// For each column, accumulate pooled covariance
			for (int j = 0; j < pooledCovariance.getColumnDimension(); j++) {
				pooledCovariance = pooledCovariance.plus(cov.times((nGroup.get(
						i, 0) - 1) / (sizeOfInput[0] - classCount)));
			}
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
	private double calculateMean(Matrix group, int i, int j) {
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
	 * Calculates covariance of column
	 * 
	 * @param group
	 *            - vector, that has 1 where index match class
	 * @param i
	 *            - currently processed class
	 * @param j
	 *            - currently processed column of matrix GroupMean
	 * @return covariance of vector
	 */
	private double calculateCovariance(Matrix group, int i, int j) {
		double cov = 0;
		int count = 0;
		for (int k = 0; k < input.getRowDimension(); k++) {
			if (group.get(k, 0) == 1) {
				double temp = input.get(k, j) - groupMean.get(i, j);
				cov += (temp * temp);
				count++;
			}
		}
		cov /= (count - 1);
		return cov;
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
	private Matrix calculateGroup(Matrix group, int i) {
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
	private void calculateNGroup(Matrix group, int i) {
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
	private void createW(int i) {
		Matrix temp2 = new Matrix(1, groupMean.getColumnDimension());
		temp2 = multiplyVectorWithMatrix(groupMean.getMatrix(i, i, 0,
				groupMean.getColumnDimension() - 1), pooledCovariance.inverse());
		Matrix temp3 = temp2.copy();
		temp3.timesEquals(-0.5);
		temp3.arrayTimesEquals(groupMean.getMatrix(i, i, 0,
				groupMean.getColumnDimension() - 1));
		w.set(i, 0, temp3.get(0, 0) + Math.log(priorProb.get(i, 0)));
		temp2.print(3, 21);
		temp3.print(3, 21);
		w.setMatrix(i, i, 1, w.getColumnDimension() - 1, temp2);
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
	private Matrix multiplyVectorWithMatrix(Matrix v, Matrix m) {
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
	 * Calculates matrix L
	 */
	private void calculateL() {
		l = new Matrix(input.getRowDimension(), input.getColumnDimension() + 1,
				1);
		l.setMatrix(0, l.getRowDimension() - 1, 1, l.getColumnDimension() - 1,
				input);
		l = l.times(w.transpose());
	}

	/**
	 * Calculates matrix P
	 */
	private void calculateP() {
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
	private Matrix createExpL() {
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
	private void createSumL(Matrix expL, Matrix sumL) {
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
	private void createRepmatL(Matrix repmatL, Matrix sumL) {
		for (int i = 0; i < repmatL.getColumnDimension(); i++) {
			repmatL.setMatrix(0, repmatL.getRowDimension() - 1, i, i, sumL);
		}
	}
}