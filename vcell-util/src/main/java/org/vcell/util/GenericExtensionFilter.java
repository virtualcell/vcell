package org.vcell.util;

import java.io.File;
import java.io.FilenameFilter;

public class GenericExtensionFilter implements FilenameFilter {
	private String extension;
	public GenericExtensionFilter(String extension) {
			this.extension = extension;
	}
	public boolean accept(File dir, String name) {
		return (name.endsWith(extension));
	}
}