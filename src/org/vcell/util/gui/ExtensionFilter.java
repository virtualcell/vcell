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

import java.io.File;

/**
 * Insert the type's description here.
 * Creation date: (7/18/2000 11:43:11 AM)
 * @author: 
 */
@SuppressWarnings("serial")
public class ExtensionFilter extends javax.swing.filechooser.FileFilter implements java.io.Serializable {
	private final String[] extensions;
	private final String description;

	/**
	 * ExtensionFilter constructor comment.
	 * @throws IllegalArgumentException if arg_extension is null
	 */
	public ExtensionFilter(String arg_extension, String descr) {
		extensions = new String[1];
		extensions[0] = arg_extension.toLowerCase();
		description = descr;
		checkPrimaryNotNull();
	}

	/**
	 * ExtensionFilter constructor comment.
	 * @throws IllegalArgumentException if extension[0] is null
	 */
	public ExtensionFilter(String[] arg_extensions, String descr) {
		extensions = arg_extensions.clone();
		description = descr;
		checkPrimaryNotNull();
	}
	
	private void checkPrimaryNotNull( ) {
		if (extensions == null || extensions[0] == null) {
			throw new IllegalArgumentException("Extension Filter " + description + " has null primary extension");
		}
	}


/**
 * accept method comment.
 */
public boolean accept(java.io.File file) {
	if (file.isDirectory()){
		return true;
	}
	for (int i = 0; i < extensions.length; i++) {
		if (ExtensionFilter.isMatchingExtension(file, extensions[i])){
			return true;
		}
	}
	return false;
}

public static final boolean isMatchingExtension(File file,String extension){
	return file.getName().toLowerCase().endsWith(extension);
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

/**
 * return first (primary) file extensions
 */
public String getPrimaryExtension( ) {
	return extensions[0];
}

}
