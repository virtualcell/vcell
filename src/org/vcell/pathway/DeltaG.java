package org.vcell.pathway;

public class DeltaG implements UtilityClass {
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

}
