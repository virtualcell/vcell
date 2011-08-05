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
import java.awt.Rectangle;

public interface GraphContainerLayout {

	public void layout(GraphModel graphModel, Graphics2D g2d, Dimension size) 
	throws GraphModel.NotReadyException;
	
	public Dimension getPreferedSize(Shape shape, Graphics2D g) 
	throws GraphModel.NotReadyException;
	
	public void refreshLayoutChildren(Shape shape);
	
	public Rectangle getBoundaryForAutomaticLayout(Shape shape);
	public boolean isContainerForAutomaticLayout(Shape shape);
	public boolean isNodeForAutomaticLayout(Shape shape);
	
}
