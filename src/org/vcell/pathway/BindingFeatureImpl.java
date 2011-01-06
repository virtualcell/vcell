package org.vcell.pathway;

public class BindingFeatureImpl extends EntityFeatureImpl implements BindingFeature {
	private BindingFeature bindsTo;
	private Boolean intraMolecular;
	public BindingFeature getBindsTo() {
		return bindsTo;
	}
	public Boolean getIntraMolecular() {
		return intraMolecular;
	}
	public void setBindsTo(BindingFeature bindsTo) {
		this.bindsTo = bindsTo;
	}
	public void setIntraMolecular(Boolean intraMolecular) {
		this.intraMolecular = intraMolecular;
	}
	
}
