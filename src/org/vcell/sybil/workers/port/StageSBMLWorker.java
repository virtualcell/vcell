package org.vcell.sybil.workers.port;

/*   StageSBMLWorker  --- by Oliver Ruebenacker, UCHC --- July 2008 to October 2009
 *   A SwingWorker to perform stage SBML of a BioPAX import
 */

import org.vcell.sybil.models.bpimport.PortStage;
import org.vcell.sybil.models.bpimport.StageSBMLBuilder;
import org.vcell.sybil.models.views.SBWorkView;
import org.vcell.sybil.util.sbml.LibSBMLUtil.LibSBMLException;

public class StageSBMLWorker extends PortWorker {

	public StageSBMLWorker(SBWorkView view, PortWorker.ResultAcceptor acceptor) {
		super(view, acceptor);
	}

	@Override
	public Result doConstruct() { 
		try {
			StageSBMLBuilder.build(view());
			return new Result(view(), PortStage.stageSBML);
		} catch (LibSBMLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String getNonSwingTaskName() { return "Creating SBML model"; }
	@Override
	public String getSwingTaskName() { return "Done creating SBML model"; }
	
};


