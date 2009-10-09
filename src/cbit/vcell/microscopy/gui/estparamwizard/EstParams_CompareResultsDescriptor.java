package cbit.vcell.microscopy.gui.estparamwizard;

import java.util.ArrayList;
import java.util.Hashtable;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.microscopy.DataVerifyInfo;
import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.FRAPWorkspace;
import cbit.vcell.microscopy.gui.defineROIwizard.DefineROI_BleachedROIDescriptor;
import cbit.vcell.microscopy.gui.loaddatawizard.LoadFRAPData_SummaryPanel;
import cbit.vcell.opt.Parameter;

import org.vcell.util.gui.DialogUtils;
import org.vcell.wizard.Wizard;
import org.vcell.wizard.WizardPanelDescriptor;

public class EstParams_CompareResultsDescriptor extends WizardPanelDescriptor
{
	public static final String IDENTIFIER = "EstimateParameters_CompareResultsAmongSelectedModels";
	private FRAPWorkspace frapWorkspace = null;
	
    public EstParams_CompareResultsDescriptor () {
        super(IDENTIFIER, new EstParams_CompareResultsPanel());
    }
    
    public String getNextPanelDescriptorID() {
        return Wizard.FINISH.getPanelDescriptorIdentifier();
    }

    public void aboutToDisplayPanel() 
    {
    	FRAPStudy fStudy = frapWorkspace.getFrapStudy();
    	if(fStudy.getAnalysisMSESummaryData() == null)
    	{
    		fStudy.createAnalysisMSESummaryData();
    	}
    	double[][] mseSummaryData = fStudy.getAnalysisMSESummaryData();
    	
    	int bestModel = FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT;
    	double minError = 1000;
    	if(mseSummaryData != null)
    	{
    		int secDimLen = mseSummaryData[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].length;
    		for(int i=0; i<FRAPModel.NUM_MODEL_TYPES; i++)
    		{
    			if(minError > mseSummaryData[i][secDimLen - 1])
    			{
    				minError = mseSummaryData[i][secDimLen - 1];
    				bestModel = i;
    			}
    		}
    	}
    	((EstParams_CompareResultsPanel)this.getPanelComponent()).setBestModelRadioButton(bestModel);
	}
    
    public ArrayList<AsynchClientTask> preNextProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
		
		AsynchClientTask saveBestModelTask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				int bestModelIndex = getBestModelIndex();
				getFrapWorkspace().getFrapStudy().setBestModelIndex(bestModelIndex);
			}
		};
		
		taskArrayList.add(saveBestModelTask);
		return taskArrayList;
    }
    
    public int getBestModelIndex()
    {
    	return ((EstParams_CompareResultsPanel)this.getPanelComponent()).getRadioButtonPanel().getBestModelIndex();
    }
    
    public FRAPWorkspace getFrapWorkspace() {
		return frapWorkspace;
	}
    
	public void setFrapWorkspace(FRAPWorkspace frapWorkspace) {
		this.frapWorkspace = frapWorkspace;
		((EstParams_CompareResultsPanel)getPanelComponent()).setFrapWorkspace(frapWorkspace);
	}
    
}
