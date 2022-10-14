package org.vcell.cli.run.hdf5;

import org.jlibsedml.Variable;

import java.util.LinkedHashMap;
import java.util.Map;

public class Hdf5JobData {
    // metadata?  parameter values??? or jobIndex??
    public Map<Variable, double[]> varData = new LinkedHashMap<>();
}
