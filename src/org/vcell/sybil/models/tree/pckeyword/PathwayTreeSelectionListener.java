package org.vcell.sybil.models.tree.pckeyword;

/*   XRefTreeSelectionListener  --- by Oliver Ruebenacker, UCHC --- December 2009 to January 2010
 *   Listen to Pathway Commons response tree for the selection of an xref
 */

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import org.vcell.sybil.util.event.Accepter;
import org.vcell.sybil.util.event.Resetable;
import org.vcell.sybil.util.http.pathwaycommons.search.Pathway;
import org.vcell.sybil.util.tree.TreeUtil;

public class PathwayTreeSelectionListener implements TreeSelectionListener {

	protected Accepter<Pathway> pathwayAccepter;
	
	public PathwayTreeSelectionListener(Accepter<Pathway> pathwayAccepter) {
		this.pathwayAccepter = pathwayAccepter;
	}

	public void valueChanged(TreeSelectionEvent event) {
		Object userObject = TreeUtil.leadSelectedUserObject(event);
		if(userObject instanceof PathwayWrapper) {
			pathwayAccepter.accept(((PathwayWrapper) userObject).pathway());
		} else {
			if(pathwayAccepter instanceof Resetable) {
				((Resetable) pathwayAccepter).reset();
			}
		}
	}

}
