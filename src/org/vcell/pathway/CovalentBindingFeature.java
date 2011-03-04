package org.vcell.pathway;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;

public class CovalentBindingFeature extends BindingFeatureImpl implements ModificationFeature {
	private SequenceModificationVocabulary modificationType;

	public SequenceModificationVocabulary getModificationType() {
		return modificationType;
	}

	public void setModificationType(SequenceModificationVocabulary modificationType) {
		this.modificationType = modificationType;
	}
	
	@Override
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject){
		super.replace(objectProxy, concreteObject);
		
		if(modificationType == objectProxy) {
			modificationType = (SequenceModificationVocabulary) concreteObject;
		}
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb,level);
		printObject(sb,"modificationType",modificationType,level);
	}
}
