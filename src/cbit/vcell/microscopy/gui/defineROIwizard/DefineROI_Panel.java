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
import cbit.vcell.geometry.gui.OverlayEditorPanelJAI;
import cbit.vcell.geometry.gui.ROISourceData;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.FRAPWorkspace;
import cbit.vcell.microscopy.MicroscopyXmlReader;
import cbit.vcell.microscopy.gui.FRAPDataPanel;
import cbit.vcell.microscopy.gui.FRAPStudyPanel;
import cbit.vcell.geometry.gui.ROIAssistPanel;
import cbit.vcell.microscopy.gui.VirtualFrapLoader;
import cbit.vcell.microscopy.gui.VirtualFrapMainFrame;
import cbit.vcell.simdata.DataSetControllerImpl;

public class DefineROI_Panel extends JPanel implements PropertyChangeListener
{
	DefineROITopTitlePanel topPanel = null;
	FRAPDataPanel centerPanel = null;
	FRAPWorkspace frapWorkspace = null;
	
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
		FRAPData fData = getFrapWorkspace().getFrapStudy().getFrapData();
		centerPanel.getOverlayEditorPanelJAI().setImages(
				(fData==null?null:fData.getImageDataset()),true,
				(fData==null || fData.getOriginalGlobalScaleInfo() == null?OverlayEditorPanelJAI.DEFAULT_SCALE_FACTOR:fData.getOriginalGlobalScaleInfo().originalScaleFactor),
				(fData==null || fData.getOriginalGlobalScaleInfo() == null?OverlayEditorPanelJAI.DEFAULT_OFFSET_FACTOR:fData.getOriginalGlobalScaleInfo().originalOffsetFactor));
		centerPanel.getOverlayEditorPanelJAI().setRoiSouceData(fData);
		centerPanel.getOverlayEditorPanelJAI().setROI(getFrapWorkspace().getFrapStudy().getFrapData().getCurrentlyDisplayedROI());
	}
	
	public void adjustComponents(int choice)
	{
		topPanel.adjustComponent(choice);
		centerPanel.adjustComponents(choice);
	}
	
	public void setCurrentROI(String roiName)
	{
		if(getFrapWorkspace() != null && getFrapWorkspace().getFrapStudy() != null &&
		   getFrapWorkspace().getFrapStudy().getFrapData() != null)
		{
			FRAPData fData = getFrapWorkspace().getFrapStudy().getFrapData();
			fData.setCurrentlyDisplayedROI(fData.getRoi(roiName));
		}
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getSource() == getCenterPanel().getOverlayEditorPanelJAI()){
			 
		}
	}
	
	public FRAPWorkspace getFrapWorkspace() {
		return frapWorkspace;
	}
    
	public void setFrapWorkspace(FRAPWorkspace frapWorkspace) {
		this.frapWorkspace = frapWorkspace;
		frapWorkspace.getFrapStudy().getFrapData().removePropertyChangeListener(centerPanel);
		frapWorkspace.getFrapStudy().getFrapData().addPropertyChangeListener(centerPanel);
		centerPanel.setFRAPWorkspace(frapWorkspace);
	}
}
