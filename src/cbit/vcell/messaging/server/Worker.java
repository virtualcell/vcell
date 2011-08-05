/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.messaging.server;
/**
 * Insert the type's description here.
 * Creation date: (2/20/2004 3:20:06 PM)
 * @author: Fei Gao
 */
public interface Worker extends ServiceProvider, cbit.vcell.messaging.ControlTopicListener, cbit.vcell.solver.SolverListener {
	public String getJobSelector();
	public boolean isRunning();
}
