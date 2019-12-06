package org.vcell.sbml.test;

import java.io.File;

import org.jlibsedml.AbstractTask;
import org.jlibsedml.Libsedml;
import org.jlibsedml.SedML;
import org.vcell.sbml.vcell.SBMLImportException;
import org.vcell.sbml.vcell.SBMLImporter;
import org.vcell.util.document.VCDocument;

import cbit.util.xml.VCLogger;
import cbit.vcell.xml.ExternalDocInfo;
import cbit.vcell.xml.XmlHelper;


public class VCellSedMLSolver {

	public static void main(String[] args){

		// place the sedml file and the sbml file(s) in inDir directory
		File inDir = new File("C:\\TEMP\\ddd\\sedml");
		File outDir = new File("C:\\TEMP\\ddd\\sedml\\out");
		
		if (!outDir.exists()) {
			outDir.mkdirs();
		}
		
		File[] directoryListing = inDir.listFiles();
		if (directoryListing == null) {
			System.err.println("inDir not a directory");
			System.exit(99);
		}
		
		File sedmlFile = null;
		for (File aFile : directoryListing) {		// look for a sedml file by extension
			if(aFile.isDirectory()) {
				continue;
			}
			String aFileName = aFile.getName();
			if(!aFileName.contains(".")) {
				continue;
			}
			int end = aFileName.indexOf(".");
			String aExtension = aFileName.substring(end);
			if(aExtension == null) {
				continue;
			}
			if(aExtension.toLowerCase().contentEquals(".sedml")) {
				sedmlFile = aFile;
				break;
			}
		}
		if(sedmlFile == null) {
			System.err.println("no sedml file found");
			System.exit(98);
		}
		
		try {
			SedML sedml = Libsedml.readDocument(sedmlFile).getSedMLModel();
			if (sedml == null || sedml.getModels().isEmpty()) {
				System.err.println("the sedml file '" + sedmlFile.getName() + "'does not contain a valid document");
				System.exit(97);
			}
			VCellSedMLSolver vCellSedMLSolver = new VCellSedMLSolver();
			ExternalDocInfo externalDocInfo = new ExternalDocInfo(sedmlFile, true);
			for(AbstractTask at : sedml.getTasks()) {
				vCellSedMLSolver.doWork(externalDocInfo, at, sedml);
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {
		}
		System.out.println("done");
	}

	// everything is done here
	public void doWork(ExternalDocInfo externalDocInfo, AbstractTask sedmlTask, SedML sedml) throws Exception {

		cbit.util.xml.VCLogger sedmlImportLogger = new LocalLogger();
		
		// create the VCDocument (bioModel + application + simulation)
		VCDocument doc = XmlHelper.sedmlToBioModel(sedmlImportLogger, externalDocInfo, sedml, sedmlTask);
		
		// TODO: write the output file, invoke the solver
		
		
		String docName = doc.getName();
		System.out.println(docName + ": - task '" + sedmlTask.getId() + "'.");
		System.out.println("-------------------------------------------------------------------------");
	}
	

	public class LocalLogger extends VCLogger {
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

}
