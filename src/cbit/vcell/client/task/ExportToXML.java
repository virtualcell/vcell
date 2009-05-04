package cbit.vcell.client.task;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Hashtable;
import javax.swing.filechooser.FileFilter;
import org.vcell.util.gui.FileFilters;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.xml.XmlHelper;
import org.vcell.util.document.VCDocument;

/**
 * Insert the type's description here.
 * Creation date: (5/31/2004 6:03:16 PM)
 * @author: Ion Moraru
 */
public class ExportToXML extends AsynchClientTask {
	
	public ExportToXML() {
		super("Exporting document to XML", TASKTYPE_NONSWING_BLOCKING);
	}
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
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(Hashtable<String, Object> hashTable) throws java.lang.Exception {
	VCDocument documentToExport = (VCDocument)hashTable.get("documentToExport");
	File exportFile = (File)hashTable.get("exportFile");
	FileFilter fileFilter = (FileFilter)hashTable.get("fileFilter");
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
				if ((fileFilter.equals(FileFilters.FILE_FILTER_SBML)) || (fileFilter.equals(FileFilters.FILE_FILTER_SBML_21)) || (fileFilter.equals(FileFilters.FILE_FILTER_SBML_23)) ) {
					SimulationContext selectedSimContext = (SimulationContext)hashTable.get("selectedSimContext");
					Simulation selectedSim = (Simulation)hashTable.get("selectedSimulation");
					int sbmlLevel = 0;
					int sbmlVersion = 0;
					if ((fileFilter.equals(FileFilters.FILE_FILTER_SBML))) {
						sbmlLevel = 1;
						sbmlVersion = 2;
					} else if (fileFilter.equals(FileFilters.FILE_FILTER_SBML_21)) {
						sbmlLevel = 2;
						sbmlVersion = 1;
					} else if (fileFilter.equals(FileFilters.FILE_FILTER_SBML_23)) {
						sbmlLevel = 2;
						sbmlVersion = 3;
					}
					if (selectedSim == null) {
						resultString = XmlHelper.exportSBML(bioModel, sbmlLevel, sbmlVersion, selectedSimContext, null);
					} else {
						for (int sc = 0; sc < selectedSim.getScanCount(); sc++) {
							SimulationJob simJob = new SimulationJob(selectedSim, null, sc);
							resultString = XmlHelper.exportSBML(bioModel, sbmlLevel, sbmlVersion, selectedSimContext, simJob);
							// Need to export each parameter scan into a separate file 
							String newExportFileName = exportFile.getPath().substring(0, exportFile.getPath().indexOf(".xml")) + "_" + sc + ".xml";
							exportFile.renameTo(new File(newExportFileName));
							java.io.FileWriter fileWriter = new java.io.FileWriter(exportFile);
							fileWriter.write(resultString);
							fileWriter.flush();
							fileWriter.close();
						}
						return;
					}
				} else if (fileFilter.equals(FileFilters.FILE_FILTER_CELLML)) {
					Integer chosenSimContextIndex = (Integer)hashTable.get("chosenSimContextIndex");
					String applicationName = bioModel.getSimulationContexts(chosenSimContextIndex.intValue()).getName();
					resultString = XmlHelper.exportCellML(bioModel, applicationName);
				}
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
		}else if (fileFilter.equals(FileFilters.FILE_FILTER_VCML)) {
			resultString = XmlHelper.mathModelToXML(mathModel);
		} else if (fileFilter.equals(FileFilters.FILE_FILTER_CELLML)) {
			resultString = XmlHelper.exportCellML(mathModel, null);
		} else if (fileFilter.equals(FileFilters.FILE_FILTER_SBML_23)) {
			resultString = XmlHelper.exportSBML(mathModel, 2, 3, null, null);
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

}