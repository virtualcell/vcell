package cbit.vcell.client.desktop.biomodel;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import cbit.vcell.client.desktop.biomodel.SPPRTreeModel.SPPRTreeFolderNode;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Model.ModelParameter;
 
public class SPPRTreeCellRenderer extends DefaultTreeCellRenderer  {

	private Icon speciesIcon;
	private Icon gParamIcon;
	private Icon aParamIcon;
	private Icon reactionsIcon;
	
	
	public SPPRTreeCellRenderer() {
		super();
		initializeIcons();
		setPreferredSize(new Dimension(150,30));
	}


    public void initializeIcons() {
	    speciesIcon = new ImageIcon(getClass().getResource("/images/speciesItem.gif"));
	    gParamIcon = new ImageIcon(getClass().getResource("/images/gparamItem.gif"));
	    aParamIcon = new ImageIcon(getClass().getResource("/images/aparamItem.gif"));
	    reactionsIcon = new ImageIcon(getClass().getResource("/images/reactionsItem.gif"));
    	
	    if((speciesIcon == null) || (gParamIcon == null) || (aParamIcon == null) || (reactionsIcon == null)) {
            System.err.println("At least one icon is missing.");
	    }
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
        BioModelNode node = (BioModelNode)value;  
        Object userObj = node.getUserObject();
    	String labelText = null;
    	String toolTipPrefix = "";
    	Icon icon = null;
    	if (userObj instanceof SimulationContext) { // --- species context	
    		labelText = ((SimulationContext)userObj).getName();
        	toolTipPrefix = "Application : ";
    	} else if (userObj instanceof SPPRTreeFolderNode) {
        	SPPRTreeFolderNode folder = (SPPRTreeFolderNode)userObj; 
        	labelText = folder.getName();
        	if (!SPPRTreeModel.FOLDER_NODE_IMPLEMENTED[folder.getId()]) {
	        	setEnabled(false);
	        	setDisabledIcon(this.getClosedIcon());
        	}
    	} else if (userObj instanceof SpeciesContext) { // --- species context
    		icon = speciesIcon;
        	labelText = ((SpeciesContext)userObj).getName();
        	toolTipPrefix = "SpeciesContext : ";
        } else if (userObj instanceof ModelParameter) {		// --- global parameter
        	icon = gParamIcon;
        	labelText = ((ModelParameter)userObj).getName();
        	toolTipPrefix = "Global Parameter : ";
        } else if (userObj instanceof ReactionStep) {			// --- reaction
        	icon = reactionsIcon;
        	labelText = ((ReactionStep)userObj).getName();
        	toolTipPrefix = "Reaction : ";
//        } else if (isApplicationParam(value)) {	// --- not implemented
//        	icon = aParamIcon;
//        	labelText = (String)((BioModelNode)value).getUserObject();
//        	toolTipPrefix = "Application parameter";
//        } else if (isApplicationFunction(value)) {	// --- not implemented
//        	icon = aParamIcon;
//        	labelText = ((String)((BioModelNode)value).getUserObject());
//        	toolTipPrefix = "Application Function";
//        } else if (isApplicationEquation(value)) {	// --- not implemented
//        	icon = aParamIcon;
//        	labelText = ((String)((BioModelNode)value).getUserObject());
//        	toolTipPrefix = "Application Equation";
        }
        if (icon != null) {
	    	setIcon(icon);
	    }
    	setText(labelText);
    	setToolTipText(toolTipPrefix + labelText);
        return this;
    }

}
