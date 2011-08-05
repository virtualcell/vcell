/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.http.uniprot.box.imp;

/*   UniProtBox  --- by Oliver Ruebenacker, UCHC --- January 2010
 *   A wrapper for a UniProt entry
 */

import org.vcell.sybil.util.http.uniprot.box.UniProtBox;

public class ThingImp implements UniProtBox.Thing {

	protected final UniProtBox box;
	
	public ThingImp(UniProtBox box) { this.box = box; }

	public UniProtBox box() { return box; }

}
