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

/*   StatementComparatorBioPAX2  --- by Oliver Ruebenacker, UCHC --- February 2009
 *   A comparator for statements containing BioPAX2 properties
 */

import java.util.Comparator;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Statement;

public class StatementComparatorBioPAX2 implements Comparator<Statement> {

	protected NodeComparatorByLabel compByLabel = new NodeComparatorByLabel();
	protected PropertyComparatorBioPAX2 compBioPAX = new PropertyComparatorBioPAX2();
	
	public int compare(Statement s1, Statement s2) {
		int comparison = compByLabel.compare(s1.getSubject(), s2.getSubject());
		if(comparison != 0) { return comparison; }
		Property predicate1 = s1.getPredicate();
		Property predicate2 = s2.getPredicate();
		comparison = compBioPAX.compare(predicate1, predicate2);
		if(comparison != 0) { return comparison; }
		comparison = compByLabel.compare(predicate1, predicate2);
		if(comparison != 0) { return comparison; }		
		return compByLabel.compare(s1.getObject(), s2.getObject());
	}

}
