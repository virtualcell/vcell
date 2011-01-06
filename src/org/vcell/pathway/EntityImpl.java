package org.vcell.pathway;

import cbit.vcell.biomodel.meta.Identifiable;

public class EntityImpl implements Entity, Identifiable {
	
	private String name;
	//private Provenance dataSource;
	//private Evidence evidence;
	
	public EntityImpl(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
