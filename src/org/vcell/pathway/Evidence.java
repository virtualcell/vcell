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

public class Evidence extends BioPaxObjectImpl implements UtilityClass {
	private ArrayList<Score> confidence = new ArrayList<Score>();
	private ArrayList<EvidenceCodeVocabulary> evidenceCode = new ArrayList<EvidenceCodeVocabulary>();
	private ArrayList<ExperimentalForm> experimentalForm = new ArrayList<ExperimentalForm>();
	private ArrayList<Xref> xRef = new ArrayList<Xref>();

	public ArrayList<Xref> getxRef() {
		return xRef;
	}

	public void setxRef(ArrayList<Xref> xRef) {
		this.xRef = xRef;
	}
	
	public ArrayList<Score> getConfidence() {
		return confidence;
	}
	public ArrayList<EvidenceCodeVocabulary> getEvidenceCode() {
		return evidenceCode;
	}
	public ArrayList<ExperimentalForm> getExperimentalForm() {
		return experimentalForm;
	}
	public void setConfidence(ArrayList<Score> confidence) {
		this.confidence = confidence;
	}
	public void setEvidenceCode(ArrayList<EvidenceCodeVocabulary> evidenceCode) {
		this.evidenceCode = evidenceCode;
	}
	public void setExperimentalForm(ArrayList<ExperimentalForm> experimentalForm) {
		this.experimentalForm = experimentalForm;
	}
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printObjects(sb, "confidence",confidence,level);
		printObjects(sb, "evidenceCode",evidenceCode,level);
		printObjects(sb, "experimentalForm",experimentalForm,level);
		printObjects(sb, "xRef",xRef,level);
	}

}
