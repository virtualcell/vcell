package cbit.gui.graph;

/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 �*/

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