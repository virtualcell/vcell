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

/*   SyCoResourceGroup  --- by Oliver Ruebenacker, UCHC --- March 2008 to November 2009
 *   A component of a graph, corresponding to a set of Resources and Statements, with a primary Resource
 */

import java.util.HashSet;
import java.util.Set;

import org.vcell.sybil.models.graphcomponents.tag.RDFGraphCompTag;
import org.vcell.sybil.models.sbbox.SBBox.NamedThing;
import org.vcell.sybil.util.sets.SetOfNone;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

public class RDFGraphCompThingGroup implements RDFGraphCompThing, RDFGraphCompContainer {

	protected RDFGraphCompThing syCoResource;
	protected Set<RDFGraphComponent> children;
	protected RDFGraphCompTag tag;
	
	public RDFGraphCompThingGroup(RDFGraphCompThing syCoResource, RDFGraphCompTag tagNew) {
		this.syCoResource = syCoResource;
		children = new HashSet<RDFGraphComponent>();
		children.add(syCoResource);
		tag = tagNew;
	}
	
	public RDFGraphCompTag tag() { return tag; }
	public RDFGraphCompThing syCoResource() { return syCoResource; }
	public String label() { return syCoResource.label(); }
	public String name() { return toString(); }
	public Resource type() { return syCoResource.type(); }
	public NamedThing thing() { return syCoResource.thing(); }
	
	public Set<RDFGraphComponent> children() { return children; }

	public Set<NamedThing> things() {
		HashSet<NamedThing> things = new HashSet<NamedThing>();
		for(RDFGraphComponent child : children) { things.addAll(child.things()); }
		return things;
	}
	
	public Set<RDFGraphComponent> dependencies() { return new SetOfNone<RDFGraphComponent>(); }
	
	public Set<Statement> statements() {
		HashSet<Statement> statements = new HashSet<Statement>();
		for(RDFGraphComponent child : children) { statements.addAll(child.statements()); }
		return statements; 		
	}

}
