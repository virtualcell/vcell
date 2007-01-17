package cbit.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.io.*;
import java.net.*;
/**
 * Insert the type's description here.
 * Creation date: (2/4/2001 7:28:43 PM)
 * @author: Ion Moraru
 */
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
