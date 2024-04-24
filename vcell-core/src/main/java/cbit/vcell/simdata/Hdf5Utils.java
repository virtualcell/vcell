package cbit.vcell.simdata;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import cbit.vcell.math.ReservedVariable;
import org.apache.commons.lang.ArrayUtils;

import cbit.vcell.math.Variable;
import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import ncsa.hdf.hdf5lib.exceptions.HDF5LibraryException;

public class Hdf5Utils {
	private static final String HDF5_GROUP_SOLUTION = "/solution";
	private static final String HDF5_GROUP_EXTRAPOLATED_VOLUMES = "/extrapolated_volumes";
	private static final String HDF5_GROUP_DIRECTORY_SEPARATOR = "/";

	/**
	 * Creates a relative path to the solution to the variable specified
	 * 
	 * @param varName the name of the variable to path to.
	 * @return the relative path
	 */
	public static String getVarSolutionPath(String varName){
		return HDF5_GROUP_SOLUTION + HDF5_GROUP_DIRECTORY_SEPARATOR + Variable.getNameFromCombinedIdentifier(varName); 
	}
	
	/**
	 * Creates a relative path to the extrapolated values of a given variable name.
	 * 
	 * @param varName name of the variable to path to
	 * @return the relative path
	 */
	public static String getVolVarExtrapolatedValuesPath(String varName){
		return HDF5_GROUP_EXTRAPOLATED_VOLUMES + HDF5_GROUP_DIRECTORY_SEPARATOR + "__" + Variable.getNameFromCombinedIdentifier(varName) + "_extrapolated__"; 
	}

    public static File exportTableToHDF5(boolean bHistogram, String blankCellValue, int[] columns, int[] rows, String xVarColumnName, String hdf5DescriptionText, String[] columnNames, String[] paramScanParamNames, Double[][] paramScanParamValues, Object[][] rowColValues) throws Exception {
		int hdf5FileID = -1;//Used if HDF5 format
		File hdf5TempFile = null;
		try {
			hdf5TempFile = File.createTempFile("plot2D", ".hdf");
			//System.out.println("/home/vcell/Downloads/hdf5/HDFView/bin/HDFView "+hdf5TempFile.getAbsolutePath());
			hdf5FileID = H5.H5Fcreate(hdf5TempFile.getAbsolutePath(), HDF5Constants.H5F_ACC_TRUNC,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
			ArrayList<ArrayList<Integer>> paramScanJobs = new ArrayList<>();
			if(!bHistogram && !columnNames[0].equals((xVarColumnName==null? ReservedVariable.TIME.getName():xVarColumnName))) {
				throw new Exception("Expecting first column in table to have name '"+xVarColumnName+"'");
			}
			//Add arraylist for the parameter scan job, add the index of the xval column
			for(int i=0;i<columnNames.length;i++) {
				if(bHistogram) {
					ArrayList<Integer> tempAL = new ArrayList<Integer>();
					paramScanJobs.add(tempAL);
					break;
				} else if(columnNames[i].equals((xVarColumnName==null?ReservedVariable.TIME.getName():xVarColumnName))){
					if(i==0) {
						ArrayList<Integer> tempAL = new ArrayList<Integer>();
						tempAL.add(i);
						paramScanJobs.add(tempAL);
					}else {
						String str1 = columnNames[i-1];
						int str1Index = str1.lastIndexOf("Set ");
						String str2 = columnNames[i+1];
						int str2Index = str2.lastIndexOf("Set ");
						if(!str1.substring(str1Index).equals(str2.substring(str2Index))) {
							ArrayList<Integer> tempAL = new ArrayList<Integer>();
							tempAL.add(i);
							paramScanJobs.add(tempAL);
						}
					}
				}
			}
			//Add selected columns to the proper paramscan arraylist
			for(int j = 0; j< columns.length; j++) {
				if(bHistogram) {
					paramScanJobs.get(0).add(columns[j]);
				}else {
					if(columnNames[columns[j]].equals((xVarColumnName==null?ReservedVariable.TIME.getName():xVarColumnName))){
						continue;//skip xcolumns
					}
					for(int k=0;k<paramScanJobs.size();k++) {
						if(columns[j] >= paramScanJobs.get(k).get(0) && ((k+1) == paramScanJobs.size() || columns[j] < paramScanJobs.get(k+1).get(0))) {
							paramScanJobs.get(k).add(columns[j]);
//								System.out.println("HDF5frm"+columnNames[columns[j]));
						}
					}
				}
			}
			//Remove unselected indexes from set lists
			for(int k=0;k<paramScanJobs.size();k++) {
				final ListIterator<Integer> listIterator = paramScanJobs.get(k).listIterator();
				if(paramScanJobs.get(k).size() > 1) {// keep x val is there more selections for this set
					listIterator.next();
				}
				while(listIterator.hasNext()) {
					final Integer columIndex = listIterator.next();
					boolean bFound = false;
					for(int j = 0; j< columns.length; j++) {
						if(columIndex == columns[j]) {
							bFound = true;
							break;
						}
					}
					if(!bFound) {
						listIterator.remove();
					}
				}
			}
			//Write out the data to HDF5 file
			for(int k=0;k<paramScanJobs.size();k++) {
				int selectedColCount = paramScanJobs.get(k).size();
				if(selectedColCount == 0) {
					continue;
				}
				int jobGroupID = -1;//(int) Hdf5Utils.createGroup(hdf5FileID, "Set "+k);
						//writeHDF5Dataset(hdf5FileID, "Set "+k, null, null, false);
				HDF5WriteHelper help0 = null;//Hdf5Utils.createDataset(jobGroupID, "data", new long[] {selectedColCount,rows.length});
						//(HDF5WriteHelper) Hdf5Utils.writeHDF5Dataset(jobGroupID, "data", new long[] {selectedColCount,rows.length}, new Object[] {}, false);
				//((DefaultTableModel)getScrollPaneTable().getModel()).getDataVector()
				double[] fromData = new double[rows.length*selectedColCount];
				int actualLength = -1;
				int index = 0;
				ArrayList<String> dataTypes = new ArrayList<String>();
				ArrayList<String> dataIDs = new ArrayList<String>();
				ArrayList<String> dataShapes = new ArrayList<String>();
				ArrayList<String> dataLabels = new ArrayList<String>();
				ArrayList<String> dataNames = new ArrayList<String>();
				ArrayList<String> paramNames = new ArrayList<String>();
				ArrayList<String> paramValues = new ArrayList<String>();
				boolean bParamsDone = false;
				for(int cols=0;cols<paramScanJobs.get(k).size();cols++) {
					final Integer column = paramScanJobs.get(k).get(cols);
					dataTypes.add("float64");
					dataIDs.add("data_set_"+columnNames[column]);
					dataShapes.add(rows.length+"");
					dataLabels.add(columnNames[column]);
					String name = "--";
					if(columnNames[column].equals((xVarColumnName==null?ReservedVariable.TIME.getName():xVarColumnName))) {
						name = columnNames[column];
					}else {
						int indx = columnNames[column].lastIndexOf("-- Set ");
						if(indx != -1) {
							name = columnNames[column].substring(0, indx);
						}else {
							name = columnNames[column];
						}
					}
					dataNames.add(name);
					for(int myrows = 0; myrows< rows.length; myrows++) {
						final int row = rows[myrows];
						final Object valueAt = rowColValues[row][column];
						if(valueAt == null && actualLength == -1) {
							actualLength = myrows;
						}
//							System.out.println(row+" "+column+" "+valueAt);
						fromData[index] = Double.parseDouble((valueAt==null? blankCellValue :valueAt.toString()));
						index++;
					}
					actualLength = (actualLength==-1? rows.length:actualLength);
					String colName = columnNames[column];
//						System.out.println("HDF5frm "+colName);
					if(colName.lastIndexOf("Set ") != -1) {
						if(!bParamsDone) {
							bParamsDone = true;
							int set = Integer.parseInt(colName.substring(colName.lastIndexOf("Set ")+4));
							jobGroupID = (int) createGroup(hdf5FileID, "Set "+set);
							help0 = createDataset(jobGroupID, "data", new long[] {selectedColCount,actualLength});
							for(int z=0;z<paramScanParamNames.length;z++) {
								paramNames.add(paramScanParamNames[z]);
								paramValues.add(paramScanParamValues[set][z]+"");
//									System.out.print(" "+paramScanParamValues[set][z]);
							}
//							System.out.println();
						}
					}

				}

				double[] fromData2 = new double[actualLength*selectedColCount];
				for(int i=0;i<selectedColCount;i++) {
					System.arraycopy(fromData, i* rows.length, fromData2, i*actualLength, actualLength);
				}
				if(help0 == null) {
					jobGroupID = (int) createGroup(hdf5FileID, "Set "+k);
					help0 = createDataset(jobGroupID, "data", new long[] {selectedColCount,actualLength});
				}
				copySlice(help0.hdf5DatasetValuesID,fromData2,new long[] {0,0},new long[] {selectedColCount,actualLength},new long[] {selectedColCount,actualLength},new long[] {0,0},new long[] {selectedColCount,actualLength},help0.hdf5DataSpaceID);
					//writeHDF5Dataset(help0.hdf5DatasetValuesID, null, null, objArr, false);
				insertAttribute(help0.hdf5DatasetValuesID, "_type", "ODE Data Export");//.writeHDF5Dataset(help0.hdf5DatasetValuesID, "_type", null, "ODE Data Export", true);
				insertAttributes(help0.hdf5DatasetValuesID,"dataSetDataTypes", dataTypes);//.writeHDF5Dataset(help0.hdf5DatasetValuesID, "dataSetDataTypes", null, dataTypes, true);
				insertAttributes(help0.hdf5DatasetValuesID,"dataSetIds",dataIDs);//Hdf5Utils.writeHDF5Dataset(help0.hdf5DatasetValuesID, "dataSetIds", null,dataIDs , true);
				insertAttributes(help0.hdf5DatasetValuesID,"dataSetLabels",dataLabels);//Hdf5Utils.writeHDF5Dataset(help0.hdf5DatasetValuesID, "dataSetLabels", null,dataLabels , true);
				insertAttributes(help0.hdf5DatasetValuesID,"dataSetNames",dataNames);//Hdf5Utils.writeHDF5Dataset(help0.hdf5DatasetValuesID, "dataSetNames", null,dataNames , true);
				insertAttributes(help0.hdf5DatasetValuesID,"dataSetShapes",dataShapes);//Hdf5Utils.writeHDF5Dataset(help0.hdf5DatasetValuesID, "dataSetShapes", null,dataShapes , true);
				insertAttribute(help0.hdf5DatasetValuesID,"id","report");//Hdf5Utils.writeHDF5Dataset(help0.hdf5DatasetValuesID, "id", null,"report" , true);
				if(paramNames.size() != 0) {
					insertAttributes(help0.hdf5DatasetValuesID,"paramNames",paramNames);
					insertAttributes(help0.hdf5DatasetValuesID,"paramValues",paramValues);
				}
				help0.close();
				H5.H5Gclose(jobGroupID);
			}
			if(hdf5DescriptionText != null) {
				insertAttributes(hdf5FileID,"dataSourceDescr", Arrays.asList(new String[] {hdf5DescriptionText}));
			}
			H5.H5Fclose(hdf5FileID);
			hdf5FileID = -1;
		}finally {
			if(hdf5FileID != -1) {try{H5.H5Fclose(hdf5FileID);}catch(Exception e){e.printStackTrace();}}
			if(hdf5TempFile != null && hdf5TempFile.exists()) {try{hdf5TempFile.delete();}catch(Exception e){e.printStackTrace();}}
		}
		return hdf5TempFile;
	}

    /**
	 * Helper class to ensure HDF5 documents are closed properly.
	 */
	public static class HDF5WriteHelper {
		/**
		 * The id number of the hdf5 dataspace
		 */
		public int hdf5DataSpaceID;

		/**
		 * The id number of the hdf5 dataset
		 */
		public int hdf5DatasetValuesID;

		/**
		 * Construtor of te helper
		 * 
		 * @param hdf5DataSpaceID The id number of the hdf5 dataspace
		 * @param hdf5DatasetValuesID The id number of the hdf5 dataset
		 */
		public HDF5WriteHelper(int hdf5DataSpaceID, int hdf5DatasetValuesID) {
			super();
			this.hdf5DataSpaceID = hdf5DataSpaceID;
			this.hdf5DatasetValuesID = hdf5DatasetValuesID;
		}
		/**
		 * Closes the dataspace and dataset referenced in this helper class
		 * 
		 * @throws HDF5LibraryException if the hdf5 dataset and/or dataspace was unable to be successfully closed
		 */
		public void close() throws HDF5LibraryException {
			H5.H5Sclose(hdf5DataSpaceID);
			H5.H5Dclose(hdf5DatasetValuesID);
		}
	}

	/**
	 * Creates a dataset at the specified hdf5 group
	 * @param hdf5GroupID the group to place the dataset in
	 * @param datasetName the name to give the dataset
	 * @param dims n-dimentional sizes to give the dataset
	 * @return a HDF5 Writer helper class to store the relevant values
	 * @throws HDF5Exception if the hdf5 library encounters something unusual
	 */
	public static HDF5WriteHelper createDataset(int hdf5GroupID,String datasetName,long[] dims) throws HDF5Exception{
		//Create dataset and return it, must be closed when finished
		long[] datasetDimensions = dims;
		int hdf5DataspaceIDValues = H5.H5Screate_simple(datasetDimensions.length, datasetDimensions, null);
		int hdf5DatasetIDValues = H5.H5Dcreate(hdf5GroupID, datasetName, HDF5Constants.H5T_NATIVE_DOUBLE, hdf5DataspaceIDValues,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
		return new HDF5WriteHelper(hdf5DataspaceIDValues,hdf5DatasetIDValues);
	}

	/**
	 * Creates a new HDF5 group underneath an exisitng group / top of the hdf5 file
	 * 
	 * @param hdf5GroupID the ID of the top-level hdf5 file or one of its subgroups
	 * @param groupName the name of the group
	 * @return the new group's ID number
	 * @throws HDF5Exception if the hdf5 library encounters something unusual
	 */
	public static int createGroup(int hdf5GroupID,String groupName) throws HDF5Exception{
		return H5.H5Gcreate(hdf5GroupID, (String)groupName,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
	}

	/**
	 * Use the HDF5 hyperslab feature to copy data from one play to another (not sure how to use it though...)
	 * 
	 * @param copyToDataSet
	 * @param copyFromData
	 * @param copyToStart
	 * @param copyToLength
	 * @param copyFromDims
	 * @param copyFromStart
	 * @param copyFromLength
	 * @param dataspaceID
	 * @throws NullPointerException
	 * @throws IllegalArgumentException
	 * @throws HDF5Exception
	 */
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

	/**
	 * Insert an attribute at the specified group where the data are a single value
	 * 
	 * @param hdf5GroupID the id of the group to apply the attribute to
	 * @param attributeName name of the attribute
	 * @param data the data to place
	 * @throws NullPointerException (unsure how this occurs)
	 * @throws HDF5Exception if the hdf5 library encounters something unusual
	 */
	public static void insertAttribute(int hdf5GroupID,String attributeName,String data) throws NullPointerException, HDF5Exception {
		//insertAttributes(hdf5GroupID, dataspaceName, new ArrayList<String>(Arrays.asList(new String[] {data})));
		//String[] attr = data.toArray(new String[0]);

		String attr = data + '\u0000';

		//https://support.hdfgroup.org/ftp/HDF5/examples/misc-examples/vlstra.c
		int h5attrcs1 = H5.H5Tcopy(HDF5Constants.H5T_C_S1);
		H5.H5Tset_size (h5attrcs1, attr.length() /*HDF5Constants.H5T_VARIABLE*/);
		int dataspace_id = -1;
		//dataspace_id = H5.H5Screate_simple(dims.length, dims,null);
		dataspace_id = H5.H5Screate(HDF5Constants.H5S_SCALAR);
		int attribute_id = H5.H5Acreate(hdf5GroupID, attributeName, h5attrcs1, dataspace_id, HDF5Constants.H5P_DEFAULT,HDF5Constants.H5P_DEFAULT);
		H5.H5Awrite(attribute_id, h5attrcs1, attr.getBytes());
		H5.H5Sclose(dataspace_id);
		H5.H5Aclose(attribute_id);
		H5.H5Tclose(h5attrcs1);
	}
	
	/**
	 * Insert an attribute at the specified group where the data are multiple values
	 * 
	 * @param hdf5GroupID the id of the group to apply the attribute to
	 * @param attributeName name of the attribute
	 * @param data the data to place
	 * @throws NullPointerException (unsure how this occurs)
	 * @throws HDF5Exception if the hdf5 library encounters something unusual
	 */
	public static void insertAttributes(int hdf5GroupID,String attributeName,List<String> data) throws NullPointerException, HDF5Exception {
		String[] attr = data.toArray(new String[0]);
		long[] dims = new long[] {attr.length}; // Always an array of length == 1
		StringBuffer sb = new StringBuffer();
		int MAXSTRSIZE=  -1;

		// Get the max length of all the data strings
		for(int i=0;i<attr.length;i++) {
			int len = attr[i] == null ? -1 : attr[i].length(); 

			if (len == 0) 
				len = 1; // Need to pad with null char for empty str; passing a 0 causes null exception

			if (attr[i] == null) 
				attr[i] = "";
			
			MAXSTRSIZE = Math.max(MAXSTRSIZE, len);
		}

		// Append data to single string buffer, padding with null characters to create uniformity.
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
		int attribute_id = H5.H5Acreate(hdf5GroupID, attributeName, h5attrcs1, dataspace_id, HDF5Constants.H5P_DEFAULT,HDF5Constants.H5P_DEFAULT);
		H5.H5Awrite(attribute_id, h5attrcs1, sb.toString().getBytes());
		H5.H5Sclose(dataspace_id);
		H5.H5Aclose(attribute_id);
		H5.H5Tclose(h5attrcs1);
	}

	private static ByteBuffer getByteBuffer(byte[] bytes, int index, int times) {
		return ByteBuffer.wrap(bytes, index * times, times).order(ByteOrder.LITTLE_ENDIAN);
	}

	private static byte[] byteArray(double[] doubleArray) {
		int times = Double.SIZE / Byte.SIZE;
		byte[] bytes = new byte[doubleArray.length * times];
		for (int i = 0; i < doubleArray.length; i++) {
			getByteBuffer(bytes, i, times).putDouble(doubleArray[i]);
		}
		return bytes;
	}

	/**
	 * Insert an attribute at the specified group where the data are multiple vnumerical values
	 * 
	 * @param hdf5GroupID the id of the group to apply the attribute to
	 * @param attributeName name of the attribute
	 * @param data the numerical data to apply
	 * @throws NullPointerException (unsure how this occurs)
	 * @throws HDF5Exception if the hdf5 library encounters something unusual
	 */
	public static void insertAttributes(int hdf5GroupID,String attributeName,double[] data) throws NullPointerException, HDF5Exception {
		long[] dims = new long[] {data.length};
		StringBuffer sb = new StringBuffer();
		//https://support.hdfgroup.org/ftp/HDF5/examples/misc-examples/vlstra.c
		int dataspace_id = H5.H5Screate_simple(dims.length, dims,null);
		int attribute_id = H5.H5Acreate(hdf5GroupID, attributeName, HDF5Constants.H5T_NATIVE_DOUBLE, dataspace_id, HDF5Constants.H5P_DEFAULT,HDF5Constants.H5P_DEFAULT);
		H5.H5Awrite (attribute_id, HDF5Constants.H5T_NATIVE_DOUBLE, byteArray(data));
		H5.H5Sclose(dataspace_id);
		H5.H5Aclose(attribute_id);
	}

//	public static void insertString(int hdf5GroupID,String dataspaceName,long[] dims,String data) throws NullPointerException, HDF5Exception {
//		insertStrings(hdf5GroupID, dataspaceName, dims,new ArrayList<String>(Arrays.asList(new String[] {data})));
//	}

	/**
	 * Insert a dataset at the specififed group where the data are strings
	 * 
	 * @param hdf5GroupID the id of the group to apply the dataset to
	 * @param datasetName name of the dataset
	 * @param dims dimentional meansurements 
	 * @param data the data to fill the dataset
	 * @throws NullPointerException (unsure how this occurs)
	 * @throws HDF5Exception if the hdf5 library encounters something unusual
	 */
	public static void insertStrings(int hdf5GroupID,String datasetName,long[] dims,List<String> data) throws NullPointerException, HDF5Exception {
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
		int hdf5DatasetID = H5.H5Dcreate(hdf5GroupID, datasetName,h5tcs1, hdf5DataspaceID,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
		//final byte[] bytes = allStringSB.toString().getBytes();
		H5.H5Dwrite(hdf5DatasetID, h5tcs1, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, bytes);
		H5.H5Tclose(h5tcs1);
		H5.H5Dclose(hdf5DatasetID);
		H5.H5Sclose(hdf5DataspaceID);
	}

	/**
	 * Insert a dataset at the specififed group where the data are integers
	 * 
	 * @param hdf5GroupID the id of the group to apply the dataset to
	 * @param datasetName name of the dataset
	 * @param dims dimentional meansurements 
	 * @param data the data to fill the dataset
	 * @throws NullPointerException (unsure how this occurs)
	 * @throws HDF5Exception if the hdf5 library encounters something unusual
	 */
	public static void insertInts(int hdf5GroupID,String dataspaceName,long[] dims,int[] data) throws NullPointerException, HDF5Exception {
		int hdf5DataspaceID = H5.H5Screate_simple(dims.length, dims, null);
		int hdf5DatasetID = H5.H5Dcreate(hdf5GroupID, dataspaceName,HDF5Constants.H5T_NATIVE_INT, hdf5DataspaceID,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
		H5.H5Dwrite_int(hdf5DatasetID, HDF5Constants.H5T_NATIVE_INT, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, (int[])data);
		H5.H5Dclose(hdf5DatasetID);
		H5.H5Sclose(hdf5DataspaceID);
	}

	/**
	 * Insert a dataset at the specififed group where the data are doubles (as an array)
	 * 
	 * @param hdf5GroupID the id of the group to apply the dataset to
	 * @param datasetName name of the dataset
	 * @param dims dimentional meansurements 
	 * @param data the data to fill the dataset
	 * @throws NullPointerException (unsure how this occurs)
	 * @throws HDF5Exception if the hdf5 library encounters something unusual
	 */
	public static void insertDoubles(int hdf5GroupID,String dataspaceName,long[] dims,double[] data) throws NullPointerException, HDF5Exception {
		int hdf5DataspaceID = H5.H5Screate_simple(dims.length, dims, null);
		int hdf5DatasetID = H5.H5Dcreate(hdf5GroupID, dataspaceName,HDF5Constants.H5T_NATIVE_DOUBLE, hdf5DataspaceID,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
		H5.H5Dwrite_double(hdf5DatasetID, HDF5Constants.H5T_NATIVE_DOUBLE, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, (double[])data);
		H5.H5Dclose(hdf5DatasetID);
		H5.H5Sclose(hdf5DataspaceID);
	}

	/**
	 * Insert a dataset at the specififed group where the data are doubles (as a java List)
	 * 
	 * @param hdf5GroupID the id of the group to apply the dataset to
	 * @param datasetName name of the dataset
	 * @param dims dimentional meansurements 
	 * @param data the data to fill the dataset
	 * @throws NullPointerException (unsure how this occurs)
	 * @throws HDF5Exception if the hdf5 library encounters something unusual
	 */
	public static void insertDoubles(int hdf5GroupID,String dataspaceName,long[] dims,List<Double> data) throws NullPointerException, HDF5Exception {
		double[] hdfData = ArrayUtils.toPrimitive(((ArrayList<Double>)data).toArray(new Double[0]));
		int hdf5DataspaceID = H5.H5Screate_simple(dims.length, dims, null);
		int hdf5DatasetID = H5.H5Dcreate(hdf5GroupID, dataspaceName,HDF5Constants.H5T_NATIVE_DOUBLE, hdf5DataspaceID,HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
		H5.H5Dwrite_double(hdf5DatasetID, HDF5Constants.H5T_NATIVE_DOUBLE, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, hdfData);
		H5.H5Dclose(hdf5DatasetID);
		H5.H5Sclose(hdf5DataspaceID);
	}
}
