package cz.zcu.kiv.rondik.mp.algorithm;

public class ConvolutionAtom extends UsersAtom
{
	@Override
	public String toString()
	{
		return name + " - position: " + position + ", scale: " + scale + ", length: " + stretch + ", modulus: " + difference;
	}
}
