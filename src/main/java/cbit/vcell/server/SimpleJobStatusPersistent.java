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

import cbit.vcell.messaging.db.SimulationDocumentLink;
import cbit.vcell.messaging.db.SimulationMetadata;

/**
 * Insert the type's description here.
 * Creation date: (9/3/2003 10:39:26 AM)
 * @author: Fei Gao
 */
public class SimpleJobStatusPersistent {
	public final SimulationMetadata simulationMetadata;
	public final SimulationDocumentLink simulationDocumentLink;
	public final SimulationJobStatusPersistent jobStatus;
	

/**
 * SimpleJobStatus constructor comment.
 */
public SimpleJobStatusPersistent(SimulationMetadata simulationMetadata, SimulationDocumentLink simulationDocumentLink, SimulationJobStatusPersistent jobStatus) {	
	super();
	this.simulationMetadata = simulationMetadata;
	this.simulationDocumentLink = simulationDocumentLink;
	this.jobStatus = jobStatus;
}

}
