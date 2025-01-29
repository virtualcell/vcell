package org.vcell.sbml;

import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.TempSimulation;
import cbit.vcell.solvers.FVSolverStandalone;

import java.io.File;

public class SBMLFakeSpatialBioModel {

    public static class MyFiniteVolumeSolver extends FVSolverStandalone {
        public MyFiniteVolumeSolver(SimulationTask simulationTask, File dataDir) throws SolverException {
            super(simulationTask, dataDir, false);
        }
        @Override
        public void initialize() throws SolverException {
            super.initialize();
        }
    }

    private static class TempSimulationJob extends SimulationJob {
        public TempSimulationJob(TempSimulation argSim, int jobIndex, FieldDataIdentifierSpec[] argFDIS) {
            super(argSim, jobIndex, argFDIS);
        }
        @Override
        public TempSimulation getSimulation() {
            return (TempSimulation)super.getSimulation();
        }
    }

    public static void simulate(File workingDir, Simulation simulation) throws SolverException, ExpressionException {

        TempSimulation tempSimulation = new TempSimulation(simulation,false);
        tempSimulation.setSimulationOwner(simulation.getSimulationOwner());
        TempSimulationJob tempSimulationJob = new TempSimulationJob(tempSimulation, 0, null);

        SimulationTask simTask = new SimulationTask(tempSimulationJob, 0);
        MyFiniteVolumeSolver solver = new MyFiniteVolumeSolver(simTask, workingDir);
        solver.runSolver();
    }

    public static void writeInputFilesOnly(File workingDir, Simulation simulation) throws SolverException, ExpressionException {

        TempSimulation tempSimulation = new TempSimulation(simulation,false);
        tempSimulation.setSimulationOwner(simulation.getSimulationOwner());
        TempSimulationJob tempSimulationJob = new TempSimulationJob(tempSimulation, 0, null);

        SimulationTask simTask = new SimulationTask(tempSimulationJob, 0);
        MyFiniteVolumeSolver solver = new MyFiniteVolumeSolver(simTask, workingDir);
        solver.initialize();
    }
}
