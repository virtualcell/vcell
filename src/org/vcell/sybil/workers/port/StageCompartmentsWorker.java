/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.workers.port;

/*   StageCompartmentsWorker  --- by Oliver Ruebenacker, UCHC --- August 2008 to January 2010
 *   A SwingWorker to perform stage compartments of a BioPAX import
 */

import org.vcell.sybil.models.bpimport.PortStage;
import org.vcell.sybil.models.bpimport.StageCompartmentsBuilder;
import org.vcell.sybil.models.views.SBWorkView;

public class StageCompartmentsWorker extends PortWorker {

	public StageCompartmentsWorker(SBWorkView view, PortWorker.ResultAcceptor acceptor) {
		super(view, acceptor);
	}

	public Object doConstruct() { 
		StageCompartmentsBuilder.build(view()); 
		return new Result(view(), PortStage.stageCompartments);
	}

	public String getNonSwingTaskName() {
		return "Going through compartment stage - which is nothing any more, really";
	}
	
	public String getSwingTaskName() { return "Done going through compartment stage"; }
	
};


