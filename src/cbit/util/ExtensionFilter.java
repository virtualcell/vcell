package cbit.util;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (7/18/2000 11:43:11 AM)
 * @author: 
 */
public class ExtensionFilter extends javax.swing.filechooser.FileFilter implements java.io.Serializable {
	private String extension;
	private String description;

/**
 * ExtensionFilter constructor comment.
 */
public ExtensionFilter(String ext, String descr) {
	extension = ext.toLowerCase();
	description = descr;
}


/**
 * accept method comment.
 */
public boolean accept(java.io.File file) {
	
	return (file.isDirectory() || file.getName().toLowerCase().endsWith(extension));
}


/**
 * This method tests if two given extension filters are equal by comparing the description.
 */
public boolean equals(Object param) {
	if (param instanceof ExtensionFilter) {
		if ( ((ExtensionFilter)param).getDescription().equals(this.getDescription()) ) {
			return true;
		}
	}
	
	return false;
}


/**
 * getDescription method comment.
 */
public String getDescription() {
	return description;
}


/**
 * Insert the method's description here.
 * Creation date: (9/21/2004 1:21:59 PM)
 * @return java.lang.String
 */
public java.lang.String getExtension() {
	return extension;
}


/**
 * getDescription method comment.
 */
public int hashCode() {	
	return getDescription().hashCode();
}
}