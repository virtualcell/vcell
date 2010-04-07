package cbit.vcell.microscopy.batchrun.gui.addFRAPdocWizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Hashtable;

import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.microscopy.DataVerifyInfo;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.microscopy.batchrun.FRAPBatchRunWorkspace;
import cbit.vcell.microscopy.gui.FRAPStudyPanel;
import cbit.vcell.microscopy.gui.VirtualFrapMainFrame;
import cbit.vcell.microscopy.gui.loaddatawizard.LoadFRAPData_SummaryPanel;

import org.vcell.wizard.Wizard;
import org.vcell.wizard.WizardPanelDescriptor;

public class FileSummaryDescriptor extends WizardPanelDescriptor{
    
    public static final String IDENTIFIER = "BATCHRUN_FileSummary";
    private FRAPBatchRunWorkspace batchRunWorkspace = null;
    
    public FileSummaryDescriptor() {
        super(IDENTIFIER, new LoadFRAPData_SummaryPanel());
        setProgressPopupShown(false); 
        setTaskProgressKnown(false);
    }
    //this method is override to make sure the next step is going to finish.
    public String getNextPanelDescriptorID() {
        return CropDescriptor.IDENTIFIER;
    }
    
	public void aboutToDisplayPanel() {
		((LoadFRAPData_SummaryPanel)getPanelComponent()).setLoadInfo(getBatchRunWorkspace().getWorkingFrapStudy());
	}
	
	//save the data before the panel disappears
    public ArrayList<AsynchClientTask> preNextProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
		
		AsynchClientTask verifyLoadedDataTask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				String msg = ((LoadFRAPData_SummaryPanel)getPanelComponent()).checkInputValidity();
				if(msg.equals(""))
				{
					DataVerifyInfo dataVerifyInfo= ((LoadFRAPData_SummaryPanel)getPanelComponent()).saveDataInfo();
					if(dataVerifyInfo != null)
					{
						getBatchRunWorkspace().getWorkingSingleWorkspace().updateImages(dataVerifyInfo);
					}
				}
				else throw new Exception(msg);
			}
		};
		
		taskArrayList.add(verifyLoadedDataTask);
		return taskArrayList;
    }
    
    public FRAPBatchRunWorkspace getBatchRunWorkspace() {
		return batchRunWorkspace;
	}
    
	public void setBatchRunWorkspace(FRAPBatchRunWorkspace batchRunWorkspace) {
		this.batchRunWorkspace = batchRunWorkspace;
	}
}