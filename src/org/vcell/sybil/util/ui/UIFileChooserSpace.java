/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.ui;

/*   UIFileChooserSpace  --- by Oliver Ruebenacker, UCHC --- October 2007 to January 2009
 *   A space for a file chooser
 */

import java.io.File;

public interface UIFileChooserSpace extends UIComponent {

	public void selectFileToOpen();
	public void selectFileToSave();
	public void selectFile(String purpose);
	public void addChoosableFileFilter(UIFileFilter newFilter);
	public File selectedFile();
}
