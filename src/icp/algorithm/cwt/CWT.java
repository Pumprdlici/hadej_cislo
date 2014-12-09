package icp.algorithm.cwt;

import icp.algorithm.cwt.wavelets.WaveletCWT;
import icp.application.ProgressInterface;

/**
 * Tøída spojité waveletové transfromace.
 */
public class CWT
{
	//parametry mìøítka waveletu
	private double minScale;
	private double maxScale;
	private double stepScale;
	
	//typ waveletu
	private WaveletCWT wavelet;
	
	//pole realných koeficientù vypoètených cwt
	private double[][] cwtDataReal;	
	//pole imaginárních koeficientù vypoètených cwt
	private double[][] cwtDataImag;
	//pole modulus koeficientù komplexní cwt
	private double[][] modulusCwtData;	
	//pole angle koeficientù komplexní cwt
	private double[][] angleCwtData;
	
	//pole hodnot nejvyšších koeficientù
	private double[] highestCoeficients;	
	//pole indexù nejvyšších koeficientù
	private int[] indexesHighestCoeficients;
	
	private ProgressInterface progress;
	private boolean transformEnabled;
	
	/**
	 * Konstruktor CWT_algotithm.
	 * 
	 * @param minScale - spodní hranice mìøítka.
	 * @param maxScale - horní hranice mìøítka.
	 * @param stepScale - krok mìøítka.
	 * @param wavelet - mateøský wavelet.
	 */
	public CWT(double minScale, double maxScale, double stepScale, WaveletCWT wavelet)
	{
		this.maxScale = maxScale;
		this.minScale = minScale;
		this.stepScale = stepScale;
		this.wavelet = wavelet;
		transformEnabled = true;
	}
	
	/**
	 * Spojitá waveletová transformace.
	 * 
	 * @param inputSignal - vstupní signál.
	 */
	public void transform(double[] inputSignal)
	{
		cwtDataReal = new double[(int) ((maxScale - minScale)/stepScale) + 1][inputSignal.length];
		cwtDataImag = new double[cwtDataReal.length][inputSignal.length];
		modulusCwtData = new double[cwtDataReal.length][inputSignal.length];
		angleCwtData = new double[cwtDataReal.length][inputSignal.length];
		highestCoeficients = new double[cwtDataReal.length];
		indexesHighestCoeficients = new int[cwtDataReal.length];
		int b, x, scale = 0;
		double t, a;
		double highestCoef; 
		int indexHighestCoef;
		
		
		for (a = minScale; a <= maxScale; a += stepScale)
		{				
			highestCoef = 0; 
			indexHighestCoef = 0;

			if(!transformEnabled)
				return;
			
			for (b = 0; b < inputSignal.length; b++)
			{
				cwtDataReal[scale][b] = 0;
				cwtDataImag[scale][b] = 0;
				
				for (x = 0; x < inputSignal.length; x++)
				{						
					t = (x - b)/a;
					
					cwtDataReal[scale][b] += inputSignal[x] * wavelet.reCoef(t, a);
					cwtDataImag[scale][b] += inputSignal[x] * wavelet.imCoef(t, a);
				}		
				
				if(highestCoef < Math.abs(cwtDataReal[scale][b]))
				{
					highestCoef = Math.abs(cwtDataReal[scale][b]);
					indexHighestCoef = b;
				}
				
				modulusCwtData[scale][b] = Math.sqrt(Math.pow(cwtDataReal[scale][b], 2.0)
											+ Math.pow(cwtDataImag[scale][b], 2.0));
				angleCwtData[scale][b] = Math.atan2(cwtDataImag[scale][b],cwtDataReal[scale][b]);
			}	
			
			highestCoeficients[scale] = highestCoef;
			indexesHighestCoeficients[scale] = indexHighestCoef;
			
			progress.sendProgressUnits();
			scale++;
		}
	}
	
	
	/**
	 * Nastavení spodní hranice mìøítka.
	 * 
	 * @param minScale - spodní hranice mìøítka.
	 */
	public void setMinScale(double minScale)
	{
		this.minScale = minScale;
	}
	
	/**
	 * Nastavení horní hranice mìøítka.
	 * 
	 * @param maxScale - horní hranice mìøítka.
	 */
	public void setMaxScale(double maxScale)
	{
		this.maxScale = maxScale;
	}
	
	/**
	 * Nastavení kroku mìøítka.
	 * 
	 * @param stepScale - krok mìøítka.
	 */
	public void setStepScale(double stepScale)
	{
		this.stepScale = stepScale;
	}
	
	/**
	 * Nastavení waveletu.
	 * 
	 * @param wavelet - mateøský wavelet.
	 */
	public void setWavelet(WaveletCWT wavelet)
	{
		this.wavelet = wavelet;
	}
	
	/**
	 * Nastavení parametrù pro cwt.
	 * 
	 * @param minScale - spodní hranice mìøítka.
	 * @param maxScale - horní hranice mìøítka.
	 * @param stepScale - krok mìøítka.
	 * @param wavelet - wavelet.
	 */
	public void setCwtParameters(double minScale, double maxScale, double stepScale, WaveletCWT wavelet)
	{
		this.maxScale = maxScale;
		this.minScale = minScale;
		this.stepScale = stepScale;
		this.wavelet = wavelet;
	}
	
	/**
	 * @return spodní hranice mìøítka.
	 */
	public double getMinScale()
	{
		return minScale;
	}
	
	/**
	 * @return spodní hranice mìøítka.
	 */
	public double getMaxScale()
	{
		return maxScale;
	}
	
	/**
	 * @return krok mìøítka.
	 */
	public double getStepScale()
	{
		return stepScale;
	}
	
	/**
	 * @return mateøský wavelet.
	 */
	public WaveletCWT getWavelet()
	{
		return wavelet;
	}
	
	/**
	 * @return pole reálných koeficientù vypoètených cwt.
	 */
	public double[][] getCwtDataReal()
	{
		return cwtDataReal;
	}
	
	/**
	 * @return pole imaginárních koeficientù vypoètených cwt.
	 */
	public double[][] getCwtDataImag()
	{		
		return cwtDataImag;
	}
	
	/**
	 * @return pole modulus koeficientù vypoètených komplexní cwt.
	 */
	public double[][] getModulusCwtData()
	{		
		return modulusCwtData;
	}
	
	/**
	 * @return pole angle koeficientù vypoètených komplexní cwt.
	 */
	public double[][] getAngleCwtData()
	{
		return angleCwtData;
	}
	
	/**
	 * Nastavuje objekt transformation.
         * @param progress
	 */
	public void setProgressInterface(ProgressInterface progress)
	{
		this.progress = progress;
	}
	
	/**
	 * @return pole hodnot nejvyšších keoficientù.
	 */
	public double[] getHighestCoeficients()
	{
		return highestCoeficients;
	}
	
	/**
	 * @return pole indexù nejvyšších keoficientù.
	 */
	public int[] getIndexesHighestCoeficients()
	{
		return indexesHighestCoeficients;
	}
	
	/**
        * @param enabled
	 */
	public void setTransform(boolean enabled)
	{
		transformEnabled = enabled;
	}
}
