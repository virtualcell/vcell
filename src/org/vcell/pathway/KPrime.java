package org.vcell.pathway;

public class KPrime implements UtilityClass {
	private Double ionicStrength;
	private Double kPrime;
	private Double ph;
	private Double pMg;
	private Double temperature ;
	public Double getIonicStrength() {
		return ionicStrength;
	}
	public Double getkPrime() {
		return kPrime;
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
	public void setIonicStrength(Double ionicStrength) {
		this.ionicStrength = ionicStrength;
	}
	public void setkPrime(Double kPrime) {
		this.kPrime = kPrime;
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
