package cbit.vcell.mapping;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import org.vcell.expression.ExpressionFactory;
import org.vcell.expression.IExpression;

import cbit.vcell.model.Species;
/**
 * This type was created in VisualAge.
 */
public class ResolvedFlux {
	private Species species=null;
	IExpression inFlux = ExpressionFactory.createExpression(0.0);
	IExpression outFlux = ExpressionFactory.createExpression(0.0);
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
