package org.vcell.cli.run.hdf5;

import org.jlibsedml.DataSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

/**
 * Struct-class to hold list of nonspacial variable data
 */
public class Hdf5SedmlResultsNonspatial extends Hdf5SedmlResultData {
    public Hdf5SedmlResultsNonspatial() {}

    /**
     * Map of all data contained within a job relevant to HDF5 formatted files
     */
    public Map<DataSet, List<double[]>> allJobResults = new LinkedHashMap<>();
    
}
