package cbit.vcell.microscopy.gui.defineROIwizard;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JPanel;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.geometry.gui.OverlayEditorPanelJAI;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.gui.FRAPDataPanel;
import org.vcell.wizard.WizardPanelDescriptor;

public class DefineROI_BleachedROIDescriptor extends WizardPanelDescriptor {
    
    public static final String IDENTIFIER = "DefineROI_BleachedROI";
    
    public DefineROI_BleachedROIDescriptor (JPanel imagePanel) {
        super(IDENTIFIER, imagePanel);
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
    	((DefineROI_Panel)getPanelComponent()).adjustComponents(OverlayEditorPanelJAI.DEFINE_BLEACHEDROI);
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
				((DefineROI_Panel)getPanelComponent()).setCurrentROI(nextROIStr);
			}
		};
		taskArrayList.add(setCurrentROITask);
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
				((DefineROI_Panel)getPanelComponent()).setCurrentROI(backROIStr);
			}
		};
		taskArrayList.add(setCurrentROITask);															
		return taskArrayList;
    }
}
