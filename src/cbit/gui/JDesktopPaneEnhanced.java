package cbit.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import javax.swing.*;
/**
 * Insert the type's description here.
 * Creation date: (3/19/01 4:04:38 PM)
 * @author: Jim Schaff
 */
public class JDesktopPaneEnhanced extends javax.swing.JDesktopPane {
/**
 * JDesktopPaneEnhanced constructor comment.
 */
public JDesktopPaneEnhanced() {
	super();
	//
	// this gets around (hopefully) the single focus problem with JDesktopPanes in Java 1.2.2
	//
	String version = System.getProperty("java.version");
	if (version.compareTo("1.2.2")<=0){
		setDesktopManager(new WindowsDesktopManagerFixed());
	}
}
}
