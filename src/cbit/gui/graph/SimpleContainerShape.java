package cbit.gui.graph;

/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/

import cbit.gui.graph.ContainerShape;
import cbit.gui.graph.EdgeShape;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.Shape;

public class SimpleContainerShape extends ContainerShape {

	private Object fieldObject = null;

	public SimpleContainerShape(Object object, GraphModel graphModel, String argLabel) {
		super(graphModel);
		setLabel(argLabel);
		setRandomLayout(false);
		fieldObject = object;
		bNoFill = false;
		defaultFGselect = java.awt.Color.red;
		defaultBGselect = java.awt.Color.white;
		backgroundColor = java.awt.Color.white;
	}

	@Override
	public Object getModelObject() {
		return fieldObject;
	}

	@Override
	public void randomize() {
		// randomize the locations of speciesContexts and of reactionSteps,
		// then draw in the reactionParticipant edges
		for (int i=0;i<childShapeList.size();i++){
			Shape child = childShapeList.get(i);
			if (ShapeUtil.isMovable(child)){
				// position normally about the center
				child.getSpaceManager().setRelPos(getRandomPosition());
			}	
		}
		// calculate locations and sizes of reactionParticipant edges
		for (int i=0;i<childShapeList.size();i++){
			Shape child = childShapeList.get(i);
			if (child instanceof EdgeShape){
				((EdgeShape)child).refreshLayoutSelf();
			}
		}
		// position label
		int centerX = getSpaceManager().getSize().width/2;
		int currentY = getLabelSize().height;
		labelPos.x = centerX - getLabelSize().width/2; 
		labelPos.y = currentY;
		currentY += getLabelSize().height;	
	}

	@Override
	public void refreshLabel() {
	}
}
