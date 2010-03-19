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

	public Object doConstruct() { 
		StageInitBuilder.createStageInit(view()); 
		return new Result(view(), PortStage.stageInit);
	}

	public String getNonSwingTaskName() { return "Performing SYBREAM reasoning"; }
	public String getSwingTaskName() { return "Done performing SYBREAM reasoning"; }
	
};


