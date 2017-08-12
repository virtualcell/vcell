/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.server;
import java.io.Serializable;
import java.math.BigDecimal;

import org.vcell.util.ComparableObject;

import cbit.vcell.solver.SimulationMetadata;

/**
 * Insert the type's description here.
 * Creation date: (9/3/2003 10:39:26 AM)
 * @author: Fei Gao
 */
public class SimpleJobStatus implements ComparableObject, Serializable {
	public final SimulationMetadata simulationMetadata;
	public final SimulationDocumentLink simulationDocumentLink;
	public final SimulationJobStatus jobStatus;
	public final StateInfo stateInfo;
	

/**
 * SimpleJobStatus constructor comment.
 */
public SimpleJobStatus(SimulationMetadata simulationMetadata, SimulationDocumentLink simulationDocumentLink, SimulationJobStatus jobStatus, StateInfo stateInfo) {	
	this.simulationMetadata = simulationMetadata;
	this.simulationDocumentLink = simulationDocumentLink;
	this.jobStatus = jobStatus;
	this.stateInfo = stateInfo;
}

public Object[] toObjects() {
	Long elapsedTime = null;
	if (jobStatus.getStartDate()!=null){
		if (jobStatus.getEndDate()!=null){
			elapsedTime = ((jobStatus.getEndDate().getTime()-jobStatus.getStartDate().getTime()));
		}else if (jobStatus.getSchedulerStatus().isRunning()){
			elapsedTime = ((System.currentTimeMillis()-jobStatus.getStartDate().getTime()));
		}
	}

	return new Object[] {
			(simulationDocumentLink instanceof BioModelLink)?("BM \""+((BioModelLink)simulationDocumentLink).bioModelName+"\", APP \""+((BioModelLink)simulationDocumentLink).simContextName+"\", SIM \""+simulationMetadata.simname+"\""):((simulationDocumentLink instanceof MathModelLink)?("MM \""+((MathModelLink)simulationDocumentLink).mathModelName+"\", SIM \""+simulationMetadata.simname+"\""):("")), 
			simulationMetadata.vcSimID.getOwner().getName(),
			new BigDecimal(simulationMetadata.vcSimID.getSimulationKey().toString()), 
			jobStatus.getJobIndex(),
			simulationMetadata.scanCount,  
			simulationMetadata.solverTaskDesc == null || simulationMetadata.solverTaskDesc.getSolverDescription() == null ? "" : simulationMetadata.solverTaskDesc.getSolverDescription().getDisplayLabel(), 		
			"<"+jobStatus.getSchedulerStatus().getDescription()+"> "+jobStatus.getSimulationMessage().getDisplayMessage(), 
			jobStatus.getComputeHost(), 
			jobStatus.getServerID().toString(),
			jobStatus.getTaskID(), 
			jobStatus.getSubmitDate(), 
			jobStatus.getStartDate(), 
			jobStatus.getEndDate(),
			elapsedTime, 
			new Long(simulationMetadata.getMeshSize())};
}

}
