package org.vcell.cli.helpers.solvers;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.math.MathException;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.parser.Discontinuity;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.OdeFileWriter;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverUtilities;
import org.vcell.cli.CLIUtils;
import org.vcell.cli.helpers.sbml.SBMLSolverHelper;
import org.vcell.util.exe.Executable;
import org.vcell.util.exe.ExecutableException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class RungeKuttaFelhbergHelper {

    public static ODESolverResultSet solve(File outDir, String taskId, BioModel bioModel){
        String docName = bioModel.getName();
        Simulation sim = bioModel.getSimulation(0);

        File rkfInputFile = new File(outDir, docName + SimDataConstants.ODE_DATA_EXTENSION);
        PrintWriter rkfPW = null;
        try {
            rkfPW = new PrintWriter(rkfInputFile);
        } catch (FileNotFoundException e) {
            System.err.print("Unable to find rkfInputFile, failed with err: " + e.getMessage());
        }
        SimulationJob simJob = new SimulationJob(sim, 0, null);
        SimulationTask simTask = new SimulationTask(simJob, 0);
        OdeFileWriter rkfFileWriter = new OdeFileWriter(rkfPW, simTask, false) {
        };
        try{
            rkfFileWriter.write();
            rkfPW.close();
        } catch (Exception e) {
            System.err.print("Failed to write Runge Kutta Felhberg solver input file, failed with err: " + e.getMessage());
        }

        File resultFile = new File(outDir, taskId + ".csv");
        String executableName = null;
        try {
            executableName = SolverUtilities.getExes(SolverDescription.RungeKuttaFehlberg)[0].getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException("failed to get executable for the solver " + SolverDescription.RungeKuttaFehlberg.getDisplayLabel() + ":" + e.getMessage(), e);
        }
        Executable executable = new Executable(new String[]{executableName, rkfInputFile.getAbsolutePath(), resultFile.getAbsolutePath()});
        try {
            executable.start();
        } catch (ExecutableException e) {
            System.err.print("Unable to execute solver executable, failed with err: " + e.getMessage());
        }
        ODESolverResultSet rkfSolverResultSet = SBMLSolverHelper.getODESolverResultSet(simJob, resultFile.getPath());
        rkfInputFile.delete();
        CLIUtils.convertIDAtoCSV(resultFile);
        return rkfSolverResultSet;
    }
}
