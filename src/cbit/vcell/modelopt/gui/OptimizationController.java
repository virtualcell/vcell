package cbit.vcell.modelopt.gui;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.SwingUtilities;

import org.vcell.optimization.ConfidenceInterval;
import org.vcell.optimization.OptSolverResultSet;
import org.vcell.optimization.ProfileData;
import org.vcell.optimization.ProfileDataElement;
import org.vcell.optimization.ProfileSummaryData;
import org.vcell.optimization.OptSolverResultSet.ProfileDistribution;
import org.vcell.util.BeanUtils;
import org.vcell.util.DescriptiveStatistics;
import org.vcell.util.Issue;
import org.vcell.util.gui.AsynchGuiUpdater;
import org.vcell.util.gui.DialogUtils;

import cbit.plot.Plot2D;
import cbit.plot.PlotData;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.math.InconsistentDomainException;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.Variable;
import cbit.vcell.matrix.MatrixException;
import cbit.vcell.model.ModelException;
import cbit.vcell.modelopt.ModelOptimizationSpec;
import cbit.vcell.modelopt.ParameterEstimationTask;
import cbit.vcell.modelopt.ParameterMappingSpec;
import cbit.vcell.modelopt.ReferenceDataMappingSpec;
import cbit.vcell.opt.OdeObjectiveFunction;
import cbit.vcell.opt.OptimizationException;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.opt.OptimizationSolverSpec;
import cbit.vcell.opt.OptimizationSpec;
import cbit.vcell.opt.Parameter;
import cbit.vcell.opt.ReferenceData;
import cbit.vcell.opt.solvers.OptSolverCallbacks;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.ode.ODESolverResultSet;
/**
 * Insert the type's description here.
 * Creation date: (5/4/2006 9:19:47 AM)
 * @author: Jim Schaff
 */
public class OptimizationController {
	private OptTestPanel optTestPanel = null;
	private ParameterEstimationTask parameterEstimationTask = null;

	public class OptSolverUpdater extends AsynchGuiUpdater {
		private int progressRunner = 0;
		private OptSolverCallbacks optSolverCallbacks = null;

		public OptSolverUpdater(OptSolverCallbacks argOptSolverCallbacks) {
			optSolverCallbacks = argOptSolverCallbacks;
		}
		private void animationDuringInit() {
			optTestPanel.setProgress(progressRunner % 100);
			progressRunner += 5;
		}
		protected void guiToDo() {
			refreshSolutionStatusDisplay(optSolverCallbacks);
			if (optSolverCallbacks.getPercentDone()==null){
				animationDuringInit();
			}else{
				optTestPanel.setProgress(optSolverCallbacks.getPercentDone().intValue());
			}
		}
		protected void guiToDo(java.lang.Object params) {
			if (params instanceof OptimizationResultSet){
				parameterEstimationTask.setOptimizationResultSet((OptimizationResultSet)params);
			}else if (params instanceof Exception){
				parameterEstimationTask.appendSolverMessageText("\n"+((Exception)params).getMessage());
				DialogUtils.showErrorDialog(OptimizationController.this.optTestPanel,((Exception)params).getMessage(), (Exception)params);
				parameterEstimationTask.setOptimizationResultSet(null);
			}
			this.stop();
			optTestPanel.updateInterface(false);
		}
	}
	private boolean bRunning = false;

	public OptimizationController(OptTestPanel arg_optTestPanel) {
		super();
		optTestPanel = arg_optTestPanel;
	}

	public ParameterEstimationTask getParameterEstimationTask() {
		return parameterEstimationTask;
	}

	public boolean isRunning() {
		return bRunning;
	}

	public void plot() {
		try {
			java.util.Vector<DataSource> dataSourceList = new java.util.Vector<DataSource>();
			java.util.Vector<String> nameVector = new java.util.Vector<String>();

			ModelOptimizationSpec modelOptimizationSpec = parameterEstimationTask.getModelOptimizationSpec();
			ReferenceDataMappingSpec[] mappingSpecs = modelOptimizationSpec.getReferenceDataMappingSpecs();
			int timeIndex = modelOptimizationSpec.getReferenceDataTimeColumnIndex();

			ReferenceData referenceData = modelOptimizationSpec.getReferenceData();
			if (referenceData!=null) {
				dataSourceList.add(new DataSource.DataSourceReferenceData("refData", timeIndex, referenceData));
				String[] refColumnNames = referenceData.getColumnNames();
				for (int i = 0; i < refColumnNames.length; i ++) {
					if (i == timeIndex) {
						continue;
					}
					nameVector.add(refColumnNames[i]);			
				}
			}

			ODESolverResultSet odeSolverResultSet = parameterEstimationTask.getOdeSolverResultSet();
			if (odeSolverResultSet!=null){
				dataSourceList.add(new DataSource.DataSourceOdeSolverResultSet("estData", odeSolverResultSet));
				if (mappingSpecs != null) {
					for (int i = 0; i < mappingSpecs.length; i ++) {
						if (i == timeIndex) {
							continue;
						}
						Variable var = parameterEstimationTask.getMathSymbolMapping().getVariable(mappingSpecs[i].getModelObject());
						nameVector.add(var.getName());
					}
				}
			}
			DataSource[] dataSources = (DataSource[])BeanUtils.getArray(dataSourceList,DataSource.class);
			MultisourcePlotPane multisourcePlotPane = new MultisourcePlotPane();
			multisourcePlotPane.setDataSources(dataSources);	

			String[] nameArray = new String[nameVector.size()];
			nameArray = (String[])BeanUtils.getArray(nameVector, String.class);
			multisourcePlotPane.select(nameArray);

			DialogUtils.showComponentCloseDialog(optTestPanel,multisourcePlotPane,"Data Plotter");
		}catch (ExpressionException e){
			e.printStackTrace(System.out);
		} catch (InconsistentDomainException e) {
			e.printStackTrace(System.out);
		}
	}

	public ProfileSummaryData[] evaluateConfidenceInterval() throws MappingException, MathException, MatrixException, ExpressionException, ModelException 
	{
		ProfileSummaryData[] summaryData = getSummaryFromOptSolverResultSet(parameterEstimationTask.getOptimizationResultSet().getOptSolverResultSet());
		return summaryData;
	}

	public ProfileSummaryData[] getSummaryFromOptSolverResultSet(OptSolverResultSet osrs) throws MappingException, MathException, MatrixException, ExpressionException, ModelException 
	{
		ProfileSummaryData[] summaryData = null;
		ArrayList<ProfileDistribution> profileDistributionList = osrs.getProfileDistributionList();
		if(profileDistributionList != null && profileDistributionList.size() > 0)
		{
			//get parameter mapping specs from which can we have lower and upper bound
			SimulationContext simulationContext = parameterEstimationTask.getModelOptimizationSpec().getSimulationContext();
			StructureMapping structureMapping = simulationContext.getGeometryContext().getStructureMappings()[0];
			ParameterMappingSpec[] parameterMappingSpecs = parameterEstimationTask.getModelOptimizationSpec().getParameterMappingSpecs();
			MathMapping mathMapping = simulationContext.createNewMathMapping();
			MathDescription origMathDesc = mathMapping.getMathDescription();
//			mathSymbolMapping = mathMapping.getMathSymbolMapping();
			summaryData = new ProfileSummaryData[profileDistributionList.size()];
			for(int k=0; k < profileDistributionList.size(); k++)
			{
				ProfileDistribution profileDistribution = profileDistributionList.get(k);
				String fixedParamName = profileDistribution.getFixedParamName();
				ParameterMappingSpec fixedParamMappingSpec = null;
				for (ParameterMappingSpec pms : parameterMappingSpecs) {
					if (pms.isSelected()) {
						String mathSymbol = mathMapping.getMathSymbol(pms.getModelParameter(),structureMapping.getGeometryClass());
						if (mathSymbol.equals(fixedParamName)) {
							fixedParamMappingSpec = pms;
							break;
						}
					}
				}
				if(fixedParamMappingSpec == null)
				{
					throw new MappingException("Can not find parameter " + fixedParamName);
				}
				int paramValueIdx = osrs.getFixedParameterIndex(fixedParamName);
				if(paramValueIdx > -1)
				{
					ArrayList<OptSolverResultSet.OptRunResultSet> optRunRSList= profileDistribution.getOptRunResultSetList();
					double[] paramValArray = new double[optRunRSList.size()];
					double[] errorArray = new double[optRunRSList.size()];
					//profile likelihood curve
					for(int i=0; i<optRunRSList.size(); i++)
					{
						paramValArray[i] = Math.log10(optRunRSList.get(i).getParameterValues()[paramValueIdx]);//TODO: not sure if the paramvalue is calcualted by log10(). 
						errorArray[i] = optRunRSList.get(i).getObjectiveFunctionValue();
					}
					PlotData dataPlot = new PlotData(paramValArray, errorArray);
					//get confidence interval line
					//make array copy in order to not change the data orders afte the sorting
					double[] paramValArrayCopy = new double[paramValArray.length];
					System.arraycopy(paramValArray, 0, paramValArrayCopy, 0, paramValArray.length);
					double[] errorArrayCopy = new double[errorArray.length];
					System.arraycopy(errorArray, 0, errorArrayCopy, 0, errorArray.length);
					DescriptiveStatistics paramValStat = DescriptiveStatistics.CreateBasicStatistics(paramValArrayCopy);
					DescriptiveStatistics errorStat = DescriptiveStatistics.CreateBasicStatistics(errorArrayCopy);
					double[] xArray = new double[2];
					double[][] yArray = new double[ConfidenceInterval.NUM_CONFIDENCE_LEVELS][2];
					//get confidence level plot lines
					xArray[0] = paramValStat.getMin() -  (Math.abs(paramValStat.getMin()) * 0.2);
					xArray[1] = paramValStat.getMax() + (Math.abs(paramValStat.getMax()) * 0.2) ;
					for(int i=0; i<ConfidenceInterval.NUM_CONFIDENCE_LEVELS; i++)
					{
						yArray[i][0] = errorStat.getMin() + ConfidenceInterval.DELTA_ALPHA_VALUE[i];
						yArray[i][1] = yArray[i][0];
					}
					PlotData confidence80Plot = new PlotData(xArray, yArray[ConfidenceInterval.IDX_DELTA_ALPHA_80]);
					PlotData confidence90Plot = new PlotData(xArray, yArray[ConfidenceInterval.IDX_DELTA_ALPHA_90]);
					PlotData confidence95Plot = new PlotData(xArray, yArray[ConfidenceInterval.IDX_DELTA_ALPHA_95]);
					PlotData confidence99Plot = new PlotData(xArray, yArray[ConfidenceInterval.IDX_DELTA_ALPHA_99]);
					//generate plot2D data
					Plot2D plots = new Plot2D(null,new String[] {"profile Likelihood Data", "80% confidence", "90% confidence", "95% confidence", "99% confidence"}, 
							                  new PlotData[] {dataPlot, confidence80Plot, confidence90Plot, confidence95Plot, confidence99Plot},
							                  new String[] {"Profile likelihood of " + fixedParamName, "Log base 10 of "+fixedParamName, "Profile Likelihood"}, 
							                  new boolean[] {true, true, true, true, true});
					//get the best parameter for the minimal error
					int minErrIndex = -1;
					for(int i=0; i<errorArray.length; i++)
					{
						if(errorArray[i] == errorStat.getMin())
						{
							minErrIndex = i;
							break;
						}
					}
					double bestParamVal = Math.pow(10,paramValArray[minErrIndex]);
					//find confidence interval points
					ConfidenceInterval[] intervals = new ConfidenceInterval[ConfidenceInterval.NUM_CONFIDENCE_LEVELS];
					//half loop through the errors(left side curve)
					int[] smallLeftIdx = new int[ConfidenceInterval.NUM_CONFIDENCE_LEVELS]; 
					int[] bigLeftIdx = new int[ConfidenceInterval.NUM_CONFIDENCE_LEVELS];
					for(int i=0; i<ConfidenceInterval.NUM_CONFIDENCE_LEVELS; i++)
					{
						smallLeftIdx[i] = -1;
						bigLeftIdx[i] = -1;
						for(int j=1; j < minErrIndex+1 ; j++)//loop from bigger error to smaller error
						{
							if((errorArray[j] < (errorStat.getMin()+ConfidenceInterval.DELTA_ALPHA_VALUE[i])) &&
							   (errorArray[j-1] > (errorStat.getMin()+ConfidenceInterval.DELTA_ALPHA_VALUE[i])))
							{
								smallLeftIdx[i]= j-1;
								bigLeftIdx[i]=j;
								break;
							}
						}
					}
					//another half loop through the errors(right side curve)
					int[] smallRightIdx = new int[ConfidenceInterval.NUM_CONFIDENCE_LEVELS]; 
					int[] bigRightIdx = new int[ConfidenceInterval.NUM_CONFIDENCE_LEVELS];
					for(int i=0; i<ConfidenceInterval.NUM_CONFIDENCE_LEVELS; i++)
					{
						smallRightIdx[i] = -1;
						bigRightIdx[i] = -1;
						for(int j=(minErrIndex+1); j<errorArray.length; j++)//loop from bigger error to smaller error
						{
							if((errorStat.getMin()+ConfidenceInterval.DELTA_ALPHA_VALUE[i]) < errorArray[j] &&
							   (errorStat.getMin()+ConfidenceInterval.DELTA_ALPHA_VALUE[i]) > errorArray[j-1])
							{
								smallRightIdx[i]= j-1;
								bigRightIdx[i]=j;
								break;
							}
						}
					}
					//calculate intervals 
					for(int i=0; i<ConfidenceInterval.NUM_CONFIDENCE_LEVELS; i++)
					{
						double lowerBound = Double.NEGATIVE_INFINITY;
						boolean bLowerBoundOpen = true;
						double upperBound = Double.POSITIVE_INFINITY;
						boolean bUpperBoundOpen = true;
						if(smallLeftIdx[i] == -1 && bigLeftIdx[i] == -1)//no lower bound
						{
							
							lowerBound = fixedParamMappingSpec.getLow();//parameter LowerBound;
							bLowerBoundOpen = false;
						}
						else if(smallLeftIdx[i] != -1 && bigLeftIdx[i] != -1)//there is a lower bound
						{
							//x=x1+(x2-x1)*(y-y1)/(y2-y1);
							double x1 = paramValArray[smallLeftIdx[i]];
							double x2 = paramValArray[bigLeftIdx[i]];
							double y = errorStat.getMin()+ConfidenceInterval.DELTA_ALPHA_VALUE[i];
							double y1 = errorArray[smallLeftIdx[i]];
							double y2 = errorArray[bigLeftIdx[i]];
							lowerBound = x1+(x2-x1)*(y-y1)/(y2-y1);
							lowerBound = Math.pow(10,lowerBound);
							bLowerBoundOpen = false;
						}
						if(smallRightIdx[i] == -1 && bigRightIdx[i] == -1)//no upper bound
						{
							upperBound = fixedParamMappingSpec.getHigh();//parameter UpperBound;
							bUpperBoundOpen = false;
						}
						else if(smallRightIdx[i] != -1 && bigRightIdx[i] != -1)//there is a upper bound
						{
							//x=x1+(x2-x1)*(y-y1)/(y2-y1);
							double x1 = paramValArray[smallRightIdx[i]];
							double x2 = paramValArray[bigRightIdx[i]];
							double y = errorStat.getMin()+ConfidenceInterval.DELTA_ALPHA_VALUE[i];
							double y1 = errorArray[smallRightIdx[i]];
							double y2 = errorArray[bigRightIdx[i]];
							upperBound = x1+(x2-x1)*(y-y1)/(y2-y1);
							upperBound = Math.pow(10,upperBound);
							bUpperBoundOpen = false;
						}
						intervals[i] = new ConfidenceInterval(lowerBound, bLowerBoundOpen, upperBound, bUpperBoundOpen);
					}
					
					summaryData[k] =  new ProfileSummaryData(plots, bestParamVal, intervals, fixedParamName);
				}
			}
	    }
		return summaryData;
	}
	
	private void refreshSolutionStatusDisplay(OptSolverCallbacks optSolverCallbacks) {
		String numEvals = "";
		String objVal = "";
		if (optSolverCallbacks!=null){
			numEvals = optSolverCallbacks.getEvaluationCount() + "";
			OptSolverCallbacks.EvaluationHolder bestEvaluation = optSolverCallbacks.getBestEvaluation();
			if (bestEvaluation!=null){
				objVal = bestEvaluation.objFunctionValue + "";
			}
		}
		optTestPanel.setNumEvaluations(numEvals);
		optTestPanel.setObjectFunctionValue(objVal);	
	}

	public void saveSolutionAsNewSimulation() {
		try {
			OptimizationSpec optSpec = parameterEstimationTask.getModelOptimizationMapping().getOptimizationSpec();
			if (optSpec == null){
				throw new RuntimeException("optimization not yet performed");
			}
			if (optSpec.getObjectiveFunction() instanceof OdeObjectiveFunction){
				//
				// add new simulation to the Application (other bookkeeping required?)
				//
				SimulationContext simContext = parameterEstimationTask.getModelOptimizationSpec().getSimulationContext();
				Simulation newSim = simContext.addNewSimulation();
				parameterEstimationTask.getModelOptimizationMapping().applySolutionToMathOverrides(newSim,parameterEstimationTask.getOptimizationResultSet());
				DialogUtils.showInfoDialog(optTestPanel, "created simulation \""+newSim.getName()+"\"");
			}
		}catch (Exception e){
			e.printStackTrace(System.out);
			DialogUtils.showErrorDialog(optTestPanel,"Error creating simulation: "+e.getMessage(), e);
		}
	}

	public void setParameterEstimationTask(ParameterEstimationTask arg_parameterEstimationTask) {
		parameterEstimationTask = arg_parameterEstimationTask;
		if (parameterEstimationTask!=null){
			refreshSolutionStatusDisplay(parameterEstimationTask.getOptSolverCallbacks());
		}
	}

	private void setRunning(boolean newRunning) {
		bRunning = newRunning;
	}

	public void solve() {
		AsynchClientTask task1 = new AsynchClientTask("update gui", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				parameterEstimationTask.setOptimizationResultSet(null);
				parameterEstimationTask.setSolverMessageText("");
				parameterEstimationTask.appendSolverMessageText("generating optimization specification...\n");
			}
		};

		AsynchClientTask task2 = new AsynchClientTask("refresh mapping", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				StringBuffer issueText = new StringBuffer();
				parameterEstimationTask.refreshMappings();
				java.util.Vector<Issue> issueList = new java.util.Vector<Issue>();
				parameterEstimationTask.gatherIssues(issueList);
				boolean bFailed = false;
				for (int i = 0; i < issueList.size(); i++){
					Issue issue = (Issue)issueList.elementAt(i);
					issueText.append(issue.getMessage()+"\n");
					if (issue.getSeverity() == Issue.SEVERITY_ERROR){
						bFailed = true;
						break;
					}
				}
				if (bFailed){
					issueText.append("fatal error, stopped.\n");
					parameterEstimationTask.appendSolverMessageText(issueText.toString());
					throw new OptimizationException(parameterEstimationTask.getSolverMessageText());
				}
			}
		};

		AsynchClientTask task3 = new AsynchClientTask("set message", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				optTestPanel.updateInterface(true);
				parameterEstimationTask.appendSolverMessageText("working...\n");
				OptSolverCallbacks optSolverCallbacks = new OptSolverCallbacks();
				parameterEstimationTask.setOptSolverCallbacks(optSolverCallbacks);
			}
		};

		AsynchClientTask task4 = new AsynchClientTask("solving", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {		
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				final OptimizationSpec optSpec = parameterEstimationTask.getModelOptimizationMapping().getOptimizationSpec();			
				final OptimizationSolverSpec optSolverSpec = parameterEstimationTask.getOptimizationSolverSpec();
				final OptSolverCallbacks optSolverCallbacks = parameterEstimationTask.getOptSolverCallbacks();
				final OptSolverUpdater optSolverUpdater = new OptSolverUpdater(optSolverCallbacks);

				Thread t = new Thread() {
					public void run() {
						OptimizationResultSet optResultSet = null;
						try {
							optSolverUpdater.setDelay(100);
							optSolverUpdater.start();
							setRunning(true);
							optResultSet = optTestPanel.getOptimizationService().solve(optSpec,optSolverSpec,optSolverCallbacks);
						}catch (final Exception e){
							e.printStackTrace(System.out);
						}finally{
							final OptimizationResultSet finalResultSet = optResultSet;
							SwingUtilities.invokeLater(new Runnable(){
								public void run() {	
									optSolverUpdater.guiToDo(finalResultSet);
								}
							});
							optSolverUpdater.stop();
							setRunning(false);
						}
					}
				};
				t.setPriority(Thread.NORM_PRIORITY-1);
				t.start();		
			}
		};
		ClientTaskDispatcher.dispatch(optTestPanel, new Hashtable<String, Object>(), new AsynchClientTask[] {task1, task2, task3, task4});
	}

	public void stop() {
		if (parameterEstimationTask!=null && parameterEstimationTask.getOptSolverCallbacks()!=null){
			parameterEstimationTask.getOptSolverCallbacks().setStopRequested(true);
		}
		return;
	}
}