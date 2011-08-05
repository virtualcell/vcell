/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.batchrun.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Hashtable;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import cbit.util.xml.XmlUtil;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.MicroscopyXmlReader;
import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;
import cbit.vcell.microscopy.gui.FRAPDataPanel;
import cbit.vcell.microscopy.gui.FRAPStudyPanel;
import cbit.vcell.microscopy.gui.VFrap_OverlayEditorPanelJAI;
import cbit.vcell.microscopy.gui.VirtualFrapLoader;
import cbit.vcell.microscopy.gui.VirtualFrapMainFrame;

@SuppressWarnings("serial")
public class BatchRunDisplayPanel extends JPanel implements PropertyChangeListener
{
	public final static String DISPLAY_PARAM_ID = "DISPLAY_PARAM";
	public final static String DISPLAY_IMG_ID = "DISPLAY_IMG";
	
	private JSplitPane rightSplit = null;
	private FRAPDataPanel frapDataPanel = null;
	private JPanel topDisplayPanel = null;
	private BatchRunResultsPanel resultsPanel = null;
	private JobStatusPanel jobStatusPanel = null;
	private VirtualFrapBatchRunFrame parentFrame = null;
	//batch run workspace
	private FRAPBatchRunWorkspace batchRunWorkspace = null;
	
	//constructor
	public BatchRunDisplayPanel(JFrame arg_parentFrame)
	{
		super();
	    rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getTopDisplayPanel(), getJobStatusPanel());
	    rightSplit.setDividerSize(2);
	    rightSplit.setDividerLocation(VirtualFrapBatchRunFrame.iniDividerLocation);
	    setLayout(new BorderLayout());
	    add(rightSplit, BorderLayout.CENTER);
	    setBorder(new EmptyBorder(0,0,0,0));
	    parentFrame = (VirtualFrapBatchRunFrame)arg_parentFrame;
	    //addMouseListener(th);
	    setVisible(true);
	}

	public JPanel getTopDisplayPanel()
	{
		if(topDisplayPanel == null)
		{
			topDisplayPanel = new JPanel(new CardLayout());
			topDisplayPanel.add(getFRAPDataPanel(), DISPLAY_IMG_ID);
			topDisplayPanel.add(getBatchRunResultsPanel(), DISPLAY_PARAM_ID);
		}
		return topDisplayPanel;
	}
	
	public FRAPDataPanel getFRAPDataPanel() {
		if (frapDataPanel == null) 
		{
			frapDataPanel = new FRAPDataPanel(false);//the frap data panel in the main frame is not editable
			//set display mode
			frapDataPanel.adjustComponents(VFrap_OverlayEditorPanelJAI.DISPLAY_WITH_ROIS);
			
			Hashtable<String, Cursor> cursorsForROIsHash = new Hashtable<String, Cursor>();
			cursorsForROIsHash.put(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name(), FRAPStudyPanel.ROI_CURSORS[FRAPStudyPanel.CURSOR_CELLROI]);
			cursorsForROIsHash.put(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name(), FRAPStudyPanel.ROI_CURSORS[FRAPStudyPanel.CURSOR_BLEACHROI]);
			cursorsForROIsHash.put(FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name(), FRAPStudyPanel.ROI_CURSORS[FRAPStudyPanel.CURSOR_BACKGROUNDROI]);
			frapDataPanel.getOverlayEditorPanelJAI().setCursorsForROIs(cursorsForROIsHash);
			
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
						ROI roi = getBatchRunWorkspace().getWorkingSingleWorkspace().getWorkingFrapStudy().getFrapData().getCurrentlyDisplayedROI();
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
								getBatchRunWorkspace().getWorkingSingleWorkspace().getWorkingFrapStudy().getFrapData().addReplaceRoi(importedROIs[i]);
							}
//							undoableEditSupport.postEdit(FRAPStudyPanel.CLEAR_UNDOABLE_EDIT);
						}
						return true;
					} catch (Exception e1) {
						throw new Exception("VFRAP ROI Import - "+e1.getMessage());
					}
				}
			};
			frapDataPanel.getOverlayEditorPanelJAI().setCustomROIImport(importVFRAPROI);
		}
		return frapDataPanel;
	}
	
	public BatchRunResultsPanel getBatchRunResultsPanel()
	{
		if(resultsPanel == null)
		{
			resultsPanel = new BatchRunResultsPanel();
		}
		return resultsPanel;
	}
	
	public JobStatusPanel getJobStatusPanel()
	{
		if(jobStatusPanel == null)
		{
			jobStatusPanel = new JobStatusPanel(this);
			jobStatusPanel.addPropertyChangeListener(this);
		}
		return jobStatusPanel;
	}
	
	public void showJobStatusPanel()
	{
		rightSplit.setBottomComponent(getJobStatusPanel());
		rightSplit.setDividerLocation(VirtualFrapBatchRunFrame.iniDividerLocation);
	}
	public void hideJobStatusPanel()
	{
//		rightSplit.setDividerLocation(DIVIDER_MAX_LOCATION);
		rightSplit.setBottomComponent(null);
	}
	public FRAPBatchRunWorkspace getBatchRunWorkspace() {
		return batchRunWorkspace;
	}

	public void setBatchRunWorkspace(FRAPBatchRunWorkspace batchRunWorkspace) {
		//remove old property change listener
		if(getBatchRunWorkspace() != null)
		{
			getBatchRunWorkspace().removePropertyChangeListener(this);
		}
		this.batchRunWorkspace = batchRunWorkspace;
		//add new property change listener
		getBatchRunWorkspace().addPropertyChangeListener(this);
		getFRAPDataPanel().setFRAPWorkspace(batchRunWorkspace.getWorkingSingleWorkspace());
		getBatchRunResultsPanel().setBatchRunWorkspace(batchRunWorkspace);
	}

	public void propertyChange(PropertyChangeEvent evt) 
	{
		if(evt.getPropertyName().equals(FRAPBatchRunWorkspace.PROPERTY_CHANGE_BATCHRUN_DISPLAY_IMG))
		{
			((CardLayout)topDisplayPanel.getLayout()).show(topDisplayPanel, DISPLAY_IMG_ID);
		}
		else if(evt.getPropertyName().equals(FRAPBatchRunWorkspace.PROPERTY_CHANGE_BATCHRUN_DISPLAY_PARAM))
		{
			resultsPanel.updateTableData();
			resultsPanel.setModelTypeLabel(FRAPModel.MODEL_TYPE_ARRAY[batchRunWorkspace.getSelectedModel()]);
			((CardLayout)topDisplayPanel.getLayout()).show(topDisplayPanel, DISPLAY_PARAM_ID);
		}
		else if(evt.getPropertyName().equals(JobStatusPanel.STATUSPANEL_PROPERTY_CHANGE))
		{
			(parentFrame).setViewJobMenuSelected(false);
		}
	}
	
	public static void main(java.lang.String[] args) {
		try {
			try{
		    	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		    }catch(Exception e){
		    	throw new RuntimeException(e.getMessage(),e);
		    }
			javax.swing.JFrame frame = new javax.swing.JFrame();
			BatchRunDisplayPanel aPanel = new BatchRunDisplayPanel(frame);
			frame.add(aPanel);
			frame.pack();
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of javax.swing.JPanel");
			exception.printStackTrace(System.out);
		}
	}
}
