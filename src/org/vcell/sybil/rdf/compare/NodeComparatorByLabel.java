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

/*   NodeComparatorByText  --- by Oliver Ruebenacker, UCHC --- February to November 2009
 *   A comparator for RDF nodes based on text
 */

import java.util.Comparator;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

public class NodeComparatorByLabel implements Comparator<RDFNode> {

	public String text(RDFNode node) {
		if(node.isURIResource()) { return ((Resource) node).getLocalName(); } 
		else if(node.isAnon())  { return ((Resource) node).getId().getLabelString(); }
		else if(node.isLiteral()) { return ((Literal) node).getLexicalForm(); }
		else { return ""; }
	}
	
	public int compare(RDFNode node1, RDFNode node2) { 
		return text(node1).compareToIgnoreCase(text(node2));
	}

}
