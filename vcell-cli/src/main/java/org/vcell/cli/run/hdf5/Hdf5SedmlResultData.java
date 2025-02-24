package org.vcell.cli.run.hdf5;

import org.vcell.cli.run.results.DataMapping;
import org.vcell.sbml.vcell.lazy.LazySBMLDataAccessor;

/**
 * Abstract class for holding hdf5-relevant data
 */
public abstract class Hdf5SedmlResultData {
    public int[] scanBounds;
    public String[] scanParameterNames;
    public double[][] scanParameterValues;
    public DataMapping<LazySBMLDataAccessor> dataItems = new DataMapping<>();
}
