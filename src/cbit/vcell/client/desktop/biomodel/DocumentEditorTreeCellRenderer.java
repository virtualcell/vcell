package cbit.vcell.client.desktop.biomodel;

/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 �*/
import java.awt.Font;

import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeCellRenderer;

@SuppressWarnings("serial")
public abstract class DocumentEditorTreeCellRenderer extends DefaultTreeCellRenderer {
	protected Font regularFont = null;
	protected Font boldFont = null;
	
	public DocumentEditorTreeCellRenderer() {
		super();
		setBorder(new EmptyBorder(0, 2, 0, 0));
	}

}
