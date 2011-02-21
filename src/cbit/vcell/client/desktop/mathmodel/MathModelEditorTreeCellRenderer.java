package cbit.vcell.client.desktop.mathmodel;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.Component;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.ImageIcon;
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
	private Icon mathModelIcon = null;
	
	public MathModelEditorTreeCellRenderer(JTree tree) {
		super(tree);
		try {
			mathModelIcon = new ImageIcon(getClass().getResource("/images/math_16x16.gif"));
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
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
	    		icon = mathModelIcon;
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
	    		case MATH_GEOMETRY_NODE:
	    			icon = VCellIcons.geometryIcon;
	    			break;
	    		case MATH_SIMULATIONS_NODE:
	    			icon = VCellIcons.simulationIcon;
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
