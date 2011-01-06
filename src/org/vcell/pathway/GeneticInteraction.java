package org.vcell.pathway;

import java.util.ArrayList;

public class GeneticInteraction extends InteractionImpl {

	private ArrayList<Score> interactionScore = new ArrayList<Score>();

	private PhenotypeVocabulary phenotype;

	public GeneticInteraction(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public ArrayList<Score> getInteractionScore() {
		return interactionScore;
	}

	public PhenotypeVocabulary getPhenotype() {
		return phenotype;
	}
	public void setInteractionScore(ArrayList<Score> interactionScore) {
		this.interactionScore = interactionScore;
	}
	
	public void setPhenotype(PhenotypeVocabulary phenotype) {
		this.phenotype = phenotype;
	}

}
