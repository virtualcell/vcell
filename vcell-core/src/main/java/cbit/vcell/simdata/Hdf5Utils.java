package cbit.vcell.simdata;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
	public static HDF5WriteHelper createDataset(int hdf5GroupID,String dataspaceName,long[] dims) throws HDF5Exception{
		//Create dataset and return it, must be closed when finished
		long[] datasetDimensions = dims;
		int hdf5DataspaceIDValues = H5.H5Screate_simple(datasetDimensions.length, datasetDimensions, null);
		int hdf5DatasetIDValues = H5.H5Dcreate(hdf5GroupID, dataspaceName,HDF5Constants.H5T_NATIVE_DOUBLE, hdf5DataspaceIDValues,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
		return new HDF5WriteHelper(hdf5DataspaceIDValues,hdf5DatasetIDValues);
	}
	public static int createGroup(int hdf5GroupID,String dataspaceName) throws HDF5Exception{
		return H5.H5Gcreate(hdf5GroupID, (String)dataspaceName,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
	}
	public static void copySlice(int copyToDataSet,double[] copyFromData,long[] copyToStart,long[] copyToLength,long[] copyFromDims,long[] copyFromStart,long[] copyFromLength,int dataspaceID) throws NullPointerException, IllegalArgumentException, HDF5Exception {
		int hdf5DataspaceIDSlice = H5.H5Screate_simple(copyFromDims.length, copyFromDims, null);
		//Select the generated sliceData to copy-from
		H5.H5Sselect_hyperslab(hdf5DataspaceIDSlice, HDF5Constants.H5S_SELECT_SET, copyFromStart, null, copyFromLength, null);
		//Select next section of destination to copy-to
		H5.H5Sselect_hyperslab(dataspaceID, HDF5Constants.H5S_SELECT_SET, copyToStart, null, copyToLength,null);
		//Copy from extracted sliceData to hdf5 file dataset
		H5.H5Dwrite_double(copyToDataSet, HDF5Constants.H5T_NATIVE_DOUBLE, hdf5DataspaceIDSlice, dataspaceID, HDF5Constants.H5P_DEFAULT, copyFromData);
		H5.H5Sselect_none(dataspaceID);
		H5.H5Sclose(hdf5DataspaceIDSlice);
	}
	public static void insertAttribute(int hdf5GroupID,String dataspaceName,String data) throws NullPointerException, HDF5Exception {
		insertAttributes(hdf5GroupID, dataspaceName, new ArrayList<String>(Arrays.asList(new String[] {data})));
	}
	public static void insertAttributes(int hdf5GroupID,String dataspaceName,List<String> data) throws NullPointerException, HDF5Exception {
		String[] attr = ((List<String>)data).toArray(new String[0]);
		long[] dims = new long[] {attr.length};
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
		dataspace_id = H5.H5Screate_simple(dims.length, dims,null);
		int attribute_id = H5.H5Acreate(hdf5GroupID, dataspaceName, h5attrcs1, dataspace_id, HDF5Constants.H5P_DEFAULT,HDF5Constants.H5P_DEFAULT);
		H5.H5Awrite (attribute_id, h5attrcs1, sb.toString().getBytes());
		H5.H5Sclose(dataspace_id);
		H5.H5Aclose(attribute_id);
		H5.H5Tclose(h5attrcs1);
	}
//	public static void insertString(int hdf5GroupID,String dataspaceName,long[] dims,String data) throws NullPointerException, HDF5Exception {
//		insertStrings(hdf5GroupID, dataspaceName, dims,new ArrayList<String>(Arrays.asList(new String[] {data})));
//	}
	public static void insertStrings(int hdf5GroupID,String dataspaceName,long[] dims,List<String> data) throws NullPointerException, HDF5Exception {
		int largestStrLen = 0;
		for(int i=0;i<data.size();i++) {
			largestStrLen = Math.max(largestStrLen, data.get(i).length());
		}
		byte[] bytes = new byte[largestStrLen*data.size()];
		int index = 0;
		for(int i=0;i<data.size();i++) {
			System.arraycopy(data.get(i).getBytes(), 0, bytes, index, data.get(i).length());
			index+= largestStrLen;
		}
		int h5tcs1 = H5.H5Tcopy(HDF5Constants.H5T_C_S1);
		H5.H5Tset_size(h5tcs1, largestStrLen);
		int hdf5DataspaceID = H5.H5Screate_simple(dims.length, dims, null);
		int hdf5DatasetID = H5.H5Dcreate(hdf5GroupID, dataspaceName,h5tcs1, hdf5DataspaceID,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
		//final byte[] bytes = allStringSB.toString().getBytes();
		H5.H5Dwrite(hdf5DatasetID, h5tcs1, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, bytes);
		H5.H5Tclose(h5tcs1);
		H5.H5Dclose(hdf5DatasetID);
		H5.H5Sclose(hdf5DataspaceID);
	}
	public static void insertInts(int hdf5GroupID,String dataspaceName,long[] dims,int[] data) throws NullPointerException, HDF5Exception {
		int hdf5DataspaceID = H5.H5Screate_simple(dims.length, dims, null);
		int hdf5DatasetID = H5.H5Dcreate(hdf5GroupID, dataspaceName,HDF5Constants.H5T_NATIVE_INT, hdf5DataspaceID,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
		H5.H5Dwrite_int(hdf5DatasetID, HDF5Constants.H5T_NATIVE_INT, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, (int[])data);
		H5.H5Dclose(hdf5DatasetID);
		H5.H5Sclose(hdf5DataspaceID);
	}
	public static void insertDoubles(int hdf5GroupID,String dataspaceName,long[] dims,double[] data) throws NullPointerException, HDF5Exception {
		int hdf5DataspaceID = H5.H5Screate_simple(dims.length, dims, null);
		int hdf5DatasetID = H5.H5Dcreate(hdf5GroupID, dataspaceName,HDF5Constants.H5T_NATIVE_DOUBLE, hdf5DataspaceID,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
		H5.H5Dwrite_double(hdf5DatasetID, HDF5Constants.H5T_NATIVE_DOUBLE, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, (double[])data);
		H5.H5Dclose(hdf5DatasetID);
		H5.H5Sclose(hdf5DataspaceID);
	}
	public static void insertDoubles(int hdf5GroupID,String dataspaceName,long[] dims,ArrayList<Double> data) throws NullPointerException, HDF5Exception {
		double[] hdfData = ArrayUtils.toPrimitive(((ArrayList<Double>)data).toArray(new Double[0]));
		int hdf5DataspaceID = H5.H5Screate_simple(dims.length, dims, null);
		int hdf5DatasetID = H5.H5Dcreate(hdf5GroupID, dataspaceName,HDF5Constants.H5T_NATIVE_DOUBLE, hdf5DataspaceID,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
		H5.H5Dwrite_double(hdf5DatasetID, HDF5Constants.H5T_NATIVE_DOUBLE, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, hdfData);
		H5.H5Dclose(hdf5DatasetID);
		H5.H5Sclose(hdf5DataspaceID);
	}
	
	// exercising some hdf5 features
    public static void main(String[] args) {
    	
    	int numColumns = 6;
    	int numRows = 5;
    	
    	int numJobs = 2;
    	int selectedColCount = 3;	// # of variables per job
    	int actualLength = numRows;
    	String[] paramScanParamNames = { "Kf" };
    	
    	String[] names = { "t", "s0", "s1" };
    	
    	double[] t_set0 = { 0, 1, 2, 3, 4 };
    	double[] s0_set0 = { 1.1, 1.2, 1.3, 1.4, 1.5 };
    	double[] s1_set0 = { 2.1, 2.2, 2.3, 2.4, 2.5 };
    	
    	double[] t_set1 = { 0, 1, 2, 3, 4 };
    	double[] s0_set1 = { 3.1, 3.2, 3.3, 3.4, 3.5 };
    	double[] s1_set1 = { 4.1, 4.2, 4.3, 4.4, 4.5 };
    			
		int hdf5FileID = -1;
		File hdf5TempFile = null;

    	try {
    		hdf5TempFile = File.createTempFile("plot2D", ".h5");
			hdf5FileID = H5.H5Fcreate(hdf5TempFile.getAbsolutePath(), HDF5Constants.H5F_ACC_TRUNC, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);				
			
			for(int k=0; k<numJobs; k++) {
				int jobGroupID = -1;
				
				Hdf5Utils.HDF5WriteHelper help0 = null;
				ArrayList<String> dataTypes = new ArrayList<String>();
				ArrayList<String> dataIDs = new ArrayList<String>();
				ArrayList<String> dataNames = new ArrayList<String>();
				ArrayList<String> paramNames = new ArrayList<String>();
				ArrayList<String> paramValues = new ArrayList<String>();

				for(int col = 0; col<3; col++) {
					dataTypes.add("float64");
					dataIDs.add("data_set_" + names[col]);
					dataNames.add(names[col]);
				}
				for(int z=0; z < paramScanParamNames.length; z++) {
					paramNames.add(paramScanParamNames[z]);
					paramValues.add(paramScanParamNames[z]);
				}
				jobGroupID = (int) Hdf5Utils.createGroup(hdf5FileID, "Set "+ k);
				help0 = Hdf5Utils.createDataset(jobGroupID, "data", new long[] {selectedColCount, actualLength});	// long[] dimensions
				
				double[] fromData2 = new double[actualLength*selectedColCount];
				
				int datasetValuesId = help0.hdf5DatasetValuesID;
				long[] copyToStart = new long[] { 0, 0 };
				long[] copyToLength = new long[] { selectedColCount, actualLength };
				long[] copyFromDims = new long[] { selectedColCount, actualLength };
				long[] copyFromStart = new long[] { 0,0 };
				long[] copyFromLength = new long[] { selectedColCount, actualLength };
				Hdf5Utils.copySlice(datasetValuesId, fromData2, copyToStart, copyToLength, copyFromDims, copyFromStart, copyFromLength, help0.hdf5DataSpaceID);

				Hdf5Utils.insertAttribute(help0.hdf5DatasetValuesID, "_type", "ODE Data Export");
				Hdf5Utils.insertAttributes(help0.hdf5DatasetValuesID,"dataSetDataTypes", dataTypes);
				Hdf5Utils.insertAttributes(help0.hdf5DatasetValuesID,"dataSetIds",dataIDs);
				Hdf5Utils.insertAttributes(help0.hdf5DatasetValuesID,"dataSetNames",dataNames);
				Hdf5Utils.insertAttribute(help0.hdf5DatasetValuesID,"id","report");
				if(paramNames.size() != 0) {
					Hdf5Utils.insertAttributes(help0.hdf5DatasetValuesID,"paramNames",paramNames);
					Hdf5Utils.insertAttributes(help0.hdf5DatasetValuesID,"paramValues",paramValues);						
				}
				help0.close();
				H5.H5Gclose(jobGroupID);
				H5.H5Fclose(hdf5FileID);
			}
    	} catch(Exception e) {
    		e.printStackTrace();
		} finally {
			if(hdf5FileID != -1) {
				try {
					H5.H5Fclose(hdf5FileID);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			if(hdf5TempFile != null && hdf5TempFile.exists()) {
				try {
					hdf5TempFile.delete();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
    	
    	
//    	try {
//    		String name = "file.h5";
//			int fileId = H5.H5Fcreate(name, HDF5Constants.H5F_ACC_TRUNC, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
//			
//			// create the dataspace for the dataset
//			long dims[] = {4, 6};
//			int dataSpaceId = H5.H5Screate_simple(dims.length,  dims, null);	// # of dimensions of dataspace, array of the size of each dimension
//			
//			// create the dataset
//			String dataspaceName = "/" + name;
//			int datasetId = H5.H5Dcreate(fileId, dataspaceName, HDF5Constants.H5T_STD_I32BE, dataSpaceId, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
//			
//			// create group and add a dataset to it using this class Hdf5Utils
//			int set = Integer.parseInt("5");
//			int jobGroupID = Hdf5Utils.createGroup(fileId, "Set "+ set);
//			Hdf5Utils.HDF5WriteHelper help = null;	// contains a int hdf5DataSpaceID, int hdf5DatasetValuesID
//			int selectedColCount = 3;
//			int actualLength = 21;
//			help = Hdf5Utils.createDataset(jobGroupID, "data", new long[] {selectedColCount,actualLength});
//
//			// writing or reading to / from a dataset
//			H5.H5Dwrite_long(datasetId, jobGroupID, selectedColCount, fileId, actualLength, dims);
//
//			ArrayList<String> dataTypes = new ArrayList<String>();
//			ArrayList<String> dataIDs = new ArrayList<String>();
//			ArrayList<String> dataLabels = new ArrayList<String>();
//			ArrayList<String> dataNames = new ArrayList<String>();
//			Hdf5Utils.insertAttribute(help.hdf5DatasetValuesID, "_type", "ODE Data Export");//.writeHDF5Dataset(help0.hdf5DatasetValuesID, "_type", null, "ODE Data Export", true);
//			Hdf5Utils.insertAttributes(help.hdf5DatasetValuesID,"dataSetDataTypes", dataTypes);//.writeHDF5Dataset(help0.hdf5DatasetValuesID, "dataSetDataTypes", null, dataTypes, true);
//			Hdf5Utils.insertAttributes(help.hdf5DatasetValuesID,"dataSetIds",dataIDs);//Hdf5Utils.writeHDF5Dataset(help0.hdf5DatasetValuesID, "dataSetIds", null,dataIDs , true);
//			Hdf5Utils.insertAttributes(help.hdf5DatasetValuesID,"dataSetLabels",dataLabels);//Hdf5Utils.writeHDF5Dataset(help0.hdf5DatasetValuesID, "dataSetLabels", null,dataLabels , true);
//			Hdf5Utils.insertAttributes(help.hdf5DatasetValuesID,"dataSetNames",dataNames);//Hdf5Utils.writeHDF5Dataset(help0.hdf5DatasetValuesID, "dataSetNames", null,dataNames , true);
//
//			
//			// close the dataset and the dataspace
//			H5.H5Dclose(datasetId);
//			H5.H5Sclose(dataSpaceId);
//			H5.H5Fclose(fileId);
//
//			
//		} catch (HDF5Exception | NullPointerException e) {
//			e.printStackTrace();
//		}
    }

}
