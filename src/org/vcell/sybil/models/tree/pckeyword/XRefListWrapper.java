package org.vcell.sybil.models.tree.pckeyword;

/*   XRefListWrapper  --- by Oliver Ruebenacker, UCHC --- December 2009
 *   Wrapper for a tree node with a (Pathway Commons) XRef list
 */

import java.util.List;
import org.vcell.sybil.models.tree.NodeDataWrapper;
import org.vcell.sybil.util.http.pathwaycommons.search.XRef;
import org.vcell.sybil.util.text.NumberText;

public class XRefListWrapper extends NodeDataWrapper<List<XRef>> {

	public XRefListWrapper(List<XRef> xRefs) {
		super(xRefs);
		for(XRef xRef : xRefs) { append(new XRefWrapper(xRef)); }
	}

	public List<XRef> data() { return (List<XRef>) super.data(); }
	public List<XRef> xRefs() { return (List<XRef>) super.data(); }
	
	public String toString() {
		return NumberText.soMany(xRefs().size(), "cross reference");
	}
	
}
