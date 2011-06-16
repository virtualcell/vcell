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

/*   GUIInfoDialogSpace  --- by Oliver Ruebenacker, UCHC --- April 2009 to March 2010
 *   A wrapper for ComparePanel to compare two models
 */

import org.vcell.sybil.gui.dialog.InfoDialog;
import org.vcell.sybil.util.ui.UIInfoDialogSpace;

public class GUIInfoDialogSpace<D extends InfoDialog> extends GUISpace<D> 
implements UIInfoDialogSpace {
	
	protected GUIInfoDialogSpace(D newDialog) { super(newDialog); }

	public D dialog() { return component(); }

	public void showInfoDialog() { dialog().showDialog(); }

}
