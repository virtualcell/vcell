package org.vcell.pathway;

import java.util.ArrayList;

public class GeneticInteraction extends InteractionImpl {

	private ArrayList<Score> interactionScore = new ArrayList<Score>();

	private PhenotypeVocabulary phenotype;

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
	
	public void showChildren(StringBuffer sb, int level){
		super.showChildren(sb, level);
		printObjects(sb, "interactionScore",interactionScore,level);
		printObject(sb, "phenoType",phenotype,level);
	}

}
