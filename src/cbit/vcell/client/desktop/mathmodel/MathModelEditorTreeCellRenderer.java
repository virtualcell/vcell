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

import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeCellRenderer;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderNode;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.mathmodel.MathModel;
 
@SuppressWarnings("serial")
public class MathModelEditorTreeCellRenderer extends DocumentEditorTreeCellRenderer  {
	private MathModel mathModel = null;
	private Icon mathModelIcon = null;
	
	public MathModelEditorTreeCellRenderer(JTree tree) {
		super(tree);
		try {
			mathModelIcon = new ImageIcon(getClass().getResource("/images/math_16x16.gif"));
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
	}

	public void setMathModel(MathModel mm) {
		mathModel = mm;
	}
	
	public Component getTreeCellRendererComponent(
                        JTree tree,
                        Object value,
                        boolean sel,
                        boolean expanded,
                        boolean leaf,
                        int row,
                        boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		Font font = regularFont;
		Icon icon = null;
    	String labelText = null;
    	String toolTipPrefix = "";
    	String toolTipSuffix = "";
    	boolean bChange = false;
    	if (value instanceof BioModelNode) {
	        BioModelNode node = (BioModelNode)value;
	        Object userObj = node.getUserObject();
	    	if (userObj instanceof MathModel) {
	    		bChange = true;
	    		font = boldFont;
	    		icon = mathModelIcon;
	    		labelText = ((MathModel)userObj).getName();
	        } else if (userObj instanceof DocumentEditorTreeFolderNode) {		// --- 1st level folders
	    		DocumentEditorTreeFolderNode folder = (DocumentEditorTreeFolderNode)userObj;
	    		DocumentEditorTreeFolderClass folderClass = folder.getFolderClass();
	    		if (folderClass == DocumentEditorTreeFolderClass.MATH_ANNOTATION_NODE) {
	    			bChange = true;
	    			String description = mathModel.getDescription();
	    			if (description == null || description.trim().length() == 0) {	    				
	    				labelText = "(click to edit notes)";
	    			} else {
	    				labelText = description.trim();
	    			}		    		
	    		}
	        }
		}
    	if (bChange) {
	    	setIcon(icon);
	    	setFont(font);
	    	setText(labelText);
	    	setToolTipText(toolTipPrefix + toolTipSuffix);
    	}
        return this;
    }
}
