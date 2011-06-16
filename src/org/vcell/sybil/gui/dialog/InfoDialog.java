/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.gui.dialog;

/*   Info Dialog  --- by Oliver Ruebenacker, UCHC --- September 2008 to February 2010
 *   A dialog for displaying information about Sybil.
 */

import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public abstract class InfoDialog extends JDialog {
	
	public static interface Factory<D extends InfoDialog> { 
		public D create(Component component); 
	}

	public InfoDialog() { super(); }
	public InfoDialog(JFrame frameNew, String title) { super(frameNew, title, true); }
	public InfoDialog(JDialog dialogNew, String title) { super(dialogNew, title, true); }

	public abstract void showDialog();
	public abstract void finished();
	
}
