package org.vcell.cli;

import cbit.util.xml.VCLogger;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.ode.ODESolver;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.xml.ExternalDocInfo;
import cbit.vcell.xml.XmlHelper;
import org.jlibsedml.AbstractTask;
import org.jlibsedml.SedML;
import org.vcell.cli.helpers.solvers.CVODEHelper;
import org.vcell.cli.helpers.solvers.IDAHelper;
import org.vcell.cli.helpers.solvers.RungeKuttaFelhbergHelper;
import org.vcell.cli.helpers.solvers.StockGibsonHelper;
import org.vcell.sbml.vcell.SBMLImportException;
import org.vcell.sbml.vcell.SBMLImporter;
import org.vcell.util.document.VCDocument;

import java.io.File;

public class SolverHandler {

    public void simulateTask(ExternalDocInfo externalDocInfo, AbstractTask sedmlTask, SedML sedml, File outputDir) {
        // create the VCDocument (bioModel + application + simulation), do sanity checks
        cbit.util.xml.VCLogger sedmlImportLogger = new LocalLogger();
        VCDocument doc = null;
        try {
            doc = XmlHelper.sedmlToBioModel(sedmlImportLogger, externalDocInfo, sedml, sedmlTask);
        } catch (Exception e) {
            System.err.println("Unable to Parse SEDML into biomodel, failed with err: " + e.getMessage()) ;
        }
        sanityCheck(doc);

        // create the work directory for this task, invoke the solver
        String docName = doc.getName();
//        String outString = Paths.get(outputDir.toString(), docName, sedmlTask.getId()).toString();
//        String outString = VCellSedMLSolver.OUT_ROOT_STRING + "/" + docName + "/" + sedmlTask.getId();


        BioModel bioModel = (BioModel)doc;
//        SimulationContext simContext = bioModel.getSimulationContext(0);
//        MathDescription mathDesc = simContext.getMathDescription();
//        String vcml = null;
//        try {
//            vcml = mathDesc.getVCML();
//        } catch (MathException e) {
//            System.err.println("Unable to get VCML from math description, failed with err: " + e.getMessage());
//        }
//        try (PrintWriter pw = new PrintWriter(outString + "/vcmlTrace.xml")) {
//            pw.println(vcml);
//        }

        Simulation sim = bioModel.getSimulation(0);
        SolverTaskDescription std = sim.getSolverTaskDescription();
        SolverDescription sd = std.getSolverDescription();
        String kisao = sd.getKisao();
        if(SolverDescription.CVODE.getKisao().contentEquals(kisao)) {
            ODESolverResultSet odeSolverResultSet = CVODEHelper.solve(outputDir, sedmlTask.getId(), bioModel);
            System.out.println("Finished: " + docName + ": - task '" + sedmlTask.getId() + "'.");
        } else if(SolverDescription.StochGibson.getKisao().contentEquals(kisao)) {
            ODESolverResultSet odeSolverResultSet = StockGibsonHelper.solve(outputDir, sedmlTask.getId(), bioModel);
            System.out.println("Finished: " + docName + ": - task '" + sedmlTask.getId() + "'.");
        } else if(SolverDescription.IDA.getKisao().contentEquals(kisao)) {
            ODESolverResultSet odeSolverResultSet = IDAHelper.solve(outputDir, sedmlTask.getId(), bioModel);
            System.out.println("Finished: " + docName + ": - task '" + sedmlTask.getId() + "'.");
        } else if (SolverDescription.RungeKuttaFehlberg.getKisao().contentEquals(kisao)){
            RungeKuttaFelhbergHelper.solve(outputDir, sedmlTask.getId(), bioModel);
            System.out.println("Finished: " + docName + ": - task '" + sedmlTask.getId() + "'.");
        } else {
            System.out.println("Unsupported solver: " + kisao);
        }
        System.out.println("-------------------------------------------------------------------------");
    }

    // TODO: Complete this logger and use it for whole CLI
    private class LocalLogger extends VCLogger {
        @Override
        public void sendMessage(Priority p, ErrorType et, String message) throws Exception {
            System.out.println("LOGGER: msgLevel="+p+", msgType="+et+", "+message);
            if (p==VCLogger.Priority.HighPriority) {
                SBMLImportException.Category cat = SBMLImportException.Category.UNSPECIFIED;
                if (message.contains(SBMLImporter.RESERVED_SPATIAL) ) {
                    cat = SBMLImportException.Category.RESERVED_SPATIAL;
                }
                throw new SBMLImportException(message,cat);
            }
        }
        public void sendAllMessages() {
        }
        public boolean hasMessages() {
            return false;
        }
    };

    private static void sanityCheck(VCDocument doc) {
        if(doc == null) {
            throw new RuntimeException("Imported VCDocument is null.");
        }
        String docName = doc.getName();
        if(docName == null || docName.isEmpty()) {
            throw new RuntimeException("The name of the imported VCDocument is null or empty.");
        }
        if(!(doc instanceof BioModel)) {
            throw new RuntimeException("The imported VCDocument '" + docName + "' is not a BioModel.");
        }
        BioModel bioModel = (BioModel)doc;
        if(bioModel.getSimulationContext(0) == null) {
            throw new RuntimeException("The imported VCDocument '" + docName + "' has no Application");
        }
        if(bioModel.getSimulation(0) == null) {
            throw new RuntimeException("The imported VCDocument '" + docName + "' has no Simulation");
        }
    }
}
