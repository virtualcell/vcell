package cbit.vcell.microscopy.gui.estparamwizard;

import java.util.ArrayList;
import java.util.Hashtable;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.FRAPWorkspace;
import cbit.vcell.opt.Parameter;

import org.vcell.util.gui.DialogUtils;
import org.vcell.wizard.WizardPanelDescriptor;

public class EstParams_TwoDiffComponentDescriptor extends WizardPanelDescriptor
{
	public static final String IDENTIFIER = "EstimateParameters_DiffusionWithTwoDiffusingComponents";
	private FRAPWorkspace frapWorkspace = null;
	
    public EstParams_TwoDiffComponentDescriptor () {
        super(IDENTIFIER, new EstParams_TwoDiffComponentPanel());
    }

    public void aboutToDisplayPanel() 
    {
    	FRAPStudy fStudy = frapWorkspace.getFrapStudy();
    	Parameter[] params = fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS].getModelParameters();
    	try{
	    	if(params == null)
	    	{
	    		params = FRAPModel.getInitialParameters(fStudy.getFrapData(), FRAPModel.MODEL_TYPE_ARRAY[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS]);
	    	}
			((EstParams_TwoDiffComponentPanel)getPanelComponent()).setData(fStudy.getFrapOptData(), fStudy.getFrapData(), params, fStudy.getFrapData().getImageDataset().getImageTimeStamps(), fStudy.getStartingIndexForRecovery(), fStudy.getSelectedROIsForErrorCalculation());
    	}catch(Exception ex)
    	{
    		ex.printStackTrace(System.out);
    		DialogUtils.showErrorDialog(((EstParams_OneDiffComponentPanel)getPanelComponent()), "Error getting parameters for diffusion with one diffusing component model.");
    	}
	}
    
    //save model parameters when go next
    public ArrayList<AsynchClientTask> preNextProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
		AsynchClientTask saveParametersTask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				saveModelParameters();
			}
		};
		
		taskArrayList.add(saveParametersTask);
	
		return taskArrayList;
    }
    
    //save model parameters also when go back
    public ArrayList<AsynchClientTask> preBackProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
		AsynchClientTask saveParametersTask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				saveModelParameters();
			}
		};
		
		taskArrayList.add(saveParametersTask);
	
		return taskArrayList;
    }
    
    private void saveModelParameters()
    {
    	Parameter[] params = ((EstParams_TwoDiffComponentPanel)getPanelComponent()).getCurrentParameters();
		FRAPModel  frapModel = getFrapWorkspace().getFrapStudy().getFrapModel(FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS);
		frapModel.setModelParameters(params);
		frapModel.setData(((EstParams_TwoDiffComponentPanel)getPanelComponent()).getCurrentEstimationResults());
    }
    
    public FRAPWorkspace getFrapWorkspace() {
		return frapWorkspace;
	}
    
	public void setFrapWorkspace(FRAPWorkspace frapWorkspace) {
		this.frapWorkspace = frapWorkspace;
		((EstParams_TwoDiffComponentPanel)getPanelComponent()).setFrapWorkspace(frapWorkspace);
	}
    
	public void clearSelectedPlotIndices()
	{
		((EstParams_TwoDiffComponentPanel)getPanelComponent()).clearSelectedPlotIndices();
	}
}
