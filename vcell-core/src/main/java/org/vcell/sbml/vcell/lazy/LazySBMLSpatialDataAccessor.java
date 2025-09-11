package org.vcell.sbml.vcell.lazy;

import org.vcell.sbml.vcell.SBMLDataRecord;

import java.util.List;
import java.util.concurrent.Callable;

public class LazySBMLSpatialDataAccessor extends LazySBMLDataAccessor {

    public LazySBMLSpatialDataAccessor(Callable<SBMLDataRecord> lazyMethod, int flatSize, List<Integer> spatialDimensions, List<Double> desiredTimes) {
        super(lazyMethod, flatSize, spatialDimensions, desiredTimes, false); // Can't allow caching; data sets can be MASSIVE!
    }

}
