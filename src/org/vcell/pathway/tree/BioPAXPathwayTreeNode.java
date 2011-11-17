package org.vcell.pathway.tree;

import javax.swing.tree.TreeNode;

import org.vcell.pathway.Pathway;
import org.vcell.pathway.PathwayModel;

public class BioPAXPathwayTreeNode extends BioPAXEntityTreeNode {

	public BioPAXPathwayTreeNode(PathwayModel pathwayModel, Pathway pathway,
			TreeNode parent) {
		super(pathwayModel, pathway, parent);
	}

	public Pathway getPathway() { return (Pathway) getEntity(); }
	
}
