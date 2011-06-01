/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.actions.files;

/*  FileActions  ---  Oliver Ruebenacker, UCHC  ---  May 2007 to February 2010
 *  Actions relating to files or the application in its entirety
 */

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.ActionTree;
import org.vcell.sybil.actions.CoreManager;

public class FileActions extends ActionTree {
	private static final long serialVersionUID = 8170923334410657865L;
	public FileActions(CoreManager coreManager) {
		add(new SingleFileActions(coreManager));
		add(new ExitAction(
				new ActionSpecs("Exit", "Exit", "Exit Application", "files/exit.gif"), 
				coreManager));
	}
}
