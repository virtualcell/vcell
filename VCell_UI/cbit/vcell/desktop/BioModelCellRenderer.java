package cbit.vcell.desktop;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.util.User;
import cbit.util.VersionFlag;

import cbit.vcell.mapping.*;
import cbit.vcell.modelapp.SimulationContext;
import cbit.vcell.biomodel.*;
/**
 * Insert the type's description here.
 * Creation date: (7/27/2000 6:30:41 PM)
 * @author: 
 */
import javax.swing.*;

 
public class BioModelCellRenderer extends VCellBasicCellRenderer {
	private User sessionUser = null;

/**
 * MyRenderer constructor comment.
 */
public BioModelCellRenderer(User argSessionUser) {
	super();
	this.sessionUser = argSessionUser;
}


/**
 * Insert the method's description here.
 * Creation date: (11/6/2002 4:41:23 PM)
 * @return int
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @deprecated
 */
int getMaxErrorLevel(BioModelInfo bioModelInfo) {
	//if (workspace!=null && workspace.getDocumentManager()!=null){
		//int worstErrorLevel = cbit.vcell.modeldb.SimContextStatus.ERROR_NONE;
		//cbit.vcell.clientdb.DocumentManager docManager = workspace.getDocumentManager();
		//try {
			//BioModelMetaData bmMetaData = docManager.getBioModelMetaData(bioModelInfo);
			//java.util.Enumeration enum1 = bmMetaData.getSimulationContextInfos();
			//while (enum1.hasMoreElements()){
				//SimulationContextInfo scInfo = (SimulationContextInfo)enum1.nextElement();
				//if (scInfo.getSimContextStatus()!=null){
					//worstErrorLevel = Math.max(worstErrorLevel,scInfo.getSimContextStatus().getErrorLevel());
				//}
			//}
			//return worstErrorLevel;
		//}catch (cbit.vcell.server.DataAccessException e){
			//e.printStackTrace();
		//}
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
	try {
		if (value instanceof BioModelNode) {
			BioModelNode node = (BioModelNode) value;
			boolean bLoaded = false;
			if (node.getUserObject() instanceof User && node.getChildCount()>0 && (((BioModelNode)node.getChildAt(0)).getUserObject() instanceof String) && ((BioModelNode)(node.getChildAt(0).getChildAt(0))).getUserObject() instanceof BioModelInfo){
				//
				// Check if node is a User, with at least one child which is a string (BioModel name)
				// and if the child's child is a BioModelInfo node
				//
				String label = null;
				if ( sessionUser != null && sessionUser.compareEqual((User)node.getUserObject())){
					label = "My Models ("+((User)node.getUserObject()).getName()+")";
				} else {
					label = ((User)node.getUserObject()).getName()+"                        ";
				}
				component.setToolTipText("User Name");
				component.setText(label);
				
				if (isLoaded((User)node.getUserObject())) {
					bLoaded = true;
				}
			}else if(node.getUserObject() instanceof BioModelInfo){
				BioModelInfo biomodelInfo = (BioModelInfo)node.getUserObject();
				if(biomodelInfo.getVersion().getFlag().compareEqual(VersionFlag.Archived)){
					component.setText("(Archived) "+component.getText());
				}else if(biomodelInfo.getVersion().getFlag().compareEqual(VersionFlag.Published)){
					component.setText("(Published) "+component.getText());
				}
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
 * Creation date: (5/8/01 10:34:18 AM)
 * @return boolean
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @deprecated
 */
protected boolean isLoaded(BioModelInfo bioModelInfo) {
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 10:34:18 AM)
 * @return boolean
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @deprecated
 */
protected boolean isLoaded(SimulationContext simulationContext) {
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 10:34:18 AM)
 * @return boolean
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 * @deprecated
 */
protected boolean isLoaded(User user) {
	return false;
}
}