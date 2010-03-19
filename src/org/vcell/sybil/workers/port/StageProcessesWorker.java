package org.vcell.sybil.workers.port;

/*   StageProcessesWorker  --- by Oliver Ruebenacker, UCHC --- July 2008 to January 2010
 *   A SwingWorker to perform stage processes of a BioPAX import
 */

import org.vcell.sybil.models.bpimport.PortStage;
import org.vcell.sybil.models.bpimport.StageProcessesBuilder;
import org.vcell.sybil.models.views.SBWorkView;

public class StageProcessesWorker extends PortWorker {

	public StageProcessesWorker(SBWorkView view, PortWorker.ResultAcceptor acceptor) {
		super(view, acceptor);
	}

	public Object doConstruct() { 
		StageProcessesBuilder.build(view()); 
		return new Result(view(), PortStage.stageProcesses);
	}

	public String getNonSwingTaskName() { return "Creating SBPAX processes"; }
	public String getSwingTaskName() { return "Done creating SBPAX processes"; }
	
};


