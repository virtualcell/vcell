/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.simulation;

import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.swing.JScrollPane;

import org.vcell.chombo.ChomboMeshValidator;
import org.vcell.chombo.ChomboMeshValidator.ChomboMeshRecommendation;
import org.vcell.util.PropertyChangeListenerProxyVCell;
import org.vcell.util.document.PropertyConstants;
import org.vcell.util.document.User;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.ClientSimManager;
import cbit.vcell.client.ClientSimManager.ViewerType;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.geometry.ChomboInvalidGeometryException;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.math.Function;
import cbit.vcell.math.JumpProcess;
import cbit.vcell.math.MathException;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.VarIniCondition;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VolVariable;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.MeshSpecification;
import cbit.vcell.solver.OutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationOwner;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.UniformOutputTimeSpec;
import edu.uchc.connjur.spectrumtranslator.CodeUtil;

public class SimulationWorkspace implements java.beans.PropertyChangeListener {
	public static final String PROPERTY_NAME_SIMULATION_STATUS = "status";
	private static final long MEGA_TO_BYTES =  1048576; //number of bytes in a megabyte
	private SimulationOwner simulationOwner = null;
	private ClientSimManager clientSimManager = null;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private Simulation[] fieldSimulations = null;

/**
 * Insert the method's description here.
 * Creation date: (5/11/2004 2:26:44 PM)
 * @param workspace cbit.vcell.desktop.controls.Workspace
 */
public SimulationWorkspace(DocumentWindowManager documentWindowManager, SimulationOwner simulationOwner) {
	setSimulationOwner(simulationOwner);
	this.clientSimManager = new ClientSimManager(documentWindowManager, this);
}

public User getLoggedInUser() {
	return getClientSimManager().getLoggedInUser();
}
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	PropertyChangeListenerProxyVCell.addProxyListener(getPropertyChange(), listener);
}


/**
 * Insert the method's description here.
 * Creation date: (5/11/2004 3:52:25 PM)
 */
private static String applyChanges(Simulation clonedSimulation, Simulation simulation) {
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
public static String checkCompatibility(SimulationOwner simOwner, Simulation simulation) {
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
private static boolean checkSimulationParameters(Simulation simulation, Component parent,boolean bCheckLimits) {
	SimulationSymbolTable simSymbolTable = new SimulationSymbolTable(simulation, 0);
	
	String errorMessage = null;
	long maxTimepoints = Simulation.MAX_LIMIT_NON_SPATIAL_TIMEPOINTS;
	long warningTimepoints = Simulation.WARNING_NON_SPATIAL_TIMEPOINTS;
	boolean bSpatial = simulation.isSpatial();
	if(bSpatial)
	{
		maxTimepoints = Simulation.MAX_LIMIT_SPATIAL_TIMEPOINTS;
		warningTimepoints = Simulation.WARNING_SPATIAL_TIMEPOINTS;
	}
		
	long maxSizeBytes = megabytesToBytes( Simulation.MAX_LIMIT_0DE_MEGABYTES );
	long warningSizeBytes = megabytesToBytes( Simulation.WARNING_0DE_MEGABYTES );
	if(bSpatial)
	{
		maxSizeBytes  = megabytesToBytes(  Simulation.MAX_LIMIT_PDE_MEGABYTES );
		warningSizeBytes = megabytesToBytes( Simulation.WARNING_PDE_MEGABYTES );
	}
	else if (simulation.getMathDescription().isNonSpatialStoch())
	{
		maxSizeBytes = megabytesToBytes( Simulation.MAX_LIMIT_STOCH_MEGABYTES );
		warningSizeBytes = megabytesToBytes( Simulation.WARNING_STOCH_MEGABYTES );
	}
	
	long expectedNumTimePoints = getExpectedNumTimePoints(simulation);
	
	long expectedSizeBytes = getExpectedSizeBytes(simSymbolTable);
	//
	// check for error conditions (hard limits on resources) ... Note: each user should have it's own limits (and quotas).
	//
	SolverTaskDescription solverTaskDescription = simulation.getSolverTaskDescription();
	SolverDescription solverDescription = solverTaskDescription.getSolverDescription();
	if (bCheckLimits && expectedNumTimePoints>maxTimepoints){
		errorMessage =  "Errors in Simulation: '" + simulation.getName() + "'!\n" +
				        "The simulation has too many timepoints ("+expectedNumTimePoints+") to be saved, which has exceeded our limit.\n\n"+
						"maximum saving timepoints limits are:\n" + 
						"     "+Simulation.MAX_LIMIT_NON_SPATIAL_TIMEPOINTS + " for compartmental simulations\n" + 
						"     "+Simulation.MAX_LIMIT_SPATIAL_TIMEPOINTS + " for spatial simulations\n"+
						"suggested saving timepoints limits are:\n" + 
						"     "+Simulation.WARNING_NON_SPATIAL_TIMEPOINTS + " for compartmental simulations\n" + 
						"     "+Simulation.WARNING_SPATIAL_TIMEPOINTS + " for spatial simulations\n"+
						"Try saving fewer timepoints\n"+
						"If you need to exceed the quota, please contact us";
		//not used for multiple stochastic run
		if(solverTaskDescription.getStochOpt()!= null && solverTaskDescription.getStochOpt().getNumOfTrials()>1)
		{
			errorMessage = null;
		}
	} else if (bCheckLimits && expectedSizeBytes>maxSizeBytes){
		errorMessage =  "Errors in Simulation: '" + simulation.getName() + "'!\n" +
				        "The simulation's result dataset ("+ CodeUtil.humanBytePrint(expectedSizeBytes) + ") is too large, which has exceeded our limit.\n\n"+
						"maximum size limits are:\n" + 
						"     "+Simulation.MAX_LIMIT_0DE_MEGABYTES + " MB for compartmental ODE simulations\n" + 
						"     "+Simulation.MAX_LIMIT_PDE_MEGABYTES + " MB for spatial simulations\n"+
						"     "+Simulation.MAX_LIMIT_STOCH_MEGABYTES + " MB for compartmental stochastic simulations\n"+
						"suggested size limits are:\n" + 
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
	} else if (bCheckLimits && simulation.getScanCount() > Simulation.MAX_LIMIT_SCAN_JOBS) {
		errorMessage =  "Errors in Simulation: '" + simulation.getName() + "'!\n" +
				        "The simulation generates too many simulations (" + simulation.getScanCount() + ") required for parameter scan, which has exceeded our limit.\n\n" +
						"maximum number of parameter sets is: " + Simulation.MAX_LIMIT_SCAN_JOBS + " \n" + 
						"suggested limit for number of parameter sets is: " + Simulation.WARNING_SCAN_JOBS + " \n" + 
						"Try choosing fewer parameters or reducing the size of scan for each parameter.";
		//not used for multiple stochastic run
		if(simulation.getMathDescription().isNonSpatialStoch() && 
		   solverTaskDescription.getStochOpt()!= null && solverTaskDescription.getStochOpt().getNumOfTrials()>1)
		{
			errorMessage = null;
		}
	}	
	else if (solverDescription.equals(SolverDescription.SundialsPDE)) {			
		if (solverTaskDescription.getOutputTimeSpec().isDefault()) {
			DefaultOutputTimeSpec dot = (DefaultOutputTimeSpec)solverTaskDescription.getOutputTimeSpec();
			int maxNumberOfSteps = dot.getKeepEvery() * dot.getKeepAtMost();
			double maximumTimeStep = solverTaskDescription.getTimeStep().getMaximumTimeStep();
			double maxSimTime = maxNumberOfSteps * maximumTimeStep;
			double endingTime = solverTaskDescription.getTimeBounds().getEndingTime();
			if (maxSimTime < endingTime) {
				errorMessage = "Errors in Simulation: '" + simulation.getName() + "'!\n" + 
				      "The maximum possible simulation time (keepEvery * maxTimestep * keepAtMost = " + maxSimTime 
					+ ") is less than simulation end time (" + endingTime + ").\n\n"
					+ "You have chosen a variable time step solver and specified a maximum number of time steps of "+maxNumberOfSteps+" (keepEvery*keepAtMost).  "
					+ "Actual time steps are often small, but even if all steps were at the maximum time step of "+maximumTimeStep+", the simulation end time of "+endingTime+" would not be reached. \n\n"
					+ "Either adjust the parameters or choose the \"Output Interval\" option.";				
			}
		}
	} else if(simulation.getMathDescription().isNonSpatialStoch() && !(solverDescription.isNonSpatialStochasticSolver())) {
		//to guarantee stochastic model uses stochastic methods and deterministic model uses ODE/PDE methods.
		errorMessage = "Errors in Simulation: '" + simulation.getName() + "'!\n" + 
				    "Stochastic simulation(s) must use stochastic solver(s).\n" +
		            solverDescription.getDisplayLabel()+" is not a stochastic solver!";
	} else if(!simulation.getMathDescription().isNonSpatialStoch() && (solverDescription.isNonSpatialStochasticSolver())) {
		errorMessage = "Errors in Simulation: '" + simulation.getName() + "'!\n" +
				    "ODE/PDE simulation(s) must use ODE/PDE solver(s).\n" + 
					solverDescription.getDisplayLabel()+" is not a ODE/PDE solver!";
	}	else if (simulation.getSolverTaskDescription().getSolverDescription().isChomboSolver()) {
		MeshSpecification meshSpecification = simulation.getMeshSpecification();
		boolean bCellCentered = simulation.hasCellCenteredMesh();
		if (meshSpecification != null && !meshSpecification.isAspectRatioOK(1e-4, bCellCentered)) {
			errorMessage =  "Non uniform spatial step is detected. This will affect the accuracy of the solution.\n\n"
				+ "\u0394x=" + meshSpecification.getDx(bCellCentered) + "\n" 
				+ "\u0394y=" + meshSpecification.getDy(bCellCentered)
				+ (meshSpecification.getGeometry().getDimension() < 3 ? "" : "\n\u0394z=" + meshSpecification.getDz(bCellCentered));
		}		
	} else {		
		errorMessage = null;
	}
	if (errorMessage != null) {
		DialogUtils.showErrorDialog(parent, errorMessage);
		return false;
	} 
	else if (simulation.getSolverTaskDescription().getSolverDescription().isChomboSolver())
	{
		Geometry geometry = simulation.getMathDescription().getGeometry();
		ChomboMeshValidator meshValidator = new ChomboMeshValidator(geometry, simulation.getSolverTaskDescription().getChomboSolverSpec());
		ChomboMeshRecommendation chomboMeshRecommendation = meshValidator.computeMeshSpecs();
		boolean bValid = chomboMeshRecommendation.validate();
		if (!bValid)
		{
			String option = DialogUtils.showWarningDialog(parent, "Error", chomboMeshRecommendation.getErrorMessage(), chomboMeshRecommendation.getDialogOptions(), ChomboMeshRecommendation.optionClose);
			if (ChomboMeshRecommendation.optionSuggestions.equals(option))
			{
				DialogUtils.showInfoDialog(parent, ChomboMeshRecommendation.optionSuggestions, chomboMeshRecommendation.getMeshSuggestions());
			}
		}
		return bValid;
	} 
	else{
		String warningMessage = null;
		//don't check warning message for stochastic multiple trials, let it run.
		if(simulation.getMathDescription().isNonSpatialStoch() && simulation.getSolverTaskDescription().getStochOpt()!=null &&
				   simulation.getSolverTaskDescription().getStochOpt().getNumOfTrials()>1)
		{
			return true;
		}
		//
		// no error conditions, check for warning conditions (suggested limits on resources)
		//
		if (expectedNumTimePoints>warningTimepoints){
			warningMessage = "Warnings from Simulation: '" + simulation.getName() + "'!\n" + 
					        "The simulation has large number of saving timepoints ("+expectedNumTimePoints+"), suggested saving timepoints limits are:\n" + 
							"     "+Simulation.WARNING_NON_SPATIAL_TIMEPOINTS + " for compartmental simulations\n" + 
							"     "+Simulation.WARNING_SPATIAL_TIMEPOINTS + " for spatial simulations\n" +
							"Try saving fewer timepoints";
		} else if (expectedSizeBytes>warningSizeBytes){
			warningMessage = "Warnings from Simulation: '" + simulation.getName() + "'!\n" +
					        "The simulation has large result dataset ("+(expectedSizeBytes/1000000L)+"MB), suggested size limits are:\n" + 
							"     "+Simulation.WARNING_0DE_MEGABYTES + " MB for compartmental ODE simulations\n" + 
							"     "+Simulation.WARNING_PDE_MEGABYTES + " MB for spatial simulations\n" +
							"     "+Simulation.WARNING_STOCH_MEGABYTES + " MB for compartmental stochastic simulations\n" +
							"Try saving fewer timepoints or using a coarser mesh if spatial.";
		} else if (simulation.getScanCount() > Simulation.WARNING_SCAN_JOBS) {
			warningMessage = "Warnings from Simulation: '" + simulation.getName() + "'!\n" + 
					    "The simulation generates a large number of simulations (" + simulation.getScanCount() + ") required for parameter scan.\n" +
						"maximum number of parameter sets is: " + Simulation.MAX_LIMIT_SCAN_JOBS + " \n" + 
						"suggested limit for the number of parameter sets is: " + Simulation.WARNING_SCAN_JOBS + " \n" + 
						"Try choosing fewer parameters or reducing the size of scan for each parameter.";
		} 
		
		if (solverDescription.equals(SolverDescription.SundialsPDE)) {
			if (solverTaskDescription.getErrorTolerance().getRelativeErrorTolerance() > 1e-4) {
				String msg = "Warnings from Simulation: '" + simulation.getName() + "'!\n" + 
					  "Warning: it is not reccomended to use a relative tolerance that is greater than \n1e-4 for " 
					+ solverDescription.getDisplayLabel() + ".";
				warningMessage = warningMessage == null? msg : warningMessage + "\n\n" + msg;
			}
		} else if (solverDescription.isSemiImplicitPdeSolver()) {
			if (solverTaskDescription.getErrorTolerance().getRelativeErrorTolerance() > 1e-8) {
				String msg = "Warnings from Simulation: '" + simulation.getName() + "'!\n" + 
					  "Warning: it is not reccomended to use a relative tolerance that is greater than \n1e-8 for " 
					+ solverDescription.getDisplayLabel() + ".";
				warningMessage = warningMessage == null? msg : warningMessage + "\n\n" + msg;
			}
		}
		
		MeshSpecification meshSpecification = simulation.getMeshSpecification();
		boolean bCellCentered = simulation.hasCellCenteredMesh();
		if (meshSpecification != null && !meshSpecification.isAspectRatioOK(bCellCentered)) {
			warningMessage =  (warningMessage == null? "" : warningMessage + "\n\n") 
				+ "Non uniform spatial step is detected. This might affect the accuracy of the solution.\n\n"
				+ "\u0394x=" + meshSpecification.getDx(bCellCentered) + "\n" 
				+ "\u0394y=" + meshSpecification.getDy(bCellCentered)
				+ (meshSpecification.getGeometry().getDimension() < 3 ? "" : "\n\u0394z=" + meshSpecification.getDz(bCellCentered));
		}		
		if (warningMessage != null)
		{
			String result = DialogUtils.showWarningDialog(parent, warningMessage + "\n\nDo you want to continue anyway?", 
					new String[] {UserMessage.OPTION_OK, UserMessage.OPTION_CANCEL}, UserMessage.OPTION_OK);
			return (result != null && result.equals(UserMessage.OPTION_OK));
		} else {
			return true;
		}
	}
}


/**
 * Comment
 */
int createBatchSimulations(Simulation[] sims, Map<Integer, Map<String, String>> batchInputDataMap, Component requester) throws java.beans.PropertyVetoException {
	if (sims == null || sims.length == 0) {
		return -1;
	}
	for (int i = 0; i < sims.length; i++){
		String errorMessage = checkCompatibility(simulationOwner, sims[i]);
		if(errorMessage != null){
			PopupGenerator.showErrorDialog(requester, errorMessage+"\nUpdate Math before copying simulations");
			return -1;
		}
	}
	Simulation copiedSim = null;
	for (int i = 0; i < sims.length; i++) {
		copiedSim = getSimulationOwner().createBatchSimulations(sims[i], batchInputDataMap);
	}
	return -1;
}
int getBatchSimulationsResults(Simulation[] sims, Component requester) throws java.beans.PropertyVetoException {
	if (sims == null || sims.length == 0) {
		return -1;
	}

	// sims contains exactly one template simulation
	ArrayList<AnnotatedFunction> outputFunctionsList = getSimulationOwner().getOutputFunctionContext().getOutputFunctionsList();
	OutputContext outputContext = new OutputContext(outputFunctionsList.toArray(new AnnotatedFunction[outputFunctionsList.size()]));
	getClientSimManager().getBatchSimulationsResults(outputContext, sims[0]);
//	getSimulationOwner().importBatchSimulations(sims[0]);	// was in simContext initially
	return -1;
}

int copySimulations(Simulation[] sims, Component requester) throws java.beans.PropertyVetoException {
	if (sims == null || sims.length == 0) {
		return -1;
	}
	for (int i = 0; i < sims.length; i++){
		String errorMessage = checkCompatibility(simulationOwner, sims[i]);
		if(errorMessage != null){
			PopupGenerator.showErrorDialog(requester, errorMessage+"\nUpdate Math before copying simulations");
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
public static void editSimulation(Component parent, SimulationOwner simOwner, Simulation simulation) {
	
	String errorMessage = checkCompatibility(simOwner, simulation);
	if(errorMessage != null){
		PopupGenerator.showErrorDialog(parent, errorMessage+"\nUpdate Math before editing");
		return;
	}
	
	try{
		SimulationEditor simEditor = new SimulationEditor();
		simEditor.prepareToEdit(simulation, parent);
		
		JScrollPane scrollPane = new JScrollPane(simEditor);
		Dimension panesize = simEditor.getPreferredSize();
		scrollPane.setPreferredSize(new Dimension(panesize.width + 20, panesize.height + 20));
		
		boolean acceptable = false;
		String errors = null;
		do {
			int ok = PopupGenerator.showComponentOKCancelDialog(parent, scrollPane,"Edit: " + simulation.getName());
			if (ok != javax.swing.JOptionPane.OK_OPTION) {
				return; // user cancels, we discard
			} else {
				acceptable = checkSimulationParameters(simEditor.getClonedSimulation(), parent,false);
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
	} catch (ChomboInvalidGeometryException e) {
		String option = DialogUtils.showWarningDialog(parent, "Warning", e.getRecommendation().getErrorMessage(),
				e.getRecommendation().getDialogOptions(), ChomboMeshRecommendation.optionClose);
		if (ChomboMeshRecommendation.optionSuggestions.equals(option)) {
			DialogUtils.showInfoDialog(parent, ChomboMeshRecommendation.optionSuggestions, e.getRecommendation().getMeshSuggestions());
		}
	}catch(Exception e){
		DialogUtils.showErrorDialog(parent, e.getMessage(), e);
	}
}

/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * Insert the method's description here.
 * Creation date: (6/7/2004 12:41:27 PM)
 * @return cbit.vcell.client.ClientSimManager
 */
public ClientSimManager getClientSimManager() {
	return clientSimManager;
}


/**
 * Insert the method's description here.
 * Creation date: (6/12/2001 10:09:25 AM)
 * @return boolean
 * @param simulation cbit.vcell.solver.Simulation
 */
private static long getExpectedNumTimePoints(Simulation simulation) {
	return simulation.getSolverTaskDescription().getExpectedNumTimePoints();
}

private static long getEstimatedNumTimePointsForStoch(SimulationSymbolTable simSymbolTable)
{
	Simulation sim = simSymbolTable.getSimulation();
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
	List<VarIniCondition> varInis = subDomain.getVarIniConditions();

	//get all the probability expressions
	ArrayList<Expression> probList = new ArrayList<Expression>();
	for (JumpProcess jp : subDomain.getJumpProcesses()){
		probList.add(jp.getProbabilityRate());
	}
		
	//loop through probability expressions
	for(int i=0; i<probList.size(); i++)
	{
		try {
			Expression pExp = new Expression(probList.get(i));
			pExp.bindExpression(simSymbolTable);
			pExp = simSymbolTable.substituteFunctions(pExp);
			pExp = pExp.flatten();
			String[] symbols = pExp.getSymbols();
			//substitute stoch vars with it's initial condition expressions
			if(symbols != null)
			{
				for(int j=0; symbols != null && j<symbols.length; j++)
				{
					for(int k = 0; k < varInis.size(); k++)
					{
						if(symbols[j].equals(varInis.get(k).getVar().getName()))
						{
							pExp.substituteInPlace(new Expression(symbols[j]), new Expression(varInis.get(k).getIniVal()));
							break;
						}
					}
				}
			}
			pExp = simSymbolTable.substituteFunctions(pExp);
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
private static long getExpectedSizeBytes(SimulationSymbolTable simSymbolTable) {
	Simulation simulation = simSymbolTable.getSimulation();
	
	long numTimepoints;
	if(simulation.getMathDescription().isNonSpatialStoch())
	{
		numTimepoints = getEstimatedNumTimePointsForStoch(simSymbolTable); 
	}
	else
	{
		numTimepoints = getExpectedNumTimePoints(simulation); 
	}
	int x,y,z;
	int numVariables = 0;
	if (simulation.isSpatial()) {
		x = simulation.getMeshSpecification().getSamplingSize().getX();
		y = simulation.getMeshSpecification().getSamplingSize().getY();
		z = simulation.getMeshSpecification().getSamplingSize().getZ();
		//
		// compute number of volume variables only (they are multiplied by x*y*z)
		//
		numVariables = 0;
		Enumeration<Variable> variables = simulation.getMathDescription().getVariables();
		while (variables.hasMoreElements()){
			Variable var = variables.nextElement(); 
			if (var instanceof VolVariable){
				numVariables++;
			}
		}
	}else{
		x = 1;
		y = 1;
		z = 1;		
		numVariables = 0;
		Enumeration<Variable> variables = simulation.getMathDescription().getVariables();
		while (variables.hasMoreElements()){
			Variable var = variables.nextElement(); 
			if ((var instanceof VolVariable) ||
				(var instanceof Function)) {
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
public SimulationOwner getSimulationOwner() {
	return simulationOwner;
}


/**
 * Gets the simulations property (cbit.vcell.solver.Simulation[]) value.
 * @return The simulations property value.
 * @see #setSimulations
 */
public Simulation[] getSimulations() {
	return fieldSimulations;
}


/**
 * Gets the simulations index property (cbit.vcell.solver.Simulation) value.
 * @return The simulations property value.
 * @param index The index value into the property array.
 * @see #setSimulations
 */
public Simulation getSimulations(int index) {
	return getSimulations()[index];
}


/**
 * Comment
 */
SimulationStatus getSimulationStatus(Simulation simulation) {
	return getClientSimManager().getSimulationStatus(simulation);
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
int newSimulation(Component requester,MathMappingCallback callback, NetworkGenerationRequirements networkGenerationRequirements) throws java.beans.PropertyVetoException {
	
	Simulation newSim = null;
	if (simulationOwner instanceof SimulationContext){
		newSim = ((SimulationContext)getSimulationOwner()).addNewSimulation(SimulationOwner.DEFAULT_SIM_NAME_PREFIX,callback,networkGenerationRequirements);
	}else{
		newSim = ((MathModel)getSimulationOwner()).addNewSimulation(SimulationOwner.DEFAULT_SIM_NAME_PREFIX);
	}
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
	if (evt.getSource() == getSimulationOwner() && evt.getPropertyName().equals(PropertyConstants.PROPERTY_NAME_SIMULATIONS)) {
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
void runSimulations(Simulation[] sims, Component parent) {
	boolean bOkToRun = true;
	for(Simulation sim : sims)
	{
		//check if every sim in the sim list is ok to run
		if(!checkSimulationParameters(sim, parent,true)){
			bOkToRun = false;
			break;
		}
	}
	if(bOkToRun){
		getClientSimManager().runSimulations(sims);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 2:40:41 AM)
 */
public void setSimulationOwner(SimulationOwner newSimulationOwner) {
	if (newSimulationOwner == simulationOwner) {
		return;
	}
	SimulationOwner oldSimulationOwner = simulationOwner;
	if (oldSimulationOwner != null) {
		oldSimulationOwner.removePropertyChangeListener(this);
	}
	if (newSimulationOwner != null) {
		newSimulationOwner.addPropertyChangeListener(this);
	}
	simulationOwner = newSimulationOwner;
	setSimulations(simulationOwner == null ? null : getSimulationOwner().getSimulations());
	firePropertyChange(PropertyConstants.PROPERTY_NAME_SIMULATION_OWNER, oldSimulationOwner, newSimulationOwner);
}


/**
 * Sets the simulations property (cbit.vcell.solver.Simulation[]) value.
 * @param simulations The new value for the property.
 * @see #getSimulations
 */
public void setSimulations(final Simulation[] simulations) {
	Simulation[] oldValue = fieldSimulations;
	fieldSimulations = simulations;
	firePropertyChange(PropertyConstants.PROPERTY_NAME_SIMULATIONS, oldValue, simulations);
}

/**
 * Comment
 */
void showSimulationResults(Simulation[] sims, ViewerType viewerType) {
	ArrayList<AnnotatedFunction> outputFunctionsList = getSimulationOwner().getOutputFunctionContext().getOutputFunctionsList();
	OutputContext outputContext = new OutputContext(outputFunctionsList.toArray(new AnnotatedFunction[outputFunctionsList.size()]));
	getClientSimManager().showSimulationResults(outputContext,sims,viewerType);
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
/**
 * convert megabytes to bytes
 * @param megabytes
 * @return input * {@value #MEGA_TO_BYTES}
 */
private static long megabytesToBytes(long megabytes) {
	return megabytes * MEGA_TO_BYTES;
}
}
