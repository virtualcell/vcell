package org.vcell.sbml.vcell.lazy;

import org.vcell.sbml.vcell.SBMLDataRecord;
import java.util.concurrent.Callable;

public class LazySBMLNonSpatialDataAccessor extends LazySBMLDataAccessor {

    public LazySBMLNonSpatialDataAccessor(Callable<SBMLDataRecord> lazyMethod, int flatSize) {
        this(lazyMethod, flatSize, true);
    }

    public LazySBMLNonSpatialDataAccessor(Callable<SBMLDataRecord> lazyMethod, int flatSize, boolean shouldUseCaching) {
        super(lazyMethod, flatSize, shouldUseCaching);
    }
}
