package org.vcell.pathway;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

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
	
	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
		
		if(bindsTo == objectProxy) {
			bindsTo = (BindingFeature) concreteObject;
		}
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb,level);
		printObject(sb,"bindsTo",bindsTo,level);
		printBoolean(sb,"intraMolecular",intraMolecular,level);
	}

}
