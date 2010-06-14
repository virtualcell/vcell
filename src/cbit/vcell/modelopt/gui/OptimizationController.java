package cbit.vcell.modelopt.gui;

import java.util.Hashtable;

import javax.swing.SwingUtilities;

import org.vcell.util.BeanUtils;
import org.vcell.util.Issue;
import org.vcell.util.gui.AsynchGuiUpdater;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.InconsistentDomainException;
import cbit.vcell.math.Variable;
import cbit.vcell.modelopt.ModelOptimizationSpec;
import cbit.vcell.modelopt.ParameterEstimationTask;
import cbit.vcell.modelopt.ReferenceDataMappingSpec;
import cbit.vcell.opt.OdeObjectiveFunction;
import cbit.vcell.opt.OptimizationException;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.opt.OptimizationSolverSpec;
import cbit.vcell.opt.OptimizationSpec;
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

/**
 * OptimizationController constructor comment.
 */
public OptimizationController(OptTestPanel arg_optTestPanel) {
	super();
	optTestPanel = arg_optTestPanel;
}


/**
 * Insert the method's description here.
 * Creation date: (5/4/2006 10:46:32 AM)
 * @return cbit.vcell.modelopt.ParameterEstimationTask
 */
public ParameterEstimationTask getParameterEstimationTask() {
	return parameterEstimationTask;
}


/**
 * Insert the method's description here.
 * Creation date: (5/4/2006 10:13:05 AM)
 * @return boolean
 */
public boolean isRunning() {
	return bRunning;
}


/**
 * Insert the method's description here.
 * Creation date: (5/4/2006 9:21:34 AM)
 */
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


/**
 * Insert the method's description here.
 * Creation date: (5/4/2006 10:01:41 AM)
 */
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


/**
 * Insert the method's description here.
 * Creation date: (5/4/2006 9:25:53 AM)
 */
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


/**
 * Insert the method's description here.
 * Creation date: (5/4/2006 10:43:05 AM)
 * @param arg_parameterEstimationTask cbit.vcell.modelopt.ParameterEstimationTask
 */
public void setParameterEstimationTask(ParameterEstimationTask arg_parameterEstimationTask) {
	parameterEstimationTask = arg_parameterEstimationTask;
	if (parameterEstimationTask!=null){
		refreshSolutionStatusDisplay(parameterEstimationTask.getOptSolverCallbacks());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/4/2006 10:13:05 AM)
 * @param newRunning boolean
 */
private void setRunning(boolean newRunning) {
	bRunning = newRunning;
}


/**
 * Insert the method's description here.
 * Creation date: (5/4/2006 9:27:42 AM)
 */
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


/**
 * Insert the method's description here.
 * Creation date: (5/4/2006 9:42:27 AM)
 */
public void stop() {
	if (parameterEstimationTask!=null && parameterEstimationTask.getOptSolverCallbacks()!=null){
		parameterEstimationTask.getOptSolverCallbacks().setStopRequested(true);
	}
	return;
}
}