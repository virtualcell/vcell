package cbit.vcell.microscopy.gui.estparamwizard;

import java.util.ArrayList;
import java.util.Hashtable;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.microscopy.DataVerifyInfo;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPOptimization;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.FRAPWorkspace;
import cbit.vcell.microscopy.SpatialAnalysisResults;
import cbit.vcell.microscopy.gui.defineROIwizard.DefineROI_BleachedROIDescriptor;
import cbit.vcell.microscopy.gui.loaddatawizard.LoadFRAPData_SummaryPanel;
import cbit.vcell.modelopt.gui.DataSource;
import cbit.vcell.opt.Parameter;
import cbit.vcell.opt.ReferenceData;
import cbit.vcell.solver.ode.ODESolverResultSet;

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
    	//create Mean square error for different models under different ROIs
    	if(fStudy.getAnalysisMSESummaryData() == null)
    	{
    		fStudy.createAnalysisMSESummaryData();
    	}
    	//auto find best model for user if best model is not selected.
    	double[][] mseSummaryData = fStudy.getAnalysisMSESummaryData();
    	
    	int bestModel = FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT;
    	if(fStudy.getBestModelIndex() != null)
    	{
    		bestModel = fStudy.getBestModelIndex().intValue();
    	}
    	else
    	{
	    	double minError = 1000;
	    	if(mseSummaryData != null)
	    	{
	    		int secDimLen = FRAPData.VFRAP_ROI_ENUM.values().length - 2 + 1;//exclude cell and bkground ROIs, include sum of error
	    		for(int i=0; i<FRAPModel.NUM_MODEL_TYPES; i++)
	    		{
	    			if(minError > mseSummaryData[i][secDimLen - 1])
	    			{
	    				minError = mseSummaryData[i][secDimLen - 1];
	    				bestModel = i;
	    			}
	    		}
	    	}
    	}
    	((EstParams_CompareResultsPanel)this.getPanelComponent()).setBestModelRadioButton(bestModel);
    	//set data source to multiSourcePlotPane
    	DataSource[] comparableDataSource = new DataSource[fStudy.getSelectedModels().size() + 1]; //selected models + exp
    	//add exp data
    	ReferenceData expReferenceData = FRAPOptimization.doubleArrayToSimpleRefData(fStudy.getDimensionReducedExpData(),
                                         fStudy.getFrapData().getImageDataset().getImageTimeStamps(), 
                                         fStudy.getStartingIndexForRecovery(), 
                                         fStudy.getSelectedROIsForErrorCalculation());
    	final DataSource expDataSource = new DataSource.DataSourceReferenceData("exp", expReferenceData); 
    	comparableDataSource[0] = expDataSource;
    	//add opt/sim data
    	ArrayList<Integer> selectedModelIndexes = fStudy.getSelectedModels();
    	double[] expTimePoints = fStudy.getFrapData().getImageDataset().getImageTimeStamps();
    	
    	for(int i = 0; i<selectedModelIndexes.size(); i++)
    	{
    		
    		DataSource newDataSource = null;
    		if(selectedModelIndexes.get(i).equals(FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT))
    		{
    			FRAPModel temModel = fStudy.getFrapModel(FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT);
    			double[] timePoints = fStudy.getFrapData().getImageDataset().getImageTimeStamps();
    			int startingIndex = fStudy.getStartingIndexForRecovery();
    			double[] truncatedTimes = new double[timePoints.length-startingIndex];
    			System.arraycopy(timePoints, startingIndex, truncatedTimes, 0, truncatedTimes.length);
    			ODESolverResultSet temSolverResultSet = FRAPOptimization.doubleArrayToSolverResultSet(temModel.getData(), 
    					             truncatedTimes,
    					             0,
    					             fStudy.getSelectedROIsForErrorCalculation());
    			newDataSource = new DataSource.DataSourceOdeSolverResultSet("opt_DF1", temSolverResultSet);
    		}
    		else if(selectedModelIndexes.get(i).equals(FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS))
    		{
    			FRAPModel temModel = fStudy.getFrapModel(FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS);
    			double[] timePoints = fStudy.getFrapData().getImageDataset().getImageTimeStamps();
    			int startingIndex = fStudy.getStartingIndexForRecovery();
    			double[] truncatedTimes = new double[timePoints.length-startingIndex];
    			System.arraycopy(timePoints, startingIndex, truncatedTimes, 0, truncatedTimes.length);
    			ODESolverResultSet temSolverResultSet = FRAPOptimization.doubleArrayToSolverResultSet(temModel.getData(), 
    								 truncatedTimes,
    					             0,
    					             fStudy.getSelectedROIsForErrorCalculation());
    			newDataSource = new DataSource.DataSourceOdeSolverResultSet("opt_DF2", temSolverResultSet);
    		}
    		else if(selectedModelIndexes.get(i).equals(FRAPModel.IDX_MODEL_DIFF_BINDING))
    		{
    			FRAPModel temModel = fStudy.getFrapModel(FRAPModel.IDX_MODEL_DIFF_BINDING);
    			double startTimePoint = expTimePoints[fStudy.getStartingIndexForRecovery()];
    			ODESolverResultSet temSolverResultSet = FRAPOptimization.doubleArrayToSolverResultSet(temModel.getData(), 
    					             temModel.getTimepoints(),
    					             startTimePoint,
    					             fStudy.getSelectedROIsForErrorCalculation());
    			newDataSource = new DataSource.DataSourceOdeSolverResultSet("opt_DB", temSolverResultSet);
    		}
    		
    		comparableDataSource[i+1] = newDataSource;
    	}
    	//set data to multiSourcePlotPane
    	((EstParams_CompareResultsPanel)this.getPanelComponent()).setPlotData(comparableDataSource);
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
