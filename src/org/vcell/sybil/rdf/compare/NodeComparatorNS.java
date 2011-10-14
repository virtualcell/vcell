/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.rdf.compare;

/*   NodeComparatorNS  --- by Oliver Ruebenacker, UCHC --- July 2009
 *   A comparator for RDF nodes by type and preferring a default name space
 */

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.vcell.sybil.util.comparator.ComparatorByScore;

public class NodeComparatorNS extends ComparatorByScore<Value> {
	
	protected String namespace;
	
	public NodeComparatorNS(String namespace) { this.namespace = namespace; }
	
	@Override
	public int score(Value node) {
		int score = 0;
		if(node instanceof Resource) {
			Resource resource = (Resource) node;
			if(resource instanceof URI) {
				if(namespace.equals(((URI) resource).getNamespace())) { score = 3; } 
				else { score = 2; }
			} else {
				score = 1;
			}
		}
		return score;
	}
}
