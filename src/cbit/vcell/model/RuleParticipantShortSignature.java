package cbit.vcell.model;

import cbit.vcell.graph.ModelCartoon;

public class RuleParticipantShortSignature extends RuleParticipantSignature {

	public RuleParticipantShortSignature() {
		super();
	}

	public static RuleParticipantShortSignature fromReactionRuleParticipant(ReactionRuleParticipant rrParticipant, ModelCartoon modelCartoon) {
		RuleParticipantShortSignature ruleParticipantSignature = new RuleParticipantShortSignature();
		ruleParticipantSignature.structure = rrParticipant.getStructure();
		ruleParticipantSignature.speciesPattern = rrParticipant.getSpeciesPattern();
		ruleParticipantSignature.modelCartoon = modelCartoon;
		return ruleParticipantSignature;
	}

}

