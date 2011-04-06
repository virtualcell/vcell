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

	@Override
	public Object doConstruct() { 
		StageCompartmentsBuilder.build(view()); 
		return new Result(view(), PortStage.stageCompartments);
	}

	@Override
	public String getNonSwingTaskName() {
		return "Going through compartment stage - which is nothing any more, really";
	}
	
	@Override
	public String getSwingTaskName() { return "Done going through compartment stage"; }
	
};


