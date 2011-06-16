/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

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
