package org.vcell.sedml;

import com.google.gson.annotations.SerializedName;

public enum TaskType {
	@SerializedName("SimContext")
	SIMCONTEXT,
	
	@SerializedName("Simulation")
	SIMULATION;
}
