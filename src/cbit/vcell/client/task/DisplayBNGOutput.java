/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.task;

import java.util.Hashtable;

import cbit.vcell.client.BNGWindowManager;
import cbit.vcell.client.bionetgen.BNGOutputPanel;
import cbit.vcell.server.bionetgen.BNGOutput;

/**
 * Insert the type's description here.
 * Creation date: (7/19/2006 2:19:07 PM)
 * @author: Anuradha Lakshminarayana
 */
public class DisplayBNGOutput extends AsynchClientTask {
	public DisplayBNGOutput() {
		super("Displaying BioNetGen output", TASKTYPE_SWING_NONBLOCKING, false, false);
	}

/**
 * Insert the method's description here.
 * Creation date: (7/19/2006 2:19:07 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(Hashtable<String, Object> hashTable) throws Exception {
	BNGOutputPanel bngOutputPanel = (BNGOutputPanel)hashTable.get(BNGWindowManager.BNG_OUTPUT_PANEL);
	BNGOutput bngOutput = (BNGOutput)hashTable.get("bngOutput");
	if (bngOutput != null) {
		bngOutputPanel.changeBNGPanelTab();
		bngOutputPanel.setBngOutput(bngOutput);		
	}
	bngOutputPanel.refreshButton(false);
}

}
