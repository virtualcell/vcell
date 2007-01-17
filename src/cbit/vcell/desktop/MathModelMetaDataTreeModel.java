package cbit.vcell.desktop;

import cbit.vcell.mathmodel.*;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.server.User;
import java.util.Vector;
import cbit.vcell.solver.SolverResultSetInfo;
import cbit.vcell.solver.SimulationInfo;
import java.util.Enumeration;
import cbit.vcell.server.DataAccessException;
import cbit.vcell.biomodel.BioModelInfo;
import cbit.vcell.clientdb.DatabaseListener;
import javax.swing.tree.DefaultTreeModel;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.biomodel.BioModelMetaData;
/**
 * Insert the type's description here.
 * Creation date: (2/14/01 3:33:23 PM)
 * @author: Jim Schaff
 */
public class MathModelMetaDataTreeModel extends javax.swing.tree.DefaultTreeModel {
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private cbit.vcell.mathmodel.MathModelInfo fieldMathModelInfo = null;
/**
 * BioModelDbTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public MathModelMetaDataTreeModel() {
	super(new BioModelNode("empty",false),true);
}
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(propertyName, listener);
}
/**
 * Insert the method's description here.
 * Creation date: (11/28/00 2:41:43 PM)
 * @param mathModelNode cbit.vcell.desktop.BioModelNode
 * @param mathModelInfo cbit.vcell.mathmodel.MathModelInfo
 */
private BioModelNode createVersionSubTree(MathModelInfo mathModelInfo) throws DataAccessException {
	BioModelNode versionNode = new BioModelNode(mathModelInfo,true);
	//
	// add children of the MathModel to the node passed in
	//
	if (mathModelInfo.getVersion().getAnnot()!=null && mathModelInfo.getVersion().getAnnot().trim().length()>0){
		versionNode.add(new BioModelNode(new Annotation(mathModelInfo.getVersion().getAnnot()),false));
	}
	
	MathModelChildSummary mathModelChildSummary = mathModelInfo.getMathModelChildSummary();

	if (mathModelChildSummary==null){
		versionNode.add(new BioModelNode("SUMMARY INFORMATION NOT AVAILABLE",false));
	}else{
		int geomDim = mathModelChildSummary.getGeometryDimension();
		String geomName = mathModelChildSummary.getGeometryName();
		BioModelNode geometryNode = null;
		if (geomDim>0){
			geometryNode = new BioModelNode(geomName + " ("+geomDim+"D)",false);
		}else{
			geometryNode = new BioModelNode("Compartmental",false);
		}
		geometryNode.setRenderHint("type","Geometry");
		geometryNode.setRenderHint("dimension",new Integer(geomDim));
		versionNode.add(geometryNode);
		//
		// add simulations to simulationContext
		//
		String simNames[] = mathModelChildSummary.getSimulationNames();
		String simAnnot[] = mathModelChildSummary.getSimulationAnnotations();
		for (int j = 0; j < simNames.length; j++){
			BioModelNode simNode = new BioModelNode(simNames[j],true);
			simNode.setRenderHint("type","Simulation");
			versionNode.add(simNode);
			if (simAnnot[j]!=null && simAnnot[j].trim().length()>0){
				simNode.add(new BioModelNode(new Annotation(simAnnot[j]),false));
			}
		}
	}
	return versionNode;
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
	getPropertyChange().firePropertyChange(evt);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, int oldValue, int newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, boolean oldValue, boolean newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * Gets the mathModelInfo property (cbit.vcell.mathmodel.MathModelInfo) value.
 * @return The mathModelInfo property value.
 * @see #setMathModelInfo
 */
public cbit.vcell.mathmodel.MathModelInfo getMathModelInfo() {
	return fieldMathModelInfo;
}
/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}
/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}
/**
 * Insert the method's description here.
 * Creation date: (2/14/01 3:50:24 PM)
 */
private void refreshTree() {
	if (getMathModelInfo()!=null){
		try {
			setRoot(createVersionSubTree(getMathModelInfo()));
		}catch (DataAccessException e){
			e.printStackTrace(System.out);
		}
	}else{
		setRoot(new BioModelNode("empty"));
	}
}
/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}
/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(propertyName, listener);
}
/**
 * Sets the mathModelInfo property (cbit.vcell.mathmodel.MathModelInfo) value.
 * @param mathModelInfo The new value for the property.
 * @see #getMathModelInfo
 */
public void setMathModelInfo(cbit.vcell.mathmodel.MathModelInfo mathModelInfo) {
	MathModelInfo oldValue = fieldMathModelInfo;
	fieldMathModelInfo = mathModelInfo;
	firePropertyChange("mathModelInfo", oldValue, mathModelInfo);
	refreshTree();
}
}
