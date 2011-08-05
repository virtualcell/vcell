/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.sbbox.factories;

/*   SBBoxFactory  --- by Oliver Ruebenacker, UCHC --- October 2009
 *   Creates SBBox objects
 */

import org.openrdf.model.Graph;
import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.imp.SBBoxImp;
import org.vcell.sybil.rdf.impl.HashGraph;

public class SBBoxFactory {

	public static SBBox create() {
		Graph data = new HashGraph();
		return new SBBoxImp(data);
	}
	
}
