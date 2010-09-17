package cbit.vcell.mapping.gui;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Robot;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableCellRenderer;

import oracle.sql.DATE;

import org.jdom.Element;
import org.junit.runner.Request;
import org.vcell.util.BeanUtils;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.TokenMangler;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.BioModelChildSummary;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.Version;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.sorttable.JSortTable;

import cbit.gui.ScopedExpression;
import cbit.gui.TableCellEditorAutoCompletion;
import cbit.image.VCImageUncompressed;
import cbit.util.xml.XmlUtil;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.VirtualMicroscopy.importer.AnnotatedImageDataset;
import cbit.vcell.VirtualMicroscopy.importer.MicroscopyXmlReader;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.DocumentWindow;
import cbit.vcell.client.desktop.biomodel.SPPRPanel;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.data.DataSymbol;
import cbit.vcell.data.FieldDataSymbol;
import cbit.vcell.data.DataSymbol.DataSymbolType;
import cbit.vcell.field.FieldDataFileOperationSpec;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.geometry.gui.OverlayEditorPanelJAI;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.simdata.VariableType;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.xml.VFrapXmlHelper;
import cbit.vcell.xml.XMLTags;
import cbit.vcell.xml.XmlReader;

/**
 * This type was created in VisualAge.
 */
public class DataSymbolsPanel extends javax.swing.JPanel {
	
	JFileChooser fc = null;
//	private SpeciesContextSpecPanel ivjSpeciesContextSpecPanel = null;
	private DataSymbolsSpecPanel ivjDataSymbolsSpecPanel = null;
	private SimulationContext fieldSimulationContext = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private SPPRPanel spprPanel = null;
	private JPanel scrollPanel = null; // added in July, 2008. Used to accommodate the radio buttons and the ivjJScrollPane1. 
	private JSortTable ivjScrollPaneTable = null;
	private NewDataSymbolPanel ivjNewDataSymbolPanel = null;
	private DataSymbolsTableModel ivjDataSymbolsTableModel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JSplitPane ivjJSplitPane1 = null;
	private javax.swing.JMenuItem ivjJMenuItemGenericAdd = null;
	private javax.swing.JMenuItem ivjJMenuItemVFrapAdd = null;
	private javax.swing.JPopupMenu ivjJPopupMenuICP = null;
	private javax.swing.JMenuItem ivjJMenuItemDelete = null;
	
	class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.MouseListener, java.beans.PropertyChangeListener, javax.swing.event.ListSelectionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == DataSymbolsPanel.this.getJMenuItemGenericAdd()) 
				addGenericDataSymbol();
			if (e.getSource() == DataSymbolsPanel.this.getJMenuItemVFrapAdd()) 
				addVFrapDataSymbol();
			if (e.getSource() == DataSymbolsPanel.this.getJMenuItemDelete()){
				int selectedIndex = getScrollPaneTable().getSelectionModel().getMaxSelectionIndex();
				DataSymbol dataSymbol = getDataSymbolsTableModel().getDataSymbol(selectedIndex);
				removeDataSymbol(dataSymbol);
			}
		};
		public void mouseClicked(java.awt.event.MouseEvent e) {
		};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {
		};
		public void mouseReleased(java.awt.event.MouseEvent e) {
			if (e.getSource() == DataSymbolsPanel.this.getScrollPaneTable()) 
				connEtoC4(e);
		};
		public void valueChanged(javax.swing.event.ListSelectionEvent e) {
			if (e.getSource() == DataSymbolsPanel.this.getScrollPaneTable().getSelectionModel()) 
				handleListEvent(e);
		}
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == DataSymbolsPanel.this && (evt.getPropertyName().equals("simulationContext"))) 
			{
				setSimulationContext((SimulationContext)evt.getNewValue());
			}
		};
	};

public DataSymbolsPanel(SPPRPanel aPanel) {
	super();
	spprPanel = aPanel;
	initialize();
}

private SPPRPanel getSPPRPanel() {
	return spprPanel;
}

/**
 * connEtoC4:  (ScrollPaneTable.mouse.mouseReleased(java.awt.event.MouseEvent) --> DataSymbolsPanel.scrollPaneTable_MouseClicked(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
private void connEtoC4(java.awt.event.MouseEvent arg1) {
	try {
		this.scrollPaneTable_MouseButton(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

public NewDataSymbolPanel getNewDataSymbolPanel() {
	if (ivjNewDataSymbolPanel == null) {
		try {
			ivjNewDataSymbolPanel = new NewDataSymbolPanel();
			ivjNewDataSymbolPanel.setName("NewDataSymbolPanel");
			ivjNewDataSymbolPanel.setLocation(328, 460);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjNewDataSymbolPanel;
}

public void addGenericDataSymbol() {
//	if(true){
//		throw new RuntimeException("to be implemented");
//	}
	
	AsynchClientTask[] taskArray = new AsynchClientTask[2];
	// select the desired vfrap file 
	taskArray[0] = new AsynchClientTask("Select a file", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			int returnVal = fc.showOpenDialog(DataSymbolsPanel.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File imageFile = fc.getSelectedFile();
				if (imageFile == null) {
					return;
				}
				hashTable.put("vFrapFile", imageFile);
			} else {
				throw UserCancelException.CANCEL_GENERIC;
			}
		}
	};
	taskArray[1] = new AsynchClientTask("Import objects", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		public void run(Hashtable<String, Object> hashTable) throws Exception {
		
			File vFrapFile = (File)hashTable.get("vFrapFile");
			if(vFrapFile == null) {
				return;
			}
			String vFrapFileNameExtended = null;
			vFrapFileNameExtended = vFrapFile.getName();
			if(vFrapFileNameExtended.indexOf(".vfrap") > -1)
			{
				String vFrapFileName = vFrapFileNameExtended.substring(0, vFrapFileNameExtended.indexOf(".vfrap"));
				
				// read the biomodel from Virtual FRAP xml file
				System.out.println("Loading " + vFrapFileNameExtended + " ...");
	                
				String xmlString = XmlUtil.getXMLString(vFrapFile.getAbsolutePath());
				// loading frap images
				MicroscopyXmlReader xmlReader = new MicroscopyXmlReader(true);
				Element vFrapRoot = XmlUtil.stringToXML(xmlString, null).getRootElement();
				
				BioModel bioModel = null;
				XmlReader vcellXMLReader = new XmlReader(true);
				Element bioModelElement = vFrapRoot.getChild(XMLTags.BioModelTag);
				if (bioModelElement!=null){
					bioModel = vcellXMLReader.getBioModel(bioModelElement);
				}
				
				Calendar cal = Calendar.getInstance();
			    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_hhmmss");
				DocumentWindow documentWindow = (DocumentWindow)BeanUtils.findTypeParentOfComponent(DataSymbolsPanel.this, DocumentWindow.class);
				DocumentManager documentManager = documentWindow.getTopLevelWindowManager().getRequestManager().getDocumentManager();
				VFrapXmlHelper vFrapXmlHelper = new VFrapXmlHelper();
				if(vFrapXmlHelper.isAlreadyImported(vFrapFileName, documentManager)) {
					throw new RuntimeException("FieldData name already in use.");
				}
//				bioModel.setName(vFrapFileName + "-" + sdf.format(cal.getTime()));
				bioModel.setName(vFrapFileName);
				BioModel feedbackModel =  documentManager.save(bioModel, null);
				BioModelChildSummary  childSummary = BioModelChildSummary.fromDatabaseSerialization(xmlString);
				BioModelInfo biomodelInfo = new BioModelInfo(feedbackModel.getVersion(), feedbackModel.getVersion().getVersionKey(), childSummary );
				documentWindow.getTopLevelWindowManager().getRequestManager().openDocument(biomodelInfo, documentWindow.getTopLevelWindowManager(), true);
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
////					getSimulationContext().getDataContext(), VCUnitDefinition.UNIT_TBD, 
////					new FieldFunctionArguments(functionInvocations[0]));
//			DataSymbol ds = new FieldDataSymbol(name, DataSymbolType.GENERIC_SYMBOL,
//					getSimulationContext().getDataContext(), VCUnitDefinition.UNIT_TBD);
//			getSimulationContext().getDataContext().addDataSymbol(ds);
//		}
//	} catch (java.lang.Throwable ivjExc) {
//		DialogUtils.showErrorDialog(this, "Data symbol " + name + " already exists");
//	}
}
public void addVFrapDataSymbol() {
	AsynchClientTask[] taskArray = new AsynchClientTask[6];
	// select the desired vfrap file 
	taskArray[0] = new AsynchClientTask("Select a file", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			int returnVal = fc.showOpenDialog(DataSymbolsPanel.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File imageFile = fc.getSelectedFile();
				if (imageFile == null) {
					return;
				}
				hashTable.put("imageFile", imageFile);
			} else {
				throw UserCancelException.CANCEL_GENERIC;
			}
		}
	};
	// load the images from the vfrap file, compute derived images, store them all in memory
	taskArray[1] = new AsynchClientTask("Import images", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		public void run(Hashtable<String, Object> hashTable) throws Exception {
		
			File imageFile = (File)hashTable.get("imageFile");
			if(imageFile == null) {
				return;
			}
			String initFDNameEx = imageFile.getName();
			if(initFDNameEx.indexOf(".vfrap") > -1)
			{
				// check whether the name is already in use in the Fielddata database
				String initialFieldDataName = initFDNameEx.substring(0, initFDNameEx.indexOf(".vfrap"));
				DocumentWindow documentWindow = (DocumentWindow)BeanUtils.findTypeParentOfComponent(DataSymbolsPanel.this, DocumentWindow.class);
				DocumentManager documentManager = documentWindow.getTopLevelWindowManager().getRequestManager().getDocumentManager();
				if(documentManager == null){
					throw new RuntimeException("Not connected to server.");
				}
				VFrapXmlHelper vFrapXmlHelper = new VFrapXmlHelper();
				if(vFrapXmlHelper.isAlreadyImported(initialFieldDataName, documentManager)) {
					hashTable.put("initialFieldDataName", "");
					return;
				}
				String mixedFieldDataName = initialFieldDataName + "Mx";
				if(vFrapXmlHelper.isAlreadyImported(mixedFieldDataName, documentManager)) {
					hashTable.put("mixedFieldDataName", "");
					return;
				}
				
				// read the image dataset from Virtual FRAP xml file
				System.out.println("Loading " + initFDNameEx + " ...");
				String xmlString = XmlUtil.getXMLString(imageFile.getAbsolutePath());
				// loading frap images
				MicroscopyXmlReader xmlReader = new MicroscopyXmlReader(true);
				AnnotatedImageDataset annotatedImages = xmlReader.getAnnotatedImageDataset(XmlUtil.stringToXML(xmlString, null).getRootElement(), null);
				//loading primary ROIs
				ROI rois[] = xmlReader.getPrimaryROIs(XmlUtil.stringToXML(xmlString, null).getRootElement(), null);
				int startingIndexRecovery = xmlReader.getStartindIndexForRecovery(XmlUtil.stringToXML(xmlString, null).getRootElement());
				
				vFrapXmlHelper.LoadVFrapSpecialImages(annotatedImages, startingIndexRecovery);	// prebleach average and first postbleach
				vFrapXmlHelper.LoadVFrapRoiCompositeImages(annotatedImages, rois);

				hashTable.put("firstPostBleach",vFrapXmlHelper.getFirstPostBleach());
				hashTable.put("prebleachAverage",vFrapXmlHelper.getPrebleachAvg());
				hashTable.put("roiComposite",vFrapXmlHelper.getRoiComposite());
				hashTable.put("rois",rois);
				hashTable.put("annotatedImages",annotatedImages);
				hashTable.put("initialFieldDataName", initialFieldDataName);
				hashTable.put("mixedFieldDataName", mixedFieldDataName);
			}
		}
	};
	
	// show the images from the vfrap file in an OverlayEditorPanelJAI dialog
	taskArray[2] = new AsynchClientTask("Display images", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			
			File imageFile = (File)hashTable.get("imageFile");
			if(imageFile == null) {
				return;
			}
			String initialFieldDataName = (String)hashTable.get("initialFieldDataName");
			String mixedFieldDataName = (String)hashTable.get("mixedFieldDataName");
			if(initialFieldDataName.equals("") || mixedFieldDataName.equals("")) {
				JOptionPane.showMessageDialog(DataSymbolsPanel.this, "Field Data name " + initialFieldDataName + " already in use."); 					
				// prevents the rest of tasks below from running
				throw UserCancelException.CANCEL_GENERIC;
			}

			AnnotatedImageDataset annotatedImages = (AnnotatedImageDataset)hashTable.get("annotatedImages");
			BufferedImage[] roiComposite = (BufferedImage[])hashTable.get("roiComposite");
			if (annotatedImages==null || roiComposite==null){
				return;
			}
			// display the images
			OverlayEditorPanelJAI overlayPanel = new OverlayEditorPanelJAI();
			overlayPanel.setAllowAddROI(false);
			ImageDataset imageDataset = annotatedImages.getImageDataset();
			overlayPanel.setImages(imageDataset, 1, 0, new OverlayEditorPanelJAI.AllPixelValuesRange(1, 200) );
			overlayPanel.setAllROICompositeImage(roiComposite, OverlayEditorPanelJAI.FRAP_DATA_INIT_PROPERTY);
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
			File imageFile = (File)hashTable.get("imageFile");
			if(imageFile == null) {
				return;
			}
			AnnotatedImageDataset annotatedImages = (AnnotatedImageDataset)hashTable.get("annotatedImages");
			String initialFieldDataName = (String)hashTable.get("initialFieldDataName");

			DocumentWindow documentWindow = (DocumentWindow)BeanUtils.findTypeParentOfComponent(DataSymbolsPanel.this, DocumentWindow.class);
			DocumentManager dm = documentWindow.getTopLevelWindowManager().getRequestManager().getDocumentManager();
			if(dm == null){
				throw new RuntimeException("Not connected to server.");
			}
			
			User owner = null;
			Version version = fieldSimulationContext.getVersion();
			if(version == null) {		// new document, so the owner is the user
				owner = dm.getUser();
			} else {
				owner = fieldSimulationContext.getVersion().getOwner();
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
			timeSeriesFieldDataOpSpec.varNames = new String[] {"fluor"};
//				owner = new User("danv", new KeyValue("1234567890"));
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
	
	// save the ROIs, prebleach average and the first postbleach frame from memory to the database as field data
	taskArray[4] = new AsynchClientTask("Saving roi masks, pre-bleach average and first post-bleach", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			File imageFile = (File)hashTable.get("imageFile");
			if(imageFile == null) {
				return;
			}
			AnnotatedImageDataset annotatedImages = (AnnotatedImageDataset)hashTable.get("annotatedImages");
			String initialFieldDataName = (String)hashTable.get("initialFieldDataName");

			DocumentWindow documentWindow = (DocumentWindow)BeanUtils.findTypeParentOfComponent(DataSymbolsPanel.this, DocumentWindow.class);
			DocumentManager dm = documentWindow.getTopLevelWindowManager().getRequestManager().getDocumentManager();
			if(dm == null){
				throw new RuntimeException("not connected to server");
			}

			// mesh
			ImageDataset imageDataset = annotatedImages.getImageDataset();
			Extent extent = imageDataset.getExtent();
			ISize isize = imageDataset.getISize();
			Origin origin = new Origin(0,0,0);
			CartesianMesh cartesianMesh = CartesianMesh.createSimpleCartesianMesh(origin, extent,isize, 
				new RegionImage( new VCImageUncompressed(null, new byte[isize.getXYZ()], extent, 
						isize.getX(),isize.getY(),isize.getZ()),0,null,null,RegionImage.NO_SMOOTHING));
			double[] firstPostBleach = (double[])hashTable.get("firstPostBleach");
			double[] prebleachAvg = (double[])hashTable.get("prebleachAverage");
			ROI[] rois = (ROI[])hashTable.get("rois");
			
			int NumTimePoints = 1; 
			int NumChannels = 2+rois.length;  // prebleach, postbleach, roi1, roi2 ... roiN
			String[] channelNames = new String[NumChannels];
			VariableType[] channelTypes = new VariableType[NumChannels];
			DataSymbolType[] channelVFrapImageType = new DataSymbolType[NumChannels];
			double[][][] pixData = new double[NumTimePoints][NumChannels][]; 
			pixData[0][0] = firstPostBleach;
			channelNames[0] = "firstPostBleach";
			channelTypes[0] = VariableType.VOLUME;
			channelVFrapImageType[0] = DataSymbolType.VFRAP_FIRST_POSTBLEACH;
			pixData[0][1] = prebleachAvg;
			channelNames[1] = "prebleachAverage";
			channelTypes[1] = VariableType.VOLUME;
			channelVFrapImageType[1] = DataSymbolType.VFRAP_PREBLEACH_AVG;
			int index = 0;
			for (ROI roi : rois){
				short[] ushortPixels = roi.getPixelsXYZ();
				double[] doublePixels = new double[ushortPixels.length];
				for (int i = 0; i < ushortPixels.length; i++) {
					doublePixels[i] = ((int)ushortPixels[i])&0xffff;
				}
				pixData[0][index+2] = doublePixels;
				channelNames[index+2] = "roi_"+index;
				channelTypes[index+2] = VariableType.VOLUME;
				channelVFrapImageType[index+2] = DataSymbolType.VFRAP_ROI;
				index++;
			}
			double[] times = new double[] { 0.0 };
			
			FieldDataFileOperationSpec vfrapMiscFieldDataOpSpec = new FieldDataFileOperationSpec();
			vfrapMiscFieldDataOpSpec.opType = FieldDataFileOperationSpec.FDOS_ADD;
			vfrapMiscFieldDataOpSpec.cartesianMesh = cartesianMesh;
			vfrapMiscFieldDataOpSpec.doubleSpecData =  pixData;
			vfrapMiscFieldDataOpSpec.specEDI = null;
			vfrapMiscFieldDataOpSpec.varNames = channelNames;
			vfrapMiscFieldDataOpSpec.owner = dm.getUser();
			vfrapMiscFieldDataOpSpec.times = times;
			vfrapMiscFieldDataOpSpec.variableTypes = channelTypes;
			vfrapMiscFieldDataOpSpec.origin = origin;
			vfrapMiscFieldDataOpSpec.extent = extent;
			vfrapMiscFieldDataOpSpec.isize = isize;
	   		//  localWorkspace.getDataSetControllerImpl().fieldDataFileOperation(fdos);

//			String formattedDate = (String)hashTable.get("formattedDate");
//			String fieldDataName = initialFieldDataName + "_vfrapMisc_" + formattedDate;
			String mixedFieldDataName = (String)hashTable.get("mixedFieldDataName");
	   		ExternalDataIdentifier vfrapMisc = dm.saveFieldData(vfrapMiscFieldDataOpSpec, mixedFieldDataName);

			hashTable.put("channelNames", channelNames);
			hashTable.put("channelVFrapImageType", channelVFrapImageType);
			hashTable.put("vfrapMisc", vfrapMisc);
		}
	};
	
	// create the data symbols for the images saved above and display them in the tree/table
	taskArray[5] = new AsynchClientTask("Display Data Symbols", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			
	   		// --- create the data symbols associated with the time series
			String initialFieldDataName = (String)hashTable.get("initialFieldDataName");
			String mixedFieldDataName = (String)hashTable.get("mixedFieldDataName");
			String formattedDate = (String)hashTable.get("formattedDate");
			ImageDataset imageDataset = (ImageDataset)hashTable.get("imageDataset");
			ExternalDataIdentifier timeSeriesEDI = (ExternalDataIdentifier)hashTable.get("timeSeriesEDI");
			
	   		for (double time : imageDataset.getImageTimeStamps()){
//	   			String fluorName = TokenMangler.fixTokenStrict("fluor_"+time+"_");
//				while (getSimulationContext().getDataContext().getDataSymbol(fluorName)!=null){
//					fluorName = TokenMangler.getNextEnumeratedToken(fluorName);
//	   			}
	   	        DecimalFormat df = new  DecimalFormat("0.##");
//	   			String fluorName = "fluor_" + df.format(time) + "_" + formattedDate;
	   			String fluorName = "fluor_" + df.format(time) + "_" + initialFieldDataName;
//				FieldFunctionArguments fluorFFArgs = new FieldFunctionArguments(timeSeriesEDI.getName(), fluorName, new Expression(time), VariableType.VOLUME);
				DataSymbol fluorDataSymbol = new FieldDataSymbol( fluorName, DataSymbolType.VFRAP_TIMEPOINT, 
						getSimulationContext().getDataContext(), VCUnitDefinition.UNIT_TBD,
						timeSeriesEDI, fluorName, VariableType.VOLUME.getTypeName(), time);
				getSimulationContext().getDataContext().addDataSymbol(fluorDataSymbol);
	   		}
	   		// --- create the vFrap-specific symbols (ROIs, first postbleach, prebleach average)
			String[] channelNames = (String[])hashTable.get("channelNames");
			DataSymbolType[] channelVFrapImageType = (DataSymbolType[])hashTable.get("channelVFrapImageType");
			ExternalDataIdentifier vfrapMisc = (ExternalDataIdentifier)hashTable.get("vfrapMisc");

			for (int i=0; i<channelNames.length; i++) {
	   			String dataSymbolID = channelNames[i] + "_" + mixedFieldDataName;
//				while (getSimulationContext().getDataContext().getDataSymbol(dataSymbolID)!=null){
//					dataSymbolID = TokenMangler.getNextEnumeratedToken(dataSymbolID);
//				}
//				FieldFunctionArguments prebleachFFArgs = new FieldFunctionArguments(vfrapMisc.getName(), channelNames[i], new Expression(0.0), VariableType.VOLUME);
//				DataSymbol dataSymbol = new FieldDataSymbol(channelNames[i], channelVFrapImageType[i],
//				DataSymbol dataSymbol = new FieldDataSymbol(dataSymbolID + "_" + formattedDate, channelVFrapImageType[i],
				DataSymbol dataSymbol = new FieldDataSymbol(dataSymbolID, channelVFrapImageType[i],
						getSimulationContext().getDataContext(), VCUnitDefinition.UNIT_TBD,
						vfrapMisc, channelNames[i], VariableType.VOLUME.getTypeName(), 0D);
				getSimulationContext().getDataContext().addDataSymbol(dataSymbol);
	   		}
		}
	};
	Hashtable<String, Object> hash = new Hashtable<String, Object>();
	ClientTaskDispatcher.dispatch(this, hash, taskArray, false, true, null);
}

private void removeDataSymbol(DataSymbol dataSymbol) {
	try {
		getSimulationContext().getDataContext().removeDataSymbol(dataSymbol);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connEtoM3:  (selectionModel1.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> SpeciesContextSpecPanel.setSpeciesContextSpec(Lcbit.vcell.mapping.SpeciesContextSpec;)V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
private void handleListEvent(javax.swing.event.ListSelectionEvent arg1) {
	try {
		int row = getScrollPaneTable().getSelectionModel().getMinSelectionIndex();
		if (row < 0) {
			getDataSymbolsSpecPanel().setDataSymbol(null);
		} else {
			getDataSymbolsSpecPanel().setDataSymbol(getDataSymbolsTableModel().getDataSymbol(row));
//			System.out.println("Initial condition selection changed");
			if(getSPPRPanel() != null) {
				getSPPRPanel().setScrollPaneTreeCurrentRow(getDataSymbolsTableModel().getDataSymbol(row));
			}
		}
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

private javax.swing.JMenuItem getJMenuItemGenericAdd() {
	if (ivjJMenuItemGenericAdd == null) {
		try {
			ivjJMenuItemGenericAdd = new javax.swing.JMenuItem();
			ivjJMenuItemGenericAdd.setName("JMenuItemGenericAdd");
			ivjJMenuItemGenericAdd.setText("Add Generic DataSymbol");
//			ivjJMenuItemGenericAdd.setText("Import vFrap Simulation");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemGenericAdd;
}
private javax.swing.JMenuItem getJMenuItemVFrapAdd() {
	if (ivjJMenuItemVFrapAdd == null) {
		try {
			ivjJMenuItemVFrapAdd = new javax.swing.JMenuItem();
			ivjJMenuItemVFrapAdd.setName("JMenuItemVFrapAdd");
			ivjJMenuItemVFrapAdd.setText("Add vFrap DataSymbols");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemVFrapAdd;
}

private javax.swing.JMenuItem getJMenuItemDelete() {
	if (ivjJMenuItemDelete == null) {
		try {
			ivjJMenuItemDelete = new javax.swing.JMenuItem();
			ivjJMenuItemDelete.setName("JMenuItemDelete");
			ivjJMenuItemDelete.setText("Delete");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemDelete;
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
			ivjJPopupMenuICP.add(getJMenuItemGenericAdd());
			ivjJPopupMenuICP.add(getJMenuItemVFrapAdd());
			ivjJPopupMenuICP.add(getJMenuItemDelete());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPopupMenuICP;
}

/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			ivjJScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getJScrollPane1().setViewportView(getScrollPaneTable());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
}

/**
 * Return the JSplitPane1 property value.
 * @return javax.swing.JSplitPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSplitPane getJSplitPane1() {
	if (ivjJSplitPane1 == null) {
		try {
			ivjJSplitPane1 = new javax.swing.JSplitPane(javax.swing.JSplitPane.VERTICAL_SPLIT);
			ivjJSplitPane1.setName("JSplitPane1");
			ivjJSplitPane1.setDividerLocation(300);
			getJSplitPane1().add(getScrollPanel(), "top");
			getJSplitPane1().add(getDataSymbolsSpecPanel(), "bottom");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSplitPane1;
}

// added in july 2008, to accommodate the radio buttons and the scrolltablepane when it is stochastic application.
private JPanel getScrollPanel()
{
	if(scrollPanel == null)
	{
		scrollPanel = new JPanel(new BorderLayout());
		scrollPanel.add(getJScrollPane1(), BorderLayout.CENTER);
	}
	
	return scrollPanel;
}

/**
 * Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
 */
private JSortTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new JSortTable();
			ivjScrollPaneTable.setName("ScrollPaneTable");
			getJScrollPane1().setColumnHeaderView(ivjScrollPaneTable.getTableHeader());
			ivjScrollPaneTable.setBounds(0, 0, 200, 200);
			ivjScrollPaneTable.setAutoCreateColumnsFromModel(true);
			ivjScrollPaneTable.setRowHeight(ivjScrollPaneTable.getRowHeight() + 2);
			getScrollPaneTable().setSelectionModel(getScrollPaneTable().getSelectionModel());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjScrollPaneTable;
}


/**
 * Gets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @return The simulationContext property value.
 * @see #setSimulationContext
 */
public SimulationContext getSimulationContext() {
	return fieldSimulationContext;
}


/**
 * Return the SpeciesContextSpecPanel property value.
 * @return cbit.vcell.mapping.SpeciesContextSpecPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private DataSymbolsSpecPanel getDataSymbolsSpecPanel() {
	if (ivjDataSymbolsSpecPanel == null) {
		try {
			ivjDataSymbolsSpecPanel = new DataSymbolsSpecPanel();
			ivjDataSymbolsSpecPanel.setName("DataSymbolsSpecPanel");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDataSymbolsSpecPanel;
}

/**
 * Return the SpeciesContextSpecsTableModel property value.
 * @return cbit.vcell.mapping.gui.SpeciesContextSpecsTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private DataSymbolsTableModel getDataSymbolsTableModel() {
	if (ivjDataSymbolsTableModel == null) {
		try {
			ivjDataSymbolsTableModel = new DataSymbolsTableModel(getScrollPaneTable());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDataSymbolsTableModel;
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
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	this.addPropertyChangeListener(ivjEventHandler);
	getScrollPaneTable().addPropertyChangeListener(ivjEventHandler);
	getScrollPaneTable().addMouseListener(ivjEventHandler);
	getJMenuItemGenericAdd().addActionListener(ivjEventHandler);
	getJMenuItemVFrapAdd().addActionListener(ivjEventHandler);
	getJMenuItemDelete().addActionListener(ivjEventHandler);
	getScrollPaneTable().setModel(getDataSymbolsTableModel());
	getScrollPaneTable().createDefaultColumnsFromModel();
	getScrollPaneTable().setDefaultEditor(ScopedExpression.class,new TableCellEditorAutoCompletion(getScrollPaneTable(), true));
	getScrollPaneTable().getSelectionModel().addListSelectionListener(ivjEventHandler);

	DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (value instanceof Species) {
				setText(((Species)value).getCommonName());
			} else if (value instanceof SpeciesContext) {
				setText(((SpeciesContext)value).getName());
			} else if (value instanceof Structure) {
				setText(((Structure)value).getName());
			}
			return this;
		}
	};
	getScrollPaneTable().setDefaultRenderer(SpeciesContext.class, renderer);
	getScrollPaneTable().setDefaultRenderer(Structure.class, renderer);
	getScrollPaneTable().setDefaultRenderer(Species.class, renderer);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		setName("DataSymbolsPanel");
		setLayout(new java.awt.GridBagLayout());
		//setSize(456, 539);

		//Create a file chooser
		fc = new JFileChooser();
		vFrapFieldDataFilter filter = new vFrapFieldDataFilter();
		fc.setFileFilter(filter);

		java.awt.GridBagConstraints constraintsJSplitPane1 = new java.awt.GridBagConstraints();
		constraintsJSplitPane1.gridx = 0; constraintsJSplitPane1.gridy = 0;
		constraintsJSplitPane1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJSplitPane1.weightx = 1.0;
		constraintsJSplitPane1.weighty = 1.0;
		constraintsJSplitPane1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJSplitPane1(), constraintsJSplitPane1);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

public void setScrollPaneTableCurrentRow(DataSymbol selection) {
	if (selection == null) {
		return;
	}
	int numRows = getScrollPaneTable().getRowCount();
	for(int i=0; i<numRows; i++) {
		String valueAt = (String)getScrollPaneTable().getValueAt(i, DataSymbolsTableModel.COLUMN_DATA_SYMBOL_NAME);
		DataSymbol dataSymbol = getSimulationContext().getDataContext().getDataSymbol(valueAt);
		if(dataSymbol!=null && dataSymbol.equals(selection)) {
			getScrollPaneTable().changeSelection(i, 0, false, false);
			return;
		}
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

/**
 * Comment
 */
private void scrollPaneTable_MouseButton(final java.awt.event.MouseEvent mouseEvent) {
	if (!getScrollPaneTable().hasFocus()) {
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
					getScrollPaneTable().addFocusListener(new FocusListener() {
						
						public void focusLost(FocusEvent e) {
						}
						
						public void focusGained(FocusEvent e) {
							getScrollPaneTable().removeFocusListener(this);
							Robot robot;
							try {
								robot = new Robot();
								robot.mousePress(InputEvent.BUTTON1_MASK);
								robot.mouseRelease(InputEvent.BUTTON1_MASK);
							} catch (AWTException ex) {
								ex.printStackTrace();
							}													
						}
					});
				}
				getScrollPaneTable().requestFocus();
			}
		});	
	}
	if(mouseEvent.isPopupTrigger()){		
		boolean bSomethingSelected = getScrollPaneTable().getSelectedRows() != null && getScrollPaneTable().getSelectedRows().length > 0;
		getJMenuItemGenericAdd().setEnabled(false);
//		getJMenuItemGenericAdd().setEnabled(true);
		getJMenuItemVFrapAdd().setEnabled(true);
		getJMenuItemDelete().setEnabled(bSomethingSelected);
		getJPopupMenuICP().show(getScrollPaneTable(),mouseEvent.getX(),mouseEvent.getY());
	}
}

/**
 * Sets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @param simulationContext The new value for the property.
 * @see #getSimulationContext
 */
public void setSimulationContext(SimulationContext simulationContext) {
	SimulationContext oldValue = fieldSimulationContext;
	fieldSimulationContext = simulationContext;
	firePropertyChange("simulationContext", oldValue, simulationContext);
	getDataSymbolsTableModel().setSimulationContext(simulationContext);
}

public class Utils {
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
        Utils u = new Utils();
        String extension = u.getExtension(f);
        if (extension != null) {
            if (extension.equals(Utils.vfrap) ) {
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
}
