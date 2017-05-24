package cbit.vcell.model;

import cbit.vcell.graph.ModelCartoon;

public class RuleParticipantLongSignature extends RuleParticipantSignature {

	public RuleParticipantLongSignature() {
		super();
	}
	
	@Override
	public GroupingCriteria getGroupingCriteria() {
		return GroupingCriteria.full;
	}

	public static RuleParticipantLongSignature fromReactionRuleParticipant(ReactionRuleParticipant rrParticipant, ModelCartoon modelCartoon) {
		RuleParticipantLongSignature ruleParticipantSignature = new RuleParticipantLongSignature();
		ruleParticipantSignature.structure = rrParticipant.getStructure();
		ruleParticipantSignature.firstSpeciesPattern = rrParticipant.getSpeciesPattern();
		ruleParticipantSignature.modelCartoon = modelCartoon;
		return ruleParticipantSignature;
	}

}

