package org.vcell.sybil.workers.port;

/*   StageModelsWorker  --- by Oliver Ruebenacker, UCHC --- July 2008 to January 2010
 *   A SwingWorker to perform stage models of a BioPAX import
 */

import org.vcell.sybil.models.bpimport.PortStage;
import org.vcell.sybil.models.bpimport.StageModelsBuilder;
import org.vcell.sybil.models.views.SBWorkView;

public class StageModelsWorker extends PortWorker {

	public StageModelsWorker(SBWorkView view, PortWorker.ResultAcceptor acceptor) {
		super(view, acceptor);
	}

	public Object doConstruct() { 
		StageModelsBuilder.build(view()); 
		return new Result(view(), PortStage.stageModels);
	}

	public String getNonSwingTaskName() { return "Creating SBPAX system model"; }
	public String getSwingTaskName() { return "Done creating SBPAX system model"; }
	
};


