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

import cbit.vcell.model.GeneralKinetics;
import cbit.vcell.model.Model;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.ExpressionException;

public abstract class DummyReactionStep extends SimpleReaction {
	public DummyReactionStep(String name, Model model, Structure structure, SpeciesContext speciesContext) throws java.beans.PropertyVetoException {
		super(model, structure,name,true);
		try {
			setKinetics(new GeneralKinetics(this));
		} catch (ExpressionException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
		addReactionParticipant(new Reactant(null,this,speciesContext,1));
	}
}
