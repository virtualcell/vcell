package org.vcell.sybil.util.tree;

import java.util.Set;
import java.util.Stack;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/*   TreeUtil  --- by Oliver Ruebenacker, UCHC --- October 2008
 *   Useful methods for node trees and tree paths
 */

public class TreeUtil {

	public static TreePath path(TreeModel tree, TreeNode node) {
		if(node != null) {
			Stack<TreeNode> nodes = new Stack<TreeNode>();
			nodes.push(node);
			while(node != null && node.getParent() instanceof TreeNode && !node.equals(tree.getRoot())) {
				node = node.getParent();
				nodes.push(node);
			}
			TreePath path = new TreePath(nodes.pop());
			while(!nodes.empty()) { path = path.pathByAddingChild(nodes.pop()); }
			return path;
		}
		return new TreePath(tree.getRoot());
	}
	
	public static TreePath longestCommonPath(TreeModel tree, Set<? extends TreeNode> nodes) {
		TreePath common = null;
		for(TreeNode node : nodes) {
			TreePath path = path(tree, node);
			if(common == null) {
				common = path;
			} else {
				while(common.getPathCount() > 0 && !common.isDescendant(path)) {
					common = common.getParentPath();
				}
			}
		}
		return common != null ? common : new TreePath(tree.getRoot());
	}

	public static Object leadSelectedUserObject(TreeSelectionEvent event) {
		TreePath selectionPath = event.getNewLeadSelectionPath();
		Object userObject = null;
		if(selectionPath != null) {
			Object lastPathComponent = selectionPath.getLastPathComponent();
			if(lastPathComponent instanceof DefaultMutableTreeNode) {
				userObject = ((DefaultMutableTreeNode) lastPathComponent).getUserObject();
			}
		}
		return userObject;
	}
	
}
