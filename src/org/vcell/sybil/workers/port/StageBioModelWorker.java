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

/*   StageBioModelWorker  --- by Oliver Ruebenacker, UCHC --- July 2008 to January 2010
 *   A SwingWorker to perform stage BioModel of a BioPAX import
 */

import org.vcell.sybil.models.bpimport.PortStage;
import org.vcell.sybil.models.bpimport.StageBioModelBuilder;
import org.vcell.sybil.models.views.SBWorkView;

public class StageBioModelWorker extends PortWorker {

	public StageBioModelWorker(SBWorkView view, PortWorker.ResultAcceptor acceptor) {
		super(view, acceptor);
	}

	public Object doConstruct() { 
		StageBioModelBuilder.build(view());
		return new Result(view(), PortStage.stageBioModel);
	}

	public String getNonSwingTaskName() { return "Constructing BioModel from SBPAX data"; }
	public String getSwingTaskName() { return "Finished constructing BioModel from SBPAX data"; }
	
};


