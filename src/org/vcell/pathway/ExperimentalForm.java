package org.vcell.pathway;

import java.util.ArrayList;

public class ExperimentalForm implements UtilityClass {
	private ArrayList<EntityFeature> experimentalFeature;
	private ArrayList<ExperimentalFormVocabulary> experimentalFormDescription;
	private ArrayList<Gene> experimentalFormGene;
	private ArrayList<PhysicalEntity> experimentalFormPhysicalEntity;
	public ArrayList<EntityFeature> getExperimentalFeature() {
		return experimentalFeature;
	}
	public ArrayList<ExperimentalFormVocabulary> getExperimentalFormDescription() {
		return experimentalFormDescription;
	}
	public ArrayList<Gene> getExperimentalFormGene() {
		return experimentalFormGene;
	}
	public ArrayList<PhysicalEntity> getExperimentalFormPhysicalEntity() {
		return experimentalFormPhysicalEntity;
	}
	public void setExperimentalFeature(ArrayList<EntityFeature> experimentalFeature) {
		this.experimentalFeature = experimentalFeature;
	}
	public void setExperimentalFormDescription(
			ArrayList<ExperimentalFormVocabulary> experimentalFormDescription) {
		this.experimentalFormDescription = experimentalFormDescription;
	}
	public void setExperimentalFormGene(ArrayList<Gene> experimentalFormGene) {
		this.experimentalFormGene = experimentalFormGene;
	}
	public void setExperimentalFormPhysicalEntity(
			ArrayList<PhysicalEntity> experimentalFormPhysicalEntity) {
		this.experimentalFormPhysicalEntity = experimentalFormPhysicalEntity;
	}
}
