/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry.gui;
import java.util.Hashtable;

import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
/**
 * Insert the type's description here.
 * Creation date: (9/13/2004 4:41:33 PM)
 * @author: Jim Schaff
 */
public class SurfaceGenerationTask extends AsynchClientTask {
	public SurfaceGenerationTask() {
		super("creating new smoothed surface", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING);
	}

/**
 * Insert the method's description here.
 * Creation date: (9/13/2004 4:41:33 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(Hashtable<String, Object> hashTable) throws Exception {
	GeometrySurfaceDescription geometrySurfaceDescription = (GeometrySurfaceDescription)hashTable.get("geometrySurfaceDescription");
	geometrySurfaceDescription.updateAll();
}

}
