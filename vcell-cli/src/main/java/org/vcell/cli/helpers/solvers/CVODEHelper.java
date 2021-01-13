package org.vcell.cli.helpers.solvers;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverUtilities;
import cbit.vcell.solver.ode.CVodeFileWriter;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.util.ColumnDescription;
import org.vcell.cli.CLIUtils;
import org.vcell.cli.helpers.sbml.SBMLSolverHelper;
import org.vcell.util.exe.Executable;
import org.vcell.util.exe.ExecutableException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CVODEHelper {

    public static ODESolverResultSet solve(File outDir, String taskId, BioModel bioModel) throws ExpressionException {
        String docName = bioModel.getName();
        Simulation sim = bioModel.getSimulation(0);

        //Creating Solver input files
        File cvodeInputFile = new File(outDir, docName + SimDataConstants.CVODEINPUT_DATA_EXTENSION);
        PrintWriter cvodePW = null;
        try {
            cvodePW = new PrintWriter(cvodeInputFile);
        } catch (FileNotFoundException e) {
            System.err.println("Unable to find path, failed with err: " + e.getMessage());
        }
        SimulationJob simJob = new SimulationJob(sim, 0, null);
        SimulationTask simTask = new SimulationTask(simJob, 0);
        CVodeFileWriter cvodeFileWriter = new CVodeFileWriter(cvodePW, simTask);
        try {
            cvodeFileWriter.write();
            cvodePW.close();
        } catch (Exception e) {
            System.err.print("Failed to write CVODE solver input file, failed with err: " + e.getMessage());
        }

        // use the cvodeStandalone solver
//        String outDirPath = outDir.getAbsolutePath();
//        int indexOfLastSlash = outDirPath.lastIndexOf("/");
//        String task_name = outDirPath.substring(indexOfLastSlash + 1);
//        String idaFilePath = outDirPath.substring(0, indexOfLastSlash);

        File resultFile = new File(outDir, taskId + ".csv");
        String executableName = null;
        try {
            // we need to specify the vCell install dir in the Eclipse Debug configuration, as VM argument
            // so that the code next knows where to look for the solver
            // -Dvcell.installDir=G:\dan\jprojects\git\vcell
            // OR
            // just type the string with the full path explicitly
            // OR
            // provide a .properties file in the working directory
            executableName = SolverUtilities.getExes(SolverDescription.CVODE)[0].getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException("failed to get executable for solver " + SolverDescription.CVODE.getDisplayLabel() + ": " + e.getMessage(), e);
        }
        Executable executable = new Executable(new String[]{executableName, cvodeInputFile.getAbsolutePath(), resultFile.getAbsolutePath()});
        try {
            executable.start();
        } catch (ExecutableException e) {
            System.err.print("Unable to execute solver executable, failed with err: " + e.getMessage());
        }
        ODESolverResultSet odeSolverResultSet = SBMLSolverHelper.getODESolverResultSet(simJob, resultFile.getPath());
        cvodeInputFile.delete();
//        CLIUtils.convertIDAtoCSV(resultFile);
        CLIUtils.createCSVFromODEResultSet(odeSolverResultSet, resultFile);
        //  CLIUtils.removeIntermediarySimFiles(outDir);
        return odeSolverResultSet;
    }
}
