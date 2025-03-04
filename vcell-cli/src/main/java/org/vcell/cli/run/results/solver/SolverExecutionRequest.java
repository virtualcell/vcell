package org.vcell.cli.run.results.solver;

import cbit.vcell.mapping.MathSymbolMapping;
import cbit.vcell.math.MathException;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.ode.AbstractJavaSolver;
import cbit.vcell.solver.server.Solver;
import cbit.vcell.solver.server.SolverFactory;
import cbit.vcell.solvers.AbstractCompiledSolver;
import org.jlibsedml.UniformTimeCourse;
import org.vcell.sbml.vcell.SBMLSimResults;
import org.vcell.sbml.vcell.SBMLSymbolMapping;
import org.vcell.sedml.log.BiosimulationLog;
import org.vcell.util.DataAccessException;

import java.io.File;
import java.io.IOException;

public abstract class SolverExecutionRequest {
    public final StringBuilder progressGeneralLog, progressErrLog;
    public final Solver solver;
    protected final SolverDescription solverDescription;
    protected final org.jlibsedml.Simulation simulation;
    protected final org.jlibsedml.UniformTimeCourse utcSim;
    private final String sedmlLocation;
    private final String entityId;

    public static SolverExecutionRequest createNewExecutionRequest(SimulationJob simulationJob, SolverDescription solverDescription, SimulationTask simTask, UniformTimeCourse utcSimRequested, File outputDirForSedml, String sedmlLocation) throws SolverException {
        Solver solver = SolverFactory.createSolver(outputDirForSedml, simTask, false);
        return solverDescription.isSpatial() ?
                new SolverSpatialExecutionRequest(solver, solverDescription, utcSimRequested, simulationJob, outputDirForSedml, sedmlLocation):
                new SolverNonSpatialExecutionRequest(solver, solverDescription, utcSimRequested, simulationJob, sedmlLocation);
    }

    protected SolverExecutionRequest(SimulationJob simulationJob, Solver solverRequested, SolverDescription solverDescription, UniformTimeCourse utcSimRequested, String sedmlLocation){
        this.progressGeneralLog = new StringBuilder("Building SolverExecutionRequest...");
        this.progressErrLog = new StringBuilder();
        if (!(solverRequested instanceof AbstractJavaSolver || solverRequested instanceof AbstractCompiledSolver)) throw new IllegalArgumentException("Solver provided not accepted");
        this.solver = solverRequested;
        this.solverDescription = solverDescription;
        this.simulation = utcSimRequested;
        this.utcSim = utcSimRequested;
        this.sedmlLocation = sedmlLocation;
        this.entityId = simulationJob.getSimulation().getImportedTaskID();
    }

    public abstract SBMLSimResults getResults(MathSymbolMapping mathMapping, SBMLSymbolMapping sbmlMapping) throws ExpressionException, MathException, IOException, DataAccessException;

    public void bioSimLogSetOutputMessage(String message){
        BiosimulationLog.instance().setOutputMessage(this.sedmlLocation, this.entityId, "task", message);
    }

    public void bioSimLogSetOutputMessage(String message, Exception e){
        String exceptionName = e == null ? "<No Exception>": e.getClass().getSimpleName();
        BiosimulationLog.instance().setExceptionMessage(this.sedmlLocation, this.entityId, "task", exceptionName, message);
    }
}
