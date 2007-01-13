package cbit.vcell.client.task;
import cbit.vcell.client.server.*;
import cbit.gui.FileFilters;
import cbit.util.*;
import cbit.util.document.VCDocument;

import java.util.*;
import java.io.*;
import javax.swing.*;


import cbit.vcell.geometry.*;
import cbit.vcell.mathmodel.*;
import cbit.vcell.client.*;
import cbit.vcell.mapping.*;
import cbit.vcell.modelapp.SimulationContext;
import cbit.vcell.modelapp.StructureMapping;
import cbit.vcell.biomodel.*;
import cbit.vcell.desktop.controls.*;
/**
 * Insert the type's description here.
 * Creation date: (5/31/2004 6:03:16 PM)
 * @author: Ion Moraru
 */
public class ChooseFile extends AsynchClientTask {

	//reset the user preference for the default path, if needed.
	private void resetPreferredFilePath(File selectedFile, UserPreferences userPreferences) {

		String oldPath = userPreferences.getGenPref(UserPreferences.GENERAL_LAST_PATH_USED);
		String newPath = selectedFile.getParent();                 
		if (!newPath.equals(oldPath)) {
			userPreferences.setGenPref(UserPreferences.GENERAL_LAST_PATH_USED, newPath);
		}
		System.out.println("New preferred file path: " + newPath + ", Old preferred file path: " + oldPath);
	}

/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @return java.lang.String
 */
public java.lang.String getTaskName() {
	return "Selecting export file destination";
}


/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @return int
 */
public int getTaskType() {
	return TASKTYPE_SWING_BLOCKING;
}


/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(java.util.Hashtable hashTable) throws java.lang.Exception {
	VCDocument documentToExport = (VCDocument)hashTable.get("documentToExport");
	File exportFile = null;
	if (documentToExport instanceof BioModel) {
		exportFile = showBioModelXMLFileChooser(hashTable);
	} else if (documentToExport instanceof MathModel) {
		exportFile = showMathModelXMLFileChooser(hashTable);
	} else if (documentToExport instanceof Geometry) {
		exportFile = showGeometryModelXMLFileChooser(hashTable);
	} else {
		throw new Exception("Unsupported document type for XML export: " + documentToExport.getClass());
	}
	hashTable.put("exportFile", exportFile);
}


/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 */
private File showBioModelXMLFileChooser(java.util.Hashtable hashTable) throws java.lang.Exception {
	BioModel bioModel = (BioModel)hashTable.get("documentToExport");
	JFrame currentWindow = (JFrame)hashTable.get("currentWindow");
	UserPreferences userPreferences = (UserPreferences)hashTable.get("userPreferences");
	TopLevelWindowManager topLevelWindowManager = (TopLevelWindowManager)hashTable.get("topLevelWindowManager");
	String defaultPath = userPreferences.getGenPref(UserPreferences.GENERAL_LAST_PATH_USED);
	cbit.gui.VCFileChooser fileChooser = new cbit.gui.VCFileChooser(defaultPath);
	fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	fileChooser.setMultiSelectionEnabled(false);
	fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_SBML);
	fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_SBML_2);
	fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_CELLML);
	fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_VCML);
	fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_MATLABV5);
	fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_MATLABV6);
	fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_PDF);
	// Set the default file filter...
	fileChooser.setFileFilter(FileFilters.FILE_FILTER_VCML);
	// remove all selector
	fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
    fileChooser.setSelectedFile(new java.io.File(bioModel.getName() + ".xml"));
	
	fileChooser.setDialogTitle("Export Virtual Cell BioModel As...");
	if (fileChooser.showSaveDialog(currentWindow) != JFileChooser.APPROVE_OPTION) {
		// user didn't choose save
		throw UserCancelException.CANCEL_FILE_SELECTION;
	} else {
		File selectedFile = fileChooser.getSelectedFile();
		javax.swing.filechooser.FileFilter fileFilter = fileChooser.getFileFilter();
		if (selectedFile == null) {
			// no file selected (no name given)
			throw UserCancelException.CANCEL_FILE_SELECTION;
		} else {
			// we have a file selection, check for overwrites
			if (selectedFile.exists()) {
				String answer = PopupGenerator.showWarningDialog(topLevelWindowManager, userPreferences, UserMessage.warn_OverwriteFile, selectedFile.getAbsolutePath());
				if (answer.equals(UserMessage.OPTION_CANCEL)){
					throw UserCancelException.CANCEL_FILE_SELECTION;
				}
			}
			///
			String n = selectedFile.getPath().toLowerCase();
			if (fileFilter == FileFilters.FILE_FILTER_SBML && !n.endsWith(".xml")) {
				selectedFile = new File(selectedFile.getPath() + ".xml");
			} else if (fileFilter == FileFilters.FILE_FILTER_SBML_2 && !n.endsWith(".xml")) {
				selectedFile = new File(selectedFile.getPath() + ".xml");
			} else if (fileFilter == FileFilters.FILE_FILTER_CELLML && !n.endsWith(".xml")) {
				selectedFile = new File(selectedFile.getPath() + ".xml");
			} else if (fileFilter == FileFilters.FILE_FILTER_VCML && !n.endsWith(".xml")) {
				selectedFile = new File(selectedFile.getPath() + ".xml");
			} else if (fileFilter == FileFilters.FILE_FILTER_MATLABV5 && !n.endsWith(".m")) {
				selectedFile = new File(selectedFile.getPath() + ".m");
			} else if (fileFilter == FileFilters.FILE_FILTER_MATLABV6 && !n.endsWith(".m")) {
				selectedFile = new File(selectedFile.getPath() + ".m");
			} else if (fileFilter == FileFilters.FILE_FILTER_PDF && !n.endsWith(".pdf")) {
				selectedFile = new File(selectedFile.getPath() + ".pdf");
			}
			// put the filter in the hash so the export task knows what to do...
			hashTable.put("fileFilter", fileFilter);
			if (fileFilter.equals(FileFilters.FILE_FILTER_VCML)) {
				// nothing more to do in this case
				resetPreferredFilePath(selectedFile, userPreferences);
				return selectedFile;
			}
			// we now also have to specify the Application....
			SimulationContext simContexts[] = bioModel.getSimulationContexts();
			if (simContexts.length == 0 && !fileFilter.equals(FileFilters.FILE_FILTER_PDF)) {
				throw new Exception("At least one application must be created in order to export to this format");
			}				
			Vector applicableAppNameList = new Vector();
			if (fileFilter.getDescription().equals(FileFilters.FILE_FILTER_MATLABV5.getDescription()) || fileFilter.getDescription().equals(FileFilters.FILE_FILTER_MATLABV6.getDescription())) {
				// only non-spatial apps
				for (int i=0;i<simContexts.length;i++){
					if (simContexts[i].getGeometryContext().getGeometry().getDimension()==0){
						applicableAppNameList.add(simContexts[i].getName());
					}
				}
				if (applicableAppNameList.size() == 0) {
					throw new Exception("No non-spatial applications in model " + bioModel.getName() + ", can only export the math from a non-spatial application to this format.");
				}
			} else {
				// all apps
				for (int i=0;i<simContexts.length;i++){
					applicableAppNameList.add(simContexts[i].getName());
				}
			}
			String chosenSimContextName = null;
			Integer chosenSimContextIndex = null;
			SimulationContext chosenSimContext = null;
			if (applicableAppNameList.size() == 1){
				chosenSimContextName = (String)applicableAppNameList.get(0);
			} else if (!fileFilter.getDescription().equals(FileFilters.FILE_FILTER_PDF.getDescription())) {
				String[] applicationNames = (String[])cbit.util.BeanUtils.getArray(applicableAppNameList,String.class);
				Object choice = PopupGenerator.showListDialog(topLevelWindowManager, applicationNames, "Please select Application");
				if (choice == null) {
					// user cancelled
					throw UserCancelException.CANCEL_FILE_SELECTION;
				}
				chosenSimContextName = (String)choice;
			}
			// identify it and store index in the hash for next task
			for (int i=0;i<simContexts.length;i++){
				if (simContexts[i].getName().equals(chosenSimContextName)){
					hashTable.put("chosenSimContextIndex", new Integer(i));
					chosenSimContext = simContexts[i];
				}
			}

			// Select a structure and set its size only for SBML models
			if (fileFilter.getDescription().equals(FileFilters.FILE_FILTER_SBML.getDescription()) || fileFilter.getDescription().equals(FileFilters.FILE_FILTER_SBML_2.getDescription())) {
				// get user choice of structure and its size and computes absolute sizes of compartments using the StructureSizeSolver.
				cbit.vcell.model.Structure[] structures = bioModel.getModel().getStructures();
				
				String strucName = null;
				double structSize = 1.0;
				int structSelection = -1;
				int option = JOptionPane.CANCEL_OPTION;

				cbit.vcell.vcml.gui.StructureSizeInputPanel structureSizeInputPanel = null;
				while (structSelection < 0) {
					structureSizeInputPanel = new cbit.vcell.vcml.gui.StructureSizeInputPanel();
					structureSizeInputPanel.setStructures(structures);
					structureSizeInputPanel.setPreferredSize(new java.awt.Dimension(325, 325));
					structureSizeInputPanel.setMaximumSize(new java.awt.Dimension(325, 325));
					option = cbit.gui.DialogUtils.showComponentOKCancelDialog(null, structureSizeInputPanel, "Choose Structure and specify size");
					structSelection = structureSizeInputPanel.getStructSelectionIndex();
					if (option == JOptionPane.CANCEL_OPTION || option == JOptionPane.CLOSED_OPTION) {
						break;
					} else if (option == JOptionPane.OK_OPTION && structSelection < 0) {
						cbit.gui.DialogUtils.showErrorDialog("Please select a structure and set its size");
					}
				}
				if (option == JOptionPane.OK_OPTION) {
					structureSizeInputPanel.applyStructureNameAndSizeValues();
					strucName = structureSizeInputPanel.getSelectedStructureName();
					structSize = structureSizeInputPanel.getStructureSize();

					// Invoke StructureSizeEvaluator to compute absolute sizes of compartments
					cbit.vcell.vcml.StructureSizeSolver ssEvaluator = new cbit.vcell.vcml.StructureSizeSolver();
					cbit.vcell.model.Structure chosenStructure = chosenSimContext.getModel().getStructure(strucName);
					StructureMapping chosenStructMapping = chosenSimContext.getGeometryContext().getStructureMapping(chosenStructure);
					ssEvaluator.updateAbsoluteStructureSizes(chosenSimContext, chosenStructure, structSize, chosenStructMapping.getSizeParameter().getUnitDefinition());
				} else if (option == JOptionPane.CANCEL_OPTION || option == JOptionPane.CLOSED_OPTION) {
					// User did not choose to set size for any structure.
					// Without that information, cannot export successfully into SBML, 
					// Hence cancelling the entire export to SBML operation.
					throw UserCancelException.CANCEL_XML_TRANSLATION;
				}
			}
			
			resetPreferredFilePath(selectedFile, userPreferences);
			return selectedFile;
		}
	}
}


/**
 * Export to Geometry
 * Creation date: (5/31/2004 6:04:14 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
private File showGeometryModelXMLFileChooser(java.util.Hashtable hashTable) throws java.lang.Exception {

	Geometry geom = (Geometry)hashTable.get("documentToExport");
	JFrame currentWindow = (JFrame)hashTable.get("currentWindow");
	UserPreferences userPreferences = (UserPreferences)hashTable.get("userPreferences");
	TopLevelWindowManager topLevelWindowManager = (TopLevelWindowManager)hashTable.get("topLevelWindowManager");
	String defaultPath = userPreferences.getGenPref(UserPreferences.GENERAL_LAST_PATH_USED);
	cbit.gui.VCFileChooser fileChooser = new cbit.gui.VCFileChooser(defaultPath);
	fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	fileChooser.setMultiSelectionEnabled(false);
	fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_VCML);
	fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_PDF);
	if (geom.getDimension()>0){
		fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_AVS);
		fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_STL);
	}
	// set the default file filter...
	fileChooser.setFileFilter(FileFilters.FILE_FILTER_VCML);
	// remove all selector
	fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
    fileChooser.setSelectedFile(new java.io.File(geom.getName() + ".xml"));
	
	fileChooser.setDialogTitle("Export Virtual Cell Geometry As...");
	if (fileChooser.showSaveDialog(currentWindow) != JFileChooser.APPROVE_OPTION) {
		// user didn't choose save
		throw UserCancelException.CANCEL_FILE_SELECTION;
	} else {
		File selectedFile = fileChooser.getSelectedFile();
		javax.swing.filechooser.FileFilter fileFilter = fileChooser.getFileFilter();
		if (selectedFile == null) {
			// no file selected (no name given)
			throw UserCancelException.CANCEL_FILE_SELECTION;
		} else {
			// we have a file selection, check for overwrites
			if (selectedFile.exists()) {
				String answer = PopupGenerator.showWarningDialog(topLevelWindowManager, userPreferences, UserMessage.warn_OverwriteFile, selectedFile.getAbsolutePath());
				if (answer.equals(UserMessage.OPTION_CANCEL)){
					throw UserCancelException.CANCEL_FILE_SELECTION;
				}
			}
			///
			String n = selectedFile.getPath().toLowerCase();
			if (fileFilter == FileFilters.FILE_FILTER_VCML && !n.endsWith(".xml")) {
				selectedFile = new File(selectedFile.getPath() + ".xml");
			} else if (fileFilter == FileFilters.FILE_FILTER_PDF && !n.endsWith(".pdf")) {
				selectedFile = new File(selectedFile.getPath() + ".pdf");
			}
			// put the filter in the hash so the export task knows what to do...
			hashTable.put("fileFilter", fileFilter);
			resetPreferredFilePath(selectedFile, userPreferences);

			return selectedFile;
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
private File showMathModelXMLFileChooser(java.util.Hashtable hashTable) throws java.lang.Exception {
	// for mathmodels:
	MathModel mathModel = (MathModel)hashTable.get("documentToExport");
	JFrame currentWindow = (JFrame)hashTable.get("currentWindow");
	UserPreferences userPreferences = (UserPreferences)hashTable.get("userPreferences");
	TopLevelWindowManager topLevelWindowManager = (TopLevelWindowManager)hashTable.get("topLevelWindowManager");
	String defaultPath = userPreferences.getGenPref(UserPreferences.GENERAL_LAST_PATH_USED);
	cbit.gui.VCFileChooser fileChooser = new cbit.gui.VCFileChooser(defaultPath);
	fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	fileChooser.setMultiSelectionEnabled(false);
	fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_CELLML);
	fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_MATLABV5);
	fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_MATLABV6);
	fileChooser.addChoosableFileFilter(FileFilters.FILE_FILTER_PDF);
	// set the default file filter...
	fileChooser.setFileFilter(FileFilters.FILE_FILTER_VCML);
	// remove all selector
	fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
    fileChooser.setSelectedFile(new java.io.File(mathModel.getName() + ".xml"));
	
	fileChooser.setDialogTitle("Export Virtual Cell MathModel As...");
	if (fileChooser.showSaveDialog(currentWindow) != JFileChooser.APPROVE_OPTION) {
		// user didn't choose save
		throw UserCancelException.CANCEL_FILE_SELECTION;
	} else {
		File selectedFile = fileChooser.getSelectedFile();
		javax.swing.filechooser.FileFilter fileFilter = fileChooser.getFileFilter();
		if (selectedFile == null) {
			// no file selected (no name given)
			throw UserCancelException.CANCEL_FILE_SELECTION;
		} else {
			// we have a file selection, check for overwrites
			if (selectedFile.exists()) {
				String answer = PopupGenerator.showWarningDialog(topLevelWindowManager, userPreferences, UserMessage.warn_OverwriteFile, selectedFile.getAbsolutePath());
				if (answer.equals(UserMessage.OPTION_CANCEL)){
					throw UserCancelException.CANCEL_FILE_SELECTION;
				}
			}
			///
			String n = selectedFile.getPath().toLowerCase();
			if (fileFilter == FileFilters.FILE_FILTER_CELLML && !n.endsWith(".xml")) {
				selectedFile = new File(selectedFile.getPath() + ".xml");
			} else if (fileFilter == FileFilters.FILE_FILTER_VCML && !n.endsWith(".xml")) {
				selectedFile = new File(selectedFile.getPath() + ".xml");
			} else if (fileFilter == FileFilters.FILE_FILTER_MATLABV5 && !n.endsWith(".m")) {
				selectedFile = new File(selectedFile.getPath() + ".m");
			} else if (fileFilter == FileFilters.FILE_FILTER_MATLABV6 && !n.endsWith(".m")) {
				selectedFile = new File(selectedFile.getPath() + ".m");
			} else if (fileFilter == FileFilters.FILE_FILTER_PDF && !n.endsWith(".pdf")) {
				selectedFile = new File(selectedFile.getPath() + ".pdf");
			}
			// put the filter in the hash so the export task knows what to do...
			hashTable.put("fileFilter", fileFilter);
			//only non-spatial math models can be exported to CellML.
			if (fileFilter == FileFilters.FILE_FILTER_CELLML &&
				mathModel.getMathDescription().getGeometry().getDimension() != 0) {
				throw new Exception("No non-spatial applications, can only export to this format the math from a non-spatial application");
			}
			resetPreferredFilePath(selectedFile, userPreferences);

			return selectedFile;
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 8:44:16 PM)
 * @return boolean
 */
public boolean skipIfAbort() {
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (6/8/2004 4:39:38 PM)
 * @return boolean
 */
public boolean skipIfCancel(UserCancelException exc) {
	return true;
}
}