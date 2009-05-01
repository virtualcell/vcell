package cbit.vcell.model;

import java.io.PrintStream;

import org.vcell.util.BigString;
import org.vcell.util.SessionLog;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.User;

import cbit.sql.ConnectionFactory;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.test.ClientTester;
import cbit.vcell.clientdb.ClientDocumentManager;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.test.LumpedKineticsTester;
import cbit.vcell.math.MathDescription;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.modeldb.DatabasePolicySQL;
import cbit.vcell.modeldb.VCDatabaseScanner;
import cbit.vcell.modeldb.VCDatabaseVisitor;
import cbit.vcell.solver.Simulation;

public class ModelParametersVisitor implements VCDatabaseVisitor {

	public boolean filterBioModel(BioModelInfo bioModelInfo) {
		if (bioModelInfo.getVersion().getOwner().getName().equals("anu")) {
			// System.err.println("BM name : " + bioModelInfo.getVersion().getName() + "\tdate : " + bioModelInfo.getVersion().getDate().toString());
			return true;
		}
		return false;
	}

	public void visitBioModel(BioModel bioModel, PrintStream logFilePrintStream) {
		// Read biomodel, and save it under another name; then read both and compareEqual
		
		if ((bioModel.getName().startsWith("alt___"))) { // || (bioModel.getName().startsWith("ZZZZZ_"))) 
			return;
		}
//		String[] args = new String[3];
//		args[0] = "-local";
//		args[1] = "anu";
//		args[2] = "LisaraC1";
		try {
//			cbit.vcell.client.server.ClientServerManager managerManager = ClientTester.mainInit(args,"ClientDocumentManagerTest",null);
//			ClientDocumentManager docManager = (ClientDocumentManager)managerManager.getDocumentManager();
			
			// save 'bioModel' with new name
//			BioModel altBiomodel = bioModel;
//			altBiomodel.setName("alt____" + bioModel.getName());
//			// get simulation names from original biomodel
//			Simulation[] sims = bioModel.getSimulations();
//			String[] simNames = new String[sims.length];
//			for (int j = 0; j < sims.length; j++){
//				// prevents parent simulation (from the original mathmodel) reference connection
//				// Otherwise it will refer to data from previous (parent) simulation.
//				sims[j].clearVersion();
//				simNames[j] = sims[j].getName();
//			}
			// save
//			altBiomodel = docManager.save(altBiomodel, simNames);
			
//			 Now re-read the original and the newly saved biomodels
			
			// create a databaseServerImpl
//			DatabasePolicySQL.bAllowAdministrativeAccess = true;
//			SessionLog log = new cbit.vcell.server.StdoutSessionLog("ModelParameterVisitorTest");
//			ConnectionFactory conFactory = new cbit.sql.OraclePoolingConnectionFactory(log);
//			cbit.sql.KeyFactory	keyFactory = new cbit.sql.OracleKeyFactory();
//			cbit.sql.DBCacheTable dbCacheTable = new cbit.sql.DBCacheTable(1000*60*30);
//			cbit.vcell.modeldb.DatabaseServerImpl dbServerImpl = new cbit.vcell.modeldb.DatabaseServerImpl(conFactory, keyFactory, dbCacheTable, log);	 

//			User user = bioModel.getVersion().getOwner();
//			BioModelInfo bioModelInfo = dbServerImpl.getBioModelInfo(user,bioModel.getVersion().getVersionKey());
//			BigString newBioModelXML = dbServerImpl.getBioModelXML(user, bioModelInfo.getVersion().getVersionKey());
//			BioModel newBiomodel = cbit.vcell.xml.XmlHelper.XMLToBioModel(newBioModelXML.toString());
//			newBiomodel.refreshDependencies();

			// user is the same as for biomodel, but getting user separately for this biomodel
//			User altUser = altBiomodel.getVersion().getOwner();
//			BioModelInfo altBioModelInfo = dbServerImpl.getBioModelInfo(altUser,altBiomodel.getVersion().getVersionKey());
//			BigString altBioModelXML = dbServerImpl.getBioModelXML(altUser, altBioModelInfo.getVersion().getVersionKey());
//			BioModel altNewBiomodel = cbit.vcell.xml.XmlHelper.XMLToBioModel(altBioModelXML.toString());
//			altNewBiomodel.refreshDependencies();
			
			SimulationContext[] simContexts = bioModel.getSimulationContexts();
			for (int k = 0; k < simContexts.length; k++) {
				// consider only spatial applications
				if (simContexts[k].getGeometry().getDimension() > 0) {
					// original biomodel math description
					MathDescription mathDesc = simContexts[k].getMathDescription();
					// since altBiomodel is a copy of newBiomodel, assuming that simContext indices are the same is ok??
					// Generate math description for SimContext.
					// SimulationContext altSimContext = altBiomodel.getSimulationContexts(k);
					MathDescription altMathDesc = (new MathMapping(simContexts[k])).getMathDescription();
					// altSimContext.setMathDescription(altMathDesc);
					
					int dim = simContexts[k].getGeometry().getDimension();
					// compare original math desc with altBiomodel math description.
					if (mathDesc.compareEqual(altMathDesc)) {
						logFilePrintStream.println("MathDescriptions for simContext '" + simContexts[k].getName() + " (" + dim + "d geometry) in Biomodel : " + bioModel.getName() + "; Version : " + bioModel.getVersion().getDate().toString() + " are equivalent");
					} else {
						logFilePrintStream.println("MathDescriptions for simContext '" + simContexts[k].getName() + " (" + dim + "d geometry) in Biomodel : " + bioModel.getName() + "; Version : " + bioModel.getVersion().getDate().toString() + " are NOT equivalent");
					}
				}
			}
			
		} catch (Exception e) {
			// e.printStackTrace(logFilePrintStream);
			logFilePrintStream.println("Biomodel : " + bioModel.getName() + ";\t (" + bioModel.getVersion().getDate() + ") : Unable to create ClientServer and ClientDocManagers");
		}
	}

	public boolean filterGeometry(GeometryInfo geometryInfo) {
		return false;
	}

	public boolean filterMathModel(MathModelInfo mathModelInfo) {
		return false;
	}

	public void visitGeometry(Geometry geometry, PrintStream logFilePrintStream) {
	}

	public void visitMathModel(MathModel mathModel,	PrintStream logFilePrintStream) {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ModelParametersVisitor visitor = new ModelParametersVisitor();
		boolean bAbortOnDataAccessException = true;
		try{
			VCDatabaseScanner.scanBioModels(args, visitor, bAbortOnDataAccessException);
		}catch(Exception e){
			e.printStackTrace(System.err);
		}
	}

}