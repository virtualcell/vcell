package cbit.gui.graph;

/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 �*/
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

import org.vcell.util.BeanUtils;

import cbit.gui.graph.GraphListener;

public abstract class GraphModel {
	private Vector<Shape> shapeList = new Vector<Shape>();
	protected transient cbit.gui.graph.GraphListener aGraphListener = null;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private int fieldZoomPercent = 100;
	private boolean fieldResizable = true;

	public GraphModel() {
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
		shapeList.addElement(shape);
	}

	protected final void clearAllShapes() {
		shapeList.removeAllElements();
	}

	public void clearSelection() {
		for (int i = 0; i < shapeList.size(); i++) {
			Shape fs = shapeList.elementAt(i);
			if (fs.isSelected()) {
				fs.unselect();
			}
		}
		fireGraphChanged(new GraphEvent(this));
	}

	public void deselect(Shape shape) {
		if (shape.isSelected()) {
			shape.unselect();
			fireGraphChanged(new GraphEvent(this));
		}
	}

	public void fireGraphChanged() {
		fireGraphChanged(new GraphEvent(this));
	}

	protected void fireGraphChanged(GraphEvent event) {
		if (aGraphListener == null) {
			return;
		}
		;
		aGraphListener.graphChanged(event);
	}

	public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
		getPropertyChange().firePropertyChange(evt);
	}

	public void firePropertyChange(java.lang.String propertyName, int oldValue,
			int newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}

	public void firePropertyChange(java.lang.String propertyName,
			java.lang.Object oldValue, java.lang.Object newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}

	public void firePropertyChange(java.lang.String propertyName,
			boolean oldValue, boolean newValue) {
		getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
	}

	ElipseShape firstNode() {
		for (int i = 0; i < shapeList.size(); i++) {
			Shape fs = shapeList.elementAt(i);
			if (fs instanceof ElipseShape) {
				return (ElipseShape) fs;
			}
		}
		return null;
	}

	public Shape[] getAllSelectedShapes() {
		Vector<Shape> selectedShapeList = new Vector<Shape>();
		for (int i = 0; i < shapeList.size(); i++) {
			Shape fs = shapeList.elementAt(i);
			if (fs.isSelected()) {
				selectedShapeList.add(fs);
			}
		}
		Shape selectedShapes[] = new Shape[selectedShapeList.size()];
		selectedShapeList.copyInto(selectedShapes);
		return selectedShapes;
	}

	int getIndexFromNode(ElipseShape shape) {
		return shapeList.indexOf(shape);
	}

	ElipseShape getNodeFromIndex(int index) {
		if (index < 0 || index >= shapeList.size()) {
			return null;
		}
		Shape shape = shapeList.elementAt(index);
		if (shape instanceof ElipseShape) {
			return (ElipseShape) shape;
		} else {
			return null;
		}
	}

	public int getNumShapes() {
		return shapeList.size();
	}

	public Dimension getPreferedSize(Graphics2D g) {
		Dimension dim = null;
		if (getTopShape() == null) {
			dim = new Dimension(1, 1);
		} else {
			AffineTransform oldTransform = g.getTransform();
			g.scale(fieldZoomPercent / 100.0, fieldZoomPercent / 100.0);
			Dimension oldDim = getTopShape().getPreferedSize(g);
			g.setTransform(oldTransform);
			double newWidth = oldDim.width * (fieldZoomPercent / 100.0);
			double newHeight = oldDim.height * (fieldZoomPercent / 100.0);
			dim = new Dimension((int) newWidth, (int) newHeight);
		}
		return dim;
	}

	protected PropertyChangeSupport getPropertyChange() {
		if (propertyChange == null) {
			propertyChange = new java.beans.PropertyChangeSupport(this);
		}
		return propertyChange;
	}

	public boolean getResizable() {
		return fieldResizable;
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
		for (int i = 0; i < shapeList.size(); i++) {
			Shape fs = shapeList.elementAt(i);
			if (fs.isSelected()) {
				selectedShapes.add(fs);
			}
		}
		return selectedShapes;
	}

	public Shape getShapeFromLabel(String label) {
		for (int i = 0; i < shapeList.size(); i++) {
			Shape fs = shapeList.elementAt(i);
			if (label.equals(fs.getLabel())) {
				return fs;
			}
		}
		return null;
	}

	public Shape getShapeFromModelObject(Object obj) {
		for (int i = 0; i < shapeList.size(); i++) {
			Shape fs = shapeList.elementAt(i);
			if (fs.getModelObject() == obj) {
				return fs;
			}
		}
		return null;
	}

	public List<Shape> getShapes() {
		return shapeList;
	}

	public Shape getTopShape() {
		if (shapeList == null) {
			return null;
		}
		int numShapes = shapeList.size();
		if (numShapes == 0) {
			return null;
		}
		Shape topShape = null;
		for (int i = 0; i < numShapes; i++) {
			Shape fs = shapeList.elementAt(i);
			if (fs.getParent() == null) {
				if (topShape != null) {
					showShapeHierarchyBottomUp();
					showShapeHierarchyTopDown();
					throw new RuntimeException(
							"ERROR: too many top level shapes, at least "
									+ topShape + " and " + fs);
				}
				topShape = fs;
			}
		}
		if (topShape == null) {
			throw new RuntimeException("there are no top shapes");
		}
		return topShape;
	}

	public int getZoomPercent() {
		return fieldZoomPercent;
	}

	protected void handleException(Throwable exception) {
		/* Uncomment the following lines to print uncaught exceptions to stdout */
		System.out
				.println("--------- UNCAUGHT EXCEPTION --------- in GraphModel");
		exception.printStackTrace(System.out);
	}

	boolean hasEdge(ElipseShape node1, ElipseShape node2) {
		for (int i = 0; i < shapeList.size(); i++) {
			Shape fs = shapeList.elementAt(i);
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

	ElipseShape nextNode(ElipseShape shape) {
		int startIndex = shapeList.indexOf(shape) + 1;
		if (startIndex >= shapeList.size()) {
			return null;
		}
		for (int i = startIndex; i < shapeList.size(); i++) {
			Shape fs = shapeList.elementAt(i);
			if (fs instanceof ElipseShape) {
				return (ElipseShape) fs;
			}
		}
		return null;
	}

	public void notifyChangeEvent() {
		fireGraphChanged(new GraphEvent(this));
	}

	int numberOfEdges() {
		int nodeCount = 0;
		for (int i = 0; i < shapeList.size(); i++) {
			Shape fs = shapeList.elementAt(i);
			if (fs instanceof EdgeShape) {
				nodeCount++;
			}
		}
		return nodeCount;
	}

	int numberOfNodes() {
		int nodeCount = 0;
		for (int i = 0; i < shapeList.size(); i++) {
			Shape fs = shapeList.elementAt(i);
			if (fs instanceof ElipseShape) {
				nodeCount++;
			}
		}
		return nodeCount;
	}

	public void paint(java.awt.Graphics2D g, GraphPane canvas) {
		// showShapeHierarchyBottomUp();
		// showShapeHierarchyTopDown();
		try {
			if (g == null) {
				System.out.println("graphics is null");
				return;
			}
			if (canvas == null) {
				System.out.println("canvas is null");
				// return;
			}
			Shape topShape = getTopShape();
			if (shapeList == null && canvas != null) {
				canvas.clear(g);
				return;
			} else if (topShape != null) {
				AffineTransform oldTransform = g.getTransform();
				g.scale(fieldZoomPercent / 100.0, fieldZoomPercent / 100.0);
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
		for (int i = 0; i < shapeList.size(); i++) {
			Shape fs = shapeList.elementAt(i);
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
		Shape topShape = getTopShape();
		if (topShape == null)
			return null;
		return topShape.pick(argPoint);
	}

	public Shape[] pickWorld(Rectangle argRectWorld) {
		Shape topShape = getTopShape();
		if (topShape == null)
			return null;
		Vector<Shape> pickedList = new Vector<Shape>();
		for (int i = 0; i < shapeList.size(); i++) {
			Shape shape = shapeList.elementAt(i);
			if (argRectWorld.contains(shape.getAbsLocation())) {
				pickedList.add(shape);
			}
		}
		return (Shape[]) BeanUtils.getArray(pickedList, Shape.class);
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
		if (shapeList.contains(shape)) {
			shapeList.removeElement(shape);
		}
		Shape parent = shape.getParent();
		if (parent != null) {
			parent.removeChild(shape);
		}
		fireGraphChanged(new GraphEvent(this));
	}

	public void resize(Graphics2D g, Dimension newSize) throws Exception {

		if (getTopShape() != null) {
			double newWidth = (100.0 / fieldZoomPercent) * newSize.getWidth();
			double newHeight = (100.0 / fieldZoomPercent) * newSize.getHeight();
			getTopShape().resize(g,
					new Dimension((int) newWidth, (int) newHeight));
		}
	}

	public void select(Shape shape) {
		if (shape == null) {
			return;
		}
		if (!shape.isSelected()) {
			shape.select();
			fireGraphChanged(new GraphEvent(this));
		}
	}

	public void setResizable(boolean resizable) {
		boolean oldValue = fieldResizable;
		fieldResizable = resizable;
		firePropertyChange("resizable", new Boolean(oldValue), new Boolean(
				resizable));
	}

	public void setZoomPercent(int zoomPercent) {
		if (zoomPercent < 1 || zoomPercent > 1000) {
			throw new RuntimeException("zoomPercent must be between 1 and 1000");
		}
		int oldValue = fieldZoomPercent;
		fieldZoomPercent = zoomPercent;
		firePropertyChange("zoomPercent", new Integer(oldValue), new Integer(
				zoomPercent));
		fireGraphChanged();
	}

	public void showShapeHierarchyBottomUp() {
		System.out.println("<<<<<<<<<Shape Hierarchy Bottom Up>>>>>>>>>");
		Vector<Shape> shapes = new Vector<Shape>(shapeList);
		// gather top(s) ... should only have one
		Vector<Shape> topList = new Vector<Shape>();
		for (int i = 0; i < shapes.size(); i++) {
			if (shapes.elementAt(i).getParent() == null) {
				topList.add(shapes.elementAt(i));
			}
		}
		// for each top, print tree
		Stack<Shape> stack = new Stack<Shape>();
		for (int j = 0; j < topList.size(); j++) {
			Shape top = topList.elementAt(j);
			System.out.println(top.toString());
			stack.push(top);
			shapes.remove(top);
			while (true) {
				// find first remaining children of current parent and print
				boolean bChildFound = false;
				for (int i = 0; i < shapes.size() && stack.size() > 0; i++) {
					Shape shape = shapes.elementAt(i);
					if (shape.getParent() == stack.peek()) {
						char padding[] = new char[4 * stack.size()];
						for (int k = 0; k < padding.length; k++)
							padding[k] = ' ';
						String pad = new String(padding);
						System.out.println(pad + shape.toString());
						stack.push(shape);
						shapes.remove(shape);
						bChildFound = true;
						break;
					}
				}
				if (stack.size() == 0) {
					break;
				}
				if (bChildFound == false) {
					stack.pop();
				}
			}
		}
		if (shapes.size() > 0) {
			System.out.println(".......shapes left over:");
			for (int i = 0; i < shapes.size(); i++) {
				System.out.println((shapes.elementAt(i)).toString());
			}
		}

	}

	public void showShapeHierarchyTopDown() {
		System.out.println("<<<<<<<<<Shape Hierarchy Top Down>>>>>>>>>");
		Vector<Shape> shapes = new Vector<Shape>(shapeList);
		// gather top(s) ... should only have one
		Vector<Shape> topList = new Vector<Shape>();
		for (int i = 0; i < shapes.size(); i++) {
			if (shapes.elementAt(i).getParent() == null) {
				topList.add(shapes.elementAt(i));
			}
		}
		// for each top, print tree
		Stack<Shape> stack = new Stack<Shape>();
		for (int j = 0; j < topList.size(); j++) {
			Shape top = topList.elementAt(j);
			System.out.println(top.toString());
			stack.push(top);
			shapes.remove(top);
			while (stack.size() > 0) {
				// find first remaining children of current parent and print
				boolean bChildFound = false;
				Shape currShape = stack.peek();
				for (Shape shape : currShape.getChildren()) {
					if (!shapes.contains(shape))
						continue;
					char padding[] = new char[4 * stack.size()];
					for (int k = 0; k < padding.length; k++)
						padding[k] = ' ';
					String pad = new String(padding);
					System.out.println(pad + shape.toString());
					stack.push(shape);
					shapes.remove(shape);
					bChildFound = true;
					break;
				}
				if (bChildFound == false) {
					stack.pop();
				}
			}
		}
		if (shapes.size() > 0) {
			System.out.println(".......shapes left over:");
			for (int i = 0; i < shapes.size(); i++) {
				System.out.println((shapes.elementAt(i)).toString());
			}
		}
	}
}