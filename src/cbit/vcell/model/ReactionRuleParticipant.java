package cbit.vcell.model;

import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;

public abstract class ReactionRuleParticipant implements ModelProcessParticipant {
	private final SpeciesPattern speciesPattern;
	private final Structure structure;
	
	public ReactionRuleParticipant(SpeciesPattern speciesPattern, Structure structure){
		this.speciesPattern = speciesPattern;
		this.structure = structure;
		if (structure == null){
			System.out.println("null structure in ReactionRulePattern");
		}
	}
	
	public SpeciesPattern getSpeciesPattern(){
		return this.speciesPattern;
	}
	
	public Structure getStructure(){
		return this.structure;
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
