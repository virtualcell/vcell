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

	@Override
	public Object doConstruct() { 
		StageBioModelBuilder.build(view());
		return new Result(view(), PortStage.stageBioModel);
	}

	@Override
	public String getNonSwingTaskName() { return "Constructing BioModel from SBPAX data"; }
	@Override
	public String getSwingTaskName() { return "Finished constructing BioModel from SBPAX data"; }
	
};


