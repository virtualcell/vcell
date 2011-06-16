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

/*   GUIFileChooserSpace  --- by Oliver Ruebenacker, UCHC --- October 2007 to March 2010
 *   A space for a file chooser
 */

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.vcell.sybil.util.ui.UIFileChooserSpace;
import org.vcell.sybil.util.ui.UIFileFilter;

public class GUIFileChooserSpace<C extends JFileChooser> extends GUISpace<C> 
implements UIFileChooserSpace {

	protected DialogParentProvider parentProvider;
	
	public GUIFileChooserSpace(DialogParentProvider parentProvider, C newChooser) { 
		super(newChooser); 
		this.parentProvider = parentProvider;
	}
	
	public C fileChooser() { return component(); }

	public void addChoosableFileFilter(UIFileFilter newFilter) { 
		fileChooser().addChoosableFileFilter((FileFilter)newFilter); 
	}

	public void selectFile(final String purpose) {
		fileChooser().showDialog(parentProvider.getDialogParent(), purpose); 
	}

	public void selectFileToOpen() {
		fileChooser().showOpenDialog(parentProvider.getDialogParent()); 
	}

	public void selectFileToSave() {
		fileChooser().showSaveDialog(parentProvider.getDialogParent()); 
	}

	public File selectedFile() { return fileChooser().getSelectedFile(); }

}
