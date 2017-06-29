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
