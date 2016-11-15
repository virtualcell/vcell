package cbit.vcell.model;

import cbit.vcell.graph.ModelCartoon;

public class RuleParticipantLongSignature extends RuleParticipantSignature {

	public RuleParticipantLongSignature() {
		super();
	}

	public static RuleParticipantLongSignature fromReactionRuleParticipant(ReactionRuleParticipant rrParticipant, ModelCartoon modelCartoon) {
		RuleParticipantLongSignature ruleParticipantSignature = new RuleParticipantLongSignature();
		ruleParticipantSignature.structure = rrParticipant.getStructure();
		ruleParticipantSignature.speciesPattern = rrParticipant.getSpeciesPattern();
		ruleParticipantSignature.modelCartoon = modelCartoon;
		return ruleParticipantSignature;
	}

}

