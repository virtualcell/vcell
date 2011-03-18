package org.vcell.pathway;

public interface BindingFeature extends EntityFeature {

	public BindingFeature getBindsTo();
	public Boolean getIntraMolecular();
	
	public void setBindsTo(BindingFeature bindsTo);
	public void setIntraMolecular(Boolean intraMolecular);

}
