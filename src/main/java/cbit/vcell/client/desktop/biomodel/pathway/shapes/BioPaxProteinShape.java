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
import org.vcell.pathway.Protein;

public class BioPaxProteinShape extends BioPaxPhysicalEntityShape {

	public BioPaxProteinShape(Protein protein, PathwayGraphModel graphModel) {
		super(protein, graphModel);
	}
	
	protected Color getDefaultBackgroundColor() { return Color.green; }
	protected int getPreferredWidth() { return 16; }
	protected int getPreferredHeight() { return 16; }
			
	public Protein getProtein() {
		return (Protein) getModelObject();
	}

}
