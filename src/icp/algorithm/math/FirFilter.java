package icp.algorithm.math;

import java.util.Arrays;

/**
 * Taken from http://ptolemy.eecs.berkeley.edu/eecs20/week12/implementation.html
 *
 * Impulse calculation and Bessel func taken from:
 * http://www.academia.edu/4919182/The_Io-sinh_function_calculation_of_Kaiser_windows_and_design_of_FIR_filters
 */
public class FirFilter implements IFilter {

    private final int length;
    private final double[] delayLine;
    private final double[] impulseResponse;
    private int count = 0;

    public FirFilter(int Fa, int Fb, int Fs, int M, int Att) {
        impulseResponse = calculateImpResponce(Fa, Fb, Fs, M, Att);
        length = impulseResponse.length;
        delayLine = new double[length];
    }
    
    public FirFilter() {
        this(0, 8, 1024, 19, 60);
    }
    
    public FirFilter(int Fa, int Fb, int Fs) {
        this(Fa, Fb, Fs, 19, 60);
    }

    @Override
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
    
    private double[] calculateImpResponce(int Fa, int Fb, int Fs, int M, int Att) {
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
