package org.vcell.cli.run.hdf5;

import org.jlibsedml.DataSet;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Struct-class to hold list of spatial variable data
 */
public class Hdf5SedmlResultsSpatial extends Hdf5SedmlResultData {
    public SpatialDataMapping dataItems = new SpatialDataMapping();
}
