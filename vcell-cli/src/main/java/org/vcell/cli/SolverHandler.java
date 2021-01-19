package org.vcell.cli;

import cbit.util.xml.VCLogger;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.ode.AbstractJavaSolver;
import cbit.vcell.solver.ode.CVodeSolverStandalone;
import cbit.vcell.solver.ode.ODESolver;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.server.Solver;
import cbit.vcell.solver.server.SolverFactory;
import cbit.vcell.solver.stoch.GibsonSolver;
import cbit.vcell.solver.stoch.HybridSolver;
import cbit.vcell.solvers.AbstractCompiledSolver;
import cbit.vcell.solvers.AbstractSolver;
import cbit.vcell.solvers.SimpleCompiledSolver;
import cbit.vcell.xml.ExternalDocInfo;
import cbit.vcell.xml.XmlHelper;
import org.jlibsedml.SedML;
import org.jlibsedml.Task;
import org.jlibsedml.UniformTimeCourse;
import org.vcell.cli.helpers.solvers.*;
import org.vcell.sbml.vcell.SBMLImportException;
import org.vcell.sbml.vcell.SBMLImporter;
import org.vcell.util.document.VCDocument;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class SolverHandler {

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

    public HashMap<String, ODESolverResultSet> simulateAllTasks(ExternalDocInfo externalDocInfo, SedML sedml, File outputDir) throws Exception {
        // create the VCDocument(s) (bioModel(s) + application(s) + simulation(s)), do sanity checks
        cbit.util.xml.VCLogger sedmlImportLogger = new LocalLogger();
        List<VCDocument> docs = null;
        // Key String is SEDML Task ID
        HashMap<String, ODESolverResultSet> resultsHash = new LinkedHashMap<String, ODESolverResultSet>();
        String docName = null;
        BioModel bioModel = null;
        Simulation[] sims = null;
        try {
            docs = XmlHelper.sedmlToBioModel(sedmlImportLogger, externalDocInfo, sedml, null);
        } catch (Exception e) {
            System.err.println("Unable to Parse SEDML into biomodel, failed with err: " + e.getMessage());
            throw e;
        }
        for (VCDocument doc : docs) {
            try {
                sanityCheck(doc);
            } catch (Exception e) {
                e.printStackTrace(System.err);
                continue;
            }
            docName = doc.getName();
            bioModel = (BioModel) doc;
            sims = bioModel.getSimulations();
			for (Simulation sim : sims) {
				SolverTaskDescription std = sim.getSolverTaskDescription();
				SolverDescription sd = std.getSolverDescription();
				String kisao = sd.getKisao();
		        SimulationJob simJob = new SimulationJob(sim, 0, null);
		        SimulationTask simTask = new SimulationTask(simJob, 0);
		        Solver solver = SolverFactory.createSolver(outputDir, simTask, false);
		        ODESolverResultSet odeSolverResultSet = null;
				try {
			        if (solver instanceof AbstractCompiledSolver) {
			        	((AbstractCompiledSolver)solver).runSolver();
			        	if (solver instanceof ODESolver) {
			        		odeSolverResultSet = ((ODESolver)solver).getODESolverResultSet();
			        	} else if (solver instanceof GibsonSolver){
			        		odeSolverResultSet = ((GibsonSolver)solver).getStochSolverResultSet();			        		
			        	} else if (solver instanceof HybridSolver) {
			        		odeSolverResultSet = ((HybridSolver)solver).getHybridSolverResultSet();			        		
			        	} else {
			        		System.err.println("Solver results are not compatible with CSV format");
			        	}
			        } else if (solver instanceof AbstractJavaSolver) {
			        	((AbstractJavaSolver)solver).runSolver();
			        	odeSolverResultSet = ((ODESolver)solver).getODESolverResultSet();
			        	// must interpolate data for uniform time course which is not supported natively by the Java solvers
			        	Task task = (Task)sedml.getTaskWithId(sim.getImportedTaskID());
			        	org.jlibsedml.Simulation sedmlSim = sedml.getSimulation(task.getSimulationReference());
			        	if (sedmlSim instanceof UniformTimeCourse) {
				        	odeSolverResultSet = CLIUtils.interpolate(odeSolverResultSet, (UniformTimeCourse)sedmlSim);
			        	}
			        } else {
			        	// this should actually never happen...
			        	throw new Exception("Unexpected solver: " + kisao + " "+solver);
			        }					
					System.out.println("Succesful execution: " + docName + ": - task '" + sim.getDescription() + "'.");
				} catch (Exception e) {
					System.err.println("Failed execution: " + docName + ": - task '" + sim.getDescription() + "'.");
					System.err.println(solver.getSolverStatus().getSimulationMessage());
					e.printStackTrace(System.err);
				}
				resultsHash.put(sim.getImportedTaskID(), odeSolverResultSet);
				// TODO Akhil
				// uncomment after finishing testing all solvers
//				CLIUtils.removeIntermediarySimFiles(outputDir);
			}
            System.out.println("-------------------------------------------------------------------------");
        }
        return resultsHash;
    }

    ;

    // TODO: Complete this logger and use it for whole CLI
    private static class LocalLogger extends VCLogger {
        @Override
        public void sendMessage(Priority p, ErrorType et, String message) throws Exception {
            System.out.println("LOGGER: msgLevel=" + p + ", msgType=" + et + ", " + message);
            if (p == VCLogger.Priority.HighPriority) {
                SBMLImportException.Category cat = SBMLImportException.Category.UNSPECIFIED;
                if (message.contains(SBMLImporter.RESERVED_SPATIAL)) {
                    cat = SBMLImportException.Category.RESERVED_SPATIAL;
                }
                throw new SBMLImportException(message, cat);
            }
        }

        public void sendAllMessages() {
        }

        public boolean hasMessages() {
            return false;
        }
    }
}
