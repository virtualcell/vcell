/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.task;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.vcell.util.BeanUtils;
import org.vcell.util.Compare;
import org.vcell.util.ISize;
import org.vcell.util.UserCancelException;
import org.vcell.util.document.VCDocument;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.ClientSimManager;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.server.JobManager;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.ParticleProperties;
import cbit.vcell.math.SubDomain;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.model.common.VCellErrorMessages;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.solver.DataProcessingInstructions;
import cbit.vcell.solver.MeshSpecification;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.SolverDescription;
/**
 * Insert the type's description here.
 * Creation date: (5/31/2004 6:03:16 PM)
 * @author: Ion Moraru
 */
public class RunSims extends AsynchClientTask {
	
	public static class SmoldynTimeStepVars {
		private double spatialRes = 0.0;
		private double maxDiffusion = 1.0;
		
		public SmoldynTimeStepVars(double argS, double argDmax) {
			this.spatialRes = argS;
			this.maxDiffusion = argDmax;
		}
		public double getSpatialResolution() {
			return spatialRes;			
		}
		public double getMaxDiffusion() {
			return maxDiffusion;		
		}
	}

	private SmoldynTimeStepVars smoldynTimestepVars = null;
	
	public RunSims() {
		super("Sending simulation start requests", TASKTYPE_NONSWING_BLOCKING);
	}

	private boolean isSmoldynTimeStepOK(Simulation sim) {
		for (int jobIndex = 0; jobIndex < sim.getScanCount(); jobIndex ++) {
			SimulationSymbolTable simSymbolTable = new SimulationSymbolTable(sim, jobIndex);
			double Dmax = 0;
			MathDescription mathDesc = sim.getMathDescription();
			
			Enumeration<SubDomain> subDomainEnumeration = mathDesc.getSubDomains();
			while (subDomainEnumeration.hasMoreElements()) {
				SubDomain subDomain = subDomainEnumeration.nextElement();
				
//				if (!(subDomain instanceof CompartmentSubDomain)) {
//					continue;
//				}
				for (ParticleProperties particleProperties : subDomain.getParticleProperties()) {
					try {
						Expression newExp = new Expression(particleProperties.getDiffusion());
						newExp.bindExpression(simSymbolTable);
						newExp = simSymbolTable.substituteFunctions(newExp).flatten();
						try {
							double diffConstant = newExp.evaluateConstant();
							Dmax = Math.max(Dmax, diffConstant);
						} catch (ExpressionException ex) {
							throw new ExpressionException("diffusion coefficient for variable " 
									+ particleProperties.getVariable().getQualifiedName() 
									+ " is not a constant. Constants are required for all diffusion coefficients");
						}
					} catch (Exception ex) {
						
					}
				}
			}
			
			double s = sim.getMeshSpecification().getDx(sim.hasCellCenteredMesh());
			double dt = sim.getSolverTaskDescription().getTimeStep().getDefaultTimeStep();
			if (dt >= s * s / (2 * Dmax)) {
				smoldynTimestepVars = new SmoldynTimeStepVars(s, Dmax);
				return false;
			}
		}
		return true;
	}	
/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(Hashtable<String, Object> hashTable) throws java.lang.Exception {
	DocumentWindowManager documentWindowManager = (DocumentWindowManager)hashTable.get(CommonTask.DOCUMENT_WINDOW_MANAGER.name);
	ClientSimManager clientSimManager = (ClientSimManager)hashTable.get("clientSimManager");
//	DocumentManager documentManager = (DocumentManager)hashTable.get(CommonTask.DOCUMENT_MANAGER.name);
	JobManager jobManager = (JobManager)hashTable.get("jobManager");
	Simulation[] simulations = (Simulation[])hashTable.get("simulations");
	Hashtable<Simulation, Throwable> failures = new Hashtable<Simulation, Throwable>();
	if (simulations != null && simulations.length > 0) {
		// we need to get the new ones if a save occurred...
		if (hashTable.containsKey(SaveDocument.DOC_KEY)) {
			VCDocument savedDocument = (VCDocument)hashTable.get(SaveDocument.DOC_KEY);
			Simulation[] allSims = null;
			if (savedDocument instanceof BioModel) {
				allSims = ((BioModel)savedDocument).getSimulations();
			} else if (savedDocument instanceof MathModel) {
				allSims = ((MathModel)savedDocument).getSimulations();
			}
			Vector<Simulation> v = new Vector<Simulation>();
			for (int i = 0; i < simulations.length; i++){
				for (int j = 0; j < allSims.length; j++){
					if(allSims[i].getSimulationOwner() instanceof SimulationContext) {
						if (simulations[i].getName().equals(allSims[j].getName()) && Compare.isEqualOrNull(allSims[i].getSimulationOwner().getName(), (simulations[i].getSimulationOwner()!=null?simulations[i].getSimulationOwner().getName():null))) {
							v.add(allSims[j]);
							break;
						}
					}else {
						if (simulations[i].getName().equals(allSims[j].getName())) {
							v.add(allSims[j]);
							break;
						}						
					}
				}
			}
			simulations = (Simulation[])BeanUtils.getArray(v, Simulation.class);
		}
		for (Simulation sim : simulations){
			try {
				int dimension = sim.getMathDescription().getGeometry().getDimension();
				if (clientSimManager.getSimulationStatus(sim).isCompleted()) { // completed
					String warningMessage =  VCellErrorMessages.getErrorMessage(VCellErrorMessages.RunSims_1, sim.getName());
					String result = DialogUtils.showWarningDialog(documentWindowManager.getComponent(), warningMessage, 
							new String[] {UserMessage.OPTION_OK, UserMessage.OPTION_CANCEL}, UserMessage.OPTION_OK);
					if (result == null || !result.equals(UserMessage.OPTION_OK)) {
						continue;
					}
				}
				DataProcessingInstructions dataProcessingInstructions = sim.getDataProcessingInstructions();
				try{
					if (dataProcessingInstructions != null) {
						dataProcessingInstructions.getSampleImageFieldData(sim.getVersion().getOwner());
					}
				}catch(Exception ex){
					DialogUtils.showErrorDialog(documentWindowManager.getComponent(), "Problem found in simulation '" + sim.getName() + "':\n" + ex.getMessage());
					continue;
				}
				if (dimension > 0) {
					MeshSpecification meshSpecification = sim.getMeshSpecification();
					boolean bCellCentered = sim.hasCellCenteredMesh();
					if (meshSpecification != null && !meshSpecification.isAspectRatioOK(bCellCentered)) {
						String warningMessage =  VCellErrorMessages.getErrorMessage(VCellErrorMessages.RunSims_2, sim.getName(),
								"\u0394x=" + meshSpecification.getDx(bCellCentered) + "\n" + "\u0394y=" + meshSpecification.getDy(bCellCentered)
								+ (dimension < 3 ? "" : "\n\u0394z=" + meshSpecification.getDz(bCellCentered)));
						String result = DialogUtils.showWarningDialog(documentWindowManager.getComponent(), warningMessage, 
								new String[] {UserMessage.OPTION_OK, UserMessage.OPTION_CANCEL}, UserMessage.OPTION_OK);
						if (result == null || !result.equals(UserMessage.OPTION_OK)) {
							continue;
						}
					}
					if (sim.getSolverTaskDescription().getSolverDescription().equals(SolverDescription.Smoldyn)) {
						if (!isSmoldynTimeStepOK(sim)) {
							double s = smoldynTimestepVars.getSpatialResolution();
							double dMax = smoldynTimestepVars.getMaxDiffusion();
							double condn = (s*s)/(2.0*dMax);
							String warningMessage =   VCellErrorMessages.getErrorMessage(VCellErrorMessages.RunSims_3, Double.toString(s),
									Double.toString(dMax), Double.toString(condn));
							DialogUtils.showErrorDialog(documentWindowManager.getComponent(), warningMessage);
							continue;
						}
					}
	
					// check the number of regions if the simulation mesh is coarser.
					Geometry mathGeometry = sim.getMathDescription().getGeometry();
					ISize newSize = meshSpecification.getSamplingSize();
					ISize defaultSize = mathGeometry.getGeometrySpec().getDefaultSampledImageSize();
					if (!sim.getSolverTaskDescription().getSolverDescription().isChomboSolver())
					{
						int defaultTotalVolumeElements = mathGeometry.getGeometrySurfaceDescription().getVolumeSampleSize().getXYZ();
						int newTotalVolumeElements = meshSpecification.getSamplingSize().getXYZ();
						if (defaultTotalVolumeElements > newTotalVolumeElements) { // coarser
							Geometry resampledGeometry = (Geometry) BeanUtils.cloneSerializable(mathGeometry);
							GeometrySurfaceDescription geoSurfaceDesc = resampledGeometry.getGeometrySurfaceDescription();
							geoSurfaceDesc.setVolumeSampleSize(newSize);
							geoSurfaceDesc.updateAll();
							
							if (mathGeometry.getGeometrySurfaceDescription().getGeometricRegions() == null) {
								mathGeometry.getGeometrySurfaceDescription().updateAll();
							}
							int defaultNumGeometricRegions = mathGeometry.getGeometrySurfaceDescription().getGeometricRegions().length;
							int numGeometricRegions = geoSurfaceDesc.getGeometricRegions().length;
							if (numGeometricRegions != defaultNumGeometricRegions) {
								String warningMessage =  VCellErrorMessages.getErrorMessage(VCellErrorMessages.RunSims_4, 
										newSize.getX() + (dimension > 1 ? " x " + newSize.getY() : "") + (dimension > 2 ? " x " + newSize.getZ() : ""), 
										sim.getName(), numGeometricRegions, defaultNumGeometricRegions);
								String result = PopupGenerator.showWarningDialog(documentWindowManager.getComponent(), warningMessage, 
										new String[] {UserMessage.OPTION_OK, UserMessage.OPTION_CANCEL}, UserMessage.OPTION_OK);
								if (result == null || !result.equals(UserMessage.OPTION_OK)) {
									continue;
								}
							}
						} 
					
						if (mathGeometry.getGeometrySpec().hasImage()) { // if it's an image.
							if (defaultSize.getX() + 1 < newSize.getX() 
									|| defaultSize.getY() + 1 < newSize.getY()
									|| defaultSize.getZ() + 1 < newSize.getZ()) { // finer
								String defaultSizeString = (defaultSize.getX() + 1) + (dimension > 1 ? " x " + (defaultSize.getY() + 1) : "") + (dimension > 2 ? " x " + (defaultSize.getZ() + 1): "");
								String warningMessage =   VCellErrorMessages.getErrorMessage(VCellErrorMessages.RunSims_5,
										newSize.getX() + (dimension > 1 ? " x " + newSize.getY() : "") + (dimension > 2 ? " x " + newSize.getZ() : ""), sim.getName(), 
										defaultSizeString, defaultSizeString);
								String result = DialogUtils.showWarningDialog(documentWindowManager.getComponent(), warningMessage, 
										new String[] {UserMessage.OPTION_OK, UserMessage.OPTION_CANCEL}, UserMessage.OPTION_OK);
								if (result == null || !result.equals(UserMessage.OPTION_OK)) {
									continue;
								}
							}
						}
					}
					
					boolean bGiveWarning = false;
					for (int i = 0; i < sim.getScanCount(); i ++) {
						if (sim.getSolverTaskDescription().getSolverDescription().equals(SolverDescription.SundialsPDE)) {
							SimulationJob simJob = new SimulationJob(sim, i, null);						
							if (simJob.getSimulationSymbolTable().hasTimeVaryingDiffusionOrAdvection()) {
								bGiveWarning = true;
								break;
							}
						}
					}
					if (bGiveWarning) {
						String warningMessage = VCellErrorMessages.RunSims_6;
						String result = DialogUtils.showWarningDialog(documentWindowManager.getComponent(), warningMessage, 
								new String[] {UserMessage.OPTION_OK, UserMessage.OPTION_CANCEL}, UserMessage.OPTION_OK);
						if (result == null || !result.equals(UserMessage.OPTION_OK)) {
							continue;
						}
					}
				}
				SimulationInfo simInfo = sim.getSimulationInfo();
				if (simInfo != null) {
					//
					// translate to common ancestral simulation (oldest mathematically equivalent simulation)
					//
					SimulationStatus simulationStatus = jobManager.startSimulation(simInfo.getAuthoritativeVCSimulationIdentifier(),sim.getScanCount());
					// updateStatus
					clientSimManager.updateStatusFromStartRequest(sim, simulationStatus);
				} else {
					// this should really not happen...
					throw new RuntimeException(">>>>>>>>>> trying to run an unsaved simulation...");
				}	
			} catch (Throwable exc) {
				exc.printStackTrace(System.out);
				failures.put(sim, exc);
			}
		}
	}
	// we actually have an array of requests and more than one can fail
	// we deal with individual request failures here, passing down only other things (that break the whole thing down) to dispatcher
	if (! failures.isEmpty()) {
		Enumeration<Simulation> en = failures.keys();
		while (en.hasMoreElements()) {
			Simulation sim = en.nextElement();
			Throwable exc = (Throwable)failures.get(sim);
//			// updateStatus
//			SimulationStatus simulationStatus = clientSimManager.updateStatusFromStartRequest(sim, true, exc.getMessage());
			// notify user
			PopupGenerator.showErrorDialog(documentWindowManager, "Failed to start simulation'"+sim.getName()+"'\n"+exc.getMessage());
		}
	}
}

/**
 * Insert the method's description here.
 * Creation date: (6/8/2004 4:40:01 PM)
 * @return boolean
 */
public boolean skipIfCancel(UserCancelException exc) {
	if (exc == UserCancelException.CANCEL_DELETE_OLD) {
		return false;
	} else {
		return true;
	}
}
}
