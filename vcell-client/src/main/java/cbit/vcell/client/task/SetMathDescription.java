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
import java.util.Vector;

import org.vcell.util.Issue;
import org.vcell.util.IssueContext;
import org.vcell.util.document.DocumentValidUtil;
import org.vcell.util.gui.exporter.FileFilters;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.ClientRequestManager.CallAction;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathDescription;
import cbit.vcell.solver.Simulation;
/**
 * Insert the type's description here.
 * Creation date: (5/31/2004 6:03:16 PM)
 * @author: Ion Moraru
 */
public class SetMathDescription extends AsynchClientTask {
	public SetMathDescription() {
		super("Setting MathDescriptions", TASKTYPE_SWING_BLOCKING);
	}

/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(Hashtable<String, Object> hashTable) throws java.lang.Exception {
	DocumentWindowManager documentWindowManager = (DocumentWindowManager)hashTable.get(CommonTask.DOCUMENT_WINDOW_MANAGER.name);
	if(hashTable.get("CallAction") != null && hashTable.get("CallAction") == CallAction.EXPORT) {
		if(hashTable.get("fileFilter") == FileFilters.FILE_FILTER_VCML) {
			return;
		}
	}
	if (documentWindowManager.getVCDocument() instanceof BioModel) {
		// try to successfully generate math and geometry region info
		BioModel bioModel = (BioModel)documentWindowManager.getVCDocument();
		SimulationContext[] scArray = bioModel.getSimulationContexts();
		MathDescription[] mathDescArray = (MathDescription[])hashTable.get("mathDescArray");
		if (scArray!=null && mathDescArray != null) {
			for (int i = 0; i < scArray.length; i++){
				scArray[i].setMathDescription(mathDescArray[i]);
			}
		}
		
		Vector<Issue> issueList = new Vector<Issue>();
		IssueContext issueContext = new IssueContext();
		Simulation[] sims = bioModel.getSimulations();
		for(Simulation sim : sims) {
		sim.gatherIssues(issueContext, issueList);
		}
		DocumentValidUtil.checkIssuesForErrors(issueList);
	}
}

}
