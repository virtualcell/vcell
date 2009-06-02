package cbit.vcell.modelopt.gui;
import javax.swing.SwingUtilities;

import cbit.vcell.opt.OptimizationSpec;
import cbit.vcell.opt.solvers.OptSolverCallbacks;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.modelopt.ParameterEstimationTask;
/**
 * Insert the type's description here.
 * Creation date: (5/4/2006 9:19:47 AM)
 * @author: Jim Schaff
 */
public class OptimizationController {
	private OptTestPanel optTestPanel = null;
	private ParameterEstimationTask parameterEstimationTask = null;

	public class OptSolverUpdater extends org.vcell.util.gui.AsynchGuiUpdater {
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
				org.vcell.util.gui.DialogUtils.showErrorDialog(OptimizationController.this.optTestPanel,((Exception)params).getMessage());
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
public cbit.vcell.modelopt.ParameterEstimationTask getParameterEstimationTask() {
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
		java.util.Vector dataSourceList = new java.util.Vector();
		java.util.Vector nameVector = new java.util.Vector();
		
		cbit.vcell.opt.ReferenceData referenceData = parameterEstimationTask.getModelOptimizationSpec().getReferenceData();
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
		
		cbit.vcell.solver.ode.ODESolverResultSet odeSolverResultSet = parameterEstimationTask.getOdeSolverResultSet();
		if (odeSolverResultSet!=null){
			dataSourceList.add(new DataSource(odeSolverResultSet,"odeData"));
			cbit.vcell.modelopt.ReferenceDataMappingSpec[] mappingSpecs = parameterEstimationTask.getModelOptimizationSpec().getReferenceDataMappingSpecs();
			if (mappingSpecs != null) {
				for (int i = 0; i < mappingSpecs.length; i ++) {
					if (mappingSpecs[i].getReferenceDataColumnName().equals("t")) {
						continue;
					}
					cbit.vcell.math.Variable var = parameterEstimationTask.getMathSymbolMapping().getVariable(mappingSpecs[i].getModelObject());
					nameVector.add(var.getName());
				}
			}
		}
		DataSource[] dataSources = (DataSource[])org.vcell.util.BeanUtils.getArray(dataSourceList,DataSource.class);
		MultisourcePlotPane multisourcePlotPane = new MultisourcePlotPane();
		multisourcePlotPane.setDataSources(dataSources);	

		String[] nameArray = new String[nameVector.size()];
		nameArray = (String[])org.vcell.util.BeanUtils.getArray(nameVector, String.class);
		multisourcePlotPane.select(nameArray);
		
		org.vcell.util.gui.DialogUtils.showComponentCloseDialog(optTestPanel,multisourcePlotPane,"Data Plotter");
	}catch (cbit.vcell.parser.ExpressionException e){
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
		if (optSpec.getObjectiveFunction() instanceof cbit.vcell.opt.OdeObjectiveFunction){
			cbit.vcell.opt.OdeObjectiveFunction odeObjFunction = (cbit.vcell.opt.OdeObjectiveFunction)optSpec.getObjectiveFunction();
			//
			// add new simulation to the Application (other bookkeeping required?)
			//
			cbit.vcell.mapping.SimulationContext simContext = parameterEstimationTask.getModelOptimizationSpec().getSimulationContext();
			cbit.vcell.solver.Simulation newSim = simContext.addNewSimulation();
			parameterEstimationTask.getModelOptimizationMapping().applySolutionToMathOverrides(newSim,parameterEstimationTask.getOptimizationResultSet());
			org.vcell.util.gui.DialogUtils.showInfoDialog("created simulation \""+newSim.getName()+"\"");
		}
	}catch (Exception e){
		e.printStackTrace(System.out);
		org.vcell.util.gui.DialogUtils.showErrorDialog(optTestPanel,"Error creating simulation: "+e.getMessage());
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
		
		cbit.vcell.modelopt.ModelOptimizationMapping modelOptMapping = null;
		parameterEstimationTask.refreshMappings();
		java.util.Vector issueList = new java.util.Vector();
		parameterEstimationTask.gatherIssues(issueList);
		boolean bFailed = false;
		for (int i = 0; i < issueList.size(); i++){
			org.vcell.util.Issue issue = (org.vcell.util.Issue)issueList.elementAt(i);
			if (issue.getSeverity() == org.vcell.util.Issue.SEVERITY_ERROR){
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
				try {
					optSolverUpdater.setDelay(100);
					optSolverUpdater.start();
					setRunning(true);
					final OptimizationResultSet optResultSet = optTestPanel.getOptimizationService().solve(optSpec,optSolverSpec,optSolverCallbacks);
					SwingUtilities.invokeLater(new Runnable(){
						public void run() {							
							optSolverUpdater.guiToDo(optResultSet);
							optTestPanel.getJProgressBar1().setValue(100);
						}
					});
				}catch (final Exception e){
					e.printStackTrace(System.out);
					SwingUtilities.invokeLater(new Runnable(){
						public void run() {	
							optSolverUpdater.guiToDo(e);
						}
					});
				}finally{
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