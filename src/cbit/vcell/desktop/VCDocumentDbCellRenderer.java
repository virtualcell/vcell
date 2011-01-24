package cbit.vcell.desktop;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
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
}

public VCDocumentDbCellRenderer() {
	this(null);
}

public final void setSessionUser(User sessionUser) {
	this.sessionUser = sessionUser;
}

}