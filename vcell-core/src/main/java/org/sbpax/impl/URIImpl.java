/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.sbpax.impl;

import org.openrdf.model.URI;
import org.vcell.pathway.id.URIUtil;

@SuppressWarnings("serial")
public class URIImpl implements URI {

	protected final String uri;
	
	public URIImpl(String uri) { this.uri = uri; }
	
	public String stringValue() { return uri; }
	public String getLocalName() { return URIUtil.getLocalName(uri); }
	public String getNamespace() { return URIUtil.getNameSpace(uri); }
	public int hashCode() { return uri.hashCode(); }
	public boolean equals(Object o) { return o instanceof URI && uri.equals(((URI) o).stringValue().equals(uri)); }
	public String toString() { return uri; }

}
