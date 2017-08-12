package org.vcell.stochtest;

public enum StochtestMathType {
	ode("ode"),
	rules("rules"),
	pde("pde"),
	nonspatialstochastic("nonspatial-stochastic"),
	spatialstochastic("spatial-stochastic");
	
	private final String databaseTag;
	
	StochtestMathType(String databaseTag){
		this.databaseTag = databaseTag;
	}
	
	public String getDatabaseTag(){
		return this.databaseTag;
	}
	
	public static StochtestMathType fromDatabaseTag(String databaseTag){
		for (StochtestMathType m : values()){
			if (m.getDatabaseTag().equals(databaseTag)){
				return m;
			}
		}
		return null;
	}
}