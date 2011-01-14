package org.vcell.pathway;

public class CovalentBindingFeature extends BindingFeatureImpl implements ModificationFeature {
	private SequenceModificationVocabulary modificationType;

	public SequenceModificationVocabulary getModificationType() {
		return modificationType;
	}

	public void setModificationType(SequenceModificationVocabulary modificationType) {
		this.modificationType = modificationType;
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb,level);
		printObject(sb,"modificationType",modificationType,level);
	}
}
