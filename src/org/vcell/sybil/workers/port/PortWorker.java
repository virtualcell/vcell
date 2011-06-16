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

/*   PortWorker  --- by Oliver Ruebenacker, UCHC --- September 2008 to January 2010
 *   A SwingWorker registering itself as a Task with a SystemState
 */

import org.vcell.sybil.models.bpimport.PortStage;
import org.vcell.sybil.models.views.SBWorkView;
import org.vcell.sybil.util.state.SystemWorker;

public abstract class PortWorker extends SystemWorker {

	public static class Result {
		
		protected SBWorkView view;
		protected PortStage stage;
		
		public Result(SBWorkView view, PortStage portStage) { 
			this.view = view;
			this.stage = portStage;
		}

		public SBWorkView view() { return view; }
		public PortStage stage() { return stage; }
	}
	
	public static interface ResultAcceptor {
		public void accept(Result result);
	}
	
	protected SBWorkView view;
	protected ResultAcceptor resultAcceptor;
	
	public PortWorker(SBWorkView view, ResultAcceptor stageAcceptor) {
		this.view = view;
		this.resultAcceptor = stageAcceptor;
	}
		
	public SBWorkView view() { return view; }
	
	public Result getResult() { return (Result) get(); }
	
	@Override
	public void doFinished() { 
		resultAcceptor.accept(getResult()); 
	}
	
}
