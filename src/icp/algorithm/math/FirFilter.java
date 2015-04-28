package icp.algorithm.math;

import java.util.Arrays;

/**
 * Taken from http://ptolemy.eecs.berkeley.edu/eecs20/week12/implementation.html
 *
 * Impulse calculation and Bessel function taken from:
 * http://www.academia.edu/4919182/The_Io-sinh_function_calculation_of_Kaiser_windows_and_design_of_FIR_filters
 */
public class FirFilter implements IFilter {
	/** Lenght of array which will store impulse response */
    private final int length;
    /** Array with stored delay line */
    private final double[] delayLine;
    /** Array with stored impulse response */
    private final double[] impulseResponse;
    /** Temporary storage for count */
    private int count = 0;
    
    /**
     * Constructor with all required parameters.
     * 
     * @param Fa Lower frequency
     * @param Fb Upper frequency
     * @param sampleRate Sampling rate of VisionRecorder
     * @param M Lenght of impulse response
     * @param Att Attenuation of VisionRecorder
     */
    public FirFilter(double Fa, double Fb, int sampleRate, int M, int Att) {
        impulseResponse = calculateImpResponce(Fa, Fb, sampleRate, M, Att);
        length = impulseResponse.length;
        delayLine = new double[length];
    }
    
    /**
     * Default constructor with default parameters.
     */
    public FirFilter() {
        this(0.1, 30, 1000, 19, 60);
    }
    
    /**
     * Simple constructor with parameters.
     * 
     * @param Fa Lower frequency
     * @param Fb Upper frequency
     * @param sampleRate Sampling rate of VisionRecorder
     */
    public FirFilter(int Fa, int Fb, int sampleRate) {
        this(Fa, Fb, sampleRate, 19, 60);
    }
    
    @Override
    /**
     * Method for filtering signal. Gets input sample and filters it
     * using calculated impulse response.
     * 
     * @param inputSample Input data
     */
    public double getOutputSample(double inputSample) {
        delayLine[count] = inputSample;
        double result = 0.0;
        int index = count;
        for (int i = 0; i < length; i++) {
            result += impulseResponse[i] * delayLine[index--];
            if (index < 0) {
                index = length - 1;
            }
        }
        if (++count >= length) {
            count = 0;
        }
        System.out.println("FIR: filtruju -> " + result);
        return result;
    }
    
    /**
     * Method for calculating impulse response of the filter.
     * 
     * @param Fa Lower frequency
     * @param Fb Upper frequency
     * @param sampleRate Sampling rate of VisionRecorder
     * @param M Lenght of impulse response
     * @param Att Attenuation of VisionRecorder
     * @return H Array with impulse response
     */
    private double[] calculateImpResponce(double Fa, double Fb, int Fs, int M, int Att) {
    	int Np = (M - 1)/2;
    	double[] H = new double[M];
    	double[] A = new double[Np + 1];
    	double pi = Math.PI, Alpha, I0Alpha;
    	
    	A[0] = 2.0*(Fb-Fa)/Fs;
		
		for(int j=1; j<=Np; j++) {
		  A[j] = (Math.sin(2*j*pi*Fb/Fs)-Math.sin(2*j*pi*Fa/Fs))/(j*pi);
		}
		
		if (Att<21) {
		  Alpha = 0;
		}
		else if (Att>50) {
		  Alpha = 0.1102*(Att-8.7);
		}
		else {
		  Alpha = 0.5842*Math.pow((Att-21), 0.4)+0.07886*(Att-21);
		}

		I0Alpha = BesselValue(Alpha);
		for (int j=0; j<=Np; j++) {
			double temp = Math.sqrt(1-(j*(double)j/(Np*Np)));
			H[Np+j] = A[j]*BesselValue(Alpha*temp)/I0Alpha;
		}
		for (int j=0; j<Np; j++) {
			H[j] = H[M-1-j];
		}
		
		System.out.println(Arrays.toString(H));
    	return H;
    }
    
    /**
     * Calculates zeroth order bessel function for x
     * @param x Given x for bessel function
     * @return result of Bessel for x
     */
    private double BesselValue(double x) {
		double d = 0, ds = 1, result = 1;

		do {
		    d += 2;
		    ds *= x*x/(d*d);
		    result += ds;
		}while (ds > result*0.000001);
		
		return result;
	}
}
