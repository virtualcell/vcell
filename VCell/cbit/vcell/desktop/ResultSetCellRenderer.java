package cbit.vcell.desktop;
import cbit.util.User;
import cbit.vcell.mathmodel.*;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.solver.*;
import cbit.vcell.biomodel.*;
/**
 * Insert the type's description here.
 * Creation date: (7/27/2000 6:30:41 PM)
 * @author: 
 */
import javax.swing.*;
 
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


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 8:35:45 AM)
 * @return javax.swing.Icon
 * @param nodeUserObject java.lang.Object
 */
protected void setComponentProperties(JLabel component, SolverResultSetInfo rsInfo) {
	super.setComponentProperties(component,rsInfo);

	//component.setIcon(fieldSimulationIcon);
	//component.setToolTipText("Simulation Results");
	component.setText("ResultSet ("+rsInfo.getStartingDate()+")");
}
}