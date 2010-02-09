package cbit.vcell.mapping;

import cbit.vcell.model.GeneralKinetics;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.ExpressionException;

public class EventReactionStep extends SimpleReaction {
	EventReactionStep(String name, Structure structure, SpeciesContext speciesContext) throws java.beans.PropertyVetoException {
		super(structure,name);
		try {
			setKinetics(new GeneralKinetics(this));
		} catch (ExpressionException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
		addReactionParticipant(new Reactant(null,this,speciesContext,1));
	}
}
