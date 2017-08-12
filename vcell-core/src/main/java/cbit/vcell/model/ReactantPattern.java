package cbit.vcell.model;

import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.Displayable;

public class ReactantPattern extends ReactionRuleParticipant implements Displayable {
	
	public ReactantPattern(SpeciesPattern speciesPattern, Structure structure){
		super(speciesPattern, structure);
	}

	public static final String typeName = "Reactant Pattern";
	@Override
	public String getDisplayName() {
		return getSpeciesPattern().toString();
	}
	@Override
	public String getDisplayType() {
		return typeName;
	}

}
