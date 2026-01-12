package cbit.vcell.simdata;


import cbit.vcell.solver.ode.ODESimData;

import java.io.Serializable;

public record LangevinBatchResultSet (
        ODESimData odeSimDataMean,
        ODESimData odeSimDataMax,
        ODESimData odeSimDataMin,
        ODESimData odeSimDataStd) implements Serializable {

}
