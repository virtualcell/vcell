package org.vcell.vcellij.api;

public class VariableInfo {

	   private String variableVtuName;
	   private String variableDisplayName;
	   private String domainName;
	   private DomainType variableDomainType;
	   
	public VariableInfo(String variableVtuName, String variableDisplayName, String domainName,
			DomainType variableDomainType) {
		super();
		this.variableVtuName = variableVtuName;
		this.variableDisplayName = variableDisplayName;
		this.domainName = domainName;
		this.variableDomainType = variableDomainType;
	}
	public String getVariableVtuName() {
		return variableVtuName;
	}
	public void setVariableVtuName(String variableVtuName) {
		this.variableVtuName = variableVtuName;
	}
	public String getVariableDisplayName() {
		return variableDisplayName;
	}
	public void setVariableDisplayName(String variableDisplayName) {
		this.variableDisplayName = variableDisplayName;
	}
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public DomainType getVariableDomainType() {
		return variableDomainType;
	}
	public void setVariableDomainType(DomainType variableDomainType) {
		this.variableDomainType = variableDomainType;
	}

}

