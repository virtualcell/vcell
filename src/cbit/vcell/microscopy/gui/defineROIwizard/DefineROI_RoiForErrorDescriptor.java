package cbit.vcell.microscopy.gui.defineROIwizard;

import java.util.ArrayList;
import java.util.Hashtable;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.microscopy.FRAPSingleWorkspace;

import org.vcell.wizard.Wizard;
import org.vcell.wizard.WizardPanelDescriptor;

public class DefineROI_RoiForErrorDescriptor extends WizardPanelDescriptor{
    
    public static final String IDENTIFIER = "DefineROI_RoiForError";
    public FRAPSingleWorkspace frapWorkspace = null;
    
    public DefineROI_RoiForErrorDescriptor () {
        super(IDENTIFIER, new DefineROI_RoiForErrorPanel());
    }
    
    public String getNextPanelDescriptorID() {
        return Wizard.FINISH.getPanelDescriptorIdentifier();
    }
    
    public String getBackPanelDescriptorID() {
        return DefineROI_SummaryDescriptor.IDENTIFIER;
    }
    
    public void aboutToDisplayPanel() 
    {
    	((DefineROI_RoiForErrorPanel)getPanelComponent()).refreshCheckboxes();
    	((DefineROI_RoiForErrorPanel)getPanelComponent()).refreshROIImage();
	}
    
    public void setFrapWorkspace(FRAPSingleWorkspace arg_FrapWorkspace)
    {
    	frapWorkspace = arg_FrapWorkspace;
    	((DefineROI_RoiForErrorPanel)getPanelComponent()).setFrapWorkspace(arg_FrapWorkspace);
    }
    
    public ArrayList<AsynchClientTask> preNextProcess()
    {   
    	ArrayList<AsynchClientTask> tasks = new ArrayList<AsynchClientTask>();
    	
    	AsynchClientTask aTask1 = new AsynchClientTask("Saving selected ROIs...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				boolean[] selectedROIs = ((DefineROI_RoiForErrorPanel)getPanelComponent()).getSelectedROIs();
				boolean isOneSelected = false;
				for(int i=0; i<selectedROIs.length; i++)
				{
					if(selectedROIs[i])
					{
						isOneSelected = true;
						break;
					}
				}
				if(isOneSelected)
				{
					frapWorkspace.getWorkingFrapStudy().setSelectedROIsForErrorCalculation(selectedROIs);
				}
				else
				{
					throw new Exception("At least one ROI has to be selected.");
				}
			}
		};
		tasks.add(aTask1);

        return tasks;
    }
}
