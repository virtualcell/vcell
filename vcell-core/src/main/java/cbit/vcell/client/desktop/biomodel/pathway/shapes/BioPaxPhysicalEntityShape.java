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

import org.vcell.pathway.PhysicalEntity;

public class BioPaxPhysicalEntityShape extends BioPaxShape {

	public BioPaxPhysicalEntityShape(PhysicalEntity physicalEntity, PathwayGraphModel graphModel) {
		super(physicalEntity, graphModel);
	}
	
	public PhysicalEntity getPhysicalEntity() {
		return (PhysicalEntity) getModelObject();
	}

	protected int getPreferredWidth() { return 16; }
	protected int getPreferredHeight() { return 16; }

}
