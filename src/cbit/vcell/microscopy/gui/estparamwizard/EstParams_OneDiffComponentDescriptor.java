package cbit.vcell.microscopy.gui.estparamwizard;

import java.util.ArrayList;
import java.util.Hashtable;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.FRAPWorkspace;
import cbit.vcell.microscopy.gui.VirtualFrapMainFrame;
import cbit.vcell.microscopy.gui.loaddatawizard.LoadFRAPData_SummaryPanel;
import cbit.vcell.opt.Parameter;
import cbit.vcell.parser.ExpressionException;

import org.vcell.util.gui.DialogUtils;
import org.vcell.wizard.WizardPanelDescriptor;

public class EstParams_OneDiffComponentDescriptor extends WizardPanelDescriptor
{
	public static final String IDENTIFIER = "EstimateParameters_DiffusionWithOneDiffusingComponent";
	private FRAPWorkspace frapWorkspace = null;
	
    public EstParams_OneDiffComponentDescriptor () {
        super(IDENTIFIER, new EstParams_OneDiffComponentPanel());
    }

    public void aboutToDisplayPanel() 
    {
    	FRAPStudy fStudy = frapWorkspace.getFrapStudy();
    	Parameter[] params = fStudy.getModels()[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].getModelParameters();
    	try{
	    	if(params == null)
	    	{
	    		params = FRAPModel.getInitialParameters(fStudy.getFrapData(), FRAPModel.MODEL_TYPE_ARRAY[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT]);
	    	}
			((EstParams_OneDiffComponentPanel)getPanelComponent()).setData(fStudy.getFrapOptData(), fStudy.getFrapData(), params, fStudy.getFrapData().getImageDataset().getImageTimeStamps(), fStudy.getStartingIndexForRecovery(), fStudy.getSelectedROIsForErrorCalculation());
    	}catch(Exception ex)
    	{
    		ex.printStackTrace(System.out);
    		DialogUtils.showErrorDialog(((EstParams_OneDiffComponentPanel)getPanelComponent()), "Error getting parameters for diffusion with one diffusing component model.");
    	}
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
				Parameter[] params = ((EstParams_OneDiffComponentPanel)getPanelComponent()).getCurrentParameters();
				FRAPModel  frapModel = getFrapWorkspace().getFrapStudy().getFrapModel(FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT);
				frapModel.setModelParameters(params);
				frapModel.setResult(((EstParams_OneDiffComponentPanel)getPanelComponent()).getCurrentEstimationResults());
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
		((EstParams_OneDiffComponentPanel)getPanelComponent()).setFrapWorkspace(frapWorkspace);
	}
	
}
