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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.sbpax.util.StringUtil;
import org.vcell.pathway.Entity;
import org.vcell.pathway.Pathway;
import org.vcell.pathway.PathwayModel;
import org.vcell.pathway.sbpax.SBEntity;
import org.vcell.pathway.sbpax.SBMeasurable;

public class BioPAXTreeMaker {

	public static TreeModel makeEmptyTree() {
		TreeNode rootNode = 
			new BioPAXMessageTreeNode(null, "[Open pathway to view tree]", null);
		return new DefaultTreeModel(rootNode, true);
	}
	
	public static TreeModel makeTree(PathwayModel pathwayModel) {
		if(pathwayModel == null || pathwayModel.getBiopaxObjects().isEmpty()) {
			return makeEmptyTree();
		}
		TreeNode rootNode = new BioPAXPathwayModelTreeNode(pathwayModel, null);
		return new DefaultTreeModel(rootNode, true);
	}
	
	public static void printClasses(String intro, Collection<Class<?>> classes) {
		Set<String> classNames = new HashSet<String>();
		for(Class<?> aClass : classes) { classNames.add(aClass.getSimpleName()); }
		System.out.println(intro + StringUtil.concat(classNames, ", "));
	}
	
	public static <T> Map<Class<?>, Set<T>> divideBySubclasses(Class<?> baseClass, Collection<T> objects) {
		HashSet<Class<?>> classes = new HashSet<Class<?>>();
		for(T object : objects) {
			Class<?> aClass = object.getClass();
			if(!baseClass.equals(aClass)) { classes.add(aClass); }
		}
		boolean thereMayBeMoreSuperclasses = true;
		while(thereMayBeMoreSuperclasses) {
			HashSet<Class<?>> superclasses = new HashSet<Class<?>>();
			for(Class<?> aClass : classes) {
				Class<?> superclass = aClass.getSuperclass();
				if(superclass != null && !baseClass.equals(superclass) && baseClass.isAssignableFrom(superclass) 
						&& !classes.contains(superclass)) { 
					superclasses.add(superclass); 
				}
				for(Class<?> superinterface : aClass.getInterfaces()) {
					if(!baseClass.equals(superinterface) && baseClass.isAssignableFrom(superinterface) 
							&& !classes.contains(superinterface)) { 
						superclasses.add(superinterface); 
					}
				}
			}
			classes.addAll(superclasses);
			thereMayBeMoreSuperclasses = !superclasses.isEmpty();
		};
		Set<Class<?>> classesToRemove = new HashSet<Class<?>>();
		for(Class<?> superclass : classes) {
			String name = superclass.getName();
			if(name.endsWith("Impl") || name.endsWith("Proxy")) {
				classesToRemove.add(superclass);
			}
		}
		classes.removeAll(classesToRemove);
		Set<Class<?>> classesToRemove2 = new HashSet<Class<?>>();
		for(Class<?> superclass1 : classes) {
			for(Class<?> superclass2 : classes) {
				if(!superclass1.equals(superclass2) && superclass1.isAssignableFrom(superclass2)) {
					classesToRemove2.add(superclass2);
				}
			}
		}
		classes.removeAll(classesToRemove2);
		HashMap<Class<?>, Set<T>> map = new HashMap<Class<?>, Set<T>>();
		for(T object : objects) {
			boolean objectIsSubclass = false;
			for(Class<?> aClass : classes) {
				if(aClass.isInstance(object)) {
					objectIsSubclass = true;
					Set<T> instances = map.get(aClass);
					if(instances == null) {
						instances = new HashSet<T>();
						map.put(aClass, instances);
					}
					instances.add(object);
				}
			}
			if(!objectIsSubclass) {
				Set<T> instances = map.get(baseClass);
				if(instances == null) {
					instances = new HashSet<T>();
					map.put(baseClass, instances);
				}
				instances.add(object);				
			}
		}
		return map;
	}
	
	public static void updateAndRecycleChildren(List<BioPAXTreeNode> children, 
			Collection<BioPAXTreeNode> childrenNew) {
		Set<Object> objectsNew = new HashSet<Object>();
		for(BioPAXTreeNode childNew : childrenNew) {
			objectsNew.add(childNew.getObject());
		}
		Set<Object> objects = new HashSet<Object>();
		Iterator<BioPAXTreeNode> childrenIter = children.iterator();
		while(childrenIter.hasNext()) {
			BioPAXTreeNode child = childrenIter.next();
			Object object = child.getObject();
			if(!objectsNew.contains(object)) {
				childrenIter.remove();
			} else {
				objects.add(object);
			}
		}
		for(BioPAXTreeNode childNew : childrenNew) {
			Object objectNew = childNew.getObject();
			if(!objects.contains(objectNew)) {
				children.add(childNew);
			}
		}
	}
	
	public static <E> List<E> sort(Collection<E> objects) {
		ArrayList<E> list = new ArrayList<E>();
		for(E object : objects) {
			list.add(object);
		}
		// TODO do some actual sorting
		return list;
	}
	
	public static BioPAXTreeNode getNodeForBioPaxObject(PathwayModel pathwayModel,
			Object object, TreeNode parent) {
		BioPAXTreeNode treeNode;
		if(object instanceof PathwayModel) {
			treeNode = new BioPAXPathwayModelTreeNode((PathwayModel) object, parent);
		} else if(object instanceof Pathway) {
			treeNode = new BioPAXPathwayTreeNode(pathwayModel, (Pathway) object, parent);
		} else if(object instanceof Entity) {
			treeNode = new BioPAXEntityTreeNode(pathwayModel, (Entity) object, parent);
		} else if(object instanceof SBMeasurable) {
			treeNode = new BioPAXSBMeasurableTreeNode(pathwayModel, (SBMeasurable) object, parent);
		} else if(object instanceof SBEntity) {
			treeNode = new BioPAXSBEntityTreeNode(pathwayModel, (SBEntity) object, parent);
		} else if(object instanceof String) {
			treeNode = new BioPAXMessageTreeNode(pathwayModel, (String) object, parent);			
		} else {
			treeNode = new BioPAXTreeNode(pathwayModel, object, parent);			
		}
		return treeNode;
	}
	
}
