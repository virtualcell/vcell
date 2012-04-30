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
import cbit.vcell.units.VCUnitDefinition;

public class ResolvedFlux {
	private SpeciesContext speciesContext=null;
	private Expression fluxExpression = new Expression(0.0);
	private VCUnitDefinition unitDefinition = null;
	/**
	 * ResolvedFlux constructor comment.
	 */
	ResolvedFlux(SpeciesContext arg_speciesContext, VCUnitDefinition unit) {
		this.speciesContext = arg_speciesContext;
		unitDefinition = unit;
	}
	/**
	 * This method was created in VisualAge.
	 * @return cbit.vcell.model.Species
	 */
	public SpeciesContext getSpeciesContext() {
		return speciesContext;
	}
	public Expression getFluxExpression() {
		return fluxExpression;
	}
	public void setFluxExpression(Expression flux) {
		this.fluxExpression = flux;
	}
	public VCUnitDefinition getUnitDefinition() {
		return unitDefinition;
	}
	}
