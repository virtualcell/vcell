package org.vcell.sbml;

import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.*;
import cbit.vcell.solver.ode.ODESolver;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.server.SolverFactory;
import cbit.vcell.solver.server.SolverStatus;
import cbit.vcell.solver.stoch.GibsonSolver;
import cbit.vcell.solver.stoch.HybridSolver;
import cbit.vcell.solvers.AbstractCompiledSolver;

import java.io.File;

public class SBMLSolver {
    private static class TempSimulationJob extends SimulationJob {
        public TempSimulationJob(TempSimulation argSim, int jobIndex, FieldDataIdentifierSpec[] argFDIS) {
            super(argSim, jobIndex, argFDIS);
        }
        @Override
        public TempSimulation getSimulation() {
            return (TempSimulation)super.getSimulation();
        }
        public Simulation getOrigSimulation() {
            return getSimulation().getOriginalSimulation();
        }
        public TempSimulation getTempSimulation() {
            return getSimulation();
        }
    }

    public SBMLResults simulate(File workingDir, Simulation simulation, SBMLSimulationSpec sbmlSimulationSpec) throws SolverException, ExpressionException {
        TempSimulation tempSimulation = new TempSimulation(simulation,false);
        tempSimulation.setSimulationOwner(simulation.getSimulationOwner());
        TempSimulationJob tempSimulationJob = new TempSimulationJob(tempSimulation, 0, null);

        SimulationTask simTask = new SimulationTask(tempSimulationJob, 0);
        AbstractCompiledSolver solver = (AbstractCompiledSolver) SolverFactory.createSolver(workingDir, simTask, false);
        solver.runSolver();

        if (solver.getSolverStatus().getStatus() != SolverStatus.SOLVER_FINISHED) {
            throw new RuntimeException("Solver status: "+solver.getSolverStatus().getStatus()+", "+solver.getSolverStatus().getSimulationMessage());
        }

        final ODESolverResultSet odeSolverResultSet;
        if (solver instanceof ODESolver) {
            odeSolverResultSet = ((ODESolver) solver).getODESolverResultSet();
        } else if (solver instanceof GibsonSolver) {
            odeSolverResultSet = ((GibsonSolver) solver).getStochSolverResultSet();
        } else if (solver instanceof HybridSolver) {
            odeSolverResultSet = ((HybridSolver) solver).getHybridSolverResultSet();
        } else {
            throw new RuntimeException("Solver results are not compatible with CSV format. ");
        }

        return SBMLResults.fromOdeSolverResultSet(odeSolverResultSet, sbmlSimulationSpec);
    }

}
