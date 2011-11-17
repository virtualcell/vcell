package org.vcell.pathway.tree;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreeNode;

import org.sbpax.util.StringUtil;
import org.vcell.pathway.Entity;
import org.vcell.pathway.PathwayModel;
import org.vcell.pathway.sbpax.SBEntity;

public class BioPAXEntityTreeNode extends BioPAXTreeNode {

	public BioPAXEntityTreeNode(PathwayModel pathwayModel, Entity entity,
			TreeNode parent) {
		super(pathwayModel, entity, parent);
	}

	public Entity getEntity() { return (Entity) getObject(); }

	protected List<BioPAXTreeNode> getNewChildren() {
		List<BioPAXTreeNode> childrenNew = new ArrayList<BioPAXTreeNode>();
		childrenNew.add(new BioPAXNameListTreeNode(getPathwayModel(), 
				getEntity().getName(), getParent()));
		childrenNew.add(new BioPAXObjectListTreeNode<SBEntity>(getPathwayModel(), 
				getEntity().getSBSubEntity(), SBEntity.class, this));
		labelText = trimLabelIfNeeded(
				BioPAXClassNameDirectory.getNameSingular(getEntity().getClass()) + " " + 
				StringUtil.concat(getEntity().getName(), ", "));
		return childrenNew;
	}
	
	public String getSimplifiedLabel() {
		String simplifiedLabel = super.getSimplifiedLabel();
		ArrayList<String> names = getEntity().getName();
		if(names != null && !names.isEmpty() && StringUtil.notEmpty(names.get(0))) {
			simplifiedLabel = names.get(0);
		}
		return simplifiedLabel;
	}
	
}
