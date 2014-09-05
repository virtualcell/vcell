/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping.gui;
import static cbit.vcell.VirtualMicroscopy.importer.VFrapXmlHelper.CreateSaveVFrapDataSymbols;
import static cbit.vcell.VirtualMicroscopy.importer.VFrapXmlHelper.LoadVFrapDisplayRoi;
import static cbit.vcell.VirtualMicroscopy.importer.VFrapXmlHelper.LoadVFrapSpecialImages;
import static cbit.vcell.VirtualMicroscopy.importer.VFrapXmlHelper.SaveVFrapSpecialImagesAsFieldData;
import static cbit.vcell.VirtualMicroscopy.importer.VFrapXmlHelper.checkNameAvailability;
import static cbit.vcell.data.VFrapConstants.ADD_ASSOCIATE_EXISTING_FD_MENU;
import static cbit.vcell.data.VFrapConstants.ADD_COPY_FROM_BIOMODEL_MENU;
import static cbit.vcell.data.VFrapConstants.ADD_IMAGE_FILE_MENU;
import static cbit.vcell.data.VFrapConstants.ADD_PSF_MENU;
import static cbit.vcell.data.VFrapConstants.ADD_VFRAP_DATASET_MENU;
import static cbit.vcell.data.VFrapConstants.ADD_VFRAP_SPECIALS_MENU;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

import org.jdom.Element;
import org.vcell.util.BeanUtils;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.User;
import org.vcell.util.document.Version;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.DownArrowIcon;

import cbit.image.VCImageUncompressed;
import cbit.util.xml.XmlUtil;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.importer.AnnotatedImageDataset;
import cbit.vcell.VirtualMicroscopy.importer.MicroscopyXmlReader;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.FieldDataWindowManager;
import cbit.vcell.client.desktop.DocumentWindow;
import cbit.vcell.client.desktop.biomodel.BioModelEditorApplicationRightSidePanel;
import cbit.vcell.client.desktop.biomodel.BioModelEditorApplicationRightSideTableModel;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.data.DataSymbol;
import cbit.vcell.data.DataSymbol.DataSymbolType;
import cbit.vcell.data.FieldDataSymbol;
import cbit.vcell.field.io.FieldDataFileOperationSpec;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.geometry.gui.OverlayEditorPanelJAI;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.VariableType;
import cbit.vcell.psf.PointSpreadFunctionManagement;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.units.VCUnitDefinition;

/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class DataSymbolsPanel extends BioModelEditorApplicationRightSidePanel<DataSymbol> {
	
	enum DataSymbolNewMenuItem {
		vfrap(ADD_VFRAP_DATASET_MENU),
		vfrap_special(ADD_VFRAP_SPECIALS_MENU),
		associate(ADD_ASSOCIATE_EXISTING_FD_MENU),
		psf(ADD_PSF_MENU),
		image(ADD_IMAGE_FILE_MENU),
		copy_from_biomodel(ADD_COPY_FROM_BIOMODEL_MENU);
		
//		String[][] choices = new String[][] {{ADD_VFRAP_DATASET_MENU},{ADD_VFRAP_SPECIALS_MENU},{ADD_ASSOCIATE_EXISTING_FD_MENU},
//				{ADD_PSF_MENU},{ADD_IMAGE_FILE_MENU},{ADD_COPY_FROM_BIOMODEL_MENU} };
		
		private String name;
		private JMenuItem menuItem;
		private DataSymbolNewMenuItem(String n) {
			this.name = n;
		}
	}
	
	private JFileChooser fc = null;	
	private IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JPopupMenu ivjJPopupMenuICP = null;
	
	private class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() instanceof JMenuItem) {
				for (DataSymbolNewMenuItem item : DataSymbolNewMenuItem.values()) {
					if (e.getSource() == item.menuItem) {
						newDataSymbol(item);
						break;
					}
				}
			}
		}
	}

public DataSymbolsPanel() {
	super();
	initialize();
}

private void newDataSymbol(DataSymbolNewMenuItem item) {
	switch (item) {
	case vfrap:
		addVFrapOriginalImages();
		break;
	case vfrap_special:
		addVFrapDerivedImages();
		break;
	case associate:
		addAssociate();
		break;
	case psf:
		addPSF();
		break;
	default:
		throw new RuntimeException("Option not yet implemented."); 		
	}	
}

private void addPSF() {
	PointSpreadFunctionManagement psfManager = new PointSpreadFunctionManagement(this, simulationContext);
	psfManager.importPointSpreadFunction();
}

private AsynchClientTask ChooseVFrapFile() {
	return new AsynchClientTask("Select a file", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			if (fc == null) {
				//Create a file chooser
				fc = new JFileChooser();
				vFrapFieldDataFilter filter = new vFrapFieldDataFilter();
				fc.setFileFilter(filter);
			}
			int returnVal = fc.showOpenDialog(DataSymbolsPanel.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File vFrapFile = fc.getSelectedFile();
				if (vFrapFile == null) {
					throw new RuntimeException("Unable to open vFrap file.");
				}
				hashTable.put("vFrapFile", vFrapFile);
			} else {
				throw UserCancelException.CANCEL_GENERIC;
			}
		}
	};
}

private void addAssociate() {
	DocumentWindow documentWindow = (DocumentWindow)BeanUtils.findTypeParentOfComponent(this, DocumentWindow.class);
	documentWindow.getTopLevelWindowManager().getRequestManager().showFieldDataWindow(new FieldDataWindowManager.DataSymbolCallBack() {
		public void createDataSymbol(ExternalDataIdentifier dataSetID,
				String fieldDataVarName, VariableType fieldDataVarType,
				double fieldDataVarTime) {
			
			System.out.println(dataSetID+" "+fieldDataVarName+" "+fieldDataVarType+" "+fieldDataVarTime);
			// ex: incomplete 51780592 danv(26766043)      fluor     Volume_VariableType     23.680419921875
			
   	        DecimalFormat df = new  DecimalFormat("###000.00");		// max time interval we can display is about 11 days
   			String fluorName = fieldDataVarName + "_" + df.format(fieldDataVarTime).substring(0, df.format(fieldDataVarTime).indexOf(".")) + "s" + df.format(fieldDataVarTime).substring(1+df.format(fieldDataVarTime).indexOf(".")) + "_" + dataSetID.getName();
//TODO:  symbol names may not be unique, must check for unicity and prompt the user
			FieldDataSymbol dataSymbol = new FieldDataSymbol(fluorName, DataSymbolType.GENERIC_SYMBOL,
					simulationContext.getDataContext(), simulationContext.getModel().getUnitSystem().getInstance_TBD(),
					dataSetID, 
					fieldDataVarName, fieldDataVarType.getTypeName(), fieldDataVarTime);
			simulationContext.getDataContext().addDataSymbol(dataSymbol);
		}
	});
}

private void addVFrapOriginalImages() {		// add dataset (normal images) from vFrap
	AsynchClientTask[] taskArray = new AsynchClientTask[5];

	// select the desired vfrap file 
	taskArray[0] = ChooseVFrapFile();
	taskArray[1] = new AsynchClientTask("Import objects", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		public void run(Hashtable<String, Object> hashTable) throws Exception {
		
			File vFrapFile = (File)hashTable.get("vFrapFile");
			Component requesterComponent = DataSymbolsPanel.this;
			DocumentWindow documentWindow = (DocumentWindow)BeanUtils.findTypeParentOfComponent(requesterComponent, DocumentWindow.class);
			DocumentManager documentManager = documentWindow.getTopLevelWindowManager().getRequestManager().getDocumentManager();
			if(documentManager == null){
				throw new RuntimeException("Not connected to server.");
			}
			String vFrapFileNameExtended = vFrapFile.getName();			// ex  ccc8.vfrap
			{	// we want to make sure to reload these strings from the hash later on
			String initialFieldDataName = vFrapFileNameExtended.substring(0, vFrapFileNameExtended.indexOf(".vfrap"));
			String mixedFieldDataName = initialFieldDataName + "Mx";	// we'll save here the "special" vFrap images (prebleach_avg, ...)
			hashTable.put("initialFieldDataName", initialFieldDataName);
			hashTable.put("mixedFieldDataName", mixedFieldDataName);
			}
			if(vFrapFileNameExtended.indexOf(".vfrap") <= -1)
			{
				throw new RuntimeException("File extension must be .vfrap");
			}
			checkNameAvailability(hashTable, false, documentManager, requesterComponent);		// normal images

			// ----- read needed info from Virtual FRAP xml file
			System.out.println("Loading " + vFrapFileNameExtended + " ...");
			String xmlString = XmlUtil.getXMLString(vFrapFile.getAbsolutePath());
			MicroscopyXmlReader xmlReader = new MicroscopyXmlReader(true);
			Element vFrapRoot = XmlUtil.stringToXML(xmlString, null).getRootElement();
			
			// loading frap images
			AnnotatedImageDataset annotatedImages = xmlReader.getAnnotatedImageDataset(vFrapRoot, null);
			hashTable.put("annotatedImages",annotatedImages);
				
			//loading ROIs for display purposes only (see next task)
			ROI rois[] = xmlReader.getPrimaryROIs(XmlUtil.stringToXML(xmlString, null).getRootElement(), null);
			LoadVFrapDisplayRoi(hashTable, annotatedImages, rois);
			
//				Calendar cal = Calendar.getInstance();
//			    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_hhmmss");
//				DocumentWindow documentWindow = (DocumentWindow)BeanUtils.findTypeParentOfComponent(DataSymbolsPanel.this, DocumentWindow.class);
//				DocumentManager documentManager = documentWindow.getTopLevelWindowManager().getRequestManager().getDocumentManager();
//				VFrapXmlHelper vFrapXmlHelper = new VFrapXmlHelper();
//				if(vFrapXmlHelper.isAlreadyImported(vFrapFileName, documentManager)) {
//					throw new RuntimeException("FieldData name already in use.");
//				}
////				bioModel.setName(vFrapFileName + "-" + sdf.format(cal.getTime()));
//				bioModel.setName(vFrapFileName);
//				BioModel feedbackModel =  documentManager.save(bioModel, null);
//				BioModelChildSummary  childSummary = BioModelChildSummary.fromDatabaseSerialization(xmlString);
//				BioModelInfo biomodelInfo = new BioModelInfo(feedbackModel.getVersion(), feedbackModel.getVersion().getVersionKey(), childSummary );
//				documentWindow.getTopLevelWindowManager().getRequestManager().openDocument(biomodelInfo, documentWindow.getTopLevelWindowManager(), true);
		}
	};
	// show the images from the vfrap file in an OverlayEditorPanelJAI dialog
	taskArray[2] = new AsynchClientTask("Display images", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			
			String initialFieldDataName = (String)hashTable.get("initialFieldDataName");
			if(initialFieldDataName.equals("")) {
				JOptionPane.showMessageDialog(DataSymbolsPanel.this, "Field Data name " + initialFieldDataName + " already in use."); 					
				// prevents the rest of tasks below from running
				throw UserCancelException.CANCEL_GENERIC;
			}

			AnnotatedImageDataset annotatedImages = (AnnotatedImageDataset)hashTable.get("annotatedImages");
			BufferedImage[] displayROI = (BufferedImage[])hashTable.get("displayROI");
			if (annotatedImages==null || displayROI==null){
				return;
			}
			// display the images
			OverlayEditorPanelJAI overlayPanel = new OverlayEditorPanelJAI();
			overlayPanel.setAllowAddROI(false);
			ImageDataset imageDataset = annotatedImages.getImageDataset();
			overlayPanel.setImages(imageDataset, 1, 0, new OverlayEditorPanelJAI.AllPixelValuesRange(1, 200) );
			overlayPanel.setAllROICompositeImage(displayROI, OverlayEditorPanelJAI.FRAP_DATA_INIT_PROPERTY);
			int choice = DialogUtils.showComponentOKCancelDialog(DataSymbolsPanel.this, overlayPanel, "vFrap Field Data");
			if(choice != JOptionPane.OK_OPTION)
			{
				throw UserCancelException.CANCEL_GENERIC;
			}
		}
	};
	// save the timepoints from memory to the database as field data
	taskArray[3] = new AsynchClientTask("Saving time series data", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		public void run(Hashtable<String, Object> hashTable) throws Exception {

			AnnotatedImageDataset annotatedImages = (AnnotatedImageDataset)hashTable.get("annotatedImages");
			String initialFieldDataName = (String)hashTable.get("initialFieldDataName");

			DocumentWindow documentWindow = (DocumentWindow)BeanUtils.findTypeParentOfComponent(DataSymbolsPanel.this, DocumentWindow.class);
			DocumentManager dm = documentWindow.getTopLevelWindowManager().getRequestManager().getDocumentManager();
			if(dm == null){
				throw new RuntimeException("Not connected to server.");
			}
			
			User owner = null;
			Version version = simulationContext.getVersion();
			if(version == null) {		// new document, so the owner is the user
				owner = dm.getUser();
			} else {
				owner = simulationContext.getVersion().getOwner();
			}

			// mesh
			ImageDataset imageDataset = annotatedImages.getImageDataset();
			Extent extent = imageDataset.getExtent();
			ISize isize = imageDataset.getISize();
			Origin origin = new Origin(0,0,0);
			CartesianMesh cartesianMesh = CartesianMesh.createSimpleCartesianMesh(origin, extent,isize, 
				new RegionImage( new VCImageUncompressed(null, new byte[isize.getXYZ()], extent, 
						isize.getX(),isize.getY(),isize.getZ()),0,null,null,RegionImage.NO_SMOOTHING));
			// save field data
			int NumTimePoints = imageDataset.getImageTimeStamps().length; 
			int NumChannels = 1;
			double[][][] pixData = new double[NumTimePoints][NumChannels][]; 
			for(int i = 0 ; i < NumTimePoints; i++)
			{
				short[] originalData = imageDataset.getPixelsZ(0, i);	// images according to zIndex at specific time points(tIndex)
				double[] doubleData = new double[originalData.length];
				for(int j = 0; j < originalData.length; j++)
				{
					doubleData[j] = 0x0000ffff & originalData[j];
				}
				pixData[i][NumChannels-1] = doubleData;
			}
			FieldDataFileOperationSpec timeSeriesFieldDataOpSpec = new FieldDataFileOperationSpec();
			timeSeriesFieldDataOpSpec.opType = FieldDataFileOperationSpec.FDOS_ADD;
			timeSeriesFieldDataOpSpec.cartesianMesh = cartesianMesh;
			timeSeriesFieldDataOpSpec.doubleSpecData =  pixData;
			timeSeriesFieldDataOpSpec.specEDI = null;
			timeSeriesFieldDataOpSpec.varNames = new String[] {SimulationContext.FLUOR_DATA_NAME};
			timeSeriesFieldDataOpSpec.owner = owner;
			timeSeriesFieldDataOpSpec.times = imageDataset.getImageTimeStamps();
			timeSeriesFieldDataOpSpec.variableTypes = new VariableType[] {VariableType.VOLUME};
			timeSeriesFieldDataOpSpec.origin = origin;
			timeSeriesFieldDataOpSpec.extent = extent;
			timeSeriesFieldDataOpSpec.isize = isize;
			// realignment for the case when first timepoint is not zero
			if(timeSeriesFieldDataOpSpec.times[0] != 0) {
				double shift = timeSeriesFieldDataOpSpec.times[0];
				for(int i = 0 ; i < NumTimePoints; i++)
				{
					timeSeriesFieldDataOpSpec.times[i] -= shift;
				}
			}

			Calendar cal = Calendar.getInstance();
		    SimpleDateFormat sdf = new SimpleDateFormat("yyMMMdd_hhmmss");
		    String formattedDate = sdf.format(cal.getTime());
			hashTable.put("formattedDate", formattedDate);

//	   		ExternalDataIdentifier timeSeriesEDI = dm.saveFieldData(timeSeriesFieldDataOpSpec, 
//	   				initialFieldDataName + "_" + formattedDate);
	   		ExternalDataIdentifier timeSeriesEDI = dm.saveFieldData(timeSeriesFieldDataOpSpec, initialFieldDataName);
			hashTable.put("imageDataset", imageDataset);
			hashTable.put("timeSeriesEDI", timeSeriesEDI);
		}
	};
	// create the data symbols for the images saved above and display them in the tree/table
	taskArray[4] = new AsynchClientTask("Display Data Symbols", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			
	   		// --- create the data symbols associated with the time series
			String initialFieldDataName = (String)hashTable.get("initialFieldDataName");
			ImageDataset imageDataset = (ImageDataset)hashTable.get("imageDataset");
			ExternalDataIdentifier timeSeriesEDI = (ExternalDataIdentifier)hashTable.get("timeSeriesEDI");
			
	   		for (double time : imageDataset.getImageTimeStamps()){
//	   			String fluorName = TokenMangler.fixTokenStrict("fluor_"+time+"_");
//				while (simulationContext.getDataContext().getDataSymbol(fluorName)!=null){
//					fluorName = TokenMangler.getNextEnumeratedToken(fluorName);
//	   			}
	   	        DecimalFormat df = new  DecimalFormat("###000.00");		// max time interval we can display is about 11 days
//	   			String fluorName = "fluor_" + df.format(time) + "_" + formattedDate;
	   			String fluorName = "fluor_" + df.format(time).substring(0, df.format(time).indexOf(".")) + "s" + df.format(time).substring(1+df.format(time).indexOf(".")) + "_" + initialFieldDataName;
//				FieldFunctionArguments fluorFFArgs = new FieldFunctionArguments(timeSeriesEDI.getName(), fluorName, new Expression(time), VariableType.VOLUME);
				DataSymbol fluorDataSymbol = new FieldDataSymbol( fluorName, DataSymbolType.VFRAP_TIMEPOINT, 
						simulationContext.getDataContext(), simulationContext.getModel().getUnitSystem().getInstance_TBD(),
						timeSeriesEDI, SimulationContext.FLUOR_DATA_NAME, VariableType.VOLUME.getTypeName(), time);
				simulationContext.getDataContext().addDataSymbol(fluorDataSymbol); 
	   		}
		}
	};
	Hashtable<String, Object> hash = new Hashtable<String, Object>();
	ClientTaskDispatcher.dispatch(this, hash, taskArray, false, true, null);

	
//	String name = null;
//	try {
//		getNewDataSymbolPanel().setSymbolName("");
//		getNewDataSymbolPanel().setSymbolExpression("vcField(dataset1,var1,0.0,Volume)");
//		int newSettings = org.vcell.util.gui.DialogUtils.showComponentOKCancelDialog(this, getNewDataSymbolPanel(), "New DataSymbol");
//		if (newSettings == JOptionPane.OK_OPTION) {
//			name = getNewDataSymbolPanel().getSymbolName();
//			String expression = getNewDataSymbolPanel().getSymbolExpression();
//			Expression exp = new Expression(expression);
//			FunctionInvocation[] functionInvocations = exp.getFunctionInvocations(null);
////			DataSymbol ds = new FieldDataSymbol(DataSymbolType.GENERIC_SYMBOL, name, "",
////					simulationContext.getDataContext(), VCUnitDefinition.UNIT_TBD, 
////					new FieldFunctionArguments(functionInvocations[0]));
//			DataSymbol ds = new FieldDataSymbol(name, DataSymbolType.GENERIC_SYMBOL,
//					simulationContext.getDataContext(), VCUnitDefinition.UNIT_TBD);
//			simulationContext.getDataContext().addDataSymbol(ds);
//		}
//	} catch (java.lang.Throwable ivjExc) {
//		DialogUtils.showErrorDialog(this, "Data symbol " + name + " already exists");
//	}
}
private void addVFrapDerivedImages() {		// add special (computed) images from vFrap
	AsynchClientTask[] taskArray = new AsynchClientTask[5];
	// select the desired vfrap file 
	taskArray[0] = ChooseVFrapFile();
	// load the images from the vfrap file, compute derived images, store them all in memory
	taskArray[1] = new AsynchClientTask("Import images", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		public void run(Hashtable<String, Object> hashTable) throws Exception {
		
			File vFrapFile = (File)hashTable.get("vFrapFile");
			Component requesterComponent = DataSymbolsPanel.this;
			DocumentWindow documentWindow = (DocumentWindow)BeanUtils.findTypeParentOfComponent(requesterComponent, DocumentWindow.class);
			DocumentManager documentManager = documentWindow.getTopLevelWindowManager().getRequestManager().getDocumentManager();
			if(documentManager == null){
				throw new RuntimeException("Not connected to server.");
			}
			String vFrapFileNameExtended = vFrapFile.getName();			// ex  ccc8.vfrap
			{	// we want to make sure to reload these strings from the hash later on
			String initialFieldDataName = vFrapFileNameExtended.substring(0, vFrapFileNameExtended.indexOf(".vfrap"));
			String mixedFieldDataName = initialFieldDataName + "Mx";	// we'll save here the "special" vFrap images (prebleach_avg, ...)
			hashTable.put("initialFieldDataName", initialFieldDataName);
			hashTable.put("mixedFieldDataName", mixedFieldDataName);
			}
			if(vFrapFileNameExtended.indexOf(".vfrap") <= -1)
			{
				throw new RuntimeException("File extension must be .vfrap");
			}
			checkNameAvailability(hashTable, true, documentManager, requesterComponent);	// for derived images and ROIs
							
			// ----- read needed info from Virtual FRAP xml file
			System.out.println("Loading " + vFrapFileNameExtended + " ...");
			String xmlString = XmlUtil.getXMLString(vFrapFile.getAbsolutePath());
			MicroscopyXmlReader xmlReader = new MicroscopyXmlReader(true);
			Element vFrapRoot = XmlUtil.stringToXML(xmlString, null).getRootElement();

			// loading frap images and a ROIs subset for display purposes only (see next task)
			AnnotatedImageDataset annotatedImages = xmlReader.getAnnotatedImageDataset(vFrapRoot, null);
			hashTable.put("annotatedImages",annotatedImages);
			ROI rois[] = xmlReader.getPrimaryROIs(XmlUtil.stringToXML(xmlString, null).getRootElement(), null);
			LoadVFrapDisplayRoi(hashTable, annotatedImages, rois);

			// ------ locate the special images within the vFrap files and load them in memory
			if(!LoadVFrapSpecialImages(hashTable, vFrapRoot)) {
				throw new RuntimeException("Unable to recover derived images from vFrap.");
			}
			
//			int startingIndexRecovery = xmlReader.getStartindIndexForRecovery(XmlUtil.stringToXML(xmlString, null).getRootElement());
//			vFrapXmlHelper.LoadVFrapSpecialImages(annotatedImages, startingIndexRecovery);	// prebleach average and first postbleach
//			vFrapXmlHelper.LoadVFrapRoiCompositeImages(annotatedImages, rois);
		}
	};
	
	// show the images from the vfrap file in an OverlayEditorPanelJAI dialog
	taskArray[2] = new AsynchClientTask("Display images", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			
			String mixedFieldDataName = (String)hashTable.get("mixedFieldDataName");
			if(mixedFieldDataName.equals("")) {
				JOptionPane.showMessageDialog(DataSymbolsPanel.this, "Field Data name " + mixedFieldDataName + " already in use."); 					
				// prevents the rest of tasks below from running
				throw UserCancelException.CANCEL_GENERIC;
			}

			AnnotatedImageDataset annotatedImages = (AnnotatedImageDataset)hashTable.get("annotatedImages");
			BufferedImage[] displayROI = (BufferedImage[])hashTable.get("displayROI");
			if (annotatedImages==null || displayROI==null){
				return;
			}
			// display the images
			OverlayEditorPanelJAI overlayPanel = new OverlayEditorPanelJAI();
			overlayPanel.setAllowAddROI(false);
			ImageDataset imageDataset = annotatedImages.getImageDataset();
			overlayPanel.setImages(imageDataset, 1, 0, new OverlayEditorPanelJAI.AllPixelValuesRange(1, 200) );
			overlayPanel.setAllROICompositeImage(displayROI, OverlayEditorPanelJAI.FRAP_DATA_INIT_PROPERTY);
			int choice = DialogUtils.showComponentOKCancelDialog(DataSymbolsPanel.this, overlayPanel, "vFrap Field Data");
			if(choice != JOptionPane.OK_OPTION)
			{
				throw UserCancelException.CANCEL_GENERIC;
			}
		}
	};
	
	// save the ROIs, prebleach average and the first postbleach frame from memory to the database as field data
	taskArray[3] = new AsynchClientTask("Saving roi masks, pre-bleach average and first post-bleach", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		public void run(Hashtable<String, Object> hashTable) throws Exception {

			Component requesterComponent = DataSymbolsPanel.this;
			DocumentWindow documentWindow = (DocumentWindow)BeanUtils.findTypeParentOfComponent(requesterComponent, DocumentWindow.class);
			DocumentManager documentManager = documentWindow.getTopLevelWindowManager().getRequestManager().getDocumentManager();
			if(documentManager == null){
				throw new RuntimeException("Not connected to server.");
			}
			ExternalDataIdentifier derivedEDI = SaveVFrapSpecialImagesAsFieldData(hashTable, documentManager);
			hashTable.put("derivedEDI", derivedEDI);
		}
	};
	
	// create the data symbols for the images saved above and display them in the tree/table
	taskArray[4] = new AsynchClientTask("Display Data Symbols", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		public void run(Hashtable<String, Object> hashTable) throws Exception {

			ExternalDataIdentifier derivedEDI = (ExternalDataIdentifier)hashTable.get("derivedEDI");
			BioModel bioModel = simulationContext.getBioModel();
			CreateSaveVFrapDataSymbols(hashTable, bioModel, derivedEDI);
		}
	};
	
	// save the ROIs, prebleach average and the first postbleach frame from memory to the database as field data
//	taskArray[4] = new AsynchClientTask("Saving roi masks, pre-bleach average and first post-bleach", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
//		public void run(Hashtable<String, Object> hashTable) throws Exception {
//			File vFrapFile = (File)hashTable.get("vFrapFile");
//			if(vFrapFile == null) {
//				return;
//			}
//			AnnotatedImageDataset annotatedImages = (AnnotatedImageDataset)hashTable.get("annotatedImages");
//			String initialFieldDataName = (String)hashTable.get("initialFieldDataName");
//
//			DocumentWindow documentWindow = (DocumentWindow)BeanUtils.findTypeParentOfComponent(DataSymbolsPanel.this, DocumentWindow.class);
//			DocumentManager dm = documentWindow.getTopLevelWindowManager().getRequestManager().getDocumentManager();
//			if(dm == null){
//				throw new RuntimeException("not connected to server");
//			}
//
//			// mesh
//			ImageDataset imageDataset = annotatedImages.getImageDataset();
//			Extent extent = imageDataset.getExtent();
//			ISize isize = imageDataset.getISize();
//			Origin origin = new Origin(0,0,0);
//			CartesianMesh cartesianMesh = CartesianMesh.createSimpleCartesianMesh(origin, extent,isize, 
//				new RegionImage( new VCImageUncompressed(null, new byte[isize.getXYZ()], extent, 
//						isize.getX(),isize.getY(),isize.getZ()),0,null,null,RegionImage.NO_SMOOTHING));
//			double[] firstPostBleach = (double[])hashTable.get("firstPostBleach");
//			double[] prebleachAvg = (double[])hashTable.get("prebleachAverage");
//			ROI[] rois = (ROI[])hashTable.get("rois");
//			
//			int NumTimePoints = 1; 
//			int NumChannels = 2+rois.length;  // prebleach, postbleach, roi1, roi2 ... roiN
//			String[] channelNames = new String[NumChannels];
//			VariableType[] channelTypes = new VariableType[NumChannels];
//			DataSymbolType[] channelVFrapImageType = new DataSymbolType[NumChannels];
//			double[][][] pixData = new double[NumTimePoints][NumChannels][]; 
//			pixData[0][0] = firstPostBleach;
//			channelNames[0] = "firstPostBleach";
//			channelTypes[0] = VariableType.VOLUME;
//			channelVFrapImageType[0] = DataSymbolType.VFRAP_FIRST_POSTBLEACH;
//			pixData[0][1] = prebleachAvg;
//			channelNames[1] = "prebleachAverage";
//			channelTypes[1] = VariableType.VOLUME;
//			channelVFrapImageType[1] = DataSymbolType.VFRAP_PREBLEACH_AVG;
//			int index = 0;
//			for (ROI roi : rois){
//				short[] ushortPixels = roi.getPixelsXYZ();
//				double[] doublePixels = new double[ushortPixels.length];
//				for (int i = 0; i < ushortPixels.length; i++) {
//					doublePixels[i] = ((int)ushortPixels[i])&0xffff;
//				}
//				pixData[0][index+2] = doublePixels;
//				channelNames[index+2] = "roi_"+index;
//				channelTypes[index+2] = VariableType.VOLUME;
//				channelVFrapImageType[index+2] = DataSymbolType.VFRAP_ROI;
//				index++;
//			}
//			double[] times = new double[] { 0.0 };
//			
//			FieldDataFileOperationSpec vfrapMiscFieldDataOpSpec = new FieldDataFileOperationSpec();
//			vfrapMiscFieldDataOpSpec.opType = FieldDataFileOperationSpec.FDOS_ADD;
//			vfrapMiscFieldDataOpSpec.cartesianMesh = cartesianMesh;
//			vfrapMiscFieldDataOpSpec.doubleSpecData =  pixData;
//			vfrapMiscFieldDataOpSpec.specEDI = null;
//			vfrapMiscFieldDataOpSpec.varNames = channelNames;
//			vfrapMiscFieldDataOpSpec.owner = dm.getUser();
//			vfrapMiscFieldDataOpSpec.times = times;
//			vfrapMiscFieldDataOpSpec.variableTypes = channelTypes;
//			vfrapMiscFieldDataOpSpec.origin = origin;
//			vfrapMiscFieldDataOpSpec.extent = extent;
//			vfrapMiscFieldDataOpSpec.isize = isize;
//	   		//  localWorkspace.getDataSetControllerImpl().fieldDataFileOperation(fdos);
//
////			String formattedDate = (String)hashTable.get("formattedDate");
////			String fieldDataName = initialFieldDataName + "_vfrapMisc_" + formattedDate;
//			String mixedFieldDataName = (String)hashTable.get("mixedFieldDataName");
//	   		ExternalDataIdentifier vfrapMisc = dm.saveFieldData(vfrapMiscFieldDataOpSpec, mixedFieldDataName);
//
//			hashTable.put("channelNames", channelNames);
//			hashTable.put("channelVFrapImageType", channelVFrapImageType);
//			hashTable.put("vfrapMisc", vfrapMisc);
//		}
//	};
	
	// create the data symbols for the images saved above and display them in the tree/table
//	taskArray[5] = new AsynchClientTask("Display Data Symbols", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
//		public void run(Hashtable<String, Object> hashTable) throws Exception {
//			
//	   		// --- create the data symbols associated with the time series
//			String initialFieldDataName = (String)hashTable.get("initialFieldDataName");
//			String mixedFieldDataName = (String)hashTable.get("mixedFieldDataName");
//			String formattedDate = (String)hashTable.get("formattedDate");
//			ImageDataset imageDataset = (ImageDataset)hashTable.get("imageDataset");
//			ExternalDataIdentifier timeSeriesEDI = (ExternalDataIdentifier)hashTable.get("timeSeriesEDI");
//			
//	   		for (double time : imageDataset.getImageTimeStamps()){
////	   			String fluorName = TokenMangler.fixTokenStrict("fluor_"+time+"_");
////				while (simulationContext.getDataContext().getDataSymbol(fluorName)!=null){
////					fluorName = TokenMangler.getNextEnumeratedToken(fluorName);
////	   			}
//	   	        DecimalFormat df = new  DecimalFormat("0.##");
////	   			String fluorName = "fluor_" + df.format(time) + "_" + formattedDate;
//	   			String fluorName = "fluor_" + df.format(time) + "_" + initialFieldDataName;
////				FieldFunctionArguments fluorFFArgs = new FieldFunctionArguments(timeSeriesEDI.getName(), fluorName, new Expression(time), VariableType.VOLUME);
//				DataSymbol fluorDataSymbol = new FieldDataSymbol( fluorName, DataSymbolType.VFRAP_TIMEPOINT, 
//						simulationContext.getDataContext(), VCUnitDefinition.UNIT_TBD,
//						timeSeriesEDI, fluorName, VariableType.VOLUME.getTypeName(), time);
//				simulationContext.getDataContext().addDataSymbol(fluorDataSymbol);
//	   		}
//	   		// --- create the vFrap-specific symbols (ROIs, first postbleach, prebleach average)
//			String[] channelNames = (String[])hashTable.get("channelNames");
//			DataSymbolType[] channelVFrapImageType = (DataSymbolType[])hashTable.get("channelVFrapImageType");
//			ExternalDataIdentifier vfrapMisc = (ExternalDataIdentifier)hashTable.get("vfrapMisc");
//
//			for (int i=0; i<channelNames.length; i++) {
//	   			String dataSymbolID = channelNames[i] + "_" + mixedFieldDataName;
////				while (simulationContext.getDataContext().getDataSymbol(dataSymbolID)!=null){
////					dataSymbolID = TokenMangler.getNextEnumeratedToken(dataSymbolID);
////				}
////				FieldFunctionArguments prebleachFFArgs = new FieldFunctionArguments(vfrapMisc.getName(), channelNames[i], new Expression(0.0), VariableType.VOLUME);
////				DataSymbol dataSymbol = new FieldDataSymbol(channelNames[i], channelVFrapImageType[i],
////				DataSymbol dataSymbol = new FieldDataSymbol(dataSymbolID + "_" + formattedDate, channelVFrapImageType[i],
//				DataSymbol dataSymbol = new FieldDataSymbol(dataSymbolID, channelVFrapImageType[i],
//						simulationContext.getDataContext(), VCUnitDefinition.UNIT_TBD,
//						vfrapMisc, channelNames[i], VariableType.VOLUME.getTypeName(), 0D);
//				simulationContext.getDataContext().addDataSymbol(dataSymbol);
//	   		}
//		}
//	};
	Hashtable<String, Object> hash = new Hashtable<String, Object>();
	ClientTaskDispatcher.dispatch(this, hash, taskArray, false, true, null);
}

/**
 * Return the JPopupMenu1 property value.
 * @return javax.swing.JPopupMenu
 */
private javax.swing.JPopupMenu getJPopupMenuICP() {
	if (ivjJPopupMenuICP == null) {
		try {
			ivjJPopupMenuICP = new javax.swing.JPopupMenu();
			ivjJPopupMenuICP.setName("JPopupMenuICP");
			ivjJPopupMenuICP.setLabel("DataSymbols");
			for (DataSymbolNewMenuItem item : DataSymbolNewMenuItem.values()) {
				JMenuItem menuItem = new JMenuItem(item.name);
				ivjJPopupMenuICP.add(menuItem);
				menuItem.addActionListener(ivjEventHandler);
				item.menuItem = menuItem;
			}
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPopupMenuICP;
}

/**
 * Return the SpeciesContextSpecsTableModel property value.
 * @return cbit.vcell.mapping.gui.SpeciesContextSpecsTableModel
 */
@Override
protected BioModelEditorApplicationRightSideTableModel<DataSymbol> createTableModel() {
	return new DataSymbolsTableModel(table);
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION --------- in cbit.vcell.mapping.InitialConditionPanel");
	exception.printStackTrace(System.out);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		setName("DataSymbolsPanel");
		addNewButton.setIcon(new DownArrowIcon());
		addNewButton.setHorizontalTextPosition(SwingConstants.LEFT);
		//setSize(456, 539);

		setLayout(new GridBagLayout());
		int gridy = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(4,4,4,4);
		add(new JLabel("Search "), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = gridy;
		gbc.weightx = 1.0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4,4,4,4);
		add(textFieldSearch, gbc);
				
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = gridy;
		gbc.insets = new Insets(4,50,4,4);
		gbc.anchor = GridBagConstraints.LINE_END;
		add(addNewButton, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 4;
		gbc.insets = new Insets(4,4,4,4);
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.LINE_END;
		add(deleteButton, gbc);
		
		gridy ++;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.insets = new Insets(4,4,4,4);
		gbc.gridy = gridy;
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.gridwidth = 6;
		gbc.fill = GridBagConstraints.BOTH;
		add(table.getEnclosingScrollPane(), gbc);
		
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/*
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		DataSymbolsPanel aDataSymbolsPanel;
		aDataSymbolsPanel = new DataSymbolsPanel(null);
		frame.setContentPane(aDataSymbolsPanel);
		frame.setSize(aDataSymbolsPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
*/

public class ExtensionsManagement {
    public final static String jpeg = "jpeg";
    public final static String jpg = "jpg";
    public final static String gif = "gif";
    public final static String tiff = "tiff";
    public final static String tif = "tif";
    public final static String png = "png";
    public final static String vfrap = "vfrap";
    
    public String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
}

public class vFrapFieldDataFilter extends FileFilter {

    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        ExtensionsManagement u = new ExtensionsManagement();
        String extension = u.getExtension(f);
        if (extension != null) {
            if (extension.equals(ExtensionsManagement.vfrap) ) {
                    return true;
            } else {
                return false;
            }
        }
        return false;
    }
    //The description of this filter
    public String getDescription() {
        return "File Formats accepted as Field Data from vFrap";
    }
}

@Override
protected void newButtonPressed() {
	getJPopupMenuICP().show(addNewButton, 0, addNewButton.getHeight());
}

@Override
protected void deleteButtonPressed() {
	int selectedIndex = table.getSelectionModel().getMaxSelectionIndex();
	DataSymbol dataSymbol = tableModel.getValueAt(selectedIndex);
	try {
		simulationContext.getDataContext().removeDataSymbol(dataSymbol);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

}
