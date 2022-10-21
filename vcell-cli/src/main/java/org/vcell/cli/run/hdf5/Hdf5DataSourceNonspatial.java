package org.vcell.cli.run.hdf5;

import org.jlibsedml.Variable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Hdf5DataSourceNonspatial extends Hdf5DataSource {
    public Hdf5DataSourceNonspatial() {
    }

    public static class Hdf5JobData {
        // metadata?  parameter values??? or jobIndex??
        public Map<Variable, double[]> varData = new LinkedHashMap<>();
    }

    public List<Hdf5JobData> jobData = new ArrayList<>();
}
