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

import org.vcell.pathway.BioPaxObject;

public class BioPaxObjectShape extends BioPaxShape {

	public BioPaxObjectShape(BioPaxObject bioPaxObject, PathwayGraphModel graphModel) {
		super(bioPaxObject, graphModel);
	}

	protected int getPreferredWidth() { return 14; }
	protected int getPreferredHeight() { return 14; }
	
	public Color getDefaultBackgroundColor() { return Color.gray; }
	
}
