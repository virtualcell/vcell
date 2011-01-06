package org.vcell.pathway;

import java.util.ArrayList;

public class Evidence implements UtilityClass {
	private ArrayList<Score> confidence;
	private ArrayList<EvidenceCodeVocabulary> evidenceCode;
	private ArrayList<ExperimentalForm> experimentalForm;
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
}
