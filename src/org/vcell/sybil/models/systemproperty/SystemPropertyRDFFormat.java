/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.systemproperty;

/*   SystemPropertyRDFFormat  --- by Oliver Ruebenacker, UCHC --- March 2010
 *   Stores a global model write style
 */

import org.vcell.sybil.rdf.RDFFormat;

public class SystemPropertyRDFFormat {

	public static final String key = SystemPropertySybil.key + ".io.rdfformat";
	public static final RDFFormat rdfFormatDefault = RDFFormat.N3;
	
	public static void setRDFFormat(RDFFormat format) { System.setProperty(key, format.toString()); }
	
	public static RDFFormat rdfFormat() { 
		String value = System.getProperty(key);
		RDFFormat rdfFormat = null;
		if(value != null) { rdfFormat = new RDFFormat(value); } 
		else { rdfFormat = rdfFormatDefault; }
		return rdfFormat;
	}
	
}
