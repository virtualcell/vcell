/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.tree.pckeyword;

/*   XRefWrapper  --- by Oliver Ruebenacker, UCHC --- December 2009
 *   Wrapper for a tree node with a (Pathway Commons) XRef
 */

import org.vcell.sybil.models.tree.NodeDataWrapper;
import org.vcell.sybil.util.http.pathwaycommons.search.XRef;
import org.vcell.sybil.util.text.StringUtil;

public class XRefWrapper extends NodeDataWrapper<XRef> {

	public XRefWrapper(XRef xRefs) {
		super(xRefs);
	}

	public XRef data() { return (XRef) super.data(); }
	public XRef xRef() { return (XRef) super.data(); }
	
	public String toString() {
		String string = xRef().db() + ":" + xRef().id();
		String url = xRef().url();
		if(StringUtil.notEmpty(url)) {
			string = string + ", " + url;
		}
		return string;
	}
	
}
