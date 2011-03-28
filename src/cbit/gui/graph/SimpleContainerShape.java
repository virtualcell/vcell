package cbit.gui.graph;

/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/

import cbit.gui.graph.ContainerShape;
import cbit.gui.graph.GraphModel;

public class SimpleContainerShape extends ContainerShape {

	private Object fieldObject = null;

	public SimpleContainerShape(Object object, GraphModel graphModel, String argLabel) {
		super(graphModel);
		setLabel(argLabel);
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
	public void refreshLabel() {
	}
}
