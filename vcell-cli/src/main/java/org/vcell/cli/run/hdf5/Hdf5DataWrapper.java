package org.vcell.cli.run.hdf5;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;

import java.io.File;
import java.io.IOException;

public class Hdf5DataWrapper {
    public Map<String, List<Hdf5DatasetWrapper>> datasetWrapperMap = new HashMap<>();

    public Hdf5DataWrapper(){
        this.datasetWrapperMap = new HashMap<>();
    }

    public void incorporate(Hdf5DataWrapper moreData){
        datasetWrapperMap.putAll(moreData.datasetWrapperMap);
    }

    public static void writeToFile(Hdf5DataWrapper data, File parentDirForOutput) throws HDF5Exception, IOException {
        Hdf5Writer.writeHdf5(data, parentDirForOutput);
    }
}
