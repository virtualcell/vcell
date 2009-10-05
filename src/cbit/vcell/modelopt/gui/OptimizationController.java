package cbit.vcell.modelopt.gui;

import javax.swing.SwingUtilities;

import org.vcell.util.BeanUtils;
import org.vcell.util.Issue;
import org.vcell.util.gui.AsynchGuiUpdater;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.opt.OdeObjectiveFunction;
import cbit.vcell.opt.OptimizationSpec;
import cbit.vcell.opt.ReferenceData;
import cbit.vcell.opt.solvers.OptSolverCallbacks;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.Variable;
import cbit.vcell.modelopt.ParameterEstimationTask;
import cbit.vcell.modelopt.ReferenceDataMappingSpec;
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
			optTestPanel.getJProgressBar1().setValue(progressRunner % 100);
			progressRunner += 5;
		}
		protected void guiToDo() {
			refreshSolutionStatusDisplay(optSolverCallbacks);
			if (optSolverCallbacks.getPercentDone()==null){
				animationDuringInit();
			}else{
				optTestPanel.getJProgressBar1().setValue(optSolverCallbacks.getPercentDone().intValue());
			}
		}
		protected void guiToDo(java.lang.Object params) {
			if (params instanceof OptimizationResultSet){
				parameterEstimationTask.setOptimizationResultSet((OptimizationResultSet)params);
				this.stop();
				optTestPanel.getSolveButton().setEnabled(true);
				optTestPanel.getStopButton().setEnabled(false);
				optTestPanel.getJProgressBar1().setValue(0);
				optTestPanel.getSolverTypeComboBox().setEnabled(true);
			}else if (params instanceof Exception){
				parameterEstimationTask.appendSolverMessageText("\n"+((Exception)params).getMessage());
				DialogUtils.showErrorDialog(OptimizationController.this.optTestPanel,((Exception)params).getMessage());
				parameterEstimationTask.setOptimizationResultSet(null);
				this.stop();
				optTestPanel.getSolveButton().setEnabled(true);
				optTestPanel.getStopButton().setEnabled(false);
				optTestPanel.getJProgressBar1().setValue(0);
				optTestPanel.getSolverTypeComboBox().setEnabled(true);
			}
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
		
		ReferenceData referenceData = parameterEstimationTask.getModelOptimizationSpec().getReferenceData();
		if (referenceData!=null) {
			dataSourceList.add(new DataSource(referenceData,"refData"));
			String[] refColumnNames = referenceData.getColumnNames();
			for (int i = 0; i < refColumnNames.length; i ++) {
				if (refColumnNames[i].equals("t")) {
					continue;
				}
				nameVector.add(refColumnNames[i]);			
			}
		}
		
		ODESolverResultSet odeSolverResultSet = parameterEstimationTask.getOdeSolverResultSet();
		if (odeSolverResultSet!=null){
			dataSourceList.add(new DataSource(odeSolverResultSet,"odeData"));
			ReferenceDataMappingSpec[] mappingSpecs = parameterEstimationTask.getModelOptimizationSpec().getReferenceDataMappingSpecs();
			if (mappingSpecs != null) {
				for (int i = 0; i < mappingSpecs.length; i ++) {
					if (mappingSpecs[i].getReferenceDataColumnName().equals("t")) {
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
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/4/2006 10:01:41 AM)
 */
private void refreshSolutionStatusDisplay(OptSolverCallbacks optSolverCallbacks) {
	if (optSolverCallbacks!=null){
		optTestPanel.getNumEvaluationsValueLabel().setText(""+optSolverCallbacks.getEvaluationCount());
		OptSolverCallbacks.EvaluationHolder bestEvaluation = optSolverCallbacks.getBestEvaluation();
		if (bestEvaluation!=null){
			optTestPanel.getObjectiveFunctionValueLabel().setText(""+bestEvaluation.objFunctionValue);
		}else{
			optTestPanel.getObjectiveFunctionValueLabel().setText("");
		}
	}else{
		optTestPanel.getNumEvaluationsValueLabel().setText("");
		optTestPanel.getObjectiveFunctionValueLabel().setText("");
	}
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
		DialogUtils.showErrorDialog(optTestPanel,"Error creating simulation: "+e.getMessage());
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
	try {
		parameterEstimationTask.setOptimizationResultSet(null);
		parameterEstimationTask.setSolverMessageText("");
		optTestPanel.getSaveSolutionAsNewSimButton().setEnabled(false);
		parameterEstimationTask.appendSolverMessageText("generating optimization specification...\n");
		
		parameterEstimationTask.refreshMappings();
		java.util.Vector<Issue> issueList = new java.util.Vector<Issue>();
		parameterEstimationTask.gatherIssues(issueList);
		boolean bFailed = false;
		for (int i = 0; i < issueList.size(); i++){
			org.vcell.util.Issue issue = (Issue)issueList.elementAt(i);
			if (issue.getSeverity() == Issue.SEVERITY_ERROR){
				bFailed = true;
			}
			parameterEstimationTask.appendSolverMessageText(issue.getMessage()+"\n");
		}
		if (bFailed){
			parameterEstimationTask.appendSolverMessageText("fatal error, stopped.\n");
		}
		//appendOptimizationText("\n\n--------Optimization Specification------------\n\n");
		//appendOptimizationText(optSpec.getVCML()+"\n");
		parameterEstimationTask.appendSolverMessageText("working...\n");
		
		final OptimizationSpec optSpec = parameterEstimationTask.getModelOptimizationMapping().getOptimizationSpec();
		final cbit.vcell.opt.solvers.OptSolverCallbacks optSolverCallbacks = new cbit.vcell.opt.solvers.OptSolverCallbacks();
		final cbit.vcell.opt.OptimizationSolverSpec optSolverSpec = parameterEstimationTask.getOptimizationSolverSpec();
		parameterEstimationTask.setOptSolverCallbacks(optSolverCallbacks);
		final OptSolverUpdater optSolverUpdater = new OptSolverUpdater(optSolverCallbacks);
		optTestPanel.getJProgressBar1().setValue(0);
		optTestPanel.getSolverTypeComboBox().setEnabled(false);

		
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
							optTestPanel.getJProgressBar1().setValue(100);
						}
					});
					optSolverUpdater.stop();
					setRunning(false);
				}
			}
		};
		t.setPriority(Thread.NORM_PRIORITY-1);
		t.start();
		
		

		optTestPanel.getSolveButton().setEnabled(false);
		optTestPanel.getStopButton().setEnabled(true);
	}catch (Exception e){
		e.printStackTrace(System.out);
		getParameterEstimationTask().appendSolverMessageText("\n"+e.getMessage()+"\n");
		org.vcell.util.gui.DialogUtils.showErrorDialog(optTestPanel,e.getMessage());
	}
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