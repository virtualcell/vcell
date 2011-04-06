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

	@Override
	public XRef data() { return super.data(); }
	public XRef xRef() { return super.data(); }
	
	@Override
	public String toString() {
		String string = xRef().db() + ":" + xRef().id();
		String url = xRef().url();
		if(StringUtil.notEmpty(url)) {
			string = string + ", " + url;
		}
		return string;
	}
	
}
