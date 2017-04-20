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

import org.vcell.pathway.Conversion;
import cbit.vcell.client.desktop.biomodel.pathway.PathwayGraphModel;

public class BioPaxConversionShape extends BioPaxInteractionShape {

	public BioPaxConversionShape(Conversion conversion, PathwayGraphModel graphModel) {
		super(conversion, graphModel);
	}
			
	public Conversion getConversion() {
		return (Conversion) getModelObject();
	}

	public Conversion getInteraction() {
		return (Conversion) getModelObject();
	}
}
