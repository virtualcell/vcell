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
import cbit.vcell.data.DataSymbol;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.mapping.BioEvent;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Model.ModelParameter;
 
public class SPPRTreeCellRenderer extends DefaultTreeCellRenderer  {

	private Icon speciesIcon;
	private Icon gParamIcon;
	private Icon aParamIcon;
	private Icon reactionsIcon;
	private Icon fluxIcon;
	private Icon eventIcon;
	private Icon fieldDataItemIcon;

	private Icon geometryFolderIcon;
	private Icon electricFolderIcon;
	private Icon structureMappingFolderIcon;
	
	public SPPRTreeCellRenderer() {
		super();
		setPreferredSize(new Dimension(170,30));
		initializeIcons();
	}


    private void initializeIcons() {
	    speciesIcon = new ImageIcon(getClass().getResource("/images/speciesItem.gif"));
	    gParamIcon = new ImageIcon(getClass().getResource("/images/gparamItem.gif"));
	    aParamIcon = new ImageIcon(getClass().getResource("/images/aparamItem.gif"));
	    reactionsIcon = new ImageIcon(getClass().getResource("/images/reactionsItem.gif"));
	    fluxIcon = new ImageIcon(getClass().getResource("/images/fluxItem.gif"));
	    eventIcon = new ImageIcon(getClass().getResource("/images/eventItem.gif"));
    	fieldDataItemIcon = new ImageIcon(getClass().getResource("/images/fieldDataItem.gif"));
//    	fieldDataItemIcon = new ImageIcon("C:/dan/work images/icons/fieldDataItem.gif");

    	geometryFolderIcon = new ImageIcon(getClass().getResource("/images/geometryFolder2D.gif"));
	    electricFolderIcon = new ImageIcon(getClass().getResource("/images/electricFolder.gif"));
	    structureMappingFolderIcon = new ImageIcon(getClass().getResource("/images/structureMappingFolder.gif"));

	    if((electricFolderIcon == null) || (structureMappingFolderIcon == null) || 
	    		(geometryFolderIcon == null) ||
	    		(fieldDataItemIcon == null) || 
		    	(speciesIcon == null) || (gParamIcon == null) || (aParamIcon == null) || 
		    	(reactionsIcon == null) || (fluxIcon == null) || (eventIcon == null)) {
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
		if (value instanceof BioModelNode) {
	        BioModelNode node = (BioModelNode)value;
	        Object userObj = node.getUserObject();
	    	String labelText = null;
	    	String toolTipPrefix = "";
	    	Icon icon = null;
			if (userObj instanceof SimulationContext) { 			// --- root: application name	
	    		labelText = ((SimulationContext)userObj).getName();
	        	toolTipPrefix = "Application : ";
	    	} else if (userObj instanceof SPPRTreeFolderNode) {		// --- 1st level folders
	        	SPPRTreeFolderNode folder = (SPPRTreeFolderNode)userObj;
	        	if (SPPRTreeModel.FOLDER_NO_CHILDREN[folder.getId()]) {
	        	switch(folder.getId())
	        	{
	        		case SPPRTreeModel.GEOMETRY_NODE:
	        			icon = geometryFolderIcon;
	        			break;
	        		case SPPRTreeModel.STRUCTURE_MAPPING_NODE:
	        			icon = structureMappingFolderIcon;
	        			break;
	        		case SPPRTreeModel.ELECTRICAL_MAPPING_NODE:
	        			icon = electricFolderIcon;
	        			break;
	        		case SPPRTreeModel.DATA_SYMBOLS_NODE:
	    	        	toolTipPrefix = "Data Symbols : ";
	        			break;
	        		default:
	        			icon = closedIcon;						// icon from DefaultTreeCellRenderer
		        		break;
	        		}
	        	}
	        	labelText = folder.getName();
	        	if (!SPPRTreeModel.FOLDER_NODE_IMPLEMENTED[folder.getId()] || !folder.isSupported()) {
		        	setEnabled(false);
		        	setDisabledIcon(this.getClosedIcon());
	    		}
	    	} else if (userObj instanceof SpeciesContext) { 	// --- species context
	    		icon = speciesIcon;
	        	labelText = ((SpeciesContext)userObj).getName();
	        	toolTipPrefix = "SpeciesContext : ";
	        } else if (userObj instanceof ModelParameter) {		// --- global parameter
	        	icon = gParamIcon;
	        	labelText = ((ModelParameter)userObj).getName();
	        	toolTipPrefix = "Global Parameter : ";
	        } else if (userObj instanceof SimpleReaction) {		// --- simple reaction
	        	icon = reactionsIcon;
	        	labelText = ((ReactionStep)userObj).getName();
	        	toolTipPrefix = "Simple Reaction : ";
	        } else if (userObj instanceof FluxReaction) {		// --- flux reaction
	        	icon = fluxIcon;
	        	labelText = ((ReactionStep)userObj).getName();
	        	toolTipPrefix = "Flux Reaction : ";
	        } else if (userObj instanceof DataSymbol) {			// --- field data
	        	icon = fieldDataItemIcon;
	        	labelText = ((DataSymbol)userObj).getName();
	        	toolTipPrefix = "Field Data : ";
	        } else if (userObj instanceof BioEvent) {			// --- event
	        	BioEvent bioEvent = (BioEvent)userObj;
	        	SimulationContext simulationContext = bioEvent.getSimulationContext();
				if (simulationContext.getGeometry() != null && simulationContext.getGeometry().getDimension() > 0 
						|| simulationContext.isStoch()) {
					setEnabled(false);
					setDisabledIcon(this.getClosedIcon());
				} else {
					icon = eventIcon;
					labelText = bioEvent.getName();
					toolTipPrefix = "Event : ";
				}
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
		}
        return this;
    }

}
