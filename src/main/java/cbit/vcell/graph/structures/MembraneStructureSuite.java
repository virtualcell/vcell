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

import java.util.List;

import org.vcell.sybil.util.lists.ListOfThree;

import cbit.vcell.model.Feature;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Structure;

public class MembraneStructureSuite extends StructureSuite {

	protected Membrane membrane;
	protected List<Structure> structures;
	
	public MembraneStructureSuite(Feature outsideFeature, Membrane membrane, Feature insideFeature) {
		super(createTitle(membrane));
		this.membrane = membrane;
		structures = new ListOfThree<Structure>(outsideFeature, membrane, insideFeature);
	}
	
	public List<Structure> getStructures() { return structures; }
	public boolean areReactionsShownFor(Structure structure) { return membrane.equals(structure); }
	
	public static String createTitle(Membrane membrane) {
		return "Reactions for Membrane " + membrane.getName();
	}
		
}
