package cbit.vcell.graph;

import cbit.vcell.model.RuleParticipantSignature;
import cbit.vcell.model.RuleParticipantSignature.Criteria;

@SuppressWarnings("serial")
public class ParticipantSignatureShapePanel extends LargeShapePanel implements ShapeModeInterface {
	
	RuleParticipantSignature.Criteria crit = RuleParticipantSignature.Criteria.full;
	RuleParticipantSignature signature = null;

	public void setCriteria(Criteria crit) {
		this.crit = crit;
	}
	public RuleParticipantSignature.Criteria getCriteria() {
		return this.crit;
	}
	public void setSignature(RuleParticipantSignature signature) {
		this.signature = signature;
	}
	public RuleParticipantSignature getSignature() {
		return this.signature;
	}
}
