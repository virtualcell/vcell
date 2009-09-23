package cbit.vcell.microscopy.gui.choosemodelwizard;

import java.util.ArrayList;
import java.util.Hashtable;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.FRAPWorkspace;
import cbit.vcell.microscopy.gui.VirtualFrapMainFrame;
import org.vcell.wizard.WizardPanelDescriptor;

public class ChooseModel_ModelTypesDescriptor extends WizardPanelDescriptor {
    
    public static final String IDENTIFIER = "ChooseModel_ModelTypes";
    private FRAPWorkspace frapWorkspace = null;
    private ChooseModel_ModelTypesPanel modelTypesPanel = new ChooseModel_ModelTypesPanel();
    
    public ChooseModel_ModelTypesDescriptor () {
    	super();
    	setPanelDescriptorIdentifier(IDENTIFIER);
        setPanelComponent(modelTypesPanel);
    }
    
    public String getNextPanelDescriptorID() {
        return ChooseModel_RoiForErrorDescriptor.IDENTIFIER;
    }
    
    public String getBackPanelDescriptorID() {
        return null;
    }  
    
    public void setFrapWorkspace(FRAPWorkspace arg_FrapWorkspace)
    {
    	frapWorkspace = arg_FrapWorkspace;
    }
    
    public ArrayList<AsynchClientTask> preNextProcess()
    {   
    	ArrayList<AsynchClientTask> tasks = new ArrayList<AsynchClientTask>();
    	
    	AsynchClientTask aTask1 = new AsynchClientTask("Saving selected model types...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				FRAPStudy fStudy = frapWorkspace.getFrapStudy();
				if(fStudy != null)
		    	{
					if( modelTypesPanel.getModelTypes()!=null)
					{
			    		//update selected models in FrapStudy
			    		String[] models = new String[modelTypesPanel.getModelTypes().size()];
			    		models = modelTypesPanel.getModelTypes().toArray(models);
			    		if(models.length >0)
			    		{
			    			fStudy.addAllSelectedModel(models);
			    		}
			    		else
			    		{
			    			fStudy.clearSelectedModel();
			    		}
					}
					else
					{
						throw new Exception("At least one model type has to be selected.");
					}
		    	}
				else
				{
					throw new Exception("FRAPStudy is null. Please load data.");
				}
			}
		};
		AsynchClientTask aTask2 = new AsynchClientTask("Refreshing dependent ROIs...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				FRAPStudy fStudy = frapWorkspace.getFrapStudy();
				if(fStudy != null)
		    	{
	    			fStudy.refreshDependentROIs();
		    	}
				else
				{
					throw new Exception("FRAPStudy is null. Please load data.");
				}
			}
		};
        tasks.add(aTask1);
        tasks.add(aTask2);

        return tasks;
    }
    
}//end of class

