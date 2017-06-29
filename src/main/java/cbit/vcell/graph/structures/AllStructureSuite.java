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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cbit.vcell.model.Diagram;
import cbit.vcell.model.Model;
import cbit.vcell.model.Structure;
import cbit.vcell.model.StructureSorter;

public class AllStructureSuite extends StructureSuite {

	public static final String TITLE = "Reactions for all Structures";
	
	protected final Model.Owner modelOwner;
	private boolean bModelStructureOrder = true;

	public AllStructureSuite(Model.Owner modelOwner) {
		super(TITLE);
		this.modelOwner = modelOwner;
	}
	
	public List<Structure> getStructures() {
		if(bModelStructureOrder){
			ArrayList<Structure> modelStructures = new ArrayList<Structure>();
			Diagram[] modelDiagrams = modelOwner.getModel().getDiagrams();
			for(Diagram diagram:modelDiagrams){
				modelStructures.add(diagram.getStructure());
			}
			return modelStructures;
		}
		return Arrays.asList(StructureSorter.sortStructures(modelOwner.getModel()));
	}
	
	public void setModelStructureOrder(boolean bModelStructureOrder){
		this.bModelStructureOrder = bModelStructureOrder;
	}
	public boolean getModelStructureOrder(){
		return this.bModelStructureOrder;
	}
	@Override
	public boolean areReactionsShownFor(Structure structure) { return true; }
	
}
