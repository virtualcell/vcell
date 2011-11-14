package org.vcell.visit;

public class VisitDatabaseInfo {
	private String visitOpenDatabaseString;
	private VisitDatabaseSpec visitDatabaseSpec;
	
	public VisitDatabaseInfo(String visitOpenDatabaseString,  VisitDatabaseSpec visitDatabaseSpec) {
		this.visitOpenDatabaseString = visitOpenDatabaseString;
		this.visitDatabaseSpec = visitDatabaseSpec;
	}

	VisitDatabaseSpec getVisitDatabaseSpec() {
		return visitDatabaseSpec;
	}

	public String getVisitOpenDatabaseString() {
		return visitOpenDatabaseString;
	}

}
