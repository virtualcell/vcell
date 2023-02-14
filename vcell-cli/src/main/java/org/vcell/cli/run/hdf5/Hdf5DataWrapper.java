package org.vcell.cli.run.hdf5;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;

import java.io.File;
import java.io.IOException;

/**
 * Data wrapper class containing the relevant data for Hdf5 
 */
public class Hdf5DataWrapper {
    /**
     *  Uri to data map
     */
    public Map<String, List<Hdf5DatasetWrapper>> datasetWrapperMap = new HashMap<>();

    /**
     * Basic constructor
     */
    public Hdf5DataWrapper(){
        this.datasetWrapperMap = new HashMap<>();
    }

    /**
     * Take the data of another wrapper and fold it into this wrapper
     * 
     * @param moreData
     */
    public void incorporate(Hdf5DataWrapper moreData){
        datasetWrapperMap.putAll(moreData.datasetWrapperMap);
    }

    /**
     * Uses this class to create and save an Hdf5 format output file
     * 
     * @param data an instance of Hdf5DataWrapper
     * @param parentDirForOutput the directory to put the Hdf5 file in
     * @throws HDF5Exception if there is an Hdf5 processing error
     * @throws IOException if the OS encountered an IO error
     */
    public static void writeToFile(Hdf5DataWrapper data, File parentDirForOutput) throws HDF5Exception, IOException {
        Hdf5Writer.writeHdf5(data, parentDirForOutput);
    }
}
