package icp.algorithm.math;

/**
 * Taken from http://ptolemy.eecs.berkeley.edu/eecs20/week12/implementation.html
 *
 * Impulse calculation and Bessel function taken from:
 * http://www.academia.edu/4919182/The_Io-sinh_function_calculation_of_Kaiser_windows_and_design_of_FIR_filters
 * @author Jan Vampol (upgrade to version 2.00)
 * @version 2.00
 */
public class FirFilter implements IFilter {
	/** Length of array which will store impulse response */
    private final int length;
    /** Array with stored delay line */
    private final double[] delayLine;
    /** Array with stored impulse response */
    private final double[] impulseResponse;
    /** Variable used as index for delayLine */
    private int count = 0;
    
    /**
     * Constructor calculates impulse response using {@link setupFilter} method. Then {@link delayLine} array is created
     * with same length as {@link impulseResponse} filled with zeroes. This array will be filled with values while
     * filter is running. These values will serve as values for next value. Magnitude of every value is
     * determined by impulse response.
     * 
     * @param Fa Lower frequency - must be lower than {@link Fb}
     * @param Fb Upper frequency - must be lower than {@link sampleRate}/2
     * @param sampleRate Sampling rate of VisionRecorder
     * @param M Lenght of impulse response
     * @param ripple expected attenuation of VisionRecorder
     */
    public FirFilter(double Fa, double Fb, int sampleRate, int M, double ripple) {
        impulseResponse = setupFilter(Fa, Fb, sampleRate, M, ripple);
        length = impulseResponse.length;
        delayLine = new double[length];
    }
    
    /**
     * Default constructor with recommended values.
     */
    public FirFilter() {
        this(0.1, 8, 1024, 30, 0);
    }
    
    /**
     * Method sets up filter so it is ready to use. Calculates impulse response.
     * 
     * @param Fa Lower frequency
     * @param Fb Upper frequency
     * @param sampleRate Sampling rate of VisionRecorder
     * @param M Length of impulse response
     * @param ripple Expected attenuation of VisionRecorder
     * @return double[] Impulse response
     */
    private double[] setupFilter(double Fa, double Fb, int sampleRate, int M, double ripple) {
    	return calculateImpulseResponse(Fa, Fb, sampleRate, M, ripple);
    }
      
    @Override
    /**
     * Method for filtering signal. Gets input sample and filters it
     * using calculated impulse response.
     * 
     * @param inputSample Input data
     * @return double Output data
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
        return result;
    }
    
    /**
     * Method for calculating impulse response of the filter. Calculation details can be found at:
     * http://www.academia.edu/4919182/The_Io-sinh_function_calculation_of_Kaiser_windows_and_design_of_FIR_filters
     * working example at:
     * http://www.arc.id.au/FilterDesign.html
     * @param Fa Lower frequency - must be lower than {@link Fb}
     * @param Fb Upper frequency - must be lower than {@link sampleRate}/2
     * @param Fs Sampling rate of VisionRecorder
     * @param M Length of impulse response
     * @param ripple Expected attenuation of VisionRecorder
     * @return H Array with impulse response
     */
    public static double[] calculateImpulseResponse(double Fa, double Fb, int Fs, int M, double ripple) {
    	int Np = (M - 1)/2;
    	double[] H = new double[M];
    	double[] A = new double[Np + 1];
    	double pi = Math.PI, Alpha, I0Alpha;
    	
    	A[0] = 2.0*(Fb-Fa)/Fs;
		
		for(int j=1; j<=Np; j++) {
		  A[j] = (Math.sin(2*j*pi*Fb/Fs)-Math.sin(2*j*pi*Fa/Fs))/(j*pi);
		}
		
		if (ripple<21) {
		  Alpha = 0;
		}
		else if (ripple>50) {
		  Alpha = 0.1102*(ripple-8.7);
		}
		else {
		  Alpha = 0.5842*Math.pow((ripple-21), 0.4)+0.07886*(ripple-21);
		}

		I0Alpha = BesselValue(Alpha);
		for (int j=0; j<=Np; j++) {
			double temp = Math.sqrt(1-(j*(double)j/(Np*Np)));
			H[Np+j] = A[j]*BesselValue(Alpha*temp)/I0Alpha;
		}
		for (int j=0; j<Np; j++) {
			H[j] = H[M-1-j];
		}
		
    	return H;
    }
    
    /**
     * Calculates zeroth order Bessel function for x
     * working example at:
     * http://www.arc.id.au/FilterDesign.html
     * 
     * @param x Given x for Bessel function
     * @return result of Bessel for x
     */
    private static double BesselValue(double x) {
		double d = 0, ds = 1, result = 1;

		do {
		    d += 2;
		    ds *= x*x/(d*d);
		    result += ds;
		}while (ds > result*0.000001);
		
		return result;
	}
}
