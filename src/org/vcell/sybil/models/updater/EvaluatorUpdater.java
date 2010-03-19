package org.vcell.sybil.models.updater;

/*   EvaluatorUpdater  --- by Oliver Ruebenacker, UCHC --- April 2008 to January 2010
 *   Listens to events from a RefMap<Model> and updates evaluators if applicable
 */

import org.vcell.sybil.actions.RequesterProvider;
import org.vcell.sybil.models.io.FileEvent;
import org.vcell.sybil.models.ontology.Evaluator;
import org.vcell.sybil.workers.evaluator.EvaluatorWorker;

public class EvaluatorUpdater implements FileEvent.Listener {

	protected Evaluator evaluator;
	
	public EvaluatorUpdater(Evaluator evaluator) { 
		this.evaluator = evaluator;
	}

	public void fileEvent(FileEvent event) {
		if(event.modelHasChanged()) {
			new EvaluatorWorker(evaluator, event.fileManager().view()).run(RequesterProvider.requester());			
		}
	}

}
