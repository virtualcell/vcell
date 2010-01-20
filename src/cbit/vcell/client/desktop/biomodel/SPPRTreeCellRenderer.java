package cbit.vcell.client.desktop.biomodel;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.SpeciesContext;
 
public class SPPRTreeCellRenderer extends DefaultTreeCellRenderer  {

	private Icon speciesIcon;
	private Icon gParamIcon;
	private Icon aParamIcon;
	private Icon reactionsIcon;
	
	
	public SPPRTreeCellRenderer() {
		super();
		initializeIcons();
		}


    public boolean initializeIcons() {
	    ImageIcon speciesIcon = new ImageIcon(getClass().getResource("/images/speciesItem.gif"));
	    ImageIcon gParamIcon = new ImageIcon(getClass().getResource("/images/gparamItem.gif"));
	    ImageIcon aParamIcon = new ImageIcon(getClass().getResource("/images/aparamItem.gif"));
	    ImageIcon reactionsIcon = new ImageIcon(getClass().getResource("/images/reactionsItem.gif"));
    	
	    if((speciesIcon == null) || (gParamIcon == null) || (aParamIcon == null) || (reactionsIcon == null)) {
            System.out.println("At least one icon is missing.");
            return false;
	    }
    	this.speciesIcon = speciesIcon;
    	this.gParamIcon = gParamIcon;
    	this.aParamIcon = aParamIcon;
    	this.reactionsIcon = reactionsIcon;
    	return true;
    }


	public Component getTreeCellRendererComponent(
                        JTree tree,
                        Object value,
                        boolean sel,
                        boolean expanded,
                        boolean leaf,
                        int row,
                        boolean hasFocus) {
    	Color dkRed = new Color(128, 0, 0);		// fiziology
    	Color dkBlue = new Color(0, 0, 128);
    	Color dkGreen = new Color(0, 128, 0);
    	Color dkBlack = new Color(0, 0, 0);		// application

		JLabel component = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if (value instanceof SimulationContext) {		// --- not implemented
        	component.setText("SimulationContext : " + ((SimulationContext)value).getName());
        	component.setToolTipText("SimulationContext");
        } else if (leaf && isSpecies(value)) {			// --- species context
        	component.setIcon(speciesIcon);
        	if(sel) {
            	component.setForeground(Color.white);
        	} else {
            	component.setForeground(dkBlack);
        	}
        	component.setText((String)((BioModelNode)value).getUserObject());
//            System.out.println((String)((BioModelNode)value).getUserObject());
        	component.setToolTipText("Species context");
        } else if (leaf && isGlobalParam(value)) {		// --- global parameter
        	component.setIcon(gParamIcon);
        	if(sel) {
            	component.setForeground(Color.white);
        	} else {
            	component.setForeground(dkBlack);
        	}
        	component.setText((String)((BioModelNode)value).getUserObject());
        	component.setToolTipText("Global variable");
        } else if (leaf && isApplicationParam(value)) {	// --- not implemented
        	component.setIcon(aParamIcon);
        	component.setText((String)((BioModelNode)value).getUserObject());
        	component.setToolTipText("Application parameter");
        } else if (leaf && isReaction(value)) {			// --- reaction
        	component.setIcon(reactionsIcon);
        	if(sel) {
            	component.setForeground(Color.white);
        	} else {
            	component.setForeground(dkBlack);
        	}
        	component.setText((String)((BioModelNode)value).getUserObject());
        	component.setToolTipText("Reaction");
        } else if (leaf && isApplicationFunction(value)) {	// --- not implemented
        	component.setIcon(aParamIcon);
        	component.setText((String)((BioModelNode)value).getUserObject());
        	component.setToolTipText("Application Function");
        } else if (leaf && isApplicationEquation(value)) {	// --- not implemented
        	component.setIcon(aParamIcon);
        	component.setText((String)((BioModelNode)value).getUserObject());
        	component.setToolTipText("Application Equation");
        } else {
        	Object obj = ((BioModelNode)value).getUserObject(); 
        	if (obj instanceof String) {
//            	component.setForeground(Color.black);
        		component.setText((String)obj);
        	} else {
        		System.err.println(obj.toString());
        	}
        	component.setToolTipText(null); //no tool tip
        } 
        
        BioModelNode node = (BioModelNode)value;  
        Object obj = node.getUserObject();
        if (obj instanceof String) {
	        String name = (String)obj;   
	        if(name.equals(SPPRTreeModel.RATERULES_FOLDER)) {   // disabled entry for now
	        	component.setEnabled(false);
	        	component.setDisabledIcon(this.getClosedIcon());
	        	component.setToolTipText("Rate rules folder");
	        } else if(name.equals(SPPRTreeModel.SPECIES_FOLDER)) {
	        	component.setToolTipText("Species folder");
	        } else if(name.equals(SPPRTreeModel.APPLICATIONP_FOLDER)) {
	        	component.setToolTipText("Appl. param. folder");
	        } else if(name.equals(SPPRTreeModel.GLOBALP_FOLDER)) {
	        	component.setToolTipText("Global param. folder");
	        } else if(name.equals(SPPRTreeModel.REACTIONS_FOLDER)) {
	        	component.setToolTipText("Reactions folder");
	        } else if(name.equals(SPPRTreeModel.APP_FUNCTIONS_FOLDER)) {
	        	component.setEnabled(false);
	        	component.setDisabledIcon(this.getClosedIcon());
	        	component.setToolTipText("Application Functions folder");
	        } else if(name.equals(SPPRTreeModel.APP_EQUATIONS_FOLDER)) {
	        	component.setEnabled(false);
	        	component.setDisabledIcon(this.getClosedIcon());
	        	component.setToolTipText("Application Equations folder");
	        }
        }
        return component;
    }

    protected boolean isSpecies(Object value) {
        BioModelNode node = (BioModelNode)value;
        BioModelNode parent = (BioModelNode)node.getParent();
        String parentName = (String)(parent.getUserObject());
        if (parentName == SPPRTreeModel.SPECIES_FOLDER) {
            return true;
        }
        return false;
    }
    protected boolean isGlobalParam(Object value) {
        BioModelNode node = (BioModelNode)value;
        BioModelNode parent = (BioModelNode)node.getParent();
        String parentName = (String)(parent.getUserObject());
        if (parentName == SPPRTreeModel.GLOBALP_FOLDER) {
//            System.out.println("Global");
            return true;
        }
        return false;
    }
    protected boolean isApplicationParam(Object value) {
        BioModelNode node = (BioModelNode)value;
        BioModelNode parent = (BioModelNode)node.getParent();
        String parentName = (String)(parent.getUserObject());
        if (parentName == SPPRTreeModel.APPLICATIONP_FOLDER) {
            return true;
        }
        return false;
    }
    protected boolean isReaction(Object value) {
        BioModelNode node = (BioModelNode)value;
        BioModelNode parent = (BioModelNode)node.getParent();
        String parentName = (String)(parent.getUserObject());
        if (parentName == SPPRTreeModel.REACTIONS_FOLDER) {
            return true;
        }
        return false;
    }
    protected boolean isApplicationFunction(Object value) {
        BioModelNode node = (BioModelNode)value;
        BioModelNode parent = (BioModelNode)node.getParent();
        String parentName = (String)(parent.getUserObject());
        if (parentName == SPPRTreeModel.APP_FUNCTIONS_FOLDER) {
            return true;
        }
        return false;
    }
    protected boolean isApplicationEquation(Object value) {
        BioModelNode node = (BioModelNode)value;
        BioModelNode parent = (BioModelNode)node.getParent();
        String parentName = (String)(parent.getUserObject());
        if (parentName == SPPRTreeModel.APP_EQUATIONS_FOLDER) {
            return true;
        }
        return false;
    }

}
