package cbit.vcell.mapping;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.model.GeneralKinetics;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.ExpressionException;
/**
 * This type was created in VisualAge.
 */
public class DiffusionReactionStep extends SimpleReaction {
/**
 * DiffusionReactionStep constructor comment.
 * @param structure cbit.vcell.model.Structure
 * @throws ExpressionException 
 */
DiffusionReactionStep(String name, Structure structure, SpeciesContext speciesContext) throws java.beans.PropertyVetoException {
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
