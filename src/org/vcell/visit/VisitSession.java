package org.vcell.visit;

import org.vcell.util.document.VCDataIdentifier;

import cbit.vcell.visit.VisitSession.VisitSessionException;

public interface VisitSession {

	VisitDatabaseSpec createDatabaseSpec(VCDataIdentifier vcDataIdentifier);

	VisitDatabaseInfo openDatabase(VisitDatabaseSpec visitDatabaseSpec) throws VisitSessionException;
	void closeDatabase(VisitDatabaseInfo visitDatabaseInfo);
	
	void init();
	
	public void addAndDrawPseudocolorPlot(String variableName);
	
	public void synchronize();

}
