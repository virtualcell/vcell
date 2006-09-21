package cbit.vcell.mapping;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.model.Species;
import cbit.vcell.parser.Expression;
/**
 * This type was created in VisualAge.
 */
public class ResolvedFlux {
	private Species species=null;
	Expression inFlux = new Expression(0.0);
	Expression outFlux = new Expression(0.0);
/**
 * ResolvedFlux constructor comment.
 */
ResolvedFlux(Species Aspecies) {
	this.species = Aspecies;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.Species
 */
public Species getSpecies() {
	return species;
}
}
