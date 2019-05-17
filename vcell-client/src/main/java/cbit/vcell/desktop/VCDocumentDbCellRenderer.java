/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.desktop;
import java.math.BigDecimal;

import javax.swing.JTree;

import org.vcell.util.document.GroupAccess;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.VersionFlag;

import cbit.vcell.desktop.BioModelNode.UserNameNode;
import cbit.vcell.desktop.VCellBasicCellRenderer.VCDocumentInfoNode;
 
@SuppressWarnings("serial")
public class VCDocumentDbCellRenderer extends VCellBasicCellRenderer {
	
	protected User sessionUser = null;

/**
 * MyRenderer constructor comment.
 */
public VCDocumentDbCellRenderer(User argSessionUser) {
	super();
	this.sessionUser = argSessionUser;
}

public VCDocumentDbCellRenderer() {
	this(null);
}

public final void setSessionUser(User sessionUser) {
	this.sessionUser = sessionUser;
}

public java.awt.Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
	super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
	if (value instanceof BioModelNode) {
		BioModelNode node = (BioModelNode) value;
		Object userObject = node.getUserObject();
		if (
			VCDocumentDbTreeModel.SHARED_BIO_MODELS.equals(userObject) ||
			VCDocumentDbTreeModel.SHARED_MATH_MODELS.equals(userObject) ||
			VCDocumentDbTreeModel.SHARED_GEOMETRIES.equals(userObject) ||
	//		VCDocumentDbTreeModel.Public_BioModels.equals(userObject) ||
	//		VCDocumentDbTreeModel.Public_MathModels.equals(userObject) ||
			VCDocumentDbTreeModel.Published_BioModels.equals(userObject) ||
			VCDocumentDbTreeModel.Curated_BioModels.equals(userObject) ||
			VCDocumentDbTreeModel.Other_BioModels.equals(userObject) ||
			VCDocumentDbTreeModel.ModelBricks.equals(userObject) ||
			VCDocumentDbTreeModel.Published_MathModels.equals(userObject) ||
			VCDocumentDbTreeModel.Curated_MathModels.equals(userObject) ||
			VCDocumentDbTreeModel.Other_MathModels.equals(userObject) ||
			VCDocumentDbTreeModel.PUBLIC_GEOMETRIES.equals(userObject) ||
			VCDocumentDbTreeModel.Education.equals(userObject) ||
			VCDocumentDbTreeModel.Tutorials.equals(userObject)
			) {
			setText(getText() + " (" + node.getChildCount() + ")");
		}
		if(userObject instanceof User && sessionUser != null && ((User)userObject).compareEqual(sessionUser)) {
			setIcon(fieldFolderSelfIcon);	// My BioModels / My MathModels / My Geometries folders (with a little locker)
		}
		else if(
			VCDocumentDbTreeModel.SHARED_BIO_MODELS.equals(userObject) ||
			VCDocumentDbTreeModel.SHARED_MATH_MODELS.equals(userObject) ||
			VCDocumentDbTreeModel.SHARED_GEOMETRIES.equals(userObject)
			) {
			setIcon(fieldFolderSharedIcon);
		}
		else if(
			VCDocumentDbTreeModel.Public_BioModels.equals(userObject) ||
			VCDocumentDbTreeModel.Public_MathModels.equals(userObject) ||
			VCDocumentDbTreeModel.PUBLIC_GEOMETRIES.equals(userObject)
			) {
			setIcon(fieldFolderPublicIcon);
		}
		else if(
			VCDocumentDbTreeModel.Published_BioModels.equals(userObject) ||
			VCDocumentDbTreeModel.Published_MathModels.equals(userObject)
			) {
			setIcon(fieldFolderPublishedIcon);
		}
		else if(
			VCDocumentDbTreeModel.Curated_BioModels.equals(userObject) ||
			VCDocumentDbTreeModel.Curated_MathModels.equals(userObject)
			) {
			setIcon(fieldFolderCuratedIcon);
		} 
		else if(userObject instanceof VCDocumentInfoNode) {
			VCDocumentInfoNode infonode = (VCDocumentInfoNode)userObject;
			User nodeUser = infonode.getVCDocumentInfo().getVersion().getOwner();
//			String modelName = infonode.getVCDocumentInfo().getVersion().getName();
//			String username = nodeUser.getName();
			if(sessionUser != null && nodeUser.compareEqual(sessionUser)) {
				if(!node.isRoot() && node.getParent() instanceof BioModelNode) {
					BioModelNode parent = (BioModelNode) node.getParent();
					Object parentUserObject = parent.getUserObject();
					if(parentUserObject instanceof User && ((User)parentUserObject).compareEqual(sessionUser)) {
						// we are inside My BioModels or My MathModels folder, all models here are ours
						// each of them has at least one version
						boolean bPublished = false;
						boolean bPublic = false;
						boolean bShared = false;
						for (int i = 0; i < node.getChildCount(); i++) {	// check all versions
							BioModelNode versionBioModelNode = (BioModelNode)node.getChildAt(i);
							VCDocumentInfo versionVCDocumentInfo = (VCDocumentInfo) versionBioModelNode.getUserObject();
							BigDecimal groupid = versionVCDocumentInfo.getVersion().getGroupAccess().getGroupid();
							if(versionVCDocumentInfo.getVersion().getFlag().compareEqual(VersionFlag.Published)) {
								bPublished = true;
								break;		// published has highest precedence
							}
							if(groupid.equals(GroupAccess.GROUPACCESS_ALL)) {
								bPublic = true;
							} else if(groupid.equals(GroupAccess.GROUPACCESS_NONE)) {
								;
							} else {
								bShared = true;
							}
						}
						if(bPublished) {
							setIcon(fieldFolderPublishedIcon);
						} else if(bPublic) {
							setIcon(fieldFolderWeakPublicIcon);
						} else if(bShared) {
							setIcon(fieldFolderSharedIcon);
						}
					}
				}
			}
		}
	}
	return this;
}
}
