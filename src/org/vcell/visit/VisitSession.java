package org.vcell.visit;

import llnl.visit.LostConnectionException;

import org.vcell.util.document.VCDataIdentifier;

import cbit.vcell.visit.VisitSession.VisitSessionException;

public interface VisitSession {

	VisitDatabaseSpec createDatabaseSpec(VCDataIdentifier vcDataIdentifier);

	VisitDatabaseInfo openDatabase(VisitDatabaseSpec visitDatabaseSpec) throws VisitSessionException, LostConnectionException;
	void closeDatabase(VisitDatabaseInfo visitDatabaseInfo);
	
	void init();
	
	public void addAndDrawPseudocolorPlot(String variableName) throws VisitSessionException;
	public void openGUI() throws VisitSessionException;
	
	public boolean synchronize();

}
