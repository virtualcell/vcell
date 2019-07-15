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

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;

import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.document.BioModelChildSummary;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.Version;

import cbit.image.VCImageInfo;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathDescription;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
 
public class VCellBasicCellRenderer extends javax.swing.tree.DefaultTreeCellRenderer {
	public static class VCDocumentInfoNode {
		private VCDocumentInfo vcDocInfo = null;
	
		public VCDocumentInfoNode(VCDocumentInfo vcDocInfo) {
			super();
			this.vcDocInfo = vcDocInfo;
		}
	
		public final VCDocumentInfo getVCDocumentInfo() {
			return vcDocInfo;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof VCDocumentInfoNode) {
				return vcDocInfo.getVersion().getName().equals(((VCDocumentInfoNode)obj).vcDocInfo.getVersion().getName());
			}
			return false;
		}

		@Override
		public int hashCode() {
			return vcDocInfo.hashCode();
		}		
	}

	protected javax.swing.Icon fieldMathModelIcon = null;
	protected javax.swing.Icon fieldBioModelIcon = null;
	protected javax.swing.Icon fieldBioModelErrorIcon = null;
	protected javax.swing.Icon fieldBioModelWarningIcon = null;
	protected javax.swing.Icon fieldGeometryIcon = null;
	protected javax.swing.Icon fieldAppTypeIcon = null;
	protected javax.swing.Icon fieldSimulationIcon = null;
	protected javax.swing.Icon fieldSimulationErrorIcon = null;
	protected javax.swing.Icon fieldSimulationWarningIcon = null;
	protected javax.swing.Icon fieldSimulationContextIcon = null;
	protected javax.swing.Icon fieldSimulationContextErrorIcon = null;
	protected javax.swing.Icon fieldSimulationContextWarningIcon = null;
	protected javax.swing.Icon fieldTextIcon = null;
	protected javax.swing.Icon fieldUserIcon = null;
	protected javax.swing.Icon fieldUsersIcon = null;
	protected javax.swing.Icon fieldCalendarIcon = null;
	protected javax.swing.Icon fieldFolderOpenIcon = null;
	protected javax.swing.Icon fieldFolderOpenErrorIcon = null;
	protected javax.swing.Icon fieldFolderOpenWarningIcon = null;
	protected javax.swing.Icon fieldFolderClosedIcon = null;
	protected javax.swing.Icon fieldFolderSelfIcon = null;
	protected javax.swing.Icon fieldFolderUserIcon = null;
	protected javax.swing.Icon fieldFolderSharedIcon = null;
	protected javax.swing.Icon fieldFolderPublicIcon = null;
	protected javax.swing.Icon fieldFolderWeakPublicIcon = null;
	protected javax.swing.Icon fieldFolderMediumPublicIcon = null;
	protected javax.swing.Icon fieldFolderPublishedIcon = null;
	protected javax.swing.Icon fieldFolderCuratedIcon = null;
	protected javax.swing.Icon fieldFolderBricksIcon = null;
	protected javax.swing.Icon fieldFolderWarningIcon = null;
	protected javax.swing.Icon fieldFolderClosedErrorIcon = null;
	protected javax.swing.Icon fieldFolderClosedWarningIcon = null;
	protected java.awt.Font selectedFont = null;
	protected java.awt.Font unselectedFont = null;

/**
 * MyRenderer constructor comment.
 */
public VCellBasicCellRenderer() {
	super();
	fieldMathModelIcon = new ImageIcon(getClass().getResource("/icons/mathModel.png"));
	fieldBioModelIcon = new ImageIcon(getClass().getResource("/icons/biomodel.png"));
	fieldBioModelErrorIcon = new ImageIcon(getClass().getResource("/images/bioModelError_16x16.gif"));
	fieldBioModelWarningIcon = new ImageIcon(getClass().getResource("/images/bioModelWarning_16x16.gif"));
	fieldGeometryIcon = new ImageIcon(getClass().getResource("/images/geometry2_16x16.gif"));
	fieldAppTypeIcon = new ImageIcon(getClass().getResource("/images/type.gif"));
	fieldSimulationIcon = new ImageIcon(getClass().getResource("/images/run2_16x16.gif"));
	fieldSimulationErrorIcon = new ImageIcon(getClass().getResource("/images/run2Error_16x16.gif"));
	fieldSimulationWarningIcon = new ImageIcon(getClass().getResource("/images/run2Warning_16x16.gif"));
	fieldSimulationContextIcon = new ImageIcon(getClass().getResource("/images/application3_16x16.gif"));
	fieldSimulationContextErrorIcon = new ImageIcon(getClass().getResource("/images/application3Error_16x16.gif"));
	fieldSimulationContextWarningIcon = new ImageIcon(getClass().getResource("/images/application3Warning_16x16.gif"));
	fieldTextIcon = new ImageIcon(getClass().getResource("/images/text_16x16.gif"));
	fieldUserIcon = new ImageIcon(getClass().getResource("/icons/aUser.png"));
	fieldUsersIcon = new ImageIcon(getClass().getResource("/icons/aUsers.png"));
	fieldCalendarIcon = new ImageIcon(getClass().getResource("/icons/calendar.png"));
	fieldFolderClosedIcon = new ImageIcon(getClass().getResource("/images/closedFolder_16x16.gif"));
	fieldFolderClosedErrorIcon = new ImageIcon(getClass().getResource("/images/closedFolderError_16x16.gif"));
	fieldFolderClosedWarningIcon = new ImageIcon(getClass().getResource("/images/closedFolderWarning_16x16.gif"));
	fieldFolderOpenIcon = new ImageIcon(getClass().getResource("/images/closedFolder_16x16.gif"));
	fieldFolderOpenErrorIcon = new ImageIcon(getClass().getResource("/images/closedFolderError_16x16.gif"));
	fieldFolderOpenWarningIcon = new ImageIcon(getClass().getResource("/images/closedFolderWarning_16x16.gif"));
	fieldFolderSelfIcon = new ImageIcon(getClass().getResource("/icons/selfFolder.png"));
	fieldFolderUserIcon = new ImageIcon(getClass().getResource("/icons/userFolder.png"));
	fieldFolderSharedIcon = new ImageIcon(getClass().getResource("/icons/sharedFolder2.png"));
	fieldFolderPublicIcon = new ImageIcon(getClass().getResource("/icons/publicFolder2.png"));
	fieldFolderWeakPublicIcon = new ImageIcon(getClass().getResource("/icons/publicFolder.png"));
	fieldFolderMediumPublicIcon = new ImageIcon(getClass().getResource("/icons/publicFolder3.png"));
	fieldFolderPublishedIcon = new ImageIcon(getClass().getResource("/icons/publishedFolder.png"));
	fieldFolderCuratedIcon = new ImageIcon(getClass().getResource("/icons/curatedFolder.png"));
	fieldFolderBricksIcon = new ImageIcon(getClass().getResource("/icons/bricksFolder.png"));
	fieldFolderWarningIcon = new ImageIcon(getClass().getResource("/icons/warningFolder.png"));
}


/**
 * Insert the method's description here.
 * Creation date: (11/6/2002 5:13:26 PM)
 * @return int
 * @param node cbit.vcell.desktop.BioModelNode
 */
int getMaxErrorLevel(BioModelNode node) {
	Integer errorLevel = (Integer)node.getRenderHint(BioModelNode.MAX_ERROR_LEVEL);
	if (errorLevel==null){
		return 0;
	}else{
		return errorLevel.intValue();
	}
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
		Object userObject = node.getUserObject();
		if (userObject instanceof SimulationInfo) {
			//
			// Check if node is a SimulationInfo
			//
			setComponentProperties(component, (SimulationInfo)userObject);
			int maxErrorLevel = getMaxErrorLevel(node);
			if (maxErrorLevel==BioModelNode.ERROR_POSSIBLE){
				setIcon(fieldSimulationWarningIcon);
				component.setToolTipText("Simulation contains possibly invalid results");
			}else if (maxErrorLevel==BioModelNode.ERROR_CONFIRMED){
				setIcon(fieldSimulationErrorIcon);
				component.setToolTipText("Simulation contains invalid results");
			}
			
		}else if (userObject instanceof String && node.getChildCount()==0){
			component.setToolTipText(null);
			component.setText((String)userObject);
			
		}else if (userObject instanceof BioModelInfo) {
			//
			// Check if node is a BioModelInfo
			//
			setComponentProperties(component,(BioModelInfo)userObject);
			bLoaded = isLoaded((BioModelInfo)userObject);
			int maxErrorLevel = getMaxErrorLevel(node);
			if (maxErrorLevel==BioModelNode.ERROR_POSSIBLE){
				setIcon(fieldBioModelWarningIcon);
				component.setToolTipText("BioModel version: Has possibly invalid simulation results");
			}else if (maxErrorLevel==BioModelNode.ERROR_CONFIRMED){
				setIcon(fieldBioModelErrorIcon);
				component.setToolTipText("BioModel version: Has invalid simulation results");
			}
			
		}else if (userObject instanceof String && node.getChildCount()>0 && ((BioModelNode)node.getChildAt(0)).getUserObject() instanceof BioModelInfo){
			//
			// Check if node is a BioModelName (String), with children (at least one version of biomodel), and if the child is a
			// BioModelInfo node
			//
			String label = (String)userObject;
			component.setToolTipText("BioModel");
			component.setText(label);
			//
			// check if child is loaded
			//
			int maxErrorLevel = BioModelNode.ERROR_NONE;
			for (int i = 0; i < node.getChildCount(); i++){
				maxErrorLevel = Math.max(maxErrorLevel,getMaxErrorLevel((BioModelNode)node.getChildAt(i)));
			}
			if (maxErrorLevel==BioModelNode.ERROR_POSSIBLE){

				if (!leaf && expanded) {
					setIcon(fieldFolderOpenWarningIcon);
				}else if (!leaf && !expanded) {
					setIcon(fieldFolderClosedWarningIcon);
				}
				component.setToolTipText("BioModel: one or more versions may have invalid simulation results");

			}else if (maxErrorLevel==BioModelNode.ERROR_CONFIRMED){
				if (!leaf && expanded) {
					setIcon(fieldFolderOpenErrorIcon);
				}else if (!leaf && !expanded) {
					setIcon(fieldFolderClosedErrorIcon);
				}
				component.setToolTipText("BioModel: one or more versions have invalid simulation results");

			}
			//
			// check if child is loaded
			//
			for (int i = 0; i < node.getChildCount(); i++){
				BioModelInfo bioModelInfo = (BioModelInfo)((BioModelNode)node.getChildAt(i)).getUserObject();
				if (isLoaded(bioModelInfo)){
					bLoaded = true;
				}
			}
			
		} else if (userObject instanceof User && node.getChildCount()>0 && (((BioModelNode)node.getChildAt(0)).getUserObject() instanceof String) && ((BioModelNode)(node.getChildAt(0).getChildAt(0))).getUserObject() instanceof BioModelInfo){
			//
			// Check if node is a User, with at least one child which is a string (BioModel name)
			// and if the child's child is a BioModelInfo node
			//
			String label = ((User)userObject).getName();
			component.setToolTipText("User Name");
			component.setText(label);
			
			if (isLoaded((User)userObject)) {
				bLoaded = true;
			}
			
		} else if (userObject instanceof MathModelInfo) {
			//
			// Check if node is a MathModelInfo node
			//
			setComponentProperties(component,(MathModelInfo)userObject);
			bLoaded = isLoaded((MathModelInfo)userObject);
			
		}else if (userObject instanceof String && node.getChildCount()>0 && ((BioModelNode)node.getChildAt(0)).getUserObject() instanceof MathModelInfo){
			//
			// Check if node is a MathModel name (String), with children (at least one version of mathmodel), and 
			// if the child is a MathModelInfo node
			//
			String label = (String)userObject;
			component.setToolTipText("Mathematical Model");
			component.setText(label);
			//
			// check if child is loaded
			//
			for (int i = 0; i < node.getChildCount(); i++){
				MathModelInfo mathModelInfo = (MathModelInfo)((BioModelNode)node.getChildAt(i)).getUserObject();
				if (isLoaded(mathModelInfo)){
					bLoaded = true;
				}
			}
			
		} else if (userObject instanceof User && node.getChildCount()>0 && (((BioModelNode)node.getChildAt(0)).getUserObject() instanceof String) && ((BioModelNode)(node.getChildAt(0).getChildAt(0))).getUserObject() instanceof MathModelInfo){
			//
			// Check if node is a User, with at least one child which is a string (Mathmodel name)
			// and if the child's child is a MathModelInfo node
			//
			String label = ((User)userObject).getName();
			component.setToolTipText("User Name");
			component.setText(label);
			
			if (isLoaded((User)userObject)) {
				bLoaded = true;
			}
			
		} else if (userObject instanceof cbit.vcell.geometry.GeometryInfo) {
			//
			// Check if node is a GeometryInfo
			//
			setComponentProperties(component,(GeometryInfo)userObject);
			bLoaded = isLoaded((GeometryInfo)userObject);
			
		}else if (userObject instanceof String && node.getChildCount()>0 && ((BioModelNode)node.getChildAt(0)).getUserObject() instanceof GeometryInfo){
			//
			// Check if node is a Geometry name (String), with children (at least one version of Geometry), and 
			// if the child is a GeometryInfo node
			//
			String label = (String)userObject;
			component.setToolTipText("Geometry");
			component.setText(label);
			//
			// check if child is loaded
			//
			for (int i = 0; i < node.getChildCount(); i++){
				GeometryInfo geometryInfo = (GeometryInfo)((BioModelNode)node.getChildAt(i)).getUserObject();
				if (isLoaded(geometryInfo)){
					bLoaded = true;
				}
			}
			
		} else if (userObject instanceof User && node.getChildCount()>0 && (((BioModelNode)node.getChildAt(0)).getUserObject() instanceof String) && ((BioModelNode)(node.getChildAt(0).getChildAt(0))).getUserObject() instanceof GeometryInfo){
			//
			// Check if node is a User, with at least one child which is a string (Geometry name)
			// and if the child's child is a GeometryInfo node
			//
			String label = ((User)userObject).getName();
			component.setToolTipText("User Name");
			component.setText(label);
			
			if (isLoaded((User)userObject)) {
				bLoaded = true;
			}
			
		} else if (userObject instanceof String && node.getChildCount()>0){
			component.setToolTipText(null);
			component.setText((String)userObject);
			
		//}else if (node.getUserObject() instanceof MathInfo) {
			//setComponentProperties(component,(MathInfo)node.getUserObject());
			
		}else if (userObject instanceof VCImageInfo) {
			setComponentProperties(component,(VCImageInfo)userObject);
			
		}else if (userObject instanceof Extent) {
			setComponentProperties(component,(Extent)userObject);
						
		}else if (userObject instanceof Annotation) {
			setComponentProperties(component,(Annotation)userObject);
						
		}else if (userObject instanceof MathModel) {
			setComponentProperties(component,(MathModel)userObject);
			
		}else if (userObject instanceof BioModel) {
			setComponentProperties(component,(BioModel)userObject);
			
		} else if (userObject instanceof SimulationContext) {
			setComponentProperties(component, (SimulationContext)userObject);
			bLoaded = isLoaded((SimulationContext)userObject);
			
		} else if (userObject instanceof Simulation) {
			setComponentProperties(component, (Simulation)userObject);
			
		} else if (userObject instanceof MathDescription) {
			setComponentProperties(component, (MathDescription)userObject);
			
		} else if (userObject instanceof Geometry) {
			setComponentProperties(component, (Geometry)userObject);
			
		}else if (userObject instanceof User) {
			setComponentProperties(component,(User)userObject);

		} else {
			setComponentProperties(component,userObject);
			
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


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 9:29:31 AM)
 * @return boolean
 * @param geometryInfo cbit.vcell.geometry.GeometryInfo
 */
protected boolean isLoaded(BioModelInfo bioModelInfo) {
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 9:29:31 AM)
 * @return boolean
 * @param geometryInfo cbit.vcell.geometry.GeometryInfo
 */
protected boolean isLoaded(GeometryInfo geometryInfo) {
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 9:29:31 AM)
 * @return boolean
 * @param geometryInfo cbit.vcell.geometry.GeometryInfo
 */
protected boolean isLoaded(SimulationContext simulationContext) {
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 9:29:31 AM)
 * @return boolean
 * @param geometryInfo cbit.vcell.geometry.GeometryInfo
 */
protected boolean isLoaded(MathModelInfo mathModelInfo) {
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 9:29:31 AM)
 * @return boolean
 * @param geometryInfo cbit.vcell.geometry.GeometryInfo
 */
protected boolean isLoaded(User user) {
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 8:35:45 AM)
 * @return javax.swing.Icon
 * @param nodeUserObject java.lang.Object
 */
protected void setComponentProperties(JLabel component, VCImageInfo imageInfo) {

	//component.setIcon(new ImageIcon(imageInfo.getBrowseGif().getGifEncodedData()));
	
	Version version = imageInfo.getVersion();
	String access = (version.getGroupAccess().getDescription());
	component.setToolTipText("Image");
	ISize size = imageInfo.getISize();
	String description = null;
	if (size.getZ()>1){
		description = "3D image ("+size.getX()+","+size.getY()+","+size.getZ()+")";
	}else if (size.getY()>1){
		description = "2D image ("+size.getX()+","+size.getY()+")";
	}else{
		description = "1D image ("+size.getX()+")";
	}
	component.setText(access+" "+description+" \""+version.getName()+"\""+"  ("+version.getDate()+")");
	
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 8:35:45 AM)
 * @return javax.swing.Icon
 * @param nodeUserObject java.lang.Object
 */
protected void setComponentProperties(JLabel component, Extent extent) {

	//component.setIcon(fieldTextIcon);
	
	component.setToolTipText("Size");
	component.setText("size = "+extent+" microns");
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 8:35:45 AM)
 * @return javax.swing.Icon
 * @param nodeUserObject java.lang.Object
 */
protected void setComponentProperties(JLabel component, BioModel bioModel) {

	component.setIcon(fieldBioModelIcon);
	
	component.setToolTipText("Biological Model");
	component.setText(bioModel.getName());
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 8:35:45 AM)
 * @return javax.swing.Icon
 * @param nodeUserObject java.lang.Object
 */
protected void setComponentProperties(JLabel component, BioModelInfo bioModelInfo) {

	component.setIcon(fieldBioModelIcon);
	
	Version version = bioModelInfo.getVersion();
	component.setToolTipText("BioModel ("+bioModelInfo.getSoftwareVersion().getDescription()+")");
	
	String access = (version.getGroupAccess().getDescription());
	component.setText(access+" "+version.getDate().toString());
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 8:35:45 AM)
 * @return javax.swing.Icon
 * @param nodeUserObject java.lang.Object
 */
protected void setComponentProperties(JLabel component, cbit.vcell.desktop.Annotation annot) {

	component.setIcon(fieldTextIcon);
	
	component.setToolTipText(annot.toString());
	component.setText(org.vcell.util.TokenMangler.replaceSubString(annot.toString(),"\n"," "));
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 8:35:45 AM)
 * @return javax.swing.Icon
 * @param nodeUserObject java.lang.Object
 */
protected void setComponentProperties(JLabel component, Geometry geometry) {

	component.setIcon(fieldGeometryIcon);
	
	Version version = geometry.getVersion();
	component.setToolTipText("Geometry");
	String date = (version!=null)?(" ("+version.getDate().toString()+")"):" (unsaved)";
	if (geometry.getDimension()>0){
		component.setText("\""+geometry.getName()+"\""+date);
	}else{
		component.setText(BioModelChildSummary.COMPARTMENTAL_GEO_STR);
	}
	
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 8:35:45 AM)
 * @return javax.swing.Icon
 * @param nodeUserObject java.lang.Object
 */
protected void setComponentProperties(JLabel component, GeometryInfo geometryInfo) {

	component.setIcon(fieldGeometryIcon);
	
	Version version = geometryInfo.getVersion();
	String access = (version.getGroupAccess().getDescription());
	component.setToolTipText("Geometry");
	String description = null;
	if (geometryInfo.getDimension()>0){
		description = geometryInfo.getDimension()+"D "+((geometryInfo.getImageRef()!=null)?"image":"analytic")+" Geometry";
		component.setText(access+" "+description+" \""+version.getName()+"\""+"  ("+version.getDate()+")");
	}else{
		component.setText(access+" compartmental Geometry \""+version.getName()+"\""+"  ("+version.getDate()+")");
	}
	
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 8:35:45 AM)
 * @return javax.swing.Icon
 * @param nodeUserObject java.lang.Object
 */
protected void setComponentProperties(JLabel component, SimulationContext simContext) {

	component.setIcon(fieldSimulationContextIcon);
	
	component.setToolTipText("Application");
	component.setText(simContext.getName());
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 8:35:45 AM)
 * @return javax.swing.Icon
 * @param nodeUserObject java.lang.Object
 */
protected void setComponentProperties(JLabel component, MathDescription mathDesc) {

	component.setIcon(fieldMathModelIcon);
	
	component.setToolTipText("Math");
	component.setText("math");
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 8:35:45 AM)
 * @return javax.swing.Icon
 * @param nodeUserObject java.lang.Object
 */
protected void setComponentProperties(JLabel component, MathModel mathModel) {

	component.setIcon(fieldMathModelIcon);
	
	component.setToolTipText("Mathematical Model");
	component.setText(mathModel.getName());
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 8:35:45 AM)
 * @return javax.swing.Icon
 * @param nodeUserObject java.lang.Object
 */
protected void setComponentProperties(JLabel component, MathModelInfo mathModelInfo) {

	component.setIcon(fieldMathModelIcon);
	
	Version version = mathModelInfo.getVersion();
	component.setToolTipText("MathModel ("+mathModelInfo.getSoftwareVersion().getDescription()+")");
	String access = (version.getGroupAccess().getDescription());
	component.setText(access+" "+version.getDate().toString());
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 8:35:45 AM)
 * @return javax.swing.Icon
 * @param nodeUserObject java.lang.Object
 */
protected void setComponentProperties(JLabel component, User user) {

	//component.setIcon(fieldBioModelIcon);
	
	component.setToolTipText("Owner");
	component.setText(user.getName());
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 8:35:45 AM)
 * @return javax.swing.Icon
 * @param nodeUserObject java.lang.Object
 */
protected void setComponentProperties(JLabel component, Simulation sim) {

	component.setIcon(fieldSimulationIcon);
	
	component.setToolTipText("Simulation");
	component.setText(sim.getName());
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 8:35:45 AM)
 * @return javax.swing.Icon
 * @param nodeUserObject java.lang.Object
 */
protected void setComponentProperties(JLabel component, SimulationInfo simInfo) {

	component.setIcon(fieldSimulationIcon);
	
	component.setToolTipText("Simulation");
	component.setText(simInfo.getVersion().getName());
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 8:35:45 AM)
 * @return javax.swing.Icon
 * @param nodeUserObject java.lang.Object
 */
protected void setComponentProperties(JLabel component, Object object) {

	component.setToolTipText("");
	if (object == null){
		component.setText("null");
	}else{
		component.setText(object.toString());
	}
}
}
