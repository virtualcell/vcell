package org.vcell.rest.common;

import cbit.vcell.modeldb.SimulationRep;


public class SimulationRepresentation {
	
	private final String key;
	private final String branchId;
	private final String name;
	private final String ownerName;
	private final String ownerKey;
	private final String mathKey;

	public SimulationRepresentation(SimulationRep simulationRep) {
		key = simulationRep.getKey().toString();
		branchId = simulationRep.getBranchID().toString();
		name = simulationRep.getName();
		ownerKey = simulationRep.getOwner().getID().toString();
		ownerName = simulationRep.getOwner().getName();
		mathKey = simulationRep.getMathKey().toString();
	}

	public String getKey() {
		return key;
	}

	public String getBranchId() {
		return branchId;
	}

	public String getName() {
		return name;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public String getOwnerKey() {
		return ownerKey;
	}

	public String getMathKey() {
		return mathKey;
	}
	

}
