package org.vcell.visit;

import java.io.File;

public class LocalVisitDatabaseSpec extends VisitDatabaseSpec {
	
	public LocalVisitDatabaseSpec(File logFile) {
		super(logFile);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getVisitOpenDatabaseString() {
		return getLogFile().getAbsolutePath();
	}
}
