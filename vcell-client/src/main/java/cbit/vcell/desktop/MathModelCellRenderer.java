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
import javax.swing.JLabel;
import javax.swing.JTree;

import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.User;

import cbit.vcell.desktop.BioModelNode.UserNameNode;
 
@SuppressWarnings("serial")
public class MathModelCellRenderer extends VCDocumentDbCellRenderer {

/**
 * MyRenderer constructor comment.
 */
public MathModelCellRenderer(User argSessionUser) {
	super(argSessionUser);
}

public MathModelCellRenderer() {
	this(null);
}

/**
 * Insert the method's description here.
 * Creation date: (7/27/2000 6:41:57 PM)
 * @return java.awt.Component
 */
public java.awt.Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
	JLabel component = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
	//
	try {
		if(value instanceof UserNameNode) {
			UserNameNode node = (UserNameNode) value;
			String label = (String)node.getUserObject();
			String qualifier = "";
			if (sessionUser != null && sessionUser.getName().contentEquals(label)) {
				String colorString = (sel)?"white":"#8B0000";
				qualifier = "<font color=\""+colorString+"\">" + label + "</font>"; 
			} else {
				String colorString = (sel)?"white":"black";
				qualifier = "<font color=\""+colorString+"\">" + label + "</font>"; 
			}
			component.setToolTipText(label);
			component.setText("<html><b>" + qualifier + "</b></html>");
		} else if (value instanceof BioModelNode) {
			BioModelNode node = (BioModelNode) value;
			Object userObject = node.getUserObject();
			if (userObject instanceof User) {
				String label = null;
				if (sessionUser != null && sessionUser.compareEqual((User)userObject)) {
					label = "My MathModels ("+((User)userObject).getName()+") (" + node.getChildCount() + ")";
				} else {
					label = ((User)userObject).getName()+"                             ";
				}
				component.setToolTipText(label);
				component.setText(label);
			} else if(userObject instanceof MathModelInfo) {
				MathModelInfo mathModelInfo = (MathModelInfo)userObject;
				if(mathModelInfo.getVersion().getFlag().compareEqual(org.vcell.util.document.VersionFlag.Archived)) {
					component.setText("(Archived) "+component.getText());
				}else if(mathModelInfo.getVersion().getFlag().compareEqual(org.vcell.util.document.VersionFlag.Published)) {
					component.setText("(Published) "+component.getText());
				}
			} else if (userObject instanceof VCDocumentInfoNode) {
				VCDocumentInfoNode infonode = (VCDocumentInfoNode)userObject;
				User nodeUser = infonode.getVCDocumentInfo().getVersion().getOwner();
				String modelName = infonode.getVCDocumentInfo().getVersion().getName();
				String username = nodeUser.getName();
				if (username.equals(VCDocumentDbTreeModel.USER_tutorial)
						|| username.equals(VCDocumentDbTreeModel.USER_Education)) {
					setText(modelName);
				} else if(nodeUser.compareEqual(sessionUser)) {
					Object pNode  = node.getParent();
					if(pNode instanceof BioModelNode) {
						BioModelNode parent = (BioModelNode) pNode;
						// TODO: do this differently
						if(parent.getUserObject() instanceof String) {
							String str = (String)parent.getUserObject();
							if(str.equals(VCDocumentDbTreeModel.Published_MathModels) 
									|| str.equals(VCDocumentDbTreeModel.Curated_MathModels)
									|| str.equals(VCDocumentDbTreeModel.Other_MathModels)) {
								String prefix = sel ? "" : "<span style=\"color:#8B0000\">";
								String suffix = sel ? "" : "</span>";
								setText("<html><b>" + prefix + nodeUser.getName() + suffix + "</b>  : " + modelName + "</html>");
							} else {
								setText(modelName);
							}
						} else {
							setText(modelName);
						}
					} else {
						setText(modelName);
					}
				} else {
					Object pNode  = node.getParent();
					if(pNode instanceof UserNameNode) {		// if we are inside an UserName folder, don't prefix again the model
															// with the user name
						setText("<html>" + modelName + "</html>");
					} else {
						setText("<html><b>" + nodeUser.getName() + " </b> : " + modelName + "</html>");
					}
				}
			}
		}
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
	//
	if (component.getToolTipText() == null || component.getToolTipText().length() == 0) {
		component.setToolTipText(component.getText());
	}
	return component;
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 9:29:31 AM)
 * @return boolean
 * @param geometryInfo cbit.vcell.geometry.GeometryInfo
 * @deprecated
 */
protected boolean isLoaded(MathModelInfo mathModelInfo) {
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 9:29:31 AM)
 * @return boolean
 * @param geometryInfo cbit.vcell.geometry.GeometryInfo
 * @deprecated
 */
protected boolean isLoaded(User user) {
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 8:35:45 AM)
 * @return javax.swing.Icon
 * @param nodeUserObject java.lang.Object
 */
protected void setComponentProperties(JLabel component, MathModelInfo mathModelInfo) {
		
	super.setComponentProperties(component, mathModelInfo);
	//cbit.vcell.numericstest.TestSuiteInfo tsInfo;
	//try {
		//tsInfo = cbit.vcell.numericstest.TSHelper.getTestSuiteInfo(mathModelInfo.getVersion().getAnnot());
	//} catch (cbit.vcell.xml.XmlParseException e) {
		//e.printStackTrace(System.out);
		//throw new RuntimeException("Error reading annotation for mathModel : "+mathModelInfo.getVersion().getName());
	//}
	//if (tsInfo != null) {
		//if (!selected){
			//component.setForeground(java.awt.Color.blue);
		//}
		//component.setText(component.getText()+" ("+cbit.vcell.numericstest.TestSuiteInfo.TESTSUITE_TAG_DONT_CHANGE+" "+tsInfo.getVersion()+")");
	//}
}

}
