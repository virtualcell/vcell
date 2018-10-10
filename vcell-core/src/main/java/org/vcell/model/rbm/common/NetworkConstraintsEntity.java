package org.vcell.model.rbm.common;

public class NetworkConstraintsEntity {
	
	private String name = null;
	private String value = null;
	
	public NetworkConstraintsEntity(String name, String value) {
		this.name = name;
		this.value = value;
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
}
