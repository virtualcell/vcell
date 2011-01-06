package org.vcell.pathway;

public class ModificationFeatureImpl extends EntityFeatureImpl implements ModificationFeature {
	private SequenceModificationVocabulary modificationType;

	public SequenceModificationVocabulary getModificationType() {
		return modificationType;
	}

	public void setModificationType(SequenceModificationVocabulary modificationType) {
		this.modificationType = modificationType;
	}

}
