/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.desktop;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import javax.swing.JLabel;
/**
 * Insert the type's description here.
 * Creation date: (7/27/2000 6:30:41 PM)
 * @author: 
 */
import javax.swing.JTree;

import org.vcell.util.document.BioModelChildSummary.MathType;
import org.vcell.util.document.PublicationInfo;
import org.vcell.util.document.User;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.VCellIcons;

import cbit.vcell.client.desktop.biomodel.BioModelPropertiesPanel;
 
public class PublicationInfoNodeCellRenderer extends VCellBasicCellRenderer {
	
/**
 * MyRenderer constructor comment.
 */
public PublicationInfoNodeCellRenderer() {
	super();
}

int getMaxErrorLevel(BioModelNode node) {
	return BioModelNode.ERROR_NONE;
}

public java.awt.Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
	JLabel component = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
	//
	if (!leaf && expanded) {
		setIcon(fieldFolderOpenIcon);
	}else if (!leaf && !expanded) {
		setIcon(fieldFolderClosedIcon);
	}
	try {
	if (value instanceof BioModelNode) {
		BioModelNode node = (BioModelNode) value;
		
		if (node.getUserObject() instanceof String && "PublicationsInfo".equals(node.getRenderHint("type"))) {
			String label = (String)node.getUserObject();
			component.setToolTipText("Publications Info");
			component.setText("<html><b>" + label + "</b></html>");
			setIcon(fieldTextIcon);
						
		} else if (node.getUserObject() instanceof String && "ModelsList".equals(node.getRenderHint("type"))) {
			String label = (String)node.getUserObject();
			component.setToolTipText("Model(s) mentioned in this Publication");
			component.setText("<html><b>" + label + "</b></html>");
			setIcon(fieldTextIcon);

		}else if (node.getUserObject() instanceof PublicationInfo && "PublicationInfoTitle".equals(node.getRenderHint("type"))) {
			PublicationInfo info = (PublicationInfo)node.getUserObject();
			component.setToolTipText("Title");
			String text = "<b>" + info.getTitle() + "</b>";
			component.setText("<html>" + text + "</html>");
			setIcon(fieldTextIcon);
						
		}else if (node.getUserObject() instanceof PublicationInfo && "PublicationInfoAuthors".equals(node.getRenderHint("type"))) {
			PublicationInfo info = (PublicationInfo)node.getUserObject();
			component.setToolTipText("Authors");
			String text = "";
			int count = 0;
			for(String author : info.getAuthors()) {
				if(count > 0) {
					text += "; ";
				}
				text += author;
				count++;
			}
			component.setText("<html>" + text + "</html>");
			setIcon(null);
						
		}else if (node.getUserObject() instanceof PublicationInfo && "PublicationInfoCitation".equals(node.getRenderHint("type"))) {
			PublicationInfo info = (PublicationInfo)node.getUserObject();
			component.setToolTipText("Citation");
			String text = "";
			text += info.getCitation();
			component.setText("<html>" + text + "</html>");
			setIcon(null);
						
		}else if (node.getUserObject() instanceof PublicationInfo && "PublicationInfoDoi".equals(node.getRenderHint("type"))) {
			PublicationInfo info = (PublicationInfo)node.getUserObject();
			component.setToolTipText("DOI");
			String text = "<a href=\"" + "https://doi.org/" + info.getDoi() + "\">" + "DOI: " + info.getDoi() + "</a>";
			component.setText("<html>" + text + "</html>");
			setIcon(null);
			
		}else if (node.getUserObject() instanceof PublicationInfo && "PublicationInfoUrl".equals(node.getRenderHint("type"))) {
			PublicationInfo info = (PublicationInfo)node.getUserObject();
			component.setToolTipText("PMID");
			String pmid = info.getUrl();	// we know from the tree model that this is not null or empty
			if(pmid.contains("list_uids=")) {	// ex: http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=PubMed&dopt=Citation&list_uids=12644446
				pmid = pmid.substring(pmid.lastIndexOf("list_uids=")+"list_uids=".length());
			} else if(pmid.contains("pubmed/")) {	// ex: http://www.ncbi.nlm.nih.gov/pubmed/23093806
				pmid = pmid.substring(pmid.lastIndexOf("/")+1);
			} else {
				pmid = "?";		// something that we don't know how to parse
			}
			String text = "<a href=\"" + info.getUrl() + "\">" + "PubMed PMID: " + pmid + "</a>";
			component.setText("<html>" + text + "</html>");
			setIcon(null);

		} else if(node.getUserObject() instanceof String && "ModelName".equals(node.getRenderHint("type"))) {
			String label = (String)node.getUserObject();
			component.setToolTipText("Model Name");
			component.setText("<html><b>" + label + "</b></html>");
			if("BioModel".equals(node.getRenderHint("category"))) {
				component.setToolTipText("BioModel Name");
				setIcon(fieldBioModelIcon);
			} else if("MathModel".equals(node.getRenderHint("category"))) {
				component.setToolTipText("MathModel Name");
				setIcon(fieldMathModelIcon);
			} else if("Geometry".equals(node.getRenderHint("category"))) {
				component.setToolTipText("Geometry Name");
				setIcon(fieldGeometryIcon);
			} else {
				component.setToolTipText("Document Name");
				setIcon(fieldTextIcon);
			}
			
		} else if(node.getUserObject() instanceof User && "ModelOwner".equals(node.getRenderHint("type"))) {
			String label = ((User)node.getUserObject()).getName();
			component.setToolTipText("Model Owner");
			component.setText("<html>" + label + "</html>");
			setIcon(fieldUsersIcon);
			
		} else if(node.getUserObject() instanceof Date && "ModelDate".equals(node.getRenderHint("type"))) {
			Date date = (Date)node.getUserObject();
			component.setToolTipText("Model Creation Date");
			component.setText("<html>" + date + "</html>");
			setIcon(fieldCalendarIcon);
			
		} else {
			setComponentProperties(component,node.getUserObject());
			
		}
		
		if (selectedFont==null && component.getFont()!=null) { selectedFont = component.getFont().deriveFont(Font.BOLD); }
		if (unselectedFont==null && component.getFont()!=null) { unselectedFont = component.getFont().deriveFont(Font.PLAIN); }
		component.setFont(unselectedFont);
	}
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
	//
	return component;
}
}
