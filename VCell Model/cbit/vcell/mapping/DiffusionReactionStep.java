package cbit.vcell.mapping;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.model.*;
/**
 * This type was created in VisualAge.
 */
public class DiffusionReactionStep extends SimpleReaction {
	private SpeciesContext speciesContext = null;
/**
 * DiffusionReactionStep constructor comment.
 * @param structure cbit.vcell.model.Structure
 */
DiffusionReactionStep(String name, Structure structure, SpeciesContext speciesContext) throws java.beans.PropertyVetoException {
	super(structure,name);
	this.speciesContext = speciesContext;
	addReactionParticipant(new Reactant(null,this,speciesContext,1));
}
}
