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


import java.awt.Point;

import cbit.gui.graph.GraphModel;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;

public class MembraneShape extends StructureShape {
	public static final int memSpacingX = 15;
	public static final int memSpacingY = 15;
	public static double AngleOffset = Math.PI/6;
	public static double BeginAngle = Math.PI/2.0 + AngleOffset;
	public static double EndAngle = 3.0*Math.PI/2.0 - AngleOffset;
	public static double TotalAngle = EndAngle - BeginAngle;

	public MembraneShape(Membrane membrane, Model model, GraphModel graphModel) {
		super(membrane, model, graphModel);
		defaultBG = java.awt.Color.lightGray;
		backgroundColor = defaultBG;
	}

	public Membrane getMembrane() {
		return (Membrane)getStructure();
	}

	public Point getRadialPosition(double angleRad) {
		//  assuming (x/a)^2 + (y/b)^2 = 1
		int a = getSpaceManager().getSize().width/2 - memSpacingX/2;
		int b = getSpaceManager().getSize().height/2 - memSpacingY/2;
		if (Math.abs(angleRad - Math.PI/2) < 1E-6){
			return new Point(0,b);
		}
		if (Math.abs(angleRad - 3*Math.PI/2) < 1E-6){
			return new Point(0,-b);
		}
		double tan_alpha = Math.tan(angleRad);
		double absX = Math.sqrt(1.0/(1.0/(a*a) + tan_alpha*tan_alpha/(b*b)));
		double absY = Math.abs(tan_alpha) * absX;
		double x, y;
		if (angleRad > Math.PI/2 && angleRad < 3*Math.PI/2){
			x = -absX;
		}else{
			x = absX;
		}		
		if (angleRad > Math.PI && angleRad < 2*Math.PI){
			y = -absY;
		}else{
			y = absY;
		}		
		return new Point((int)x+a,(int)y+b);
	}

	public void refreshLayoutSelf() {
		int centerX = getSpaceManager().getSize().width/2;
		labelPos.x = centerX - getLabelSize().width/2; 
		labelPos.y = 0;
		
	}

}
