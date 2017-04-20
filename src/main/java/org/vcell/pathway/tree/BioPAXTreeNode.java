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
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

import org.sbpax.util.StringUtil;
import org.vcell.pathway.PathwayModel;

public class BioPAXTreeNode implements TreeNode {
	
	public static final int MAX_LABEL_LENGTH = 70;
	
	protected boolean childrenNeedUpdating = true;
	protected final PathwayModel pathwayModel;
	protected final Object object;
	protected final TreeNode parent;
	protected final List<BioPAXTreeNode> children = new ArrayList<BioPAXTreeNode>();
	protected String labelText = "";
	
	public BioPAXTreeNode(PathwayModel pathwayModel, Object object, TreeNode parent) {
		this.pathwayModel = pathwayModel;
		this.object = object;
		this.parent = parent;
	}

	public Enumeration<BioPAXTreeNode> children() {  
		updateChildrenIfNeeded();
		return Collections.enumeration(children); 
	}
	
	public boolean getAllowsChildren() { return true; }
	
	public TreeNode getChildAt(int index) { 
		updateChildrenIfNeeded();
		return children.get(index); 
	}
	
	public int getChildCount() { 
		updateChildrenIfNeeded();
		return children.size(); 
	}
	
	public int getIndex(TreeNode node) { 
		updateChildrenIfNeeded();
		return children.indexOf(node); 
	}
	
	public TreeNode getParent() { return parent; }
	public boolean isLeaf() { return false; }

	public Object getObject() { return object; }
	public PathwayModel getPathwayModel() { return pathwayModel; }
	
	public String getLabelText() { 
		updateChildrenIfNeeded();
		return labelText; 
	}
	
	public String toString() { return "<html>" + getLabelText() + "</html>"; }
	
	public final void updateChildrenIfNeeded() {
		if(childrenNeedUpdating) { 
			updateChildren(); 
		}
	}
	
	public final void setChildrenNeedUpdating(boolean childrenNeedUpdating) {
		this.childrenNeedUpdating = childrenNeedUpdating;
	}
	
	public final void updateChildren() { 
		doUpdateChildren();
		childrenNeedUpdating = false;
	}
	
	public String getSimplifiedLabel() {
		return BioPAXClassNameDirectory.getNameSingular(getObject().getClass());
	}
	
	protected List<BioPAXTreeNode> getNewChildren() {
		if(StringUtil.isEmpty(labelText)) {
			labelText = getSimplifiedLabel();
		}
		return new ArrayList<BioPAXTreeNode>();
	}
	
	protected final void doUpdateChildren() {
		BioPAXTreeMaker.updateAndRecycleChildren(children, getNewChildren());
	}
	
	public static String trimLabelIfNeeded(String label) {
		if(label.length() > MAX_LABEL_LENGTH) {
			return label.substring(0, MAX_LABEL_LENGTH - 4) + "...";
		}
		return label;
	}
	
	
}
