package org.vcell.sybil.models.tree.pckeyword;

/*   XRefTreeSelectionListener  --- by Oliver Ruebenacker, UCHC --- December 2009
 *   Launch a web request using command search from Pathway Commons
 */

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.vcell.sybil.util.event.Accepter;
import org.vcell.sybil.util.http.pathwaycommons.search.XRef;

public class XRefTreeSelectionListener implements TreeSelectionListener {

	protected Accepter<XRef> xRefAccepter;
	
	public XRefTreeSelectionListener(Accepter<XRef> xRefAccepter) {
		this.xRefAccepter = xRefAccepter;
	}
	
	public void valueChanged(TreeSelectionEvent event) {
		TreePath selectionPath = event.getNewLeadSelectionPath();
		if(selectionPath != null) {
			Object lastPathComponent = selectionPath.getLastPathComponent();
			if(lastPathComponent instanceof DefaultMutableTreeNode) {
				Object userObject = ((DefaultMutableTreeNode) lastPathComponent).getUserObject();
				if(userObject instanceof XRefWrapper) {
					xRefAccepter.accept(((XRefWrapper) userObject).xRef());
				}
			}
		}
	}

}
