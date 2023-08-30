/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JComponent;

import org.vcell.util.TokenMangler;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.bionetgen.BNGOutputSpec;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.geometry.GeometryThumbnailImageFactoryAWT;
import cbit.vcell.mapping.MathMappingCallbackTaskAdapter;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.UniformOutputTimeSpec;

public class ClientTaskManager {
	
	public static AsynchClientTask[] newApplication(JComponent tempRequester, final BioModel bioModel, final SimulationContext.Application simContextType) {		
		
		AsynchClientTask task0 = new AsynchClientTask("create application", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				String newApplicationName = bioModel.getFreeSimulationContextName();
				SimulationContext newSimulationContext = bioModel.addNewSimulationContext(newApplicationName, simContextType);
				hashTable.put("newSimulationContext", newSimulationContext);
			}
		};
		AsynchClientTask task1 = new AsynchClientTask("process geometry", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				SimulationContext newSimulationContext = (SimulationContext)hashTable.get("newSimulationContext");
				newSimulationContext.getGeometry().precomputeAll(new GeometryThumbnailImageFactoryAWT());
			}
		};
		return new AsynchClientTask[] {task0, task1};
	}

	public static AsynchClientTask[] copyApplication(final JComponent requester, final BioModel bioModel, final SimulationContext simulationContext, final boolean bSpatial, final SimulationContext.Application appType) {	
		//get valid application name
		String newApplicationName = null;
		String baseName = "Copy of " + simulationContext.getName();
		int count = 0;
		while (true) {
			if (count == 0) {
				newApplicationName = baseName;
			} else {
				newApplicationName = baseName + " " + count;
			}
			if (bioModel.getSimulationContext(newApplicationName) == null) {
				break;
			}
			count ++;
		}
		
		final String newName = newApplicationName;
		AsynchClientTask task1 = new AsynchClientTask("preparing to copy", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				SimulationContext newSimulationContext = SimulationContext.copySimulationContext(simulationContext, newName, bSpatial, appType);
				newSimulationContext.getGeometry().precomputeAll(new GeometryThumbnailImageFactoryAWT());
				if (newSimulationContext.isSameTypeAs(simulationContext)) { 
					MathMappingCallback callback = new MathMappingCallbackTaskAdapter(getClientTaskStatusSupport());
					String oldHash = simulationContext.getMd5hash();
					BNGOutputSpec oldSpec = simulationContext.getMostRecentlyCreatedOutputSpec();
					if(oldHash != null && oldSpec != null) {
						// Warning: the results from "Edit / Test Constraints" are never cached because we want to repeatedly run it 
						// even if nothing changed
						newSimulationContext.setMd5hash(oldHash);
						newSimulationContext.setMostRecentlyCreatedOutputSpec(oldSpec);
						newSimulationContext.setInsufficientIterations(simulationContext.isInsufficientIterations());
						newSimulationContext.setInsufficientMaxMolecules(simulationContext.isInsufficientMaxMolecules());
					}
					newSimulationContext.refreshMathDescription(callback,NetworkGenerationRequirements.ComputeFullStandardTimeout);
				}
				hashTable.put("newSimulationContext", newSimulationContext);
			}
		};
		AsynchClientTask task2 = new AsynchClientTask("copying application and simulations", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				SimulationContext newSimulationContext = (SimulationContext)hashTable.get("newSimulationContext");
				bioModel.addSimulationContext(newSimulationContext);
				if (newSimulationContext.isSameTypeAs(simulationContext)) {
					// copy simulations to new simContext
					for (Simulation sim : simulationContext.getSimulations()) {
						Simulation clonedSimulation = new Simulation(sim, false);
						clonedSimulation.setMathDescription(newSimulationContext.getMathDescription());
						String newName = sim.getName()+"_";
						boolean bFound = false;
						do {
							bFound = false;
							newName = TokenMangler.getNextEnumeratedToken(newName);
							Simulation[] origSimulations = simulationContext.getBioModel().getSimulations();
							for (int i = 0; i < origSimulations.length; i++) {
								if(origSimulations[i].getName().equals(newName)) {
									bFound = true;
									break;
								}
							}
						}while(bFound);
						clonedSimulation.setName(newName/*simulationContext.getBioModel().getFreeSimulationName()*/);
						newSimulationContext.addSimulation(clonedSimulation);
					}
					// copy output functions to new simContext
					ArrayList<AnnotatedFunction> outputFunctions = simulationContext.getOutputFunctionContext().getOutputFunctionsList(); 
					ArrayList<AnnotatedFunction> newOutputFunctions = new ArrayList<AnnotatedFunction>();
					for (AnnotatedFunction afn : outputFunctions) {
						newOutputFunctions.add(new AnnotatedFunction(afn));
					}
					newSimulationContext.getOutputFunctionContext().setOutputFunctions(newOutputFunctions);
				} else {
					if (simulationContext.getSimulations().length > 0) {
						DialogUtils.showWarningDialog(requester, "Simulations are not copied because new application is of different type.");
					}
				}
			}
		};
		return new AsynchClientTask[] { task1, task2};			
	}

	public static void changeEndTime(JComponent requester, SolverTaskDescription solverTaskDescription, double newEndTime) throws PropertyVetoException {
		TimeBounds oldTimeBounds = solverTaskDescription.getTimeBounds();
		TimeBounds timeBounds = new TimeBounds(oldTimeBounds.getStartingTime(), newEndTime);
		solverTaskDescription.setTimeBounds(timeBounds);
		
		if (solverTaskDescription.getOutputTimeSpec() instanceof UniformOutputTimeSpec) {
			UniformOutputTimeSpec uniformOutputTimeSpec = (UniformOutputTimeSpec)solverTaskDescription.getOutputTimeSpec();
			if (timeBounds.getEndingTime() < uniformOutputTimeSpec.getOutputTimeStep()) {
				double outputTime = solverTaskDescription.getTimeBounds().getEndingTime()/20.0;
				String ret = PopupGenerator.showWarningDialog(requester, "Output Interval", 
						"Output interval(" + uniformOutputTimeSpec.getOutputTimeStep() + "s) is greater than end time(" + timeBounds.getEndingTime() + "s) which will not output any results. Do you want to change " +
						"output interval to every " + outputTime + "s (20 time points)?\n\nIf not, output interval will change to " + timeBounds.getEndingTime() + "s(the end time).",
						new String[]{ UserMessage.OPTION_YES, UserMessage.OPTION_NO}, UserMessage.OPTION_YES);
				if (ret.equals(UserMessage.OPTION_YES)) {
					solverTaskDescription.setOutputTimeSpec(new UniformOutputTimeSpec(outputTime));
				} else {
					solverTaskDescription.setOutputTimeSpec(new UniformOutputTimeSpec(timeBounds.getEndingTime()));
				}
			}
		}
	}
}
