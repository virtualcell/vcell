/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.gui.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cbit.gui.graph.GraphResizeManager.ResizeMode;

public abstract class GraphModel {
	
	@SuppressWarnings("serial")
	public static class NotReadyException extends Exception {
		public NotReadyException(String message) { super(message); }
		public NotReadyException(String message, Throwable throwable) { super(message, throwable); }
		public NotReadyException(Throwable throwable) { super(throwable); }
	}
	
	public static final String PROPERTY_NAME_SELECTED = "selected";
	
	protected transient GraphListener aGraphListener = null;
	protected transient PropertyChangeSupport propertyChange;
	
	protected GraphContainerLayout containerLayout = new GraphContainerLayoutVCellClassical();
	
	protected GraphResizeManager resizeManager = new GraphResizeManager(this);
	
	protected Set<Object> selectedObjects = new HashSet<Object>();
	protected Map<Object, Shape> objectShapeMap = new HashMap<Object, Shape>();

	public GraphResizeManager getResizeManager() { return resizeManager; }
	
	public GraphContainerLayout getContainerLayout() {
		return containerLayout;
	}
	
	public void addGraphListener(GraphListener newListener) {
		aGraphListener = GraphEventMulticaster.add(aGraphListener, newListener);
		return;
	}

	public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(listener);
	}

	public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		getPropertyChange().addPropertyChangeListener(propertyName, listener);
	}

	public final void addShape(Shape shape) {
		objectShapeMap.put(shape.getModelObject(), shape);
	}

	protected final void clearAllShapes() {
		clearSelection();
		objectShapeMap.clear();
	}

	public void clearSelection() {
		if (selectedObjects.size() == 0) {
			return;
		}
		Object[] selectedOld = selectedObjects.toArray();
		selectedObjects.clear();
		for(Object object : selectedOld) {
			Shape shape = objectShapeMap.get(object);
			shape.notifyUnselected();
		}
		fireGraphChanged(new GraphEvent(this));
		Object[] selectedNew = selectedObjects.toArray();
		firePropertyChange(PROPERTY_NAME_SELECTED, selectedOld, selectedNew);
	}

	public void selectShape(Shape shape) {
		if (shape != null) {
			select(shape.getModelObject());
		}
	}
	
	public Object[] getSelectedObjects() {
		return selectedObjects.toArray();
	}
	
	public void setSelectedObjects(Object[] objects) {
		Set<Object> keySet = objectShapeMap.keySet();
		Set<Object> selectedOld = this.selectedObjects;
		Set<Object> selectedNew = new HashSet<Object>(Arrays.asList(objects));
		selectedNew.retainAll(keySet);
		if (selectedOld.containsAll(selectedNew) && selectedNew.containsAll(selectedOld)) {
			return;
		}
		
		selectedObjects = selectedNew;
		for (Object object : selectedOld) {
			Shape shape = objectShapeMap.get(object);
			if (shape != null) {
				shape.notifyUnselected();
			}
		}
		for (Object object : selectedNew) {
			objectShapeMap.get(object).notifySelected();
		}
		fireGraphChanged(new GraphEvent(this));
		firePropertyChange(PROPERTY_NAME_SELECTED, selectedOld.toArray(), selectedObjects.toArray());
	}
	
	public void select(Object object) {
		if(!selectedObjects.contains(object)) {
			Object[] selectedOld = selectedObjects.toArray();
			selectedObjects.add(object);
			objectShapeMap.get(object).notifySelected();
			Object[] selectedNew = selectedObjects.toArray();
			fireGraphChanged(new GraphEvent(this));
			firePropertyChange(PROPERTY_NAME_SELECTED, selectedOld, selectedNew);
		}
	}
	
	public void deselectShape(Shape shape) {
		if (shape != null) {
			deselect(shape.getModelObject());
		}
	}
	
	public void deselect(Object object) {
		if(selectedObjects.contains(object)) {
			Object[] selectedOld = selectedObjects.toArray();
			selectedObjects.remove(object);
			objectShapeMap.get(object).notifyUnselected();
			Object[] selectedNew = selectedObjects.toArray();
			fireGraphChanged(new GraphEvent(this));
			firePropertyChange(PROPERTY_NAME_SELECTED, selectedOld, selectedNew);
		}
	}
	
	public boolean isShapeSelected(Shape shape) {
		return isSelected(shape.getModelObject());
	}
	
	public boolean isSelected(Object object) {
		return selectedObjects.contains(object);
	}

	public void fireGraphChanged() {
		fireGraphChanged(new GraphEvent(this));
	}

	protected void fireGraphChanged(GraphEvent event) {
		if (aGraphListener == null) {
			return;
		}
		aGraphListener.graphChanged(event);
	}

	public void firePropertyChange(PropertyChangeEvent evt) {
		getPropertyChange().firePropertyChange(evt);
	}

	public void firePropertyChange(String propertyName, int oldValue, int newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}

	public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}

	public Dimension getPreferedCanvasSize(Graphics2D g) throws NotReadyException {
		Dimension dim = null;
		if (getTopShape() == null) {
			dim = new Dimension(1, 1);
		} else {
			AffineTransform oldTransform = g.getTransform();
			resizeManager.zoomGraphics(g);
			Dimension oldDim = getContainerLayout().getPreferedSize(getTopShape(), g);
			g.setTransform(oldTransform);
			switch(resizeManager.getResizeMode()) {
				case AUTO_DISPLAYED: case FIX_DISPLAYED:
					return resizeManager.zoom(oldDim);
				case AUTO_UNZOOMED: case FIX_UNZOOMED:
					return oldDim;
			}
			return oldDim;
		}
		return dim;
	}

	protected PropertyChangeSupport getPropertyChange() {
		if (propertyChange == null) {
			propertyChange = new java.beans.PropertyChangeSupport(this);
		}
		return propertyChange;
	}

	public Shape getSelectedShape() {
		List<Shape> selectedShapes = getSelectedShapes();
		if (selectedShapes != null && selectedShapes.size() > 0) {
			return selectedShapes.get(0);
		}
		return null;
	}

	public List<Shape> getSelectedShapes() {
		List<Shape> selectedShapes = new ArrayList<Shape>();
		for(Object selectedObject : selectedObjects) {
			Shape fs = objectShapeMap.get(selectedObject);
			if (fs.getVisualState().isShowingItself()) {
				selectedShapes.add(fs);
			}
		}
		return selectedShapes;
	}

	public Shape getShapeFromLabel(String label) {
		for(Shape fs : objectShapeMap.values()) {
			if (label.equals(fs.getLabel())) {
				return fs;
			}
		}
		return null;
	}

	public Shape getShapeFromModelObject(Object obj) {
		return objectShapeMap.get(obj);
	}

	public Collection<Shape> getShapes() {
		return objectShapeMap.values();
	}
	
	public Set<Object> getObjects() {
		return objectShapeMap.keySet();
	}

	public Shape getTopShape() throws NotReadyException {
		if (objectShapeMap == null) {
			return null;
		}
		int numShapes = objectShapeMap.size();
		if (numShapes == 0) {
			return null;
		}
		Shape topShape = null;
		try {
			for (Shape fs : objectShapeMap.values()) {
				if (fs.getParent() == null) {
					if (topShape != null) {
						GraphModelShapeHierarchyPrinter.showShapeHierarchyBottomUp(this);
						GraphModelShapeHierarchyPrinter.showShapeHierarchyTopDown(this);
						throw new NotReadyException(
								"Too many top level shapes, at least "
								+ topShape + " and " + fs + 
						"; probably tried to paint while updating graph model.");
					}
					topShape = fs;
				}
			}
		} 
		catch(NullPointerException exception) { throw new GraphModel.NotReadyException(exception); }
		catch(ConcurrentModificationException exception) { throw new GraphModel.NotReadyException(exception); }
		if (topShape == null) {
			throw new NotReadyException("There are no top shapes; probably tried to paint while" +
					" updating graph model.");
		}
		return topShape;
	}
	
	public int getNumShapes(){
		if (objectShapeMap==null){
			return 0;
		}
		return objectShapeMap.size();
	}

	public int getZoomPercent() {
		return resizeManager.getZoomPercent();
	}

	protected void handleException(Throwable exception) {
		/* Uncomment the following lines to print uncaught exceptions to stdout */
		System.out
				.println("--------- UNCAUGHT EXCEPTION --------- in GraphModel");
		exception.printStackTrace(System.out);
	}

	boolean hasEdge(ElipseShape node1, ElipseShape node2) {
		for (Shape fs : objectShapeMap.values()) {
			if (fs instanceof EdgeShape) {
				EdgeShape edge = (EdgeShape) fs;
				if ((edge.startShape == node1 && edge.endShape == node2)
						|| (edge.startShape == node2 && edge.endShape == node1)) {
					return true;
				}
			}
		}
		return false;
	}

	public synchronized boolean hasListeners(String propertyName) {
		return getPropertyChange().hasListeners(propertyName);
	}

	public void notifyChangeEvent() {
		fireGraphChanged(new GraphEvent(this));
	}

	public void paint(java.awt.Graphics2D g) {
		// showShapeHierarchyBottomUp();
		// showShapeHierarchyTopDown();
		try {
			if (g == null) {
				System.out.println("graphics is null");
				return;
			}
			Shape topShape = getTopShape();
			if (objectShapeMap == null) {
				return;
			} else if (topShape != null) {
				AffineTransform oldTransform = g.getTransform();
				resizeManager.zoomGraphics(g);
				topShape.paint(g, 0, 0);
				g.setTransform(oldTransform);
			}
		} catch (Exception e) {
			g.setColor(Color.red);
			g.drawString("EXCEPTION IN GraphModel.paint(): " + e.getMessage(),
					20, 20);
			e.printStackTrace(System.out);
			return;
		}
	}

	public Shape pickEdgeWorld(Point point) {
		for (Shape fs : objectShapeMap.values()) {
			if (fs instanceof EdgeShape) {
				Shape pickedShape = fs.pick(point);
				if (pickedShape == fs) {
					return pickedShape;
				}
			}
		}
		return null;
	}

	public Shape pickWorld(Point argPoint) {
		Shape topShape;
		try {
			topShape = getTopShape();
		} catch (NotReadyException e) {
			return null;
		}
		if (topShape == null)
			return null;
		return topShape.pick(argPoint);
	}

	public List<Shape> pickWorld(Rectangle argRectWorld) {
		Shape topShape;
		try {
			topShape = getTopShape();
		} catch (NotReadyException e) {
			return null;
		}
		if (topShape == null)
			return null;
		List<Shape> pickedList = new ArrayList<Shape>();
		for (Shape shape : objectShapeMap.values()) {
			if (argRectWorld.contains(shape.spaceManager.getAbsLoc())) {
				pickedList.add(shape);
			}
		}
		return pickedList;
	}

	public abstract void refreshAll();

	public void removeGraphListener(GraphListener newListener) {
		aGraphListener = GraphEventMulticaster.remove(aGraphListener,
				newListener);
		return;
	}

	public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(listener);
	}

	public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		getPropertyChange().removePropertyChangeListener(propertyName, listener);
	}

	public void removeShape(Shape shape) {
		deselectShape(shape);
		objectShapeMap.remove(shape.getModelObject());
		Shape parent = shape.getParent();
		if (parent != null) {
			parent.removeChild(shape);
		}
		fireGraphChanged(new GraphEvent(this));
	}

	public ResizeMode getResizeMode() { return resizeManager.getResizeMode(); }
	
	public void setResizeMode(ResizeMode resizeMode) throws GraphModel.NotReadyException { 
		resizeManager.setResizeMode(resizeMode); 
	}
	
	public void searchText(String text) {
		String lowerCaseText = text.toLowerCase();
		Set<Object> selectedObjectsNew = new HashSet<Object>();
		for(Map.Entry<Object, Shape> entry : objectShapeMap.entrySet()) {
			Object object = entry.getKey();
			Shape shape = entry.getValue();
			if(text != null && text.length() != 0 && shape.getLabel() != null && shape.getLabel().toLowerCase().contains(lowerCaseText)) {
				selectedObjectsNew.add(object);
			}
		}
		setSelectedObjects(selectedObjectsNew.toArray());
	}

}
