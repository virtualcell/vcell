package cbit.vcell.client.desktop.simulation;

import java.awt.Dimension;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

import org.vcell.util.NumberUtils;

import cbit.gui.PropertyChangeListenerProxyVCell;
import cbit.vcell.client.ClientSimManager;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.document.SimulationOwner;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.JumpProcess;
import cbit.vcell.math.MathException;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.VarIniCondition;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.OutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.UniformOutputTimeSpec;
import cbit.vcell.solver.ode.gui.SimulationStatus;

public class SimulationWorkspace implements java.beans.PropertyChangeListener {
	private SimulationOwner simulationOwner = null;
	private ClientSimManager clientSimManager = null;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private cbit.vcell.solver.Simulation[] fieldSimulations = null;
	private JProgressBar[] statusBars = null;

/**
 * Insert the method's description here.
 * Creation date: (5/11/2004 2:26:44 PM)
 * @param workspace cbit.vcell.desktop.controls.Workspace
 */
public SimulationWorkspace(DocumentWindowManager documentWindowManager, SimulationOwner simulationOwner) {
	setSimulationOwner(simulationOwner);
	this.clientSimManager = new ClientSimManager(documentWindowManager, this);
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(new PropertyChangeListenerProxyVCell(listener));
}


/**
 * Insert the method's description here.
 * Creation date: (5/11/2004 3:52:25 PM)
 */
private String applyChanges(Simulation clonedSimulation, Simulation simulation) {
	// we cannot simply replace the simulation with the clone because of two reasons:
	// major - vetoable listeners are transient, thus not preserved during serialization/deserialization
	// minor - proper refreshing the simulation list would require delete/reinsert which would make it non-trivial to avoid reordering
	String errors = "";
	try {
		simulation.setName(clonedSimulation.getName());
	} catch (java.beans.PropertyVetoException exc) {
		errors += "\n" + exc.getMessage();
	}
	try {
		simulation.setDescription(clonedSimulation.getDescription());
	} catch (java.beans.PropertyVetoException exc) {
		errors += "\n" + exc.getMessage();
	}
	simulation.setMathOverrides(new MathOverrides(simulation, clonedSimulation.getMathOverrides()));
	try {
		simulation.setMeshSpecification(clonedSimulation.getMeshSpecification());
	} catch (java.beans.PropertyVetoException exc) {
		errors += "\n" + exc.getMessage();
	}
	try {
		simulation.setSolverTaskDescription(new SolverTaskDescription(simulation, clonedSimulation.getSolverTaskDescription()));
	} catch (java.beans.PropertyVetoException exc) {
		errors += "\n" + exc.getMessage();
	}
	simulation.setDataProcessingInstructions(clonedSimulation.getDataProcessingInstructions());
	simulation.setIsDirty(true);
	return errors;
}


/**
 * Insert the method's description here.
 * Creation date: (10/4/2004 6:36:12 AM)
 */
public String checkCompatibility(Simulation simulation) {
	SimulationOwner simOwner = getSimulationOwner();
	if (simOwner instanceof cbit.vcell.mathmodel.MathModel){
		return null;
	}
	if (simOwner instanceof SimulationContext){
		SimulationContext simContext = (SimulationContext)simOwner;
		if(simulation == null){
			return "Simulation does not exist";
		}
		if(simulation.getMathDescription() == null || simContext.getMathDescription() == null){
			return "Simulation has no mathDescription";
		}
		if(simContext.getGeometry() != simulation.getMathDescription().getGeometry()){
			return "Application geometry does not match Simulation geometry";
		}
		if(simulation.getMathDescription().isSpatial() && simulation.getMeshSpecification() == null){
			return "Simulation has no Mesh Specification";
		}
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (5/11/2004 2:57:10 PM)
 * @return boolean
 * @param simulation cbit.vcell.solver.Simulation
 */
private boolean checkSimulationParameters(Simulation simulation, JComponent parent) {
	String errorMessage = null;
	long maxTimepoints = Simulation.MAX_LIMIT_ODE_TIMEPOINTS;
	long warningTimepoints = Simulation.WARNING_ODE_TIMEPOINTS;
	if(simulation.getIsSpatial())
	{
		maxTimepoints = Simulation.MAX_LIMIT_PDE_TIMEPOINTS;
		warningTimepoints = Simulation.WARNING_PDE_TIMEPOINTS;
	}
	else if (simulation.getMathDescription().isStoch())
	{
		maxTimepoints = Simulation.MAX_LIMIT_STOCH_TIMEPOINTS;
		warningTimepoints = Simulation.WARNING_STOCH_TIMEPOINTS;
	}
	
	long maxSizeBytes = Simulation.MAX_LIMIT_0DE_MEGABYTES*1000000L;
	long warningSizeBytes = Simulation.WARNING_0DE_MEGABYTES*1000000L;
	if(simulation.getIsSpatial())
	{
		maxSizeBytes = Simulation.MAX_LIMIT_PDE_MEGABYTES*1000000L;
		warningSizeBytes = Simulation.WARNING_PDE_MEGABYTES*1000000L;
	}
	else if (simulation.getMathDescription().isStoch())
	{
		maxSizeBytes = Simulation.MAX_LIMIT_STOCH_MEGABYTES*1000000L;
		warningSizeBytes = Simulation.WARNING_STOCH_MEGABYTES*1000000L;
	}
	
	long expectedNumTimePoints;
	if(simulation.getMathDescription().isStoch())
	{
		expectedNumTimePoints = getEstimatedNumTimePointsForStoch(simulation); 
	}
	else
	{
		expectedNumTimePoints = getExpectedNumTimePoints(simulation);
	}
	long expectedSizeBytes = getExpectedSizeBytes(simulation);
	//
	// check for error conditions (hard limits on resources) ... Note: each user should have it's own limits (and quotas).
	//
	SolverTaskDescription solverTaskDescription = simulation.getSolverTaskDescription();
	SolverDescription solverDescription = solverTaskDescription.getSolverDescription();
	if (expectedNumTimePoints>maxTimepoints){
		errorMessage = "Too many timepoints to be saved ("+expectedNumTimePoints+")\n"+
						"maximum allowed is:\n" + 
						"     "+Simulation.MAX_LIMIT_ODE_TIMEPOINTS + " for compartmental ODE simulations\n" + 
						"     "+Simulation.MAX_LIMIT_PDE_TIMEPOINTS + " for spatial simulations\n"+
						"     "+Simulation.MAX_LIMIT_STOCH_TIMEPOINTS + " for compartmental stochastic simulations\n"+
						"recommended limits are:\n" + 
						"     "+Simulation.WARNING_ODE_TIMEPOINTS + " for compartmental ODE simulations\n" + 
						"     "+Simulation.WARNING_PDE_TIMEPOINTS + " for spatial simulations\n"+
						"     "+Simulation.WARNING_STOCH_TIMEPOINTS + " for compartmental stochastic simulations\n"+
						"Try saving fewer timepoints\n"+
						"If you need to exceed the quota, please contact us";
		//not used for multiple stochastic run
		if(solverTaskDescription.getStochOpt()!= null && solverTaskDescription.getStochOpt().getNumOfTrials()>1)
		{
			errorMessage = null;
		}
	} else if (expectedSizeBytes>maxSizeBytes){
		errorMessage = "Resulting dataset ("+(expectedSizeBytes/1000000L)+"MB) is too large\n"+
						"maximum size is:\n" + 
						"     "+Simulation.MAX_LIMIT_0DE_MEGABYTES + " MB for compartmental ODE simulations\n" + 
						"     "+Simulation.MAX_LIMIT_PDE_MEGABYTES + " MB for spatial simulations\n"+
						"     "+Simulation.MAX_LIMIT_STOCH_MEGABYTES + " MB for compartmental stochastic simulations\n"+
						"suggested limits are:\n" + 
						"     "+Simulation.WARNING_0DE_MEGABYTES + " MB for compartmental ODE simulations\n" + 
						"     "+Simulation.WARNING_PDE_MEGABYTES + " MB for spatial simulations\n"+
						"     "+Simulation.WARNING_STOCH_MEGABYTES + " MB for compartmental stochastic simulations\n"+
						"Try saving fewer timepoints or using a smaller mesh (if spatial)\n"+
						"If you need to exceed the quota, please contact us";
		//not used for multiple stochastic run
		if(solverTaskDescription.getStochOpt()!= null && solverTaskDescription.getStochOpt().getNumOfTrials()>1)
		{
			errorMessage = null;
		}
	} else if (simulation.getScanCount() > Simulation.MAX_LIMIT_SCAN_JOBS) {
		errorMessage = "Too many simulations (" + simulation.getScanCount() + ") required for parameter scan.\n" +
						"maximum number of parameter sets is: " + Simulation.MAX_LIMIT_SCAN_JOBS + " \n" + 
						"suggested limit for number of parameter sets is: " + Simulation.WARNING_SCAN_JOBS + " \n" + 
						"Try choosing fewer parameters or reducing the size of scan for each parameter.";
		//not used for multiple stochastic run
		if(simulation.getMathDescription().isStoch() && 
		   solverTaskDescription.getStochOpt()!= null && solverTaskDescription.getStochOpt().getNumOfTrials()>1)
		{
			errorMessage = null;
		}
	}	
	else if(getSimulationOwner() != null)
	{ //to gurantee stochastic model uses stochastic methods and deterministic model uses ODE/PDE methods. 
		if (getSimulationOwner() instanceof SimulationContext)
		{
			if(((SimulationContext)getSimulationOwner()).isStoch())
			{
				if(! (solverDescription.isSTOCHSolver()))
					errorMessage = "Stochastic simulation(s) must use stochastic solver(s).\n" +
			               			solverDescription.getDisplayLabel()+" is not a stochastic solver!";
			}
			else
			{
				if((solverDescription.isSTOCHSolver()))
					errorMessage = "ODE/PDE simulation(s) must use ODE/PDE solver(s).\n" +
					               solverDescription.getDisplayLabel()+" is not a ODE/PDE solver!";
				
			}
		}
		else if(getSimulationOwner() instanceof MathModel)
		{
			if(((MathModel)getSimulationOwner()).getMathDescription().isStoch())
			{
				if(! (solverDescription.isSTOCHSolver()))
					errorMessage = "Stochastic simulation(s) must use stochastic solver(s).\n" +
			               			solverDescription.getDisplayLabel()+" is not a stochastic solver!";	
			}
			else
			{
				if((solverDescription.isSTOCHSolver()))
					errorMessage = "ODE/PDE simulation(s) must use ODE/PDE solver(s).\n" +
					               solverDescription.getDisplayLabel()+" is not a ODE/PDE solver!";
			}
		}
	} 
	
	else {
		errorMessage = null;
	}
	if (errorMessage != null) {
		JOptionPane.showMessageDialog(parent, errorMessage, "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
		return false;
	}else{
		String warningMessage = null;
		//don't check warning message for stochastic multiple trials, let it run.
		if(simulation.getMathDescription().isStoch() && simulation.getSolverTaskDescription().getStochOpt()!=null &&
				   simulation.getSolverTaskDescription().getStochOpt().getNumOfTrials()>1)
		{
			return true;
		}
		//
		// no error conditions, check for warning conditions (suggested limits on resources)
		//
		if (expectedNumTimePoints>warningTimepoints){
			warningMessage = "Warning: large number of timepoints ("+expectedNumTimePoints+"), suggested limits are:\n" + 
							"     "+Simulation.WARNING_ODE_TIMEPOINTS + " for compartmental ODE simulations\n" + 
							"     "+Simulation.WARNING_PDE_TIMEPOINTS + " for spatial simulations\n" +
							"     "+Simulation.WARNING_STOCH_TIMEPOINTS + " for compartmental stochastic simulations\n" +
							"Try saving fewer timepoints";
		} else if (expectedSizeBytes>warningSizeBytes){
			warningMessage = "Warning: large simulation result set ("+(expectedSizeBytes/1000000L)+"MB) exceeds suggested limits of:\n" + 
							"     "+Simulation.WARNING_0DE_MEGABYTES + " MB for compartmental ODE simulations\n" + 
							"     "+Simulation.WARNING_PDE_MEGABYTES + " MB for spatial simulations\n" +
							"     "+Simulation.WARNING_STOCH_MEGABYTES + " MB for compartmental stochastic simulations\n" +
							"Try saving fewer timepoints or using a coarser mesh if spatial.";
		} else if (simulation.getScanCount() > Simulation.WARNING_SCAN_JOBS) {
			warningMessage = "Warning : large number of simulations (" + simulation.getScanCount() + ") required for parameter scan.\n" +
						"maximum number of parameter sets is: " + Simulation.MAX_LIMIT_SCAN_JOBS + " \n" + 
						"suggested limit for the number of parameter sets is: " + Simulation.WARNING_SCAN_JOBS + " \n" + 
						"Try choosing fewer parameters or reducing the size of scan for each parameter.";
		} 
		
		if (solverDescription.equals(SolverDescription.SundialsPDE)) {
			if (solverTaskDescription.getErrorTolerance().getRelativeErrorTolerance() > 1e-4) {
				String msg = "Warning : it is not reccomended to use a relative tolerance that is greater than \n1e-4 for " 
					+ solverDescription.getDisplayLabel() + ".";
				warningMessage = warningMessage == null? msg : warningMessage + "\n\n" + msg;
			}
		}
		if (warningMessage != null)
		{
			int result = JOptionPane.showConfirmDialog(parent, warningMessage + "\n\nDo you want to continue anyway?", "Warning", JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				// continue anyway
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}
}


/**
 * Comment
 */
int copySimulations(Simulation[] sims) throws java.beans.PropertyVetoException {
	if (sims == null || sims.length == 0) {
		return -1;
	}
	for (int i = 0; i < sims.length; i++){
		String errorMessage = checkCompatibility(sims[i]);
		if(errorMessage != null){
			PopupGenerator.showErrorDialog(errorMessage+"\nUpdate Math before copying simulations");
			return -1;
		}
	}
	Simulation copiedSim = null;
	for (int i = 0; i < sims.length; i++){
		copiedSim = getSimulationOwner().copySimulation(sims[i]);
	}
	// set selection to the last copied one
	if (copiedSim != null) {
		for (int i = 0; i < getSimulationOwner().getSimulations().length; i++){
			if (getSimulationOwner().getSimulations()[i].getName().equals(copiedSim.getName())) {
				return i;
			}
		}
	}
	return -1;
}


/**
 * Comment
 */
void deleteSimulations(Simulation[] sims) throws java.beans.PropertyVetoException {
	if (sims == null || sims.length == 0) {
		return;
	}
	for (int i = 0; i < sims.length; i++){
		getSimulationOwner().removeSimulation(sims[i]);
	}
}


/**
 * Comment
 */
void editSimulation(JComponent parent, Simulation simulation) {
	
	String errorMessage = checkCompatibility(simulation);
	if(errorMessage != null){
		PopupGenerator.showErrorDialog(errorMessage+"\nUpdate Math before editing");
		return;
	}
	
	SimulationEditor simEditor = new SimulationEditor();
	simEditor.prepareToEdit(simulation);
	
	JScrollPane scrollPane = new JScrollPane(simEditor);
    Dimension panesize = simEditor.getPreferredSize();
    scrollPane.setPreferredSize(new Dimension(panesize.width + 20, panesize.height + 20));


	boolean acceptable = false;
	String errors = null;
	try{
		do {
			int ok = PopupGenerator.showComponentOKCancelDialog(parent, scrollPane,"Edit: " + simulation.getName());
			if (ok != javax.swing.JOptionPane.OK_OPTION) {
				return; // user cancels, we discard
			} else {
				acceptable = checkSimulationParameters(simEditor.getClonedSimulation(), parent);
			}
		} while (! acceptable);
		Simulation clonedSimulation = simEditor.getClonedSimulation();
		if (clonedSimulation.compareEqual(simulation)) {
			return;
		}
		errors = applyChanges(clonedSimulation, simulation); //clonedSimulation is the new one.
		if (!errors.equals("")) {
			throw new Exception("Some or all of the changes could not be applied:" + errors);
		}
	}catch(Exception e){
		PopupGenerator.showErrorDialog(simEditor, e.getMessage());
	}
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
	getPropertyChange().firePropertyChange(evt);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, int oldValue, int newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, boolean oldValue, boolean newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * Insert the method's description here.
 * Creation date: (6/7/2004 12:41:27 PM)
 * @return cbit.vcell.client.ClientSimManager
 */
public cbit.vcell.client.ClientSimManager getClientSimManager() {
	return clientSimManager;
}


/**
 * Insert the method's description here.
 * Creation date: (6/12/2001 10:09:25 AM)
 * @return boolean
 * @param simulation cbit.vcell.solver.Simulation
 */
private long getExpectedNumTimePoints(Simulation simulation) {
	return simulation.getSolverTaskDescription().getExpectedNumTimePoints();
}

private long getEstimatedNumTimePointsForStoch(Simulation sim)
{
	SolverTaskDescription solverTaskDescription = sim.getSolverTaskDescription();
	TimeBounds tb = solverTaskDescription.getTimeBounds();
	double startTime = tb.getStartingTime();
	double endTime = tb.getEndingTime();
	OutputTimeSpec tSpec = solverTaskDescription.getOutputTimeSpec();
	//hybrid G_E and G_M are fixed time step methods using uniform output time spec
	if (tSpec.isUniform())
	{
		
		double outputTimeStep = ((UniformOutputTimeSpec)tSpec).getOutputTimeStep();
		return (long)((endTime - startTime)/outputTimeStep);
	}
	
	double maxProbability = 0;
	SubDomain subDomain = sim.getMathDescription().getSubDomains().nextElement();
	Vector<VarIniCondition> varInis = subDomain.getVarIniConditions();

	//get all the probability expressions
	ArrayList<Expression> probList = new ArrayList<Expression>();
	Vector<JumpProcess> jumpProcesses = subDomain.getJumpProcesses();
	for(int i=0; i<jumpProcesses.size(); i++)
	{
		probList.add(jumpProcesses.elementAt(i).getProbabilityRate());
	}
		
	//loop through probability expressions
	for(int i=0; i<probList.size(); i++)
	{
		try {
			Expression pExp = new Expression(probList.get(i));
			pExp.bindExpression(sim);
			pExp = sim.substituteFunctions(pExp);
			pExp = pExp.flatten();
			String[] symbols = pExp.getSymbols();
			//substitute stoch vars with it's initial condition expressions
			for(int j=0; j<symbols.length; j++)
			{
				for(int k = 0; k < varInis.size(); k++)
				{
					if(symbols[j].equals(varInis.elementAt(k).getVar().getName()))
					{
						pExp.substituteInPlace(new Expression(symbols[j]), new Expression(varInis.elementAt(k).getIniVal()));
						break;
					}
				}
			}
			pExp = sim.substituteFunctions(pExp);
			pExp = pExp.flatten();
			double val = pExp.evaluateConstant();
			if(maxProbability < val)
			{
				maxProbability = val;
			}
		} catch (ExpressionBindingException e) {
			System.out.println("Cannot estimate the total time points for stochastic simulation!! Due to the reason below...");
			e.printStackTrace();
		} catch (ExpressionException ex) {
			System.out.println("Cannot estimate the total time points for stochastic simulation!! Due to the reason below...");
			ex.printStackTrace();
		} catch (MathException e) {
			System.out.println("Cannot estimate the total time points for stochastic simulation!! Due to the reason below...");
			e.printStackTrace();
		}
	}
	int keepEvery = 1;
	if (tSpec.isDefault()) {
		keepEvery = ((DefaultOutputTimeSpec)tSpec).getKeepEvery();
	}
	//points = (endt-startt)/(t*keepEvery) = (endt - startt)/(keepEvery*1/prob)
	long estimatedPoints = Math.round((tb.getEndingTime()-tb.getStartingTime())*maxProbability/keepEvery)+1;
	return estimatedPoints;
}
	

/**
 * Insert the method's description here.
 * Creation date: (6/12/2001 10:09:25 AM)
 * @return boolean
 * @param simulation cbit.vcell.solver.Simulation
 */
private long getExpectedSizeBytes(Simulation simulation) {
	long numTimepoints;
	if(simulation.getMathDescription().isStoch())
	{
		numTimepoints = getEstimatedNumTimePointsForStoch(simulation); 
	}
	else
	{
		numTimepoints = getExpectedNumTimePoints(simulation); 
	}
	int x,y,z;
	int numVariables = 0;
	if (simulation.getIsSpatial()) {
		x = simulation.getMeshSpecification().getSamplingSize().getX();
		y = simulation.getMeshSpecification().getSamplingSize().getY();
		z = simulation.getMeshSpecification().getSamplingSize().getZ();
		//
		// compute number of volume variables only (they are multiplied by x*y*z)
		//
		numVariables = 0;
		cbit.vcell.math.Variable variables[] = simulation.getVariables();
		for (int i = 0; i < variables.length; i++){
			if (variables[i] instanceof cbit.vcell.math.VolVariable){
				numVariables++;
			}
		}
	}else{
		x = 1;
		y = 1;
		z = 1;		
		numVariables = 0;
		cbit.vcell.math.Variable variables[] = simulation.getVariables();
		for (int i = 0; i < variables.length; i++){
			if ((variables[i] instanceof cbit.vcell.math.VolVariable) ||
				(variables[i] instanceof cbit.vcell.math.Function)) {
				numVariables++;
			}
		}
	}
	// approximate, don't compute header size since it's negligible whenever we approach quota size anyway...
	long expectedSize = numTimepoints * numVariables * x * y * z * 8; // values are actually written as longs
	return expectedSize;
}


/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}


/**
 * Gets the simulations index property (cbit.vcell.solver.Simulation) value.
 * @return The simulations property value.
 * @param index The index value into the property array.
 * @see #setSimulations
 */
public int getSimulationIndex(Simulation sim) {
	for (int i = 0; i < getSimulations().length; i++){
		if (getSimulations()[i] == sim) {
			return i;
		}
	}
	return -1;
}


/**
 * Insert the method's description here.
 * Creation date: (6/4/2004 2:15:23 AM)
 * @return cbit.vcell.document.SimulationOwner
 */
public cbit.vcell.document.SimulationOwner getSimulationOwner() {
	return simulationOwner;
}


/**
 * Gets the simulations property (cbit.vcell.solver.Simulation[]) value.
 * @return The simulations property value.
 * @see #setSimulations
 */
public cbit.vcell.solver.Simulation[] getSimulations() {
	return fieldSimulations;
}


/**
 * Gets the simulations index property (cbit.vcell.solver.Simulation) value.
 * @return The simulations property value.
 * @param index The index value into the property array.
 * @see #setSimulations
 */
public cbit.vcell.solver.Simulation getSimulations(int index) {
	return getSimulations()[index];
}


/**
 * Comment
 */
SimulationStatus getSimulationStatus(Simulation simulation) {
	return getClientSimManager().getSimulationStatus(simulation);
}


/**
 * Comment
 */
Object getSimulationStatusDisplay(Simulation simulation) {
	int index = -1;
	if (getSimulations() == null) {
		return null;
	} else {
		for (int i = 0; i < getSimulations().length; i++){
			if (getSimulations()[i] == simulation) {
				index = i;
				break;
			}
		}
	}
	if (index == -1) {
		return null;
	} else {
		SimulationStatus simStatus = getClientSimManager().getSimulationStatus(simulation);
		boolean displayProgress = (simStatus.isRunning() || (simStatus.isFailed() && simStatus.numberOfJobsDone() < simulation.getScanCount()))
								  && simStatus.getProgress() != null && simStatus.getProgress().doubleValue() >= 0;
		if (displayProgress){
			double progress = simStatus.getProgress().doubleValue() / simulation.getScanCount();
			getStatusBars()[index].setValue((int)(progress * 100));
			getStatusBars()[index].setString(NumberUtils.formatNumber(progress * 100, 4) + "%");
			if (simStatus.isFailed()) {
				getStatusBars()[index].setString("One or more jobs failed");
			}
			return getStatusBars()[index];
		} else {
			return simStatus.getDetails();
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/10/2004 3:03:08 AM)
 * @return javax.swing.JProgressBar[]
 */
private javax.swing.JProgressBar[] getStatusBars() {
	return statusBars;
}


/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}


/**
 * Comment
 */
int newSimulation() throws java.beans.PropertyVetoException {
	Simulation newSim = getSimulationOwner().addNewSimulation();
	for (int i = 0; i < getSimulationOwner().getSimulations().length; i++){
		if (getSimulationOwner().getSimulations()[i].getName().equals(newSim.getName())) {
			return i;
		}
	}
	return -1;
}


	/**
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   	and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getSource() == getSimulationOwner() && evt.getPropertyName().equals("simulations")) {
		setSimulations(getSimulationOwner().getSimulations());
	}
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
	PropertyChangeListenerProxyVCell.removeProxyListener(getPropertyChange(), listener);
	getPropertyChange().removePropertyChangeListener(listener);
}


/**
 * Comment
 */
void runSimulations(Simulation[] sims) {
	getClientSimManager().runSimulations(sims);
}


/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 2:40:41 AM)
 */
public void setSimulationOwner(SimulationOwner newSimulationOwner) {
	SimulationOwner oldSimulationOwner = simulationOwner;
	if (oldSimulationOwner != null) {
		oldSimulationOwner.removePropertyChangeListener(this);
	}
	if (newSimulationOwner != null) {
		newSimulationOwner.addPropertyChangeListener(this);
	}
	simulationOwner = newSimulationOwner;
	setSimulations(getSimulationOwner() == null ? null : getSimulationOwner().getSimulations());
	firePropertyChange(new java.beans.PropertyChangeEvent(this, "simulationOwner", oldSimulationOwner, newSimulationOwner));
}


/**
 * Sets the simulations property (cbit.vcell.solver.Simulation[]) value.
 * @param simulations The new value for the property.
 * @see #getSimulations
 */
public void setSimulations(final cbit.vcell.solver.Simulation[] simulations) {
	cbit.vcell.solver.Simulation[] oldValue = fieldSimulations;
	fieldSimulations = simulations;
	if (simulations == null) {
		setStatusBars(null);
	} else {
		setStatusBars(new JProgressBar[simulations.length]);
		for (int i = 0; i < getStatusBars().length; i++){
			JProgressBar bar = new JProgressBar();
			bar.setStringPainted(true);
			getStatusBars()[i] = bar;
		}
	}
	firePropertyChange("simulations", oldValue, simulations);
}


/**
 * Insert the method's description here.
 * Creation date: (6/10/2004 3:03:08 AM)
 * @param newStatusBars javax.swing.JProgressBar[]
 */
private void setStatusBars(javax.swing.JProgressBar[] newStatusBars) {
	statusBars = newStatusBars;
}


/**
 * Comment
 */
void showSimulationResults(Simulation[] sims) {
	getClientSimManager().showSimulationResults(sims);
}


/**
 * Comment
 */
void showSimulationStatusDetails(Simulation[] sims) {
	getClientSimManager().showSimulationStatusDetails(sims);
}


/**
 * Comment
 */
void stopSimulations(Simulation[] sims) {
	getClientSimManager().stopSimulations(sims);
}
}