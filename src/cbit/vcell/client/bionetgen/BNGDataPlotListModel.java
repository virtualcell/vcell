package cbit.vcell.client.bionetgen;
import cbit.vcell.modelopt.gui.DataReference;
/**
 * Insert the type's description here.
 * Creation date: (8/31/2005 4:07:05 PM)
 * @author: Jim Schaff
 */
public class BNGDataPlotListModel extends org.vcell.util.gui.DefaultListModelCivilized implements java.beans.PropertyChangeListener {
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private cbit.vcell.modelopt.gui.DataSource[] fieldDataSources = null;
	private cbit.vcell.modelopt.gui.DataSource fieldDataSource = null;

/**
 * MultisourcePlotListModel constructor comment.
 */
public BNGDataPlotListModel() {
	super();
	addPropertyChangeListener(this);
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
 * Gets the dataSource property (cbit.vcell.modelopt.gui.DataSource) value.
 * @return The dataSource property value.
 * @see #setDataSource
 */
public cbit.vcell.modelopt.gui.DataSource getDataSource() {
	return fieldDataSource;
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
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source 
	 *   	and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getSource() == this && evt.getPropertyName().equals("dataSource")){
		refreshAll();
		fireIntervalAdded(this,0,getSize()-1);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/31/2005 4:18:09 PM)
 */
private void refreshAll() {
	//
	// get list of objects (data names)
	//
	if (getDataSource() == null) {
		return;
	}
	if (getDataSource().getSource() instanceof cbit.vcell.solver.ode.ODESolverResultSet){
		cbit.vcell.solver.ode.ODESolverResultSet odeSolverResultSet = (cbit.vcell.solver.ode.ODESolverResultSet)getDataSource().getSource();
		int numColumns = odeSolverResultSet.getColumnDescriptionsCount();
		cbit.vcell.modelopt.gui.DataReference[] dataReference = new cbit.vcell.modelopt.gui.DataReference[numColumns-1];
		int i = 0;
		for (int j = 0; j < numColumns; j++){
			if (odeSolverResultSet.getColumnDescriptions(j).getName().equals("t")){
				continue;
			}
			dataReference[i++] = new DataReference(getDataSource(),odeSolverResultSet.getColumnDescriptions()[j].getName());
		}
		setContents(dataReference);
	} else {
		throw new RuntimeException("Data source not of ODESolverResultSet type.");
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
 * Sets the dataSource property (cbit.vcell.modelopt.gui.DataSource) value.
 * @param dataSource The new value for the property.
 * @see #getDataSource
 */
public void setDataSource(cbit.vcell.modelopt.gui.DataSource dataSource) {
	cbit.vcell.modelopt.gui.DataSource oldValue = fieldDataSource;
	fieldDataSource = dataSource;
	firePropertyChange("dataSource", oldValue, dataSource);
}
}