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

/*   PropertyComparatorBioPAX2  --- by Oliver Ruebenacker, UCHC --- February 2009
 *   A comparator for BioPAX2 properties
 */

import org.vcell.sybil.rdf.schemas.BioPAX2;
import org.vcell.sybil.util.comparator.ComparatorScore;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class PropertyComparatorBioPAX2 extends ComparatorScore<Property> {

	
	public int score(Property p) {
		if(RDF.type.equals(p)) { return 5; }
		else if(RDFS.label.equals(p)) { return 4; }
		else if(BioPAX2.NAME.equals(p)) { return 3; }
		else if(BioPAX2.SHORT_NAME.equals(p)) { return 2; }
		else if(BioPAX2.SYNONYMS.equals(p)) { return 1; }
		else { return 0; }
	}

}
