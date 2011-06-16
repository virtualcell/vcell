/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.gui;

/*   GUIManager  --- by Oliver Ruebenacker, UCHC --- April 2007 to March 2010
 *   Coordinates various GUI components
 */

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.vcell.sybil.actions.ActionBranch;
import org.vcell.sybil.actions.ActionMap;
import org.vcell.sybil.actions.BaseAction;
import org.vcell.sybil.actions.CoreManager;
import org.vcell.sybil.actions.files.FileActions;
import org.vcell.sybil.actions.graph.components.GraphCompActions;
import org.vcell.sybil.actions.graph.edit.GraphEditActions;
import org.vcell.sybil.actions.graph.layout.GraphLayoutActions;
import org.vcell.sybil.actions.info.InfoActions;

import org.vcell.sybil.gui.space.GUIBase;
import org.vcell.sybil.gui.space.GUIFrameSpace;
import org.vcell.sybil.util.enumerations.SmartEnum;
import org.vcell.sybil.util.exception.CatchUtil;

public class GUIManager extends GUIBase implements Runnable {

	public GUIManager(GUIFrameSpace frameSpaceNew, CoreManager coreManager, ActionMap actionMapNew) {
		super(frameSpaceNew, coreManager, actionMapNew);
	}
		
	public void run(){
		try {		
			coreManager.frameSpace().prepare();		
			coreManager.frameSpace().setVisible(true);	
			// TODO necessarily twice?
			coreManager.frameSpace().updateUI();
			coreManager.postInit();
			coreManager.frameSpace().updateUI();
		} catch (Throwable e) {
			CatchUtil.handle(e);
		}
	}
	
	public JMenuBar createMenuBar() {
		JMenuBar myBar = new JMenuBar();
		myBar.add(createMenu("File", FileActions.class));
		myBar.add(createMenu("Edit", GraphEditActions.class));
		myBar.add(createMenu("View", GraphLayoutActions.class));
		myBar.add(createMenu("Components", GraphCompActions.class));
		myBar.add(createMenu("Advice", InfoActions.class));
		return myBar;
	}

	private JMenu createMenu(String name, Class<? extends ActionBranch> branchClass) {
		JMenu menu = new JMenu(name);
		SmartEnum<BaseAction> actions = actionMap.actions(branchClass);
		while(actions.hasMoreElements()) {
			menu.add(new JMenuItem(actions.nextElement()));
			if(actions.isAtInternalBoundary()) { menu.addSeparator(); }
		}
		return menu;
	}

}

