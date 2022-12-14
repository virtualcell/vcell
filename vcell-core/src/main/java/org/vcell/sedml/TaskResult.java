package org.vcell.sedml;

import com.google.gson.annotations.SerializedName;

public enum TaskResult {
	@SerializedName("Suceeded")
	SUCCEEDED,
	
	@SerializedName("Failed")
	FAILED;
}
