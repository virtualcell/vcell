package org.vcell.cli.run.hdf5;

import java.util.List;

public class Hdf5PreparedData {
    public String sedmlId;
    public long[] dataDimensions;
    public double[] flattenedDataBuffer;
    public List<Integer> spatialDimensions;
    public double[] times;
}
