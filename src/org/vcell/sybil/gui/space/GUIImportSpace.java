/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.gui.space;

/*   GUIImportSpace  --- by Oliver Ruebenacker, UCHC --- November 2007 to March 2009
 *   GUI for data import
 */

import org.vcell.sybil.gui.bpimport.ImportPanel;
import org.vcell.sybil.models.io.FileManager;
import org.vcell.sybil.util.ui.UIImportSpace;

public class GUIImportSpace<P extends ImportPanel> extends GUISpace<P> implements UIImportSpace {
	
	protected GUIImportSpace(P newPanel) { super(newPanel); }

	public P importPanel() { return component(); }
	
	public FileManager fileManager() { return component().fileManager(); }
	public void requestFocusForThis() { component().focusOnInput(); }

}
