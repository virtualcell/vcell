package cbit.vcell.desktop;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.mathmodel.*;
import java.math.BigDecimal;
import cbit.sql.Version;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.math.*;
import cbit.vcell.solver.*;
import cbit.vcell.mapping.*;
import cbit.vcell.model.*;
import cbit.vcell.biomodel.*;
import cbit.vcell.geometry.GeometryInfo;
import java.awt.Font;
/**
 * Insert the type's description here.
 * Creation date: (7/27/2000 6:30:41 PM)
 * @author: 
 */
import javax.swing.*;

import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
 
public class GeometryCellRenderer extends VCellBasicCellRenderer {
	private User sessionUser = null;

/**
 * MyRenderer constructor comment.
 */
public GeometryCellRenderer(User argSessionUser) {
	super();
	this.sessionUser = argSessionUser;
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
			if (node.getUserObject() instanceof User && node.getChildCount()>0 && (((BioModelNode)node.getChildAt(0)).getUserObject() instanceof String) && ((BioModelNode)(node.getChildAt(0).getChildAt(0))).getUserObject() instanceof GeometryInfo){
				//
				// Check if node is a User, with at least one child which is a string (Geometry name)
				// and if the child's child is a BioModelInfo node
				//
				String label = null;
				if (sessionUser != null && sessionUser.compareEqual((User)node.getUserObject())) {
					label = "My Geometries ("+((User)node.getUserObject()).getName()+")";
				} else {
					label = ((User)node.getUserObject()).getName()+"                              ";
				}
				component.setToolTipText("User Name");
				component.setText(label);
				
				if (isLoaded((User)node.getUserObject())) {
					bLoaded = true;
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
 * Creation date: (5/8/01 9:29:31 AM)
 * @return boolean
 * @param geometryInfo cbit.vcell.geometry.GeometryInfo
 * @deprecated
 */
protected boolean isLoaded(GeometryInfo geometryInfo) {
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 9:29:31 AM)
 * @return boolean
 * @param geometryInfo cbit.vcell.geometry.GeometryInfo
 * @deprecated
 */
protected boolean isLoaded(User user) {
	return false;
}
}