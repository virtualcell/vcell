/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.uome.publish;

import java.util.Comparator;

import org.openrdf.model.URI;

public class URIComparator implements Comparator<URI> {

	public int compare(URI uri0, URI uri1) {
		return uri0.toString().compareTo(uri1.toString());
	}

}
