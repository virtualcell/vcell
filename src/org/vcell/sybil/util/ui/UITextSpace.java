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

/*   GUITextSpace  --- by Oliver Ruebenacker, UCHC --- November 2007 to January 2009
 *   An interface for text components
 */

import javax.swing.text.Document;

public interface UITextSpace extends UIComponent {
	
	public Document document();
	public void setDocument(Document newDocument);
	

}
