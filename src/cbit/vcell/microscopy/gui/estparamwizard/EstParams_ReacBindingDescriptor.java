package cbit.vcell.microscopy.gui.estparamwizard;

import java.util.ArrayList;
import java.util.Hashtable;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.FRAPWorkspace;
import cbit.vcell.microscopy.LocalWorkspace;
import cbit.vcell.opt.Parameter;

import org.vcell.util.gui.DialogUtils;
import org.vcell.wizard.WizardPanelDescriptor;

public class EstParams_ReacBindingDescriptor extends WizardPanelDescriptor
{
	public static final String IDENTIFIER = "EstimateParameters_ReactionPlusBinding";
	private FRAPWorkspace frapWorkspace = null;
	private LocalWorkspace localWorkspace = null;
    
    public EstParams_ReacBindingDescriptor () {
        super(IDENTIFIER, new EstParams_ReacBindingPanel());
    }

    public void aboutToDisplayPanel() 
    {
    	FRAPStudy fStudy = getFrapWorkspace().getFrapStudy();
		FRAPModel[] frapModels = fStudy.getModels();
		Parameter[] params = null;
		//get parameters to display in reaction binding panel
		if(frapModels[FRAPModel.IDX_MODEL_DIFF_BINDING] != null && 
		   frapModels[FRAPModel.IDX_MODEL_DIFF_BINDING].getModelParameters()!= null)
		{
			params = frapModels[FRAPModel.IDX_MODEL_DIFF_BINDING].getModelParameters();
		}
		else if(frapModels[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS] != null && 
				frapModels[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS].getModelParameters()!= null)
		{
			params = FRAPModel.createReacBindingParametersFromDiffusionParameters(frapModels[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS].getModelParameters());
		}
		else if(frapModels[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT] != null && 
				frapModels[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].getModelParameters()!=null)
		{
			params = FRAPModel.createReacBindingParametersFromDiffusionParameters(frapModels[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].getModelParameters());
		}
		else // no parameters are available
		{
			try{
		    	params = FRAPModel.getInitialParameters(fStudy.getFrapData(), FRAPModel.MODEL_TYPE_ARRAY[FRAPModel.IDX_MODEL_DIFF_BINDING]);
	    	}catch(Exception ex)
	    	{
	    		ex.printStackTrace(System.out);
	    		DialogUtils.showErrorDialog(((EstParams_OneDiffComponentPanel)getPanelComponent()), "Error getting parameters for diffusion plus binding model.");
	    	}
		}
    	
    	((EstParams_ReacBindingPanel)getPanelComponent()).setReacBindingParams(params);
	}    
    
    //save model parameters
    public ArrayList<AsynchClientTask> preNextProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
		AsynchClientTask saveParametersTask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				Parameter[] params = ((EstParams_ReacBindingPanel)getPanelComponent()).getCurrentParameters();
				FRAPModel  frapModel = getFrapWorkspace().getFrapStudy().getFrapModel(FRAPModel.IDX_MODEL_DIFF_BINDING);
				frapModel.setModelParameters(params);
				frapModel.setResult(((EstParams_ReacBindingPanel)getPanelComponent()).getCurrentSimResults());
			}
		};
		
		taskArrayList.add(saveParametersTask);
	
		return taskArrayList;
    }
    
    public FRAPWorkspace getFrapWorkspace() {
		return frapWorkspace;
	}
    
	public void setFrapWorkspace(FRAPWorkspace frapWorkspace) {
		this.frapWorkspace = frapWorkspace;
		((EstParams_ReacBindingPanel)getPanelComponent()).setFrapWorkspace(frapWorkspace);
	}
	
	public LocalWorkspace getLocalWorkspace() {
		return localWorkspace;
	}
    
	public void setLocalWorkspace(LocalWorkspace localWorkspace) {
		this.localWorkspace = localWorkspace;
		((EstParams_ReacBindingPanel)getPanelComponent()).setLocalWorkspace(localWorkspace);
	}
}
