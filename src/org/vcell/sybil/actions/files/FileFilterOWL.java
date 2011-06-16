/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.actions.files;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.vcell.sybil.util.ui.UIFileFilter;


public class FileFilterOWL extends FileFilter implements UIFileFilter {

	public boolean hasExtension(String name, String extension) {
		return name.toLowerCase().endsWith("." + extension.toLowerCase());
	}
	
	@Override
	public boolean accept(File file) {
		return file.isDirectory() || hasExtension(file.getName(), "owl");
	}

	@Override
	public String getDescription() { return "OWL files"; };

}
