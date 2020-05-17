package org.vcell.cli.helpers.solvers;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverUtilities;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.stoch.StochFileWriter;
import org.vcell.cli.helpers.sbml.SBMLSolverHelper;
import org.vcell.util.exe.Executable;
import org.vcell.util.exe.ExecutableException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class StockGibsonHelper {

    public static ODESolverResultSet solve(File outDir, String taskId, BioModel bioModel) {
        String docName = bioModel.getName();
        Simulation sim = bioModel.getSimulation(0);
        File gibsonInputFile = new File(outDir, docName + SimDataConstants.STOCHINPUT_DATA_EXTENSION);
        PrintWriter gibsonPW = null;
        try {
            gibsonPW = new PrintWriter(gibsonInputFile);
        } catch (FileNotFoundException e) {
            System.err.println("Unable to find path, failed with err: " + e.getMessage());
        }
        SimulationJob simJob = new SimulationJob(sim, 0, null);
        SimulationTask simTask = new SimulationTask(simJob, 0);
        StochFileWriter stFileWriter = new StochFileWriter(gibsonPW, simTask, false);
        try {
            stFileWriter.write();
            gibsonPW.close();
        } catch (Exception e) {
            System.err.print("Failed to write Stock Gibson solver input file, failed with err: " + e.getMessage());
        }

        File gibsonOutputFile = new File(outDir, taskId + SimDataConstants.IDA_DATA_EXTENSION);

//        writeFunctionFile(outDir, docName, SimDataConstants.FUNCTIONFILE_EXTENSION, simTask);

        String executableName = null;
        try {
            executableName = SolverUtilities.getExes(SolverDescription.StochGibson)[0].getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException("failed to get executable for solver "+SolverDescription.StochGibson.getDisplayLabel()+": "+e.getMessage(),e);
        }
        Executable executable = new Executable(new String[]{executableName, "gibson", gibsonInputFile.getAbsolutePath(), gibsonOutputFile.getAbsolutePath()});
        try {
            executable.start();
        } catch (ExecutableException e) {
            System.err.print("Unable to execute solver executable, failed with err: " + e.getMessage());
        }
        ODESolverResultSet odeSolverResultSet = SBMLSolverHelper.getODESolverResultSet(simJob, gibsonOutputFile.getPath());
        return odeSolverResultSet;
    }
}
