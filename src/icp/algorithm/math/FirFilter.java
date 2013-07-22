package icp.algorithm.math;


/**
 * Taken from http://ptolemy.eecs.berkeley.edu/eecs20/week12/implementation.html
 * 
 *
 */
public class FirFilter implements IFilter {
    private int length;
    private double[] delayLine;
    private double[] impulseResponse;
    private int count = 0;

    public FirFilter(double[] coefs) {
		length = coefs.length;
        impulseResponse = coefs;
        delayLine = new double[length];
    }

    public double getOutputSample(double inputSample) {
        delayLine[count] = inputSample;
        double result = 0.0;
        int index = count;
        for (int i=0; i<length; i++) {
            result += impulseResponse[i] * delayLine[index--];
            if (index < 0) index = length-1;
        }
        if (++count >= length) count = 0;
        return result;
    }

}


