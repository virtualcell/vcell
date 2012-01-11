/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */
package org.vcell.inversepde.microscopy;

public class FRAPModelParameters {
	private final String startIndexForRecovery;
	private final String diffusionRate;
	private final String monitorBleachRate;
	private final String mobileFraction;
	private final String secondRate;
	private final String secondFraction;
	
	public FRAPModelParameters(
			String startingIndexForRecovery,
			String diffusionRate,
			String monitorBleachRate,
			String mobileFraction,
			String secondRate,
			String secondFraction){
		this.startIndexForRecovery = startingIndexForRecovery;
		this.diffusionRate = diffusionRate;
		this.monitorBleachRate = monitorBleachRate;
		this.mobileFraction = mobileFraction;
		this.secondRate = secondRate;
		this.secondFraction = secondFraction;
	}

	public String getStartIndexForRecovery() {
		return startIndexForRecovery;
	}

	public String getDiffusionRate() {
		return diffusionRate;
	}

	public String getMonitorBleachRate() {
		return monitorBleachRate;
	}

	public String getMobileFraction() {
		return mobileFraction;
	}

	public String getSecondRate() {
		return secondRate;
	}

	public String getSecondFraction() {
		return secondFraction;
	}
}