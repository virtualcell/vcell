/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.graphcomponents;

/*   SyCo  --- by Oliver Ruebenacker, UCHC --- August 2007 to November 2009
 *   A component of a graph, corresponding to a set of Resources and Statements
 */

import java.util.Set ;

import org.vcell.sybil.models.graphcomponents.tag.RDFGraphCompTag;
import org.vcell.sybil.models.sbbox.SBBox.NamedThing;

import com.hp.hpl.jena.rdf.model.Statement;

public interface RDFGraphComponent {
	
	public RDFGraphCompTag tag();
	public Set<RDFGraphComponent> dependencies();
	public Set<RDFGraphComponent> children();
	public Set<NamedThing> things();
	public Set<Statement> statements();	
	public String name();
	public String label();
}
