package org.vcell.sbmlsim.api.common;

public class SimulationInfo{
	private final long localId;
	
	public SimulationInfo(long localId) {
		this.localId = localId;
	}

	public long getLocalId() {
		return localId;
	}
}

