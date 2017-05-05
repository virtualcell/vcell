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
import org.vcell.pathway.Rna;

public class BioPaxRnaShape extends BioPaxPhysicalEntityShape {

	public BioPaxRnaShape(Rna rna, PathwayGraphModel graphModel) {
		super(rna, graphModel);
	}
	
	protected Color getDefaultBackgroundColor() { return Color.cyan; }
	protected int getPreferredWidth() { return 16; }
	protected int getPreferredHeight() { return 16; }
			
	public Rna getRna() {
		return (Rna) getModelObject();
	}

}
