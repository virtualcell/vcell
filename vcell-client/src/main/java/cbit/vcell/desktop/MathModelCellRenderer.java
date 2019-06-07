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
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JLabel;
import javax.swing.JTree;

import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.PublicationInfo;
import org.vcell.util.document.User;

import cbit.vcell.desktop.BioModelNode.PublicationInfoNode;
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
				qualifier = "<font color=\""+colorString+"\"><b>" + label + "</b></font>"; 
			} else {
				String colorString = (sel)?"white":"black";
				qualifier = "<font color=\""+colorString+"\">" + label + "</font>"; 
			}
			component.setToolTipText(label);
			component.setText("<html>" + qualifier + " (" + node.getChildCount() + ")" + "</html>");
			component.setIcon(fieldFolderUserIcon);
		} else if(value instanceof PublicationInfoNode) {
			BioModelNode node = (PublicationInfoNode)value;
			PublicationInfo pi = (PublicationInfo) node.getUserObject();
			String label = pi.getTitle();
			String name = "";
			if(pi.getAuthors() != null && pi.getAuthors().length > 0) {
				name = pi.getAuthors()[0];
			}
			if(name.contains(",")) {
				name = name.substring(0, name.indexOf(","));
			}
			int year = Integer.parseInt((new SimpleDateFormat("yyyy")).format(pi.getPubDate()));
			String label2 = name + " " + year + " " + label;
			int maxLen = MaxPublicationLabelLength + (node.getChildCount() > 1 ? 0 : 4);
			if(label2.length() > maxLen) {
				label2 = label2.substring(0, maxLen) + "...";
			}
			if(node.getChildCount() > 1) {
				String prefix = sel ? "" : "<span style=\"color:#8B0000\">";
				String suffix = sel ? "" : "</span>";
				label += prefix + " (" + node.getChildCount() + ")" + suffix;
				label2 += prefix + " (" + node.getChildCount() + ")" + suffix;
			}
			component.setText("<html>" + label2 + "</html>");			
			component.setToolTipText("<html>" + label + "</html>");
			if(pi.getPubDate().compareTo(Calendar.getInstance().getTime()) > 0) {	// sanity check
				setIcon(fieldFolderWarningIcon);
			} else if(pi.getDoi() == null) {
				setIcon(fieldFolderWarningIcon);
			} else if(name.contains(" ")) {
				setIcon(fieldFolderWarningIcon);
			}
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
			} else if(userObject instanceof MathModelInfo) {		// a math model version
				MathModelInfo mathModelInfo = (MathModelInfo)userObject;
				if(mathModelInfo.getPublicationInfos() != null && mathModelInfo.getPublicationInfos().length > 0) {
					if(mathModelInfo.getVersion().getFlag().compareEqual(org.vcell.util.document.VersionFlag.Published)) {
						component.setText("(Published) "+component.getText());
					} else {
						component.setText("(Curated) "+component.getText());
					}
				} else if(mathModelInfo.getVersion().getFlag().compareEqual(org.vcell.util.document.VersionFlag.Archived)) {
					component.setText("(Archived) "+component.getText());
				}
				String str = component.getToolTipText();
				if(str != null && !str.isEmpty()) {
					component.setToolTipText(str + " " + mathModelInfo.getVersion().getVersionKey());
				}
				
				// we change color of version if it's in the Other folder (Uncurated) and belongs to login user
				Object pNode  = node.getParent();
				if(pNode instanceof BioModelNode) {
					pNode = ((BioModelNode)pNode).getParent();
					if(pNode instanceof BioModelNode && ((BioModelNode)pNode).getUserObject() instanceof String) {
						str = (String)((BioModelNode)pNode).getUserObject();
						pNode = ((BioModelNode)pNode).getParent();
						if(((BioModelNode)pNode).getUserObject() instanceof String 
								&& str.equalsIgnoreCase(sessionUser.getName())) {
							str = (String)((BioModelNode)pNode).getUserObject();
							if(str.equals(VCDocumentDbTreeModel.Other_MathModels)) {
								String prefix = sel ? "" : "<span style=\"color:#808080\">";	// GRAY
								String suffix = sel ? "" : "</span>";
								String str1 = prefix + component.getText() + suffix;
								setText("<html>" + str1 + "</html>"); 
							}
						}
					}
				}
			} else if (userObject instanceof VCDocumentInfoNode) {
				VCDocumentInfoNode infonode = (VCDocumentInfoNode)userObject;
				User nodeUser = infonode.getVCDocumentInfo().getVersion().getOwner();
				String modelName = infonode.getVCDocumentInfo().getVersion().getName();
				String username = nodeUser.getName();
				if (username.equals(VCDocumentDbTreeModel.USER_tutorial)
						|| username.equals(VCDocumentDbTreeModel.USER_Education)) {
					setText(modelName);		// keep it simple for Education and Tutorial
				} else if(nodeUser.compareEqual(sessionUser)) {
					Object pNode  = node.getParent();
					if(pNode instanceof BioModelNode) {
						BioModelNode parent = (BioModelNode) pNode;
						if(parent.getUserObject() instanceof String) {		// the Published, Curated and Other folders are the only
							String str = (String)parent.getUserObject();	// ones where the parent user object is a String
							if(str.equals(VCDocumentDbTreeModel.Published_MathModels) 
									|| str.equals(VCDocumentDbTreeModel.Curated_MathModels)
									/*|| str.equals(VCDocumentDbTreeModel.Other_MathModels)*/) {
								String prefix = sel ? "" : "<span style=\"color:#8B0000\">";
								String suffix = sel ? "" : "</span>";
								setText("<html><b>" + prefix + nodeUser.getName() + suffix + "</b>  : " + modelName + "</html>");
							} else {
								// in the Other folder / owner folder
								String prefix = sel ? "" : "<span style=\"color:#808080\">";	// GRAY
								String suffix = sel ? "" : "</span>";
								String str1 = prefix + modelName + suffix;
								if(node.getChildCount() > 1) {
									prefix = sel ? "" : "<span style=\"color:#8B0000\">";
									suffix = sel ? "" : "</span>";
									str1 += prefix + " (" + node.getChildCount() + ")" + suffix;
								}
								setText("<html>" + str1 + "</html>");	// the name of the model container - which holds the versions 
							}
						} else if(parent.getUserObject() instanceof PublicationInfo) {
							String prefix = sel ? "" : "<span style=\"color:#8B0000\">";
							String suffix = sel ? "" : "</span>";
							setText("<html><b>" + prefix + nodeUser.getName() + suffix + "</b>  : " + modelName + "</html>");
						} else {
							String str = modelName;
							if(node.getChildCount() > 1) {
								String prefix = sel ? "" : "<span style=\"color:#8B0000\">";
								String suffix = sel ? "" : "</span>";
								str += prefix + " (" + node.getChildCount() + ")" + suffix;
							}
							setText("<html>" + str + "</html>");
						}
					} else {
						setText(modelName);			// unreachable
					}
				} else {
					Object pNode  = node.getParent();
					if(pNode instanceof UserNameNode) {		// if we are inside an UserName folder (like Shared or Uncurated public),
						String str = modelName;				// don't prefix again the model with the user name
						if(node.getChildCount() > 1) {
							String prefix = sel ? "" : "<span style=\"color:#8B0000\">";
							String suffix = sel ? "" : "</span>";
							str += prefix + " (" + node.getChildCount() + ")" + suffix;
						}
						setText("<html>" + str + "</html>");
					} else {
						setText("<html><b>" + nodeUser.getName() + " </b> : " + modelName + "</html>");	// content of "Published" folder
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
