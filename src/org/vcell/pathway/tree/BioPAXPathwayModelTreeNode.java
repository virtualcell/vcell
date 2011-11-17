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
import java.util.Map;
import java.util.Set;

import javax.swing.tree.TreeNode;

import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.Pathway;
import org.vcell.pathway.PathwayModel;

public class BioPAXPathwayModelTreeNode extends BioPAXTreeNode {

	public BioPAXPathwayModelTreeNode(PathwayModel pathwayModel, TreeNode parent) {
		super(pathwayModel, pathwayModel, parent);
	}

	public List<BioPAXTreeNode> getNewChildren() {
		Set<BioPaxObject> objects = getPathwayModel().getBiopaxObjects();
		Map<Class<?>, Set<BioPaxObject>> subclassMap = BioPAXTreeMaker.divideBySubclasses(BioPaxObject.class, objects);
		List<BioPAXTreeNode> childrenNew = new ArrayList<BioPAXTreeNode>();
		for(Map.Entry<Class<?>, Set<BioPaxObject>> entry : subclassMap.entrySet()) {
			Class<?> subclass = entry.getKey();
			Set<BioPaxObject> objectsOfSubclass = entry.getValue();
			List<BioPaxObject> objectsOfSubclassList = new ArrayList<BioPaxObject>();
			objectsOfSubclassList.addAll(objectsOfSubclass);
			childrenNew.add(new BioPAXObjectListTreeNode<BioPaxObject>(getPathwayModel(), objectsOfSubclassList, subclass, this));
		}
//		for(BioPaxObject object : objects) {
//			children.add(
//					new BioPAXMessageTreeNode(getPathwayModel(), object.toString(), this));
//		}
		// TODO
		int nObjects = pathwayModel.getBiopaxObjects().size();
		Pathway topLevelPathway = pathwayModel.getTopLevelPathway();
		if(topLevelPathway != null) {
			List<String> nameList = topLevelPathway.getName();
			if(nameList != null && !nameList.isEmpty()) { }
		}
		labelText = nObjects + " objects";
		return childrenNew;
	}
	
	
	
}
