package icp.algorithm.mp;

public class DetectionAtom extends UsersAtom
{
	@Override
	public String toString()
	{
		return name + " - position: " + position + ", scale: " + scale + ", length: " + stretch + ", modulus: " + difference;
	}
}
