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
