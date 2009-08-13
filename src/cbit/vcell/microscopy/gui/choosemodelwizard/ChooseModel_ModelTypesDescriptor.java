package cbit.vcell.microscopy.gui.choosemodelwizard;

import java.util.ArrayList;
import java.util.Hashtable;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.gui.VirtualFrapMainFrame;
import org.vcell.wizard.WizardPanelDescriptor;

public class ChooseModel_ModelTypesDescriptor extends WizardPanelDescriptor {
    
    public static final String IDENTIFIER = "ChooseModel_ModelTypes";
    private FRAPStudy localFrapStudy = null;
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
    
    public void setFrapStudy(FRAPStudy arg_FrapStudy)
    {
    	localFrapStudy = arg_FrapStudy;
    }
    
    public ArrayList<AsynchClientTask> postNextProcess()
    {   
    	ArrayList<AsynchClientTask> tasks = new ArrayList<AsynchClientTask>();
    	//don't need progress popup
    	setProgressPopupShown(false);
    	
    	AsynchClientTask aTask1 = new AsynchClientTask("", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				try
				{
					if(localFrapStudy != null && modelTypesPanel.getModelTypes()!=null)
			    	{
			    		//update selected models in FrapStudy
			    		String[] models = new String[modelTypesPanel.getModelTypes().size()];
			    		models = modelTypesPanel.getModelTypes().toArray(models);
			    		if(models.length >0)
			    		{
			    			localFrapStudy.addAllSelectedModel(models);
			    		}
			    		else
			    		{
			    			localFrapStudy.clearSelectedModel();
			    		}
			    	}
				}catch (Exception e){
					e.printStackTrace(System.out);
				}
			}
		};
        tasks.add(aTask1);

        return tasks;
    }
    
}//end of class

