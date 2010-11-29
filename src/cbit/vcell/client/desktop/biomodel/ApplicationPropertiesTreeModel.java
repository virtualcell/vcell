package cbit.vcell.client.desktop.biomodel;

import javax.swing.JTree;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.desktop.Annotation;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.solver.Simulation;
/**
 * Insert the type's description here.
 * Creation date: (2/14/01 3:33:23 PM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class ApplicationPropertiesTreeModel extends javax.swing.tree.DefaultTreeModel implements java.beans.PropertyChangeListener {
	private static final String PROPERTY_NAME_SIMULATION_CONTEXT = "simulationContext";
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private SimulationContext simulationContext = null;
	private BioModelNode rootNode = null;
	private JTree ownerTree = null;
/**
 * BioModelDbTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public ApplicationPropertiesTreeModel(JTree tree) {
	super(new BioModelNode("Select an application to show properties", true), true);
	rootNode = (BioModelNode)root;
	ownerTree = tree;
	addPropertyChangeListener(this);
}
/**
 * Insert the method's description here.
 * Creation date: (11/28/00 2:41:43 PM)
 * @param bioModelNode cbit.vcell.desktop.BioModelNode
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
private void populateRootNode() {
	rootNode.removeAllChildren();
	if (simulationContext == null){
		rootNode.setUserObject("Select an application to show properties");
	} else {	
		rootNode.setUserObject(simulationContext);
			
		//add application type node
		String typeInfo = simulationContext.getMathType();
		
		BioModelNode appTypeNode = new BioModelNode(typeInfo,false);
		appTypeNode.setRenderHint("type","AppType");
		rootNode.add(appTypeNode);
		//
		// Display Annotation on tree
		//
		rootNode.add(new BioModelNode(new Annotation(simulationContext.getDescription()),false));
			
		rootNode.add(new BioModelNode(simulationContext.getGeometry(),false));
		if (simulationContext.getMathDescription()!=null){
			rootNode.add(new BioModelNode(simulationContext.getMathDescription(),false));
		}else{
			rootNode.add(new BioModelNode("math not generated",false));
		}
	
		Simulation simArray[] = simulationContext.getSimulations();
		if (simArray!=null){
			for (int j=0;j<simArray.length;j++){
				BioModelNode simNode = new BioModelNode(simArray[j],true);
				rootNode.add(simNode);
				simNode.add(new BioModelNode(new Annotation(simArray[j].getDescription()),false));
			}
		}
	}
	nodeStructureChanged(rootNode);	
//	expandAll(rootNode);
}

//private void expandAll(BioModelNode treeNode) {
//	int childCount = treeNode.getChildCount();
//	if (childCount > 0) {
//		for (int i = 0; i < childCount; i++) {
//			TreeNode n = treeNode.getChildAt(i);
//			if (n instanceof BioModelNode) {
//				expandAll((BioModelNode)n);
//			}
//		}
//	} else {
//		TreePath path = new TreePath(treeNode.getPath());
//		if (!ownerTree.isExpanded(path)) {
//			ownerTree.expandPath(path.getParentPath());
//		}
//	}
//}
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}

/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}

private java.beans.PropertyChangeSupport getPropertyChange() {
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
 * Creation date: (5/9/01 8:28:22 AM)
 * @param evt java.beans.PropertyChangeEvent
 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	try {
		if (evt.getSource() == ApplicationPropertiesTreeModel.this && evt.getPropertyName().equals(PROPERTY_NAME_SIMULATION_CONTEXT)) {
			populateRootNode();
		}
	} catch (Exception e){
		e.printStackTrace(System.out);
	}
}

/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}
/**
 * Sets the bioModel property (cbit.vcell.biomodel.BioModel) value.
 * @param bioModel The new value for the property.
 * @see #getBioModel
 */
public void setSimulationContext(SimulationContext newValue) {
//	SimulationContext oldValue = simulationContext;
	simulationContext = newValue;
//	firePropertyChange(PROPERTY_NAME_SIMULATION_CONTEXT, oldValue, newValue);
	populateRootNode();
}
}
