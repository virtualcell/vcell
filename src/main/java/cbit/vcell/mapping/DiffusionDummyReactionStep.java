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

import cbit.vcell.model.Model;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.ExpressionException;
/**
 * This type was created in VisualAge.
 */
public class DiffusionDummyReactionStep extends DummyReactionStep {
/**
 * DiffusionReactionStep constructor comment.
 * @param structure cbit.vcell.model.Structure
 * @throws ExpressionException 
 */
public DiffusionDummyReactionStep(String name, Model model, Structure structure, SpeciesContext speciesContext) throws java.beans.PropertyVetoException {
	super(name, model, structure, speciesContext);
}
}
