/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.sbpax.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

public class RDFGraphMatchIterator implements Iterator<Statement> {

	protected final Graph graph;
	protected final Iterator<Statement> iterator;
	protected Statement current;
	
	public RDFGraphMatchIterator(Graph graph, Resource subject, URI predicate, Value object) {
		this.graph = graph;
		List<Statement> list = new ArrayList<Statement>();
		for(Statement statement : graph) {
			if(subject != null && !subject.equals(statement.getSubject())) { continue; }
			if(predicate != null && !predicate.equals(statement.getPredicate())) { continue; }
			if(object != null && !object.equals(statement.getObject())) { continue; }
			list.add(statement);
		}
		this.iterator = list.iterator();
	}
	
	public boolean hasNext() { return iterator.hasNext(); }

	public Statement next() { 
		current = iterator.next();
		return current; 
	}

	public void remove() {
		iterator.remove();
		graph.remove(current);
	}

}
