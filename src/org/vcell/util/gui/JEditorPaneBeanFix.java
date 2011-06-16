/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.gui;


import java.net.*;
/**
 * Insert the type's description here.
 * Creation date: (2/4/2001 7:28:43 PM)
 * @author: Ion Moraru
 */
@SuppressWarnings("serial")
public class JEditorPaneBeanFix extends javax.swing.JEditorPane {
	// this is just a dummy class to fix the a (VAJava ?) bug:
	// "page" is a bound property of the JEditorPane superclass that is for some reason not detected
/**
 * Gets the current url being displayed.  If a URL was 
 * not specified in the creation of the document, this
 * will return null, and relative URL's will not be 
 * resolved.
 *
 * @return the URL
 */
public URL getPage() {
	return super.getPage();
}
}
