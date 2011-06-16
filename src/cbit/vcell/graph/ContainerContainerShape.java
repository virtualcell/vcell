/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.graph;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cbit.gui.graph.ContainerShape;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.Shape;
import cbit.vcell.model.Model;


public class ContainerContainerShape extends ContainerShape {
	Model model = null;

	// or all compartments
	List<ReactionContainerShape> structureContainers = null;

	public ContainerContainerShape(GraphModel graphModel, Model model, 
			List<ReactionContainerShape> reactionContainers) {
		super(graphModel);
		this.structureContainers = new ArrayList<ReactionContainerShape>(reactionContainers);
		this.model = model;
		setLabel(" ");
		for(ReactionContainerShape reactionContainer : reactionContainers) {
			addChildShape(reactionContainer);
		}

	}

	@Override
	public Object getModelObject() {
		return model;
	}

	public void addReactionContainer(ReactionContainerShape reactionContainerShape) {
		if(!structureContainers.contains(reactionContainerShape)) {
			structureContainers.add(reactionContainerShape);
		}
		if(!getChildren().contains(reactionContainerShape)) {
			addChildShape(reactionContainerShape);
		}
		graphModel.getContainerLayout().refreshLayoutChildren(this);
	}
	
	public void removeReactionContainer(ReactionContainerShape reactionContainerShape) {
		while(structureContainers.contains(reactionContainerShape)) {
			structureContainers.remove(reactionContainerShape);
		}
		while(getChildren().contains(reactionContainerShape)) {
			removeChild(reactionContainerShape);
		}
		graphModel.getContainerLayout().refreshLayoutChildren(this);
	}

	public void setReactionContainerShapeList(List<ReactionContainerShape> newList) {
		structureContainers = new ArrayList<ReactionContainerShape>(newList);
		for(ReactionContainerShape newShape : newList) {
			if(!getChildren().contains(newShape)) {
				addChildShape(newShape);
			}
		}
		Set<Shape> childrenToBeRemoved = new HashSet<Shape>();
		for(Shape child : getChildren()) {
			if(!structureContainers.contains(child)) {
				childrenToBeRemoved.add(child);
			}
		}
		for(Shape childToBeRemoved : childrenToBeRemoved) {
			removeChild(childToBeRemoved);
		}
		graphModel.getContainerLayout().refreshLayoutChildren(this);
	}
	
	public final void refreshLayoutSelf() {}
	
	public void paintSelf (Graphics2D g, int absPosX, int absPosY ) {
		g.setColor(backgroundColor);
		g.fillRect(absPosX, absPosY, spaceManager.getSize().width, spaceManager.getSize().height);
		g.setColor(forgroundColor);
		g.drawRect(absPosX,absPosY, spaceManager.getSize().width, spaceManager.getSize().height);
	}

	public List<ReactionContainerShape> getStructureContainers() {
		return structureContainers;
	}

	@Override
	public void notifySelected() {
		notifyUnselected();
	}
}
