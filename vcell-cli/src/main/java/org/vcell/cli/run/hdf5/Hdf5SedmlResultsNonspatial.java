package org.vcell.cli.run.hdf5;

import org.vcell.cli.run.results.NonSpatialDataMapping;

/**
 * Struct-class to hold list of nonspatial variable data
 */
public class Hdf5SedmlResultsNonspatial extends Hdf5SedmlResultData {
    public Hdf5SedmlResultsNonspatial() {}

    /**
     * Map of all data contained within a job relevant to HDF5 formatted files
     */
    public NonSpatialDataMapping dataItems = new NonSpatialDataMapping();
    
}
