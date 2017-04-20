/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.mathmodel;

import java.awt.Component;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JTree;

import org.vcell.util.gui.VCellIcons;

import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeCellRenderer;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderNode;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.mathmodel.MathModel;
 
@SuppressWarnings("serial")
public class MathModelEditorTreeCellRenderer extends DocumentEditorTreeCellRenderer  {
//	private MathModel mathModel = null;
	
	public MathModelEditorTreeCellRenderer() {
		super();
	}

//	public void setMathModel(MathModel mm) {
//		mathModel = mm;
//	}
	
	public Component getTreeCellRendererComponent(
                        JTree tree,
                        Object value,
                        boolean sel,
                        boolean expanded,
                        boolean leaf,
                        int row,
                        boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		if (regularFont == null) {
			regularFont = getFont();
			boldFont = regularFont.deriveFont(Font.BOLD);
		}		
		Font font = regularFont;
		Icon icon = null;
    	String labelText = null;
    	String toolTipPrefix = "";
    	String toolTipSuffix = "";
    	if (value instanceof BioModelNode) {
	        BioModelNode node = (BioModelNode)value;
	        if (node.getChildCount() > 0) {
	        	icon = getIcon();
	        }
	        Object userObj = node.getUserObject();
	    	if (userObj instanceof MathModel) {
	    		font = boldFont;
	    		icon = VCellIcons.documentIcon;
	    		labelText = ((MathModel)userObj).getName();
	    		toolTipPrefix = "MathModel: ";
	    	} else if (userObj instanceof DocumentEditorTreeFolderNode) {		// --- 1st level folders
	    		DocumentEditorTreeFolderNode folder = (DocumentEditorTreeFolderNode)userObj;
	    		labelText = folder.getName();
	    		if (folder.isBold()) {
	    			font = boldFont;
	    		}
	    		DocumentEditorTreeFolderClass folderClass = folder.getFolderClass();
	    		switch(folderClass) {
	    		case MATH_VCML_NODE:
	    			icon = VCellIcons.textNotesIcon;
	    			break;
	    		case MATH_GEOMETRY_NODE:
	    			icon = VCellIcons.geometryIcon;
	    			break;
	    		case MATH_SIMULATIONS_NODE:
	    			icon = VCellIcons.simulationIcon;
	    			break;
	    		case MATH_OUTPUT_FUNCTIONS_NODE:
	    			icon = VCellIcons.getOutputFunctionIcon();
	    			break;
	    		}
	    	}
		}
    	setIcon(icon);
    	setFont(font);
    	setText(labelText);
    	if (toolTipSuffix.length() == 0) {
			toolTipSuffix = labelText;
		}
    	setToolTipText(toolTipPrefix + toolTipSuffix);
        return this;
    }
}
