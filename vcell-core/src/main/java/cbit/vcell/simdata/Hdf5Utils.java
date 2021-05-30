package cbit.vcell.simdata;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import cbit.vcell.math.Variable;
import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import ncsa.hdf.hdf5lib.exceptions.HDF5LibraryException;

public class Hdf5Utils {
	private static final String HDF5_GROUP_SOLUTION = "/solution";
	private static final String HDF5_GROUP_EXTRAPOLATED_VOLUMES = "/extrapolated_volumes";
	private static final String HDF5_GROUP_DIRECTORY_SEPARATOR = "/";

	public static String getVarSolutionPath(String varName)
	{
		return HDF5_GROUP_SOLUTION + HDF5_GROUP_DIRECTORY_SEPARATOR + Variable.getNameFromCombinedIdentifier(varName); 
	}
	
	public static String getVolVarExtrapolatedValuesPath(String varName)
	{
		return HDF5_GROUP_EXTRAPOLATED_VOLUMES + HDF5_GROUP_DIRECTORY_SEPARATOR + "__" + Variable.getNameFromCombinedIdentifier(varName) + "_extrapolated__"; 
	}

	public static class HDF5WriteHelper {
		public int hdf5DataSpaceID;
		public int hdf5DatasetValuesID;
		public HDF5WriteHelper(int hdf5DataSpaceID, int hdf5DatasetValuesID) {
			super();
			this.hdf5DataSpaceID = hdf5DataSpaceID;
			this.hdf5DatasetValuesID = hdf5DatasetValuesID;
		}
		public void close() throws HDF5LibraryException {
			H5.H5Sclose(hdf5DataSpaceID);
			H5.H5Dclose(hdf5DatasetValuesID);
		}
	}
	public static Object writeHDF5Dataset(int hdf5GroupID,String dataspaceName,long[] dims,Object data,boolean bAttribute) throws NullPointerException, HDF5Exception {
		if(data instanceof Object[] && ((Object[])data).length == 0) {//Create dataset and return it, must be closed when finished
			//Create dataset
			long[] datasetDimensions = dims;
			int hdf5DataspaceIDValues = H5.H5Screate_simple(datasetDimensions.length, datasetDimensions, null);
			int hdf5DatasetIDValues = H5.H5Dcreate(hdf5GroupID, dataspaceName,HDF5Constants.H5T_NATIVE_DOUBLE, hdf5DataspaceIDValues,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
			return new HDF5WriteHelper(hdf5DataspaceIDValues,hdf5DatasetIDValues);
		}else if(data instanceof Object[] && ((Object[])data).length == 7 /*&& ((Object[])data)[0] instanceof Integer*/ && ((Object[])data)[0] instanceof double[] && ((Object[])data)[1] instanceof long[] && ((Object[])data)[2] instanceof long[] && ((Object[])data)[3] instanceof long[] && ((Object[])data)[4] instanceof long[] && ((Object[])data)[5] instanceof long[] && ((Object[])data)[6] instanceof Integer) {
			double[] copyFromData = (double[])((Object[])data)[0];
			long[] copyToStart = (long[])((Object[])data)[1];
			long[] copyToLength = (long[])((Object[])data)[2];
			long[] copyFromDims = (long[])((Object[])data)[3];
			long[] copyFromStart = (long[])((Object[])data)[4];
			long[] copyFromLength = (long[])((Object[])data)[5];
			int dataspaceID = (Integer)((Object[])data)[6];
			//int hdf5DataspaceIDValues = (Integer)((Object[])data)[2];
			
			int hdf5DataspaceIDSlice = H5.H5Screate_simple(copyFromDims.length, copyFromDims, null);
			//Select the generated sliceData to copy-from
			H5.H5Sselect_hyperslab(hdf5DataspaceIDSlice, HDF5Constants.H5S_SELECT_SET, copyFromStart, null, copyFromLength, null);


			//Select next section of destination to copy-to
			H5.H5Sselect_hyperslab(dataspaceID, HDF5Constants.H5S_SELECT_SET, copyToStart, null, copyToLength,null);
			//Copy from extracted sliceData to hdf5 file dataset
			H5.H5Dwrite_double(hdf5GroupID, HDF5Constants.H5T_NATIVE_DOUBLE, hdf5DataspaceIDSlice, dataspaceID, HDF5Constants.H5P_DEFAULT, copyFromData);
			H5.H5Sselect_none(dataspaceID);
			
			H5.H5Sclose(hdf5DataspaceIDSlice);
			return null;

		}else if(!bAttribute && dims == null && data == null) {//Create Group
			return H5.H5Gcreate(hdf5GroupID, (String)dataspaceName,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
		} else if(bAttribute && (data instanceof String || (data instanceof List && ((List<?>)data).get(0) instanceof String))) {
			String[] attr = null;
			if(data instanceof String) {
				attr = new String[] {(String)data};
			}else {
				attr = ((List<String>)data).toArray(new String[0]);
			}
			if(dims == null) {
				dims = new long[] {attr.length};
			}else if(dims.length != 1 && dims[0] != attr.length) {
				throw new IllegalArgumentException("dims not match length of String array");
			}
			StringBuffer sb = new StringBuffer();
			int MAXSTRSIZE=  -1;
			for(int i=0;i<attr.length;i++) {
				MAXSTRSIZE = Math.max(MAXSTRSIZE, attr[i].length());
			}
			for(int i=0;i<attr.length;i++) {
				sb.append(attr[i]);
				for(int j=0;j<(MAXSTRSIZE-attr[i].length());j++) {
					sb.append('\u0000');//null terminated string for hdf5 native code
				}
			}
			//https://support.hdfgroup.org/ftp/HDF5/examples/misc-examples/vlstra.c
			int h5attrcs1 = H5.H5Tcopy(HDF5Constants.H5T_C_S1);
			H5.H5Tset_size (h5attrcs1, MAXSTRSIZE/*HDF5Constants.H5T_VARIABLE*/);
			int dataspace_id = -1;
//			if(data instanceof String) {
//				dataspace_id = H5.H5Screate (HDF5Constants.H5S_SCALAR);
//			}else {
				dataspace_id = H5.H5Screate_simple(dims.length, dims,null);
//			}
			int attribute_id = H5.H5Acreate(hdf5GroupID, dataspaceName, h5attrcs1, dataspace_id, HDF5Constants.H5P_DEFAULT,HDF5Constants.H5P_DEFAULT);
			H5.H5Awrite (attribute_id, h5attrcs1, sb.toString().getBytes());
			H5.H5Sclose(dataspace_id);
			H5.H5Aclose(attribute_id);
			H5.H5Tclose(h5attrcs1);

//			H5.H5Tset_size(h5attrcs1, ((String)data).length());
//			int dataspace_id = H5.H5Screate (HDF5Constants.H5S_SCALAR);
//			int attribute_id = H5.H5Acreate (hdf5GroupID, dataspaceName, h5attrcs1, dataspace_id, HDF5Constants.H5P_DEFAULT,HDF5Constants.H5P_DEFAULT);
//			H5.H5Awrite (attribute_id, h5attrcs1, ((String)data).getBytes());
//			H5.H5Sclose(dataspace_id);
//			H5.H5Aclose(attribute_id);
//			H5.H5Tclose(h5attrcs1);
			return -1;
		}
		int hdf5DataspaceID = H5.H5Screate_simple(dims.length, dims, null);
		int hdf5DatasetID = -1;
		if(data instanceof ArrayList && ((ArrayList<?>)data).get(0) instanceof Double) {
			double[] hdfData = ArrayUtils.toPrimitive(((ArrayList<Double>)data).toArray(new Double[0]));
			hdf5DatasetID = H5.H5Dcreate(hdf5GroupID, dataspaceName,HDF5Constants.H5T_NATIVE_DOUBLE, hdf5DataspaceID,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
			H5.H5Dwrite_double(hdf5DatasetID, HDF5Constants.H5T_NATIVE_DOUBLE, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, hdfData);
		}else if(data instanceof double[]) {
			hdf5DatasetID = H5.H5Dcreate(hdf5GroupID, dataspaceName,HDF5Constants.H5T_NATIVE_DOUBLE, hdf5DataspaceID,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
			H5.H5Dwrite_double(hdf5DatasetID, HDF5Constants.H5T_NATIVE_DOUBLE, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, (double[])data);
		}else if(data instanceof int[]) {
			hdf5DatasetID = H5.H5Dcreate(hdf5GroupID, dataspaceName,HDF5Constants.H5T_NATIVE_INT, hdf5DataspaceID,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
			H5.H5Dwrite_int(hdf5DatasetID, HDF5Constants.H5T_NATIVE_INT, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, (int[])data);
		}else if(data instanceof ArrayList && ((ArrayList<?>)data).get(0) instanceof String) {
			int largestStrLen = 0;
			for(int i=0;i<((ArrayList<String>)data).size();i++) {
				largestStrLen = Math.max(largestStrLen, ((ArrayList<String>)data).get(i).length());
			}
			StringBuffer allStringSB = new StringBuffer();
			for(int i=0;i<((ArrayList<String>)data).size();i++) {
				allStringSB.append(StringUtils.rightPad(((ArrayList<String>)data).get(i), largestStrLen-((ArrayList<String>)data).get(i).length()));
			}
			int h5tcs1 = H5.H5Tcopy(HDF5Constants.H5T_C_S1);
			H5.H5Tset_size(h5tcs1, largestStrLen);
			hdf5DatasetID = H5.H5Dcreate(hdf5GroupID, dataspaceName,h5tcs1, hdf5DataspaceID,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
			final byte[] bytes = allStringSB.toString().getBytes();
			H5.H5Dwrite(hdf5DatasetID, h5tcs1, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, bytes);
			H5.H5Tclose(h5tcs1);
	
		}else {
			throw new IllegalArgumentException("Unexpected data object "+data.toString());
		}
		H5.H5Dclose(hdf5DatasetID);
		H5.H5Sclose(hdf5DataspaceID);
		return -1;
	}
}
