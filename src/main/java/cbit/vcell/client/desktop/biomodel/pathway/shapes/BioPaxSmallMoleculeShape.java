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

import org.vcell.pathway.SmallMolecule;

public class BioPaxSmallMoleculeShape extends BioPaxPhysicalEntityShape {

	public BioPaxSmallMoleculeShape(SmallMolecule smallMolecule, PathwayGraphModel graphModel) {
		super(smallMolecule, graphModel);
	}
	
	protected Color getDefaultBackgroundColor() { return Color.cyan; }
	protected int getPreferredWidth() { return 12; }
	protected int getPreferredHeight() { return 12; }
			
	public SmallMolecule getConversion() {
		return (SmallMolecule) getModelObject();
	}

}
