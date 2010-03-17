package org.vcell.sybil.util.ui;

import java.io.File;

public interface UIFileFilter {

	public boolean accept(File file);
	public String getDescription();
	
}
