package org.vcell.cli.run.hdf5;

import org.jlibsedml.DataSet;
import org.jlibsedml.Report;

import java.util.*;

/**
 * Struct-class to hold list of spatial variable data
 */
public class Hdf5SedmlResultsSpatial extends Hdf5SedmlResultData {
    //public SpatialDataMapping dataItems = new SpatialDataMapping();
    public static class SpatialComponents {
        public Hdf5DataSourceSpatialSimMetadata metadata;
        public Hdf5DataSourceSpatialSimVars varsToData;
    }

    Map<Report, SpatialComponents> dataMapping = new HashMap<>();
}
