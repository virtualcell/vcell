package org.vcell.pathway.tree;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreeNode;

import org.vcell.pathway.PathwayModel;
import org.vcell.sybil.util.text.NumberText;

public class BioPAXNameListTreeNode extends BioPAXTreeNode {

	public BioPAXNameListTreeNode(PathwayModel pathwayModel,
			List<String> nameList, TreeNode parent) {
		super(pathwayModel, nameList, parent);
	}

	@SuppressWarnings("unchecked")
	public List<String> getNameList() { return (List<String>) getObject(); }
	public List<BioPAXTreeNode> getNewChildren() {
		ArrayList<BioPAXTreeNode> childrenNew = new ArrayList<BioPAXTreeNode>();
		for(String name : getNameList()) {
			childrenNew.add(new BioPAXMessageTreeNode(getPathwayModel(), name, this));
		}
		labelText = trimLabelIfNeeded(NumberText.soManyThings(getNameList(), "name"));
		return childrenNew;
	}
	
}
