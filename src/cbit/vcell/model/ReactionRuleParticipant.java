package cbit.vcell.model;

import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;

public abstract class ReactionRuleParticipant implements ModelProcessParticipant {
	private final SpeciesPattern speciesPattern;
	
	public ReactionRuleParticipant(SpeciesPattern speciesPattern){
		this.speciesPattern = speciesPattern;
	}
	
	public SpeciesPattern getSpeciesPattern(){
		return this.speciesPattern;
	}

	@Override
	public boolean compareEqual(Matchable obj) {
		if (getClass().equals(obj.getClass())){
			ReactionRuleParticipant other = (ReactionRuleParticipant)obj;
			if (!Compare.isEqual(speciesPattern, other.speciesPattern)){
				return false;
			}
			return true;
		}
		return false;
	}
	
	
}
