package org.vcell.cli;

import cbit.util.xml.VCLogger;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.xml.ExternalDocInfo;
import cbit.vcell.xml.XmlHelper;
import org.jlibsedml.AbstractTask;
import org.jlibsedml.SedML;
import org.vcell.cli.helpers.solvers.*;
import org.vcell.sbml.vcell.SBMLImportException;
import org.vcell.sbml.vcell.SBMLImporter;
import org.vcell.util.document.VCDocument;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SolverHandler {

    private static void sanityCheck(VCDocument doc) {
        if (doc == null) {
            throw new RuntimeException("Imported VCDocument is null.");
        }
        String docName = doc.getName();
        if (docName == null || docName.isEmpty()) {
            throw new RuntimeException("The name of the imported VCDocument is null or empty.");
        }
        if (!(doc instanceof BioModel)) {
            throw new RuntimeException("The imported VCDocument '" + docName + "' is not a BioModel.");
        }
        BioModel bioModel = (BioModel) doc;
        if (bioModel.getSimulationContext(0) == null) {
            throw new RuntimeException("The imported VCDocument '" + docName + "' has no Application");
        }
        if (bioModel.getSimulation(0) == null) {
            throw new RuntimeException("The imported VCDocument '" + docName + "' has no Simulation");
        }
    }

    public void simulateAllTasks(ExternalDocInfo externalDocInfo, SedML sedml, File outputDir) {
        // create the VCDocument(s) (bioModel(s) + application(s) + simulation(s)), do sanity checks
        cbit.util.xml.VCLogger sedmlImportLogger = new LocalLogger();
        List<VCDocument> docs = null;
        try {
            docs = XmlHelper.sedmlToBioModel(sedmlImportLogger, externalDocInfo, sedml, null);
        } catch (Exception e) {
            System.err.println("Unable to Parse SED-ML into biomodel, failed with err: " + e.getMessage());
        }
        for (VCDocument doc : docs) {
			sanityCheck(doc);
			// create the work directory for this task, invoke the solver
			String docName = doc.getName();
			BioModel bioModel = (BioModel) doc;
			Simulation[] sims = bioModel.getSimulations();
			for (int i = 0; i < sims.length; i++) {
				SolverTaskDescription std = sims[i].getSolverTaskDescription();
				SolverDescription sd = std.getSolverDescription();
				String kisao = sd.getKisao();
				if (SolverDescription.CVODE.getKisao().contentEquals(kisao)) {
					ODESolverResultSet odeSolverResultSet = CVODEHelper.solve(outputDir, sims[i].getDescription().replaceAll("[:\\\\/*?|<>]", "_"), bioModel);
					System.out.println("Finished: " + docName + ": - task '" + sims[i].getDescription() + "'.");
				} else if (SolverDescription.StochGibson.getKisao().contentEquals(kisao)) {
					ODESolverResultSet odeSolverResultSet = StockGibsonHelper.solve(outputDir, sims[i].getDescription().replaceAll("[:\\\\/*?|<>]", "_"),
							bioModel);
					System.out.println("Finished: " + docName + ": - task '" + sims[i].getDescription() + "'.");
				} else if (SolverDescription.IDA.getKisao().contentEquals(kisao)) {
					ODESolverResultSet odeSolverResultSet = IDAHelper.solve(outputDir, sims[i].getDescription().replaceAll("[:\\\\/*?|<>]", "_"), bioModel);
					System.out.println("Finished: " + docName + ": - task '" + sims[i].getDescription() + "'.");
				} else if (SolverDescription.RungeKuttaFehlberg.getKisao().contentEquals(kisao)) {
					ODESolverResultSet odeSolverResultSet = RungeKuttaFelhbergHelper.solve(outputDir, sims[i].getDescription().replaceAll("[:\\\\/*?|<>]", "_"),
							bioModel);
					System.out.println("Finished: " + docName + ": - task '" + sims[i].getDescription() + "'.");
				} else if (SolverDescription.AdamsMoulton.getKisao().contentEquals(kisao)) {
					ODESolverResultSet odeSolverResultSet = AdamsMoultonHelper.solve(outputDir, sims[i].getDescription().replaceAll("[:\\\\/*?|<>]", "_"),
							bioModel);
					System.out.println("Finished: " + docName + ": - task '" + sims[i].getDescription() + "'.");
				} else if (SolverDescription.ForwardEuler.getKisao().contentEquals(kisao)) {
					ODESolverResultSet odeSolverResultSet = ForwardEulerHelper.solve(outputDir, sims[i].getDescription().replaceAll("[:\\\\/*?|<>]", "_"),
							bioModel);
					System.out.println("Finished: " + docName + ": - task '" + sims[i].getDescription() + "'.");
				} else if (SolverDescription.RungeKutta2.getKisao().contentEquals(kisao)) {
					ODESolverResultSet odeSolverResultSet = RungeKutta2Helper.solve(outputDir, sims[i].getDescription().replaceAll("[:\\\\/*?|<>]", "_"),
							bioModel);
					System.out.println("Finished: " + docName + ": - task '" + sims[i].getDescription() + "'.");
				} else if (SolverDescription.RungeKutta4.getKisao().contentEquals(kisao)) {
					ODESolverResultSet odeSolverResultSet = RungeKutta4Helper.solve(outputDir, sims[i].getDescription().replaceAll("[:\\\\/*?|<>]", "_"),
							bioModel);
					System.out.println("Finished: " + docName + ": - task '" + sims[i].getDescription() + "'.");
				} else {
					System.out.println("Unsupported solver: " + kisao);
				} 
			}
			System.out.println("-------------------------------------------------------------------------");
		}
    }

    ;

    // TODO: Complete this logger and use it for whole CLI
    private class LocalLogger extends VCLogger {
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
