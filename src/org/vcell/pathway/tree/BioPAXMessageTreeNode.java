package org.vcell.pathway.tree;

import javax.swing.tree.TreeNode;

import org.vcell.pathway.PathwayModel;

public class BioPAXMessageTreeNode extends BioPAXTreeNode {

	public BioPAXMessageTreeNode(PathwayModel pathwayModel, String message,
			TreeNode parent) {
		super(pathwayModel, message, parent);
	}

	public String getMessage() { return (String) getObject(); }
	
	public String toString() { return getMessage(); }
	
}
