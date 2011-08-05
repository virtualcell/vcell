/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway;

public class DeltaG extends BioPaxObjectImpl implements UtilityClass {
	private Double deltaGPrime0;
	private Double ionicStrength;
	private Double ph;
	private Double pMg;
	private Double temperature;
	
	public Double getDeltaGPrime0() {
		return deltaGPrime0;
	}
	public Double getIonicStrength() {
		return ionicStrength;
	}
	public Double getPh() {
		return ph;
	}
	public Double getpMg() {
		return pMg;
	}
	public Double getTemperature() {
		return temperature;
	}
	public void setDeltaGPrime0(Double deltaGPrime0) {
		this.deltaGPrime0 = deltaGPrime0;
	}
	public void setIonicStrength(Double ionicStrength) {
		this.ionicStrength = ionicStrength;
	}
	public void setPh(Double ph) {
		this.ph = ph;
	}
	public void setpMg(Double pMg) {
		this.pMg = pMg;
	}
	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb,level);
		printDouble(sb,"deltaGPrime0",deltaGPrime0,level);
		printDouble(sb,"ionicStrength",ionicStrength,level);
		printDouble(sb,"pH",ph,level);
		printDouble(sb,"pMg",pMg,level);
		printDouble(sb,"temperature",temperature,level);
	}

}
