package cbit.vcell.client.desktop.biomodel;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
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
//	    ImageIcon speciesIcon = new ImageIcon("C:/dan/work images/icons/species1.gif");
//	    ImageIcon gParamIcon = new ImageIcon("C:/dan/work images/icons/gparam3.gif");
//	    ImageIcon aParamIcon = new ImageIcon("C:/dan/work images/icons/aparam2.gif");
//	    ImageIcon reactionsIcon = new ImageIcon("C:/dan/work images/icons/reactions2.gif");
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

//    private ImageIcon createImageIcon(String path) {
//    	java.net.URL imgURL = getClass().getResource(path);
//        if (imgURL != null) {
//            return new ImageIcon(imgURL);
//        } else {
//            System.err.println("Couldn't find file: " + path);
//            return null;
//        }
//	}


	public Component getTreeCellRendererComponent(
                        JTree tree,
                        Object value,
                        boolean sel,
                        boolean expanded,
                        boolean leaf,
                        int row,
                        boolean hasFocus) {

		JLabel component = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if (value instanceof SimulationContext) {
        	component.setText("SimulationContext : " + ((SimulationContext)value).getName());
        	component.setToolTipText("SimulationContext");
        } else if (leaf && isSpecies(value)) {
        	component.setIcon(speciesIcon);
        	component.setText((String)((BioModelNode)value).getUserObject());
            System.out.println((String)((BioModelNode)value).getUserObject());
        	component.setToolTipText("Your usual species");
        } else if (leaf && isGlobalParam(value)) {
        	component.setIcon(gParamIcon);
        	component.setText((String)((BioModelNode)value).getUserObject());
        	component.setToolTipText("Your usual global");
        } else if (leaf && isApplicationParam(value)) {
        	component.setIcon(aParamIcon);
        	component.setText((String)((BioModelNode)value).getUserObject());
        	component.setToolTipText("Your usual param");
        } else if (leaf && isReaction(value)) {
        	component.setIcon(reactionsIcon);
        	component.setText((String)((BioModelNode)value).getUserObject());
        	component.setToolTipText("Your usual reaction");
        } else {
        	Object obj = ((BioModelNode)value).getUserObject(); 
        	if (obj instanceof String) {
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

}
