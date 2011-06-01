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

/*   UIImportSpace  --- by Oliver Ruebenacker, UCHC --- November 2007 to March 2009
 *   An interface for import UI
 */

import org.vcell.sybil.models.io.FileManager;

public interface UIImportSpace extends UIComponent {
	
	public FileManager fileManager();
	public void requestFocusForThis();


}
