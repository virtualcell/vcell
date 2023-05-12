package org.vcell.cli.run.hdf5;
//import ncsa.hdf.hdf5lib.*;

import java.io.File;
import java.io.IOException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;


import java.util.List;
import java.util.Map;
import java.util.HashMap;

import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import ncsa.hdf.hdf5lib.exceptions.HDF5LibraryException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.cli.run.hdf5.Hdf5DataPreparer.Hdf5PreparedData;

/**
 * Class to handle Hdf5 creation, data, and assist with I/O.
 */
public class Hdf5File {
    // NB: Hdf5 group management is ***important***.
    private final static Logger logger = LogManager.getLogger(Hdf5File.class);

    final private int H5F_ACC_TRUNC = HDF5Constants.H5F_ACC_TRUNC;
    final private int H5P_DEFAULT = HDF5Constants.H5P_DEFAULT;
    final private int H5F_ACC_EXCL = HDF5Constants.H5F_ACC_EXCL;
    final private int H5T_C_S1 = HDF5Constants.H5T_C_S1;
    final private int H5I_INVALID_HID = HDF5Constants.H5I_INVALID_HID;
    final private int H5T_VARIABLE = HDF5Constants.H5T_VARIABLE;
    final private int H5T_STR_NULLTERM = HDF5Constants.H5T_STR_NULLTERM;
    final private int H5T_CSET_UTF8 = HDF5Constants.H5T_CSET_UTF8;
    final private int H5E_DEFAULT_ERROR_STACK = HDF5Constants.H5E_DEFAULT;

    private File javaFileTarget;
    private int fileId;
    private boolean isOpen, allowExceptions;

    
    // TODO: create actual java Bimap implementation(s) with accompaning java interface (good intro task for new appdevI hires?)
    private Map<Integer, String> idToPathMap;
    private Map<String, Integer> pathToIdMap;
    private Map<Integer, Integer> datasetToDataspaceMap;
    
    private Hdf5File(){
        this.fileId = HDF5Constants.H5I_INVALID_HID;
        this.isOpen = false;

        // Explicit generic typing to highlight relationship; this is a "bi-map":
        this.idToPathMap = new HashMap<Integer, String>();
        this.pathToIdMap = new HashMap<String, Integer>();
        this.datasetToDataspaceMap = new HashMap<>();
    }

    /**
     * Creates an Hdf5File named "reports.h5" in the provided directory, and will throw exceptions where c-style error codes would be returned.
     * 
     * @param parentDir the directory to put the Hdf5 file inside of.
     */
    public Hdf5File(File parentDir) { //"/home/ldrescher/VCell/hdf5Rebuild/testingDir"
        this(parentDir, true);
    }

    /**
     * The main constructor for Hdf5File. Note the special interpretation of allowExceptions.
     * 
     * @param parentDir the directory to put the Hdf5 file inside of.
     * @param allowExceptions Whether to interperate C-style error codes as exceptions or let the user handle them.
     *                        Hdf5 Library-produced exceptions will still be generated regardless.
     */
    public Hdf5File(File parentDir, boolean allowExceptions){
        this(parentDir, "reports.h5", allowExceptions);
    }


    public void printErrorStack() {
        try {
            H5.H5Eprint2(H5E_DEFAULT_ERROR_STACK, null);
        } catch (HDF5LibraryException e){
            String message = "Catatrophic HDF5 error reporting failure detected; Something big just happened...";
            logger.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Complete constructor of `Hdf5File`
     * 
     * @param parentDir the directory to put the Hdf5 file inside of.
     * @param filename name of the h5 file to write.
     * @param allowExceptions Whether to interperate C-style error codes as exceptions or let the user handle them.
     *                        Hdf5 Library-produced exceptions will still be generated regardless.
     */
    public Hdf5File(File parentDir, String filename, boolean allowExceptions){
        this();
        this.javaFileTarget = new File(parentDir, filename);
        this.allowExceptions = allowExceptions;
    }

    /**
     * Opens the Hdf5 file
     * 
     * @throws HDF5LibraryException
     * @throws IOException
     */
    public void open() throws HDF5LibraryException, IOException {
        this.open(true);
    }

    /**
     * Opens the Hdf5 file if and only the file does not already exist
     * 
     * @param overwrite allow an overwrite of an existing file
     * @return the HDF5 id number of the file
     * @throws HDF5LibraryException
     * @throws IOException
     */
    public int open(boolean overwrite) throws HDF5LibraryException, IOException {
        String path = this.javaFileTarget.getCanonicalPath();
        int accessMod = overwrite ? H5F_ACC_TRUNC: H5F_ACC_EXCL;
        this.fileId = H5.H5Fcreate(path, accessMod, H5P_DEFAULT, H5P_DEFAULT);
        if (this.fileId < 0){
            String message = "HDF5 File could not be created [H5Fcreate]; HDF5 files can not be generated.";
            RuntimeException e = new RuntimeException(message); // nvestigate if Hdf5Exception would be more appropriate
            logger.warn("Hdf5 error occured", e);
            if (this.allowExceptions) throw e;
        }
        this.isOpen = true;
        return this.fileId;
    }

    /**
     * Add a group to the Hdf5 file based on a given path. If the group exists, the group_id will be returned.
     * 
     * @param groupPath the unix-style path *relative from the Hdf5 root (known as "/")* to place the group at
     *                  while hdf5 does allow with relative pathing from other groups, VCell does not support that at this time.
     * @return the group ID
     */
    public int addGroup(String groupPath) throws HDF5LibraryException {
        if (!this.isOpen){
            if (this.allowExceptions) throw new RuntimeException("Hdf5 file is not open.");
            return -1;
        }

        if (groupPath == null || groupPath.charAt(0) != '/'){
            if (this.allowExceptions) throw new RuntimeException("groupPath is not formatted correctly, or null");
            return -1;
        }

        int groupId = H5.H5Gcreate(this.fileId, groupPath, H5P_DEFAULT, H5P_DEFAULT, H5P_DEFAULT);

        if (groupId < 0){
            String message = "HDF5 File could not be created [H5Fcreate]; HDF5 files can not be generated.";
            RuntimeException e = new RuntimeException(message); // investigate if Hdf5Exception would be more appropriate
            logger.warn("Hdf5 error occured", e);
            if (this.allowExceptions) throw e;
        }

        this.idToPathMap.put(groupId, groupPath);
        this.pathToIdMap.put(groupPath, groupId);

        return groupId;
    }

    /**
     * Get the path to the group referenced by the provided group ID
     * 
     * @param id the identification number of the group
     * @return the hdf5 path connected to the id, or null if the group is not registered / does not exist.
     */
    public String getGroupPath(int id){
        if (this.idToPathMap.containsKey(id)) return this.idToPathMap.get(id);
        return null;
    }

    /**
     * Get the group ID of a group specfified by the provided hdf5 path 
     * 
     * @param path path where the the group is located in the HDF5 file
     * @return the group ID, or -1 if the group is not registered / does not exist.
     */
    public int getGroup(String path){
        if (this.pathToIdMap.containsKey(path)) return this.pathToIdMap.get(path);
        return -1;
    }

    /**
     * Checks if a group exists based on a provided group ID
     * 
     * @param id the woulb be identification number of the group
     * @return whether or not the group could be found
     */
    public boolean containsGroup(int id){
        return this.idToPathMap.containsKey(id);
    }

    /**
     * Checks if a group exists based on a provided hdf5 path
     * 
     * @param path path where the the group would be located in the HDF5 file
     * @return whether or not the group could be found
     */
    public boolean containsGroup(String path){
        return this.pathToIdMap.containsKey(path);
    }

    /**
     * [BROKEN, made private until var strings inplemented] Inserts a HDF5 attribute into a HDF5 group (including datasets) with a variable length string datum
     * 
     * @param hdf5GroupID the id of the group to place the attribute in
     * @param attributeName the name of the attribute to insert
     * @param datum the attribute data / value to apply
     * @throws HDF5LibraryException if HDF5 encountered a problem
     */
    private void insertVarStringAttribute(int hdf5GroupID, String attributeName, String datum) throws HDF5LibraryException {
		String attr = datum;

		int datatypeId = this.createVLStringDatatype();
		int dataspace_id = H5.H5Screate(HDF5Constants.H5S_SCALAR);
		int attribute_id = H5.H5Acreate(hdf5GroupID, attributeName, datatypeId, dataspace_id, HDF5Constants.H5P_DEFAULT,HDF5Constants.H5P_DEFAULT);
		H5.H5Awrite(attribute_id, datatypeId, attr.getBytes());
		H5.H5Sclose(dataspace_id);
		H5.H5Aclose(attribute_id);
		H5.H5Tclose(datatypeId);
	}

    /**
     * Inserts a HDF5 attribute into a HDF5 group (including datasets) with a fixed length string datum
     * 
     * @param hdf5GroupID the id of the group to place the attribute in
     * @param attributeName the name of the attribute to insert
     * @param datum the attribute data / value to apply
     * @throws HDF5LibraryException if HDF5 encountered a problem
     */
    public void insertFixedStringAttribute (int hdf5GroupID, String attributeName, String datum) throws HDF5LibraryException {
		String attr = datum + '\u0000';

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
     * [BROKEN, made private until var strings inplemented] Inserts a HDF5 attribute into a HDF5 group (including datasets) with a list of fixed length strings of data
     *  
     * @param hdf5GroupID the id of the group to place the attribute in
     * @param attributeName the name of the attribute to insert
     * @param data the attribute data / value list to apply
     * @throws HDF5Exception if HDF5 encountered a problem.
     */
	private void insertVarStringAttributes(int hdf5GroupID, String attributeName, List<String> data) throws HDF5Exception {
        String flatData = "";
        for (String datum : data){
            flatData += (datum + '\u0000');
        }

		int typeId = this.createVLStringDatatype();
        long dims[] = new long[]{data.size()};
		int dataspaceId = H5.H5Screate_simple(1, dims, null);
        //dataspaceId = H5.H5Screate(typeId);
		int attributeId = H5.H5Acreate(hdf5GroupID, attributeName, typeId, dataspaceId, HDF5Constants.H5P_DEFAULT,HDF5Constants.H5P_DEFAULT);
		H5.H5Awrite(attributeId, typeId, flatData.getBytes());
		H5.H5Sclose(dataspaceId);
		H5.H5Aclose(attributeId);
		H5.H5Tclose(typeId); 
	}

    /**
     * Inserts a HDF5 attribute into a HDF5 group (including datasets) with a fixed length string of data
     *  
     * @param hdf5GroupID the id of the group to place the attribute in
     * @param attributeName the name of the attribute to insert
     * @param data the attribute data / value list to apply
     * @throws HDF5Exception if HDF5 encountered a problem
     */
    public void insertFixedStringAttributes(int hdf5GroupID, String attributeName, List<String> data) throws HDF5Exception {
		String[] attr = data.toArray(new String[0]);
		long[] dims = new long[] {attr.length}; // Always an array of length == 1
		StringBuffer sb = new StringBuffer();
		int MAXSTRSIZE=  -1;

		// Get the max length of all the data strings
		for(int i = 0; i < attr.length; i++) {
			int len = attr[i] == null ? -1 : attr[i].length(); 

			if (len == 0) len = 1; // Need to pad with null char for empty str; passing a 0 causes null exception
			if (attr[i] == null) attr[i] = ""; // Padding comes later, don't worry.
			
			MAXSTRSIZE = Math.max(MAXSTRSIZE, len);
		}

		// Append data to single string buffer, padding with null characters to create uniformity.
		for(int i = 0; i < attr.length; i++) {
			sb.append(attr[i]);
			for(int j = 0; j < (MAXSTRSIZE - attr[i].length()); j++) {
				sb.append('\u0000'); //null terminated string for hdf5 native code
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

    /**
     * Inserts a HDF5 attribute into a HDF5 group (including datasets) with a fixed length string of data
     *  
     * @param hdf5GroupID the id of the group to place the attribute in
     * @param dataspaceName the name of the attribute to insert
     * @param data the attribute data / value list to apply
     * @throws HDF5Exception if HDF5 encountered a problem
     */
    public void insertNumericAttributes(int hdf5GroupID,String dataspaceName,double[] data) throws HDF5Exception {
		long[] dims = new long[] {data.length};
		//https://support.hdfgroup.org/ftp/HDF5/examples/misc-examples/vlstra.c
		int dataspace_id = H5.H5Screate_simple(dims.length, dims,null);
		int attribute_id = H5.H5Acreate(hdf5GroupID, dataspaceName, HDF5Constants.H5T_NATIVE_DOUBLE, dataspace_id, HDF5Constants.H5P_DEFAULT,HDF5Constants.H5P_DEFAULT);
		H5.H5Awrite (attribute_id, HDF5Constants.H5T_NATIVE_DOUBLE, this.byteArray(data));
		H5.H5Sclose(dataspace_id);
		H5.H5Aclose(attribute_id);
	}

    public Integer insertSedmlData(String canonicalGroupPath, Hdf5PreparedData preparedData) throws HDF5Exception {
        if (this.pathToIdMap.containsKey(canonicalGroupPath)){
            return this.insertSedmlData(this.pathToIdMap.get(canonicalGroupPath), preparedData);
        }
        if (this.allowExceptions) throw new HDF5Exception("Group path provided has not been created.");
        return H5I_INVALID_HID;
    }

    public int insertSedmlData(int groupId, Hdf5PreparedData preparedData) throws HDF5Exception {
        //String datasetPath = Paths.get(sedmlUri, datasetWrapper.datasetMetadata.sedmlId).toString();
        int hdf5DataspaceID = H5.H5Screate_simple(preparedData.dataDimensions.length, preparedData.dataDimensions, null);
        int hdf5DatasetID = H5.H5Dcreate(groupId, preparedData.sedmlId, HDF5Constants.H5T_NATIVE_DOUBLE, hdf5DataspaceID, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
        H5.H5Dwrite_double(hdf5DatasetID, HDF5Constants.H5T_NATIVE_DOUBLE, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, (double[])preparedData.flattenedDataBuffer);
        
        this.datasetToDataspaceMap.put(hdf5DatasetID, hdf5DataspaceID); // Put the ids in here to ensure we close everything right.
        return hdf5DatasetID;
    }

    /**
     * 
     * 
     * @param datasetId
     * @return
     * @throws HDF5Exception
     */
    public int closeDataset(int datasetId) throws HDF5Exception {
        if (!this.datasetToDataspaceMap.containsKey(datasetId)){
            if (this.allowExceptions) throw new HDF5Exception("Dataset provided has not been created.");
            return H5I_INVALID_HID;
        }

        H5.H5Sclose(this.datasetToDataspaceMap.get(datasetId));
        this.datasetToDataspaceMap.remove(datasetId);
        return H5.H5Dclose(datasetId);
    }

    public int close() throws HDF5Exception {
        if (!this.isOpen) return 0;
        //this.fileId = HDF5Constants.H5I_INVALID_HID;
        this.isOpen = false;

        // Don't forget to close datasets (and their dataspaces)
        for (int datasetId : this.datasetToDataspaceMap.keySet()){
            this.closeDataset(datasetId);
        }

        // Don't forget to close all groups
        for (int groupId : this.idToPathMap.keySet()){
            H5.H5Gclose(groupId);
        }
        this.idToPathMap.clear();
        this.pathToIdMap.clear();

        return this.fileId < 0 ? this.fileId : (this.fileId = H5.H5Fclose(this.fileId));
    }



    //----------------------------------------------------------------------------------------------------------------



    private int createVLStringDatatype() {
        int datatypeId = H5I_INVALID_HID;
        try {
            datatypeId = H5.H5Tcopy(H5T_C_S1);
            int status = H5.H5Tset_size(datatypeId, H5T_VARIABLE);
            H5.H5Tset_strpad(datatypeId, H5T_STR_NULLTERM);
            H5.H5Tset_cset(datatypeId, H5T_CSET_UTF8);

            if (status < 0){
                throw new HDF5LibraryException("Size unable to be set");
            }
        } catch (HDF5LibraryException e){
            String message = "Unable to generate important datatype: Var string.";
            logger.error(message, e);
            throw new RuntimeException(message, e);
        }

        return datatypeId;
        // Remember to close the dataspace when you're done
    }

    private byte[] byteArray(double[] doubleArray) {
		int times = Double.SIZE / Byte.SIZE;
		byte[] bytes = new byte[doubleArray.length * times];
		for (int i = 0; i < doubleArray.length; i++) {
			getByteBuffer(bytes, i, times).putDouble(doubleArray[i]);
		}
		return bytes;
	}

    private static ByteBuffer getByteBuffer(byte[] bytes, int index, int times) {
		return ByteBuffer.wrap(bytes, index * times, times).order(ByteOrder.LITTLE_ENDIAN);
	}

}
