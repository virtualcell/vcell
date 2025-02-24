package org.vcell.sbml.vcell;

import java.util.List;

public record SBMLDataRecord(double[] data, List<Integer> dimensions, double[] times) {}
