package cbit.vcell.microscopy.gui.defineROIwizard;

import java.awt.BorderLayout;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.geometry.gui.OverlayEditorPanelJAI;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.VFRAPPreference;
import cbit.vcell.microscopy.gui.FRAPDataPanel;
import cbit.vcell.microscopy.gui.FRAPStudyPanel;

import org.vcell.wizard.Wizard;
import org.vcell.wizard.WizardPanelDescriptor;

public class DefineROI_BackgroundROIDescriptor extends WizardPanelDescriptor {
    
    public static final String IDENTIFIER = "DefineROI_backgroundROI";
    private JPanel imgPanel = null;
    public DefineROI_BackgroundROIDescriptor (JPanel imagePanel) {
    	super();
        imgPanel = imagePanel;
        JPanel bgPanel = new JPanel(new BorderLayout());
        setPanelDescriptorIdentifier(IDENTIFIER);
        setPanelComponent(bgPanel);
        setProgressPopupShown(false); 
        setTaskProgressKnown(false);
    }
    
    public String getNextPanelDescriptorID() {
        return DefineROI_SummaryDescriptor.IDENTIFIER;
    }
    
    public String getBackPanelDescriptorID() {
        return DefineROI_BleachedROIDescriptor.IDENTIFIER;
    }
    
    public void aboutToDisplayPanel()
    {
    	((JPanel)getPanelComponent()).removeAll();
    	((JPanel)getPanelComponent()).add(imgPanel);
    	((DefineROI_Panel)imgPanel).adjustComponents(OverlayEditorPanelJAI.DEFINE_BACKGROUNDROI);
    }
    
    public ArrayList<AsynchClientTask> preNextProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
		
		final String nextROIStr = null;
		AsynchClientTask setCurrentROITask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				//save current ROI and load ROI in the panel it goes next to
				((DefineROI_Panel)imgPanel).setCurrentROI(nextROIStr);
				FRAPData fData = ((DefineROI_Panel)imgPanel).getFrapWorkspace().getFrapStudy().getFrapData();
				fData.setCurrentlyDisplayedROI(fData.getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()));
			}
		};
		taskArrayList.add(setCurrentROITask);
		return taskArrayList;
    } 
 
    public ArrayList<AsynchClientTask> preBackProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
    	
		final String backROIStr = FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name();
		AsynchClientTask setCurrentROITask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				//save current ROI and load ROI in the panel it backs to 
				((DefineROI_Panel)imgPanel).setCurrentROI(backROIStr);
			}
		};
		taskArrayList.add(setCurrentROITask);															
		return taskArrayList;
    }
    
    public ArrayList<AsynchClientTask> postBackProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
		
		AsynchClientTask ifNeedROIAssistTask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				if(VFRAPPreference.getValue(VFRAPPreference.ROI_ASSIST_REQUIREMENT_TYPE, VFRAPPreference.ROI_ASSIST_REQUIRE_ALWAYS).equals(VFRAPPreference.ROI_ASSIST_REQUIRE_ALWAYS) &&
				   ((DefineROI_Panel)imgPanel).getFrapWorkspace().getFrapStudy().getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()).isAllPixelsZero())
				{
					((FRAPDataPanel)((DefineROI_Panel)imgPanel).getCenterPanel()).getOverlayEditorPanelJAI().showAssistDialog();
				}
			}
		};
		taskArrayList.add(ifNeedROIAssistTask);
		return taskArrayList;
    }
}

