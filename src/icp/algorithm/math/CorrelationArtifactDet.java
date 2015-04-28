package icp.algorithm.math;

import java.io.*;
import java.util.*;

import icp.online.app.EpochMessenger;

/**
 * Class for removal of artifacts in EEG signal using the 
 * Pearson's correlation method. The EEG signal is correlated 
 * to a pattern of e.g. eye artifact. If the 
 * correlation coefficient is higher than the threshold,
 * the epoch is removed from the signal.
 * 
 * @author Michal Veverka
 * @version 1.00
 */
public class CorrelationArtifactDet implements IArtifactDetection{
	
	public static final double DEFAULT_THRESHOLD = 0.86;
	private static final String EYE_ARTIFACT_FILE = "data/blink.txt";
	private static final String DEFAULT_DELIMITER = " ";
	
	/**
	 * Pattern used for correlation.
	 */
	private double[] pattern;
	
	/**
	 * The maximum value of correlation coefficient allowed.
	 */
	public double threshold;
	
	/**
	 * Creates an instance of CorrelationArtifactDet with threshold set to {@link DEFAULT_THRESHOLD}.
	 * The default pattern of eye blink is used.
	 */
	public CorrelationArtifactDet(){
		this.threshold = DEFAULT_THRESHOLD;
	}
	
	/**
	 * Creates an instance of CorrelationArtifactDet with the threshold given 
	 * as parameter.
	 * 
	 * @param threshold Threshold of correlation coefficient.
	 */
	public CorrelationArtifactDet(double threshold){
		this.setThreshold(threshold);
	}
	
	/**
	 * Creates an instance of CorrelationArtifactDet with the threshold given 
	 * as parameter.
	 * 
	 * @param pattern Pattern that is used for correlation.
	 * @param threshold Maximal correlation coefficient.
	 */
	public CorrelationArtifactDet(double[] pattern, double threshold){
		this.setThreshold(threshold);
		this.pattern = pattern;
	}
	
	/**
	 * Generates gaussian curve and returns its values as an array of double. 
	 * The gaussian function is given as: y = a*e^(-((x-mu)^2)/(2*sigma^2).
	 * Only the "hill" of the curve is generated. That is done by computing the width of 
	 * the curve, and then converting that width into nPoints.
	 * 
	 * @param nPoints The width of the curve (size of array).
	 * @param a The maximal value of the curve.
	 * @param mu The padding of the curve on the x-axis.
	 * @param sigma The width of the "hill" of the curve.
	 * @return Array of double values representing the "hill" of the gaussian curve.
	 */
	public double[] generateGaussianPattern(int nPoints, double a, double mu, double sigma){
		double[] curve = new double[nPoints];
		double width = 2*Math.sqrt(2*Math.log(10))*sigma;
		width*=1.1;
		for(int i = 0; i < nPoints; i++){
			double x = width*((double)i/(double)nPoints) - width/2;
			curve[i] = Math.pow(a*Math.E,-(x-mu)*(x-mu)/(2*sigma*sigma));
		}
		return curve;
	}
	
	/**
	 * Reads pattern for correlation from a file with name given as parameter fileName. 
	 * The values in the file must be separated by the given delimiter.
	 * 
	 * @param fileName Name of the file.
	 * @param delimiter Delimiter seperating the values.
	 * @return Array of doubles representing the pattern.
	 */
	public static double[] readPatternFromFile(String fileName, String delimiter){
		ArrayList<Double> patternList = new ArrayList<Double>();
		try {
			BufferedReader bfr = new BufferedReader(new FileReader(new File(fileName)));
			String line;
			try {
				while((line = bfr.readLine()) != null){
					String[] hodnoty = line.trim().split(delimiter);
					for(int i = 0; i<hodnoty.length; i++){
						try {
							double a = Double.parseDouble(hodnoty[i]);
							patternList.add(a);
						} catch(NumberFormatException e){
							e.printStackTrace();
							bfr.close();
							return null;
						}
					}
				}
				bfr.close();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		double[] pattern = new double[patternList.size()];
		for(int i = 0; i < pattern.length; i++){
			pattern[i] = patternList.get(i);
		}
		return pattern;
	}
	
	/**
	 * Shifts the pattern against the epoch and with each shift calculates the correlation 
	 * coefficient. If the correlation coefficient is higher than the threshold, than the part of 
	 * the epoch is similar to the pattern (contains artifact) and the epoch is removed.
	 * The correlation coefficient can have the values from -1 (not similar at all) to 1 
	 * (exactly the same) and is calculated using the Pearson's correlation equation: 
	 * r = (n*sum(xi*yi)-sum(xi)*sum(yi))/(sqrt(n*sum(xi*xi)-sum(xi)*sum(xi))*
	 * 	   sqrt(n*sum(yi*yi)-sum(yi)*sum(yi))
	 * 
	 * @param epochMes Epoch to be correlated with pattern.
	 * @return epochMes Returns the correlated epoch, unless it contains artifact. In that 
	 * case returns null.
	 */
	public EpochMessenger detectArtifact(EpochMessenger epochMes){
		epochMes = detectArtifact(epochMes, this.pattern);
		return epochMes;
	}
	
	/**
	 * Shifts the pattern against the epoch and with each shift calculates the correlation 
	 * coefficient. If the correlation coefficient is lower than the threshold, then that part of 
	 * the epoch is similar to the pattern (contains artifact) and the epoch is removed.
	 * The correlation coefficient can have the values from -1 (not similar at all) to 1 
	 * (exactly the same) and is calculated using the Pearson's correlation equation: 
	 * r = (n*sum(xi*yi)-sum(xi)*sum(yi))/(sqrt(n*sum(xi*xi)-sum(xi)*sum(xi))*
	 * 	   sqrt(n*sum(yi*yi)-sum(yi)*sum(yi))
	 * 
	 * @param epochMes Epoch to be correlated with pattern.
	 * @param pattern Pattern to be correlated with epoch.
	 * 
	 * @return epochMes Returns the correlated epoch, unless it contains artifact. In that 
	 * case returns null.
	 */
	public EpochMessenger detectArtifact(EpochMessenger epochMes, double[] pattern){
		if(pattern == null)return epochMes;
		
		double[][] epoch = epochMes.getEpoch();
		double n = pattern.length;
		for(int channel = 0; channel<epoch.length; channel++){
			double sumXY = 0;
			double sumX = 0;
			double sumY = 0;
			double sumXSqr = 0;
			double sumYSqr = 0;
			for(int pointE = 0; pointE<=epoch[0].length-n; pointE++){
				for(int pointP = 0; pointP<n; pointP++){
					sumXY += epoch[channel][pointE+pointP] * pattern[pointP];
					sumX += epoch[channel][pointE+pointP];
					sumY += pattern[pointP];
					sumXSqr += epoch[channel][pointE+pointP]*epoch[channel][pointE+pointP];
					sumYSqr += pattern[pointP]*pattern[pointP];
				}
				double corrCoef = (n*sumXY - sumX*sumY) / 
						(Math.sqrt(n*sumXSqr-sumX*sumX) * 
						Math.sqrt(n*sumYSqr-sumY*sumY));
				
				if(corrCoef > this.threshold)
				{
					return null;
				}
					
				sumXY = 0;
				sumX = 0;
				sumY = 0;
				sumXSqr = 0;
				sumYSqr = 0;
			}
		}
		return epochMes;
	}
	
	/**
	 * Returns the threshold (maximum difference between average of epoch values and 
	 * one point in epoch).
	 * 
	 * @return threshold The threshold (maximal correlation coefficient).
	 */
	public double getThreshold(){
		return this.threshold;
	}
	
	/**
	 * Sets the threshold (maximal correlation coefficient). If the given parameter 
	 * is lower than -1 or higher than 1, the threshold 
	 * is set to default value of {@link #DEFAULT_THRESHOLD}.
	 * 
	 * @param threshold Maximal correlation coefficient.
	 */
	public void setThreshold(double threshold){
		if(Math.abs(threshold) > 1)
			this.threshold = DEFAULT_THRESHOLD;
		else
			this.threshold = threshold;
	}
	
	public double[] getPattern(){
		return this.pattern;
	}
	
	public void setPattern(double[] pattern){
		this.pattern = pattern;
	}
}
