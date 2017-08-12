/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.gui.exporter;

import java.io.File;
import java.util.Hashtable;

import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.lang3.StringUtils;
import org.vcell.util.UserCancelException;

import cbit.vcell.client.TopLevelWindowManager;
import cbit.vcell.mapping.SimulationContext;

/**
 * Insert the type's description here.
 * Creation date: (7/18/2000 11:43:11 AM)
 * @author: 
 */
@SuppressWarnings("serial")
public class ExtensionFilter extends FileFilter implements Comparable<ExtensionFilter>, java.io.Serializable {
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
		validateExtensions();
	}

	/**
	 * ExtensionFilter constructor comment.
	 * @throws IllegalArgumentException if extension[0] is null
	 */
	public ExtensionFilter(String[] arg_extensions, String descr) {
		extensions = arg_extensions.clone();
		description = descr;
		validateExtensions();
	}
	
	/**
	 * see if extension is valid for this Filter
	 * @param ext
	 * @return true if is
	 */
	public boolean isValidExtension(String ext) {
		for (String e : extensions) {
			if (e.equals(ext)) {
				return true;
			}
		}
		return false;
	}

	private void validateExtensions( ) {
		if (extensions == null) {
			throw new IllegalArgumentException("Extension Filter " + description + " has null primary extension");
		}
		for (String e : extensions) {
			if (e == null) {
				throw new IllegalArgumentException("Extension Filter " + description + " has null extension");
			}
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


public String getDescription() {
	return description;
}
/**
 * @return description without file extensions, as indicated by <b>(</b>
 */
public String getShortDescription() {
	return StringUtils.substringBefore(description, "(" );
}

@Override
public int hashCode() {	
	return getDescription().hashCode();
}



@Override
public int compareTo(ExtensionFilter o) {
	return  description.compareTo(o.description);
}

/**
 * return first (primary) file extensions
 */
public String getPrimaryExtension( ) {
	return extensions[0];
}

/**
 * data subtypes may need if they require additional user input 
 */
public static class ChooseContext {
	final Hashtable<String, Object> hashTable; 
	final TopLevelWindowManager topLevelWindowManager;
	final JFrame currentWindow;
	final SimulationContext chosenContext;
	final File selectedFile;
	final String filename;
	public ChooseContext(Hashtable<String, Object> hashTable,
			TopLevelWindowManager topLevelWindowManager, JFrame currentWindow,
			SimulationContext chosenContext, File selectedFile, String filename) {
		super();
		this.hashTable = hashTable;
		this.topLevelWindowManager = topLevelWindowManager;
		this.currentWindow = currentWindow;
		this.chosenContext = chosenContext;
		this.selectedFile = selectedFile;
		this.filename = filename;
	}
}

/**
 * indicates whether additional user choices must be made
 * @return
 */
public boolean requiresMoreChoices( ) {
	return false;
}

/**
 * should be called if {@link #requiresMoreChoices()} returns true. 
 * @param c not null
 * @throws UnsupportedOperationException
 */
public void askUser(ChooseContext c) throws UserCancelException{
	throw new UnsupportedOperationException("not implemented");
}


}
