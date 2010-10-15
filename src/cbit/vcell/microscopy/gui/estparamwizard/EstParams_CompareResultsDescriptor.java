package cbit.vcell.microscopy.gui.estparamwizard;

import java.util.ArrayList;
import java.util.Hashtable;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.microscopy.ConfidenceInterval;
import cbit.vcell.microscopy.DataVerifyInfo;
import cbit.vcell.microscopy.FRAPData;
import cbit.vcell.microscopy.FRAPModel;
import cbit.vcell.microscopy.FRAPOptData;
import cbit.vcell.microscopy.FRAPOptimization;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.microscopy.FRAPSingleWorkspace;
import cbit.vcell.microscopy.ProfileData;
import cbit.vcell.microscopy.ProfileSummaryData;
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
	private FRAPSingleWorkspace frapWorkspace = null;
	
    public EstParams_CompareResultsDescriptor () {
        super(IDENTIFIER, new EstParams_CompareResultsPanel());
    }
    
    public String getNextPanelDescriptorID() {
        return Wizard.FINISH.getPanelDescriptorIdentifier();
    }

    public void aboutToDisplayPanel() 
    {
    	FRAPStudy fStudy = frapWorkspace.getWorkingFrapStudy();
    	//create Mean square error for different models under different ROIs
//    	if(fStudy.getAnalysisMSESummaryData() == null)
//    	{
    	fStudy.createAnalysisMSESummaryData();
//    	}
    	//auto find best model for user if best model is not selected. 
    	double[][] mseSummaryData = fStudy.getAnalysisMSESummaryData();
//    	for(int i =0; i<10; i++)
//    	System.out.print(mseSummaryData[0][i]+"  ");
    	
    	int bestModel = FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT;
    	if(fStudy.getBestModelIndex() != null)
    	{
    		bestModel = fStudy.getBestModelIndex().intValue();
    	}
    	else
    	{
    		//check least error model
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
	    	//check model significance if more than one model
	    	if(fStudy.getSelectedModels().size() > 1)
	    	{	
		    	ProfileSummaryData[][] allProfileSumData = frapWorkspace.getWorkingFrapStudy().getFrapOptData().getAllProfileSummaryData();
				FRAPModel[] frapModels = frapWorkspace.getWorkingFrapStudy().getModels();
				int confidenceIdx = ((EstParams_CompareResultsPanel)this.getPanelComponent()).getSelectedConfidenceIndex();
				boolean diffOneModelSignificance = true;
				boolean diffTwoModelSignificance = true;
				if(frapModels[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].getModelParameters() != null &&
				   allProfileSumData != null && allProfileSumData[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT]!= null && 
				   allProfileSumData[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT][0] != null)
				{
					for(int i=0; i<FRAPModel.NUM_MODEL_PARAMETERS_ONE_DIFF; i++)
					{
						ConfidenceInterval[] intervals = allProfileSumData[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT][i].getConfidenceIntervals();
						if(intervals[confidenceIdx].getUpperBound() == frapModels[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].getModelParameters()[i].getUpperBound() && 
						   intervals[confidenceIdx].getLowerBound() == frapModels[FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT].getModelParameters()[i].getLowerBound())
						{
							diffOneModelSignificance = false;
							break;
						}
					}
				}
				if(frapModels[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS].getModelParameters() != null &&
				   allProfileSumData != null && allProfileSumData[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS]!= null && 
				   allProfileSumData[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS][0] != null)
				{
					for(int i=0; i<FRAPModel.NUM_MODEL_PARAMETERS_TWO_DIFF; i++)
					{
						ConfidenceInterval[] intervals = allProfileSumData[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS][i].getConfidenceIntervals();
						if(intervals[confidenceIdx].getUpperBound() == frapModels[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS].getModelParameters()[i].getUpperBound() && 
						   intervals[confidenceIdx].getLowerBound() == frapModels[FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS].getModelParameters()[i].getLowerBound())
						{
							diffTwoModelSignificance = false;
							break;
						}
					}
				}
				if((diffOneModelSignificance && !diffTwoModelSignificance) || (! diffOneModelSignificance && diffTwoModelSignificance))
				{
					if(diffOneModelSignificance)
					{
						bestModel = FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT;
					}
					else if(diffTwoModelSignificance)
					{
						bestModel = FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS;
					}
				}
	    	}
    	}
    	((EstParams_CompareResultsPanel)this.getPanelComponent()).setBestModelRadioButton(bestModel);
    	//set data source to multiSourcePlotPane
    	ArrayList<DataSource> comparableDataSource = new ArrayList<DataSource>(); // length shoulf be fStudy.getSelectedModels().size()+1, however, reaction binding may not have data
    	//add exp data
    	ReferenceData expReferenceData = FRAPOptimization.doubleArrayToSimpleRefData(fStudy.getDimensionReducedExpData(),
                                         fStudy.getFrapData().getImageDataset().getImageTimeStamps(), 
                                         fStudy.getStartingIndexForRecovery(), 
                                         fStudy.getSelectedROIsForErrorCalculation());
    	final DataSource expDataSource = new DataSource.DataSourceReferenceData("exp", expReferenceData); 
    	comparableDataSource.add(expDataSource);
    	//add opt/sim data
    	//using the same loop, disable the radio button if the model is not included
    	((EstParams_CompareResultsPanel)this.getPanelComponent()).disableAllRadioButtons();//adjust radio buttons
    	ArrayList<Integer> selectedModelIndexes = fStudy.getSelectedModels();
    	double[] expTimePoints = fStudy.getFrapData().getImageDataset().getImageTimeStamps();
    	
    	for(int i = 0; i<selectedModelIndexes.size(); i++)
    	{
    		
    		DataSource newDataSource = null;
    		if(selectedModelIndexes.get(i).equals(FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT))
    		{
    			((EstParams_CompareResultsPanel)this.getPanelComponent()).enableRadioButton(FRAPModel.IDX_MODEL_DIFF_ONE_COMPONENT);//adjust radio button
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
    			((EstParams_CompareResultsPanel)this.getPanelComponent()).enableRadioButton(FRAPModel.IDX_MODEL_DIFF_TWO_COMPONENTS);//adjust radio button
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
    			((EstParams_CompareResultsPanel)this.getPanelComponent()).enableRadioButton(FRAPModel.IDX_MODEL_DIFF_BINDING);//adjust radio button
    			FRAPModel temModel = fStudy.getFrapModel(FRAPModel.IDX_MODEL_DIFF_BINDING);
    			double startTimePoint = expTimePoints[fStudy.getStartingIndexForRecovery()];
    			if(temModel.getData() != null)
    			{
	    			ODESolverResultSet temSolverResultSet = FRAPOptimization.doubleArrayToSolverResultSet(temModel.getData(), 
	    					             temModel.getTimepoints(),
	    					             startTimePoint,
	    					             fStudy.getSelectedROIsForErrorCalculation());
	    			newDataSource = new DataSource.DataSourceOdeSolverResultSet("sim_DB", temSolverResultSet);
    			}
    		}
    		if(newDataSource != null)
    		{
    			comparableDataSource.add(newDataSource);
    		}
    	}
    	//set data to multiSourcePlotPane
    	((EstParams_CompareResultsPanel)this.getPanelComponent()).setPlotData(comparableDataSource.toArray(new DataSource[comparableDataSource.size()]));
	}
    
    public ArrayList<AsynchClientTask> preNextProcess()
    {
    	//create AsynchClientTask arraylist
		ArrayList<AsynchClientTask> taskArrayList = new ArrayList<AsynchClientTask>();
		
		AsynchClientTask saveBestModelTask = new AsynchClientTask("", AsynchClientTask.TASKTYPE_SWING_BLOCKING) 
		{
			public void run(Hashtable<String, Object> hashTable) throws Exception
			{
				int bestModelIndex = getBestModelIndex();
				getFrapWorkspace().getWorkingFrapStudy().setBestModelIndex(new Integer(bestModelIndex));
			}
		};
		
		taskArrayList.add(saveBestModelTask);
		return taskArrayList;
    }
    
    public int getBestModelIndex()
    {
    	return ((EstParams_CompareResultsPanel)this.getPanelComponent()).getRadioButtonPanel().getBestModelIndex();
    }
    
    public FRAPSingleWorkspace getFrapWorkspace() {
		return frapWorkspace;
	}
    
	public void setFrapWorkspace(FRAPSingleWorkspace frapWorkspace) {
		this.frapWorkspace = frapWorkspace;
		((EstParams_CompareResultsPanel)getPanelComponent()).setFrapWorkspace(frapWorkspace);
	}
    
}
