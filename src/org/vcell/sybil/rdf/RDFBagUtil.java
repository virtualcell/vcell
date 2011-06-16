/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.rdf;

/*   RDFBag  --- by Oliver Ruebenacker, UCHC --- March 2010
 *   A wrapper for RDF bags
 */

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.vocabulary.RDF;

public class RDFBagUtil {

	public static Property memberProperty(int index) { return RDF.li(index); }
	
	public static boolean isRDFBagMemberProperty(Property property) {
		boolean isMemberProperty = false;
		String uri = property.getURI();
		String[] splits = uri.split("_");
		if(splits.length == 2 && splits[0].equals(RDF.getURI()) && Integer.valueOf(splits[1]) > 0) {
			isMemberProperty = true;
		}
		return isMemberProperty;
	}
	
	public static int memberPropertyIndex(Property property) {
		int index = -1;
		String uri = property.getURI();
		String[] splits = uri.split("_");
		if(splits.length == 2 && splits[0].equals(RDF.getURI())) { index = Integer.valueOf(splits[1]); }
		return index;
	}
	
}
