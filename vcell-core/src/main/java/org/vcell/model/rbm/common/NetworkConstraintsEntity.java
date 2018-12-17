package org.vcell.model.rbm.common;

public class NetworkConstraintsEntity {
	
	private String name = null;
	private String value = null;
	private String defaultValue = null;
	
	public NetworkConstraintsEntity(String name, String value, String defaultValue) {
		this.name = name;
		this.value = value;
		this.defaultValue = defaultValue;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDefaultValue() {
		return defaultValue;
	}

}
