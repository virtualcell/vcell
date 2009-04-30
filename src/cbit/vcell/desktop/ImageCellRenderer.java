package cbit.vcell.desktop;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
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
 
public class ImageCellRenderer extends VCellBasicCellRenderer {
	private org.vcell.util.document.User sessionUser = null;

/**
 * MyRenderer constructor comment.
 */
public ImageCellRenderer(User aUser) {
	super();
	this.sessionUser = aUser;
}


/**
 * Insert the method's description here.
 * Creation date: (7/27/2000 6:41:57 PM)
 * @return java.awt.Component
 */
public java.awt.Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
	JLabel component = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
	
	try {
		if (value instanceof BioModelNode) {
			BioModelNode node = (BioModelNode) value;
			if (node.getUserObject() instanceof User && node.getChildCount()>0 && (((BioModelNode)node.getChildAt(0)).getUserObject() instanceof String) && ((BioModelNode)(node.getChildAt(0).getChildAt(0))).getUserObject() instanceof cbit.image.VCImageInfo){
				//
				// Check if node is a User, with at least one child which is a string (Image name)
				// and if the child's child is a VCImageInfo node
				//
				String label = null;
				if ( sessionUser != null && sessionUser.compareEqual((User)node.getUserObject())){
					label = "My Images ("+((User)node.getUserObject()).getName()+")";
				} else {
					label = ((User)node.getUserObject()).getName()+"                        ";
				}
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
}