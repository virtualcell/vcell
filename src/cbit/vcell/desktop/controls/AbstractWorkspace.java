package cbit.vcell.desktop.controls;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.beans.*;
import javax.swing.JOptionPane;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.desktop.controls.*;
import cbit.vcell.model.gui.VCellNames;
/**
 * Insert the type's description here.
 * Creation date: (5/17/2001 5:31:08 PM)
 * @author: Ion Moraru
 */
public abstract class AbstractWorkspace implements PropertyChangeListener, VetoableChangeListener, cbit.vcell.clientdb.DatabaseListener {
	public static final String SIMULATION_BASENAME = "Simulation ";
	private transient PropertyChangeSupport propertyChange;
	private transient VetoableChangeSupport vetoPropertyChange;
	private transient WorkspaceListener aWorkspaceListener = null;
//	private cbit.vcell.clientdb.DocumentManager fieldDocumentManager = null;
	protected transient java.util.Vector aDatabaseListener = null;	

/**
 * Insert the method's description here.
 * Creation date: (5/17/2001 5:53:10 PM)
 */
public AbstractWorkspace() {
	super();
	addPropertyChangeListener(this);
	addVetoableChangeListener(this);
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
 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void addVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(listener);
}


/**
 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void addVetoableChangeListener(java.lang.String propertyName, java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(propertyName, listener);
}


/**
 * 
 * @param newListener cbit.vcell.desktop.controls.WorkspaceListener
 */
public synchronized void addWorkspaceListener(cbit.vcell.desktop.controls.WorkspaceListener newListener) {
	aWorkspaceListener = cbit.vcell.desktop.controls.WorkspaceEventMulticaster.add(aWorkspaceListener, newListener);
	return;
}


/**
 * 
 * @param event cbit.vcell.clientdb.DatabaseEvent
 */
public void databaseDelete(cbit.vcell.clientdb.DatabaseEvent event) {
}

/**
 * 
 * @param event cbit.vcell.clientdb.DatabaseEvent
 */
public void databaseInsert(cbit.vcell.clientdb.DatabaseEvent event) {}


/**
 * 
 * @param event cbit.vcell.clientdb.DatabaseEvent
 */
public void databaseRefresh(cbit.vcell.clientdb.DatabaseEvent event) {
	
}


/**
 * 
 * @param event cbit.vcell.clientdb.DatabaseEvent
 */
public void databaseUpdate(cbit.vcell.clientdb.DatabaseEvent event) {
	
}


/**
 * Method to support listener events.
 * @param event cbit.vcell.desktop.controls.WorkspaceEvent
 */
protected void fireBioModelSaved(final cbit.vcell.desktop.controls.WorkspaceEvent event) {
	if (aWorkspaceListener == null) {
		return;
	};
	if (javax.swing.SwingUtilities.isEventDispatchThread()){
		aWorkspaceListener.bioModelSaved(event);
	}else{
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				aWorkspaceListener.bioModelSaved(event);
			}
		});
	}
}


/**
 * Method to support listener events.
 * @param event cbit.vcell.desktop.controls.GeometryWorkspaceEvent
 */
protected void fireGeometrySaved(final cbit.vcell.desktop.controls.WorkspaceEvent event) {
	if (aWorkspaceListener == null) {
		return;
	};
	if (javax.swing.SwingUtilities.isEventDispatchThread()){
		aWorkspaceListener.geometrySaved(event);
	}else{
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				aWorkspaceListener.geometrySaved(event);
			}
		});
	}
}


/**
 * Method to support listener events.
 * @param event cbit.vcell.desktop.controls.GeometryWorkspaceEvent
 */
protected void fireImageSaved(final cbit.vcell.desktop.controls.WorkspaceEvent event) {
	if (aWorkspaceListener == null) {
		return;
	};
	if (javax.swing.SwingUtilities.isEventDispatchThread()){
		aWorkspaceListener.imageSaved(event);
	}else{
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				aWorkspaceListener.imageSaved(event);
			}
		});
	}
}


/**
 * Method to support listener events.
 * @param event cbit.vcell.desktop.controls.WorkspaceEvent
 */
protected void fireMathModelSaved(final cbit.vcell.desktop.controls.WorkspaceEvent event) {
	if (aWorkspaceListener == null) {
		return;
	};
	if (javax.swing.SwingUtilities.isEventDispatchThread()){
		aWorkspaceListener.mathModelSaved(event);
	}else{
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				aWorkspaceListener.mathModelSaved(event);
			}
		});
	}
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(final java.lang.String propertyName, final java.lang.Object oldValue, final java.lang.Object newValue) {
	if (javax.swing.SwingUtilities.isEventDispatchThread()){
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}else{
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
			}
		});
	}
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(final String propertyName, final Object oldValue, final Object newValue) throws java.beans.PropertyVetoException {
	
	if (javax.swing.SwingUtilities.isEventDispatchThread()){
		getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
	}else{
		class WrapperException extends RuntimeException {
			private Exception exc = null;
			WrapperException(Exception argExc){	exc = argExc; }
			Exception getWrappedException() { return exc; }
		};
		try {
			javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					try {
						getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
					}catch (PropertyVetoException e){
						throw new WrapperException(e);
					}
				}
			});
		}catch (WrapperException e){
			if (e.getWrappedException() instanceof PropertyVetoException){
				throw (PropertyVetoException)e.getWrappedException();
			}
		}catch (java.lang.reflect.InvocationTargetException e){
			e.printStackTrace(System.out);
		}catch (InterruptedException e){
			e.printStackTrace(System.out);
		}
	}
}


/**
 * Accessor for the propertyChange field.
 */
private java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}


/**
 * Accessor for the vetoPropertyChange field.
 */
private java.beans.VetoableChangeSupport getVetoPropertyChange() {
	if (vetoPropertyChange == null) {
		vetoPropertyChange = new java.beans.VetoableChangeSupport(this);
	};
	return vetoPropertyChange;
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
 * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void removeVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(listener);
}


/**
 * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void removeVetoableChangeListener(java.lang.String propertyName, java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(propertyName, listener);
}


/**
 * 
 * @param newListener cbit.vcell.desktop.controls.WorkspaceListener
 */
public synchronized void removeWorkspaceListener(cbit.vcell.desktop.controls.WorkspaceListener newListener) {
	aWorkspaceListener = cbit.vcell.desktop.controls.WorkspaceEventMulticaster.remove(aWorkspaceListener, newListener);
	return;
}


	/**
	 * This method gets called when a constrained property is changed.
	 *
	 * @param     evt a <code>PropertyChangeEvent</code> object describing the
	 *   	      event source and the property that has changed.
	 * @exception PropertyVetoException if the recipient wishes the property
	 *              change to be rolled back.
	 */
public abstract void vetoableChange(java.beans.PropertyChangeEvent evt) throws PropertyVetoException;
}