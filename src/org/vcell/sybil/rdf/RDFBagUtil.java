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

/*   RDFBag  --- by Oliver Ruebenacker, UCHC --- April 2010
 *   A wrapper for RDF bags
 */

import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;

public class RDFBagUtil {

	public static boolean isRDFContainerMembershipProperty(URI uri) {
		if(RDF.LI.equals(uri)) { return true; }
		if(uri.stringValue().contains(RDF.NAMESPACE + "_")) { return true; }
		return false;
	}
	
}
