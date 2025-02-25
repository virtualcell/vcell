package org.vcell.sbml.vcell.lazy;

import org.vcell.sbml.vcell.SBMLDataRecord;
import java.util.concurrent.Callable;

public class LazySBMLSpatialDataAccessor extends LazySBMLDataAccessor {

    public LazySBMLSpatialDataAccessor(Callable<SBMLDataRecord> lazyMethod, int flatSize) {
        super(lazyMethod, flatSize, false); // Can't allow caching; data sets can be MASSIVE!
    }

}
