package org.vcell.cli.run;

import cbit.util.xml.VCLogger;
import cbit.util.xml.VCLoggerException;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.solver.*;
import cbit.vcell.solver.ode.AbstractJavaSolver;
import cbit.vcell.solver.ode.CVodeSolverStandalone;
import cbit.vcell.solver.ode.ODESolver;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.server.Solver;
import cbit.vcell.solver.server.SolverFactory;
import cbit.vcell.solver.server.SolverStatus;
import cbit.vcell.solver.stoch.GibsonSolver;
import cbit.vcell.solver.stoch.HybridSolver;
import cbit.vcell.solvers.AbstractCompiledSolver;
import cbit.vcell.solvers.FVSolverStandalone;
import cbit.vcell.xml.ExternalDocInfo;
import cbit.vcell.xml.XmlHelper;
import org.jlibsedml.SedML;
import org.jlibsedml.Task;
import org.jlibsedml.UniformTimeCourse;
import org.python.core.Py;
import org.vcell.cli.CLIUtils;
import org.vcell.cli.vcml.VCMLHandler;
import org.vcell.sbml.vcell.SBMLImportException;
import org.vcell.sbml.vcell.SBMLImporter;
import org.vcell.util.document.VCDocument;
import org.vcell.util.exe.Executable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SolverHandler {
	
	public int countBioModels = 0;		// number of biomodels in this sedml file
	public int countSuccessfulSimulationRuns = 0;	// number of simulations that we ran successfully for this sedml file
	public Map <String, String> sim2Hdf5Map = new LinkedHashMap<> ();
	
    private static void sanityCheck(VCDocument doc) throws Exception {
        if (doc == null) {
            throw new Exception("Imported VCDocument is null.");
        }
        String docName = doc.getName();
        if (docName == null || docName.isEmpty()) {
            throw new Exception("The name of the imported VCDocument is null or empty.");
        }
        if (!(doc instanceof BioModel)) {
            throw new Exception("The imported VCDocument '" + docName + "' is not a BioModel.");
        }
        BioModel bioModel = (BioModel) doc;
        if (bioModel.getSimulationContext(0) == null) {
            throw new Exception("The imported VCDocument '" + docName + "' has no Application");
        }
        if (bioModel.getSimulation(0) == null) {
            throw new Exception("The imported VCDocument '" + docName + "' has no Simulation");
        }
    }


    public HashMap<String, ODESolverResultSet>
            simulateAllTasks(ExternalDocInfo externalDocInfo, SedML sedml,
                             File outputDirForSedml, String outDir, String outputBaseDir, String sedmlLocation,
                             boolean keepTempFiles, boolean exactMatchOnly, boolean bForceLogFiles) throws Exception {
        // create the VCDocument(s) (bioModel(s) + application(s) + simulation(s)), do sanity checks
        cbit.util.xml.VCLogger sedmlImportLogger = new LocalLogger();
        String inputFile = externalDocInfo.getFile().getAbsolutePath();
        String bioModelBaseName = org.vcell.util.FileUtils.getBaseName(inputFile);
        
        List<BioModel> bioModelList = null;
        // Key String is SEDML Task ID
        HashMap<String, ODESolverResultSet> resultsHash = new LinkedHashMap<String, ODESolverResultSet>();
        String docName = null;
        Simulation[] sims = null;
        String outDirRoot = outputDirForSedml.toString().substring(0, outputDirForSedml.toString().lastIndexOf(System.getProperty("file.separator")));
        try {
            bioModelList = XmlHelper.importSEDML(sedmlImportLogger, externalDocInfo, sedml, exactMatchOnly);
        } catch (Exception e) {
            System.err.println("Unable to Parse SED-ML into Bio-Model, failed with err: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        if(bioModelList != null) {
        	countBioModels = bioModelList.size();
        }

        int simulationCount = 0;
        int bioModelCount = 0;
        boolean hasSomeSpatial = false;
        boolean bTimeoutFound = false;
        
        for (BioModel bioModel : bioModelList) {
            try {
                sanityCheck(bioModel);
            } catch (Exception e) {
                e.printStackTrace(System.err);
//                continue;
            }
            docName = bioModel.getName();
            sims = bioModel.getSimulations();
            for (Simulation sim : sims) {
            	if(sim.getImportedTaskID() == null) {
            		// this is a simulation not matching the imported task, so we skip it
            		continue;
            	}
            	String logTaskMessage = "Initializing simulation... ";
            	String logTaskError = "";
                long startTimeTask = System.currentTimeMillis();

                SimulationTask simTask;
                String kisao = "null";
            	ODESolverResultSet odeSolverResultSet = null;
            	SolverTaskDescription std = null;
            	SolverDescription sd = null;
            	int solverStatus = SolverStatus.SOLVER_READY;
            	
                try {
                	SimulationOwner so = sim.getSimulationOwner();
                	sim = new TempSimulation(sim, false);
                	sim.setSimulationOwner(so);
                	
                	std = sim.getSolverTaskDescription();
                	sd = std.getSolverDescription();
                	kisao = sd.getKisao();
                	if(kisao == null) {
                		throw new RuntimeException("KISAO is null.");
                	}
                	SimulationJob simJob = new SimulationJob(sim, 0, null);
                	simTask = new SimulationTask(simJob, 0);
                	Solver solver = SolverFactory.createSolver(outputDirForSedml, simTask, false);
                	logTaskMessage += "done. Starting simulation... ";

                	if(sd.isSpatial()) {
                		hasSomeSpatial = true;
                	}
//                	if(solver instanceof FVSolverStandalone) {
//                		hasSomeSpatial = true;
//                		throw new RuntimeException("FVSolverStandalone timeout failure.");
//                	} 
//                	else 
                	if (solver instanceof AbstractCompiledSolver) {
                        ((AbstractCompiledSolver) solver).runSolver();
                        System.out.println(solver);
                        System.out.println(solver.getSolverStatus());
						if (solver instanceof ODESolver) {
                            odeSolverResultSet = ((ODESolver) solver).getODESolverResultSet();
                        } else if (solver instanceof GibsonSolver) {
                            odeSolverResultSet = ((GibsonSolver) solver).getStochSolverResultSet();
                        } else if (solver instanceof HybridSolver) {
                            odeSolverResultSet = ((HybridSolver) solver).getHybridSolverResultSet();
                        } else {
                        	String str = "Solver results are not compatible with CSV format. ";
                            System.err.println(str);
//                            keepTempFiles = true;		// temp fix for Jasraj
//                        	throw new RuntimeException(str);
                        }
                    } else if (solver instanceof AbstractJavaSolver) {
                        ((AbstractJavaSolver) solver).runSolver();
                        odeSolverResultSet = ((ODESolver) solver).getODESolverResultSet();
                        // must interpolate data for uniform time course which is not supported natively by the Java solvers
                        Task task = (Task) sedml.getTaskWithId(sim.getImportedTaskID());
                        assert task != null;
                        org.jlibsedml.Simulation sedmlSim = sedml.getSimulation(task.getSimulationReference());
                        if (sedmlSim instanceof UniformTimeCourse) {
                            odeSolverResultSet = RunUtils.interpolate(odeSolverResultSet, (UniformTimeCourse) sedmlSim);
                            logTaskMessage += "done. Interpolating... ";
                        }
                    } else {
                        // this should actually never happen...
                    	String str = "Unexpected solver: " + kisao + " " + solver + ". ";
                        throw new RuntimeException(str);
                    }
                   
                    if (solver.getSolverStatus().getStatus() == SolverStatus.SOLVER_FINISHED) {
                    	
                    	if(sd.isSpatial()) {
                    		
                    	// TODO: proper file name (results folder + importedTaskId + ".h5")
                    	// check if successfully created
//                        File aaa = new File("C:\\TEMP\\aaa.hdf5");
//                        try {
//                        	CLIUtils.exportPDE2HDF5(sim, outputDirForSedml, aaa);
//                        	sim2Hdf5Map.put(sim.getImportedTaskID(), null);
//                        } catch(Exception e) {
//                        	sim2Hdf5Map.put(sim.getImportedTaskID(), null);
//                        }
                    		
                    	}

                    	
                    	logTaskMessage += "done. ";
                        System.out.println("Succesful execution: Model '" + docName + "' Task '" + sim.getDescription() + "'.");

                        long endTimeTask = System.currentTimeMillis();
                		long elapsedTime = endTimeTask - startTimeTask;
                		int duration = (int)Math.ceil(elapsedTime /1000.0);

                		String msg = "Running simulation " + simTask.getSimulation().getName() + ", " + elapsedTime + " ms";
                		System.out.println(msg);
                		countSuccessfulSimulationRuns++;	// we only count the number of simulations (tasks) that succeeded
                		PythonCalls.updateTaskStatusYml(sedmlLocation, sim.getImportedTaskID(), Status.SUCCEEDED, outDir ,duration + "", kisao);
                		PythonCalls.setOutputMessage(sedmlLocation, sim.getImportedTaskID(), outDir, "task", logTaskMessage);
                        RunUtils.drawBreakLine("-", 100);
                    } else {
                    	solverStatus = solver.getSolverStatus().getStatus();
                        System.err.println("Solver status: " + solverStatus);
                        System.err.println("Solver message: " + solver.getSolverStatus().getSimulationMessage().getDisplayMessage());
                        String error = solver.getSolverStatus().getSimulationMessage().getDisplayMessage() + " ";
                        throw new RuntimeException(error);
                    }
//                    CLIUtils.finalStatusUpdate( CLIUtils.Status.SUCCEEDED, outDir);
                } catch (Exception e) {
                	
//                    File aaa = new File("C:\\TEMP\\aaa.hdf5");
//                    CLIUtils.exportPDE2HDF5(sim, outputDirForSedml, aaa);

                	
                	String error = "Failed execution: Model '" + docName + "' Task '" + sim.getDescription() + "'. ";
                    System.err.println(error);
                    
                    long endTime = System.currentTimeMillis();
            		long elapsedTime = endTime - startTimeTask;
            		int duration = (int)Math.ceil(elapsedTime /1000.0);
            		String msg = "Running simulation for " + elapsedTime + " ms";
            		System.out.println(msg);
                    
            		if(sim.getImportedTaskID() == null) {
            			String str = "'null' imported task id, this should never happen. ";
            			System.err.println();
            			logTaskError += str;
            		} else {
            			if(solverStatus == SolverStatus.SOLVER_ABORTED) {
            				PythonCalls.updateTaskStatusYml(sedmlLocation, sim.getImportedTaskID(), Status.ABORTED, outDir ,duration + "", kisao);
            			} else {
            				PythonCalls.updateTaskStatusYml(sedmlLocation, sim.getImportedTaskID(), Status.FAILED, outDir ,duration + "", kisao);
            			}
            		}
//                    CLIUtils.finalStatusUpdate(CLIUtils.Status.FAILED, outDir);
                    if (e.getMessage() != null) {
                        // something else than failure caught by solver instance during execution
                    	logTaskError += (e.getMessage() + ". ");
                        System.err.println(e.getMessage());
                    } else {
                    	logTaskError += (error + ". ");
                    }
                    String type = e.getClass().getSimpleName();
                    PythonCalls.setOutputMessage(sedmlLocation, sim.getImportedTaskID(), outDir, "task", logTaskMessage);
                    PythonCalls.setExceptionMessage(sedmlLocation, sim.getImportedTaskID(), outDir, "task", type, logTaskError);
                    String sdl = "";
                    if(sd != null && sd.getShortDisplayLabel() != null && !sd.getShortDisplayLabel().isEmpty()) {
                    	sdl = sd.getShortDisplayLabel();
                    } else {
                    	sdl = kisao;
                    }
                    if(logTaskError.contains("Process timed out")) {
                    	if(bTimeoutFound == false) {		// don't repeat this for each task
                    		String str = logTaskError.substring(0, logTaskError.indexOf("Process timed out"));
                    		str += "Process timed out";		// truncate the rest of the spam
                        	CLIUtils.writeDetailedErrorList(outputBaseDir, bioModelBaseName + ",  solver: " + sdl + ": " + type + ": " + str, bForceLogFiles);
                        	bTimeoutFound = true;
                    	}
                    } else {
                    	CLIUtils.writeDetailedErrorList(outputBaseDir, bioModelBaseName + ",  solver: " + sdl + ": " + type + ": " + logTaskError, bForceLogFiles);
                    }
                    RunUtils.drawBreakLine("-", 100);
                }
                if(odeSolverResultSet != null) {
                    resultsHash.put(sim.getImportedTaskID(), odeSolverResultSet);
                } else {
                	resultsHash.put(sim.getImportedTaskID(), null);	// if any task fails, we still put it in the hash with a null value
                }
                if(keepTempFiles == false) {
                	RunUtils.removeIntermediarySimFiles(outputDirForSedml);
                }
                simulationCount++;
            }
            bioModelCount++;
        }
        System.out.println("Ran " + simulationCount + " simulations for " + bioModelCount + " biomodels.");
        if(hasSomeSpatial) {
        	writeSpatialList(outputBaseDir, bioModelBaseName, bForceLogFiles);
        }
        return resultsHash;
    }

    @Deprecated
    public HashMap<String, ODESolverResultSet> simulateAllVcmlTasks(File vcmlPath, File outputDir) throws Exception {
        // create the VCDocument(s) (bioModel(s) + application(s) + simulation(s)), do sanity checks
        List<VCDocument> docs = null;
        // Key String is SEDML Task ID
        HashMap<String, ODESolverResultSet> resultsHash = new LinkedHashMap<String, ODESolverResultSet>();
        String biomodelName = null;
        BioModel bioModel = null;
        Simulation[] sims = null;
        VCDocument singleDoc = null;
        try {
            singleDoc = VCMLHandler.convertVcmlToVcDocument(vcmlPath);
        } catch (Exception e) {
            System.err.println("Unable to Parse SED-ML into Bio-Model, failed with err: " + e.getMessage());
            throw e;
        }
        try {
            sanityCheck(singleDoc);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        assert singleDoc != null;
        biomodelName = singleDoc.getName();
        bioModel = (BioModel) singleDoc;
        sims = bioModel.getSimulations();
        for (Simulation sim : sims) {
            sim = new TempSimulation(sim, false);
            SolverTaskDescription std = sim.getSolverTaskDescription();
            SolverDescription sd = std.getSolverDescription();
            String kisao = sd.getKisao();
            SimulationJob simJob = new SimulationJob(sim, 0, null);
            SimulationTask simTask = new SimulationTask(simJob, 0);
            Solver solver = SolverFactory.createSolver(outputDir, simTask, false);
            ODESolverResultSet odeSolverResultSet = null;
            try {
                if (solver instanceof AbstractCompiledSolver) {
                    ((AbstractCompiledSolver) solver).runSolver();
                    if (solver instanceof ODESolver) {
                        odeSolverResultSet = ((ODESolver) solver).getODESolverResultSet();
                    } else if (solver instanceof GibsonSolver) {
                        odeSolverResultSet = ((GibsonSolver) solver).getStochSolverResultSet();
                    } else if (solver instanceof HybridSolver) {
                        odeSolverResultSet = ((HybridSolver) solver).getHybridSolverResultSet();
                    } else {
                        System.err.println("Solver results are not compatible with CSV format");
                    }
                    //TODO: Add support for JAVA solvers and implement interpolation

//                        odeSolverResultSet = CLIUtils.interpolate(odeSolverResultSet, (UniformTimeCourse) sedmlSim);

                } else {
                    // this should actually never happen...
                    throw new Exception("Unexpected solver: " + kisao + " " + solver);
                }
                if (solver.getSolverStatus().getStatus() == SolverStatus.SOLVER_FINISHED) {
                    System.out.println("Succesful execution: Model '" + biomodelName + "' Task '" + sim.getDescription() + "'.");

                } else {
                    System.err.println("Solver status: " + solver.getSolverStatus().getStatus());
                    System.err.println("Solver message: " + solver.getSolverStatus().getSimulationMessage().getDisplayMessage());
                    throw new Exception();
                }

            } catch (Exception e) {
                System.err.println("Failed execution: Model '" + biomodelName + "' Task '" + sim.getDescription() + "'.");

                if (e.getMessage() != null) {
                    // something else than failure caught by solver instance during execution
                    System.err.println(e.getMessage());
                }
            }
            if(odeSolverResultSet != null) {
                resultsHash.put(sim.getName(), odeSolverResultSet);
            }

            RunUtils.removeIntermediarySimFiles(outputDir);

        }
        return resultsHash;
    }

    ;

    // TODO: Complete this logger and use it for whole CLI
    private static class LocalLogger extends VCLogger {
        @Override
        public void sendMessage(Priority p, ErrorType et, String message) throws VCLoggerException {
            System.out.println("LOGGER: msgLevel=" + p + ", msgType=" + et + ", " + message);
            if (p == VCLogger.Priority.HighPriority) {
                SBMLImportException.Category cat = SBMLImportException.Category.UNSPECIFIED;
                if (message.contains(SBMLImporter.RESERVED_SPATIAL)) {
                    cat = SBMLImportException.Category.RESERVED_SPATIAL;
                }
                throw new VCLoggerException(new SBMLImportException(message, cat));
            }
        }

        public void sendAllMessages() {
        }

        public boolean hasMessages() {
            return false;
        }
    }
    
    //
    // Running python interactively from java code
    // Proof of concept 
    //
    public static void main(String[] args) throws Exception {  
    	
    	ProcessBuilder processBuilder = new ProcessBuilder("cmd");
    	processBuilder.redirectErrorStream(true);
    	processBuilder.start();

    	/*
    	 * Interprocess communication with Python - proof of concept
    	 * Possible race conditions
    	 */
    	processBuilder.command("python","-i");
    	Process pythonProcess = processBuilder.start();
    	OutputStream outputStream = pythonProcess.getOutputStream();
    	OutputStreamWriter osw = new OutputStreamWriter(outputStream);
    	InputStream inputStream = pythonProcess.getInputStream();
    	BufferedReader bufferedReader = new BufferedReader(
    	                  new InputStreamReader(inputStream));

    	String line = "";

    	osw.write("2+2\r\n");
//    	osw.write("2+2\r\nquit()\r\n");
    	osw.flush();
//    	osw.close();
//    	InputStream inputStream = pythonProcess.getInputStream();
//    	BufferedReader bufferedReader = new BufferedReader(
//    	                  new InputStreamReader(inputStream));
//    	String line;
//    	while( (line=bufferedReader.readLine())!=null) {
//    	    System.out.println(line);
//    	}
//	    System.out.println(line);
	    
    	osw.write("3+2\r\n");
    	osw.flush();
//    	InputStream inputStream = pythonProcess.getInputStream();
//    	BufferedReader bufferedReader = new BufferedReader(
//    	                  new InputStreamReader(inputStream));

    	osw.write("4+2\r\n");
    	osw.flush();
    	
    	osw.write("5+2\r\n");
    	osw.flush();

        Thread.sleep(1000);

    	osw.write("6+2\r\n");
    	osw.write("9\r\nquit()\r\n");
    	osw.flush();
    	osw.close();

    	line = "";
    	while( (line=bufferedReader.readLine())!=null) {
    	    System.out.println(line);
    	}
    	System.out.println("done");
        	
    	
    	/*
    	
    	
        ProcessBuilder pb;
                pb = new ProcessBuilder("C:/Users/Motan/AppData/Local/Programs/Python/Python39/python.exe", "-qi", "/dev/null");

                Process p = pb.start();

    	
    	
        char[] readBuffer = new char[1000];
        InputStreamReader isr = new InputStreamReader(p.getInputStream());
        BufferedReader br = new BufferedReader(isr);
        int charCount;
        boolean written = false;
        while(true) {
            if (!br.ready() && !written) {
                // Ugly. Should be reading for '>>>' prompt then writing.
                Thread.sleep(1000);
                if (!written) {
                    written = true;
                    OutputStream os = p.getOutputStream();
                    OutputStreamWriter osw = new OutputStreamWriter(os);
                    BufferedWriter bw = new BufferedWriter(osw);
                    bw.write("2+2");
                    bw.newLine();
                    //bw.write("quit()");
                    bw.newLine();
                    bw.flush();
                    bw.close();
                }
                continue;
            }
            charCount = br.read(readBuffer);
            if (charCount > 0)
                System.out.print(new String(readBuffer, 0, charCount));
            else
                break;
        }

//        br = new BufferedReader(isr);
        written = false;
        while(true) {
            if (!br.ready() && !written) {
                // Ugly. Should be reading for '>>>' prompt then writing.
                Thread.sleep(1000);
                if (!written) {
                    written = true;
                    OutputStream os2 = p.getOutputStream();
                    OutputStreamWriter osw2 = new OutputStreamWriter(os2);
                    BufferedWriter bw2 = new BufferedWriter(osw2);
                    bw2.write("3+3");
                    bw2.newLine();
                    bw2.write("quit()");
                    bw2.newLine();
                    bw2.flush();
                    bw2.close();
                }
                continue;
            }
            charCount = br.read(readBuffer);
            if (charCount > 0)
                System.out.print(new String(readBuffer, 0, charCount));
            else
                break;
        }

*/
    	

    }

    // we make a list with the omex files that contain (some) spatial simulations (FVSolverStandalone solver)
    static void writeSpatialList(String outputBaseDir, String s, boolean bForceLogFile) throws IOException {
        if (CLIUtils.isBatchExecution(outputBaseDir, bForceLogFile)) {
            String dest = outputBaseDir + File.separator + "hasSpatialLog.txt";
            Files.write(Paths.get(dest), (s + "\n").getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
    }

}

