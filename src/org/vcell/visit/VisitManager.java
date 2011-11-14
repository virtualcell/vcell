package org.vcell.visit;

import java.util.Iterator;

import cbit.vcell.visit.VisitConnectionInfo;

public interface VisitManager {
	
	Iterator<VisitSession> getVisitSessions();
	
	public VisitSession createLocalSession();

	public VisitSession createRemoteSession(VisitConnectionInfo visitConnectionInfo);
}
