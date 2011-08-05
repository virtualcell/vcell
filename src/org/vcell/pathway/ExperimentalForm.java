/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway;

import java.util.ArrayList;

public class ExperimentalForm extends BioPaxObjectImpl implements UtilityClass {
	private ArrayList<EntityFeature> experimentalFeature = new ArrayList<EntityFeature>();
	private ArrayList<ExperimentalFormVocabulary> experimentalFormDescription = new ArrayList<ExperimentalFormVocabulary>();
	private ArrayList<Gene> experimentalFormGene = new ArrayList<Gene>();
	private ArrayList<PhysicalEntity> experimentalFormPhysicalEntity = new ArrayList<PhysicalEntity>();
	
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
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printObjects(sb, "experimentalFeature",experimentalFeature,level);
		printObjects(sb, "experimentalFormDescription",experimentalFormDescription,level);
		printObjects(sb, "experimentalFormGene",experimentalFormGene,level);
		printObjects(sb, "experimentalFormPhysicalEntity",experimentalFormPhysicalEntity,level);
	}

}
