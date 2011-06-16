/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.bpimport.edges;

/*   EdgeSBTray  --- by Oliver Ruebenacker, UCHC --- July to September 2009
 *   An SBBox and some process edges
 */

import java.util.Vector;

import org.vcell.sybil.models.sbbox.SBInferenceBox;

public class EdgeSBTray  {

	protected SBInferenceBox box;
	protected Vector<MutableEdge> edges = new Vector<MutableEdge>();

	public EdgeSBTray(SBInferenceBox box) { 
		this.box = box;
	}
	
	public SBInferenceBox box() { return box; }
	public Vector<MutableEdge> edges() { return edges; }
		
}
