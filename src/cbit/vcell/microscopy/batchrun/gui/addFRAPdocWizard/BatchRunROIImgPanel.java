package cbit.vcell.microscopy.batchrun.gui.addFRAPdocWizard;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Hashtable;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;

import org.vcell.util.BeanUtils;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.ZEnforcer;

import cbit.util.xml.XmlUtil;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.ROI;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.microscopy.MicroscopyXmlReader;
import cbit.vcell.microscopy.FRAPData.OriginalGlobalScaleInfo;
import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;
import cbit.vcell.microscopy.gui.FRAPDataPanel;
import cbit.vcell.microscopy.gui.FRAPStudyPanel;
import cbit.vcell.microscopy.gui.VFrap_OverlayEditorPanelJAI;
import cbit.vcell.microscopy.gui.VirtualFrapLoader;
import cbit.vcell.microscopy.gui.VirtualFrapMainFrame;
import cbit.vcell.simdata.DataSetControllerImpl;

public class BatchRunROIImgPanel extends JPanel implements PropertyChangeListener
{
	ROITopTitlePanel topPanel = null;
	FRAPDataPanel centerPanel = null;
	FRAPBatchRunWorkspace batchRunWorkspace = null;
	
	public BatchRunROIImgPanel() {
		super();
		initialize();
	}

	public void initialize()
	{
		setLayout(new BorderLayout());
		JPanel tPanel = new JPanel(new BorderLayout());
		tPanel.add(getTopPanel(), BorderLayout.NORTH);
		tPanel.add(new JSeparator(), BorderLayout.CENTER);
		add(tPanel, BorderLayout.NORTH);
		add(getCenterPanel(), BorderLayout.CENTER);
	}
	
	public FRAPDataPanel getCenterPanel()
	{
		if(centerPanel == null)
		{
			centerPanel = new FRAPDataPanel();
			centerPanel.addPropertyChangeListener(this);
		}
		return centerPanel;
	}
	
	public ROITopTitlePanel getTopPanel()
	{
		if(topPanel == null)
		{
			topPanel = new ROITopTitlePanel();
		}
		return topPanel;
	}
	
	public void refreshUI()
	{
		FRAPStudy workingFrapStudy = getBatchRunWorkspace().getWorkingFrapStudy();
		if(workingFrapStudy != null && workingFrapStudy.getFrapData() != null)
		{
			FRAPData fData = workingFrapStudy.getFrapData();
			OriginalGlobalScaleInfo originalGlobalScaleInfo = fData.getOriginalGlobalScaleInfo();
			centerPanel.getOverlayEditorPanelJAI().setImages(
					(fData==null?null:fData.getImageDataset()),true,
					(fData==null || originalGlobalScaleInfo == null?VFrap_OverlayEditorPanelJAI.DEFAULT_SCALE_FACTOR:originalGlobalScaleInfo.originalScaleFactor),
					(fData==null || originalGlobalScaleInfo == null?VFrap_OverlayEditorPanelJAI.DEFAULT_OFFSET_FACTOR:originalGlobalScaleInfo.originalOffsetFactor));
			centerPanel.getOverlayEditorPanelJAI().setRoiSouceData(fData);
			centerPanel.getOverlayEditorPanelJAI().setROI(workingFrapStudy.getFrapData().getCurrentlyDisplayedROI());
		}
	}
	
	public void adjustComponents(int choice)
	{
		topPanel.adjustComponent(choice);
		centerPanel.adjustComponents(choice);
	}
	
	public void setCurrentROI(String roiName, boolean bSave)
	{
		if(getBatchRunWorkspace() != null && getBatchRunWorkspace().getWorkingFrapStudy() != null &&
		   getBatchRunWorkspace().getWorkingFrapStudy().getFrapData() != null)
		{
			FRAPData fData = getBatchRunWorkspace().getWorkingFrapStudy().getFrapData();
			fData.setCurrentlyDisplayedROI(fData.getRoi(roiName), bSave);
		}
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getSource() == getCenterPanel().getOverlayEditorPanelJAI()){
			 
		}
	}
	
	public FRAPBatchRunWorkspace getBatchRunWorkspace() {
		return batchRunWorkspace;
	}
    
	public void setBatchRunWorkspace(FRAPBatchRunWorkspace batchRunWorkspace) {
		this.batchRunWorkspace = batchRunWorkspace;
		if(batchRunWorkspace.getWorkingFrapStudy() != null && batchRunWorkspace.getWorkingFrapStudy().getFrapData() != null)
		{
			batchRunWorkspace.getWorkingFrapStudy().getFrapData().removePropertyChangeListener(centerPanel);
			batchRunWorkspace.getWorkingFrapStudy().getFrapData().addPropertyChangeListener(centerPanel);
		}
		centerPanel.setFRAPWorkspace(batchRunWorkspace.getWorkingSingleWorkspace());
	}
}
