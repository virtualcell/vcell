package org.vcell.cli.run.hdf5;

import java.util.ArrayList;
import java.util.List;

/**
 * Struct-class to hold list of spatial variable data
 */
public class Hdf5SedmlResultsSpatial extends Hdf5SedmlResultData {

    public List<Hdf5DataSourceSpatialVarDataItem> varDataItems = new ArrayList<>();
}
