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

/*   StageTwoWorker  --- by Oliver Ruebenacker, UCHC --- June 2008 to March 2010
 *   A SwingWorker to perform stage two of a BioPAX import
 */

import org.vcell.sybil.models.bpimport.PortStage;
import org.vcell.sybil.models.bpimport.StageInitBuilder;
import org.vcell.sybil.models.views.SBWorkView;

public class StageInitWorker extends PortWorker {

	public StageInitWorker(SBWorkView view, PortWorker.ResultAcceptor acceptor) {
		super(view, acceptor);
	}

	@Override
	public Object doConstruct() { 
		StageInitBuilder.createStageInit(view()); 
		return new Result(view(), PortStage.stageInit);
	}

	@Override
	public String getNonSwingTaskName() { return "Performing SYBREAM reasoning"; }
	@Override
	public String getSwingTaskName() { return "Done performing SYBREAM reasoning"; }
	
};


