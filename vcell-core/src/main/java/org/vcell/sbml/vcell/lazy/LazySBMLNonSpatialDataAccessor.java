package org.vcell.sbml.vcell.lazy;

import org.vcell.sbml.vcell.SBMLDataRecord;

import java.util.List;
import java.util.concurrent.Callable;

public class LazySBMLNonSpatialDataAccessor extends LazySBMLDataAccessor {

    public LazySBMLNonSpatialDataAccessor(Callable<SBMLDataRecord> lazyMethod, int flatSize, List<Double> desiredTimes) {
        this(lazyMethod, flatSize, desiredTimes, true);
    }

    public LazySBMLNonSpatialDataAccessor(Callable<SBMLDataRecord> lazyMethod, int flatSize, List<Double> desiredTimes, boolean shouldUseCaching) {
        super(lazyMethod, flatSize, List.of(1), desiredTimes, shouldUseCaching);
    }
}
