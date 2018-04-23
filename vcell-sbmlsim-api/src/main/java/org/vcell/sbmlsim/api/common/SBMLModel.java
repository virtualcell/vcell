package org.vcell.sbmlsim.api.common;

import java.io.File;

public class SBMLModel  {
	private File filepath;

	public SBMLModel(File filepath) {
		super();
		this.filepath = filepath;
	}

	public File getFilepath() {
		return filepath;
	}

	public void setFilepath(File filepath) {
		this.filepath = filepath;
	}
}

