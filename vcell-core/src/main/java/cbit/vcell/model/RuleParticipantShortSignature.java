package cbit.vcell.model;

import cbit.vcell.graph.ModelCartoon;

public class RuleParticipantShortSignature extends RuleParticipantSignature {

	public RuleParticipantShortSignature() {
		super();
	}

	@Override
	public GroupingCriteria getGroupingCriteria() {
		return GroupingCriteria.molecule;
	}

	public static RuleParticipantShortSignature fromReactionRuleParticipant(ReactionRuleParticipant rrParticipant, ModelCartoon modelCartoon) {
		RuleParticipantShortSignature ruleParticipantSignature = new RuleParticipantShortSignature();
		ruleParticipantSignature.structure = rrParticipant.getStructure();
		ruleParticipantSignature.firstSpeciesPattern = rrParticipant.getSpeciesPattern();
		ruleParticipantSignature.modelCartoon = modelCartoon;
		return ruleParticipantSignature;
	}

}

