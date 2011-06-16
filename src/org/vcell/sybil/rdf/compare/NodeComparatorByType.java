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

import org.vcell.sybil.util.comparator.ComparatorScore;

import com.hp.hpl.jena.rdf.model.RDFNode;

public class NodeComparatorByType extends ComparatorScore<RDFNode> {
	@Override
	public int score(RDFNode node) {
		if(node.isURIResource()) { return 2; }
		else if(node.isResource()) { return 1; }
		else { return 0; }
	}
}
