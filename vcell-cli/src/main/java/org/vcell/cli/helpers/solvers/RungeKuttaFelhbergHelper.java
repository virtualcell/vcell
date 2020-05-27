package org.vcell.cli.helpers.solvers;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.math.MathException;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.parser.Discontinuity;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.solver.*;
import cbit.vcell.solver.ode.*;
import cbit.vcell.solver.server.Solver;
import org.vcell.cli.CLIUtils;
import org.vcell.cli.helpers.sbml.SBMLSolverHelper;
import org.vcell.util.exe.Executable;
import org.vcell.util.exe.ExecutableException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import static cbit.vcell.simdata.SimDataConstants.LOGFILE_EXTENSION;
import static cbit.vcell.simdata.SimDataConstants.ODE_DATA_EXTENSION;

public class RungeKuttaFelhbergHelper {

    public static void solve(File outDir, String taskId, BioModel bioModel){
        String docName = bioModel.getName();
        Simulation sim = bioModel.getSimulation(0);

        SimulationJob simJob = new SimulationJob(sim, 0, null);
        SimulationTask simTask = new SimulationTask(simJob, 0);


        try {
            Solver solver = new RungeKuttaFehlbergSolver(simTask, outDir);
            solver.startSolver();

        } catch (SolverException e) {
            System.err.println("Unable to run simulation with RungeKuttaFehlbergSolver: " + e.getMessage());
        }
    }
}
