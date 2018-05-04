package org.vcell.sbmlsim.api.common;

import java.io.File;

public class VcmlToSbmlResults {
	String sbmlFilePath;

	public String getSbmlFilePath() {
		return sbmlFilePath;
	}

	public void setSbmlFilePath(File sbmlFile) {
		this.sbmlFilePath = sbmlFile.getAbsolutePath();
	}

}
