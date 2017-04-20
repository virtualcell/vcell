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

import java.awt.Font;

import javax.swing.JLabel;
/**
 * Insert the type's description here.
 * Creation date: (7/27/2000 6:30:41 PM)
 * @author: 
 */
import javax.swing.JTree;
 
public class BioModelInfoCellRenderer extends VCellBasicCellRenderer {
/**
 * MyRenderer constructor comment.
 */
public BioModelInfoCellRenderer() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (11/6/2002 5:13:26 PM)
 * @return int
 * @param node cbit.vcell.desktop.BioModelNode
 * @deprecated
 */
int getMaxErrorLevel(BioModelNode node) {
	//if (node.getUserObject() instanceof SolverResultSetInfo){
		//// parent should be a simulationInfo
		//// parent's parent should be a simulationContextInfo (with a SimContextStat)
		//SimulationContextInfo scInfo = (SimulationContextInfo)((BioModelNode)node.getParent().getParent()).getUserObject();
		//cbit.vcell.modeldb.SimContextStatus scStatus = scInfo.getSimContextStatus();
		//if (scStatus!=null){
			//return scStatus.getErrorLevel();
		//}else{
			//return cbit.vcell.modeldb.SimContextStatus.ERROR_NONE;
		//}
	//}else if (node.getUserObject() instanceof SimulationContextInfo){
		//SimulationContextInfo scInfo = (SimulationContextInfo)node.getUserObject();
		//cbit.vcell.modeldb.SimContextStatus scStatus = scInfo.getSimContextStatus();
		//if (scStatus!=null){
			//return scStatus.getErrorLevel();
		//}else{
			//return cbit.vcell.modeldb.SimContextStatus.ERROR_NONE;
		//}
	//}else{
		//return super.getMaxErrorLevel(node);
	//}
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
	if (!leaf && expanded) {
		setIcon(fieldFolderOpenIcon);
	}else if (!leaf && !expanded) {
		setIcon(fieldFolderClosedIcon);
	}
	try {
	if (value instanceof BioModelNode) {
		BioModelNode node = (BioModelNode) value;
		
		boolean bLoaded = false;

		//
		// Check if node is a SolverResultSetInfo
		//
		if (node.getUserObject() instanceof String && "Geometry".equals(node.getRenderHint("type"))) {
			String label = (String)node.getUserObject();
			component.setToolTipText("Geometry");
			component.setText(label);
			setIcon(fieldGeometryIcon);
		}else if (node.getUserObject() instanceof String && "SimulationContext".equals(node.getRenderHint("type"))) {
			String label = (String)node.getUserObject();
			component.setToolTipText("Application");
			component.setText(label);
			setIcon(fieldSimulationContextIcon);
		}else if (node.getUserObject() instanceof String && "Simulation".equals(node.getRenderHint("type"))) {
			String label = (String)node.getUserObject();
			component.setToolTipText("Simulation");
			component.setText(label);
			setIcon(fieldSimulationIcon);

		}else if (node.getUserObject() instanceof String && "AppType".equals(node.getRenderHint("type"))) {
			String label = (String)node.getUserObject();
			component.setToolTipText("Simulation");
			component.setText(label);
			setIcon(fieldAppTypeIcon);

		}else if (node.getUserObject() instanceof Annotation) {
			String label = ((Annotation)node.getUserObject()).toString();
			component.setToolTipText("Annotation");
			component.setText(label);
			setIcon(fieldTextIcon);
						
		} else{
			setComponentProperties(component,node.getUserObject());
			
		}
		
		if (selectedFont==null && component.getFont()!=null) { selectedFont = component.getFont().deriveFont(Font.BOLD); }
		if (unselectedFont==null && component.getFont()!=null) { unselectedFont = component.getFont().deriveFont(Font.PLAIN); }
		
		if (bLoaded){
			component.setFont(selectedFont);
		}else{
			component.setFont(unselectedFont);
		}
	}
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
	//
	return component;
}
}
