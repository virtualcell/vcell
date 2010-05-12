package cbit.vcell.microscopy.batchrun.gui.addFRAPdocWizard;

import java.util.ArrayList;
import java.util.Hashtable;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;
import cbit.vcell.microscopy.batchrun.gui.chooseModelWizard.ModelTypesDescriptor;
import cbit.vcell.microscopy.gui.loaddatawizard.LoadFRAPData_SummaryPanel;

import org.vcell.wizard.Wizard;
import org.vcell.wizard.WizardPanelDescriptor;

public class RoiForErrorDescriptor extends WizardPanelDescriptor{
    
    public static final String IDENTIFIER = "BatchRun_RoiForError";
    public FRAPBatchRunWorkspace bathRunWorkspace = null;
    
    public RoiForErrorDescriptor () {
        super(IDENTIFIER, new RoiForErrorPanel());
    }
    
    public String getNextPanelDescriptorID() {
        return FileSaveDescriptor.IDENTIFIER;
    }
    
    public String getBackPanelDescriptorID() {
        return ROISummaryDescriptor.IDENTIFIER;
    }
    
    public void aboutToDisplayPanel() 
    {
    	((RoiForErrorPanel)getPanelComponent()).refreshCheckboxes();
    	((RoiForErrorPanel)getPanelComponent()).refreshROIImage();
	}
    
    public void setBatchRunWorkspace(FRAPBatchRunWorkspace arg_BarchRunWorkspace)
    {
    	bathRunWorkspace = arg_BarchRunWorkspace;
    	((RoiForErrorPanel)getPanelComponent()).setFrapBatchRunWorkspace(arg_BarchRunWorkspace);
    }
    
    public ArrayList<AsynchClientTask> preNextProcess()
    {   
    	ArrayList<AsynchClientTask> tasks = new ArrayList<AsynchClientTask>();
    	
    	AsynchClientTask aTask1 = new AsynchClientTask("Saving selected ROIs...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				boolean[] selectedROIs = ((RoiForErrorPanel)getPanelComponent()).getSelectedROIs();
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
					bathRunWorkspace.getWorkingFrapStudy().setSelectedROIsForErrorCalculation(selectedROIs);
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
