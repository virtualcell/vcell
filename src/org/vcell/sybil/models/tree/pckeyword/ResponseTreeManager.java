package org.vcell.sybil.models.tree.pckeyword;

/*   ResponseTreeMan  --- by Oliver Ruebenacker, UCHC --- December 2009
 *   Manage a tree model of Pathway Commons responses
 */

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.vcell.sybil.util.event.Accepter;
import org.vcell.sybil.util.http.pathwaycommons.PCExceptionResponse;
import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsResponse;
import org.vcell.sybil.util.http.pathwaycommons.search.PCErrorResponse;
import org.vcell.sybil.util.http.pathwaycommons.search.PCKeywordResponse;

public class ResponseTreeManager implements Accepter<PathwayCommonsResponse> {

	protected static class RootData {
		protected ResponseTreeManager treeMan;
		public RootData(ResponseTreeManager treeMan) { this.treeMan = treeMan; }
		
		public String toString() {
			int nChildren = treeMan.rootNode.getChildCount();
			String message;
			if(nChildren > 0) { 
//wei				message = nChildren + " responses. Expand to view."; 
				message = nChildren + " responses. "; // wei's code
			}
			else { message = "No responses yet. Start search or wait for response."; }
			return message;
		}
	}
	
	protected RootData rootData = new RootData(this);
	DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootData);
	DefaultTreeModel tree = new DefaultTreeModel(rootNode);
	
	public void accept(PathwayCommonsResponse response) {
		ResponseWrapper responseWrapper = null;
		if(response instanceof PCKeywordResponse) {
			responseWrapper = new KeywordResponseWrapper((PCKeywordResponse) response);
		} else if(response instanceof PCErrorResponse) {
			responseWrapper = new ErrorResponseWrapper((PCErrorResponse) response);
		} else if(response instanceof PCExceptionResponse) {
			responseWrapper = new ExceptionResponseWrapper((PCExceptionResponse) response);
		} else {
			responseWrapper = new ResponseWrapper(response);			
		}
		int nChildren = rootNode.getChildCount();
		tree.insertNodeInto(responseWrapper.node(), rootNode, nChildren);
	}
	
	public TreeModel tree() { return tree; }
	
}
