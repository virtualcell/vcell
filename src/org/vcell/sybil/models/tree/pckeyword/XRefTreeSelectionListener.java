package org.vcell.sybil.models.tree.pckeyword;

/*   XRefTreeSelectionListener  --- by Oliver Ruebenacker, UCHC --- December 2009 to January 2010
 *   Listen to Pathway Commons response tree for the selection of an xref
 */

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import org.vcell.sybil.util.event.Accepter;
import org.vcell.sybil.util.event.Resetable;
import org.vcell.sybil.util.http.pathwaycommons.search.XRef;
import org.vcell.sybil.util.tree.TreeUtil;

public class XRefTreeSelectionListener implements TreeSelectionListener {

	protected Accepter<XRef> xRefAccepter;
	
	public XRefTreeSelectionListener(Accepter<XRef> xRefAccepter) {
		this.xRefAccepter = xRefAccepter;
	}

	public void valueChanged(TreeSelectionEvent event) {
		Object userObject = TreeUtil.leadSelectedUserObject(event);
		if(userObject instanceof XRefWrapper) {
			xRefAccepter.accept(((XRefWrapper) userObject).xRef());
		} else {
			if(xRefAccepter instanceof Resetable) {
				((Resetable) xRefAccepter).reset();
			}
		}
	}

}
