package org.vcell.cli.run.hdf5;

/**
 * Abstract class for holding hdf5-relevant data
 */
public abstract class Hdf5SedmlResultData {
    public int[] scanBounds;
    public String[] scanParameterNames;

    public double[][] scanParameterValues;
}
