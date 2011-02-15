package cbit.vcell.client.desktop.biomodel;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.mapping.SimulationContext;

public class SelectionManager {
	public static enum ActiveViewID {
		pathway,
		model,	
		biomodel_parameters,	
		data,	
		applications,	
		scripting,

		reactions,
		structures,
		species,
		reaction_diagram,
		structure_diagram,
		
		geometry_settings,
		structure_mapping,
		
		species_settings,
		reaction_setting,
		
		events,
		electrical,
		microscope_measuremments,
		
		simulations,
		output_functions,
		generated_math,
		fitting,
		parameter_estimation,
		
		math_annotation,
		math_vcml,
		math_geometry,
		math_simulations,
		math_output_functions;
	}
	
	public static class ActiveView implements Matchable {
		private SimulationContext simulationContext;
		private DocumentEditorTreeFolderClass documentEditorTreeFolderClass;
		private ActiveViewID activeViewID;
		
		public ActiveView(SimulationContext simulationContext, DocumentEditorTreeFolderClass documentEditorTreeFolderClass, ActiveViewID activeViewID) {
			super();
			this.simulationContext = simulationContext;
			this.documentEditorTreeFolderClass = documentEditorTreeFolderClass;
			this.activeViewID = activeViewID;
		}
		public final SimulationContext getSimulationContext() {
			return simulationContext;
		}
		public final DocumentEditorTreeFolderClass getDocumentEditorTreeFolderClass() {
			return documentEditorTreeFolderClass;
		}
		public boolean compareEqual(Matchable obj) {
			if (obj instanceof ActiveView) {
				ActiveView activeView = (ActiveView) obj;
				if (simulationContext != activeView.simulationContext) {
					return false; 
				}
				if (documentEditorTreeFolderClass != null && !documentEditorTreeFolderClass.equals(activeView.documentEditorTreeFolderClass)
					|| activeView.documentEditorTreeFolderClass != null && !activeView.documentEditorTreeFolderClass.equals(documentEditorTreeFolderClass)) {
					return false;
				}
					
				if (activeViewID != null && activeViewID.equals(activeView.activeViewID)
					|| activeView.activeViewID != null && activeView.activeViewID.equals(activeViewID)) {
					return false;
				}
			}
			return true;
		}
		public final ActiveViewID getActiveViewID() {
			return activeViewID;
		}		
	}
	
	public static final String PROPERTY_NAME_ACTIVE_VIEW = "activeView";
	public static final String PROPERTY_NAME_SELECTED_OBJECTS = "selectedObjects";
	private Object[] selectedObjects = null;
	private ActiveView activeView = null;
	private transient java.beans.PropertyChangeSupport propertyChange;
	private boolean bBusy = false;
	private boolean bActiveViewBusy = false;
	
	public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(listener);
	}

	public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}

	private java.beans.PropertyChangeSupport getPropertyChange() {
		if (propertyChange == null) {
			propertyChange = new java.beans.PropertyChangeSupport(this);
		};
		return propertyChange;
	}
	
	public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(listener);
	}
	
	public final void setSelectedObjects(Object[] newValue) {
		if (bBusy) {
			return;
		}
		bBusy = true;
		try {
			Object[] oldValue = this.selectedObjects;
			if (oldValue == newValue) {
				return;
			}
			if (oldValue != null && newValue != null && oldValue.length == 0 && newValue.length == 0) {
				return;
			}
			this.selectedObjects = newValue;
			firePropertyChange(PROPERTY_NAME_SELECTED_OBJECTS, oldValue, newValue);
		} finally {
			bBusy = false;
		}
	}
	
	public final void setActiveView(ActiveView newValue) {
		if (bActiveViewBusy) {
			return;
		}
		bActiveViewBusy = true;
		try {
			ActiveView oldValue = this.activeView;
			if (Compare.isEqualOrNull(oldValue, newValue)) {
				return;
			}
			this.activeView = newValue;
			firePropertyChange(PROPERTY_NAME_ACTIVE_VIEW, oldValue, newValue);
		} finally {
			bActiveViewBusy = false;
		}
	}
 
	public final Object[] getSelectedObjects() {
		return selectedObjects;
	}

	public final ActiveView getActiveView() {
		return activeView;
	}
}
