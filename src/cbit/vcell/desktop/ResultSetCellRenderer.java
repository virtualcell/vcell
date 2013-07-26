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

import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.User;
 
public class ResultSetCellRenderer extends VCellBasicCellRenderer {
/**
 * MyRenderer constructor comment.
 */
public ResultSetCellRenderer() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (11/6/2002 5:13:26 PM)
 * @return int
 * @param node cbit.vcell.desktop.BioModelNode
 */
int getMaxErrorLevel(BioModelNode node) {
	//if (node.getUserObject() instanceof SimulationInfo){
		////
		//// go to children (Simulations) to ask what is their greatest error
		////
		//int maxErrorLevel = cbit.vcell.modeldb.SimContextStatus.ERROR_NONE;
		//for (int i = 0; i < node.getChildCount(); i++){
			//BioModelNode child = (BioModelNode)node.getChildAt(i);
			//maxErrorLevel = Math.max(maxErrorLevel,getMaxErrorLevel(child));
		//}
		//return maxErrorLevel;
	//}
	//if (node.getUserObject() instanceof BioModelInfo){
		////
		//// go to children (Simulations) to ask what is their greatest error
		////
		//int maxErrorLevel = cbit.vcell.modeldb.SimContextStatus.ERROR_NONE;
		//for (int i = 0; i < node.getChildCount(); i++){
			//BioModelNode child = (BioModelNode)node.getChildAt(i);
			//maxErrorLevel = Math.max(maxErrorLevel,getMaxErrorLevel(child));
		//}
		//return maxErrorLevel;
	//}
	//if (node.getUserObject() instanceof SimulationContextInfo){
		////
		//// go to children (Simulations) to ask what is their greatest error
		////
		//int maxErrorLevel = cbit.vcell.modeldb.SimContextStatus.ERROR_NONE;
		//for (int i = 0; i < node.getChildCount(); i++){
			//BioModelNode child = (BioModelNode)node.getChildAt(i);
			//maxErrorLevel = Math.max(maxErrorLevel,getMaxErrorLevel(child));
		//}
		//return maxErrorLevel;
	//}
	//return super.getMaxErrorLevel(node);
	return BioModelNode.ERROR_NONE;
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
		if (value instanceof BioModelNode) {
			BioModelNode node = (BioModelNode) value;
			boolean bLoaded = false;
			if (node.getUserObject() instanceof User) {				
				//
				// Check if node is a User, 
				//
				String label = null;
				//if ( dataWorkspace != null && dataWorkspace.getDocumentManager() != null && dataWorkspace.getDocumentManager().getUser().getName().equals(((User)node.getUserObject()).getName())) {
					 //label = "My Datasets ("+((User)node.getUserObject()).getName()+")";
				//} else {
				label = ((User)node.getUserObject()).getName()+"                            ";
				//}
				component.setToolTipText("User Name");
				component.setText(label);
			}
		}
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
	//
	return component;
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 8:35:45 AM)
 * @return javax.swing.Icon
 * @param nodeUserObject java.lang.Object
 */
protected void setComponentProperties(JLabel component, BioModelInfo bioModelInfo) {

	super.setComponentProperties(component,bioModelInfo);
	
	component.setToolTipText("BioModel version");
	component.setText(bioModelInfo.getVersion().getName());
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 8:35:45 AM)
 * @return javax.swing.Icon
 * @param nodeUserObject java.lang.Object
 */
protected void setComponentProperties(JLabel component, MathModelInfo mathModelInfo) {

	super.setComponentProperties(component,mathModelInfo);
	
	component.setToolTipText("Mathematical Model");
	component.setText(mathModelInfo.getVersion().getName());
}


}
