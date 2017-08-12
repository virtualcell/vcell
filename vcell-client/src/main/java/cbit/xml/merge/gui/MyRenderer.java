/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.xml.merge.gui;

/**
 * Insert the type's description here.
 * Creation date: (7/27/2000 6:30:41 PM)
 * @author: 
 */
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;

import cbit.xml.merge.NodeInfo;
 
public class MyRenderer extends javax.swing.tree.DefaultTreeCellRenderer {
	private javax.swing.ImageIcon fieldAttributeIcon = null;
	private javax.swing.ImageIcon fieldNewAttributeIcon = null;
	private javax.swing.ImageIcon fieldRemovedAttributeIcon = null;
	private javax.swing.ImageIcon fieldChangedAttributeIcon = null;
	private javax.swing.ImageIcon fieldBadAttributeIcon = null;
	//
	private javax.swing.ImageIcon fieldFolderIcon = null;	
	private javax.swing.ImageIcon fieldNewFolderIcon = null;
	private javax.swing.ImageIcon fieldRemovedFolderIcon = null;
	private javax.swing.ImageIcon fieldChangedFolderIcon = null;
	private javax.swing.ImageIcon fieldBadFolderIcon = null;
/**
 * MyRenderer constructor comment.
 */
public MyRenderer() {
	super();
	this.fieldFolderIcon = new ImageIcon(getClass().getResource("/images/folder.gif"));
	this.fieldBadFolderIcon = new ImageIcon(getClass().getResource("/images/badfolder.gif"));
	this.fieldNewFolderIcon = new ImageIcon(getClass().getResource("/images/folder_new.gif"));
	this.fieldRemovedFolderIcon = new ImageIcon(getClass().getResource("/images/folder_removed.gif"));
	this.fieldChangedFolderIcon = new ImageIcon(getClass().getResource("/images/questionfolder.gif"));
	//
	this.fieldAttributeIcon = new ImageIcon(getClass().getResource("/images/attribute.gif"));
	this.fieldBadAttributeIcon = new ImageIcon(getClass().getResource("/images/badattribute.gif"));
	this.fieldNewAttributeIcon = new ImageIcon(getClass().getResource("/images/attribute_new.gif"));
	this.fieldChangedAttributeIcon = new ImageIcon(getClass().getResource("/images/attribute_changed.gif"));
	this.fieldRemovedAttributeIcon = new ImageIcon(getClass().getResource("/images/attribute_removed.gif"));
}
/**
 * This method modifies the way the nodes are represented upon their status and type.
 * Creation date: (7/27/2000 6:41:57 PM)
 * @return java.awt.Component
 */
public java.awt.Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
	JLabel component = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
	//
	if (value instanceof NodeInfo) {
		NodeInfo nodeInfo = (NodeInfo) value;

		//Check the kind of node to decide which icon to use
		if ( nodeInfo.isAttribute()) {
			switch (nodeInfo.getStatus()) {
				case NodeInfo.STATUS_NORMAL:
					component.setIcon(this.fieldAttributeIcon);
					setToolTipText(null);
					break;
				case NodeInfo.STATUS_NEW:
					component.setIcon(this.fieldNewAttributeIcon);
					component.setForeground(java.awt.Color.blue);
					setToolTipText("New " + nodeInfo.getName());
					break;
				case NodeInfo.STATUS_REMOVED:
					component.setIcon(this.fieldRemovedAttributeIcon);
					component.setForeground(java.awt.Color.red);
					setToolTipText("Removed " + nodeInfo.getName());
					break;
				case NodeInfo.STATUS_CHANGED:
					component.setIcon(this.fieldChangedAttributeIcon);
					component.setForeground(java.awt.Color.gray);
					setToolTipText("Altered " + nodeInfo.getName());
					break;
				default:
					component.setIcon(this.fieldBadAttributeIcon);
					setToolTipText(null);
			}
		} else {
			//is an element
			switch (nodeInfo.getStatus()) {
				case NodeInfo.STATUS_NORMAL:
					component.setIcon(this.fieldFolderIcon);
					setToolTipText(null);
					break;
				case NodeInfo.STATUS_NEW:
					component.setIcon(this.fieldNewFolderIcon);
					component.setForeground(java.awt.Color.blue);
					setToolTipText("New " + nodeInfo.getName());
					break;
				case NodeInfo.STATUS_REMOVED:
					component.setIcon(this.fieldRemovedFolderIcon);
					component.setForeground(java.awt.Color.red);
					setToolTipText("Removed " + nodeInfo.getName());
					break;
				case NodeInfo.STATUS_CHANGED:
					component.setIcon(this.fieldChangedFolderIcon);
					component.setForeground(java.awt.Color.gray);
					setToolTipText("Altered " + nodeInfo.getName());
					break;
				default:
					component.setIcon(this.fieldBadFolderIcon);
					setToolTipText(null);
			}
		}
	}
		
	return component;
}
}
