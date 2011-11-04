/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping.vcell_4_8;

import cbit.vcell.model.Species;
import cbit.vcell.parser.Expression;
/**
 * This type was created in VisualAge.
 */
class ResolvedFlux {
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
Species getSpecies() {
	return species;
}
}
