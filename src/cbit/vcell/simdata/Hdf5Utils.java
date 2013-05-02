package cbit.vcell.simdata;

public class Hdf5Utils {
	private static final String HDF5_GROUP_SOLUTION = "/solution";
	private static final String HDF5_GROUP_DIRECTORY_SEPARATOR = "/";

	public static String getVarSolutionPath(String varName)
	{
		return HDF5_GROUP_SOLUTION + HDF5_GROUP_DIRECTORY_SEPARATOR +  varName; 
	}
}
