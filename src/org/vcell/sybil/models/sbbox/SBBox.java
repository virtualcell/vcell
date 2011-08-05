/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.sbbox;

/*   SBBox  --- by Oliver Ruebenacker, UCHC --- June 2008 to April 2010
 *   Organizes the RDF data and structures to edit it
 */

import org.vcell.sybil.models.sbbox.factories.Factories;
import org.vcell.sybil.rdf.RDFBox;

public interface SBBox extends RDFBox {
	
	public static interface NamedThing extends RDFThing {
		public SBBox box();
	}
	
	public Factories factories();
		
}
