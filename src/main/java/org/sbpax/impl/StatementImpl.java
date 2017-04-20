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

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

@SuppressWarnings("serial")
public class StatementImpl implements Statement {

	protected final Resource subject;
	protected final URI predicate;
	protected final Value object;
	
	public StatementImpl(Resource subject, URI predicate, Value object) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}
	
	public Resource getContext() { return null; }
	public Resource getSubject() { return subject; }
	public URI getPredicate() { return predicate; }
	public Value getObject() { return object; }
	public int hashCode() { return 961*subject.hashCode() + 31*predicate.hashCode() + object.hashCode(); }
	
	public boolean equals(Object o) {
		if(o instanceof Statement) {
			Statement os = (Statement) o;
			return subject.equals(os.getSubject()) && predicate.equals(os.getPredicate()) && object.equals(os.getObject());
		}
		return false;
	}
	
	public String toString() { return "(" + subject + " " + predicate + " " + object + ")"; }

}
