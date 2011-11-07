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

import org.vcell.pathway.GroupObject;

import cbit.vcell.client.desktop.biomodel.pathway.PathwayGraphModel;

public class BioPaxGroupShape extends BioPaxShape {
	public BioPaxGroupShape(GroupObject groupObject, PathwayGraphModel graphModel) {
		super(groupObject, graphModel);
	}
	
	protected Color getDefaultBackgroundColor() { 
		GroupObject groupObject = getGroupObject();
		if(groupObject.getType().equals(GroupObject.Type.GROUPEDCOMPLEX)){
			return Color.orange;
		}else if(groupObject.getType().equals(GroupObject.Type.GROUPEDINTERACTION)){
			return Color.orange;
		}
		return Color.cyan; 
	}
	protected int getPreferredWidth() { return 30; }
	protected int getPreferredHeight() { return 30; }
			
	public GroupObject getGroupObject() {
		return (GroupObject) getModelObject();
	}
}
