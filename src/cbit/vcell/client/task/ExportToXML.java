package cbit.vcell.client.task;
import cbit.vcell.geometry.*;
import cbit.vcell.mathmodel.*;
import cbit.vcell.xml.*;
import cbit.vcell.clientdb.*;
import java.io.*;
import cbit.vcell.client.*;
import java.util.*;
import cbit.vcell.client.desktop.*;
import cbit.vcell.mapping.*;
import cbit.vcell.math.*;
import cbit.vcell.biomodel.*;
import cbit.vcell.desktop.controls.*;
import cbit.vcell.document.*;
import cbit.util.*;
/**
 * Insert the type's description here.
 * Creation date: (5/31/2004 6:03:16 PM)
 * @author: Ion Moraru
 */
public class ExportToXML extends AsynchClientTask {
/**
 * Insert the method's description here.
 * Creation date: (7/26/2004 12:29:53 PM)
 */
private String exportMatlab(File exportFile, javax.swing.filechooser.FileFilter fileFilter, MathDescription mathDesc) throws cbit.vcell.parser.ExpressionException, MathException {
	cbit.vcell.solver.Simulation sim = new cbit.vcell.solver.Simulation(mathDesc);
	cbit.vcell.matlab.MatlabOdeFileCoder coder = new cbit.vcell.matlab.MatlabOdeFileCoder(sim);
	java.io.StringWriter sw = new java.io.StringWriter();
	java.io.PrintWriter pw = new java.io.PrintWriter(sw);
	String functionName = exportFile.getName();
	if (functionName.endsWith(".m")){
		functionName = functionName.substring(0,functionName.length()-2);
	}
	if (fileFilter.getDescription().equals(FileFilters.FILE_FILTER_MATLABV5.getDescription())){
		coder.write_V5_OdeFile(pw,functionName);
	}else if (fileFilter.getDescription().equals(FileFilters.FILE_FILTER_MATLABV6.getDescription())){
		coder.write_V6_MFile(pw,functionName);
	}
	pw.flush();
	pw.close();
	return sw.getBuffer().toString();
}


/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @return java.lang.String
 */
public java.lang.String getTaskName() {
	return "Exporting document";
}


/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @return int
 */
public int getTaskType() {
	return TASKTYPE_NONSWING_BLOCKING;
}


/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(java.util.Hashtable hashTable) throws java.lang.Exception {
	VCDocument documentToExport = (VCDocument)hashTable.get("documentToExport");
	File exportFile = (File)hashTable.get("exportFile");
	javax.swing.filechooser.FileFilter fileFilter = (javax.swing.filechooser.FileFilter)hashTable.get("fileFilter");
	DocumentManager documentManager = (DocumentManager)hashTable.get("documentManager");
	String resultString = null;			
	if (documentToExport instanceof BioModel) {
		BioModel bioModel = (BioModel)documentToExport;
		// check format requested
		if (fileFilter.getDescription().equals(FileFilters.FILE_FILTER_MATLABV5.getDescription()) ||
			fileFilter.getDescription().equals(FileFilters.FILE_FILTER_MATLABV6.getDescription())){
			// matlab from application; get application
			Integer chosenSimContextIndex = (Integer)hashTable.get("chosenSimContextIndex");
			SimulationContext chosenSimContext = bioModel.getSimulationContexts(chosenSimContextIndex.intValue());
			// regenerate a fresh MathDescription
			MathMapping mathMapping = new MathMapping(chosenSimContext);
			MathDescription mathDesc = mathMapping.getMathDescription();
			// do export
			resultString = exportMatlab(exportFile, fileFilter, mathDesc);
		} else if (fileFilter.equals(FileFilters.FILE_FILTER_PDF)) {            
			documentManager.generatePDF(bioModel, new FileOutputStream(exportFile));
			return; 									//will take care of writing to the file as well.
		} else {
			// convert it if other format
			if (!fileFilter.equals(FileFilters.FILE_FILTER_VCML)) {
				// SBML or CellML; get application name
				Integer chosenSimContextIndex = (Integer)hashTable.get("chosenSimContextIndex");
				String applicationName = bioModel.getSimulationContexts(chosenSimContextIndex.intValue()).getName();
				// get dialect
				XmlDialect toDialect = null;
				if (fileFilter.equals(FileFilters.FILE_FILTER_SBML)) {
					toDialect = XmlDialect.SBML_L1V1;
				} else if (fileFilter.equals(FileFilters.FILE_FILTER_SBML_2)){
					toDialect = XmlDialect.SBML_L2V1;
				} else if (fileFilter.equals(FileFilters.FILE_FILTER_CELLML)) {
					toDialect = XmlDialect.QUAL_CELLML;
				}
				//convert the given model using the specified application
				resultString = XmlHelper.exportXML(bioModel, toDialect, applicationName);
			} else {
				// if format is VCML, get it from biomodel.
				resultString = XmlHelper.bioModelToXML(bioModel);
			}
		}
	} else if (documentToExport instanceof MathModel) {
		MathModel mathModel = (MathModel)documentToExport;
		// check format requested
		if (fileFilter.equals(FileFilters.FILE_FILTER_MATLABV5) ||
			fileFilter.equals(FileFilters.FILE_FILTER_MATLABV6)){
			MathDescription mathDesc = mathModel.getMathDescription();
			resultString = exportMatlab(exportFile, fileFilter, mathDesc);
		} else if (fileFilter.equals(FileFilters.FILE_FILTER_PDF)) {            
			documentManager.generatePDF(mathModel, new FileOutputStream(exportFile));
			return;                                                       //will take care of writing to the file as well.
		}else{ // should be an XML document (VCML or CellML)
			if (fileFilter.equals(FileFilters.FILE_FILTER_VCML)) {
				resultString = XmlHelper.mathModelToXML(mathModel);
			} else if (fileFilter.equals(FileFilters.FILE_FILTER_CELLML)) {
				resultString = XmlHelper.exportXML(mathModel, cbit.vcell.xml.XmlDialect.QUAN_CELLML);
			}
		}
	} else if (documentToExport instanceof Geometry){
		Geometry geom = (Geometry)documentToExport;
		if (fileFilter.equals(FileFilters.FILE_FILTER_PDF)) {            
			documentManager.generatePDF(geom, new FileOutputStream(exportFile));
			return;                                                       //will take care of writing to the file as well.
		}else if (fileFilter.equals(FileFilters.FILE_FILTER_VCML)) {
			resultString = XmlHelper.geometryToXML(geom);
		}else if (fileFilter.equals(FileFilters.FILE_FILTER_AVS)) {
			java.io.StringWriter stringWriter = new java.io.StringWriter();
			cbit.vcell.geometry.surface.AVS_UCD_Exporter.writeUCD(geom.getGeometrySurfaceDescription(),stringWriter);
			stringWriter.flush();
			stringWriter.close();
			resultString = stringWriter.getBuffer().toString();
		}else if (fileFilter.equals(FileFilters.FILE_FILTER_STL)) {
			java.io.StringWriter stringWriter = new java.io.StringWriter();
			cbit.vcell.geometry.surface.StlExporter.writeStl(geom.getGeometrySurfaceDescription(),stringWriter);
			stringWriter.flush();
			stringWriter.close();
			resultString = stringWriter.getBuffer().toString();
		}
	}
	java.io.FileWriter fileWriter = new java.io.FileWriter(exportFile);
	fileWriter.write(resultString);
	fileWriter.flush();
	fileWriter.close();
}


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 8:44:12 PM)
 * @return boolean
 */
public boolean skipIfAbort() {
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (6/8/2004 4:39:26 PM)
 * @return boolean
 */
public boolean skipIfCancel(UserCancelException exc) {
	return true;
}
}