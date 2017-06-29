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

/*  The structures displayed by a reaction cartoon
 *   November 2010
 */

import java.util.Collection;

import cbit.vcell.model.Structure;

public abstract class StructureSuite {

	protected final String title;
	
	public StructureSuite(String title) {
		this.title = title;
	}
	
	public String getTitle() { return title; }
	public abstract Collection<Structure> getStructures();
	public abstract boolean areReactionsShownFor(Structure structure);

	public boolean equals(Object object) {
		if(object instanceof StructureSuite) {
			StructureSuite suite = (StructureSuite) object;
			return title.equals(suite.getTitle());
		}
		return false;
	}
	
	public int hashCode() {
		return title.hashCode();
	}
	
}
