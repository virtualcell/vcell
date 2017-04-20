package cbit.vcell.model;

import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.Displayable;

public class ProductPattern extends ReactionRuleParticipant implements Displayable {
	
	public ProductPattern(SpeciesPattern speciesPattern, Structure structure){
		super(speciesPattern, structure);
	}

	public static final String typeName = "Product Pattern";
	@Override
	public String getDisplayName() {
		return getSpeciesPattern().toString();
	}
	@Override
	public String getDisplayType() {
		return typeName;
	}
}
