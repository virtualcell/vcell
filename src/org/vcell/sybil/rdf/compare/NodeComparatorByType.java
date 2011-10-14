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

/*   NodeComparatorByClass  --- by Oliver Ruebenacker, UCHC --- February 2009
 *   A comparator for RDF nodes by type (URI node, anon node, literal)
 */

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.vcell.sybil.util.comparator.ComparatorByScore;

public class NodeComparatorByType extends ComparatorByScore<Value> {
	@Override
	public int score(Value node) {
		if(node instanceof URI) { return 2; }
		else if(node instanceof Resource) { return 1; }
		else { return 0; }
	}
}
