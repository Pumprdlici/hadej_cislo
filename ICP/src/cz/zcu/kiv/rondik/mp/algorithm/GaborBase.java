package cz.zcu.kiv.rondik.mp.algorithm;

public class GaborBase implements Base
{
	@Override
	public Atom getOptimalAtom(double[] signal)
	{
		GaborsAtom gaborAtom = null;
		double scale, position, positionDiferencial = 0;
		int n = signal.length;
		double a1 = 0, a = 0, b = 0, b1 = 0, frequency = 0, phase = 0, coef = 0, product = 0;
		double[] x = new double[n];
		double[] y = new double[n];
		Complex[] X = new Complex[n];
		Complex[] Y = new Complex[n];
		double FP = 0, FQ = 0, PP = 0, QQ = 0, PQ = 0;
		scale = n;
		while (scale > 1)
		{
			positionDiferencial = scale / 2;
			for (position = 0; position <= n; position += positionDiferencial)
			{
				for (int j = 0; j < n; j++)
				{
					double g = Utils.gaussianWindow((j - position) / scale);
					x[j] = signal[j] * g;
					y[j] = g * g;
				}
				X = Utils.doFFT(x, false);
				Y = Utils.doFFT(y, false);
				for (int k = 0; k < n / 2; k++)
				{
					// computing product
					FP = X[k].getRe();
					FQ = -X[k].getIm();
					double c = Y[0].getRe();
					if (k <= n / 2 - 1)
					{
						PP = (c + Y[2 * k].getRe()) / 2;
						QQ = (c - Y[2 * k].getRe()) / 2;
						PQ = -Y[2 * k].getIm() / 2;
					}
					else
					{
						PP = (c + Y[2 * k - n].getRe()) / 2;
						QQ = (c - Y[2 * k - n].getRe()) / 2;
						PQ = -Y[2 * k - n].getIm() / 2;
					}
					frequency = 2 * Math.PI * k / n;
					a = FP;
					b = FQ;
					a1 = a * QQ - b * PQ;
					// calculation of parameter a2
					b1 = b * PP - a * PQ;
					// if frequency of atom is 0
					if (frequency == 0)
					{
						phase = 0;
						product = a / Math.sqrt(PP);
					}
					// if a1 parameter is 0
					else if (a1 < Complex.ZERO_ALLOWANCE)
					{
						phase = Math.PI / 2;
						product = -b / Math.sqrt(QQ);
					}
					// in all other cases
					else
					{
						phase = Math.atan(-b1 / a1);
						product = (a * a1 + b * b1)
								/ Math.sqrt(Math.pow(a1, 2) * PP + Math.pow(b1, 2) * QQ + 2
										* a1 * b1 * PQ);
					}
					
					// if this atom has better characteristics than previous
					if (product >= coef && product != Double.POSITIVE_INFINITY)
					{
						coef = product;
						gaborAtom = new GaborsAtom(scale, position, frequency, phase);
						gaborAtom.setModulus(coef);
					}
				}
			}
			scale = scale / 2;
		}
		
		double difference = 0;
		double[] valuesOfAtom = gaborAtom.getValues(signal.length);
		for (int oi = 0; oi < signal.length; oi++)
		{
			difference += Math.abs(signal[oi] - valuesOfAtom[oi]);
		}
		
		gaborAtom.setDifference(difference);
		
		return gaborAtom;
	}
}
