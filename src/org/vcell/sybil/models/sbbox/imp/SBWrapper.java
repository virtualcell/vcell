/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.sbbox.imp;

/*   SBWrapper  --- by Oliver Ruebenacker, UCHC --- June 2009 to April 2010
 *   A view of a resource representing an SBPAX object
 */

import org.openrdf.model.Resource;
import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.rdf.RDFBox.ResourceWrapper;

public class SBWrapper extends ResourceWrapper implements SBBox.NamedThing {
	
	public SBWrapper(SBBox box, Resource resource) { super(box, resource); }

	@Override
	public SBBox box() { return (SBBox) super.box(); }

}
