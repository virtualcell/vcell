package cbit.vcell.microscopy.gui.loaddatawizard;

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
import cbit.vcell.microscopy.gui.FRAPStudyPanel;
import cbit.vcell.microscopy.gui.VirtualFrapMainFrame;

import org.vcell.wizard.Wizard;
import org.vcell.wizard.WizardPanelDescriptor;

public class LoadFRAPData_SummaryDescriptor extends WizardPanelDescriptor{
    
    public static final String IDENTIFIER = "LoadFRAPData_Summary";
    private FRAPSingleWorkspace frapWorkspace = null;
    
    public LoadFRAPData_SummaryDescriptor() {
        super(IDENTIFIER, new LoadFRAPData_SummaryPanel());
        setProgressPopupShown(false); 
        setTaskProgressKnown(false);
    }
    //this method is override to make sure the next step is going to finish.
    public String getNextPanelDescriptorID() {
        return Wizard.FINISH.getPanelDescriptorIdentifier();
    }
    
	public void aboutToDisplayPanel() {
		((LoadFRAPData_SummaryPanel)getPanelComponent()).setLoadInfo(getFrapWorkspace().getWorkingFrapStudy());
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
						getFrapWorkspace().updateImages(dataVerifyInfo);
					}
				}
				else throw new Exception(msg);
			}
		};
		
		taskArrayList.add(verifyLoadedDataTask);
		return taskArrayList;
    }
    
    public FRAPSingleWorkspace getFrapWorkspace() {
		return frapWorkspace;
	}
    
	public void setFrapWorkspace(FRAPSingleWorkspace frapWorkspace) {
		this.frapWorkspace = frapWorkspace;
	}
}