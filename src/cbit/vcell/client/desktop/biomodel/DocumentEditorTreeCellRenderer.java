package cbit.vcell.client.desktop.biomodel;

/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 �*/
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeCellRenderer;

@SuppressWarnings("serial")
public abstract class DocumentEditorTreeCellRenderer extends DefaultTreeCellRenderer {
	protected Font regularFont = null;
	protected Font boldFont = null;
	
	private JTree ownerTree;

	public DocumentEditorTreeCellRenderer(JTree tree) {
		super();
		ownerTree = tree;
		setPreferredSize(new Dimension(170,30));
		setBorder(new EmptyBorder(0, 2, 0, 0));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (getForeground() == getTextSelectionColor()) {
			String text = getText();
			if (text != null) {
				FontMetrics metrics = getFontMetrics(getFont());
				int textLength = metrics.stringWidth(text);
				int extraLength = getWidth() - textLength;
				if (extraLength > 3) {
					// empty border 2
					int startX = 2 + (getIcon() == null ? 0 : getIcon().getIconWidth() + getIconTextGap());
					int startY = 0; //You probably have some vertical offset to add here.
					int height = ownerTree.getRowHeight();
					g.setColor(Color.white);
					g.fillRect(startX + textLength + extraLength/3, startY, extraLength*2/3, height);
				}
			}
		}
	}
}
