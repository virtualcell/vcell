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

import org.vcell.pathway.MolecularInteraction;

public class BioPaxMolecularInteractionShape extends BioPaxInteractionShape{
	public BioPaxMolecularInteractionShape(MolecularInteraction interaction, PathwayGraphModel graphModel) {
		super(interaction, graphModel);
	}
	
	public MolecularInteraction getInteraction() {
		return (MolecularInteraction) getModelObject();
	}
}

