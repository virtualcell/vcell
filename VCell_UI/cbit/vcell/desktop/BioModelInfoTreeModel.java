package cbit.vcell.desktop;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.util.BioModelChildSummary;
import cbit.util.BioModelInfo;
import cbit.util.DataAccessException;
/**
 * Insert the type's description here.
 * Creation date: (2/14/01 3:33:23 PM)
 * @author: Jim Schaff
 */
public class BioModelInfoTreeModel extends javax.swing.tree.DefaultTreeModel {
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private cbit.util.BioModelInfo fieldBioModelInfo = null;
/**
 * BioModelDbTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public BioModelInfoTreeModel() {
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
 * @param bioModelNode cbit.vcell.desktop.BioModelNode
 * @param bioModelInfo cbit.vcell.biomodel.BioModelInfo
 */
private BioModelNode createVersionSubTree(BioModelInfo bioModelInfo) throws DataAccessException {
	BioModelNode versionNode = new BioModelNode(bioModelInfo,true);
	//
	// add children of the BioModel to the node passed in
	//
	if (bioModelInfo.getVersion().getAnnot()!=null && bioModelInfo.getVersion().getAnnot().trim().length()>0){
		versionNode.add(new BioModelNode(new Annotation(bioModelInfo.getVersion().getAnnot()),false));
	}

	BioModelChildSummary bioModelChildSummary = bioModelInfo.getBioModelChildSummary();
	if (bioModelChildSummary==null){
		versionNode.add(new BioModelNode("SUMMARY INFORMATION NOT AVAILABLE",false));
	}else{
		String scNames[] = bioModelChildSummary.getSimulationContextNames();
		String scAnnot[] = bioModelChildSummary.getSimulationContextAnnotations();
		int geomDims[] = bioModelChildSummary.getGeometryDimensions();
		String geomNames[] = bioModelChildSummary.getGeometryNames();
		for (int i = 0; i < scNames.length; i++){
			BioModelNode scNode = new BioModelNode(scNames[i],true);
			scNode.setRenderHint("type","SimulationContext");
			versionNode.add(scNode);
			if (scAnnot[i]!=null && scAnnot[i].trim().length()>0){
				scNode.add(new BioModelNode(new Annotation(scAnnot[i]),false));
			}
			BioModelNode geometryNode = null;
			if (geomDims[i]>0){
				geometryNode = new BioModelNode(geomNames[i],false);
			}else{
				geometryNode = new BioModelNode("Compartmental",false);
			}
			geometryNode.setRenderHint("type","Geometry");
			geometryNode.setRenderHint("dimension",new Integer(geomDims[i]));
			scNode.add(geometryNode);
			//
			// add simulations to simulationContext
			//
			String simNames[] = bioModelChildSummary.getSimulationNames(scNames[i]);
			String simAnnot[] = bioModelChildSummary.getSimulationAnnotations(scNames[i]);
			for (int j = 0; j < simNames.length; j++){
				BioModelNode simNode = new BioModelNode(simNames[j],true);
				simNode.setRenderHint("type","Simulation");
				scNode.add(simNode);
				if (simAnnot[j]!=null && simAnnot[j].trim().length()>0){
					simNode.add(new BioModelNode(new Annotation(simAnnot[j]),false));
				}
				////
				//// add resultSet (optional) to simulation
				////
				//Enumeration rsEnum = bioModelMetaData.getResultSetInfos();
				//while (rsEnum.hasMoreElements()){
					//SolverResultSetInfo rsInfo = (SolverResultSetInfo)rsEnum.nextElement();
					//if (rsInfo.getSimulationInfo().getVersion().getVersionKey().equals(simInfo.getVersion().getVersionKey()) ||
						//rsInfo.getSimulationInfo().getVersion().getVersionKey().equals(simInfo.getParentSimulationReference())){
							
						//BioModelNode rsNode = new BioModelNode(rsInfo,true);
						//simNode.add(rsNode);
						////
						//// add Export (optional) to simulation
						////
						//Enumeration elEnum = bioModelMetaData.getExportLogs();
						//while (elEnum.hasMoreElements()){
							//cbit.vcell.export.server.ExportLog exportLog = (cbit.vcell.export.server.ExportLog)elEnum.nextElement();
							//if (exportLog.getSimulationIdentifier().equals(rsInfo.getSimulationInfo().getSimulationIdentifier())){
								//cbit.vcell.export.server.ExportLogEntry exportLogEntries[] = exportLog.getExportLogEntries();
								//for (int i = 0; i < exportLogEntries.length; i++){
									//BioModelNode eleNode = new BioModelNode(exportLogEntries[i],false);
									//rsNode.add(eleNode);
								//}
							//}
						//}
					//}
				//}
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
 * Gets the bioModelInfo property (cbit.vcell.biomodel.BioModelInfo) value.
 * @return The bioModelInfo property value.
 * @see #setBioModelInfo
 */
public cbit.util.BioModelInfo getBioModelInfo() {
	return fieldBioModelInfo;
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
	if (getBioModelInfo()!=null){
		try {
			setRoot(createVersionSubTree(getBioModelInfo()));
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
 * Sets the bioModelInfo property (cbit.vcell.biomodel.BioModelInfo) value.
 * @param bioModelInfo The new value for the property.
 * @see #getBioModelInfo
 */
public void setBioModelInfo(cbit.util.BioModelInfo bioModelInfo) {
	cbit.util.BioModelInfo oldValue = fieldBioModelInfo;
	fieldBioModelInfo = bioModelInfo;
	firePropertyChange("bioModelInfo", oldValue, bioModelInfo);
	refreshTree();
}
}
