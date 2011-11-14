package org.vcell.visit;

import java.io.File;

public abstract class VisitDatabaseSpec {
	private File logFile;

	public VisitDatabaseSpec(File logFile) {
		super();
		this.logFile = logFile;
	}

	public File getLogFile() {
		return logFile;
	}

	public abstract String getVisitOpenDatabaseString();
}
