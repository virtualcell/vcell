/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JPanel;

import org.vcell.util.gui.DialogUtils;

import cbit.image.ImageException;
import cbit.plot.Plot2D;
import cbit.plot.PlotData;
import cbit.plot.gui.PlotPane;
import cbit.util.xml.XmlUtil;
import cbit.vcell.VirtualMicroscopy.ImageLoadingProgress;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.client.ChildWindowManager;
import cbit.vcell.client.ChildWindowManager.ChildWindow;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPDataAnalysis;
import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.LocalWorkspace;
import cbit.vcell.microscopy.MicroscopyXmlReader;

//comments added in Jan, 2008. This panel is with the first tab that users can see when VFrap is just started.
//This panel displays the images base on time serials or Z serials. In addtion, Users can mark ROIs and manipulate
//ROIs in this panel.
public class FRAPDataPanel extends JPanel implements PropertyChangeListener{

	private static final long serialVersionUID = 1L;
	private VFrap_OverlayEditorPanelJAI overlayEditorPanel = null;
	private FRAPSingleWorkspace frapWorkspace = null;  
//	private EventHandler eventHandler = new EventHandler();
	private LocalWorkspace localWorkspace = null;
	//The frap data panel can be editable or not. e.g. the frapData panel in the main frame is not editable.
	//However the frap data panel in define ROI wizard is editable
	private boolean isEditable = true;
		
	//implementation of propertychange as a propertyChangeListener
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getSource() instanceof  ImageLoadingProgress && e.getPropertyName().equals("ImageLoadingProgress"))
		{
			int prog = ((Integer)e.getNewValue()).intValue();
			VirtualFrapMainFrame.updateProgress(prog);
		}
		else if(e.getPropertyName().equals(VFrap_OverlayEditorPanelJAI.FRAP_DATA_CROP_PROPERTY)){
			try {
				crop((Rectangle) e.getNewValue());
			} catch (Exception ex) {
				PopupGenerator.showErrorDialog(this, "Error Cropping:\n"+ex.getMessage());
			}
		}
		else if (e.getPropertyName().equals(FRAPSingleWorkspace.PROPERTY_CHANGE_CURRENTLY_DISPLAYED_ROI_WITH_SAVE)){
			//Save user changes from viewer to ROI
			//To save only when the image is editable in this panel
			if(isEditable)
			{
				getOverlayEditorPanelJAI().saveUserChangesToROI();
			}
			//Set new ROI on viewer
			getOverlayEditorPanelJAI().setROI(getFrapWorkspace().getWorkingFrapStudy().getFrapData().getCurrentlyDisplayedROI());
		}
		else if (e.getPropertyName().equals(FRAPSingleWorkspace.PROPERTY_CHANGE_CURRENTLY_DISPLAYED_ROI_WITHOUT_SAVE)){
			//Set new ROI on viewer
			getOverlayEditorPanelJAI().setROI(getFrapWorkspace().getWorkingFrapStudy().getFrapData().getCurrentlyDisplayedROI());
		}
		else if (e.getPropertyName().equals(FRAPSingleWorkspace.PROPERTY_CHANGE_FRAPSTUDY_NEW) ||
				e.getPropertyName().equals(FRAPSingleWorkspace.PROPERTY_CHANGE_FRAPSTUDY_UPDATE) ||
				e.getPropertyName().equals(FRAPSingleWorkspace.PROPERTY_CHANGE_FRAPSTUDY_BATCHRUN))
		{
			if(e.getNewValue() instanceof FRAPStudy)
			{
				FRAPStudy fStudy = (FRAPStudy)e.getNewValue();
				FRAPStudy oldFrapStudy = (FRAPStudy)e.getOldValue();
				
				FRAPData fData = ((fStudy!=null)?(fStudy.getFrapData()):(null));
				FRAPData oldFrapData = (oldFrapStudy!=null)?(oldFrapStudy.getFrapData()):(null);
				
				if (oldFrapData!=null){
					oldFrapData.removePropertyChangeListener(this);
				}
				
				if (fData!=null){
					fData.addPropertyChangeListener(this);
				}
				
				if(e.getPropertyName().equals(FRAPSingleWorkspace.PROPERTY_CHANGE_FRAPSTUDY_NEW)||
				   e.getPropertyName().equals(FRAPSingleWorkspace.PROPERTY_CHANGE_FRAPSTUDY_BATCHRUN))
				{
					overlayEditorPanel.setImages(
						(fData==null?null:fData.getImageDataset()),true
						/*(fData==null || fData.getOriginalGlobalScaleInfo() == null?VFrap_OverlayEditorPanelJAI.DEFAULT_SCALE_FACTOR:fData.getOriginalGlobalScaleInfo().originalScaleFactor),
						(fData==null || fData.getOriginalGlobalScaleInfo() == null?VFrap_OverlayEditorPanelJAI.DEFAULT_OFFSET_FACTOR:fData.getOriginalGlobalScaleInfo().originalOffsetFactor)*/);
				}
				else
				{
					overlayEditorPanel.setImages(
							(fData==null?null:fData.getImageDataset()),false
							/*(fData==null || fData.getOriginalGlobalScaleInfo() == null?VFrap_OverlayEditorPanelJAI.DEFAULT_SCALE_FACTOR:fData.getOriginalGlobalScaleInfo().originalScaleFactor),
							(fData==null || fData.getOriginalGlobalScaleInfo() == null?VFrap_OverlayEditorPanelJAI.DEFAULT_OFFSET_FACTOR:fData.getOriginalGlobalScaleInfo().originalOffsetFactor)*/);
				}

				if(fData != null && fData.getROILength() > 0)
				{
					overlayEditorPanel.setRoiSouceData(fData);
					if(e.getPropertyName().equals(FRAPSingleWorkspace.PROPERTY_CHANGE_FRAPSTUDY_BATCHRUN))
					{
						fData.setCurrentlyDisplayedROIForBatchRun(fData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()));
					}
					else
					{
						fData.setCurrentlyDisplayedROI(fData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()));
					}
				}
			}
		}
		else if (e.getPropertyName().equals(FRAPSingleWorkspace.FRAPDATA_VERIFY_INFO_PROPERTY))
		{
			FRAPData fData = (FRAPData)e.getNewValue();
			overlayEditorPanel.setImages(
					(fData==null?null:fData.getImageDataset()),true
					/*(fData==null || fData.getOriginalGlobalScaleInfo() == null?VFrap_OverlayEditorPanelJAI.DEFAULT_SCALE_FACTOR:fData.getOriginalGlobalScaleInfo().originalScaleFactor),
					(fData==null || fData.getOriginalGlobalScaleInfo() == null?VFrap_OverlayEditorPanelJAI.DEFAULT_OFFSET_FACTOR:fData.getOriginalGlobalScaleInfo().originalOffsetFactor)*/);
			overlayEditorPanel.setRoiSouceData(fData);
			fData.setCurrentlyDisplayedROI(fData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()));
		}
	}
	//There are two FRAPDataPanel instances, one is in MainFrame and antoher is in DefineROIWizard
	//The crop function is called in DefineROIWizard, the image change will only be reflected in the
	//FRAPDataPanel in DefineROIWizard. The newFrapStudy will only be set to FrapdataPanel in Mainframe
	//when FINISH button is pressed.
	protected void crop(Rectangle cropRectangle) throws ImageException {
		FRAPStudy fStudy = getFrapWorkspace().getWorkingFrapStudy();
		if (fStudy == null || fStudy.getFrapData()==null){
			return;
		}
		getOverlayEditorPanelJAI().saveUserChangesToROI();
		FRAPData frapData = fStudy.getFrapData();
		FRAPData newFrapData = frapData.crop(cropRectangle);
		FRAPStudy newFrapStudy = new FRAPStudy();
		newFrapStudy.setFrapData(newFrapData);
		newFrapStudy.setXmlFilename(fStudy.getXmlFilename());
		newFrapStudy.setFrapDataExternalDataInfo(fStudy.getFrapDataExternalDataInfo());
		newFrapStudy.setRoiExternalDataInfo(fStudy.getRoiExternalDataInfo());
		newFrapStudy.setStoredRefData(fStudy.getStoredRefData());
		newFrapStudy.setModels(fStudy.getModels());
		newFrapStudy.setStartingIndexForRecovery(fStudy.getStartingIndexForRecovery());
		getFrapWorkspace().setFrapStudy(newFrapStudy,false);
	}
	/**
	 * This is the default constructor
	 */
	public FRAPDataPanel() {
		this(true);
	}
	
	/**
	 * The constructor which specifies whether the image is editable or not is the panel.
	 * Added in August, 2009
	 */
	public FRAPDataPanel(boolean arg_isEditable) {
		super();
		isEditable = arg_isEditable;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.ipadx = 0;
		gridBagConstraints1.ipady = 0;
		gridBagConstraints1.fill = GridBagConstraints.BOTH;
		gridBagConstraints1.weighty = 1.0D;
		gridBagConstraints1.weightx = 1.0D;
		gridBagConstraints1.gridy = 0;
		this.setSize(653, 492);
		this.setLayout(new GridBagLayout());
		this.add(getOverlayEditorPanelJAI(),gridBagConstraints1);
		getOverlayEditorPanelJAI().addPropertyChangeListener(
			new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent evt) {
					if(evt.getPropertyName().equals(VFrap_OverlayEditorPanelJAI.FRAP_DATA_TIMEPLOTROI_PROPERTY)){
						try {
							plotROI();
						} catch (Exception e) {
							DialogUtils.showErrorDialog(FRAPDataPanel.this, "Error Time Plot ROI:\n"+e.getMessage());
						}
					}else if(evt.getPropertyName().equals(VFrap_OverlayEditorPanelJAI.FRAP_DATA_CURRENTROI_PROPERTY)){
						try {
							String roiName = (String)evt.getNewValue();
							if(roiName != null)
							{
								getFrapWorkspace().getWorkingFrapStudy().getFrapData().setCurrentlyDisplayedROI(getFrapWorkspace().getWorkingFrapStudy().getFrapData().getRoi(roiName), false);
							}
						} catch (Exception e) {
							DialogUtils.showErrorDialog(FRAPDataPanel.this, "Error Setting Current ROI:\n"+e.getMessage());
						}						
					}else if(evt.getPropertyName().equals(VFrap_OverlayEditorPanelJAI.FRAP_DATA_UNDOROI_PROPERTY)){
						try {
							ROI undoableROI = (ROI)evt.getNewValue();
							getFrapWorkspace().getWorkingFrapStudy().getFrapData().addReplaceRoi(undoableROI);
						} catch (Exception e) {
							PopupGenerator.showErrorDialog(FRAPDataPanel.this, "Error Setting Current ROI:\n"+e.getMessage());
						}						
					}
				}
			}
		);
		VFrap_OverlayEditorPanelJAI.CustomROIImport importVFRAPROI = new VFrap_OverlayEditorPanelJAI.CustomROIImport(){
			public boolean importROI(File inputFile) throws Exception{
				try{
					if(!VirtualFrapLoader.filter_vfrap.accept(inputFile)){
						return false;
					}
					String xmlString = XmlUtil.getXMLString(inputFile.getAbsolutePath());
					MicroscopyXmlReader xmlReader = new MicroscopyXmlReader(true);

					FRAPStudy importedFrapStudy = xmlReader.getFrapStudy(XmlUtil.stringToXML(xmlString, null).getRootElement(),null);
					VirtualFrapMainFrame.updateProgress(0);
					ROI roi = getFrapWorkspace().getWorkingFrapStudy().getFrapData().getCurrentlyDisplayedROI();
					ROI[] importedROIs = importedFrapStudy.getFrapData().getRois();
					if(importedFrapStudy.getFrapData() != null && importedROIs != null){
						if(!importedROIs[0].getISize().compareEqual(roi.getISize())){
							throw new Exception(
									"Imported ROI mask size ("+
									importedROIs[0].getISize().getX()+","+
									importedROIs[0].getISize().getY()+","+
									importedROIs[0].getISize().getZ()+")"+
									" does not match current Frap DataSet size ("+
									roi.getISize().getX()+","+
									roi.getISize().getY()+","+
									roi.getISize().getZ()+
									")");
						}
						for (int i = 0; i < importedROIs.length; i++) {
							getFrapWorkspace().getWorkingFrapStudy().getFrapData().addReplaceRoi(importedROIs[i]);
						}
//						undoableEditSupport.postEdit(FRAPStudyPanel.CLEAR_UNDOABLE_EDIT);
					}
					return true;
				} catch (Exception e1) {
					throw new Exception("VFRAP ROI Import - "+e1.getMessage());
				}
			}
		};
		getOverlayEditorPanelJAI().setCustomROIImport(importVFRAPROI);
	}

	public VFrap_OverlayEditorPanelJAI getOverlayEditorPanelJAI(){
		if (overlayEditorPanel==null){
			//if the panel is not editable, it is in the main frame which should have buttons listed in one column
			//otherwise, the image panel is in ROI wizard, the panel should have buttons listed in two columns.
			overlayEditorPanel = new VFrap_OverlayEditorPanelJAI(isEditable);
			overlayEditorPanel.setROITimePlotVisible(true);
			overlayEditorPanel.addROIName(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name(), false, FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name());
			overlayEditorPanel.addROIName(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name(), false, FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name());
			overlayEditorPanel.addROIName(FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name(), false, FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name());
			overlayEditorPanel.addPropertyChangeListener(this);
		}
		return overlayEditorPanel;
	}


	protected void plotROI()
	{
		FRAPStudy fStudy = getFrapWorkspace().getWorkingFrapStudy();
		if (fStudy == null || fStudy.getFrapData() == null){
			return;
		}
		saveROI();
		double[] averageFluor = FRAPDataAnalysis.getAverageROIIntensity(fStudy.getFrapData(), fStudy.getFrapData().getCurrentlyDisplayedROI(),null,null);
		showCurve(new String[] { "f" }, fStudy.getFrapData().getImageDataset().getImageTimeStamps(),new double[][] { averageFluor });
	}
	
	public void saveROI(){
		getOverlayEditorPanelJAI().saveUserChangesToROI();
	}

	public void adjustComponents(int choice)
	{
		getOverlayEditorPanelJAI().adjustComponentsForVFRAP(choice);
	}
		
	public void main(String args[]){
		try {
			
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	public LocalWorkspace getLocalWorkspace() {
		return localWorkspace;
	}

	public void setLocalWorkspace(LocalWorkspace localWorkspace) {
		this.localWorkspace = localWorkspace;
	}
	
	public void setFRAPWorkspace(FRAPSingleWorkspace arg_frapWorkspace) {
		FRAPSingleWorkspace oldWorkspace = frapWorkspace;
		if(oldWorkspace != null)
		{
			oldWorkspace.removePropertyChangeListener(this);
		}
		frapWorkspace = arg_frapWorkspace;
		frapWorkspace.addPropertyChangeListener(this);
	}
	
	public FRAPSingleWorkspace getFrapWorkspace() {
		return frapWorkspace;
	}
	
	private void showCurve(String[] varNames, double[] independent, double[][] dependents){
		PlotPane plotter = new PlotPane();
		PlotData[] plotDatas = new PlotData[dependents.length];
		for (int i = 0; i < plotDatas.length; i++) {
			plotDatas[i] = new PlotData(independent, dependents[i]);
		}
		Plot2D plot2D = new Plot2D(null, null,varNames, plotDatas);
		
		plotter.setPlot2D(plot2D);

		ChildWindow plotChildWindow = ChildWindowManager.findChildWindowManager(this).addChildWindow(plotter, plotter, "ROI time course", true);
		plotChildWindow.setTitle("ROI time course");
		plotChildWindow.setIsCenteredOnParent();
		plotChildWindow.setSize(new Dimension(400,400));
		plotChildWindow.showModal();
	}
	
	
} 
