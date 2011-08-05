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

/*   ProcessModelImp  --- by Oliver Ruebenacker, UCHC --- June to November 2009
 *   A view of a resource representing an SBPAX process model
 */

import org.openrdf.model.Resource;
import org.vcell.sybil.models.sbbox.SBBox;

public class ProcessModelImp extends SBWrapper implements SBBox.NamedThing {

	public ProcessModelImp(SBBox box, Resource resource) { super(box, resource); }

}
