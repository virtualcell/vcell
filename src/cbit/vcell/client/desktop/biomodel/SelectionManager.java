/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel;

import java.util.ArrayList;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.client.desktop.biomodel.DocumentEditorTreeModel.DocumentEditorTreeFolderClass;
import cbit.vcell.mapping.SimulationContext;

public class SelectionManager {
	public static enum ActiveViewID {
		pathway,
		model,	
		
		parameters_functions,
		predefined_symbols,
		
		data,	
		applications,	
		scripting,

		reactions,
		structures,
		species,
		reaction_diagram,
		structure_diagram,
		
		pathway_diagram,
		pathway_objects,
		biopax_summary,
		biopax_tree,
		
		geometry_definition,
		structure_mapping,
		
		species_settings,
		reaction_setting,
		
		events,
		electrical,
		microscope_measuremments,
		
		simulations,
		output_functions,
		generated_math,
		parameter_estimation,
		parameter_estimation_parameters,
		parameter_estimation_experimental_data_import,
		parameter_estimation_experimental_data_mapping,
		parameter_estimation_run_task,
		
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
				if (documentEditorTreeFolderClass != activeView.documentEditorTreeFolderClass) {
					return false;
				}
					
				if (activeViewID != activeView.activeViewID) {
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
	private boolean bSelectionBusy = false;
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
	
	public final void followHyperlink(ActiveView newActiveView, Object[] newSelection){
		if (bSelectionBusy || bActiveViewBusy){
			return;
		}
		try {
			bSelectionBusy = true;
			bActiveViewBusy = true;
			Object[] oldSelection = this.selectedObjects;
			this.selectedObjects = newSelection;
			ActiveView oldActiveView = this.activeView;
			this.activeView = newActiveView;
			
			firePropertyChange(PROPERTY_NAME_ACTIVE_VIEW, oldSelection, this.selectedObjects);
			firePropertyChange(PROPERTY_NAME_SELECTED_OBJECTS, oldActiveView, newActiveView);
		}finally{
			bSelectionBusy = false;
			bActiveViewBusy = false;
		}
	}
	
	public final void setSelectedObjects(Object[] newValue) {
		if (bSelectionBusy) {
			return;
		}
		bSelectionBusy = true;
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
			bSelectionBusy = false;
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

	public final ArrayList<Object> getSelectedObjects(Class<?> cls) {
		ArrayList<Object> objectList = new ArrayList<Object>();
		if (selectedObjects != null) {
			for (Object object : selectedObjects) {
				if (object == null) {
					continue;
				}
				if (cls.isAssignableFrom(object.getClass())) {
					objectList.add(object);
				}
			}
		}
		return objectList;
	}
	
	public final ActiveView getActiveView() {
		return activeView;
	}
}
