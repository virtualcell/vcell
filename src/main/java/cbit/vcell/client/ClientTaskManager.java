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

import org.vcell.util.gui.DialogUtils;

import cbit.image.ImageException;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.bionetgen.BNGOutputSpec;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.geometry.GeometryThumbnailImageFactoryAWT;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.MathMappingCallbackTaskAdapter;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.Application;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.model.Parameter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.UniformOutputTimeSpec;

public class ClientTaskManager {
	
	public static AsynchClientTask[] newApplication(JComponent tempRequester, final BioModel bioModel, final SimulationContext.Application simContextType) {		
		
		AsynchClientTask task0 = new AsynchClientTask("create application", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			
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
				SimulationContext newSimulationContext = ClientTaskManager.copySimulationContext(simulationContext, newName, bSpatial, appType);
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
						clonedSimulation.setName(simulationContext.getBioModel().getFreeSimulationName());
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

	public static 
	SimulationContext copySimulationContext(SimulationContext srcSimContext, String newSimulationContextName, boolean bSpatial, SimulationContext.Application simContextType) 
				throws java.beans.PropertyVetoException, ExpressionException, MappingException, GeometryException, ImageException {
		Geometry newClonedGeometry = new Geometry(srcSimContext.getGeometry());
		newClonedGeometry.precomputeAll(new GeometryThumbnailImageFactoryAWT());
		//if stoch copy to ode, we need to check is stoch is using particles. If yes, should convert particles to concentraton.
		//the other 3 cases are fine. ode->ode, ode->stoch, stoch-> stoch 
		SimulationContext destSimContext = new SimulationContext(srcSimContext,newClonedGeometry, simContextType);
		if(srcSimContext.getApplicationType() == Application.NETWORK_STOCHASTIC && !srcSimContext.isUsingConcentration() && simContextType == Application.NETWORK_DETERMINISTIC)  
		{
			try {
				destSimContext.convertSpeciesIniCondition(true);
			} catch (MappingException e) {
				e.printStackTrace();
				throw new java.beans.PropertyVetoException(e.getMessage(), null);
			}
		}
		if (srcSimContext.getGeometry().getDimension() > 0 && !bSpatial) { // copy the size over
			destSimContext.setGeometry(new Geometry("nonspatial", 0));
			StructureMapping srcStructureMappings[] = srcSimContext.getGeometryContext().getStructureMappings();
			StructureMapping destStructureMappings[] = destSimContext.getGeometryContext().getStructureMappings();
			for (StructureMapping destStructureMapping : destStructureMappings) {
				for (StructureMapping srcStructureMapping : srcStructureMappings) {
					if (destStructureMapping.getStructure() == srcStructureMapping.getStructure()) {
						if (srcStructureMapping.getUnitSizeParameter() != null) {
							Expression sizeRatio = srcStructureMapping.getUnitSizeParameter().getExpression();
							GeometryClass srcGeometryClass = srcStructureMapping.getGeometryClass();
							GeometricRegion[] srcGeometricRegions = srcSimContext.getGeometry().getGeometrySurfaceDescription().getGeometricRegions(srcGeometryClass);
							if (srcGeometricRegions != null) {
								double size = 0;
								for (GeometricRegion srcGeometricRegion : srcGeometricRegions) {
									size += srcGeometricRegion.getSize();
								}
								destStructureMapping.getSizeParameter().setExpression(Expression.mult(sizeRatio, new Expression(size)));
							}
						}
						break;
					}
				}
			}
			//If changing spatial to non-spatial
			//set diffusion to 0, velocity and boundary to null
//			srcSimContext.getReactionContext().getspe
			Parameter[] allParameters = destSimContext.getAllParameters();
			if(allParameters != null && allParameters.length > 0){
				for (int i = 0; i < allParameters.length; i++) {
					if(allParameters[i] instanceof SpeciesContextSpecParameter){
						SpeciesContextSpecParameter speciesContextSpecParameter = (SpeciesContextSpecParameter)allParameters[i];
						int role = speciesContextSpecParameter.getRole();
						if(role == SpeciesContextSpec.ROLE_DiffusionRate){
							speciesContextSpecParameter.setExpression(new Expression(0));
						}else if (role == SpeciesContextSpec.ROLE_BoundaryValueXm
								|| role == SpeciesContextSpec.ROLE_BoundaryValueXp
								|| role == SpeciesContextSpec.ROLE_BoundaryValueYm
								|| role == SpeciesContextSpec.ROLE_BoundaryValueYp
								|| role == SpeciesContextSpec.ROLE_BoundaryValueZm
								|| role == SpeciesContextSpec.ROLE_BoundaryValueZp) {
							speciesContextSpecParameter.setExpression(null);
							
						} else if (role == SpeciesContextSpec.ROLE_VelocityX
								|| role == SpeciesContextSpec.ROLE_VelocityY
								|| role == SpeciesContextSpec.ROLE_VelocityZ) {
							speciesContextSpecParameter.setExpression(null);
						}
					}
				}
			}
			
		}
		destSimContext.fixFlags();
		destSimContext.setName(newSimulationContextName);	
		return destSimContext;
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
