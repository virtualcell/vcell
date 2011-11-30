/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

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
		ArrayList<String> names = getEntity().getName();
		if(names != null && names.size() > 0) {			
			childrenNew.add(new BioPAXNameListTreeNode(getPathwayModel(), 
					names, getParent()));
		}
		ArrayList<SBEntity> sbSubEntities = getEntity().getSBSubEntity();
		if(sbSubEntities != null && sbSubEntities.size() > 0) {
			childrenNew.add(new BioPAXObjectListTreeNode<SBEntity>(getPathwayModel(), 
					sbSubEntities, SBEntity.class, this));			
		}
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
