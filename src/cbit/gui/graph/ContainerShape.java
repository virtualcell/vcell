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

import java.awt.Point;
import java.util.Random;


public abstract class ContainerShape extends RectangleShape {

	private Random rand = new Random();

	public ContainerShape(GraphModel graphModel) {
		super(graphModel);
		getSpaceManager().setSize(100, 100);
	}

	public java.awt.Point getRandomPosition() {
		double meanX = getSpaceManager().getSize().width/2;
		double meanY = getSpaceManager().getSize().height/2;
		double sdX = getSpaceManager().getSize().width/8;
		double sdY = getSpaceManager().getSize().height/8;
		double randX = rand.nextGaussian();
		double randY = rand.nextGaussian();
		Point randPos = new Point();
		// position normally about the center
		randPos.x = (int)(sdX*randX + meanX);
		randPos.y = (int)(sdY*randY + meanY);
		// make sure that they fit
		double maxObjectWidth = 50;
		double maxObjectHeight = 50;
		randPos.x = (int) Math.max(maxObjectWidth/2, 
				Math.min(randPos.x, getSpaceManager().getSize().width - maxObjectWidth/2));
		randPos.y = (int) Math.max(maxObjectHeight/2, 
				Math.min(randPos.y, getSpaceManager().getSize().height - maxObjectHeight/2));
		return randPos;
	}

	@Override
	public void refreshLabel() {
		setLabel("Graph");
	}

}
