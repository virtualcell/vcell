package org.vcell.sedml;

import com.google.gson.annotations.SerializedName;

public enum TaskType {
	@SerializedName("Units")
	UNITS,

	@SerializedName("Model")
	MODEL,

	@SerializedName("SimContext")
	SIMCONTEXT,

	@SerializedName("Simulation")
	SIMULATION,

	@SerializedName("Biomodel")
	BIOMODEL;
}
