package org.vcell.cli.run.results.solver;

import cbit.vcell.mapping.MathSymbolMapping;
import cbit.vcell.math.MathException;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.server.Solver;
import org.jlibsedml.UniformTimeCourse;
import org.vcell.sbml.vcell.SpatialSBMLSimResults;
import org.vcell.sbml.vcell.SBMLSymbolMapping;
import org.vcell.util.DataAccessException;

import java.io.File;
import java.io.IOException;

public class SolverSpatialExecutionRequest extends SolverExecutionRequest {
    private final SimulationJob simulationJob;
    private final File solverResultsDir;

    protected SolverSpatialExecutionRequest(Solver solverRequested, SolverDescription solverDescription, UniformTimeCourse utcSimRequested, SimulationJob simulationJob, File solverResultsDir, String sedmlLocation) {
        super(simulationJob, solverRequested, solverDescription, utcSimRequested, sedmlLocation);
        this.simulationJob = simulationJob;
        this.solverResultsDir = solverResultsDir;
        this.progressGeneralLog.append("Done!\n");
    }

    @Override
    public SpatialSBMLSimResults getResults(MathSymbolMapping mathMapping, SBMLSymbolMapping sbmlMapping) throws MathException, IOException, DataAccessException {
        return new SpatialSBMLSimResults(this.simulationJob, this.solverResultsDir, sbmlMapping, mathMapping);
    }
}
