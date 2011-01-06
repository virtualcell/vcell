package org.vcell.pathway;

public interface ModificationFeature extends EntityFeature {
	public SequenceModificationVocabulary getModificationType();
	public void setModificationType(SequenceModificationVocabulary modificationType);
}
