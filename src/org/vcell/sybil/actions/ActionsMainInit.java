/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.actions;

/*   ActionsMainInit  --- by Oliver Ruebenacker, UCHC --- April 2007 to February 2010
 *   Provides a universe in which actions live. To be instantiated exactly once.
 */

import org.vcell.sybil.actions.files.FileActions;
import org.vcell.sybil.actions.graph.GraphManager;
import org.vcell.sybil.actions.info.InfoActions;
import org.vcell.sybil.util.ui.UserInterface;

import cbit.vcell.biomodel.BioModel;


public class ActionsMainInit {

	public static interface SubInitGraph  {
		public void addGraphActions(ActionMap actionMap, CoreManager modSysNew);
		public GraphManager graphManager();
	}
	
	protected ActionMap actionMap = new ActionHashMap();
	protected CoreManager coreManager;
	protected SubInitGraph subInitGraph;
	
	public ActionsMainInit(BioModel bioModel, SubInitGraph subInitGraphNew) {
		coreManager = new CoreManager(bioModel);
		subInitGraph = subInitGraphNew;
		subInitGraph.addGraphActions(actionMap, coreManager);
		actionMap.put(new FileActions(coreManager));
		actionMap.put(new InfoActions(coreManager));
	}
	
	public CoreManager coreManager() { return coreManager; }
	public ActionMap actionMap() { return actionMap; }
	
	public void setUI(UserInterface newUI) {
		coreManager.setUI(newUI, subInitGraph.graphManager());
	}

}
