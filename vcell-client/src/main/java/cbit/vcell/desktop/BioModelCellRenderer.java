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

import org.vcell.sybil.models.AnnotationQualifier;
import org.vcell.sybil.models.miriam.MIRIAMQualifier;
import org.vcell.util.document.BioModelChildSummary;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.User;
import org.vcell.util.gui.VCellIcons;

import cbit.vcell.desktop.BioModelNode.UserNameNode;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.xml.gui.MiriamTreeModel.DateNode;
import cbit.vcell.xml.gui.MiriamTreeModel.LinkNode;
 
@SuppressWarnings("serial")
public class BioModelCellRenderer extends VCDocumentDbCellRenderer {
	
/**
 * MyRenderer constructor comment.
 */
public BioModelCellRenderer(User argSessionUser) {
	super(argSessionUser);
}

public BioModelCellRenderer() {
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
		if (value instanceof DateNode) {
			DateNode dateNode = (DateNode)value;
			AnnotationQualifier qualifier = dateNode.getDateQualifier();
			String colorString = (sel)?"white":"black";
			component.setText("<html>"+qualifier.getDescription()+"&nbsp;<font color=\""+colorString+"\">" + dateNode.getDate().getDateString() + "</font></html>");
		} else if (value instanceof LinkNode) {
			LinkNode ln = (LinkNode)value;
			String link = ln.getLink();
			String text = ln.getText();
			MIRIAMQualifier mQualifier = ln.getMiriamQualifier();
			String qualifier = "";
			if(mQualifier.equals(MIRIAMQualifier.MODEL_isDescribedBy)) {
				String colorString = (sel)?"white":"black";
				qualifier = "<font color=\""+colorString+"\">" + mQualifier.getDescription() + "</font>"; 
			} else {
				String colorString = (sel)?"white":"#8B0000";
				qualifier = "<font color=\""+colorString+"\">" + mQualifier.getDescription() + "</font>"; 
			}
			if (link != null) {
				String colorString = (sel)?"white":"blue";
				component.setToolTipText("Double-click to open link");
				component.setText("<html>"+qualifier+"&nbsp;<font color=\""+colorString+"\"><a href=" + link + ">" + text + "</a></font></html>");
			} else {
				String colorString = (sel)?"white":"black";
				component.setText("<html>"+qualifier+"&nbsp;<font color=\""+colorString+"\">" + text + "</font></html>");
			}
		} else if(value instanceof UserNameNode) {
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
				if ( sessionUser != null && sessionUser.compareEqual((User)userObject)){
					label = "My BioModels ("+((User)userObject).getName()+") (" + node.getChildCount() + ")";
				} else {
					label = ((User)userObject).getName()+"(abcdefghijklmnopq)(000000)";
				}
				component.setToolTipText(label);
				component.setText(label);
			}else if(userObject instanceof BioModelInfo) {
				BioModelInfo biomodelInfo = (BioModelInfo)userObject;
				if(biomodelInfo.getVersion().getFlag().compareEqual(org.vcell.util.document.VersionFlag.Archived)){
					component.setText("(Archived) "+component.getText());
				}else if(biomodelInfo.getVersion().getFlag().compareEqual(org.vcell.util.document.VersionFlag.Published)){
					component.setText("(Published) "+component.getText());
				}
			}else if (userObject instanceof Geometry) {
				Geometry geo = (Geometry)userObject;
				String label = "";
				//geomety info, when spatial--shows name+1D/2D/3D				
				if(geo.getDimension()>0)
				{
					label = geo.getName() + " ("+geo.getDimension()+"D)";
				}
				else
				{
					label = BioModelChildSummary.COMPARTMENTAL_GEO_STR;
				}

				component.setToolTipText("Geometry");
				component.setText(label);
				setIcon(fieldGeometryIcon);
			} else if (userObject instanceof String && "AppType".equals(node.getRenderHint("type"))) {
				String label = (String)userObject;
				component.setToolTipText("Application type");
				component.setText(label);
				setIcon(VCellIcons.mathTypeIcon);
			} else if (userObject instanceof VCDocumentInfoNode) {
				VCDocumentInfoNode infonode = (VCDocumentInfoNode)userObject;
				User nodeUser = infonode.getVCDocumentInfo().getVersion().getOwner();
				String modelName = infonode.getVCDocumentInfo().getVersion().getName();
				String username = nodeUser.getName();
				if (username.equals(VCDocumentDbTreeModel.USER_tutorial)
						|| username.equals(VCDocumentDbTreeModel.USER_Education)
						|| username.equals(VCDocumentDbTreeModel.USER_tutorial610)
						|| username.equals(VCDocumentDbTreeModel.USER_tutorial611)
						|| username.equals(VCDocumentDbTreeModel.USER_modelBricks)) {
					component.setText(modelName);
				} else if(nodeUser.compareEqual(sessionUser)) {
					Object pNode  = node.getParent();
					if(pNode instanceof BioModelNode) {
						BioModelNode parent = (BioModelNode) pNode;
						// TODO: do this differently
						if(parent.getUserObject() instanceof String) {
							String str = (String)parent.getUserObject();
							if(str.equals(VCDocumentDbTreeModel.Published_BioModels) 
									|| str.equals(VCDocumentDbTreeModel.Curated_BioModels) 
									|| str.equals(VCDocumentDbTreeModel.Other_BioModels)) {
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
 * Creation date: (5/8/01 10:34:18 AM)
 * @return boolean
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @deprecated
 */
protected boolean isLoaded(BioModelInfo bioModelInfo) {
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 10:34:18 AM)
 * @return boolean
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @deprecated
 */
protected boolean isLoaded(SimulationContext simulationContext) {
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 10:34:18 AM)
 * @return boolean
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @deprecated
 */
protected boolean isLoaded(User user) {
	return false;
}

}
