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

import org.vcell.api.common.SimpleJobStatusRepresentation;

import cbit.vcell.solver.SimulationMetadata;

/**
 * Insert the type's description here.
 * Creation date: (9/3/2003 10:39:26 AM)
 * @author: Fei Gao
 */
public class SimpleJobStatus implements Serializable {
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

public SimpleJobStatusRepresentation toRep() {
	return this.jobStatus.toRep();
}

}
