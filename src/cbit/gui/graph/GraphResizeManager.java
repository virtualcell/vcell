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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

public class GraphResizeManager {
	
	public static enum ResizeMode { 
		AUTO_DISPLAYED("Automatic Displayed Size", "Automatically adjust displayed size to panel or need"), 
		AUTO_UNZOOMED("Automatic Unzoomed Size", "Automatically adjust unzoomed size to panel or need"), 
		FIX_DISPLAYED("Fixed Displayed Size", "Keep displayed size fixed"), 
		FIX_UNZOOMED("Fixed Unzoomed Size", "Keep unzoomed size fixed"); 
	
		protected final String shortDescription, longDescription;
	
		private ResizeMode(String shortDescription, String longDescription) {
			this.shortDescription = shortDescription;
			this.longDescription = longDescription;
		}
		
		public String getShortDescription() { return shortDescription; }
		public String getLongDescription() { return longDescription; }
		
	}
	
	public static class State {
		protected final ResizeMode resizeMode;
		protected final int zoomPercent;
		protected final Dimension size;
		
		public State(ResizeMode resizeMode, int zoomPercent, Dimension size) {
			this.resizeMode = resizeMode;
			this.zoomPercent = zoomPercent;
			this.size = size;
		}
		
		public ResizeMode getResizeMode() { return resizeMode; }
		public int getZoomPercent() { return zoomPercent; }
		public Dimension getSize() { return size; }
	}

	public static enum Property { MODE, ZOOMPERCENT, SIZE }
	
	public static class Event {
		protected final State oldState, newState;

		public Event(State oldState, State newState) {
			this.oldState = oldState;
			this.newState = newState;
		}
		
		public State getOldState() { return oldState; }
		public State getNewState() { return newState; }
		
		public boolean hasChanged(Property property) {
			switch(property) {
			case MODE: 
				return !newState.getResizeMode().equals(oldState.getResizeMode());
			case ZOOMPERCENT:
				return newState.getZoomPercent() != oldState.getZoomPercent();
			case SIZE:
				return !newState.getSize().equals(oldState.getSize());
			default:
				return false;
			}
			
		}
	}
	
	public static interface Listener {
		public void resizeStateChanged(Event event) throws GraphModel.NotReadyException;
	}
	
	protected transient Set<Listener> listeners = new HashSet<Listener>();
	protected final GraphModel graph;
	
	protected ResizeMode resizeMode = ResizeMode.AUTO_DISPLAYED;
	private int zoomPercent = 100;
	
	public GraphResizeManager(GraphModel graph) {
		this.graph = graph;
	}
	
	public void setResizeMode(ResizeMode resizeMode) throws GraphModel.NotReadyException {
		if(this.resizeMode != resizeMode) {
			State oldState = getState();
			this.resizeMode = resizeMode;
			graph.fireGraphChanged();
			fireChange(oldState, getState());
		}
	}
	
	public ResizeMode getResizeMode() { return resizeMode; }
	
	@SuppressWarnings("serial")
	public static class ZoomRangeException extends Exception {
		public ZoomRangeException(String message) { super(message); }
	}
	
	public void setZoomPercent(int zoomPercent) throws ZoomRangeException, GraphModel.NotReadyException {
		if (zoomPercent < minZoomLevel) {
			throw new ZoomRangeException("Zoom percentage " + zoomPercent + 
					" is too small. Must be at least 1.");
		}
		if (zoomPercent > maxZoomLevel) {
			throw new ZoomRangeException("Zoom percentage " + zoomPercent + 
			" is too large. Must be no more than 1000.");
		}
		if(zoomPercent != this.zoomPercent) {
			State oldState = getState();
			switch(getResizeMode()) {
			case AUTO_DISPLAYED: case AUTO_UNZOOMED: case FIX_UNZOOMED: {
				this.zoomPercent = zoomPercent;
				graph.fireGraphChanged();
				fireChange(oldState, getState());
				break;
			}
			case FIX_DISPLAYED: {
				Dimension displayedSize = getDisplayedSize();
				this.zoomPercent = zoomPercent;
				setDisplayedSize(displayedSize);
				break;
			}
			}
		}
	}
	
	public int getZoomPercent() { return zoomPercent; }
	
	private static final int minZoomLevel = 1, maxZoomLevel = 1000;
	private static final int[] zoomLevels = { 1, 2, 3, 4, 5, 6, 8, 10, 12, 14, 17, 21, 26, 32, 40, 50, 
		64, 80, 100, 120, 140, 170, 210, 260, 320, 400, 500, 640, 800, 1000};

	public static int getNextHigherZoomPercent(int zoomPercent) throws ZoomRangeException {
		for(int zoomPercentNew : zoomLevels) {
			if(zoomPercentNew > zoomPercent) {
				return zoomPercentNew;
			}
		}		
		throw new ZoomRangeException("There is no zoom level higher than " + zoomPercent +
				". Highest possible is " + maxZoomLevel);
	}
	
	public void zoomIn() throws ZoomRangeException, GraphModel.NotReadyException {
		setZoomPercent(getNextHigherZoomPercent(zoomPercent));
	}

	public static int getNextLowerZoomPercent(int zoomPercent) throws ZoomRangeException {
		for(int i = zoomLevels.length - 1;  i >= 0; --i) {
			int zoomPercentNew = zoomLevels[i];
			if(zoomPercentNew < zoomPercent) {
				return zoomPercentNew;
			}
		}
		throw new ZoomRangeException("There is no zoom level lower than " + zoomPercent +
				". Lowest possible is " + minZoomLevel);
	}
	
	public void zoomOut() throws ZoomRangeException, GraphModel.NotReadyException {
		setZoomPercent(getNextLowerZoomPercent(zoomPercent));
	}

	public void zoomGraphics(Graphics2D graphics) { 
		graphics.scale(zoomPercent/100.0, zoomPercent/100.0); 
	}
	
	public double zoom(double x) { return x*(zoomPercent/100.0); }
	
	public Dimension zoom(Dimension dim) {
		return new Dimension((int) zoom(dim.width), (int) zoom(dim.height)); 
	}
	
	public Point zoom(Point point) {
		return new Point((int) zoom(point.x), (int) zoom(point.y)); 
	}
	
	public double unzoom(double x) { return x*(100.0/zoomPercent); }
	
	public Dimension unzoom(Dimension dim) {
		return new Dimension((int) unzoom(dim.width), (int) unzoom(dim.height)); 
	}
	
	public Point unzoom(Point point) {
		return new Point((int) unzoom(point.x), (int) unzoom(point.y)); 
	}
	
	public State getState() throws GraphModel.NotReadyException {
		return new State(resizeMode, zoomPercent, getUnzoomedSize());
	}
	
	public void setState(State state) throws GraphModel.NotReadyException {
		Dimension size = getUnzoomedSize();
		if(!resizeMode.equals(state.getResizeMode()) || zoomPercent != state.getZoomPercent()
				|| !size.equals(state.getSize())) {
			State oldState = getState();
			resizeMode = state.getResizeMode();
			zoomPercent = state.getZoomPercent();
			setUnzoomedSize(state.getSize());
			graph.fireGraphChanged();			
			fireChange(oldState, state);
		}
	}
	
	public void setUnzoomedSize(Dimension size) throws GraphModel.NotReadyException {
		ShapeSpaceManager spaceManager = graph.getTopShape().getSpaceManager();
		if(!spaceManager.getSize().equals(size)) {
			State oldState = getState();
			spaceManager.setSize(size);	
			graph.fireGraphChanged();
			fireChange(oldState, getState());
		}
	}
	
	public Dimension getUnzoomedSize() throws GraphModel.NotReadyException {
		return graph.getTopShape().getSpaceManager().getSize();
	}
	
	public void setDisplayedSize(Dimension displayedSize) throws GraphModel.NotReadyException {
		Dimension size = unzoom(displayedSize);
		setUnzoomedSize(size);
	}
	
	public Dimension getDisplayedSize() throws GraphModel.NotReadyException {
		return zoom(graph.getTopShape().getSpaceManager().getSize());
	}
	
	public void notifyComponentSize(Dimension newSize) throws GraphModel.NotReadyException {
		if (graph.getTopShape() != null) {
			switch(getResizeMode()) {
			case AUTO_DISPLAYED: {
				setDisplayedSize(newSize);
				break;
			}
			case AUTO_UNZOOMED: {
				setUnzoomedSize(newSize);
				break;
			}
			case FIX_DISPLAYED: case FIX_UNZOOMED:
				break; // do nothing
			}
		}
	}
	
	public void addListener(Listener listener) { listeners.add(listener); }
	public void removeListener(Listener listener) { listeners.remove(listener); }
	public boolean hasListener(Listener listener) { return listeners.contains(listener); }
	
	public void fireChange(State oldState, State newState) 
	throws GraphModel.NotReadyException {
		Event event = new Event(oldState, newState);
		for(Listener listener : listeners) {
			listener.resizeStateChanged(event);
		}
	}
	
}
