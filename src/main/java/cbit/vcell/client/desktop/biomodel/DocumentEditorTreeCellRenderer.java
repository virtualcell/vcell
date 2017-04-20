/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel;

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
