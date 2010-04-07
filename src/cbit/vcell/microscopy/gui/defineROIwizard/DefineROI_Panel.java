package cbit.vcell.microscopy.gui.defineROIwizard;

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
import cbit.vcell.microscopy.gui.FRAPDataPanel;
import cbit.vcell.microscopy.gui.FRAPStudyPanel;
import cbit.vcell.microscopy.gui.VFrap_OverlayEditorPanelJAI;
import cbit.vcell.microscopy.gui.VirtualFrapLoader;
import cbit.vcell.microscopy.gui.VirtualFrapMainFrame;
import cbit.vcell.simdata.DataSetControllerImpl;

public class DefineROI_Panel extends JPanel implements PropertyChangeListener
{
	DefineROITopTitlePanel topPanel = null;
	FRAPDataPanel centerPanel = null;
	FRAPSingleWorkspace frapWorkspace = null;
	
	public DefineROI_Panel() {
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
	
	public DefineROITopTitlePanel getTopPanel()
	{
		if(topPanel == null)
		{
			topPanel = new DefineROITopTitlePanel();
		}
		return topPanel;
	}
	
	public void refreshUI()
	{
		FRAPData fData = getFrapWorkspace().getWorkingFrapStudy().getFrapData();
		centerPanel.getOverlayEditorPanelJAI().setImages(
				(fData==null?null:fData.getImageDataset()),true,
				(fData==null || fData.getOriginalGlobalScaleInfo() == null?VFrap_OverlayEditorPanelJAI.DEFAULT_SCALE_FACTOR:fData.getOriginalGlobalScaleInfo().originalScaleFactor),
				(fData==null || fData.getOriginalGlobalScaleInfo() == null?VFrap_OverlayEditorPanelJAI.DEFAULT_OFFSET_FACTOR:fData.getOriginalGlobalScaleInfo().originalOffsetFactor));
		centerPanel.getOverlayEditorPanelJAI().setRoiSouceData(fData);
		centerPanel.getOverlayEditorPanelJAI().setROI(getFrapWorkspace().getWorkingFrapStudy().getFrapData().getCurrentlyDisplayedROI());
	}
	
	public void adjustComponents(int choice)
	{
		topPanel.adjustComponent(choice);
		centerPanel.adjustComponents(choice);
	}
	
	public void setCurrentROI(String roiName, boolean bSave)
	{
		if(getFrapWorkspace() != null && getFrapWorkspace().getWorkingFrapStudy() != null &&
		   getFrapWorkspace().getWorkingFrapStudy().getFrapData() != null)
		{
			FRAPData fData = getFrapWorkspace().getWorkingFrapStudy().getFrapData();
			fData.setCurrentlyDisplayedROI(fData.getRoi(roiName), bSave);
		}
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getSource() == getCenterPanel().getOverlayEditorPanelJAI()){
			 
		}
	}
	
	public FRAPSingleWorkspace getFrapWorkspace() {
		return frapWorkspace;
	}
    
	public void setFrapWorkspace(FRAPSingleWorkspace frapWorkspace) {
		this.frapWorkspace = frapWorkspace;
		frapWorkspace.getWorkingFrapStudy().getFrapData().removePropertyChangeListener(centerPanel);
		frapWorkspace.getWorkingFrapStudy().getFrapData().addPropertyChangeListener(centerPanel);
		centerPanel.setFRAPWorkspace(frapWorkspace);
	}
}
