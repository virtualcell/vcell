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
import java.awt.Font;
import java.util.Date;

import javax.swing.JLabel;
/**
 * Insert the type's description here.
 * Creation date: (7/27/2000 6:30:41 PM)
 * @author: 
 */
import javax.swing.JTree;

import org.vcell.util.BeanUtils;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.PublicationInfo;
import org.vcell.util.document.User;
import org.vcell.util.gui.VCellIcons;

import cbit.vcell.resource.PropertyLoader;
 
public class MathModelMetaDataCellRenderer extends VCellBasicCellRenderer {
/**
 * MyRenderer constructor comment.
 */
public MathModelMetaDataCellRenderer() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (7/27/2000 6:41:57 PM)
 * @return java.awt.Component
 */
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
		
		boolean bLoaded = false;

		//
		// Check if node is a SolverResultSetInfo
		//
		if (node.getUserObject() instanceof String && "Geometry".equals(node.getRenderHint("type"))) {
			String label = (String)node.getUserObject();
			component.setToolTipText("Geometry");
			component.setText(label);
			setIcon(fieldGeometryIcon);
			
		}else if (node.getUserObject() instanceof String && "SimulationContext".equals(node.getRenderHint("type"))) {
			String label = (String)node.getUserObject();
			component.setToolTipText("Application");
			component.setText(label);
			setIcon(fieldSimulationContextIcon);
			
		}else if (node.getUserObject() instanceof String && "Simulation".equals(node.getRenderHint("type"))) {
			String label = (String)node.getUserObject();
			component.setToolTipText("Simulation");
			component.setText(label);
			setIcon(fieldSimulationIcon);

		}else if (node.getUserObject() instanceof String && "AppType".equals(node.getRenderHint("type"))) {
			String label = (String)node.getUserObject();
			component.setToolTipText("Model Type");
			component.setText(label);
			setIcon(fieldAppTypeIcon);

		}else if (node.getUserObject() instanceof String && "Provenance".equals(node.getRenderHint("type"))) {
			String label = (String)node.getUserObject();
			component.setToolTipText("Provenance");
			component.setText("<html><b>" + label + "</b></html>");
			setIcon(fieldTextIcon);

		}else if (node.getUserObject() instanceof Annotation) {
			String label = ((Annotation)node.getUserObject()).toString();
			component.setToolTipText("Version Annotation");
			component.setText(label);
			setIcon(null);
			
			// ------------------------------------------------------
		}else if (node.getUserObject() instanceof String && "PublicationsInfo".equals(node.getRenderHint("type"))) {
			String label = (String)node.getUserObject();
			component.setToolTipText("Publications Info");
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
			String text = "<a href=\"" + BeanUtils.getDynamicClientProperties().getProperty(PropertyLoader.DOI_URL) + info.getDoi() + "\">" + "DOI: " + info.getDoi() + "</a>";
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
			} else if(pmid.contains("pubmed.ncbi")) {	// ex: https://pubmed.ncbi.nlm.nih.gov/34360784/
				if(pmid.endsWith("/")) {
					pmid = pmid.substring(0, pmid.length()-1);
				}
				pmid = pmid.substring(pmid.lastIndexOf("/")+1);
			} else {
				pmid = "?";		// something that we don't know how to parse
			}
			String text = "<a href=\"" + info.getUrl() + "\">" + "PubMed PMID: " + pmid + "</a>";
			component.setText("<html>" + text + "</html>");
			setIcon(null);

		} else if(node.getUserObject() instanceof String && "GeneralFileInfo".equals(node.getRenderHint("type"))) {
			component.setToolTipText("Database File Info");
			String text = "<b>" + (String)node.getUserObject() + "</b>";
			component.setText("<html>" + text + "</html>");
			setIcon(fieldDatabaseIcon);

		} else if(node.getUserObject() instanceof String && "ModelName".equals(node.getRenderHint("type"))) {
			component.setToolTipText("BioModel Name");
			String text = "<b>" + (String)node.getUserObject() + "</b>";
			component.setText("<html>" + text + "</html>");
//			setIcon(fieldTextIcon);

		} else if(node.getUserObject() instanceof KeyValue && "VCellIdentifier".equals(node.getRenderHint("type"))) {
			component.setToolTipText("Virtual Cell Identifier");
			KeyValue key = (KeyValue)node.getUserObject();
			String text = "mathmodel-" + key;
			component.setText("<html>" + text + "</html>");
			setIcon(fieldDatabaseModelKeyIcon);

		} else if(node.getUserObject() instanceof User && "ModelOwner".equals(node.getRenderHint("type"))) {
			String label = ((User)node.getUserObject()).getName();
			component.setToolTipText("Model Owner");
			component.setText("<html>" + label + "</html>");
			setIcon(fieldUserIcon);

		} else if(node.getUserObject() instanceof Date && "ModelDate".equals(node.getRenderHint("type"))) {
			Date date = (Date)node.getUserObject();
			component.setToolTipText("Last Modified");
			component.setText("<html>" + date + "</html>");
			setIcon(fieldCalendarIcon);

		} else if(node.getUserObject() instanceof String && "Permissions".equals(node.getRenderHint("type"))) {
			component.setToolTipText("Permissions");
			String text = (String)node.getUserObject();
			component.setText("<html>" + text + "</html>");
			setIcon(fieldPermissionsIcon);

		} else if(node.getUserObject() instanceof String && "Annotations".equals(node.getRenderHint("type"))) {
			component.setToolTipText("Text Annotation");
			String text = (String)node.getUserObject();
			component.setText("<html>" + text + "</html>");
			setIcon(VCellIcons.noteIcon);
			
		}else {
			setComponentProperties(component,node.getUserObject());
		}
		
		if (selectedFont==null && component.getFont()!=null) { selectedFont = component.getFont().deriveFont(Font.BOLD); }
		if (unselectedFont==null && component.getFont()!=null) { unselectedFont = component.getFont().deriveFont(Font.PLAIN); }
		
		if (bLoaded){
			component.setFont(selectedFont);
		}else{
			component.setFont(unselectedFont);
		}
	}
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
	//
	return component;
}
}
