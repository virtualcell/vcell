package org.vcell.cli.helpers.solvers;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverUtilities;
import cbit.vcell.solver.ode.IDAFileWriter;
import cbit.vcell.solver.ode.ODESolverResultSet;
import org.vcell.cli.CLIUtils;
import org.vcell.cli.helpers.sbml.SBMLSolverHelper;
import org.vcell.util.exe.Executable;
import org.vcell.util.exe.ExecutableException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class IDAHelper {

    public static ODESolverResultSet solve(File outDir, String taskId, BioModel bioModel) {
        String docName = bioModel.getName();
        Simulation sim = bioModel.getSimulation(0);
        File idaInputFile = new File(outDir, docName + SimDataConstants.IDAINPUT_DATA_EXTENSION);
        PrintWriter idaPW = null;
        try {
            idaPW = new PrintWriter(idaInputFile);
        } catch (FileNotFoundException e) {
            System.err.print("Unable to find idaInputPath, failed with err: " + e.getMessage());
        }
        SimulationJob simJob = new SimulationJob(sim, 0, null);
        SimulationTask simTask = new SimulationTask(simJob, 0);
        IDAFileWriter idaFileWriter = new IDAFileWriter(idaPW, simTask);
        try {
            idaFileWriter.write();
            idaPW.close();
        } catch (Exception e) {
            System.err.print("Failed to write IDA solver input file, failed with err: " + e.getMessage());
        }

        // use the idastandalone solver
        File idaOutputFile = new File(outDir, taskId + ".csv");
        String executableName = null;
        try {
            executableName = SolverUtilities.getExes(SolverDescription.IDA)[0].getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException("failed to get executable for solver " + SolverDescription.IDA.getDisplayLabel() + ": " + e.getMessage(), e);
        }
        Executable executable = new Executable(new String[]{executableName, idaInputFile.getAbsolutePath(), idaOutputFile.getAbsolutePath()});
        try {
            executable.start();
        } catch (ExecutableException e) {
            System.err.print("Unable to execute solver executable, failed with err: " + e.getMessage());
        }
        ODESolverResultSet odeSolverResultSet = SBMLSolverHelper.getODESolverResultSet(simJob, idaOutputFile.getPath());
        CLIUtils.removeIntermediarySimFiles(outDir);
        return odeSolverResultSet;
    }
}
