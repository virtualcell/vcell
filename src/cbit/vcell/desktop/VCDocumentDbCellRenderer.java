package cbit.vcell.desktop;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.Dimension;

import javax.swing.JTree;

import org.vcell.util.document.User;
 
@SuppressWarnings("serial")
public class VCDocumentDbCellRenderer extends VCellBasicCellRenderer {
	
	protected User sessionUser = null;

/**
 * MyRenderer constructor comment.
 */
public VCDocumentDbCellRenderer(User argSessionUser) {
	super();
	this.sessionUser = argSessionUser;
	setPreferredSize(new Dimension(200, 20));
}

public VCDocumentDbCellRenderer() {
	this(null);
}

public final void setSessionUser(User sessionUser) {
	this.sessionUser = sessionUser;
}

public java.awt.Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
	super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
	if (value instanceof BioModelNode) {
		BioModelNode node = (BioModelNode) value;
		Object userObject = node.getUserObject();
		if (VCDocumentDbTreeModel.SHARED_BIO_MODELS.equals(userObject)
				|| VCDocumentDbTreeModel.SHARED_MATH_MODELS.equals(userObject)
				|| VCDocumentDbTreeModel.SHARED_GEOMETRIES.equals(userObject)
				|| VCDocumentDbTreeModel.Public_BioModels.equals(userObject)
				|| VCDocumentDbTreeModel.Public_MathModels.equals(userObject)
				|| VCDocumentDbTreeModel.Education.equals(userObject)
				|| VCDocumentDbTreeModel.Tutorials.equals(userObject)
				) {
			setText(getText() + " (" + node.getChildCount() + ")");
		}
	}
	return this;
}
}