package cbit.vcell.client.desktop.simulation;

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

import cbit.vcell.client.desktop.simulation.SimulationListTreeModel.SimulationListTreeFolderNode;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.solver.Simulation;
 
public class SimulationListTreeCellRenderer extends DefaultTreeCellRenderer  {

	private Icon simulationIcon;	
	private Icon outputFunctionIcon;	
	
	public SimulationListTreeCellRenderer() {
		super();
		setPreferredSize(new Dimension(170,30));
    	simulationIcon = new ImageIcon(getClass().getResource("/images/run2_16x16.gif"));
	    outputFunctionIcon = new ImageIcon(getClass().getResource("/icons/function_icon.png"));
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
	    	if (userObj instanceof SimulationWorkspace) { 
	    		labelText = ((SimulationWorkspace)userObj).getSimulationOwner().getName();
	        	toolTipPrefix = "";
	    	} else if (userObj instanceof SimulationListTreeFolderNode) {
	    		SimulationListTreeFolderNode folder = (SimulationListTreeFolderNode)userObj; 
	        	labelText = folder.getName();	        	
	    	} else if (userObj instanceof Simulation) {
	    		icon = simulationIcon;
	        	labelText = ((Simulation)userObj).getName();
	        	toolTipPrefix = "Simulation : ";
	        } else if (userObj instanceof AnnotatedFunction) {
	        	icon = outputFunctionIcon;
	        	labelText = ((AnnotatedFunction)userObj).getName();
	        	toolTipPrefix = "Output Function : ";
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
