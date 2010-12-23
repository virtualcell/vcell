package org.vcell.util.gui;

import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import cbit.vcell.desktop.BioModelNode;

public class GuiUtils {
	public static void treeExpandAll(JTree tree, BioModelNode treeNode, boolean bExpand) {
		int childCount = treeNode.getChildCount();
		if (childCount > 0) {
			for (int i = 0; i < childCount; i++) {
				TreeNode n = treeNode.getChildAt(i);
				if (n instanceof BioModelNode) {
					treeExpandAll(tree, (BioModelNode)n, bExpand);
				}
			}
			if (!bExpand) {
				tree.collapsePath(new TreePath(treeNode.getPath()));
			}
		} else {
			TreePath path = new TreePath(treeNode.getPath());
			if (bExpand && !tree.isExpanded(path)) {
				tree.expandPath(path.getParentPath());
			} 
		}
	}
}
