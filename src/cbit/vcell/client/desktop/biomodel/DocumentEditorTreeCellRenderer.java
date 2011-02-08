package cbit.vcell.client.desktop.biomodel;

/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 �*/
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.vcell.util.gui.VCellIcons;

import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderNode;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.Model;
import cbit.vcell.xml.gui.MiriamTreeModel.LinkNode;

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
	
	public Component getTreeCellRendererComponent(
                        JTree tree,
                        Object value,
                        boolean sel,
                        boolean expanded,
                        boolean leaf,
                        int row,
                        boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		if (regularFont == null) {
			regularFont = getFont();
			boldFont = regularFont.deriveFont(Font.BOLD);
		}
		Font font = regularFont;
		Icon icon = null;
    	String labelText = null;
    	String toolTipPrefix = "";
    	String toolTipSuffix = "";
		if (value instanceof LinkNode){
			LinkNode ln = (LinkNode)value;
			String link = ln.getLink();
			String text = ln.getText();
			String qualifier = ln.getMiriamQualifier().getDescription();
			if (link != null) {
				String colorString = (sel)?"white":"blue";
				toolTipPrefix = "double-click to open link " + link;
				labelText = "<html>"+qualifier+"&nbsp;<font color=\""+colorString+"\"><a href=" + link + ">" + text + "</a></font></html>";
			}else{
				String colorString = (sel)?"white":"black";
				labelText = "<html>"+qualifier+"&nbsp;<font color=\""+colorString+"\">" + text + "</font></html>";
			}
		} else if (value instanceof BioModelNode) {
	        BioModelNode node = (BioModelNode)value;
	        Object userObj = node.getUserObject();
	        if (userObj instanceof Model) {
	    		labelText = "Biological Model";
	    		font = boldFont;
	    	} else if (userObj instanceof SimulationContext) {		// --- root: application name
	    		font = boldFont;
	    		labelText = ((SimulationContext)userObj).getName();
	    		toolTipPrefix = "Application: ";
	    	} else if (userObj instanceof DocumentEditorTreeFolderNode) {		// --- 1st level folders
	    		DocumentEditorTreeFolderNode folder = (DocumentEditorTreeFolderNode)userObj;
	    		if (folder.isBold()) {
	    			font = boldFont;
	    		} 
	    		DocumentEditorTreeFolderClass folderClass = folder.getFolderClass();
	    		switch(folderClass) {
	    		case GEOMETRY_NODE:
	    			icon = VCellIcons.geometryIcon;
	    			break;
	    		case SETTINGS_NODE:
	    			icon = VCellIcons.settingsIcon;
	    			break;
	    		case PROTOCOLS_NODE:
	    			icon = VCellIcons.protocolsIcon;
	    			break;
	    		case SIMULATIONS_NODE:
	    			icon = VCellIcons.simulationIcon;
	    			break;
	    		case FITTING_NODE:
	    			icon = VCellIcons.fittingIcon;
	    			break;
	    		}
	    		labelText = folder.getName();
	        }
		}
		setIcon(icon);
		setFont(font);
		setText(labelText);
		if (toolTipSuffix.length() == 0) {
			toolTipSuffix = labelText;
		}
		setToolTipText(toolTipPrefix + toolTipSuffix);
        return this;
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
