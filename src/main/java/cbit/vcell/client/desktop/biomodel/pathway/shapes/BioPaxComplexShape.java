/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel.pathway.shapes;

import java.awt.Color;

import org.vcell.pathway.Complex;
import cbit.vcell.client.desktop.biomodel.pathway.PathwayGraphModel;

public class BioPaxComplexShape extends BioPaxPhysicalEntityShape {

	public BioPaxComplexShape(Complex complex, PathwayGraphModel graphModel) {
		super(complex, graphModel);
	}
	
	protected Color getDefaultBackgroundColor() { return Color.orange; }
	protected int getPreferredWidth() { return 20; }
	protected int getPreferredHeight() { return 20; }
			
	public Complex getComplex() {
		return (Complex) getModelObject();
	}

}
