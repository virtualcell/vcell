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

import java.awt.Window;
import java.util.ArrayList;

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
		species_definitions,
		observables,
		reaction_diagram,
		structure_diagram,
		
		pathway_diagram,
		pathway_objects,
		biopax_summary,
		biopax_tree,
		
		geometry_definition,
		structure_mapping,
		spatial_objects,
		
		species_settings,
		reaction_setting,
		network_setting,
		network_free_setting,
		membrane_setting,
		
		events,
		electrical,
		microscope_measuremments,
		rateRules,
		
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
		private final SimulationContext simulationContext;
		private final DocumentEditorTreeFolderClass documentEditorTreeFolderClass;
		private final ActiveViewID activeViewID;
		/**
		 * activated panel; provided for attaching Dialogs
		 */
		private Window activated;
		
		public ActiveView(SimulationContext simulationContext, DocumentEditorTreeFolderClass documentEditorTreeFolderClass, ActiveViewID activeViewID) {
			super();
			this.simulationContext = simulationContext;
			this.documentEditorTreeFolderClass = documentEditorTreeFolderClass;
			this.activeViewID = activeViewID;
			activated = null;
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
		/**
		 * set Window that's been activated
		 * @param activated
		 */
		public void setActivated(Window activated) {
			this.activated = activated;
		}	
		
		public Window getActivated() {
			return activated;
		}
		
//		public String toString(){
//			String simContextText = "no simContext";
//			if (simulationContext!=null){
//				simContextText = simulationContext.getName();
//			}
//			String docEditorTreeClass = "no TreeClass";
//			if (documentEditorTreeFolderClass!=null){
//				docEditorTreeClass = documentEditorTreeFolderClass.toString();
//			}
//			return "id="+getActiveViewID()+":tab="+docEditorTreeClass+":sc=\""+simContextText+"\"";
//		}
	}
	
	public static final String PROPERTY_NAME_ACTIVE_VIEW = "activeView";
	public static final String PROPERTY_NAME_SELECTED_OBJECTS = "selectedObjects";
	private Object[] selectedObjects = null;
	private ActiveView activeView = null;
	private transient java.beans.PropertyChangeSupport propertyChange;
	private boolean bBusy = false;
	
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
//		System.out.println("----- setSelectedObjects (enter) thread="+Thread.currentThread().getName());
//		showStack();
		followHyperlink(getActiveView(), newValue);
//		System.out.println("----- setSelectedObjects (leave) thread="+Thread.currentThread().getName());
//		show();
	}
	
	public final void setActiveView(ActiveView newValue) {
//		System.out.println("===== setActiveView (enter) thread="+Thread.currentThread().getName());
//		showStack();
		followHyperlink(newValue, new Object[0]);
//		System.out.println("===== setActiveView (leave) thread="+Thread.currentThread().getName());
//		show();
	}
	
	public final void followHyperlink(ActiveView newActiveView, Object ... newSelection) { 
		if (bBusy){
//			System.out.println("     busy...");
			return;
		}
		try {
			bBusy = true;
			Object[] oldSelection = this.selectedObjects;
			this.selectedObjects = newSelection;
			ActiveView oldActiveView = this.activeView;
			this.activeView = newActiveView;
			
			if (newActiveView != oldActiveView){
				firePropertyChange(PROPERTY_NAME_ACTIVE_VIEW, oldActiveView, newActiveView);
			}
			//if (newSelection != oldSelection){
				firePropertyChange(PROPERTY_NAME_SELECTED_OBJECTS, oldSelection, newSelection);
			//}
		}finally{
			bBusy = false;
		}
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
	public final Object[] getSelectedObjects() {
		return selectedObjects;
	}
	public final ActiveView getActiveView() {
		return activeView;
	}
	public boolean isBusy() {
		return bBusy;
	}

//	private void show() {
//		showStack();
//		System.out.println("  active view:     " + activeView);
//		for(Object so : this.selectedObjects) {
//			if(so instanceof SpeciesContextSpec) {
//				SpeciesContextSpec scs = (SpeciesContextSpec)so;
//				System.out.println(" selected object:  " + scs.getSpeciesContext().toString());
//			} else {
//				System.out.println(" selected object:  " + so);
//			}
//		}
//		if(this.selectedObjects==null) {
//			System.out.println("   selectedObjects is a null array");
//		}else if (this.selectedObjects.length == 0) {
//			System.out.println("   no object selected");
//		}
//		System.out.println("---------------------------------------------------------------------------");
//	}
//	public String getStatusText() {
//		StringBuffer buffer = new StringBuffer();
//		if (activeView!=null){
//			buffer.append("view: ("+activeView.toString()+"), ");
//		}else{
//			buffer.append("view: (none), ");
//		}
//		if (selectedObjects==null || selectedObjects.length==0){
//			buffer.append("selected(0): ");
//		}else{
//			buffer.append("selected("+selectedObjects.length+"): s[0] = ("+selectedObjects[0]+")");
//		}
//		return buffer.toString();
//	}
//	private void showStack() {
//	StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
//	System.out.println("..... from .... "+stackTraceElements[1].toString());
//	System.out.println("..... from .... "+stackTraceElements[2].toString());
//	System.out.println("..... from .... "+stackTraceElements[3].toString());
//	System.out.println("..... from .... "+stackTraceElements[4].toString());
//}

}
