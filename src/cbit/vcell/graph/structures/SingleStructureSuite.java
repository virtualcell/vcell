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

import java.util.Collections;
import java.util.Set;

import cbit.vcell.model.Structure;

public class SingleStructureSuite extends StructureSuite {

	protected Structure structure;
	protected Set<Structure> structures;
	
	public SingleStructureSuite(Structure structure) {
		super(createTitle(structure));
		this.structure = structure;
		structures = Collections.singleton(structure);
	}
	
	public Set<Structure> getStructures() { return structures; }
	public boolean areReactionsShownFor(Structure structure) {
		return this.structure.equals(structure);
	}
	
	public static String createTitle(Structure structure) {
		return "Reactions for " + structure.getName();
	}
	
}
