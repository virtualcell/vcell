package cbit.gui.graph;

/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 �*/

import java.awt.Dimension;
import java.awt.Graphics2D;

public interface GraphContainerLayout {

	public void layout(GraphModel graphModel, Graphics2D g2d, Dimension size);
	
	public Dimension getPreferedSize(Shape shape, Graphics2D g);
	
	public void refreshLayoutChildren(Shape shape);
	
}