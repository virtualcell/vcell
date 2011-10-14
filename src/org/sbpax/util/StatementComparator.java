/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.sbpax.util;

import java.util.Comparator;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;

public class StatementComparator implements Comparator<Statement> {

	public int rank(Value value) {
		if(value instanceof Resource) {
			if(value instanceof URI) {
				String uriString = value.stringValue();
				if(uriString.contains(RDF.NAMESPACE)) { return 0; }
				else if(uriString.contains(RDFS.NAMESPACE)) { return 1; }
				else if(uriString.contains(OWL.NAMESPACE)) { return 2; }
				else { return 3; }
			} else { return 4; }
		} else { return 5; }
	}
	
	public int compare(Value value1, Value value2) {
		int comparison = rank(value1) - rank(value2);
		if(comparison == 0) {
			comparison = value1.stringValue().compareTo(value2.stringValue());
		}
		return comparison;
	}
	
	public int compare(Statement statement1, Statement statement2) {
		int comparison = compare(statement1.getSubject(), statement2.getSubject());
		if(comparison == 0) {
			comparison = compare(statement1.getPredicate(), statement2.getPredicate());			
			if(comparison == 0) {
				comparison = compare(statement1.getObject(), statement2.getObject());			
			}
		}
		return comparison;
	}
	
}
