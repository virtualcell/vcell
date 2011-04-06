package org.vcell.sybil.workers.evaluator;

/*   EvaluatorWorker  --- by Oliver Ruebenacker, UCHC --- April 2007 to January 2010
 *   A worker to update the evaluator
 */

import org.vcell.sybil.models.ontology.Evaluator;
import org.vcell.sybil.models.views.SBWorkView;
import org.vcell.sybil.util.exception.CatchUtil;
import org.vcell.sybil.util.state.SystemWorker;

public class EvaluatorWorker extends SystemWorker {
	
	protected Evaluator evaluator;
	protected SBWorkView view;
	protected Evaluator.Event event;
	
	public EvaluatorWorker(Evaluator evaluator, SBWorkView view) {
		this.evaluator = evaluator;
		this.view = view;
	}
	
	@Override
	public Object doConstruct() {
		try { event = evaluator.createDataSet(view); } 
		catch (Exception e) { CatchUtil.handle(e); }
		return null;
	}
	
	@Override
	public void doFinished() {
		if(event != null) { evaluator.listeners().evaluatorEvent(event); }
	}

	@Override
	public String getNonSwingTaskName() { return "evaluating data"; }
	@Override
	public String getSwingTaskName() { return "applying data evaluation"; }

};
