package org.vcell.sybil.models.tree.pckeyword;

/*   XRefListWrapper  --- by Oliver Ruebenacker, UCHC --- December 2009 to January 2010
 *   Wrapper for a tree node with a (Pathway Commons) XRef list
 */

import java.util.List;
import java.util.Vector;

import org.vcell.sybil.models.tree.NodeDataWrapper;
import org.vcell.sybil.util.http.pathwaycommons.search.XRef;
import org.vcell.sybil.util.text.NumberText;

public class XRefObsoleteListWrapper extends NodeDataWrapper<List<XRef>> {

	public XRefObsoleteListWrapper() {
		super(new Vector<XRef>());
	}

	public void add(XRef xRef) {
		data().add(xRef);
		append(new XRefWrapper(xRef));
	}
	
	@Override
	public List<XRef> data() { return super.data(); }
	public List<XRef> xRefs() { return super.data(); }
	
	@Override
	public String toString() {
		return NumberText.soMany(xRefs().size(), "obsolete cross reference");
	}
	
}
