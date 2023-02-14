package org.vcell.cli.run.hdf5;

import java.util.ArrayList;
import java.util.List;

/**
 * Struct-class to hold list of spacial variable data
 */
public class Hdf5DataSourceSpatial extends Hdf5DataSource {

    public List<Hdf5DataSourceSpatialVarDataItem> varDataItems = new ArrayList<>();
}
