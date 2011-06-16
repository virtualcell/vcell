/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping;

import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.Expression;

public class ResolvedFlux {
	private SpeciesContext speciesContext=null;
	private Expression flux = new Expression(0.0);
	/**
	 * ResolvedFlux constructor comment.
	 */
	ResolvedFlux(SpeciesContext arg_speciesContext) {
		this.speciesContext = arg_speciesContext;
	}
	/**
	 * This method was created in VisualAge.
	 * @return cbit.vcell.model.Species
	 */
	public SpeciesContext getSpeciesContext() {
		return speciesContext;
	}
	public Expression getFlux() {
		return flux;
	}
	public void setFlux(Expression flux) {
		this.flux = flux;
	}
	}
