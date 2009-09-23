package cbit.vcell.microscopy.gui.choosemodelwizard;

import java.util.ArrayList;
import java.util.Hashtable;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.FRAPWorkspace;
import cbit.vcell.microscopy.gui.loaddatawizard.LoadFRAPData_SummaryPanel;

import org.vcell.wizard.Wizard;
import org.vcell.wizard.WizardPanelDescriptor;

public class ChooseModel_RoiForErrorDescriptor extends WizardPanelDescriptor{
    
    public static final String IDENTIFIER = "ChooseModel_RoiForError";
    public FRAPWorkspace frapWorkspace = null;
    
    public ChooseModel_RoiForErrorDescriptor () {
        super(IDENTIFIER, new ChooseModel_RoiForErrorPanel());
    }
    
    public String getNextPanelDescriptorID() {
        return Wizard.FINISH.getPanelDescriptorIdentifier();
    }
    
    public String getBackPanelDescriptorID() {
        return ChooseModel_ModelTypesDescriptor.IDENTIFIER;
    }
    
    public void aboutToDisplayPanel() 
    {
    	((ChooseModel_RoiForErrorPanel)getPanelComponent()).refreshCheckboxes();
    	((ChooseModel_RoiForErrorPanel)getPanelComponent()).refreshROIImage();
	}
    
    public void setFrapWorkspace(FRAPWorkspace arg_FrapWorkspace)
    {
    	frapWorkspace = arg_FrapWorkspace;
    	((ChooseModel_RoiForErrorPanel)getPanelComponent()).setFrapWorkspace(arg_FrapWorkspace);
    }
    
    public ArrayList<AsynchClientTask> preNextProcess()
    {   
    	ArrayList<AsynchClientTask> tasks = new ArrayList<AsynchClientTask>();
    	
    	AsynchClientTask aTask1 = new AsynchClientTask("Saving selected model types...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				frapWorkspace.getFrapStudy().setSelectedROIsForErrorCalculation(((ChooseModel_RoiForErrorPanel)getPanelComponent()).getSelectedROIs());
			}
		};
		tasks.add(aTask1);

        return tasks;
    }
}
