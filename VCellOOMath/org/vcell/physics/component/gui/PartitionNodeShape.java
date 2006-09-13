package org.vcell.physics.component.gui;

import cbit.gui.graph.GraphModel;
import cbit.gui.graph.NodeShape;

public class PartitionNodeShape extends NodeShape {
	
	public PartitionNodeShape(cbit.util.graph.Node node, GraphModel graphModel, int degree){
		super(node,graphModel,0);
		defaultBG = java.awt.Color.orange;
		defaultFGselect = java.awt.Color.black;
		backgroundColor = defaultBG;
		darkerBackground = backgroundColor.darker().darker();
		refreshLabel();
	}

	public void paint(java.awt.Graphics2D g, int parentOffsetX,	int parentOffsetY) {
		super.paint(g, parentOffsetX, parentOffsetY);

		int absPosX = screenPos.x + parentOffsetX;
		int absPosY = screenPos.y + parentOffsetY;

		g.drawString("E", absPosX+6,absPosY+13);
	}
}
