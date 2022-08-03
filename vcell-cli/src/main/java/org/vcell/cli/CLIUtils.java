package org.vcell.cli;

import cbit.util.xml.VCLogger;
import cbit.vcell.export.server.ExportConstants;
import cbit.vcell.export.server.ExportFormat;
import cbit.vcell.export.server.ExportOutput;
import cbit.vcell.export.server.ExportServiceImpl;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.export.server.FileDataContainerManager;
import cbit.vcell.export.server.FormatSpecificSpecs;
import cbit.vcell.export.server.GeometrySpecs;
import cbit.vcell.export.server.JobRequest;
import cbit.vcell.export.server.TimeSpecs;
import cbit.vcell.export.server.VariableSpecs;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.export.server.ASCIIExporter;
import cbit.vcell.export.server.ASCIISpecs;
import cbit.vcell.export.server.ASCIISpecs.csvRoiLayout;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SimpleSymbolTable;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.SpatialSelection;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.OutputFunctionContext;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.util.ColumnDescription;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jlibsedml.*;
import org.jlibsedml.execution.IXPathToVariableIDResolver;
import org.jlibsedml.modelsupport.SBMLSupport;
import org.vcell.stochtest.TimeSeriesMultitrialData;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

import com.google.common.io.Files;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

//import java.nio.file.Files;

public class CLIUtils {
    // Simulation Status enum

    public static boolean removeDirs(File f) {
        try {
            CLIUtils.deleteRecursively(f);
        } catch (IOException ex) {
            System.err.println("Failed to delete the file: " + f);
            return false;
        }
        return true;
    }

    private static void deleteRecursively(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : Objects.requireNonNull(f.listFiles())) {
                CLIUtils.deleteRecursively(c);
            }
        }
        if (!f.delete()) {
            throw new FileNotFoundException("Failed to delete file: " + f);
        }
    }

    public static boolean isBatchExecution(String outputBaseDir, boolean bForceKeepLogs) {
        Path path = Paths.get(outputBaseDir);
        boolean isDirectory = java.nio.file.Files.isDirectory(path);
        return isDirectory || bForceKeepLogs;
    }

    public static void createHeader(File outputFilePath, boolean bForceLogFiles) {
        /**
         * Header Components:
         *  * base name of the omex file
         *  *   foreach sed-ml file:
         *  *   - (current) sed-ml file name
         *  *   - error(s) (if any)
         *  *   - number of models
         *  *   - number of sims
         *  *   - number of tasks
         *  *   - number of outputs
         *  *   - number of biomodels
         *  *   - number of succesful sims that we managed to run
         *  *   (NB: we assume that the # of failures = # of tasks - # of successful simulations)
         *  *   (NB: if multiple sedml files in the omex, we display on multiple rows, one for each sedml)
         */

        String header = "BaseName,SedML,Error,Models,Sims,Tasks,Outputs,BioModels,NumSimsSuccessful";
        try {
            CLIUtils.writeDetailedResultList(outputFilePath.getAbsolutePath(), header, bForceLogFiles);
        } catch (IOException e1) {
            // not big deal, we just failed to make the header; we'll find out later what went wrong
            e1.printStackTrace();
        }
    }

    public static void writeDetailedErrorList(String outputBaseDir, String s, boolean bForceKeepLogs) throws IOException {
        if (isBatchExecution(outputBaseDir, bForceKeepLogs)) {
            String dest = outputBaseDir + File.separator + "detailedErrorLog.txt";
            java.nio.file.Files.write(Paths.get(dest), (s + "\n").getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
    }

    public static void writeFullSuccessList(String outputBaseDir, String s, boolean bForceLogFiles) throws IOException {
        if (CLIUtils.isBatchExecution(outputBaseDir, bForceLogFiles)) {
            String dest = outputBaseDir + File.separator + "fullSuccessLog.txt";
            java.nio.file.Files.write(Paths.get(dest), (s + "\n").getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
    }

    // we just make a list with the omex files that failed
    public static void writeErrorList(String outputBaseDir, String s, boolean bForceLogFiles) throws IOException {
        if (CLIUtils.isBatchExecution(outputBaseDir, bForceLogFiles)) {
            String dest = outputBaseDir + File.separator + "errorLog.txt";
            java.nio.file.Files.write(Paths.get(dest), (s + "\n").getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
    }

    public static void writeDetailedResultList(String outputBaseDir, String s, boolean bForceLogFiles) throws IOException {
        if (CLIUtils.isBatchExecution(outputBaseDir, bForceLogFiles)) {
            String dest = outputBaseDir + File.separator + "detailedResultLog.txt";
            java.nio.file.Files.write(Paths.get(dest), (s + "\n").getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
    }

    public static class LocalLogger extends VCLogger {
        @Override
        public void sendMessage(Priority p, ErrorType et, String message) throws Exception {
            System.out.println("LOGGER: msgLevel=" + p + ", msgType=" + et + ", " + message);
        }

        public void sendAllMessages() {
        }

        public boolean hasMessages() {
            return false;
        }
    }



}

