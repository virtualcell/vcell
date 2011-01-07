package cbit.vcell.microscopy.gui.choosemodelwizard;

import java.util.ArrayList;
import java.util.Hashtable;

import org.vcell.wizard.Wizard;
import org.vcell.wizard.WizardPanelDescriptor;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.microscopy.FRAPStudy;

public class ChooseModel_ModelTypesDescriptor extends WizardPanelDescriptor {
    
    public static final String IDENTIFIER = "ChooseModel_ModelTypes";
    private FRAPSingleWorkspace frapWorkspace = null;
    private ChooseModel_ModelTypesPanel modelTypesPanel = new ChooseModel_ModelTypesPanel();
    
    public ChooseModel_ModelTypesDescriptor () {
    	super();
    	setPanelDescriptorIdentifier(IDENTIFIER);
        setPanelComponent(modelTypesPanel);
    }
    
    public String getNextPanelDescriptorID() {
        return Wizard.FINISH.getPanelDescriptorIdentifier();
    }
    
    public String getBackPanelDescriptorID() {
        return null;
    }  
    
    public void setFrapWorkspace(FRAPSingleWorkspace arg_FrapWorkspace)
    {
    	frapWorkspace = arg_FrapWorkspace;
    }
    
    public void aboutToDisplayPanel() 
    {
    	FRAPStudy fStudy = frapWorkspace.getWorkingFrapStudy();
    	//if there are models selected and saved, load the model types. otherwise, apply default(diffusion with one component is selected).
    	if(fStudy.getModels() != null && fStudy.getModels().length > 0 && fStudy.getSelectedModels().size() > 0)
    	{
    		modelTypesPanel.clearAllSelected();
    		FRAPModel[] models = fStudy.getModels();
			if(models[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT] != null)
			{
				modelTypesPanel.setDiffOneSelected(true);
			}
			if(models[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS] != null)
			{
				modelTypesPanel.setDiffTwoSelected(true);
			}
			if(models[FRAPModel.IDX_MODEL_REACTION_OFF_RATE] != null)
			{
				modelTypesPanel.setReactionOffRateSelected(true);
			}
    	}
    	else //new frap document
    	{
    		modelTypesPanel.clearAllSelected();
    		modelTypesPanel.setDiffOneSelected(true);
    	}
    }
    
    public ArrayList<AsynchClientTask> preNextProcess()
    {   
    	ArrayList<AsynchClientTask> tasks = new ArrayList<AsynchClientTask>();
    	
    	AsynchClientTask aTask1 = new AsynchClientTask("Saving selected model types...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				FRAPStudy fStudy = frapWorkspace.getWorkingFrapStudy();
				if(fStudy == null)
		    	{
					throw new Exception("FRAPStudy is null. Please load data.");
		    	}
				boolean[] models = modelTypesPanel.getModelTypes();
				//perform checkings...is one selected, is koffRatemodel selected(in this case force to use bleached ROI only)					
				if(modelTypesPanel.getNumUserSelectedModelTypes() < 1)
				{
					throw new Exception("At least one model type has to be selected.");
				}
				/*if(models[FRAPModel.IDX_MODEL_REACTION_OFF_RATE])
				{
					if((fStudy.getNumSelectedROIs() > 1) ||
					   (fStudy.getNumSelectedROIs() == 1) && !fStudy.isROISelectedForErrorCalculation(FRAPData.VFRAP_ROI_ENUM.ROI_BLEACHED.name()))
					{
						String choice = DialogUtils.showWarningDialog(modelTypesPanel, "Since \'Reaction Dominant Off Rate\' model is selected,\nthe selected ROI for error calculation will be set to bleached\nregion only. There are two solutions, \n\nClick OK to select only bleached ROI to proceed, or\nClick Cancel and uncheck \'Finding Reaction Off Rate\' model to proceed.",
								                                   new String[]{UserMessage.OPTION_OK, UserMessage.OPTION_CANCEL}, UserMessage.OPTION_OK);
						if(choice.equals(UserMessage.OPTION_OK))
						{
							fStudy.setSelectedROIsForErrorCalculation(FRAPStudy.createSelectedROIsForReactionOffRateModel());
						}
						else
						{
							throw UserCancelException.CANCEL_GENERIC;
						}
					}
				}*/
				//update selected models in FrapStudy
			    fStudy.refreshModels(models);
			}
		};
		
        tasks.add(aTask1);

        return tasks;
    }
    
}//end of class

