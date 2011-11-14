package org.vcell.visit;

import java.io.File;

import cbit.vcell.visit.VisitConnectionInfo;


public class RemoteVisitDatabaseSpec extends VisitDatabaseSpec {
	private VisitConnectionInfo visitConnectionInfo;
	
	public RemoteVisitDatabaseSpec(File logFile, VisitConnectionInfo visitConnectionInfo) {
		super(logFile);
		this.visitConnectionInfo = visitConnectionInfo;
	}

	public VisitConnectionInfo getVisitConnectionInfo() {
		return visitConnectionInfo;
	}

	@Override
	public String getVisitOpenDatabaseString() {
		String unixFileSeparator = "/";
		return getVisitConnectionInfo().getIPAddress()+":"+
				getVisitConnectionInfo().getDatabaseOpenPath(
						visitConnectionInfo.getUser(),
						getLogFile().getName(),
						unixFileSeparator);
	}

	
}
