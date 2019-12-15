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

import javax.swing.JLabel;
/**
 * Insert the type's description here.
 * Creation date: (7/27/2000 6:30:41 PM)
 * @author: 
 */
import javax.swing.JTree;

import org.vcell.util.document.BioModelChildSummary.MathType;
import org.vcell.util.BeanUtils;
import org.vcell.util.document.PublicationInfo;
import org.vcell.util.gui.DialogUtils;
import org.vcell.util.gui.VCellIcons;

import cbit.vcell.client.desktop.biomodel.BioModelPropertiesPanel;
import cbit.vcell.resource.PropertyLoader;
 
public class BioModelInfoCellRenderer extends VCellBasicCellRenderer {
	
/**
 * MyRenderer constructor comment.
 */
public BioModelInfoCellRenderer() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (11/6/2002 5:13:26 PM)
 * @return int
 * @param node cbit.vcell.desktop.BioModelNode
 * @deprecated
 */
int getMaxErrorLevel(BioModelNode node) {
	//if (node.getUserObject() instanceof SolverResultSetInfo){
		//// parent should be a simulationInfo
		//// parent's parent should be a simulationContextInfo (with a SimContextStat)
		//SimulationContextInfo scInfo = (SimulationContextInfo)((BioModelNode)node.getParent().getParent()).getUserObject();
		//cbit.vcell.modeldb.SimContextStatus scStatus = scInfo.getSimContextStatus();
		//if (scStatus!=null){
			//return scStatus.getErrorLevel();
		//}else{
			//return cbit.vcell.modeldb.SimContextStatus.ERROR_NONE;
		//}
	//}else if (node.getUserObject() instanceof SimulationContextInfo){
		//SimulationContextInfo scInfo = (SimulationContextInfo)node.getUserObject();
		//cbit.vcell.modeldb.SimContextStatus scStatus = scInfo.getSimContextStatus();
		//if (scStatus!=null){
			//return scStatus.getErrorLevel();
		//}else{
			//return cbit.vcell.modeldb.SimContextStatus.ERROR_NONE;
		//}
	//}else{
		//return super.getMaxErrorLevel(node);
	//}
	return BioModelNode.ERROR_NONE;
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
			
		}else if (node.getUserObject() instanceof String && "Applications".equals(node.getRenderHint("type"))) {
			String label = (String)node.getUserObject();
			component.setToolTipText("Applications List");
			component.setText("<html><b>" + label + "</b></html>");
			setIcon(fieldTextIcon);
						
		}else if (node.getUserObject() instanceof String && "SimulationContext".equals(node.getRenderHint("type"))) {
			String label = (String)node.getUserObject();
			component.setToolTipText("Application");
			component.setText(label);
			
			if(MathType.RuleBased.getDescription().equals(node.getRenderHint("appType"))) {
				setIcon(VCellIcons.appRbmNonspIcon);
			} else if(MathType.Stochastic.getDescription().equals(node.getRenderHint("appType"))) {
				if("0".equals(node.getRenderHint("dimension"))) {
					setIcon(VCellIcons.appStoNonspIcon);
				} else {
					setIcon(VCellIcons.appStoSpatialIcon);
				}
			} else if(MathType.Deterministic.getDescription().equals(node.getRenderHint("appType"))) {
				if("0".equals(node.getRenderHint("dimension"))) {
					setIcon(VCellIcons.appDetNonspIcon);
				} else {
					setIcon(VCellIcons.appDetSpatialIcon);
				}
			} else {
				setIcon(fieldSimulationContextIcon);	// don't know what it is, use old "generic" icon
			}
			
		}else if (node.getUserObject() instanceof String && "Simulation".equals(node.getRenderHint("type"))) {
			String label = (String)node.getUserObject();
			component.setToolTipText("Simulation");
			component.setText(label);
			setIcon(fieldSimulationIcon);

		}else if (node.getUserObject() instanceof String && "AppType".equals(node.getRenderHint("type"))) {
			String label = (String)node.getUserObject();
			component.setToolTipText("Simulation");
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
			} else {
				pmid = "?";		// something that we don't know how to parse
			}
			String text = "<a href=\"" + info.getUrl() + "\">" + "PubMed PMID: " + pmid + "</a>";
			component.setText("<html>" + text + "</html>");
			setIcon(null);

		} else {
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
