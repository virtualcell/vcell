package org.vcell.cli.run.hdf5;

import org.jlibsedml.Variable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Struct-class to hold list of nonspacial variable data
 */
public class Hdf5DataSourceNonspatial extends Hdf5DataSource {
    public Hdf5DataSourceNonspatial() {}

    /**
     * List of all data contained within a job relevant to HDF5 formatted files
     */
    public List<Hdf5JobData> allJobResults = new ArrayList<>();

    /**
     * Struct-Subclass for holding job data
     */
    public static class Hdf5JobData {
        // metadata?  parameter values??? or jobIndex??
        /**
         * Mapping that connects a sedml varaible to its data
         */
        public Map<Variable, double[]> varData = new LinkedHashMap<>();
    }

    
}
