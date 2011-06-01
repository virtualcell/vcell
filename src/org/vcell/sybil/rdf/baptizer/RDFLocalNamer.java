/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.rdf.baptizer;

/*   RDFLocalNameGenerator  --- by Oliver Ruebenacker, UCHC --- June 2009
 *   Creates a new local name for (e.g. anonymous) resources
 */

import com.hp.hpl.jena.rdf.model.Resource;

public interface RDFLocalNamer {
	
	public String newLocalName(Resource resource);

}
