/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.constraints.graph;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

import cbit.gui.graph.GraphEvent;
import cbit.gui.graph.ContainerShape;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.Shape;
import cbit.gui.graph.SimpleContainerShape;
import cbit.vcell.constraints.ConstraintContainerImpl;
import cbit.vcell.constraints.GeneralConstraint;
import cbit.vcell.constraints.SimpleBounds;

public class ConstraintsGraphModel extends GraphModel implements PropertyChangeListener {
	private ConstraintContainerImpl fieldConstraintContainerImpl = null;

public ConstraintsGraphModel() {
	super();
	this.addPropertyChangeListener(this);
}

public ConstraintContainerImpl getConstraintContainerImpl() {
	return fieldConstraintContainerImpl;
}

public void propertyChange(java.beans.PropertyChangeEvent evt) {
	if (evt.getSource() == this && evt.getPropertyName().equals("constraintContainerImpl")){
		clearAllShapes();
		refreshAll();
	}
	if (evt.getSource() == getConstraintContainerImpl()){
		refreshAll();
	}
	if (evt.getSource() instanceof GeneralConstraint){
		if (evt.getPropertyName().equals("expression")){
			refreshAll();
		}else{
			fireGraphChanged();
		}
	}
	if (evt.getSource() instanceof SimpleBounds){
		fireGraphChanged();
	}
}

public void refreshAll() {
	Set<Shape> unwantedShapes = new HashSet<Shape>();
	unwantedShapes.addAll(getShapes());
	for(Shape shape : getShapes()) {
		if (shape instanceof ConstraintVarNode){
			((ConstraintVarNode)shape).setDegree(1);
		}
	}	
	ContainerShape containerShape = (ContainerShape)getShapeFromModelObject(getConstraintContainerImpl());
	if (containerShape==null){
		containerShape = new SimpleContainerShape(getConstraintContainerImpl(),this,"constraint network");
		containerShape.setLabel("constraint network");
		addShape(containerShape);
	}
	containerShape.refreshLabel();
	unwantedShapes.remove(containerShape);
	//
	// add nodes for GeneralConstraints and edges to its Variables (add Variable when necessary)
	//
	GeneralConstraint generalConstraints[] = getConstraintContainerImpl().getGeneralConstraints();
	for (int i = 0; i < generalConstraints.length; i++){
		String symbols[] = generalConstraints[i].getExpression().getSymbols();
		if (symbols==null){
			continue;
		}
		GeneralConstraintNode generalConstraintNode = (GeneralConstraintNode)getShapeFromModelObject(generalConstraints[i]);
		if (generalConstraintNode == null){
			generalConstraintNode = new GeneralConstraintNode(generalConstraints[i],this,symbols.length);
			containerShape.addChildShape(generalConstraintNode);
			addShape(generalConstraintNode);
		}
		generalConstraintNode.refreshLabel();
		unwantedShapes.remove(generalConstraintNode);		
		for (int j = 0; j < symbols.length; j++){
			ConstraintVarNode constraintVarNode = (ConstraintVarNode)getShapeFromLabel(symbols[j]);
			if (constraintVarNode==null){
				constraintVarNode = new ConstraintVarNode(symbols[j],this,1);
				containerShape.addChildShape(constraintVarNode);
				addShape(constraintVarNode);
			}else{
				constraintVarNode.setDegree(constraintVarNode.getDegree()+1);
			}
			constraintVarNode.refreshLabel();
			unwantedShapes.remove(constraintVarNode);
			ConstraintDependencyEdgeShape constraintDependencyEdgeShape = null;
			for(Shape shape : getShapes()) {
				if (shape instanceof ConstraintDependencyEdgeShape){
					if (((ConstraintDependencyEdgeShape)shape).getConstraintShape() == generalConstraintNode &&
						((ConstraintDependencyEdgeShape)shape).getVarShape() == constraintVarNode){
						constraintDependencyEdgeShape = (ConstraintDependencyEdgeShape)shape;
					}
				}
			}
			if (constraintDependencyEdgeShape == null){
				constraintDependencyEdgeShape = new ConstraintDependencyEdgeShape(generalConstraintNode,constraintVarNode,this);
				containerShape.addChildShape(constraintDependencyEdgeShape);
				addShape(constraintDependencyEdgeShape);
			}
			unwantedShapes.remove(constraintDependencyEdgeShape);
		}
	}
	//
	// add nodes for SimpleBounds and edges to its Variables (add Variable when necessary)
	//
	cbit.vcell.constraints.SimpleBounds simpleBounds[] = getConstraintContainerImpl().getSimpleBounds();
	for (int i = 0; i < simpleBounds.length; i++){
		BoundsNode boundsNode = (BoundsNode)getShapeFromModelObject(simpleBounds[i]);
		if (boundsNode==null){
			boundsNode = new BoundsNode(simpleBounds[i],this);
			containerShape.addChildShape(boundsNode);
			addShape(boundsNode);
		}
		boundsNode.refreshLabel();
		unwantedShapes.remove(boundsNode);
		ConstraintVarNode constraintVarNode = 
			(ConstraintVarNode)getShapeFromLabel(simpleBounds[i].getIdentifier());
		if (constraintVarNode==null){
			constraintVarNode = new ConstraintVarNode(simpleBounds[i].getIdentifier(),this,1);
			containerShape.addChildShape(constraintVarNode);
			addShape(constraintVarNode);
		}else{
			constraintVarNode.setDegree(constraintVarNode.getDegree()+1);
		}
		constraintVarNode.refreshLabel();
		unwantedShapes.remove(constraintVarNode);
		ConstraintDependencyEdgeShape constraintDependencyEdgeShape = null;
		for(Shape shape : getShapes()) {
			if (shape instanceof ConstraintDependencyEdgeShape){
				if (((ConstraintDependencyEdgeShape)shape).getConstraintShape() == boundsNode &&
					((ConstraintDependencyEdgeShape)shape).getVarShape() == constraintVarNode){
					constraintDependencyEdgeShape = (ConstraintDependencyEdgeShape)shape;
				}
			}
		}
		if (constraintDependencyEdgeShape == null){
			constraintDependencyEdgeShape = new ConstraintDependencyEdgeShape(boundsNode,constraintVarNode,this);
			containerShape.addChildShape(constraintDependencyEdgeShape);
			addShape(constraintDependencyEdgeShape);
		}
		constraintDependencyEdgeShape.refreshLabel();
		unwantedShapes.remove(constraintDependencyEdgeShape);
	}
	for(Shape unwantedShape : unwantedShapes) { removeShape(unwantedShape); }
	fireGraphChanged(new GraphEvent(this));
}

public void setConstraintContainerImpl(ConstraintContainerImpl constraintContainerImpl) {
	ConstraintContainerImpl oldValue = fieldConstraintContainerImpl;
	if (oldValue!=null){
		oldValue.removePropertyChangeListener(this);
		GeneralConstraint oldConstraints[] = oldValue.getGeneralConstraints();
		for (int i = 0; i < oldConstraints.length; i++){
			oldConstraints[i].removePropertyChangeListener(this);
		}
		SimpleBounds oldSimpleBounds[] = oldValue.getSimpleBounds();
		for (int i = 0; i < oldSimpleBounds.length; i++){
			oldSimpleBounds[i].removePropertyChangeListener(this);
		}
	}
	fieldConstraintContainerImpl = constraintContainerImpl;
	if (fieldConstraintContainerImpl!=null){
		fieldConstraintContainerImpl.addPropertyChangeListener(this);
		GeneralConstraint newConstraints[] = fieldConstraintContainerImpl.getGeneralConstraints();
		for (int i = 0; i < newConstraints.length; i++){
			newConstraints[i].addPropertyChangeListener(this);
		}
		SimpleBounds newSimpleBounds[] = fieldConstraintContainerImpl.getSimpleBounds();
		for (int i = 0; i < newSimpleBounds.length; i++){
			newSimpleBounds[i].addPropertyChangeListener(this);
		}
	}
	firePropertyChange("constraintContainerImpl", oldValue, constraintContainerImpl);
	refreshAll();
}
}
