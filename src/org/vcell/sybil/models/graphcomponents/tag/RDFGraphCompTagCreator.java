/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.graphcomponents.tag;

/*   SyCoTagGraphCreator  --- by Oliver Ruebenacker, UCHC --- April to October 2007
 *   Creates a graph from a Jena model
 */

import org.vcell.sybil.models.graph.GraphCreationMethod;
import org.vcell.sybil.models.graph.UIGraph;
import org.vcell.sybil.models.graph.UIShape;

import com.hp.hpl.jena.rdf.model.Model;

public class RDFGraphCompTagCreator<S extends UIShape<S>, G extends UIGraph<S, G>> 
implements RDFGraphCompTag {

	protected Model model;
	protected UIGraph<S, G> graph;
	protected GraphCreationMethod method;
	
	public RDFGraphCompTagCreator(Model model, UIGraph<S, G> graph, GraphCreationMethod method) {
		this.model = model;
		this.graph = graph;
		this.method = method;
	}
	
	public Model model() { return model; }
	public UIGraph<S, G> graph() { return graph; }
	public GraphCreationMethod method() { return method; }
}
