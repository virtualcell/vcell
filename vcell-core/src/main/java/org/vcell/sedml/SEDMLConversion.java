package org.vcell.sedml;

import com.google.gson.annotations.SerializedName;

public enum SEDMLConversion {
	@SerializedName("Import")
	IMPORT,
	
	@SerializedName("Export")
	EXPORT;
}
