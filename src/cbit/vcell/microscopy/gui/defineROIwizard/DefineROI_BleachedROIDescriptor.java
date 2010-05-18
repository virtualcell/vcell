package cbit.vcell.microscopy.gui.defineROIwizard;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.VFRAPPreference;
import cbit.vcell.microscopy.gui.FRAPDataPanel;
import cbit.vcell.microscopy.gui.VFrap_OverlayEditorPanelJAI;

import org.vcell.wizard.WizardPanelDescriptor;

public class DefineROI_BleachedROIDescriptor extends WizardPanelDescriptor {
    
    public static final String IDENTIFIER = "DefineROI_BleachROI";
    private JPanel imgPanel = null;
    public DefineROI_BleachedROIDescriptor (JPanel imagePanel) {
    	super();
        imgPanel = imagePanel;
        JPanel bleachPanel = new JPanel(new BorderLayout());
//        cropPanel.add(imagePanel);
        setPanelDescriptorIdentifier(IDENTIFIER);
        setPanelComponent(bleachPanel);
        setProgressPopupShown(false); 
        setTaskProgressKnown(false);
    }
    
    public String getNextPanelDescriptorID() {
        return DefineROI_BackgroundROIDescriptor.IDENTIFIER;
    }
    
    public String getBackPanelDescriptorID() {
        return DefineROI_CellROIDescriptor.IDENTIFIER;
    }
    
    public void aboutToDisplayPanel()
    {
    	((JPanel)getPanelComponent()).removeAll();
    	((JPanel)getPanelComponent()).add(imgPanel);
    	((DefineROI_Panel)imgPanel).adjustComponents(VFrap_OverlayEditorPanelJAI.DEFINE_BLEACHEDROI);
    }
    
    public ArrayList<AsynchClientTask> preNextProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
		
		final String nextROIStr = FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name();
		AsynchClientTask setCurrentROITask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				//save current ROI and load ROI in the panel it goes next to
				((DefineROI_Panel)imgPanel).setCurrentROI(nextROIStr, true);
			}
		};
		taskArrayList.add(setCurrentROITask);
		return taskArrayList;
    } 
    
    public ArrayList<AsynchClientTask> postNextProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
		
		AsynchClientTask ifNeedROIAssistTask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				if(VFRAPPreference.getValue(VFRAPPreference.ROI_ASSIST_REQUIREMENT_TYPE, VFRAPPreference.ROI_ASSIST_REQUIRE_ALWAYS).equals(VFRAPPreference.ROI_ASSIST_REQUIRE_ALWAYS) &&
					((DefineROI_Panel)imgPanel).getFrapWorkspace().getFrapStudy().getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_BACKGROUND.name()).getNonzeroPixelsCount()<1)
				{
					((FRAPDataPanel)((DefineROI_Panel)imgPanel).getCenterPanel()).getOverlayEditorPanelJAI().showAssistDialog();
				}
			}
		};
		taskArrayList.add(ifNeedROIAssistTask);
		return taskArrayList;
    }
    
    public ArrayList<AsynchClientTask> preBackProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
    	
		final String backROIStr = FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name();
		AsynchClientTask setCurrentROITask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				//save current ROI and load ROI in the panel it backs to 
				((DefineROI_Panel)imgPanel).setCurrentROI(backROIStr, true);
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
				   ((DefineROI_Panel)imgPanel).getFrapWorkspace().getFrapStudy().getFrapData().getRoi(FRAPData.VFRAP_ROI_ENUM.ROI_CELL.name()).getNonzeroPixelsCount()<1)
				{
					((FRAPDataPanel)((DefineROI_Panel)imgPanel).getCenterPanel()).getOverlayEditorPanelJAI().showAssistDialog();
				}
			}
		};
		taskArrayList.add(ifNeedROIAssistTask);
		return taskArrayList;
    }
}
