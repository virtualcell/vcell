package cbit.vcell.desktop;
import cbit.util.ISize;
import cbit.image.VCImageInfo;
import cbit.util.Extent;
import cbit.util.document.BioModelInfo;
import cbit.util.document.MathModelInfo;
import cbit.util.document.User;
import cbit.util.document.Version;
import cbit.vcell.mathmodel.*;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.Font;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.math.*;
import cbit.vcell.simulation.*;
import cbit.vcell.mapping.*;
import cbit.vcell.modelapp.SimulationContext;
import cbit.vcell.modeldb.SolverResultSetInfo;
import cbit.vcell.biomodel.*;
import cbit.vcell.geometry.GeometryInfo;
/**
 * Insert the type's description here.
 * Creation date: (7/27/2000 6:30:41 PM)
 * @author: 
 */
import javax.swing.*;

 
public class VCellBasicCellRenderer extends javax.swing.tree.DefaultTreeCellRenderer {
	protected javax.swing.Icon fieldMathModelIcon = null;
	protected javax.swing.Icon fieldBioModelIcon = null;
	protected javax.swing.Icon fieldBioModelErrorIcon = null;
	protected javax.swing.Icon fieldBioModelWarningIcon = null;
	protected javax.swing.Icon fieldGeometryIcon = null;
	protected javax.swing.Icon fieldSimulationIcon = null;
	protected javax.swing.Icon fieldSimulationErrorIcon = null;
	protected javax.swing.Icon fieldSimulationWarningIcon = null;
	protected javax.swing.Icon fieldSimulationContextIcon = null;
	protected javax.swing.Icon fieldSimulationContextErrorIcon = null;
	protected javax.swing.Icon fieldSimulationContextWarningIcon = null;
	protected javax.swing.Icon fieldTextIcon = null;
	protected javax.swing.Icon fieldFolderOpenIcon = null;
	protected javax.swing.Icon fieldFolderOpenErrorIcon = null;
	protected javax.swing.Icon fieldFolderOpenWarningIcon = null;
	protected javax.swing.Icon fieldFolderClosedIcon = null;
	protected javax.swing.Icon fieldFolderClosedErrorIcon = null;
	protected javax.swing.Icon fieldFolderClosedWarningIcon = null;
	protected java.awt.Font selectedFont = null;
	protected java.awt.Font unselectedFont = null;

/**
 * MyRenderer constructor comment.
 */
public VCellBasicCellRenderer() {
	super();
	fieldMathModelIcon = new ImageIcon(getClass().getResource("/images/math_16x16.gif"));
	fieldBioModelIcon = new ImageIcon(getClass().getResource("/images/bioModel_16x16.gif"));
	fieldBioModelErrorIcon = new ImageIcon(getClass().getResource("/images/bioModelError_16x16.gif"));
	fieldBioModelWarningIcon = new ImageIcon(getClass().getResource("/images/bioModelWarning_16x16.gif"));
	fieldGeometryIcon = new ImageIcon(getClass().getResource("/images/geometry2_16x16.gif"));
	fieldSimulationIcon = new ImageIcon(getClass().getResource("/images/run2_16x16.gif"));
	fieldSimulationErrorIcon = new ImageIcon(getClass().getResource("/images/run2Error_16x16.gif"));
	fieldSimulationWarningIcon = new ImageIcon(getClass().getResource("/images/run2Warning_16x16.gif"));
	fieldSimulationContextIcon = new ImageIcon(getClass().getResource("/images/application3_16x16.gif"));
	fieldSimulationContextErrorIcon = new ImageIcon(getClass().getResource("/images/application3Error_16x16.gif"));
	fieldSimulationContextWarningIcon = new ImageIcon(getClass().getResource("/images/application3Warning_16x16.gif"));
	fieldTextIcon = new ImageIcon(getClass().getResource("/images/text_16x16.gif"));
	fieldFolderClosedIcon = new ImageIcon(getClass().getResource("/images/closedFolder_16x16.gif"));
	fieldFolderClosedErrorIcon = new ImageIcon(getClass().getResource("/images/closedFolderError_16x16.gif"));
	fieldFolderClosedWarningIcon = new ImageIcon(getClass().getResource("/images/closedFolderWarning_16x16.gif"));
	fieldFolderOpenIcon = new ImageIcon(getClass().getResource("/images/closedFolder_16x16.gif"));
	fieldFolderOpenErrorIcon = new ImageIcon(getClass().getResource("/images/closedFolderError_16x16.gif"));
	fieldFolderOpenWarningIcon = new ImageIcon(getClass().getResource("/images/closedFolderWarning_16x16.gif"));
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
		if (node.getUserObject() instanceof SolverResultSetInfo) {
			setComponentProperties(component, (SolverResultSetInfo)node.getUserObject());
			if (leaf){
				setIcon(fieldFolderClosedIcon);
			}
			int maxErrorLevel = getMaxErrorLevel(node);
			if (maxErrorLevel==BioModelNode.ERROR_POSSIBLE){
				if (!leaf && expanded) {
					setIcon(fieldFolderOpenWarningIcon);
				}else if (leaf || (!leaf && !expanded)) {
					setIcon(fieldFolderClosedWarningIcon);
				}
				component.setToolTipText("Simulation Results may be invalid, re-run");
				component.setText("<<<possibly invalid sim results>>> "+component.getText());
				component.setForeground(java.awt.Color.red);
			}else if (maxErrorLevel==BioModelNode.ERROR_CONFIRMED){
				if (!leaf && expanded) {
					setIcon(fieldFolderOpenErrorIcon);
				}else if (leaf || (!leaf && !expanded)) {
					setIcon(fieldFolderClosedErrorIcon);
				}
				component.setToolTipText("Simulation Results are invalid, re-run");
				component.setText("<<<INVALID SIM RESULTS>>> "+component.getText());
				component.setForeground(java.awt.Color.red);
			}
			
		//}else if (node.getUserObject() instanceof SimulationContextInfo) {
			////
			//// Check if node is a SimulationContextInfo
			////
			//setComponentProperties(component, (SimulationContextInfo)node.getUserObject());
			//int maxErrorLevel = getMaxErrorLevel(node);
			//if (maxErrorLevel==BioModelNode.ERROR_POSSIBLE){
				//setIcon(fieldSimulationContextWarningIcon);
				//component.setToolTipText("Application contains possibly invalid simulation results");
			//}else if (maxErrorLevel==BioModelNode.ERROR_CONFIRMED){
				//setIcon(fieldSimulationContextErrorIcon);
				//component.setToolTipText("Application contains invalid simulation results");
			//}
			
		}else if (node.getUserObject() instanceof SimulationInfo) {
			//
			// Check if node is a SimulationInfo
			//
			setComponentProperties(component, (SimulationInfo)node.getUserObject());
			int maxErrorLevel = getMaxErrorLevel(node);
			if (maxErrorLevel==BioModelNode.ERROR_POSSIBLE){
				setIcon(fieldSimulationWarningIcon);
				component.setToolTipText("Simulation contains possibly invalid results");
			}else if (maxErrorLevel==BioModelNode.ERROR_CONFIRMED){
				setIcon(fieldSimulationErrorIcon);
				component.setToolTipText("Simulation contains invalid results");
			}
			
		}else if (node.getUserObject() instanceof String && node.getChildCount()==0){
			component.setToolTipText(null);
			component.setText((String)node.getUserObject());
			
		}else if (node.getUserObject() instanceof BioModelInfo) {
			//
			// Check if node is a BioModelInfo
			//
			setComponentProperties(component,(BioModelInfo)node.getUserObject());
			bLoaded = isLoaded((BioModelInfo)node.getUserObject());
			int maxErrorLevel = getMaxErrorLevel(node);
			if (maxErrorLevel==BioModelNode.ERROR_POSSIBLE){
				setIcon(fieldBioModelWarningIcon);
				component.setToolTipText("BioModel version: Has possibly invalid simulation results");
			}else if (maxErrorLevel==BioModelNode.ERROR_CONFIRMED){
				setIcon(fieldBioModelErrorIcon);
				component.setToolTipText("BioModel version: Has invalid simulation results");
			}
			
		}else if (node.getUserObject() instanceof String && node.getChildCount()>0 && ((BioModelNode)node.getChildAt(0)).getUserObject() instanceof BioModelInfo){
			//
			// Check if node is a BioModelName (String), with children (at least one version of biomodel), and if the child is a
			// BioModelInfo node
			//
			String label = (String)node.getUserObject();
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
			
		} else if (node.getUserObject() instanceof User && node.getChildCount()>0 && (((BioModelNode)node.getChildAt(0)).getUserObject() instanceof String) && ((BioModelNode)(node.getChildAt(0).getChildAt(0))).getUserObject() instanceof BioModelInfo){
			//
			// Check if node is a User, with at least one child which is a string (BioModel name)
			// and if the child's child is a BioModelInfo node
			//
			String label = ((User)node.getUserObject()).getName();
			component.setToolTipText("User Name");
			component.setText(label);
			
			if (isLoaded((User)node.getUserObject())) {
				bLoaded = true;
			}
			
		} else if (node.getUserObject() instanceof MathModelInfo) {
			//
			// Check if node is a MathModelInfo node
			//
			setComponentProperties(component,(MathModelInfo)node.getUserObject());
			bLoaded = isLoaded((MathModelInfo)node.getUserObject());
			
		}else if (node.getUserObject() instanceof String && node.getChildCount()>0 && ((BioModelNode)node.getChildAt(0)).getUserObject() instanceof MathModelInfo){
			//
			// Check if node is a MathModel name (String), with children (at least one version of mathmodel), and 
			// if the child is a MathModelInfo node
			//
			String label = (String)node.getUserObject();
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
			
		} else if (node.getUserObject() instanceof User && node.getChildCount()>0 && (((BioModelNode)node.getChildAt(0)).getUserObject() instanceof String) && ((BioModelNode)(node.getChildAt(0).getChildAt(0))).getUserObject() instanceof MathModelInfo){
			//
			// Check if node is a User, with at least one child which is a string (Mathmodel name)
			// and if the child's child is a MathModelInfo node
			//
			String label = ((User)node.getUserObject()).getName();
			component.setToolTipText("User Name");
			component.setText(label);
			
			if (isLoaded((User)node.getUserObject())) {
				bLoaded = true;
			}
			
		} else if (node.getUserObject() instanceof cbit.vcell.geometry.GeometryInfo) {
			//
			// Check if node is a GeometryInfo
			//
			setComponentProperties(component,(GeometryInfo)node.getUserObject());
			bLoaded = isLoaded((GeometryInfo)node.getUserObject());
			
		}else if (node.getUserObject() instanceof String && node.getChildCount()>0 && ((BioModelNode)node.getChildAt(0)).getUserObject() instanceof GeometryInfo){
			//
			// Check if node is a Geometry name (String), with children (at least one version of Geometry), and 
			// if the child is a GeometryInfo node
			//
			String label = (String)node.getUserObject();
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
			
		} else if (node.getUserObject() instanceof User && node.getChildCount()>0 && (((BioModelNode)node.getChildAt(0)).getUserObject() instanceof String) && ((BioModelNode)(node.getChildAt(0).getChildAt(0))).getUserObject() instanceof GeometryInfo){
			//
			// Check if node is a User, with at least one child which is a string (Geometry name)
			// and if the child's child is a GeometryInfo node
			//
			String label = ((User)node.getUserObject()).getName();
			component.setToolTipText("User Name");
			component.setText(label);
			
			if (isLoaded((User)node.getUserObject())) {
				bLoaded = true;
			}
			
		} else if (node.getUserObject() instanceof String && node.getChildCount()>0){
			component.setToolTipText(null);
			component.setText((String)node.getUserObject());
			
		//}else if (node.getUserObject() instanceof MathInfo) {
			//setComponentProperties(component,(MathInfo)node.getUserObject());
			
		}else if (node.getUserObject() instanceof VCImageInfo) {
			setComponentProperties(component,(VCImageInfo)node.getUserObject());
			
		}else if (node.getUserObject() instanceof Extent) {
			setComponentProperties(component,(Extent)node.getUserObject());
						
		}else if (node.getUserObject() instanceof Annotation) {
			setComponentProperties(component,(Annotation)node.getUserObject());
						
		}else if (node.getUserObject() instanceof MathModel) {
			setComponentProperties(component,(MathModel)node.getUserObject());
			
		}else if (node.getUserObject() instanceof BioModel) {
			setComponentProperties(component,(BioModel)node.getUserObject());
			
		} else if (node.getUserObject() instanceof SimulationContext) {
			setComponentProperties(component, (SimulationContext)node.getUserObject());
			bLoaded = isLoaded((SimulationContext)node.getUserObject());
			
		} else if (node.getUserObject() instanceof Simulation) {
			setComponentProperties(component, (Simulation)node.getUserObject());
			
		} else if (node.getUserObject() instanceof MathDescription) {
			setComponentProperties(component, (MathDescription)node.getUserObject());
			
		} else if (node.getUserObject() instanceof Geometry) {
			setComponentProperties(component, (Geometry)node.getUserObject());
			
		}else if (node.getUserObject() instanceof User) {
			setComponentProperties(component,(User)node.getUserObject());

		} else{
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
	component.setToolTipText("BioModel version");
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
	component.setText(cbit.util.TokenMangler.replaceSubString(annot.toString(),"\n"," "));
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 8:35:45 AM)
 * @return javax.swing.Icon
 * @param nodeUserObject java.lang.Object
 */
protected void setComponentProperties(JLabel component, cbit.vcell.export.ExportLogEntry exportLogEntry) {

	//component.setIcon(fieldResultsIcon);
	
	component.setToolTipText("Exported Data from Simulation Results");
	component.setText("exported as \""+exportLogEntry.getFormat()+"\" to \""+exportLogEntry.getLocation()+"\"");
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
		component.setText("Compartmental");
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

	//component.setIcon(fieldMathModelIcon);
	
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
	component.setToolTipText("Mathematical Model");
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
protected void setComponentProperties(JLabel component, SolverResultSetInfo rsInfo) {

	//component.setIcon(fieldResultsIcon);
	
	component.setToolTipText("Simulation Results");
	component.setText("Results in \""+rsInfo.getDataFilePath()+"\"");
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