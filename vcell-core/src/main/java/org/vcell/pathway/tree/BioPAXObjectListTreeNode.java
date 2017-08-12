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

import org.sbpax.util.StringUtil;
import org.vcell.pathway.PathwayModel;
import org.vcell.sybil.util.text.NumberText;

public class BioPAXObjectListTreeNode<E> extends BioPAXTreeNode {

	protected final Class<?> baseClass;
	
	public BioPAXObjectListTreeNode(PathwayModel pathwayModel, List<E> list, Class<?> baseClass, TreeNode parent) {
		super(pathwayModel, list, parent);
		this.baseClass = baseClass;
	}

	@SuppressWarnings("unchecked")
	public List<E> getList() { return (List<E>) getObject(); }
	public Class<?> getBaseClass() { return baseClass; }
	
	public List<BioPAXTreeNode> getNewChildren() {
		List<BioPAXTreeNode> childrenNew = new ArrayList<BioPAXTreeNode>();
		List<E> list = getList();
		Map<Class<?>, Set<E>> subClassMap = 
			BioPAXTreeMaker.divideBySubclasses(baseClass, list);
		List<String> subclassStats = new ArrayList<String>();
		boolean thereAreSubclasses = false;
		for(Class<?> subclass : subClassMap.keySet()) {
			if(!baseClass.equals(subclass)) {
				thereAreSubclasses = true;
				Set<E> objects = subClassMap.get(subclass);
				if(objects != null && !objects.isEmpty()) {
					childrenNew.add(
							new BioPAXObjectListTreeNode<E>(pathwayModel, 
									BioPAXTreeMaker.sort(objects), subclass, this));
					NameWithPlural className = 
						BioPAXClassNameDirectory.getClassName(subclass);
					subclassStats.add(NumberText.soMany(objects.size(), 
							className.getSingular(), className.getPlural()));
				}
			}
		}
		Set<E> objects = subClassMap.get(baseClass);
		if(objects != null) {
			for(Object object : objects) {
				childrenNew.add(
						BioPAXTreeMaker.getNodeForBioPaxObject(pathwayModel, object, 
								this));				
			}
			subclassStats.add(NumberText.soMany(objects.size(), "other"));
		}		
		// TODO objects of type base class
		NameWithPlural className = BioPAXClassNameDirectory.getClassName(getBaseClass());
		if(thereAreSubclasses) {
			String soMany = 
				NumberText.soMany(getList().size(), className.getSingular(), 
						className.getPlural()) + " (" + 
						StringUtil.concat(subclassStats, ", ") + ")";
			labelText = trimLabelIfNeeded(soMany);			
		} else {
			List<String> simplifiedLabels = new ArrayList<String>();
			for(BioPAXTreeNode childNew : childrenNew) {
				simplifiedLabels.add(childNew.getSimplifiedLabel());
			}
			String soMany = 
				NumberText.soManyThings(simplifiedLabels, className.getSingular(), 
						className.getPlural());
			labelText = trimLabelIfNeeded(soMany);			
		}
		return childrenNew;
	}
	
}
