/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.graph.structures;

import java.util.Arrays;
import java.util.List;

import org.vcell.sbml.vcell.SBMLImporter;

import cbit.vcell.model.Model;
import cbit.vcell.model.Structure;

public class AllStructureSuite extends StructureSuite {

	public static final String TITLE = "Reactions for all Structures";
	
	protected final Model.Owner modelOwner;

	public AllStructureSuite(Model.Owner modelOwner) {
		super(TITLE);
		this.modelOwner = modelOwner;
	}
	
	public List<Structure> getStructures() {
		return Arrays.asList(SBMLImporter.sortStructures(modelOwner.getModel()));
	}

	@Override
	public boolean areReactionsShownFor(Structure structure) { return true; }
	
}
