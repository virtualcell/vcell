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
/**
 * Insert the type's description here.
 * Creation date: (7/18/2000 11:43:11 AM)
 * @author: 
 */
@SuppressWarnings("serial")
public class ExtensionFilter extends javax.swing.filechooser.FileFilter implements java.io.Serializable {
	private String[] extensions;
	private String description;

	/**
	 * ExtensionFilter constructor comment.
	 */
	public ExtensionFilter(String arg_extension, String descr) {
		extensions = new String[1];
		extensions[0] = arg_extension.toLowerCase();
		description = descr;
	}

	/**
	 * ExtensionFilter constructor comment.
	 */
	public ExtensionFilter(String[] arg_extensions, String descr) {
		extensions = arg_extensions.clone();
		description = descr;
	}


/**
 * accept method comment.
 */
public boolean accept(java.io.File file) {
	if (file.isDirectory()){
		return true;
	}
	for (int i = 0; i < extensions.length; i++) {
		if (file.getName().toLowerCase().endsWith(extensions[i])){
			return true;
		}
	}
	return false;
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
 * getDescription method comment.
 */
public int hashCode() {	
	return getDescription().hashCode();
}
}
